package com.fizz.cmpp.exception;

public class UnknownCommandIdException extends CMPPException {

    private static final long serialVersionUID = -5161354611524783737L;

    public UnknownCommandIdException() {
    }

    public UnknownCommandIdException(String s) {
        super(s);
    }

    public UnknownCommandIdException(Exception e) {
        super(e);
    }

    public UnknownCommandIdException(String s, Exception e) {
        super(s, e);
    }

}
