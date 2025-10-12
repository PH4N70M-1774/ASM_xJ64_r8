package com.jasm;

import com.jasm.tokenizer.JasmScanner;
import com.jasm.tokenizer.Tokenizer;

import com.jasm.translator.Encoder;
import com.jasm.translator.JasmoWriter;

public class Jasm {
    public void assemble(String fileName) {
        try {
            String content = JasmScanner.scan(fileName);
            String tokens[] = Tokenizer.tokenize(content);
            Encoder encoder = new Encoder(tokens);
            encoder.encode();
            byte[] bytecode = encoder.getFinalBytecode();
            JasmoWriter writer = new JasmoWriter(fileName+'o', bytecode);
            writer.write();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
