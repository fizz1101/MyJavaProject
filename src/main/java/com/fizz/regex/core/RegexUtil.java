package com.fizz.regex.core;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 正则校验工具类
 */
public class RegexUtil {

    public static final String REGEX_MOBILE = "^[1][3-9][0-9]{9}$";

    /**
     * 返回匹配结果boolean
     * @param matchStr  校验字符串
     * @param regStr    校验正则表达式
     * @return
     */
    public static boolean matcherResult(String matchStr,String regStr){
        Pattern pattern = Pattern.compile(regStr);
        Matcher matcher = pattern.matcher(matchStr);
        return matcher.matches();
    }

    /**
     * 获取匹配字符串
     * @param matchStr  校验字符串
     * @param regStr    校验正则表达式
     * @return
     */
    public static String getMatcherStr(String matchStr,String regStr){
        List<String> matchList = getMatcherList(matchStr, regStr);
        if (matchList != null && matchList.size() > 0) {
            return matchList.get(0);
        }
        return "";
    }
    
    /**
     * 返回匹配的字符串数组
     * @param matchStr  校验字符串
     * @param regStr    校验正则表达式
     * @return
     */
    public static List<String> getMatcherList(String matchStr,String regStr){
        List<String> matchList = new ArrayList<>();
        if (StringUtils.isNotEmpty(matchStr) && StringUtils.isNotEmpty(regStr)) {
            Pattern pattern = Pattern.compile(regStr);
            Matcher matcher = pattern.matcher(matchStr);
            while (matcher.find()) {
                matchList.add(matcher.group(0));
            }
        }
        return matchList;
    }

    public static void main(String[] args) {
        String str = "/portal/huanchuang/WWW/phone/js/main.js";
        str = str.replaceAll("", "");
    }


}
