package com.fizz.cmpp.core.cmpp3;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.base.CMPPHeader;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.util.ByteBuffer;
import com.fizz.cmpp.util.ByteData;

import java.io.UnsupportedEncodingException;

public class CMPPConnectResp extends CMPP {

    private CMPPHeader header;
    private int status;
    private byte[] auth;
    private short version;

    public CMPPConnectResp() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.status = 0;
        this.version = 0;
        this.auth = null;
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(getHeader().getBody().getBuffer());
        b.appendInt(getStatus());
        b.appendBytes(getAuth(), 16);
        b.appendByte(ByteData.encodeUnsigned(this.version));

        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        reset();

        this.header.setBody(buffer.removeBuffer(12));
        setStatus(buffer.removeInt());
        setAuth(buffer.removeBuffer(16).getBuffer());
        setVersion(buffer.removeByte());
    }

    public byte[] getAuth() {
        return this.auth;
    }

    public void setAuth(byte[] auth) {
        this.auth = auth;
    }

    public CMPPHeader getHeader() {
        return this.header;
    }

    public void setHeader(CMPPHeader header) {
        this.header = header;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public short getVersion() {
        return this.version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

}
