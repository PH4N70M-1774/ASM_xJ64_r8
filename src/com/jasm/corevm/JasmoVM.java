package com.jasm.corevm;

import com.jasm.FunctionMeta;
import static com.jasm.Instruction.*;

import java.util.Arrays;

public class JasmoVM {
    private int[] instr;
    private String[] pool;
    private FunctionMeta[] metadata;

    private int ip;

    public JasmoVM(JasmoFile file) {
        instr = file.instructions;
        pool = file.pool;
        metadata = file.metadata;
        ip = file.start;
    }

    public void run() {
        int[] instructions = instr;
        int rop = -1;
        int[] reg = new int[16];
        int[] memory = new int[512];
        int[] retOffsets = new int[1024];

        loop: while (ip < instructions.length) {
            int opcode = instructions[ip++];
            switch (opcode) {
                case NOP -> {

                }
                case HLT -> {
                    break loop;
                }
                case LDI -> {
                    reg[instructions[ip++]] = instructions[ip++];
                }
                case ADD -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a + b;
                }
                case SUB -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a - b;
                }
                case MUL -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a * b;
                }
                case AND -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a & b;
                }
                case OR -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a | b;
                }
                case XOR -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = a ^ b;
                }
                case ADI -> {
                    reg[instructions[ip++]] += instructions[ip++];
                }
                case SUI -> {
                    reg[instructions[ip++]] -= instructions[ip++];
                }
                case CAL -> {
                    int idx = instructions[ip++];
                    retOffsets[++rop] = ip;
                    ip = metadata[idx].offset;
                }
                case RET -> {
                    int offset = retOffsets[rop--];
                    ip = offset;
                }
                case LOD -> {
                    int offset = instructions[ip++];
                    int r = instructions[ip++];
                    reg[r] = memory[offset];
                }
                case STR -> {
                    int offset = instructions[ip++];
                    int r = instructions[ip++];
                    memory[offset] = reg[r];
                }
                case BRT -> {
                    ip = ((reg[instructions[ip++]] != 0) ? instructions[ip++] : (ip + 1));
                }
                case BRF -> {
                    ip = ((reg[instructions[ip++]] == 0) ? instructions[ip++] : (ip + 1));
                }
                case JMP -> {
                    ip = instructions[ip++];
                }
                case PRT -> {
                    System.out.print(reg[instructions[ip++]]);
                }
                case PRS -> {
                    System.out.print((char) reg[instructions[ip++]]);
                }
                case PRP -> {
                    System.out.print(pool[instructions[ip++]]);
                }
                case CRR -> {
                    Arrays.fill(reg, 0);
                }
                case CME -> {
                    Arrays.fill(memory, 0);
                }
            }
        }
    }
}
