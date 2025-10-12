package com.jasm.translator;

import java.io.FileOutputStream;

import com.jasm.JasmException;

public class JasmoWriter {
    private static final byte[] jasmoMagic = {74,65,83,77,79};
    private String fileName;
    private byte[] bytecode;

    public JasmoWriter(String fileName, byte[] bytecode) {
        this.fileName = fileName;
        this.bytecode = bytecode;
    }

    public void write() throws JasmException {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(jasmoMagic);
            fos.write(bytecode);
            fos.close();
        } catch (Exception e) {
            throw new JasmException(e.getMessage(), e);
        }
    }
}

