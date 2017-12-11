package com.fizz.cmpp.core.base;

import com.fizz.cmpp.core.cmpp2.CMPPConnect;
import com.fizz.cmpp.core.cmpp2.CMPPConnectResp;
import com.fizz.cmpp.core.cmpp2.CMPPSubmit;
import com.fizz.cmpp.core.cmpp2.CMPPSubmitResp;
import com.fizz.cmpp.exception.DeliverFailException;
import com.fizz.cmpp.exception.NotEnoughDataInByteBufferException;
import com.fizz.cmpp.exception.WrongLengthOfStringException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fizz.cmpp.util.ByteBuffer;
import com.fizz.cmpp.util.ByteData;

public class CMPP implements CMPPMarco {

    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    private Socket socket = null;
    private static int seq = 0;
    public static final int CMPP_LEN_SUBMIT_EXCEPT_MSG_DESTCOUNT = 138;
    public static final int CMPP_LEN_SR = 71;

    public static final String SMPP_TIME_DATE_FORMAT = "yyMMddHHmmss";
    public static final String CMPP_TIME_DATE_FORMAT = "MMddHHmmss";
    public static final int CMPP_LEN_TIME = 17;
    public static final int CMPP_LEN_DELV_TIME = 10;
    public static final int CMPP_LEN_SP_NUM = 21;
    public static final int CMPP_LEN_MSISDN = 32;
    public static final int CMPP_LEN_LINK_ID = 20;
    public static final int CMPP_LEN_SR_STAT = 7;
    public static final int CMPP_LEN_STRING_MSGID = 64;
    public static final int CMPP_DFT_TP_PID = 0;
    public static final int CMPP_DFT_TP_UDHI = 0;
    public static final int CMPP_DFT_MSISDN_TYPE = 0;


    public static final String StringDefaultValue = "";
    public static final int ByteDefaultValue = 0;
    public static final int IntDefaultValue = 0;
    public static final int LongDefaultValue = 0;

    public int connect(CMPPConnect cmppConn, String ip, int port)
            throws IOException, NotEnoughDataInByteBufferException,
            DeliverFailException, WrongLengthOfStringException {
        int status = CMPP_E_SYSTEM;
        this.socket = new Socket(ip, port);
        this.socket.setKeepAlive(true);
        this.socket.setSoTimeout(CMPP_NET_TIMEOUT*1000);
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());

