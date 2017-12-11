package com.fizz.cmpp.exception;

public class UnknownPackException extends CMPPException {

    private static final long serialVersionUID = 7437415755710607719L;

    public UnknownPackException() {
    }

    public UnknownPackException(String s) {
        super(s);
    }

    public UnknownPackException(Exception e) {
        super(e);
    }

    public UnknownPackException(String s, Exception e) {
        super(s, e);
    }

}
