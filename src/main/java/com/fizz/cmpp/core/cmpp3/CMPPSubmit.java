package com.fizz.cmpp.core.cmpp3;

import com.fizz.cmpp.core.base.CMPP;
import com.fizz.cmpp.core.base.CMPPHeader;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.exception.WrongLengthOfStringException;
import com.fizz.cmpp.util.ByteBuffer;
import com.fizz.cmpp.util.ByteData;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;

public class CMPPSubmit extends CMPP {

    private CMPPHeader header;
    private long msgId;
    private short pkTotal;
    private short pkNumber;
    private short registeredDelivery;
    private short msgLevel;
    private String serviceId;
    private short feeUserType;
    private String feeTerminalId;
    private short feeTerminalType;
    private short TPPId;
    private short TPUdhi;
    private short msgFmt;
    private String msgSrc;
    private String feeType;
    private String feeCode;
    private String validTime;
    private String atTime;
    private String srcId;
    private short destUsrTotal;
    private LinkedList destTerminalId;
    private short destTerminalType;
    private short msgLength;
    private byte[] msgContent;
    private String linkId;

    public CMPPSubmit() {
        reset();
    }

    private void reset() {
        this.header = new CMPPHeader();
        this.header.setPk_cmd(4);
        this.header.setPk_seq(CMPP.getSeq());

        this.msgId = 0L;
        this.pkTotal = 1;
        this.pkNumber = 1;
        this.registeredDelivery = 0;
        this.msgLevel = 0;
        this.serviceId = "";
        this.feeUserType = 0;
        this.feeTerminalId = "";
        this.feeTerminalType = 0;
        this.TPPId = 0;
        this.TPUdhi = 0;
        this.msgFmt = 0;
        this.msgSrc = "";
        this.feeType = "";
        this.feeCode = "";
        this.validTime = "";
        this.atTime = "";
        this.srcId = "";
        this.destUsrTotal = 0;
        this.destTerminalId = new LinkedList();
        this.destTerminalType = 0;
        this.msgLength = 0;
        this.linkId = "";
        this.msgContent = null;
    }

    public ByteBuffer getBody() {
        if (getDestUsrTotal() != getDestTerminalId().size()) {
            setDestUsrTotal((short) getDestTerminalId().size());
        }
        int len = 163 + getMsgLength() + getDestUsrTotal() * 32;

        ByteBuffer buffer = new ByteBuffer();
        getHeader().setPk_len(len);
        buffer.appendBytes(getHeader().getBody().getBuffer());
        buffer.appendLong(getMsgId());
        buffer.appendByte(ByteData.encodeUnsigned(getPkTotal()));
        buffer.appendByte(ByteData.encodeUnsigned(getPkNumber()));
        buffer.appendByte(ByteData.encodeUnsigned(getRegisteredDelivery()));
        buffer.appendByte(ByteData.encodeUnsigned(getMsgLevel()));
        buffer.appendString(getServiceId(), 10);
        buffer.appendByte(ByteData.encodeUnsigned(getFeeUserType()));
        buffer.appendString(getFeeTerminalId(), 32);
        buffer.appendByte(ByteData.encodeUnsigned(getFeeTerminalType()));
        buffer.appendByte(ByteData.encodeUnsigned(getTPPId()));
        buffer.appendByte(ByteData.encodeUnsigned(getTPUdhi()));
        buffer.appendByte(ByteData.encodeUnsigned(getMsgFmt()));
        buffer.appendString(getMsgSrc(), 6);
        buffer.appendString(getFeeType(), 2);
        buffer.appendString(getFeeCode(), 6);
        buffer.appendString(getValidTime(), 17);
        buffer.appendString(getAtTime(), 17);
        buffer.appendString(getSrcId(), 21);

        buffer.appendByte(ByteData.encodeUnsigned(getDestUsrTotal()));
        Iterator iterator = getDestTerminalId().iterator();
        while (iterator.hasNext()) {
            buffer.appendString((String) iterator.next(), 32);
        }
        buffer.appendByte(ByteData.encodeUnsigned(getDestTerminalType()));
        buffer.appendByte(ByteData.encodeUnsigned(getMsgLength()));
        buffer.appendBuffer(new ByteBuffer(this.msgContent));
        buffer.appendString(getLinkId(), 20);
        return buffer;
    }

