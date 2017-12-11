package com.fizz.cmpp.exception;

public class DeliverFailException extends CMPPException {

    public DeliverFailException() {
    }

    public DeliverFailException(String s) {
        super(s);
    }

    public DeliverFailException(Exception e) {
        super(e);
    }

    public DeliverFailException(String s, Exception e) {
        super(s, e);
    }

}
