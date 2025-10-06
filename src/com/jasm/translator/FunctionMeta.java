package com.jasm.translator;

public class FunctionMeta {
    byte[] name;
    byte offset;

    public FunctionMeta(String name) {
        this(name, ((byte) 0));
    }

    public FunctionMeta(String name, byte offset) {
        this.name = name.getBytes();
        this.offset = offset;
    }

    public void setOffset(byte offset) {
        this.offset = offset;
    }

    public int[] getMetabytes() {
        int[] metabytes = new int[name.length+2];
        metabytes[0] = name.length;
        for (int i = 0; i < name.length; i++) {
            metabytes[i+1] = name[i];
        }
        metabytes[name.length] = offset;
        return metabytes;
    }
}
