package com.fizz.poi.core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoiUtil {

    private static Workbook wb = null;

    /**
     * 读取Excel内容
     * @date 2017年8月14日
     * @author 张纯真
     * @param srcPath
     * @return
     * @throws IOException
     */
    public static Map<Integer, List<List<String>>> readExcelToList(String srcPath) throws IOException {
        //存放整个excel内容(key: sheet编号;value: sheet内容)
        Map<Integer, List<List<String>>> map_excel = new HashMap<Integer, List<List<String>>>();
        try {
            readExcelToWorkbook(srcPath);
            if (wb != null) {
                int size_sheet = wb.getNumberOfSheets();
                //遍历所有sheet
                for (int i=0; i<size_sheet; i++) {
                    List<List<String>> list_sheet = new ArrayList<List<String>>();
                    //获取sheet
                    Sheet sheet = wb.getSheetAt(i);
                    int excelLength = 0;
                    //遍历所有行
                    for (Row row : sheet) {
                        if (excelLength == 0) {
                            //获取列长
                            excelLength = row.getLastCellNum();
                        }
                        List<String > list = new ArrayList<String>();
                        for(int cellNum = 0; cellNum < excelLength; cellNum++) {
                            //获取单元格内容
                            Cell cell = row.getCell(cellNum);
                            if(cell!=null){
                                if(cell.getCellType() == 0){
                                    DecimalFormat df = new DecimalFormat("0");
                                    String whatYourWant = df.format(cell.getNumericCellValue());
                                    list.add(whatYourWant);
                                }else{
                                    list.add(cell.getStringCellValue().trim());
                                }
                            }else{
                                list.add("");
                            }
                        }
                        list_sheet.add(list);
                    }
                    map_excel.put(i, list_sheet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map_excel;
    }

    /**
     * 获取Workbook
     * @date 2017年8月14日
     * @author 张纯真
     * @param srcPath
     * @return
     * @throws Exception
     */
    public static Workbook readExcelToWorkbook(String srcPath) throws Exception {
        InputStream stream = new FileInputStream(srcPath);
        if (srcPath.endsWith(".xls")) {
            wb = new HSSFWorkbook(stream);
        }else if (srcPath.endsWith(".xlsx")) {
            wb = new XSSFWorkbook(stream);
        }else {
            System.out.println("您输入的excel格式不正确,src:" + srcPath);
        }
        return wb;
    }

    /**
     * 读取excel并修改内容后输出excel
     * @date 2017年8月14日
     * @author 张纯真
     * @param map_sheet_cell_data
     * @param path_in
     * @param path_out
     * @throws Exception
     */
    public static void readAndInsert(Map<Integer, List<Map<String, String>>> map_sheet_cell_data, String path_in, String path_out) throws Exception {
        if (path_in != null && !"".equals(path_in)) {
            wb = readExcelToWorkbook(path_in);
        }
        for (Integer key : map_sheet_cell_data.keySet()) {
            Sheet sheet = wb.getSheetAt(key);
            List<Map<String, String>> list_cell_data = map_sheet_cell_data.get(key);
            for (int i=0; i<list_cell_data.size(); i++) {
                Map<String, String> map_cell_data = list_cell_data.get(i);
                int row = Integer.parseInt(map_cell_data.get("row"));
                int column = Integer.parseInt(map_cell_data.get("column"));
                setCellStyle(sheet, row, column, Font.COLOR_RED);
                setCellValue(sheet, row, column, map_cell_data.get("value"));
            }
        }
        exportToExcel(path_out);
    }

    /**
     * 修改指定单元格内容
     * @date 2017年8月14日
     * @author 张纯真
     * @param sheet
     * @param num_row
     * @param num_column
     * @param value
     */
    public static void setCellValue(Sheet sheet, int num_row, int num_column, String value) {
        Row row = sheet.getRow(num_row);
        Cell cell = row.getCell(num_column);
        value = value.toString();
        if (!"".equals(value)) {
            Pattern p = Pattern.compile("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
            Matcher matcher = p.matcher(value);
            if (matcher.matches()) {
                if (value.length() < 9) {
                    //是数字当作double处理
                    cell.setCellValue(Double.parseDouble(value));
                } else {
                    cell.setCellValue(value);
                }
            } else {
//		    	HSSFRichTextString richString = new HSSFRichTextString(value);
//		        cell.setCellValue(richString);
                cell.setCellValue(value);
            }
        } else {
            cell.setCellValue(value);
        }
    }

    /**
     * 设置单元格样式
     * @date 2017年8月16日
     * @author 张纯真
     * @param sheet
     * @param num_row
     * @param num_column
     * @param color_font
     */
    public static void setCellStyle(Sheet sheet, int num_row, int num_column, short color_font) {
        Row row = sheet.getRow(num_row);
        if (row == null) {
            row = sheet.createRow(num_row);
        }
        Cell cell = row.getCell(num_column);
        if (cell == null) {
            cell = row.createCell(num_column);
        }
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        Font font = wb.createFont();
        font.setColor(color_font);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    /**
     * 设置行样式
     * @date 2017年8月16日
     * @author 张纯真
     * @param sheet
     * @param num_row
     * @param color_background
     */
    public static void setRowStyle(Sheet sheet, int num_row, short color_background) {
        Row row = sheet.getRow(num_row);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillBackgroundColor(color_background);
        row.setRowStyle(cellStyle);
    }

    /**
     * 导出excel(Workbook形式)
     * @date 2017年8月14日
     * @author 张纯真
     * @param path_out
     * @throws Exception
     */
    public static void exportToExcel(String path_out) throws Exception {
        int index = path_out.lastIndexOf("\\");
        if (index == -1) {
            index = path_out.lastIndexOf("/");
        }
        String path_out_base = path_out.substring(0, index);
        File file = new File(path_out_base);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fileOut = new FileOutputStream(path_out);
        wb.write(fileOut);
        fileOut.close();
    }

}
