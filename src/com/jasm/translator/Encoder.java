package com.jasm.translator;

import java.util.List;
import java.util.ArrayList;

public class Encoder {
    private String[] tokens;
    private List<FunctionMeta> metadata;
    private byte startAddr;

    public Encoder(String[] tokens) {
        this.tokens = tokens;
        metadata = new ArrayList<>();
        startAddr = 0;
    }

    public void encode() {
        startAddr = 0;
        startAddr = (byte) (startAddr + 0);
        for (String str : tokens) {
            if(str.isBlank()) {
                continue;
            } else if (str.startsWith(".")) {
                metadata.add(new FunctionMeta(str));
            }
        }
    }
}
