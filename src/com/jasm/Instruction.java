package com.jasm;

public class Instruction {
    public static final int NOP = 0;

    public static final int HLT = 1;

    public static final int LDI = 2;
    public static final int ADD = 3;
    public static final int SUB = 4;
    public static final int MUL = 5;
    public static final int AND = 6;
    public static final int OR = 7;
    public static final int XOR = 8;
    public static final int ADI = 9;
    public static final int SUI = 10;

    public static final int CAL = 11;
    public static final int RET = 12;

    public static final int LOD = 13;
    public static final int STR = 14;

    public static final int BRT = 15;
    public static final int BRF = 16;
    public static final int JMP = 17;

    public static final int PRT = 18;
    public static final int PRS = 19;
    public static final int PRP = 20;

    public static final int CRR = 21;
    public static final int CME = 22;

    public static final int LT = 23;
    public static final int LTE = 24;
    public static final int GT = 25;
    public static final int GTE = 26;
    public static final int EQU = 27;
    public static final int NEQ = 28;

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
