package com.fizz.file.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Cut {

    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException {
        String filePath = "E:\\user_auth_detail.txt";
        String destPath = "E:\\user_auth_detail_new.txt";
        writer = new BufferedWriter(new FileWriter(new File(destPath), true));
        List<List<String>> list_content = FileUtil.readFile(filePath, ";", "utf-8");
        for (List<String> list_cell : list_content) {
            String colmun2 = list_cell.get(1);
            System.out.println(list_cell.toString());
            String newLine;
            if (colmun2.indexOf(",") > 0) {
                colmun2 = colmun2.replaceAll("\"", "");
                String[] arr = colmun2.split(",");
                for (int i=0; i< arr.length; i++) {
                    list_cell.set(1, "\""+arr[i]+"\"");
                    //list_cell.add(0,"\"999999\"");
                    newLine = list_cell.toString();
                    newLine = newLine.substring(1, newLine.length()-1);
                    newLine = newLine.replaceAll(",", ";");
                    newLine += "\t\n";
                    FileUtil.writeFile(writer, "E:\\", null, newLine);
                }
            }
        }
    }

}
