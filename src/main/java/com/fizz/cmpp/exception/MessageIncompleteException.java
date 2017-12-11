package com.fizz.cmpp.exception;

public class MessageIncompleteException extends CMPPException {

    public MessageIncompleteException() {
    }

    public MessageIncompleteException(String s) {
        super(s);
    }

    public MessageIncompleteException(Exception e) {
        super(e);
    }

    public MessageIncompleteException(String s, Exception e) {
        super(s, e);
    }

}
