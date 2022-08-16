package com.fizz.file.core;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XdocreportTemp {

    /**
     * 将word文档， 转换成pdf, 中间替换掉变量
     * @param srcPath 源word文档路径， 必须为docx文档
     * @param destPath 目标输出文件路径
     * @param params 需要替换的变量=
     * @throws Exception
     */
    public static void wordConverterToPdf(String srcPath, String destPath, Map<String, String> params) throws Exception {
        XWPFDocument doc = new XWPFDocument(new FileInputStream(srcPath));
        paragraphReplace(doc.getParagraphs(), params);
        tableReplace(doc.getTables(), params);
        /*PdfOptions options = PdfOptions.create();
        //中文字体处理
        options.fontProvider((familyName, encoding, size, style, color) -> {
            try {
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                Font fontChinese = new Font(bfChinese, size, style, color);
                if (familyName != null)
                    fontChinese.setFamily(familyName);
                return fontChinese;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
        OutputStream target = new FileOutputStream(destPath);
        PdfConverter.getInstance().convert(doc, target, options);*/
        OutputStream target = new FileOutputStream(destPath);
        doc.write(target);
    }

    /** 替换段落中内容 */
    private static void paragraphReplace(List<XWPFParagraph> paragraphs, Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            for (XWPFParagraph p : paragraphs) {
                List<XWPFRun> runs = p.getRuns();
                for (int i=0; i<runs.size(); i++) {
                    XWPFRun run = runs.get(i);
                    String runText = run.getText(run.getTextPosition());
                    if (runText == null) {
                        continue;
                    }
                    Matcher matcher = matcher(runText);
                    if (matcher.find()) {
                        while ((matcher = matcher(runText)).find()) {
                            String key = matcher.group(1);
                            if (params.containsKey(key)) {
                                runText = matcher.replaceFirst(String.valueOf(params.get(key)));
                            }
                        }
                        //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                        //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
//                        run.setText(runText,0);
                        int fontSize = run.getFontSize();
                        String fontFamily = run.getFontFamily();
                        p.removeRun(i);
                        p.insertNewRun(i).setText(runText);
                        p.getRuns().get(i).setFontFamily(fontFamily);
                        p.getRuns().get(i).setFontSize(fontSize);
//                        if(bold){
//                            p.getRuns().get(i).setBold(true);
//                        }
                    }
                }
            }
        }
    }

    /** 替换表格中内容 */
    private static void tableReplace(List<XWPFTable> tables, Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            for (XWPFTable table : tables) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        paragraphReplace(cell.getParagraphs(), params);
                    }
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
        Pattern pattern = Pattern.compile("\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    public static void main(String[] args) throws Exception {
        String srcPath = "E:\\pdf\\template\\fffff.docx";
        String destPath = "E:\\pdf\\template\\ddd.docx";
       /* Map<String, String> params = new HashMap<>();
        params.put("title", "标题");
        params.put("t", "桂林");
        params.put("m", "2019年1月15日");*/

        Map<String,String> replaceData = new HashMap<>();
//        replaceData.put("customerName","什么什么股份有限公司");
//        replaceData.put("t","什么省市区哪里哪里哪里");
//        replaceData.put("customerContact","张三");
//        replaceData.put("customerPhone","17855566632");
//        replaceData.put("businessName","联和通信什么什么哪里哪里股份有限公司");
//        replaceData.put("businessAddr","什么省市区哪里哪里哪里");
//        replaceData.put("businessContact","李铁锤");
//        replaceData.put("businessPhone","13755687895");
//        replaceData.put("totalPrice","130000");
//        replaceData.put("totalPriceChina","壹万叁仟元整");
//        replaceData.put("productContent","一个产品、两个产品、三个产品、第四种");
        replaceData.put("projectNo","SG202006095Yh89uyt");
        wordConverterToPdf(srcPath, destPath, replaceData);
    }


}
