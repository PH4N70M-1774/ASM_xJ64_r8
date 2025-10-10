package com.jasm.translator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.jasm.Instruction;

public class Encoder {
    private String[] tokens;
    private List<FunctionMeta> metadata;
    private List<Byte> bytecode;
    private byte startAddr;
    private Map<String, List<Integer>> unresolvedCalls;

    public Encoder(String[] tokens) {
        this.tokens = tokens;
        metadata = new ArrayList<>();
        bytecode = new ArrayList<>();
        unresolvedCalls = new HashMap<>();
        startAddr = 0;
    }

    public void encode() {
        @SuppressWarnings("unused")
        int tPtr = 0;
        boolean mainStart = false;
        startAddr = 0;

        for (int i = 0; i < tokens.length; i++) {
            String str = tokens[i];

            // detect main start variants
            if (str.equals("_main::") || str.equals(".main:") || str.equals("_main:") || str.equals(".main::")) {
                tPtr = -1;
                mainStart = true;
                continue;
            }

            if (!mainStart) {
                tPtr++;
                continue;
            }

            if (str.isBlank()) {
                tPtr++;
                continue;
            }

            // ---------- FUNCTION DEFINITION ----------
            if (str.startsWith(".") && str.endsWith(":")) {
                String fname = str.substring(1, str.length() - 1); // remove leading '.' and trailing ':'
                defineFunction(fname);
                // The original code used tPtr-- after adding metadata; keep parity if you need
                // tPtr-based addresses
                tPtr--;
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
                    System.out.println("Function name (resolved inline): " + fname + " -> " + metaIndex);
                } else {
                    // unresolved: add placeholder and remember its bytecode index
                    bytecode.add((byte) 0); // placeholder
                    int placeholderPos = bytecode.size() - 1;
                    unresolvedCalls.computeIfAbsent(fname, k -> new ArrayList<>()).add(placeholderPos);
                    System.out.println(
                            "Unresolved function call recorded: " + fname + " at bytecode index " + placeholderPos);
                }
                tPtr++;
                continue;
            }

            // ---------- NUMBERS ----------
            if (str.startsWith("0x")) {
                Byte val = Byte.decode(str);
                System.out.println("Number : " + val);
                bytecode.add(val);
                tPtr++;
                continue;
            }

            // ---------- REGISTERS -----------
            if (str.startsWith("%rx")) {
                
            }

            // ---------- INSTRUCTION ----------
            byte opcode = Instruction.getOpcode(str);
            System.out.println(str + "  :  " + opcode);
            bytecode.add(opcode);
            tPtr++;
        }

        for (FunctionMeta meta : metadata) {
            if (meta.getName().equals(".main")) {
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
    }

    // helper to add metadata + patch previously-unresolved calls
    private void defineFunction(String fname) {
        int metaIndex = metadata.size(); // new index will be this
        metadata.add(new FunctionMeta(fname, (byte) metaIndex));
        System.out.println("Defined function: " + fname + " at metadata index " + metaIndex);

        // patch any previously unresolved calls
        List<Integer> waiting = unresolvedCalls.get(fname);
        if (waiting != null) {
            for (int pos : waiting) {
                // overwrite placeholder with the correct metadata index
                bytecode.set(pos, (byte) metaIndex);
                System.out.println("Patched call to " + fname + " at bytecode index " + pos + " -> " + metaIndex);
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

    // getters for tests / inspection
    public List<Byte> getBytecode() {
        return bytecode;
    }

    public List<FunctionMeta> getMetadata() {
        return metadata;
    }
}
