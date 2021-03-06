package com.fizz.cmpp.core.cmpp2;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.base.CMPPHeader;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.util.ByteBuffer;

import java.io.UnsupportedEncodingException;

public class CMPPSubmitResp extends CMPP {

    private CMPPHeader header;
    private long msgId;
    private byte result;

    public CMPPSubmitResp() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.msgId = 0L;
        this.result = 0;
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(getHeader().getBody().getBuffer());
        b.appendLong(getMsgId());
        b.appendByte(getResult());

        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        reset();

        this.header.setBody(buffer.removeBuffer(12));
        setMsgId(buffer.removeLong());
        setResult(buffer.removeByte());
    }

    public CMPPHeader getHeader() {
        return this.header;
    }

    public void setHeader(CMPPHeader header) {
        this.header = header;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public byte getResult() {
        return this.result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

}
