import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fizz.regex.core.RegexUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tttt {

    public static void main(String[] args) throws UnsupportedEncodingException {
        List<String> aaa =new ArrayList<>();
        aaa.remove(null);
    }

    public static String gb2312ToUtf8(String str) {

        String urlEncode = "";

        try {

            urlEncode = URLEncoder.encode (str, "UTF-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        }

        return urlEncode;

    }

}
