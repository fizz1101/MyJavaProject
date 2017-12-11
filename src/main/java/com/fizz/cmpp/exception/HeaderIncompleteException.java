package com.fizz.cmpp.exception;

public class HeaderIncompleteException extends CMPPException {

    public HeaderIncompleteException() {
    }

    public HeaderIncompleteException(String s) {
        super(s);
    }

    public HeaderIncompleteException(Exception e) {
        super(e);
    }

    public HeaderIncompleteException(String s, Exception e) {
        super(s, e);
    }

}
