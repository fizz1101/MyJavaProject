package com.fizz.cmpp.exception;

public class CMPPException extends Exception {

    public CMPPException() {
    }

    public CMPPException(String s) {
        super(s);
    }

    public CMPPException(Exception e) {
        super(e);
    }

    public CMPPException(String s, Exception e) {
        super(s, e);
    }

}
