package com.fizz.poi;

import com.fizz.poi.core.PoiUtil;

import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws Exception {
        String path_src = "E:/111.xlsx";
        Map<Integer, List<List<String>>> map_excel = PoiUtil.readExcelToList(path_src);
        List<List<String>> list_sheet_1 = map_excel.get(0);
        for (int i=0; i < list_sheet_1.size(); i++) {
            List<String> list_line = list_sheet_1.get(i);
            for (int j=0; j<list_line.size(); j++) {
                System.out.println("["+i+","+j+"]："+list_line.get(j));
            }
        }
    }

}
