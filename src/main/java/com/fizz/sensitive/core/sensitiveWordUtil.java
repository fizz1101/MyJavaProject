package com.fizz.sensitive.core;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class sensitiveWordUtil {

    private static JSONObject sensitiveWord = new JSONObject();

    /**
     * 初始化敏感词池
     * @return
     */
    public static JSONObject initWords(List<String> words) {
        for (String word : words) {
            if (StringUtils.isBlank(word) || word.length() < 2) {
                continue;
            }
            String w = String.valueOf(word.charAt(0));
            if (sensitiveWord.containsKey(w)) {
                sensitiveWord.put(w, split(word.substring(1), sensitiveWord.getJSONObject(w)));
            } else {
                split(word, sensitiveWord);
            }
        }
        return sensitiveWord;
    }

    private static JSONObject split(String word, JSONObject child) {
        String w = String.valueOf(word.charAt(0));
        JSONObject cell = new JSONObject();
        if (child.containsKey(w)) {
            if (word.length() == 1) {
                child.getJSONObject(w).put("end", 1);
            } else {
                child.put(w, split(word.substring(1), child.getJSONObject(w)));
            }
        } else {
            if (word.length() == 1) {
                cell.put("end", 1);
                child.put(w, cell);
            } else {
                cell.put("end", 0);
                child.put(w, cell);
                split(word.substring(1), child.getJSONObject(w));
            }
        }
        return child;
    }

    public static String check(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        str = str.replaceAll(" ", "");
        JSONObject sensitive = sensitiveWord;
        for (int i=0; i<str.length(); i++) {
            String w = String.valueOf(str.charAt(i));
            if (!sensitive.containsKey(w)) {
                continue;
            }
            sb.append(w);
            sensitive = sensitive.getJSONObject(w);
            for (int j=i+1; j<str.length(); j++) {
                w = String.valueOf(str.charAt(j));
                if (!sensitive.containsKey(w)) {
                    sb = new StringBuffer();
                    sensitive = sensitiveWord;
                    break;
                }
                sb.append(w);
                sensitive = sensitive.getJSONObject(w);
                if (1 == sensitive.getIntValue("end")) {
                    return sb.toString();
                }
            }
        }
        return "";
    }

    public static void main(String[] args) {
        List<String> words = new ArrayList<>();
        words.add("色情");
        words.add("黄色");
        words.add("黄赌毒");
        words.add("共产党");
        words.add("国民党");
        JSONObject res = initWords(words);
        System.out.println(res);

        String str = "的说法黄色啊胜多负少黄赌打法的就是反抗色情军活动空间发哈士大夫看见安徽罚款决定书会计核算的机会反馈回馈积分和凯撒的话发客户的开发哈萨克发很少看到合法化阿斯蒂芬获得丰厚说的话福克斯答复客户士大夫华盛顿或粉红色 返回佛挡杀佛";
        String word = check(str);
        System.out.println(word);


        // 初始化敏感词库对象
        SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
        // 从数据库中获取敏感词对象集合（调用的方法来自Dao层，此方法是service层的实现类）
//        List<SensitiveWord> sensitiveWords = sensitiveWordDao.getSensitiveWordListAll();
        // 构建敏感词库
        Map sensitiveWordMap = sensitiveWordInit.initKeyWord(words);
        // 传入SensitivewordEngine类中的敏感词库
        SensitivewordEngine.sensitiveWordMap = sensitiveWordMap;
        // 得到敏感词有哪些，传入2表示获取所有敏感词
        Set<String> set = SensitivewordEngine.getSensitiveWord(str, 2);
        System.out.println(set);

        HashMap map = new HashMap();
        map.put("a", 1);
        map.get("a");
        map.put(null, null);
        System.out.println(map.containsKey(null));
        System.out.println(map);

        ConcurrentHashMap m = new ConcurrentHashMap();
        m.put(null, 1);

        System.out.println("key".hashCode());
    }

}
