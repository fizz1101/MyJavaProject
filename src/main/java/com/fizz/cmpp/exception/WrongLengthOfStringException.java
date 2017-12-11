package com.fizz.cmpp.exception;

public class WrongLengthOfStringException extends CMPPException {

    private static final long serialVersionUID = 530742393574877452L;

    public WrongLengthOfStringException() {
        super("The string is shorter or longer than required.");
    }

    public WrongLengthOfStringException(String s) {
        super(s);
    }

    public WrongLengthOfStringException(Exception e) {
        super(e);
    }

    public WrongLengthOfStringException(String s, Exception e) {
        super(s, e);
    }

    public WrongLengthOfStringException(int min, int max, int actual) {
        super("The string is shorter or longer than required:  min=" + min
                + " max=" + max + " actual=" + actual + ".");
    }

}
