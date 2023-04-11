import com.fizz.encrypt.core.MD5Util;
import com.fizz.file.core.FileUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    private static Random random = new Random();

    private static String answer = "D:\\20220905\\answer.txt";
    private static String process = "D:\\20220905\\process.txt";

    private static Map<String, Integer> matchMap = new HashMap<>();
    private static List<Integer> numbers = new ArrayList<>();
    static {
        for (int i=1; i<10; i++) {
            numbers.add(i);
        }
        matchMap.put("A", 0);
        matchMap.put("B", 0);
    }

    public static void main(String[] args) throws IOException {
//        create();
        test("8729");
    }

    private static void create() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(answer));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 4; i++) {
                sb.append(numbers.remove(random.nextInt(numbers.size())));
            }
            writer.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void test(String number) throws IOException {
        List<List<String>> lists = FileUtil.readFile(answer, ";", "utf-8");
        String answer = lists.get(0).get(0);
        for (int i=0; i<number.length(); i++) {
            String c = String.valueOf(number.charAt(i));
            if (answer.contains(c)) {
                if (c.equals(String.valueOf(answer.charAt(i)))) {
                    matchMap.put("A", matchMap.get("A") + 1);
                } else {
                    matchMap.put("B", matchMap.get("B") + 1);
                }
            }
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(process, true));
            String match = matchMap.get("A") + "A" + matchMap.get("B") + "B";
            writer.write(number + "\t" + match + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        List<List<String>> matchs = FileUtil.readFile(process, ";", "utf-8");
        matchs.stream().forEach(e -> System.out.println(e.get(0)));
    }

}