    public void setBody(ByteBuffer b)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException, WrongLengthOfStringException {
        reset();

        this.header.setBody(b.removeBuffer(12));
        setMsgId(b.removeLong());
        setPkTotal(ByteData.decodeUnsigned(b.removeByte()));
        setPkNumber(ByteData.decodeUnsigned(b.removeByte()));
        setRegisteredDelivery(b.removeByte());
        setMsgLevel(ByteData.decodeUnsigned(b.removeByte()));
        setServiceId(b.removeString(10, "ASCII"));
        setFeeUserType(ByteData.decodeUnsigned(b.removeByte()));
        setFeeTerminalId(b.removeString(32, "ASCII"));
        setFeeTerminalType(ByteData.decodeUnsigned(b.removeByte()));
        setTPPId(ByteData.decodeUnsigned(b.removeByte()));
        setTPUdhi(ByteData.decodeUnsigned(b.removeByte()));
        setMsgFmt(ByteData.decodeUnsigned(b.removeByte()));
        setMsgSrc(b.removeString(6, "ASCII"));
        setFeeType(b.removeString(2, "ASCII"));
        setFeeCode(b.removeString(6, "ASCII"));
        setValidTime(b.removeString(17, "ASCII"));
        setAtTime(b.removeString(17, "ASCII"));
        setSrcId(b.removeString(21, "ASCII"));

        setDestUsrTotal(ByteData.decodeUnsigned(b.removeByte()));
        setDestTerminalId(b.removeString(32, "ASCII"));
        if (getDestUsrTotal() > 1) {
            for (int i = 1; i < getDestUsrTotal(); i++) {
                addDestTerminalId(b.removeString(32, "ASCII"));
            }
        }
        setDestTerminalId(b.removeString(getDestUsrTotal() * 32, "ASCII"));
        setDestTerminalType(ByteData.decodeUnsigned(b.removeByte()));

        setMsgLength(ByteData.decodeUnsigned(b.removeByte()));
        setMsgContent(b.removeBuffer(getMsgLength()).getBuffer());
        setLinkId(b.removeString(20, "ASCII"));
    }

    public void setDestTerminalId(String destAddr) {
        this.destTerminalId.add(destAddr);
        this.destUsrTotal = 1;
    }

    public void addDestTerminalId(String destAddr) {
        this.destTerminalId.add(destAddr);
        this.destUsrTotal = ((short) (this.destUsrTotal + 1));
    }

    public void setMsgContent(String value) {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(value.getBytes());
        this.msgContent = b.getBuffer();
        setMsgLength((short) this.msgContent.length);
    }

    public void setMsgContent(String value, String encoding)
            throws UnsupportedEncodingException {
        ByteBuffer b = new ByteBuffer();
        b.appendBytes(value.getBytes(encoding));
        this.msgContent = b.getBuffer();
        setMsgLength((short) this.msgContent.length);
    }

    public String getAtTime() {
        return this.atTime;
    }

    public void setAtTime(String atTime) {
        this.atTime = atTime;
    }

    public LinkedList getDestTerminalId() {
        return this.destTerminalId;
    }

    public void setDestTerminalId(LinkedList destTerminalId) {
        this.destTerminalId = destTerminalId;
    }

    public short getDestTerminalType() {
        return this.destTerminalType;
    }

    private void setDestTerminalType(short destTerminalType) {
        this.destTerminalType = destTerminalType;
    }

    public short getDestUsrTotal() {
        return this.destUsrTotal;
    }

    public void setDestUsrTotal(short destUsrTotal) {
        this.destUsrTotal = destUsrTotal;
    }

    public String getFeeCode() {
        return this.feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getFeeTerminalId() {
        return this.feeTerminalId;
    }

    public void setFeeTerminalId(String feeTerminalId) {
        this.feeTerminalId = feeTerminalId;
    }

    public short getFeeTerminalType() {
        return this.feeTerminalType;
    }

    private void setFeeTerminalType(short feeTerminalType) {
        this.feeTerminalType = feeTerminalType;
    }

    public String getFeeType() {
        return this.feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public short getFeeUserType() {
        return this.feeUserType;
    }

    public void setFeeUserType(short feeUserType) {
        this.feeUserType = feeUserType;
    }

    public CMPPHeader getHeader() {
        return this.header;
    }

    public void setHeader(CMPPHeader header) {
        this.header = header;
    }

    public String getLinkId() {
        return this.linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public byte[] getMsgContent() {
        return this.msgContent;
    }

    public void setMsgContent(byte[] msgContent) {
        this.msgContent = msgContent;
        this.msgLength = ((short) msgContent.length);
    }

    public short getMsgFmt() {
        return this.msgFmt;
    }

    public void setMsgFmt(short msgFmt) {
        this.msgFmt = msgFmt;
    }

    public long getMsgId() {
        return this.msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public short getMsgLength() {
        return this.msgLength;
    }

    public void setMsgLength(short msgLength) {
        this.msgLength = msgLength;
    }

    public short getMsgLevel() {
        return this.msgLevel;
    }

    public void setMsgLevel(short msgLevel) {
        this.msgLevel = msgLevel;
    }

    public String getMsgSrc() {
        return this.msgSrc;
    }

    public void setMsgSrc(String msgSrc) {
        this.msgSrc = msgSrc;
    }

    public short getPkNumber() {
        return this.pkNumber;
    }

    public short getPkTotal() {
        return this.pkTotal;
    }

    public short getRegisteredDelivery() {
        return this.registeredDelivery;
    }

    public void setRegisteredDelivery(short registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSrcId() {
        return this.srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public short getTPPId() {
        return this.TPPId;
    }

    private void setTPPId(short id) {
        this.TPPId = id;
    }

    public short getTPUdhi() {
        return this.TPUdhi;
    }

    private void setTPUdhi(short udhi) {
        this.TPUdhi = udhi;
    }

    public String getValidTime() {
        return this.validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    private void setPkNumber(short pkNumber) {
        this.pkNumber = pkNumber;
    }

    private void setPkTotal(short pkTotal) {
        this.pkTotal = pkTotal;
    }

}
