package com.fizz.cmpp.util;

import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.exception.TerminatingZeroNotFoundException;

import java.io.UnsupportedEncodingException;

public class ByteBuffer {

    private byte[] buffer;
    private static final byte SZ_BYTE = 1;
    private static final byte SZ_SHORT = 2;
    private static final byte SZ_INT = 4;
    private static final byte SZ_LONG = 8;
    private static byte[] zero = new byte['?'];

    public ByteBuffer() {
        int i = 0;
        this.buffer = null;
        for (i = 0; i < zero.length; i++) {
            zero[i] = 0;
        }
    }

    public ByteBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int length() {
        if (this.buffer == null) {
            return 0;
        }
        return this.buffer.length;
    }

    private static int length(byte[] buffer) {
        if (buffer == null) {
            return 0;
        }
        return buffer.length;
    }

    public void appendByte(byte data) {
        byte[] byteBuf = new byte[1];
        byteBuf[0] = data;
        appendBytes0(byteBuf, 1);
    }

    public void appendShort(short data) {
        byte[] shortBuf = new byte[2];
        shortBuf[1] = ((byte) (data & 0xFF));
        shortBuf[0] = ((byte) (data >>> 8 & 0xFF));
        appendBytes0(shortBuf, 2);
    }

    public void appendInt(int data) {
        byte[] intBuf = new byte[4];
        intBuf[3] = ((byte) (data & 0xFF));
        intBuf[2] = ((byte) (data >>> 8 & 0xFF));
        intBuf[1] = ((byte) (data >>> 16 & 0xFF));
        intBuf[0] = ((byte) (data >>> 24 & 0xFF));
        appendBytes0(intBuf, 4);
    }

    public void appendLong(long data) {
        byte[] longBuf = new byte[8];
        longBuf[7] = ((byte) (int) (data & 0xFF));
        longBuf[6] = ((byte) (int) (data >>> 8 & 0xFF));
        longBuf[5] = ((byte) (int) (data >>> 16 & 0xFF));
        longBuf[4] = ((byte) (int) (data >>> 24 & 0xFF));
        longBuf[3] = ((byte) (int) (data >>> 32 & 0xFF));
        longBuf[2] = ((byte) (int) (data >>> 40 & 0xFF));
        longBuf[1] = ((byte) (int) (data >>> 48 & 0xFF));
        longBuf[0] = ((byte) (int) (data >>> 56 & 0xFF));
        appendBytes0(longBuf, 8);
    }

    public void appendString(String string) {
        try {
            appendString0(string, string.length(), "ASCII");
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
        }
    }

    public void appendString(String string, String encoding)
            throws UnsupportedEncodingException {
        appendString0(string, string.length(), encoding);
    }

    public void appendString(String string, int length) {
        try {
            appendString0(string, length, "ASCII");
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
        }
    }

    private void appendString0(String string, int length, String encoding)
            throws UnsupportedEncodingException {
        UnsupportedEncodingException encodingException = null;
        byte[] stringBuf = (byte[]) null;
        if ((string != null) && (string.length() > 0)) {
            try {
                if (encoding != null) {
                    if (string.length() > length) {
                        stringBuf = string.substring(0, length).getBytes(
                                encoding);
                    } else {
                        stringBuf = string.getBytes(encoding);
                    }
                } else if (string.length() > length) {
                    stringBuf = string.substring(0, length).getBytes();
                } else {
                    stringBuf = string.getBytes();
                }
            } catch (UnsupportedEncodingException e) {
                encodingException = e;
            }
            if ((stringBuf != null) && (stringBuf.length > 0)) {
                appendBytes0(stringBuf, stringBuf.length);
            }
        } else {
            appendBytes(zero, length);
            return;
        }
        if (encodingException != null) {
            throw encodingException;
        }
        if (stringBuf.length < length) {
            appendBytes0(zero, length - stringBuf.length);
        }
    }

    public void appendBuffer(ByteBuffer buf) {
        if (buf != null) {
            try {
                appendBytes(buf, buf.length());
            } catch (NotEnoughDataInByteBufferException localNotEnoughDataInByteBufferException) {
            }
        }
    }

    public void appendBytes(ByteBuffer bytes, int count)
            throws NotEnoughDataInByteBufferException {
        if (count > 0) {
            if (bytes == null) {
                throw new NotEnoughDataInByteBufferException(0, count);
            }
            if (bytes.length() < count) {
                throw new NotEnoughDataInByteBufferException(bytes.length(),
                        count);
            }
            appendBytes0(bytes.getBuffer(), count);
        }
    }

    public void appendBytes(byte[] bytes, int count) {
        if (bytes != null) {
            if (count > bytes.length) {
                count = bytes.length;
            }
            appendBytes0(bytes, count);
        }
    }

    public void appendBytes(byte[] bytes) {
        if (bytes != null) {
            appendBytes0(bytes, bytes.length);
        }
    }

    public byte removeByte() throws NotEnoughDataInByteBufferException {
        byte result = 0;
        byte[] resBuff = removeBytes(1).getBuffer();
        result = resBuff[0];
        return result;
    }

    public short removeShort() throws NotEnoughDataInByteBufferException {
        short result = 0;
        result = readShort();
        removeBytes0(2);
        return result;
    }

