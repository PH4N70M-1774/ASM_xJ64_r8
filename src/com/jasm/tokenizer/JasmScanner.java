package com.jasm.tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.jasm.JasmException;

public class JasmScanner {
    public static String scan(String filePath) throws JasmException {
        if (!filePath.endsWith(".jasm")) {
            throw new JasmException("Invalid file. Only '.jasm' files allowed.");
        }
        String content = "";
        try {
            Scanner sc = new Scanner(new File(filePath));
            while (sc.hasNextLine()) {
                content+=sc.nextLine()+"\n";
            }
            sc.close();
        } catch (IOException e) {
            throw new JasmException(e);
        }

        return content;
    }
}
