package com.fizz.cmpp.exception;

public class TerminatingZeroNotFoundException extends CMPPException {

    private static final long serialVersionUID = 3018105968402128976L;

    public TerminatingZeroNotFoundException() {
        super("Terminating zero not found in buffer.");
    }

    public TerminatingZeroNotFoundException(String s) {
        super(s);
    }

    public TerminatingZeroNotFoundException(Exception e) {
        super(e);
    }

    public TerminatingZeroNotFoundException(String s, Exception e) {
        super(s, e);
    }

}
