package model;

/**
 *
 */
public class Triple {
    private String op;
    private String arg1;
    private String arg2;

    public Triple(String op, String arg1, String arg2) {
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public String getOp() {
        return op;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }
};

