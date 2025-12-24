package com.jasm.corevm;

import com.jasm.FunctionMeta;
import com.jasm.JasmException;

import static com.jasm.Instruction.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Scanner;

public class JasmoVM {
    private int[] instr;
    private String[] pool;
    private FunctionMeta[] metadata;

    private int ip;

    private String vm_details = """
            \u001B[38;2;0;255;255m     ██╗ █████╗ ███████╗███╗   ███╗ ██████╗       ██╗   ██╗███╗   ███╗\u001B[0m
            \u001B[38;2;0;204;255m     ██║██╔══██╗██╔════╝████╗ ████║██╔═══██╗      ██║   ██║████╗ ████║\u001B[0m
            \u001B[38;2;0;153;255m     ██║███████║███████╗██╔████╔██║██║   ██║█████╗██║   ██║██╔████╔██║\u001B[0m
            \u001B[38;2;0;102;255m██   ██║██╔══██║╚════██║██║╚██╔╝██║██║   ██║╚════╝╚██╗ ██╔╝██║╚██╔╝██║\u001B[0m
            \u001B[38;2;0;51;255m╚█████╔╝██║  ██║███████║██║ ╚═╝ ██║╚██████╔╝       ╚████╔╝ ██║ ╚═╝ ██║\u001B[0m
            \u001B[38;2;0;0;255m ╚════╝ ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚═════╝         ╚═══╝  ╚═╝     ╚═╝\u001B[0m

            JVS JasmoVM [Version 1.0.00]
            (c) JVS Corporation. All rights  reserved.
                        """;
    private String vm_details_uncolored = """
                 ██╗ █████╗ ███████╗███╗   ███╗ ██████╗       ██╗   ██╗███╗   ███╗
                 ██║██╔══██╗██╔════╝████╗ ████║██╔═══██╗      ██║   ██║████╗ ████║
                 ██║███████║███████╗██╔████╔██║██║   ██║█████╗██║   ██║██╔████╔██║
            ██   ██║██╔══██║╚════██║██║╚██╔╝██║██║   ██║╚════╝╚██╗ ██╔╝██║╚██╔╝██║
            ╚█████╔╝██║  ██║███████║██║ ╚═╝ ██║╚██████╔╝       ╚████╔╝ ██║ ╚═╝ ██║
             ╚════╝ ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚═════╝         ╚═══╝  ╚═╝     ╚═╝

            JVS JasmoVM [Version 1.0.00]
            (c) JVS Corporation. All rights reserved.
                        """;

    public JasmoVM(JasmoFile file) {
        instr = file.instructions;
        pool = file.pool;
        metadata = file.metadata;
        ip = file.start;
    }

    public void run() throws JasmException, Exception {
        int[] instructions = instr;
        int rop = -1;
        int[] reg = new int[16];
        int[] memory = new int[512];
        int[] retOffsets = new int[1024];
        boolean internal = false;

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
                    internal = true;
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
                case LT -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a < b) ? 1 : 0);
                }
                case LTE -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a <= b) ? 1 : 0);
                }
                case GT -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a > b) ? 1 : 0);
                }
                case GTE -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a >= b) ? 1 : 0);
                }
                case EQU -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a == b) ? 1 : 0);
                }
                case NEQ -> {
                    int a = reg[instructions[ip++]];
                    int b = reg[instructions[ip++]];
                    reg[instructions[ip++]] = ((a != b) ? 1 : 0);
                }
                case INV -> {
                    String name = pool[instructions[ip++]];
                    JasmoVM vm = new JasmoVM(JasmoLoader.load(name));
                    vm.setStart(vm.metadata[instructions[ip++]].offset);
                    retOffsets[++rop] = ip;
                    vm.run();
                }
                case IRT -> {
                    if (!internal) {
                        break loop;
                    }
                }
                case SCN -> {
                    Scanner sc = new Scanner(System.in);
                    reg[instructions[ip++]] = sc.nextInt();
                    sc.close();
                }
                case SCP -> {
                    System.out.print(pool[instructions[ip++]]);
                    Scanner sc = new Scanner(System.in);
                    reg[instructions[ip++]] = sc.nextInt();
                    sc.close();
                }
                case SYS -> {
                    syscall(instructions[ip++], reg, memory);
                }
            }
        }
    }

    public void setStart(int idx) {
        ip = idx;
    }

    private void syscall(int call, int reg[], int memory[]) throws Exception {
        switch (call) {
            case 0 -> {
                System.out.println(reg[14]);
                System.exit(reg[14]);
            }
            case 1 -> { // * IMPLEMENTATION PENDING.
                switch (reg[10]) {
                    case 0 -> {
                        LocalTime time = LocalTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
                        System.out.println(time.format(formatter));
                    }
                    case 1 -> {
                        System.out.println(System.currentTimeMillis());
                    }
                    case 2 -> {
                        LocalDate date = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                        System.out.println(date.format(formatter));
                    }
                    case 3 -> {
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy HH:mm:ss a");
                        System.out.println(now.format(formatter));
                    }
                }
            }
            case 2 -> {
                for (int i = 0; i < reg.length; i++) {
                    System.out.println("%rx" + Integer.toHexString(i) + ": " + reg[i]);
                }
            }
            case 3 -> {
                for (int i = 0; i < memory.length; i += 4) {
                    System.out.println(
                            String.format("%04d: %-20d%04d: %-20d%04d: %-20d%04d: %-20d", i, memory[i],
                                    i + 1, memory[i + 1], i + 2, memory[i + 2], i + 3, memory[i + 3]));
                }
            }
            case 4 -> {
                if (reg[12] == 0x1c) {
                    System.out.println(vm_details);
                } else {
                    System.out.println(vm_details_uncolored);
                }
            }
            case 5 -> {
                System.out.print("\033[2J\033[3J\033[H");
                System.out.flush();
            }
            case 6 -> {
                log(reg[12], pool[reg[13]]);
            }
            case 7 -> {
                reg[10] = pool[reg[10]].length();
            }
        }
    }

    private static void log(int type, String msg) {
        String RESET = "\u001B[0m";
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String BLUE = "\u001B[34m";
        String CYAN = "\u001B[36m";

        String color;
        String label;

        switch (type) {
            case 0:
                color = GREEN;
                label = "OK   ";
                break;

            case 1:
                color = BLUE;
                label = "INFO ";
                break;

            case 2:
                color = YELLOW;
                label = "WARN ";
                break;

            case 3:
                color = CYAN;
                label = "DEBUG";
                break;

            case 4:
                color = RED;
                label = "ERROR";
                break;

            default:
                color = RESET;
                label = "LOG  ";
        }

        String time = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS a"));

        String output = color + "[" + label + " " + time + "] " + msg + RESET;

        System.out.println(output);
    }
}
