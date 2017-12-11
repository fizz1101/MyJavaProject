package com.fizz.cmpp.core.cmpp3;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.base.CMPPHeader;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.util.ByteBuffer;
import com.fizz.cmpp.util.ByteData;

import java.io.UnsupportedEncodingException;

public class CMPPConnect extends CMPP {

    private CMPPHeader header;
    private String sourceAddr;
    private String password;
    private long timeStamp;
    private byte[] auth;
    private short version;

    public CMPPConnect() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.header.setPk_cmd(1);
        this.header.setPk_seq(CMPP.getSeq());
        this.header.setPk_len(39);
        this.sourceAddr = "";
        this.password = "";
        this.timeStamp = 0L;
        this.version = 48;
        this.auth = null;
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(getHeader().getBody().getBuffer());
        b.appendString(getSourceAddr(), 6);
        b.appendBytes(getAuth(), 16);
        b.appendByte(ByteData.encodeUnsigned(this.version));
        b.appendInt(ByteData.encodeUnsigned(this.timeStamp));

        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        reset();

        this.header.setBody(buffer.removeBuffer(12));
        setSourceAddr(buffer.removeString(6, "ASCII"));
        setAuth(buffer.removeBuffer(16).getBuffer());
        setVersion(buffer.removeByte());
        setTimeStamp(ByteData.decodeUnsigned(buffer.removeInt()));
    }

    public byte[] getAuth() {
        if (this.auth == null) {
            setAuth();
        }
        return this.auth;
    }

    private void setAuth(byte[] b) {
        this.auth = b;
    }

    private void setAuth() {
        int position = 0;
        String sharedSecret = getPassword();

        byte[] authenSrcByte = new byte[15 + sharedSecret.length() + 10];
        byte[] zero = new byte[9];
        try {
            System.arraycopy(getSourceAddr().getBytes("ASCII"), 0,
                    authenSrcByte, position, 6);
            position += 6;

            System.arraycopy(zero, 0, authenSrcByte, position, 9);
            position += 9;

            System.arraycopy(sharedSecret.getBytes("ASCII"), 0, authenSrcByte,
                    position, sharedSecret.length());
            position += sharedSecret.length();

            byte[] timeStampByte = Long.toString(getTimeStamp()).getBytes();
            for (int i = 0; i < 10 - timeStampByte.length; i++) {
                authenSrcByte[(position++)] = 48;
            }
            System.arraycopy(timeStampByte, 0, authenSrcByte, position,
                    timeStampByte.length);
        } catch (Exception localException) {
        }
        this.auth = md5(authenSrcByte);
    }

    public String getSourceAddr() {
        return this.sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public CMPPHeader getHeader() {
        return this.header;
    }

    public void setHeader(CMPPHeader header) {
        this.header = header;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public short getVersion() {
        return this.version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

}
