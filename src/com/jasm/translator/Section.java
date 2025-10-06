package com.jasm.translator;

public class Section {
    // As of now, only the pool and main types of sections will be implemented.
    public enum Type {
        POOL, // This section stores the String pool.

        MAIN, // This section has the main assembly.

        CONST, // This section stores the Constant pool.

        /**
         * This section stores the function metadata.
         *
         * Even though the bytecode has a metadata section by default,
         * you can include a meta section so that the assembler does not
         * have to manually sort out the offsets.
         *
         * This section is solely used by the assembler for easier assembling.
         * It has no effect whatsoever on the runtime performance.
         */
        META,

        REG, // Can be used to give the registers a default initial value.

        EXT, // To be implemented in future. This will have all the external files' names.

        MEM, // Can be used to give the memory a default initial value. May not be
             // implemented.

        CUSTOM;// Sections defined by user. It acts like a class which solely has functions (as
               // of now).
    }
}
