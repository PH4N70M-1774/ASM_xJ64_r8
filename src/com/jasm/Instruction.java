package com.jasm;

public class Instruction {
    // ==== NO OPERATION ====
    public static final int NOP = 0; // Does no operation.

    // ==== EXIT PROGRAM ====
    public static final int HLT = 1; // Exits program.

    // ==== BASIC INTEGER OPERATIONS ====
    public static final int LDI = 2; // Loads a value into a register.

    public static final int ADD = 3; // Adds value of two registers and stores result in a third register.

    public static final int SUB = 4; // Subtracts value of two registers and stores result in a third register.

    public static final int MUL = 5; // Multiplies value of two registers and stores result in a third register.

    public static final int AND = 6; // Performs AND on the value of two registers and stores result in a third
                                     // register.

    public static final int OR = 7; // Performs OR on the value of two registers and stores result in a third
                                    // register.

    public static final int XOR = 8; // Performs XOR on the value of two registers and stores result in a third
                                     // register.

    public static final int ADI = 9; // Adds a value to a register.

    public static final int SUI = 10; // Subtracts a value from a register.

    // ==== OPERATIONS RELATED TO FUNCTIONS ====
    public static final int CAL = 11; // Used to call a function.

    public static final int RET = 12; // Used to return from a function.

    // ==== OPERATIONS RELATED TO MEMORY ====
    public static final int LOD = 13; // Load a value from memory to register.

    public static final int STR = 14; // Store value from register to memory.

    // ==== BRANCHING OPERATIONS ====
    public static final int BRT = 15; // Jump to index if value of register is not zero.

    public static final int BRF = 16; // Jump to index if value of register is zero.

    public static final int JMP = 17; // Jump to index.

    // ==== PRINTING OPERATIONS ====
    public static final int PRT = 18; // Print the value of a register.

    public static final int PRS = 19; // Print the value of a registeras a string. ( %rx0 = 65, PRS %rx0 ==> A)

    public static final int PRP = 20; // Print a String from the String pool.

    // ==== OPERATIONS RELATED TO MEMORY ====
    public static final int CRR = 21; // Clear memory.

    public static final int CME = 22; // Clear registers.

    // ==== BOOLEAN OPERATIONS ====
    public static final int LT = 23; // Performs less than on the value of two registers and stores result in a third
                                     // register.

    public static final int LTE = 24; // Performs less than or equal to on the value of two registers and stores
                                      // result in a third register.

    public static final int GT = 25; // Performs greater than on the value of two registers and stores result in a
                                     // third register.

    public static final int GTE = 26; // Performs greater than or equal to on the value of two registers and stores
                                      // result in a third register.

    public static final int EQU = 27; // Performs equal to on the value of two registers and stores result in a third
                                      // register.

    public static final int NEQ = 28; // Performs not equal to on the value of two registers and stores result in a
                                      // third register.

    // ==== OPERATIONS RELATED TO EXTERNAL FUNCTIONS ====
    public static final int INV = 29; // Invoke external function.

    public static final int IRT = 30; // Return from external function.

    // ==== OPERATIONS RELATED TO DATA INPUT ====
    public static final int SCN = 31; // Scans for user input.

    public static final int SCP = 32; // Prints String from String pool and then scans for user input.

    private static final Data data[] = {
            getDataFor("NOP", 0),

            getDataFor("HLT", 0),

            getDataFor("LDI", 2),
            getDataFor("ADD", 3),
            getDataFor("SUB", 3),
            getDataFor("MUL", 3),
            getDataFor("AND", 3),
            getDataFor("OR", 3),
            getDataFor("XOR", 3),
            getDataFor("ADI", 2),
            getDataFor("SUI", 2),

            getDataFor("CAL", 1),
            getDataFor("RET", 0),

            getDataFor("LOD", 2),
            getDataFor("STR", 2),

            getDataFor("BRT", 2),
            getDataFor("BRF", 2),
            getDataFor("JMP", 1),

            getDataFor("PRT", 1),
            getDataFor("PRS", 1),
            getDataFor("PRP", 1),

            getDataFor("CRR", 0),
            getDataFor("CME", 0),

            getDataFor("LT", 3),
            getDataFor("LTE", 3),
            getDataFor("GT", 3),
            getDataFor("GTE", 3),
            getDataFor("EQU", 3),
            getDataFor("NEQ", 3),

            getDataFor("INV", 2),
            getDataFor("IRT", 0),

            getDataFor("SCN", 1),
            getDataFor("SCP", 2)
    };

    public static Data getData(int opcode) {
        return data[opcode];
    }

    public static byte getOpcode(String str) {
        byte idx = 0;
        for (Data data2 : data) {
            if (data2.mnemonic.equals(str)) {
                return idx;
            }
            idx++;
        }
        return 0;
    }

    private static Data getDataFor(String mnemonic, int operands) {
        return new Data(mnemonic, operands);
    }

    public static class Data {
        public String mnemonic;
        public int operands;

        public Data(String mnemonic, int operands) {
            this.mnemonic = mnemonic;
            this.operands = operands;
        }
    }
}
