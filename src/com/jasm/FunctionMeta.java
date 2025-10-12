package com.jasm;

import java.util.List;
import java.util.ArrayList;

public class FunctionMeta {
    byte[] name;
    public byte offset;

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
        List<Byte> meta = new ArrayList<>();

        meta.add((byte) name.length);
        for (int i = 0; i < name.length; i++) {
            meta.add(name[i]);
        }
        meta.add(offset);

        byte[] metabytes = new byte[meta.size()];
        for (int i = 0; i < meta.size(); i++) {
            metabytes[i] = meta.get(i);
        }
        return metabytes;
    }

    @Override
    public String toString() {
        return "{Name: " + new String(name) + ", Offset: " + offset + "}";
    }
}
