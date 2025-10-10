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

    public String getName() {
        return new String(name);
    }

    public byte[] getMetabytes() {
        byte[] metabytes = new byte[name.length + 2];
        metabytes[0] = (byte) name.length;
        for (int i = 0; i < name.length; i++) {
            metabytes[i + 1] = name[i];
        }
        metabytes[name.length] = offset;
        return metabytes;
    }

    @Override
    public String toString() {
        return "{Name: " + new String(name) + ", Offset: " + offset + "}";
    }
}
