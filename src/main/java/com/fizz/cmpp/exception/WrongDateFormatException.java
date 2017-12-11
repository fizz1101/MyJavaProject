package com.fizz.cmpp.exception;

public class WrongDateFormatException extends CMPPException {

    private static final long serialVersionUID = -1972186016365436605L;

    public WrongDateFormatException() {
        super("Date must be either null or of format YYMMDDhhmmsstnnp");
    }

    public WrongDateFormatException(String s) {
        super("Date must be either null or of format YYMMDDhhmmsstnnp and not "
                + s + ".");
    }

    public WrongDateFormatException(Exception e) {
        super(e);
    }

    public WrongDateFormatException(String s, Exception e) {
        super(s, e);
    }

    public WrongDateFormatException(String dateStr, String msg) {
        super("Invalid date " + dateStr + ": " + msg);
    }

}
