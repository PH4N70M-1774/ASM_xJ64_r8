package com.jasm.corevm;

import java.io.FileInputStream;

import com.jasm.FunctionMeta;
import com.jasm.JasmException;

public class JasmoLoader {
    public static JasmoFile load(String fileName) throws JasmException {
        if (!(fileName.endsWith(".jasmo"))) {
            throw new JasmException("Invalid file format. Only \".jasmo\" files allowed.");
        }

        try (FileInputStream fis = new FileInputStream(fileName)) {
            if (!(fis.read() == 74 && fis.read() == 65 && fis.read() == 83 && fis.read() == 77 && fis.read() == 79)) {
                throw new JasmException("Invalid file content.");
            }

            int instructions[];
            String pool[];
            int poolLength = 0;
            FunctionMeta metadata[];

            int numOfMethods = fis.read();

            metadata = new FunctionMeta[numOfMethods];
            for (int i = 0; i < numOfMethods; i++) {
                int nameLength = fis.read();
                String name = "";
                for (int j = 0; j < nameLength; j++) {
                    name += ((char) fis.read());
                }
                int offset = fis.read();
                metadata[i] = new FunctionMeta(name, (byte) offset);
            }
            poolLength = fis.read();

            pool = new String[poolLength];
            for (int i = 0; i < poolLength; i++) {
                int length = fis.read();
                String s = "";
                for (int j = 0; j < length; j++) {
                    s += (char) fis.read();
                }
                pool[i] = s;
            }

            int start = fis.read();

            instructions = new int[fis.available()];
            for (int i = 0; i < instructions.length; i++) {
                instructions[i] = fis.read();
            }
            unescapeArray(pool);
            return new JasmoFile(instructions, pool, poolLength, metadata, start);
        } catch (Exception e) {
            throw new JasmException(e.getMessage(), e);
        }
    }

    private static void unescapeArray(String[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = unescape(s[i]);
        }
    }

    private static String unescape(String s) {
        int len = s.length();
        char[] out = new char[len]; // max possible size
        int pos = 0;

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);

            if (c != '\\') {
                out[pos++] = c;
                continue;
            }

            // We have a backslash, ensure next exists
            if (i + 1 >= len) {
                out[pos++] = '\\';
                break;
            }

            char next = s.charAt(++i);

            switch (next) {
                case 'n':
                    out[pos++] = '\n';
                    break;
                case 't':
                    out[pos++] = '\t';
                    break;
                case 'b':
                    out[pos++] = '\b';
                    break;
                case 'r':
                    out[pos++] = '\r';
                    break;
                case 'f':
                    out[pos++] = '\f';
                    break;
                case '\'':
                    out[pos++] = '\'';
                    break;
                case '"':
                    out[pos++] = '"';
                    break;
                case '\\':
                    out[pos++] = '\\';
                    break;

                case 'u': {
                    // Expect exactly 4 hex digits
                    if (i + 4 < len) {
                        int code = 0;
                        for (int k = 0; k < 4; k++) {
                            char h = s.charAt(++i);
                            code = (code << 4) | hexValue(h);
                        }
                        out[pos++] = (char) code;
                    }
                    break;
                }

                default:
                    // Fast octal handling: \012
                    if (next >= '0' && next <= '7') {
                        int oct = next - '0';
                        int count = 1;

                        while (count < 3 && i + 1 < len) {
                            char oc = s.charAt(i + 1);
                            if (oc >= '0' && oc <= '7') {
                                oct = (oct << 3) + (oc - '0');
                                i++;
                                count++;
                            } else {
                                break;
                            }
                        }

                        out[pos++] = (char) oct;
                    } else {
                        // Unknown escape → pass through
                        out[pos++] = next;
                    }
            }
        }

        return new String(out, 0, pos);
    }

    private static int hexValue(char c) {
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        return c - 'a' + 10; // assume valid input
    }
}