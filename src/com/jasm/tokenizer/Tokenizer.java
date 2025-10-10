package com.jasm.tokenizer;

import java.util.List;
import java.util.ArrayList;

public class Tokenizer {
    public static String[] tokenize(String content) {
        List<String> t = new ArrayList<>();

        for (String s1 : content.split("\n")) {
            for (String s2 : s1.trim().split("\\s+(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)|(?:::|:)(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)\r\n")) {
                t.add(s2);
            }
        }

        String[] tokens=new String[t.size()];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = t.get(i);
        }

        return tokens;
    }
}
