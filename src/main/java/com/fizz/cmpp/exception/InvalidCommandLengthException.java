package com.fizz.cmpp.exception;

public class InvalidCommandLengthException extends CMPPException {

    public InvalidCommandLengthException() {
        super(
                "command length is invalid,I's fatal error,recommend you to reconnect the ISMG");
    }

    public InvalidCommandLengthException(String s) {
        super(s);
    }

    public InvalidCommandLengthException(Exception e) {
        super(e);
    }

    public InvalidCommandLengthException(String s, Exception e) {
        super(s, e);
    }

}
