package com.fizz.cmpp.core.base;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.base.CMPPHeader;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.util.ByteBuffer;

import java.io.UnsupportedEncodingException;

public class CMPPTerminateResp extends CMPP {

    private CMPPHeader header;

    public CMPPTerminateResp() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.header.setPk_cmd(CMPP_LEN_TERMINATE_RESP);
        this.header.setPk_seq(CMPP.getSeq());
        this.header.setPk_len(CMPP_LEN_HEADER);
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(getHeader().getBody().getBuffer());
        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        reset();

        this.header.setBody(buffer.removeBuffer(CMPP_LEN_TERMINATE_RESP));
    }

    public CMPPHeader getHeader() {
        return this.header;
    }

    public void setHeader(CMPPHeader header) {
        this.header = header;
    }

}
