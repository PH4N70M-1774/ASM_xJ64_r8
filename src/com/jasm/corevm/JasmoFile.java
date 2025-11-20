package com.jasm.corevm;

import com.jasm.FunctionMeta;

public class JasmoFile {
    public int[] instructions;
    public String[] pool;
    public FunctionMeta[] metadata;
    public int poolLength, start;

    public JasmoFile(int[] instructions, String[] pool, int poolLength, FunctionMeta[] metadata, int start) {
        this.instructions = instructions;
        this.pool = pool;
        this.poolLength = poolLength;
        this.metadata = metadata;
        this.start = start;
    }

    public void printContent() {
        System.out.println("METADATA:");
        for (FunctionMeta meta : metadata) {
            System.out.println(meta);
        }
        System.out.println("====");

        System.out.println("POOL:");
        System.out.println("Pool Length: " + poolLength);
        for (String str : pool) {
            System.out.println(str);
        }
        System.out.println("====");
        System.out.println("Start Address: " + start);
        System.out.println("Instructions:");
        for (int i : instructions) {
            System.out.println(i);
        }
        System.out.println("====");
    }
}
