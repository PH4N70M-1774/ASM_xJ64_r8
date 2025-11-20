package com.jasm;

import com.jasm.corevm.JasmoFile;
import com.jasm.corevm.JasmoLoader;
import com.jasm.corevm.JasmoVM;

public class Jrun {
    public void run(String fileName) {
        try {
            JasmoFile file = JasmoLoader.load(fileName);
            JasmoVM vm = new JasmoVM(file);
            vm.run();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
