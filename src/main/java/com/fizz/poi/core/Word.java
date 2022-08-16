package com.fizz.poi.core;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

/**
 *
 * @author unknown
 * @Date 2020年3月2日
 * @Time 下午1:41:31
 */
public class Word {
    /**
     * 占位符初始依赖
     */
    public static final String Symbol="$";
    /**
     * 验证主体
     */
    public static final Pattern findBody=Pattern.compile("(\\?\\{)([a-zA-Z0-9]+)(})".replace("?", Symbol));
    /**
     * 验证附属主体-后半段
     */
    public static final Pattern surplus=Pattern.compile("(\\{[a-zA-Z0-9]+}|[a-zA-Z0-9]+})");
    /**
     * 文档主体-占位符确定后的截取行为依赖
     */
    public static final Pattern keyInfo=Pattern.compile("[a-zA-Z0-9]+");

    /**
     *
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午1:42:17
     * @param fileName 模板数据
     * @param map 数据格式为Map<String,String>
     * @param empty 查找不到数据时用的占位
     * @param ignorecase 忽略大小写
     * @return
     * @throws IOException
     */
    public static final XWPFDocument searchAndReplace(String fileName,Map<String, Object> map, String empty,boolean ignorecase) throws IOException {
        return searchAndReplace(POIXMLDocument.openPackage(fileName),map,empty,ignorecase);
    }
    /**
     * poi 查找word中占位符并替换
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午1:41:40
     * @param oPCPackage 模板数据
     * @param map 数据格式为Map<String,String>
     * @param empty 查找不到数据时用的占位
     * @param ignorecase 忽略大小写
     * @return
     * @throws IOException
     */
    public static final XWPFDocument searchAndReplace(OPCPackage oPCPackage,Map<String, Object> map, String empty,boolean ignorecase) throws IOException {
        return searchAndReplace(new XWPFDocument(oPCPackage),map,empty,ignorecase);
    }
    /**
     * poi 查找word中占位符并替换
     * @author unknown
     * @Date 2020年2月28日
     * @Time 下午1:59:19
     * @param Document 模板数据
     * @param map 数据格式为Map<String,String>
     * @param empty 查找不到数据时用的占位
     * @param ignorecase 忽略大小写
     * @return XWPFDocument
     * @throws IOException
     */
    public static final XWPFDocument searchAndReplace(XWPFDocument Document,Map<String, Object> map, String empty,boolean ignorecase) throws IOException {
        //对单行中存在多个的进行再次捕捉，表格自己优化，暂时没有贴别的解决方案
        //处理大小写,使其忽略大小写,精度可能降低,全部转小写
        if(ignorecase){
            //找到所有的key
            Map<String, Object> TmpMap=new HashMap<String,Object>();
            //全部替换
            for (String key : map.keySet()) {
                //重新复制并将其转为小写
                TmpMap.put(key.toLowerCase(), map.get(key));
            }
            //重新赋值
            map=TmpMap;
        }
        Word.ParagraphSearchAndReplace(Document.getParagraphsIterator(),map,empty,ignorecase);
        Word.TableSearchAndReplace(Document.getTablesIterator(), map, empty, ignorecase);
        return Document;
    }
    /**
     * poi 查找word表格中占位符并替换
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午2:01:47
     * @param itTable
     * @param map
     * @param empty
     * @param ignorecase
     */
    public static final void TableSearchAndReplace(Iterator<XWPFTable> itTable,Map<String, Object> map, String empty,boolean ignorecase) {
        while (itTable.hasNext()) {
            XWPFTable table = itTable.next();
            int count = table.getNumberOfRows();
            for (int i = 0; i < count; i++) {
                XWPFTableRow row = table.getRow(i);
                List<XWPFTableCell> cells = row.getTableCells();
                for (XWPFTableCell cell : cells) {
                    List<XWPFParagraph> Paragraph= cell.getParagraphs();
                    if(Paragraph.size()>1) {
                        Word.ParagraphSearchAndReplace(Paragraph,map,empty,ignorecase);
                    }else {
                        String nowText=cell.getText();
                        // 第一次查询
                        Matcher DivFind = findBody.matcher(nowText);

                        while(DivFind.find()){
                            String key=new StringBuffer(nowText).substring(DivFind.start(2),DivFind.end(2)).toString();
                            // 忽略大小写
                            if(ignorecase){
                                //全部置为小写
                                key=key.toLowerCase();
                            }
                            //删除表格内容(改到代码内部,不进行数据替换时不删除)
                            //cell.removeParagraph(0);
//                            System.out.println(key);
//                            if(key.contains("sampledate")) {
//                                System.out.println("");
//                            }
                            if(key!=null && map.containsKey(key)){
                                //获取数据
                                Object _value= map.get(key);

                                String ClassName= _value.getClass().getName().toLowerCase();
                                //System.out.println(ClassName);
                                if(ClassName.equals("java.lang.string")) {
                                    //数据转String
                                    String value=_value!=null?_value.toString():empty;
                                    //直接替换占位符开始的部位
                                    nowText=new StringBuffer(nowText).replace(DivFind.start(), DivFind.end(), value).toString();

                                    if(!DivFind.find()) {
                                        //删除表格内容
                                        cell.removeParagraph(0);
                                        //新建段落
                                        XWPFParagraph pIO = cell.addParagraph();
                                        //新建字体开始
                                        XWPFRun rIO = pIO.createRun();
                                        rIO.setBold(false);
                                        rIO.setFontFamily("仿宋_GB2312");
                                        rIO.setFontSize(11);
                                        rIO.setText(nowText);
                                    }else {
                                        DivFind=findBody.matcher(nowText);
                                    }


                                }else if(ClassName.equals("com.fizz.poi.core.word$wordtable")) {
                                    //删除表格内容
                                    cell.removeParagraph(0);

                                    WordTable tableData=(WordTable)_value;
                                    XWPFParagraph cellPara = cell.addParagraph();
                                    XWPFTable cell_table= cell.insertNewTbl(cellPara.getCTP().newCursor());
                                    Word.createTable(cell_table, tableData.data, tableData.head,tableData.width,false);
                                }

                            }else{
                                nowText=new StringBuffer(nowText).replace(DivFind.start(), DivFind.end(), empty).toString();
                                //删除表格内容
                                cell.removeParagraph(0);
                                //新建段落
                                XWPFParagraph pIO = cell.addParagraph();
                                //新建字体开始
                                XWPFRun rIO = pIO.createRun();
                                rIO.setBold(false);
                                rIO.setFontFamily("仿宋_GB2312");
                                rIO.setFontSize(11);
                                rIO.setText(nowText);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * poi 查找word表格中占位符并替换
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午2:39:10
     * @param tables
     * @param map
     * @param empty
     * @param ignorecase
     */
    public static void TableSearchAndReplace(List<XWPFTable> tables, Map<String, Object> map, String empty,boolean ignorecase) {
        Word.TableSearchAndReplace(tables.iterator(), map, empty, ignorecase);
    }
    /**
     * poi 查找word段落中占位符并替换
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午2:02:18
     * @param itPara
     * @param map
     * @param empty
     * @param ignorecase
     */
    public static final void ParagraphSearchAndReplace(Iterator<XWPFParagraph> itPara,Map<String, Object> map, String empty,boolean ignorecase) {
        //循环文本主体,文本支值循环一遍
        while (itPara.hasNext()) {
            // 获取一行文本结构主体
            XWPFParagraph tmpBody = itPara.next();
            // 获取一行的文本结构数组
            List<XWPFRun> run = tmpBody.getRuns();
            // 循环文本(每一次循环确定一个占位符)
            for (int runIndex = 0; runIndex < run.size(); runIndex++) {
                // 获取到占位符文本深度(即文本从左到右的非可用长度),避免处理过多的文本
                int runDepth=0;
                //记录总共深度(即在for里边用了几个runIndex)
                int j=0;
                //最终确认的文本位置
                int findIndex=runIndex;
                //数据最终的key
                String DivText=null;
                //获取文本节点的第一个
                String NowRunText = run.get(runIndex).getText(run.get(runIndex).getTextPosition());
                if(NowRunText==null){
                    continue;
                }
                //第一次查找
                Matcher DivFind = findBody.matcher(NowRunText);
                //全文本节点
                String AllRunText = NowRunText;
                //查找到符号位置
                if(NowRunText.contains(Symbol)){
                    //
                    j=runIndex;
                    //一直循环知道处理完成,直到所有文本全部找到
                    while(!DivFind.find()){
                        //继续深度处理,记录处理深度
                        runDepth++;
                        j++;
                        //当前文本
                        String NewRunText=run.get(j).getText(run.get(j).getTextPosition());
                        //存在文本
                        if(NewRunText!=null){
                            //拼接全部文本
                            AllRunText+=NewRunText;
                        }
                        //查找到符号位置(原位优化程序用的,但是在处理上存在问题所以注释掉)
                        //if(NewRunText.contains(Symbol)){
                        //重置第一个确认字
                        //NowRunText=NewRunText;
                        //重置第一个确认位
                        //findIndex=runDepth;
                        //}
                        //继续深度获取文本
                        DivFind=findBody.matcher(AllRunText);
                    }
                    //重置查找,避免过多运行find找不到参数
                    DivFind=findBody.matcher(AllRunText);
                    //只处理占位符位置,可能存在其他文本,所以使用find
                    if(DivFind.find()){//之查找一个多余的不动
                        //直接拉取字符
                        DivText=new StringBuffer(AllRunText).substring(DivFind.start(2),DivFind.end(2)).toString();
                        // 忽略大小写
                        if(ignorecase){
                            //全部置为小写
                            DivText=DivText.toLowerCase();
                        }
//                        if(DivText.contains("sampledate")) {
//                            System.out.println("");
//                        }
                        //判断是否存在文本,是否存在数据
                        if(DivText!=null && map.containsKey(DivText)){
                            //获取数据
                            Object _value= map.get(DivText);
                            //数据转String
                            String value=_value!=null?_value.toString():empty;
                            //直接替换占位符开始的部位
                            String rText=new StringBuffer(NowRunText).replace(DivFind.start(), DivFind.end(), value).toString();
                            //对单行中存在多个的进行再次捕捉,正则
                            Matcher mg = findBody.matcher(rText);
                            //查找新的占位符
                            while(mg.find()){
                                //查找新的key
                                String newKey=new StringBuffer(rText).substring(mg.start(2), mg.end(2)).toString();
                                //忽略大小写
                                if(ignorecase){
                                    //全部置为小写
                                    newKey=newKey.toLowerCase();
                                }
                                //查找新的数据
                                if(newKey!=null && map.containsKey(newKey)){
                                    //获取新的数据
                                    Object _value1= map.get(newKey);
                                    //数据转String
                                    String value1=_value1!=null?_value1.toString():empty;
                                    //覆盖赋值
                                    rText=new StringBuffer(rText).replace(mg.start(), mg.end(), value1).toString();
                                }
                                //替换word文本
                                mg = findBody.matcher(rText);
                            }
                            //将文本置换
                            run.get(findIndex).setText(rText,0);
                        }else{
                            //查找不到为空
                            run.get(findIndex).setText(empty,0);
                        }
                        //清空剩余项(对深度处理的数据进行清理)
                        int g=runIndex;
                        //对深度处理的数据进行清除占位符
                        for (int i = 0; i < runDepth; i++) {
                            g++;
                            //获取要清理的文本
                            String ClearText =run.get(g).getText(run.get(g).getTextPosition());
                            //获取要清理的正则规则
                            Matcher ClearFind=surplus.matcher(ClearText);
                            Matcher ClearKey=keyInfo.matcher(ClearText);
                            //寻找与规则相同的文本
                            if(ClearFind.find()){
                                //清空规则内的信息(只清除第一个存在的规则文本)
                                ClearText=new StringBuffer(ClearText).replace(ClearFind.start(),ClearFind.end(), "").toString();
                            }
                            //完整规则,不进行查找,确保值只是英文以及数字
                            else if(ClearKey.matches()){
                                ClearText=new StringBuffer(ClearText).replace(ClearKey.start(),ClearKey.end(), "").toString();
                            }
                            //不存在的直接删除文本开始的第一个字符
                            else{
                                ClearText=ClearText.substring(1);
                            }
                            //重新赋值
                            run.get(g).setText(ClearText,0);

                            //如果文本中存在占位符头,将深度减去1在次使用
                            if(ClearText.contains(Symbol)){
                                j--;
                            }
                        }
                        //跳过已经使用的深度循环
                        runIndex=j;
                    }
                }
            }
        }
    }
    /**
     *
     * @author unknown
     * @Date 2020年3月2日
     * @Time 下午3:51:17
     * @param paragraph
     * @param map
     * @param empty
     * @param ignorecase
     */
    public static void ParagraphSearchAndReplace(List<XWPFParagraph> paragraph, Map<String, Object> map, String empty,boolean ignorecase) {
        Word.ParagraphSearchAndReplace(paragraph.iterator(),map,empty,ignorecase);
    }
    /**
     * 创建简单表格
     * @author unknown
     * @Date 2020年2月28日
     * @Time 下午4:44:03
     * @param XWPtable 单元格或者文档
     * @param table 表格数据
     * @param head 头
     * @return
     */
    public static final void createTable(XWPFTable XWPtable,List<Map<String,Object>> table,Map<String,String> head,List<Integer> width,boolean isFound) {
        //头
        Iterator<String> tmp_head=table.get(0).keySet().iterator();
        //表格行数
        int rowNum = table.size();
        //表格列数
        int colNum = table.get(0).keySet().size();
        //表格占用行数
        int headNum = head!=null?1:0;
        //表格数据开始行数
        int i = headNum;
        //表格总行数
        rowNum += headNum;
        if(headNum==1) {
            int tmpcolNum=head.keySet().size();
            colNum=colNum>=tmpcolNum?colNum:tmpcolNum;
        }

        if(!isFound){
            for (int j = 0; j < rowNum; j++) {
                XWPFTableRow row= XWPtable.insertNewTableRow(j);
                for (int k = 0; k < colNum; k++) {
                    row.createCell();
                }
            }

        }

        if(XWPtable!=null) {
            //如果表格存在头
            if(headNum==1) {
                //更改新的头
                tmp_head=head.keySet().iterator();
                //列计数
                int j=0;
                //循环头
                while(tmp_head.hasNext()) {
                    //添加头
                    XWPtable.getRow(0).getCell(j).setText(head.get(tmp_head.next()).toString());

                    if(width!=null && width.size()>0) {
                        CTTcPr tcpr =  XWPtable.getRow(0).getCell(j).getCTTc().addNewTcPr();
                        CTTblWidth cellw = tcpr.addNewTcW();
                        cellw.setType(STTblWidth.DXA);
                        cellw.setW(BigInteger.valueOf(width.get(j<width.size()?j:width.size()-1)));
                    }

                    //列计数+1
                    j+=1;
                }
            }
            //数据添加
            for (;i< table.size()+headNum; i++) {
                tmp_head=headNum==1? head.keySet().iterator():table.get(0).keySet().iterator();
                //获取每行数据
                Map<String,Object> data=table.get(i-headNum);
                //列计数
                int j=0;
                //循环
                while(tmp_head.hasNext()) {
                    //添加数据
                    String key= tmp_head.next();
                    if(data.containsKey(key)) {
                        XWPtable.getRow(i).getCell(j).setText(data.get(key).toString());
                    }

                    if(width!=null && width.size()>0) {
                        CTTcPr tcpr =  XWPtable.getRow(i).getCell(j).getCTTc().addNewTcPr();
                        CTTblWidth cellw = tcpr.addNewTcW();
                        cellw.setType(STTblWidth.DXA);
                        cellw.setW(BigInteger.valueOf(width.get(j<width.size()?j:width.size()-1)));
                    }

                    //列计数+1
                    j+=1;
                }
            }
        }
    }
    /**
     *新的表格对象（面向数据），简单表格（目前只能在表格中使用暂时无法定位段落）
     * @author unknown
     * @Date 2020年3月2日
     * @Time 上午10:40:08
     */
    public static class WordTable {

        private List<Map<String,Object>> data=new ArrayList<>();
        private Map<String,String> head=new HashMap<>();
        private List<Integer> width=new ArrayList<>();

        public void addWidth(int w) {
            this.width.add(w);
        }
        public void setWidth(List<Integer> w) {
            this.width=w;
        }

        public void setData(List<Map<String, Object>> data) {
            this.data = data;
        }

        public void setHead(Map<String, String> head) {
            this.head = head;
        }

    }

    public static void main(String[] args) throws IOException {
        String srcPath = "E:\\pdf\\template\\qqq.docx";

        //这里应该不用多做解释了吧，就是从dao层中查询数据
        Map<String,Object> data= new HashMap<>();
        data.put("title", "标题");
        data.put("aaa", "aaa");
        data.put("bbb", "bbb");
        data.put("ccc", "ccc");
        //这是在上边的Word类里边的静态类，这个位置的作用就是绘画表格
        WordTable table = new WordTable();
        //定义表格的表头，当然也可以不要，默认就是数据的字段
        table.setHead(new LinkedHashMap<String, String>(){
            private static final long serialVersionUID = 1L;
            {
                this.put("sampleName", "样品名称");
                this.put("appllyNo", "原始编号");
                this.put("testProject", "项目名称");
                this.put("result", "检测结果");
                this.put("resultUnit", "结果单位");
                this.put("resultIsEligible", "结果判定");
            }
        });
        //塞入数据这里的格式为List<Map<String,Object>>
        //构造假表格数据
        List<Map<String, Object>> tableData = new ArrayList<>();
        Map<String, Object> cellData = new LinkedHashMap<>();
        cellData.put("1", "1");
        cellData.put("2", "1");
        cellData.put("3", "1");
        cellData.put("4", "1");
        cellData.put("5", "1");
        cellData.put("6", "1");
        Map<String, Object> cellData2 = new LinkedHashMap<>();
        cellData2.put("1", "2");
        cellData2.put("2", "2");
        cellData2.put("3", "2");
        cellData2.put("4", "2");
        cellData2.put("5", "2");
        cellData2.put("6", "2");
        Map<String, Object> cellData3 = new LinkedHashMap<>();
        cellData3.put("1", "3");
        cellData3.put("2", "3");
        cellData3.put("3", "3");
        cellData3.put("4", "3");
        cellData3.put("5", "3");
        cellData3.put("6", "3");
        tableData.add(cellData);
        tableData.add(cellData2);
        tableData.add(cellData3);
        table.setData(tableData);
        //这里设置列表的宽度，其实这里是一个List，setWidth（list）,
        table.addWidth(356*5);
        table.addWidth(356*8);
        table.addWidth(356*5);
        table.addWidth(356*4+80);
        table.addWidth(356*3);
        table.addWidth(356*3);
        //把整个对象放进数据里
        data.put("listData",table);
        //打开表格并替换内容
        XWPFDocument Document=  Word.searchAndReplace(srcPath, data, "", true);
        //定义页脚的数据
        Map<String,Object> footdata= new HashMap<>();

        footdata.put("nowusername", "fizz");
        footdata.put("nowdate", new Date());
        //获取文档页脚
        List<XWPFFooter> pageFooters = Document.getFooterList();
        for (int i = 0; i < pageFooters.size(); i++) {
            //获取表格列表并替换占位符
            Word.TableSearchAndReplace(pageFooters.get(i).getTables(), footdata,"", true);
        }
    }

}
