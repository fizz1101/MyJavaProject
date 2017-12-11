package com.fizz.cmpp.core.base;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.util.ByteBuffer;

public final class CMPPHeader extends CMPP {

    protected int pk_len;
    protected int pk_cmd;
    protected int pk_seq;

    public CMPPHeader() {
        reset();
    }

    private void reset() {
        this.pk_len = 0;
        this.pk_cmd = 0;
        this.pk_seq = 0;
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();

        b.appendInt(this.pk_len);
        b.appendInt(this.pk_cmd);
        b.appendInt(this.pk_seq);

        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException {
        reset();

        ByteBuffer b = buffer;

        setPk_len(b.removeInt());
        setPk_cmd(b.removeInt());
        setPk_seq(b.removeInt());
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CMPPHeader { Command_Length = ");
        sb.append(this.pk_len);
        sb.append(", Command_ID = ");
        sb.append(Integer.toHexString(this.pk_cmd));
        sb.append(", Sequence_No = ");
        sb.append(this.pk_seq);
        sb.append("}");
        return sb.toString();
    }

    public int getPk_cmd() {
        return this.pk_cmd;
    }

    public void setPk_cmd(int pk_cmd) {
        this.pk_cmd = pk_cmd;
    }

    public int getPk_len() {
        return this.pk_len;
    }

    public void setPk_len(int pk_len) {
        this.pk_len = pk_len;
    }

    public int getPk_seq() {
        return this.pk_seq;
    }

    public void setPk_seq(int pk_seq) {
        this.pk_seq = pk_seq;
    }

}