        send(cmppConn);
        CMPP temp = receive();
        if ((temp instanceof CMPPConnectResp)) {
            CMPPConnectResp resp = (CMPPConnectResp) temp;
            if (resp.getHeader().getPk_cmd() == CMPP_CONNECT_RESP) {
                status = resp.getStatus();
            } else {
                status = CMPP_E_RESP_PROT;
            }
        } else {
            status = CMPP_E_RESP_PROT;
        }
        return status;
    }

    public int submit(CMPPSubmit cmppSub) throws IOException,
            NotEnoughDataInByteBufferException, DeliverFailException,
            WrongLengthOfStringException {
        int result = CMPP_E_SYSTEM;
        send(cmppSub);
        CMPP temp = receive();
        if (temp instanceof CMPPSubmitResp) {
            CMPPSubmitResp resp = (CMPPSubmitResp) temp;
            if (resp.getHeader().getPk_cmd() == CMPP_SUBMIT_RESP) {
                result = resp.getResult();
            } else {
                result = CMPP_E_RESP_PROT;
            }
        } else {
            result = CMPP_E_RESP_PROT;
        }
        return result;
    }

    public void closeConn() throws IOException,
            NotEnoughDataInByteBufferException, DeliverFailException,
            WrongLengthOfStringException {
        CMPPTerminate terminate = new CMPPTerminate();
        send(terminate);
        receive();
        close();
    }

    public int send(CMPP cmpp) throws IOException {
        int status = 0;
        ByteBuffer b = null;
        if ((cmpp instanceof CMPPSubmit)) {
            CMPPSubmit submit = (CMPPSubmit) cmpp;
            b = submit.getBody();
            send_count_byte(b.getBuffer(), b.length());
        } else if ((cmpp instanceof CMPPTerminate)) {
            CMPPTerminate terminate = (CMPPTerminate) cmpp;
            b = terminate.getBody();
            send_count_byte(b.getBuffer(), b.length());
        } else if ((cmpp instanceof CMPPConnect)) {
            CMPPConnect connect = (CMPPConnect) cmpp;
            b = connect.getBody();
            send_count_byte(b.getBuffer(), b.length());
        }
        return status;
    }

    public CMPP receive() throws IOException,
            NotEnoughDataInByteBufferException, DeliverFailException,
            WrongLengthOfStringException {
        ByteBuffer b = new ByteBuffer(read_unknown_count_byte());
        ByteBuffer temp = new ByteBuffer(b.getBuffer());
        temp.removeInt();
        int commandId = temp.removeInt();
        if (commandId == CMPP_CONNECT_RESP) {
            CMPPConnectResp resp = new CMPPConnectResp();
            resp.setBody(b);
            return resp;
        }
        if (commandId == CMPP_SUBMIT_RESP) {
            CMPPSubmitResp resp = new CMPPSubmitResp();
            resp.setBody(b);
            return resp;
        }
        if (commandId == CMPP_TERMINATE_RESP) {
            CMPPTerminateResp resp = new CMPPTerminateResp();
            resp.setBody(b);
            return resp;
        }
        throw new DeliverFailException(commandId+"");
    }

    public void close() {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            } catch (IOException localIOException) {
            }
            this.inputStream = null;
        }
        if (this.outputStream != null) {
            try {
                this.outputStream.close();
            } catch (IOException localIOException1) {
            }
            this.outputStream = null;
        }
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException localIOException2) {
            }
            this.socket = null;
        }
    }

    private boolean send_count_byte(byte[] buf, int len) throws IOException {
        this.outputStream.write(buf, 0, len);
        this.outputStream.flush();
        return true;
    }

    private byte[] read_unknown_count_byte() throws IOException {
        byte[] ret = (byte[]) null;
        byte[] temp = (byte[]) null;
        byte[] bLen = read_count_byte(4);

        int len = ByteData.decodeInt(bLen);
        temp = read_count_byte(len - 4);
        ret = new byte[len];
        System.arraycopy(bLen, 0, ret, 0, 4);
        System.arraycopy(temp, 0, ret, 4, len - 4);
        return ret;
    }

    private byte[] read_count_byte(int len) throws IOException {
        byte[] ret = (byte[]) null;
        ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = this.inputStream.readByte();
        }
        return ret;
    }

	/*public byte[] string2ByteArray(String input, int byteArrayLen) {
		byte[] output;
		if (input == null) {
			output = new byte[byteArrayLen];
			for (int i = 0; i < byteArrayLen; i++) {
				output[i] = 0;
			}
		} else {
			byte[] temp = input.getBytes();
			int stringLen = temp.length;
			output = new byte[byteArrayLen];
			if (byteArrayLen >= stringLen) {
				System.arraycopy(temp, 0, output, 0, stringLen);
			} else {
				System.arraycopy(temp, 0, output, 0, byteArrayLen);
			}
		}
		return output;
	}

	public byte[] stringToNullTerminateGB2312ByteArray(String input) {
		byte[] output = (byte[]) null;
		if (input == null) {
			output = new byte[1];
			output[0] = 0;
		} else {
			try {
				byte[] temp = input.getBytes("GB2312");
				int len = temp.length;
				output = new byte[len + 1];
				System.arraycopy(temp, 0, output, 0, len);
				output[len] = 0;
			} catch (Exception e) {
				output = new byte[1];
				output[0] = 0;
			}
		}
		return output;
	}

	public byte[] byteArray2ByteArray(byte[] input, int byteArrayLen) {
		byte[] output = new byte[byteArrayLen];
		if (input != null) {
			int inputLen = input.length;
			if (byteArrayLen >= inputLen) {
				System.arraycopy(input, 0, output, 0, inputLen);
			} else {
				System.arraycopy(input, 0, output, 0, byteArrayLen);
			}
		}
		return output;
	}

	public String byteArrayToNullTerminateString(byte[] src, int begin) {
		int i = begin;
		while (src[begin] != 0) {
			begin++;
		}
		if (i != begin) {
			byte[] temp = new byte[begin - i];
			System.arraycopy(src, i, temp, 0, begin - i);
			return new String(temp);
		}
		return "";
	}

	public String byteArrayToString(byte[] src, int len) {
		String ret = null;
		byte[] temp = new byte[len];
		System.arraycopy(src, 0, temp, 0, len);
		ret = new String(temp);
		return ret;
	}

	public int byteArrayToInteger(byte[] src, int begin) {
		byte[] temp = new byte[4];
		int newInt = 0;
		System.arraycopy(src, begin, temp, 0, 4);
		newInt |= temp[0] << 24 & 0xFF000000;
		newInt |= temp[1] << 16 & 0xFF0000;
		newInt |= temp[2] << 8 & 0xFF00;
		newInt |= temp[3] & 0xFF;
		return newInt;
	}

	public short byteArrayToShort(byte[] src, int begin) {
		short newShort = 0;
		newShort = (short) src[begin];
		newShort = decodeUnsigned(newShort);
		return newShort;
	}

	public int nextStringPosition(int start, String previous) {
		int i = previous.getBytes().length;
		if (i > 0) {
			return start + i + 1;
		}
		return start + 1;
	}

	public int nextIntegerPosition(int start, byte[] previous) {
		int i = previous.length;
		if (i > 0) {
			return start + i + 1;
		}
		return start + 1;
	}

	public int nextIntegerPosition(int start) {
		return start + 4;
	}

	private static short decodeUnsigned(short signed) {
		if (signed >= 0) {
			return signed;
		}
		return (short) (65536 + signed);
	}*/

    /**
     * 获取本次序列号
     * @return
     */
    public static int getSeq() {
        seq ++;
        if (seq == Integer.MAX_VALUE) {
            seq = 0;
        }
        return seq;
    }

    /**
     * 获取当前时间的要求格式
     * @return
     */
    public static long getCmppDate() {
        SimpleDateFormat cmppDateFormatter = new SimpleDateFormat(CMPP_TIME_DATE_FORMAT);
        String dateStr = cmppDateFormatter.format(new Date());
        return Long.parseLong(dateStr);
    }

    /**
     * MD5加密(16位)
     * @param input
     * @return
     */
    public byte[] md5(byte[] input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        return md.digest(input);
    }


    public static final BigInteger BigIntegerDefaultValue = new BigInteger("0");
    public static byte[] byteArrayDefaultValue = null;
    public static final int CMPP_NET_TIMEOUT = 120;
    public static final short CMPP_TLV_TAG = 20481;

    public static final String CMPP_DELV_E_DELIVERED = "DELIVRD";
    public static final String CMPP_DELV_E_EXPIRED = "EXPIRED";
    public static final String CMPP_DELV_E_DELETED = "DELETED";
    public static final String CMPP_DELV_E_UNDELIVERABLE = "UNDELIV";
    public static final String CMPP_DELV_E_ACCEPTED = "ACCEPTD";
    public static final String CMPP_DELV_E_UNKNOWN = "UNKNOWN";
    public static final String CMPP_DELV_E_REJECTED = "REJECTD";
    public static final int CMPP_RESP_QUEUE_FULL = 52;
    public static final int CMPP_RESP_FLOW_CONTROL = 86;

}
