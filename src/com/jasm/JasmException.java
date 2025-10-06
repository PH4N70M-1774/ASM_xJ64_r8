package com.jasm;

public class JasmException extends Exception {
    private String msg;

    public JasmException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public JasmException(Exception e) {
        super(e);
        this.msg = e.toString();
    }

    @Override
    public String toString() {
        return "Error: "+msg;
    }
}