    public short readShort() throws NotEnoughDataInByteBufferException {
        short result = 0;
        result = (short) (result | this.buffer[0] & 0xFF);
        result = (short) (result << 8);
        result = (short) (result | this.buffer[1] & 0xFF);
        return result;
    }

    public int removeInt() throws NotEnoughDataInByteBufferException {
        int value = readInt();
        removeBytes0(4);
        return value;
    }

    public long removeLong() throws NotEnoughDataInByteBufferException {
        long value = readLong();
        removeBytes0(8);
        return value;
    }

    public int readInt() throws NotEnoughDataInByteBufferException {
        int result = 0;
        int len = length();
        if (len >= 4) {
            result |= this.buffer[0] & 0xFF;
            result <<= 8;
            result |= this.buffer[1] & 0xFF;
            result <<= 8;
            result |= this.buffer[2] & 0xFF;
            result <<= 8;
            result |= this.buffer[3] & 0xFF;
            return result;
        }
        throw new NotEnoughDataInByteBufferException(len, 4);
    }

    public long readLong() throws NotEnoughDataInByteBufferException {
        int i = 0;
        long result = 0L;
        int len = length();
        if (len >= 8) {
            for (i = 0; i < 7; i++) {
                result |= this.buffer[i] & 0xFF;
                result <<= 8;
            }
            result |= this.buffer[7] & 0xFF;

            return result;
        }
        throw new NotEnoughDataInByteBufferException(len, 8);
    }

    public String removeCString() throws NotEnoughDataInByteBufferException,
            TerminatingZeroNotFoundException {
        int len = length();
        int zeroPos = 0;
        if (len == 0) {
            throw new NotEnoughDataInByteBufferException(0, 1);
        }
        while ((zeroPos < len) && (this.buffer[zeroPos] != 0)) {
            zeroPos++;
        }
        if (zeroPos < len) {
            String result = null;
            if (zeroPos > 0) {
                try {
                    result = new String(this.buffer, 0, zeroPos, "ASCII");
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                result = new String("");
            }
            removeBytes0(zeroPos + 1);
            return result;
        }
        throw new TerminatingZeroNotFoundException();
    }

    public String removeString(int size, String encoding)
            throws NotEnoughDataInByteBufferException,
            UnsupportedEncodingException {
        int len = length();
        if (len < size) {
            throw new NotEnoughDataInByteBufferException(len, size);
        }
        UnsupportedEncodingException encodingException = null;
        String result = null;
        if (len > 0) {
            try {
                if (encoding != null) {
                    result = new String(this.buffer, 0, size, encoding);
                } else {
                    result = new String(this.buffer, 0, size);
                }
            } catch (UnsupportedEncodingException e) {
                encodingException = e;
            }
            removeBytes0(size);
        } else {
            result = new String("");
        }
        if (encodingException != null) {
            throw encodingException;
        }
        int index = result.indexOf(0);
        if ((index != -1) && (index < len)) {
            result = result.substring(0, index);
        }
        return result;
    }

    public ByteBuffer removeBuffer(int count)
            throws NotEnoughDataInByteBufferException {
        return removeBytes(count);
    }

    public ByteBuffer removeBytes(int count)
            throws NotEnoughDataInByteBufferException {
        ByteBuffer result = readBytes(count);
        removeBytes0(count);
        return result;
    }

    public void removeBytes0(int count)
            throws NotEnoughDataInByteBufferException {
        int len = length();
        int lefts = len - count;
        if (lefts > 0) {
            byte[] newBuf = new byte[lefts];
            System.arraycopy(this.buffer, count, newBuf, 0, lefts);
            setBuffer(newBuf);
        } else {
            setBuffer(null);
        }
    }

    public ByteBuffer readBytes(int count)
            throws NotEnoughDataInByteBufferException {
        int len = length();
        ByteBuffer result = null;
        if (count > 0) {
            if (len >= count) {
                byte[] resBuf = new byte[count];
                System.arraycopy(this.buffer, 0, resBuf, 0, count);
                result = new ByteBuffer(resBuf);
                return result;
            }
            throw new NotEnoughDataInByteBufferException(len, count);
        }
        return result;
    }

    private void appendBytes0(byte[] bytes, int count) {
        int len = length();
        byte[] newBuf = new byte[len + count];
        if (len > 0) {
            System.arraycopy(this.buffer, 0, newBuf, 0, len);
        }
        System.arraycopy(bytes, 0, newBuf, len, count);
        setBuffer(newBuf);
    }

    public static int readIntegerBits(int value, int offset, int length) {
        if ((offset < 0) || (length < 0) || (offset + length > 32)) {
            return 0;
        }
        return value << 32 - offset - length >>> 32 - length;
    }

    public String getHexDump() {
        String dump = "";
        try {
            int dataLen = length();
            byte[] buffer = getBuffer();
            for (int i = 0; i < dataLen; i++) {
                dump = dump + Character.forDigit(buffer[i] >> 4 & 0xF, 16);
                dump = dump + Character.forDigit(buffer[i] & 0xF, 16);
            }
        } catch (Throwable t) {
            dump = "Throwable caught when dumping = " + t;
        }
        return dump;
    }

}
