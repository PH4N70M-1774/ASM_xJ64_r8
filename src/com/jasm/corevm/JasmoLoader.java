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

            return new JasmoFile(instructions, pool, poolLength, metadata, start);
        } catch (Exception e) {
            throw new JasmException(e.getMessage(), e);
        }
    }
}