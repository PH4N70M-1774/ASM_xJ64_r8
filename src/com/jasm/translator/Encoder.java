package com.jasm.translator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.jasm.FunctionMeta;
import com.jasm.Instruction;

public class Encoder {
    private String[] tokens;
    private String[] pool;
    private List<FunctionMeta> metadata;
    private List<Byte> bytecode, byteFinal;
    private byte startAddr;
    private Map<String, List<Integer>> unresolvedCalls;

    public Encoder(String[] tokens) {
        this.tokens = tokens;
        metadata = new ArrayList<>();
        bytecode = new ArrayList<>();
        byteFinal = new ArrayList<>();
        pool = new String[2];
        unresolvedCalls = new HashMap<>();
        startAddr = 0;
    }

    public void encode() {
        boolean poolStart = false;
        startAddr = 0;
        int poolPtr = 0;

        for (int i = 0; i < tokens.length; i++) {
            String str = tokens[i];

            if (str.equals("_pool::")) {
                poolStart = true;
                continue;
            }

            if (poolStart && (isNumber(str) || str.startsWith("\""))) {
                if (isNumber(str)) {
                    pool = new String[Integer.valueOf(str)];
                    continue;
                } else {
                    pool[poolPtr++] = str;
                    continue;
                }

            }

            // detect main start variants
            if (str.equals("_main::")) {
                poolStart = false;
                continue;
            }

            // ---------- FUNCTION DEFINITION ----------
            if (str.startsWith(".") && str.endsWith(":")) {
                String fname = str.substring(1, str.length() - 1); // remove leading '.' and trailing ':'
                defineFunction(fname, bytecode.size());
                continue;
            }

            // ---------- FUNCTION REFERENCE / CALL ----------
            // token that begins with '.' but not a definition (like ".print")
            if (str.startsWith(".")) {
                String fname = str.substring(1);
                // try to resolve immediately
                int metaIndex = findMetadataIndex(fname);
                if (metaIndex >= 0) {
                    // patch resolved index
                    bytecode.add((byte) metaIndex);
                    // System.out.println("Function name (resolved inline): " + fname + " -> " +
                    // metaIndex);
                } else {
                    // unresolved: add placeholder and remember its bytecode index
                    bytecode.add((byte) 0); // placeholder
                    int placeholderPos = bytecode.size() - 1;
                    unresolvedCalls.computeIfAbsent(fname, k -> new ArrayList<>()).add(placeholderPos);
                    // System.out.println(
                    // "Unresolved function call recorded: " + fname + " at bytecode index " +
                    // placeholderPos);
                }
                continue;
            }

            // ---------- NUMBERS ----------
            if (str.startsWith("0x")) {
                Byte val = Byte.decode(str);
                // System.out.println("Number : " + val);
                bytecode.add(val);
                continue;
            }

            // ---------- REGISTERS -----------
            if (str.startsWith("%rx")) {
                Byte val = switch (str.charAt(3)) {
                    case '0' -> 0;
                    case '1' -> 1;
                    case '2' -> 2;
                    case '3' -> 3;
                    case '4' -> 4;
                    case '5' -> 5;
                    case '6' -> 6;
                    case '7' -> 7;
                    case '8' -> 8;
                    case '9' -> 9;
                    case 'a', 'A' -> 10;
                    case 'b', 'B' -> 11;
                    case 'c', 'C' -> 12;
                    case 'd', 'D' -> 13;
                    case 'e', 'E' -> 14;
                    case 'f', 'F' -> 15;
                    default -> 0;
                };
                // System.out.println("Register: " + str);
                bytecode.add(val);
                continue;
            }

            // ---------- INSTRUCTION ----------
            byte opcode = Instruction.getOpcode(str);
            // System.out.println(str + " : " + opcode);
            bytecode.add(opcode);
        }

        for (FunctionMeta meta : metadata) {
            if (meta.getName().equals("main")) {
                startAddr = meta.offset;
                break;
            }
        }

        // after parsing, warn about any unresolved function calls
        if (!unresolvedCalls.isEmpty()) {
            System.err.println("Warning: unresolved function references remain:");
            for (Map.Entry<String, List<Integer>> e : unresolvedCalls.entrySet()) {
                System.err.println("  " + e.getKey() + " -> placeholders at " + e.getValue());
            }
        }

        for (FunctionMeta meta : metadata) {
            if (meta.getName().equals(".main")) {
                startAddr = meta.offset;
                break;
            }
        }

        makeFinalBytecode();
    }

    // helper to add metadata + patch previously-unresolved calls
    private void defineFunction(String fname, int tPtr) {
        int metaIndex = metadata.size(); // new index will be this
        metadata.add(new FunctionMeta(fname, (byte) tPtr));
        // System.out.println("Defined function: " + fname + " at metadata index " +
        // metaIndex);

        // patch any previously unresolved calls
        List<Integer> waiting = unresolvedCalls.get(fname);
        if (waiting != null) {
            for (int pos : waiting) {
                // overwrite placeholder with the correct metadata index
                bytecode.set(pos, (byte) metaIndex);
                // System.out.println("Patched call to " + fname + " at bytecode index " + pos +
                // " -> " + metaIndex);
            }
            unresolvedCalls.remove(fname);
        }
    }

    // helper to search metadata by name; returns index or -1 if not found
    private int findMetadataIndex(String name) {
        for (int m = 0; m < metadata.size(); m++) {
            if (metadata.get(m).getName().equals(name))
                return m;
        }
        return -1;
    }

    public boolean isNumber(String str) {
        for (char c : str.toCharArray()) {
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    // getters for tests / inspection
    public List<Byte> getBytecode() {
        return bytecode;
    }

    public List<FunctionMeta> getMetadata() {
        return metadata;
    }

    private void makeFinalBytecode() {
        byteFinal.add((byte) metadata.size());
        for (FunctionMeta meta : metadata) {
            for (byte b : meta.getMetabytes()) {
                byteFinal.add(b);
            }
        }

        byteFinal.add((byte) pool.length);
        for (String s : pool) {
            byteFinal.add((byte) (s.length() - 2));
            for (byte b : s.substring(1, s.length() - 1).getBytes()) {
                byteFinal.add(b);
            }
        }

        byteFinal.add(startAddr);

        for (byte b : bytecode) {
            byteFinal.add(b);
        }
    }

    public byte[] getFinalBytecode() {
        byte code[] = new byte[byteFinal.size()];
        for (int i = 0; i < byteFinal.size(); i++) {
            code[i] = byteFinal.get(i);
        }
        return code;
    }
}
