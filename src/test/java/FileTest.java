import com.fizz.encrypt.core.MD5Util;
import com.fizz.file.core.FileUtil;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileTest {

    public static String readFile = "D:\\manager.txt";
    public static String writeFile = "D:\\manager_code.txt";

    @Test
    public void createAuthCode() {
        List<List<String>> lists = FileUtil.readFile(readFile, ";", "utf-8");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(writeFile));
            for (List<String> content : lists) {
                String phone = content.get(0);
                String salt = phone.substring(phone.length()-4);
                String code = MD5Util.encrypt(phone, salt);
                code = code.substring(12, 20).toUpperCase();
                writer.write(phone + "," + code + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
