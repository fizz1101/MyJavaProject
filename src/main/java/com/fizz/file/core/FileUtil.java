package com.fizz.file.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {

    /**
     * 读取文件内容
     * @date 2017年1月11日
     * @author 张纯真
     * @param filePath
     * @param encoding
     * @return
     */
    public static List<List<String>> readFile(String filePath, String separator, String encoding) {
        List<List<String>> list_txt = new ArrayList<List<String>>();
        BufferedReader bufferedReader = null;
        try {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    List<String> list_cell = new ArrayList<String>();
                    if (separator != null) {
                        String[] cellArr = lineTxt.split(separator);
                        for (int i=0; i<cellArr.length; i++) {
                            String content = cellArr[i];
                            if (content != null && !"".equals(content)) {
                                list_cell.add(cellArr[i].trim());
                            }
                        }
                    } else {
                        list_cell.add(lineTxt);
                    }
                    list_txt.add(list_cell);
                }
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list_txt;
    }

    /**
     * 写数据入文件
     * @date 2017年10月17日
     * @author 张纯真
     * @param filePath
     * @param fileName
     * @param content
     * @return
     */
    public static boolean writeFile(BufferedWriter writer, String filePath, String fileName, String content) {
        boolean flag = false;
        boolean flag_bw = false;
        try {
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(new File(filePath + fileName), true));
                flag_bw = true;
            }
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (flag_bw && writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 获取目录下的文件(根据文件名)
     * @date 2017年9月30日
     * @author 张纯真
     * @param path
     * @param fileNames
     * @param order 1:asc;2:desc
     * @return
     */
    public static List<File> getFileListByName(String path, List<String> fileNames, int order) {
        List<File> list_res = new ArrayList<File>();
        List<File> list_file = new ArrayList<File>();
        int size = fileNames.size();
        File file_path = new File(path);
        File[] files = file_path.listFiles();
        if (files != null) {
            int count = 0;
            for (File f : files) {
                if (f.isFile()) {
                    list_file.add(f);
                }
            }
            switch (order) {
                case 1:
                    Collections.sort(list_file, new Comparator<File>() {
                        public int compare(File o1, File o2) {
                            if (o1.isDirectory() && o2.isFile())
                                return -1;
                            if (o1.isFile() && o2.isDirectory())
                                return 1;
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    break;
                case 2:
                    Collections.sort(list_file, new Comparator<File>() {
                        public int compare(File o1, File o2) {
                            if (o1.isDirectory() && o2.isFile())
                                return -1;
                            if (o1.isFile() && o2.isDirectory())
                                return 1;
                            return o2.getName().compareTo(o1.getName());
                        }
                    });
                default:
                    break;
            }
            for (File file : list_file) {
                String fileName = file.getName();
                if (fileNames.contains(fileName)) {
                    list_res.add(file);
                    count++;
                }
                if (count == size) {
                    break;
                }
            }
        }
        return list_res;
    }

    /**
     * 获取目录下的文件并排序
     * @date 2017年1月13日
     * @author 张纯真
     * @param path
     * @param extName
     * @param order 0:不排序;1:升序;2:降序;
     * @return
     */
    public static List<File> getFileListSort(String path, String extName, int order, int limit) {
        List<File> list_file = new ArrayList<File>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(extName)) {
                    list_file.add(f);
                }
            }
        }
        switch (order) {
            case 1:
                Collections.sort(list_file, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case 2:
                Collections.sort(list_file, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                break;
            default:
                break;
        }
        if (limit > 0 && list_file.size()>limit) {
            list_file = list_file.subList(0, limit);
        }
        return list_file;
    }

    /**
     * 移动文件到目标目录
     * @date 2017年1月20日
     * @author 张纯真
     * @param file
     * @param destPath
     * @param newName
     * @return
     */
    public static boolean moveFile(File file, String destPath, String newName) {
        boolean flag = false;
        File folder = new File(destPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (newName!=null && !"".equals(newName)) {
            newName = file.getName();
        }
        File file_new = new File(destPath + newName);
        if (file.exists() && file.renameTo(file_new)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 删除该目录下的所有文件
     * @param filePath
     *            文件目录路径
     */
    public static void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 删除单个文件
     * @param filePath
     *         文件目录路径
     * @param fileName
     *         文件名称
     */
    public static void deleteFile(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 下载文件
     * @param response
     * @param csvFilePath
     *              文件路径
     * @param fileName
     *              文件名称
     * @throws IOException
     */
    /*public static void exportFile(HttpServletResponse response, String csvFilePath, String fileName) throws IOException {
        response.setContentType("application/csv;charset=GBK");
        response.setHeader("Content-Disposition",
            "attachment;  filename=" + new String(fileName.getBytes("GBK"), "ISO8859-1"));
        //URLEncoder.encode(fileName, "GBK")

        InputStream in = null;
        try {
            in = new FileInputStream(csvFilePath);
            int len = 0;
            byte[] buffer = new byte[1024];
            response.setCharacterEncoding("GBK");
            OutputStream out = response.getOutputStream();
            while ((len = in.read(buffer)) > 0) {
                //out.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
                out.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/

    /**
     * 关闭输入输出流
     * @param is
     * @param out
     */
    public static void close(InputStream is, OutputStream out) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
