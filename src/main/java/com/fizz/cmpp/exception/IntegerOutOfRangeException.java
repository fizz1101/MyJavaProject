package com.fizz.cmpp.exception;

public class IntegerOutOfRangeException extends CMPPException {

    public IntegerOutOfRangeException() {
        super("The integer is lower or greater than required.");
    }

    public IntegerOutOfRangeException(String s) {
        super(s);
    }

    public IntegerOutOfRangeException(Exception e) {
        super(e);
    }

    public IntegerOutOfRangeException(String s, Exception e) {
        super(s, e);
    }

    public IntegerOutOfRangeException(int min, int max, int val) {
        super("The integer is lower or greater than required:  min=" + min
                + " max=" + max + " actual=" + val + ".");
    }

}
