package com.jasm;

import com.jasm.corevm.JasmoFile;
import com.jasm.corevm.JasmoLoader;

public class Jrun {
    public void run(String fileName) {
        try {
            JasmoFile file = JasmoLoader.load(fileName);
            file.printContent();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
