package com.fizz.file.core;

import com.fizz.file.core.FileUtil;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;

public class XdocreportUtil {

    /**
     * word转pdf
     * @throws IOException
     */
    public static void word2pdf(String docPath, String pdfPath) throws IOException {
        InputStream doc = new FileInputStream(docPath);
        XWPFDocument document = new XWPFDocument(doc);
        PdfOptions options = PdfOptions.create();
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
        OutputStream out = new FileOutputStream(pdfPath);
        PdfConverter.getInstance().convert(document, out, options);

        FileUtil.close(doc, out);
    }

    public static void main(String[] args) throws IOException {
        String docPath = "E:\\pdf\\template\\ggg.docx";
        String pdfPath = "E:\\pdf\\template\\ccc.pdf";
        word2pdf(docPath, pdfPath);
    }

}
