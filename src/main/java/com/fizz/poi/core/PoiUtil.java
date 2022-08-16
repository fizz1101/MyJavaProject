package com.fizz.poi.core;

import com.fizz.file.core.FileUtil;
import fr.opensagres.xdocreport.itext.extension.font.IFontProvider;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
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

    /**
     * 用一个docx文档作为模板，然后替换其中的内容，再写入目标文档中。
     * @throws Exception
     */
    public static void replaceByWordTemplate(String srcPath, String descPath, Map<String, Object> params) throws Exception {
        InputStream is = new FileInputStream(srcPath);
        XWPFDocument doc = new XWPFDocument(is);
        //替换段落里面的变量
        replaceInPara(doc, params);
        //替换表格里面的变量
        replaceInTable(doc, params);
        OutputStream os = new FileOutputStream(descPath);
        doc.write(os);

        FileUtil.close(is, os);
    }

    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;

        while (iterator.hasNext()) {
            para = iterator.next();
//            CTPPr pr = para.getCTP().getPPr();
            replaceInPara(para, params);
        }
    }

    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private static void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;
        Matcher matcher;
        if (matcher(para.getParagraphText()).find()) {
//            System.out.println("1:" + para.getParagraphText());
            runs = para.getRuns();
            System.out.println("run:"+runs);
            for (int i=0; i<runs.size(); i++) {
                XWPFRun run = runs.get(i);
                String runText = run.getText(run.getTextPosition());
                if (StringUtils.isEmpty(runText)) {
                    continue;
                }
                matcher = matcher(runText);
                if (matcher.find()) {
                    while ((matcher = matcher(runText)).find()) {
                        runText = matcher.replaceFirst(String.valueOf(params.get(matcher.group(1))));
                    }
                    //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                    //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。

//                    run.setText(runText,0);
                    int fontSize = run.getFontSize();
                    String fontFamily = run.getFontFamily();
                    para.removeRun(i);
                    para.insertNewRun(i).setText(runText);
                    para.insertNewRun(i).setFontSize(fontSize);
                    para.insertNewRun(i).setFontFamily(fontFamily);
                }
            }
        }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        List<XWPFTableRow> rows;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            for (XWPFTableRow row : rows) {
                cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    paras = cell.getParagraphs();
                    for (XWPFParagraph para : paras) {
                        replaceInPara(para, params);
                    }
                }
            }
        }
    }

    /**
     * 设置样式
     * @param doc
     * @param tempdoc
     * @param params
     */
    private  void setStyle(XWPFDocument doc,XWPFDocument tempdoc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        Iterator<XWPFParagraph> iterator2 = tempdoc.getParagraphsIterator();
        XWPFParagraph para ;
        XWPFParagraph para2;
        while (iterator.hasNext()) {
            para = iterator.next();
            para2 = iterator2.next();
            this.styleInPara(para,para2, params);
        }
    }
    private  void styleInPara(XWPFParagraph para, XWPFParagraph para2,Map<String, Object> params) {
        List<XWPFRun> runs;
        List<XWPFRun> runs2;
        Matcher matcher;
        if (matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            runs2 = para2.getRuns();
            for (int i=0; i<runs.size(); i++) {
                XWPFRun run = runs.get(i);
                XWPFRun run2 = runs2.get(i);
                String runText = run.toString();
                matcher = matcher(runText);
                if (matcher.find()) {
                    //按模板文件格式设置格式
                    run2.getCTR().setRPr(run.getCTR().getRPr());
                }
            }
        }
    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    private static Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public static void main(String[] args) throws Exception {
        String srcPath = "E:\\pdf\\template\\aaa.docx";
        String destPath = "E:\\pdf\\template\\aaaa.docx";
        Map<String, Object> params = new HashMap<>();
        params.put("title", "标题");
        params.put("t", "桂林");
        params.put("m", "2019年1月15日");
        replaceByWordTemplate(srcPath, destPath, params);
    }

}
