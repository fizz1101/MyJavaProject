package com.fizz.cmpp.core.base;

public interface CMPPMarco {

    /**version*/
    final short CMPP_VERSION20 = 32;	//CMPP2版本号
    final short CMPP_VERSION30 = 48;	//CMPP3版本号

    /**header*/
    final int CMPP_LEN_HEADER = 12;
    final int CMPP_MAX_HEADER_LEN = 16;
    final int CMPP_LEN_TOTLE = 4;
    final int CMPP_LEN_COMMAND = 4;
    final int CMPP_LEN_SEQUENCE = 4;

    /**connect*/
    final int CMPP_LEN_SOURCE_ADDR = 6;
    final int CMPP_LEN_AUTHENTICATOR_SOURCE = 16;
    final int CMPP_LEN_BYTE_ZERO = 9;
    final int CMPP_LEN_TIMESTAMP = 10;
    final int CMPP_LEN_CONNECT = 39;
    final int CMPP_LEN_CONNECT_RESP = 30;

    /**submit*/
    final int CMPP_LEN_MSGID = 8;
    final int CMPP_LEN_PK_TOTAL = 1;
    final int CMPP_LEN_PK_NUMBER = 1;
    final int CMPP_LEN_SERVICE_ID = 10;
    final int CMPP_LEN_FEE_TYPE = 2;
    final int CMPP_LEN_FEE_CODE = 6;
    final int CMPP_LEN_SUBMIT_RESP = 21;

    /**terminate*/
    final int CMPP_LEN_TERMINATE = 12;
    final int CMPP_LEN_TERMINATE_RESP = 12;

    /**active test*/
    final int CMPP_LEN_ACTIVE_TEST = 12;
    final int CMPP_LEN_ACTIVE_TEST_RESP = 13;

    /**commandId*/
    final int CMPP_CONNECT = 0x00000001;	//CMPP_CONNECT请求连接
    final int CMPP_CONNECT_RESP = 0x80000001;	//请求连接应答
    final int CMPP_TERMINATE = 0x00000002;	//终止连接
    final int CMPP_TERMINATE_RESP = 0x80000002;	//终止连接应答
    final int CMPP_SUBMIT = 0x00000004;	//提交短信
    final int CMPP_SUBMIT_RESP = 0x80000004;	//提交短信应答
    final int CMPP_DELIVER = 0x00000005;	//短信下发
    final int CMPP_DELIVER_RESP = 0x80000005;	//下发短信应答
    final int CMPP_QUERY = 0x00000006;	//发送短信状态查询
    final int CMPP_QUERY_RESP = 0x80000006;	//发送短信状态查询应答
    final int CMPP_CANCEL = 0x00000007;	//删除短信
    final int CMPP_CANCEL_RESP = 0x80000007;	//删除短信应答
    final int CMPP_ACTIVE_TEST = 0x00000008;	//激活测试
    final int CMPP_ACTIVE_TEST_RESP = 0x80000008;	//激活测试应答

    /**encode*/
    public static final String ENC_ASCII = "ASCII";
    public static final String ENC_GB = "GB2312";
    public static final String ENC_UCS2 = "UTF-16BE";
    public static final String ENC_UTF_8 = "UTF-8";
    public static final String ENC_UTF8 = "UTF8";
    public static final String ENC_UTF16 = "UTF-16";
    public static final String ENC_CODE = ENC_ASCII;

    /**result*/
    final int CMPP_E_SYSTEM = 1975;
    final int CMPP_E_RESP_PROT = 1976;
    final int CMPP_E_SUCCESS = 0;	//成功
    final int CMPP_DFLT_E_SUCCESS = 0;
    final int CMPP_DFLT_E_STRUCT = 1;
    final int CMPP_DFLT_E_COMMOND_ID = 2;
    final int CMPP_DFLT_E_SEQUENCE_REPEAT = 3;
    final int CMPP_DFLT_E_MSG_LEN = 4;
    final int CMPP_DFLT_E_FEE = 5;
    final int CMPP_DFLT_E_MAX_LEN = 6;
    final int CMPP_DFLT_E_SERVICE_ID = 7;
    final int CMPP_DFLT_E_FLOW = 8;
    final int CMPP_DFLT_E_OTHER = 9;

}
