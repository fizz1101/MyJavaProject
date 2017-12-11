package com.fizz.cmpp.exception;

public class OutOfBoundsException extends CMPPException {

    private static final long serialVersionUID = -3380741534652169600L;

    public OutOfBoundsException() {
    }

    public OutOfBoundsException(String s) {
        super(s);
    }

    public OutOfBoundsException(Exception e) {
        super(e);
    }

    public OutOfBoundsException(String s, Exception e) {
        super(s, e);
    }

}
