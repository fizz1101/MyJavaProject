package com.fizz.cmpp.core.cmpp2;

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
    private byte[] auth;
    private short version;
    private long timeStamp;

    public CMPPConnect() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.header.setPk_cmd(CMPP_CONNECT);
        this.header.setPk_seq(super.getSeq());
        this.header.setPk_len(CMPP_LEN_CONNECT);
        this.sourceAddr = "";
        this.password = "";
        this.timeStamp = 0L;
        this.version = CMPP_VERSION20;
        this.auth = null;
    }

    public ByteBuffer getBody() {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(getHeader().getBody().getBuffer());
        b.appendString(getSourceAddr(), CMPP_LEN_SOURCE_ADDR);
        b.appendBytes(getAuth(), CMPP_LEN_AUTHENTICATOR_SOURCE);
        b.appendByte(ByteData.encodeUnsigned(this.version));
        b.appendInt(ByteData.encodeUnsigned(this.timeStamp));
        return b;
    }

    public void setBody(ByteBuffer buffer)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        reset();
        this.header.setBody(buffer.removeBuffer(CMPP_LEN_HEADER));
        setSourceAddr(buffer.removeString(CMPP_LEN_SOURCE_ADDR, ENC_CODE));
        setAuth(buffer.removeBuffer(CMPP_LEN_AUTHENTICATOR_SOURCE).getBuffer());
        setVersion(buffer.removeByte());
        setTimeStamp(ByteData.decodeUnsigned(buffer.removeInt()));
    }

    /**
     * 生成AuthenticatorSource
     * @author 张纯真
     */
    private void setAuth() {
        int position = 0;
        String sharedSecret = getPassword();

        byte[] authenSrcByte = new byte[CMPP_LEN_SOURCE_ADDR + CMPP_LEN_BYTE_ZERO + sharedSecret.length() + CMPP_LEN_TIMESTAMP];
        byte[] zero = new byte[CMPP_LEN_BYTE_ZERO];
        try {
            System.arraycopy(getSourceAddr().getBytes(ENC_CODE), 0, authenSrcByte, position, CMPP_LEN_SOURCE_ADDR);
            position += CMPP_LEN_SOURCE_ADDR;

            System.arraycopy(zero, 0, authenSrcByte, position, CMPP_LEN_BYTE_ZERO);
            position += CMPP_LEN_BYTE_ZERO;

            System.arraycopy(sharedSecret.getBytes(ENC_CODE), 0, authenSrcByte, position, sharedSecret.length());
            position += sharedSecret.length();

            byte[] timeStampByte = Long.toString(getTimeStamp()).getBytes(ENC_CODE);
            for (int i = 0; i < CMPP_LEN_TIMESTAMP-timeStampByte.length; i++) {
                authenSrcByte[(position++)] = 48;	//不足位数的前面补0
            }
            System.arraycopy(timeStampByte, 0, authenSrcByte, position, timeStampByte.length);
        } catch (Exception localException) {
        }
        this.auth = super.md5(authenSrcByte);
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

    public byte[] getAuth() {
        if (this.auth == null) {
            setAuth();
        }
        return this.auth;
    }

    private void setAuth(byte[] b) {
        this.auth = b;
    }

    public short getVersion() {
        return this.version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public long getTimeStamp() {
        if (this.timeStamp == 0) {
            this.timeStamp = super.getCmppDate();
        }
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
