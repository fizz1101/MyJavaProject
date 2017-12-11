package com.fizz.cmpp.exception;

public class ValueNotSetException extends CMPPException {

    private static final long serialVersionUID = 1120182959933727123L;

    public ValueNotSetException() {
    }

    public ValueNotSetException(String s) {
        super(s);
    }

    public ValueNotSetException(Exception e) {
        super(e);
    }

    public ValueNotSetException(String s, Exception e) {
        super(s, e);
    }

}
