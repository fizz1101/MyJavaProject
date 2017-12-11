package com.fizz.cmpp.exception;

public class NotEnoughDataInByteBufferException extends CMPPException {

    private int available;
    private int expected;

    public NotEnoughDataInByteBufferException() {
    }

    public NotEnoughDataInByteBufferException(String s) {
        super(s);
    }

    public NotEnoughDataInByteBufferException(Exception e) {
        super(e);
        this.available = 0;
        this.expected = 0;
    }

    public NotEnoughDataInByteBufferException(String s, Exception e) {
        super(s, e);
    }

    public NotEnoughDataInByteBufferException(int p_available, int p_expected) {
        super("Not enough data in byte buffer. Expected " + p_expected
                + ", available: " + p_available + ".");
        this.available = p_available;
        this.expected = p_expected;
    }

    public int getAvailable() {
        return this.available;
    }

    public int getExpected() {
        return this.expected;
    }

}
