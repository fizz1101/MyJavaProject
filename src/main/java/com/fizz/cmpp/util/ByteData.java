package com.fizz.cmpp.util;

import com.fizz.cmpp.exception.IntegerOutOfRangeException;
import com.fizz.cmpp.exception.WrongDateFormatException;
import com.fizz.cmpp.exception.WrongLengthOfStringException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ByteData {

    private static SimpleDateFormat smppDateFormatter;
    private static SimpleDateFormat cmppDateFormatter;
    private static char[] zero = new char['?'];
    private static boolean libraryCheckDateFormat = true;

    static {
        smppDateFormatter = new SimpleDateFormat("yyMMddHHmmss");
        smppDateFormatter.setLenient(false);

        cmppDateFormatter = new SimpleDateFormat("MMddHHmmss");
        cmppDateFormatter.setLenient(false);
    }

    public static void strictCheckString(String string, int length)
            throws WrongLengthOfStringException {
        if (string.length() != length) {
            throw new WrongLengthOfStringException(length, length,
                    string.length());
        }
    }

    protected static void checkString(String string, int max)
            throws WrongLengthOfStringException {
        checkString(string, 0, max);
    }

    protected static void checkString(String string, int max, String encoding)
            throws WrongLengthOfStringException, UnsupportedEncodingException {
        checkString(string, 0, max, encoding);
    }

    protected static void checkString(String string, int min, int max)
            throws WrongLengthOfStringException {
        int length = string == null ? 0 : string.length();
        checkString(min, length, max);
    }

    protected static void checkString(String string, int min, int max,
                                      String encoding) throws WrongLengthOfStringException,
            UnsupportedEncodingException {
        byte[] stringBytes = string.getBytes(encoding);
        int length = stringBytes == null ? 0 : stringBytes.length;
        checkString(min, length, max);
    }

    protected static void checkString(int min, int length, int max)
            throws WrongLengthOfStringException {
        if ((length < min) || (length > max)) {
            throw new WrongLengthOfStringException(min, max, length);
        }
    }

    protected static void checkCString(String string, int max)
            throws WrongLengthOfStringException {
        checkCString(string, 1, max);
    }

    protected static void checkCString(String string, int min, int max)
            throws WrongLengthOfStringException {
        int count = string == null ? 1 : string.length();
        if ((count < min) || (count > max)) {
            throw new WrongLengthOfStringException(min, max, count);
        }
    }

    public static long getSmppDate() {
        String dateStr = smppDateFormatter.format(new Date());
        return Long.parseLong(dateStr);
    }

    public static long getCmppDate() {
        String dateStr = cmppDateFormatter.format(new Date());
        return Long.parseLong(dateStr);
    }

    protected static void checkDate(String dateStr)
            throws WrongDateFormatException {
        int count = dateStr == null ? 1 : dateStr.length() + 1;
        if ((count != 1) && (count != 17)) {
            throw new WrongDateFormatException(dateStr);
        }
        if ((count == 1) || (!libraryCheckDateFormat)) {
            return;
        }
        char locTime = dateStr.charAt(dateStr.length() - 1);
        if ("+-R".lastIndexOf(locTime) == -1) {
            throw new WrongDateFormatException(dateStr,
                    "time difference relation indicator incorrect; should be +, - or R and is "
                            + locTime);
        }
        int formatLen = "yyMMddHHmmss".length();
        String dateJavaStr = dateStr.substring(0, formatLen);
        Date date = null;
        synchronized (smppDateFormatter) {
            try {
                if (locTime == 'R') {
                    Long.parseLong(dateJavaStr);
                } else {
                    date = smppDateFormatter.parse(dateJavaStr);
                }
            } catch (ParseException e) {
                throw new WrongDateFormatException(dateStr,
                        "format of absolute date-time incorrect");
            } catch (NumberFormatException e) {
                throw new WrongDateFormatException(dateStr,
                        "format of relative date-time incorrect");
            }
        }
        String tenthsOfSecStr = dateStr.substring(formatLen, formatLen + 1);
        int tenthsOfSec = 0;
        try {
            tenthsOfSec = Integer.parseInt(tenthsOfSecStr);
        } catch (NumberFormatException e) {
            throw new WrongDateFormatException(dateStr,
                    "non-numeric tenths of seconds " + tenthsOfSecStr);
        }
        String timeDiffStr = dateStr.substring(formatLen + 1, formatLen + 3);
        int timeDiff = 0;
        try {
            timeDiff = Integer.parseInt(timeDiffStr);
        } catch (NumberFormatException e) {
            throw new WrongDateFormatException(dateStr,
                    "non-numeric time difference " + timeDiffStr);
        }
        if ((timeDiff < 0) || (timeDiff > 48)) {
            throw new WrongDateFormatException(dateStr,
                    "time difference is incorrect; should be between 00-48 and is "
                            + timeDiffStr);
        }
    }

    protected static void checkRange(int min, int val, int max)
            throws IntegerOutOfRangeException {
        if ((val < min) || (val > max)) {
            throw new IntegerOutOfRangeException(min, max, val);
        }
    }

    public static int decodeUnsignedbits(byte signed, int count) {
        int unsignedint = 1;
        for (int i = 0; i < count; i++) {
            unsignedint *= 2;
        }
        if (signed >= 0) {
            return signed;
        }
        return unsignedint + signed;
    }

    public static short decodeUnsigned(byte signed) {
        if (signed >= 0) {
            return signed;
        }
        return (short) (256 + (short) signed);
    }


    public static int decodeInt(byte[] signed) {
        int ret = 0;
        int ch0 = decodeUnsigned(signed[0]);
        int ch1 = decodeUnsigned(signed[1]);
        int ch2 = decodeUnsigned(signed[2]);
        int ch3 = decodeUnsigned(signed[3]);

        return (ch0 << 24) + (ch1 << 16) + (ch2 << 8) + ch3;
    }

    public static int decodeUnsigned(byte[] signed, int count) {
        int num = 0;
        byte b = 0;
        for (int i = 0; i < count; i++) {
            num = num + signed[i] << 8 * (count - 1);
        }
        return num;
    }

    public static int decodeUnsigned(short signed) {
        if (signed >= 0) {
            return signed;
        }
        return 65536 + signed;
    }

    public static long decodeUnsigned(int signed) {
        if (signed >= 0) {
            return signed;
        }
        Long l = new Long("4294967296");
        return l.longValue() + signed;
    }

    public static long decodeUnsigned(long signed) {
        if (signed >= 0L) {
            return signed;
        }
        BigInteger l = new BigInteger("18446744073709551616");
        return l.longValue() + signed;
    }

    public static byte encodeUnsigned(short positive) {
        if (positive < 128) {
            return (byte) positive;
        }
        return (byte) -(256 - positive);
    }

    public static short encodeUnsigned(int positive) {
        if (positive < 32768) {
            return (short) positive;
        }
        return (short) -(65536 - positive);
    }

    public static int encodeUnsigned(long positive) {
        Long value = new Long("2147483648");
        if (positive < value.longValue()) {
            return (int) positive;
        }
        value = new Long("4294967296");
        return (int) -(value.longValue() - positive);
    }

    public static String getMsgId(long msgId) {
        ByteBuffer bc = ByteBuffer.allocate(8);
        bc.position(0);
        byte[] ba = bc.putLong(msgId).array();

        String str = "";

        byte tb0 = ba[0];
        byte tb1 = ba[1];
        byte tb2 = ba[2];
        byte tb3 = ba[3];
        byte tb4 = ba[4];
        byte tb5 = ba[5];
        byte tb6 = ba[6];
        byte tb7 = ba[7];

        str = ""+decodeUnsignedbits((byte) (tb0 >>> 4), 4);
        str = str
                + decodeUnsignedbits(
                (byte) (tb1 >>> 7 & 0x1 | tb0 << 1 & 0x1E), 5);
        int ti = decodeUnsignedbits((byte) (tb1 >> 2 & 0x1F), 5);
        if (ti < 10) {
            str = str + "0" + ti;
        } else {
            str = str + ti;
        }
        ti = decodeUnsignedbits((byte) (tb1 << 4 & 0x30 | tb2 >> 4 & 0xF), 6);
        if (ti < 10) {
            str = str + "0" + ti;
        } else {
            str = str + ti;
        }
        ti = decodeUnsignedbits((byte) (tb2 << 2 & 0x3C | tb3 >> 6 & 0x3), 6);
        if (ti < 10) {
            str = str + "0" + ti;
        } else {
            str = str + ti;
        }
        byte[] tbx = new byte[3];
        tbx[0] = ((byte) (tb3 & 0x3F));
        tbx[1] = tb4;
        tbx[2] = tb5;
        ti = decodeUnsigned(tbx, 3);
        if (ti < 10) {
            str = str + "00000" + ti;
        } else if (ti < 100) {
            str = str + "0000" + ti;
        } else if (ti < 1000) {
            str = str + "000" + ti;
        } else if (ti < 10000) {
            str = str + "00" + ti;
        } else if (ti < 100000) {
            str = str + "0" + ti;
        } else {
            str = str + ti;
        }
        byte[] tby = new byte[2];
        tby[0] = tb6;
        tby[1] = tb7;
        ti = decodeUnsigned(tby, 2);
        if (ti < 10) {
            str = str + "00000" + ti;
        } else if (ti < 100) {
            str = str + "0000" + ti;
        } else if (ti < 1000) {
            str = str + "000" + ti;
        } else if (ti < 10000) {
            str = str + "00" + ti;
        } else if (ti < 100000) {
            str = str + "0" + ti;
        } else {
            str = str + ti;
        }
        return str;
    }

    public static String geCOctecttStringValue(byte[] msg, int maxlength,
                                               int messageIndex) throws WrongLengthOfStringException {
        boolean nullReached = false;
        int target_len = 0;
        int msg_len = msg.length;
        int startPoint = messageIndex;
        while ((!nullReached) && (messageIndex <= msg_len)
                && (target_len < maxlength)) {
            if (msg[messageIndex] == 0) {
                nullReached = true;
            } else {
                target_len++;
            }
            messageIndex++;
        }
        if (target_len < maxlength) {
            byte[] result = new byte[target_len];
            System.arraycopy(msg, startPoint, result, 0, target_len);
            return new String(result);
        }
        throw new WrongLengthOfStringException("字符串转换长度[" + target_len
                + 1 + "]超过最大长度[" + maxlength + "]");
    }

    public static String long2UnsignedLongString(long in) {
        BigInteger b = new BigInteger(Long.toHexString(in), 16);
        return b.toString(10);
    }

}
