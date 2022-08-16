package com.fizz.file.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XDocUtil
{
    public static String DEFAULT_URL = "http://www.xdocin.com";

    public static String DEFAULT_KEY = "";
    private String url;
    private String key;
    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    private static byte[] codes = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            codes[i] = -1;
        }
        for (int i = 65; i <= 90; i++) {
            codes[i] = ((byte)(i - 65));
        }
        for (int i = 97; i <= 122; i++) {
            codes[i] = ((byte)(26 + i - 97));
        }
        for (int i = 48; i <= 57; i++) {
            codes[i] = ((byte)(52 + i - 48));
        }
        codes[43] = 62;
        codes[47] = 63;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public XDocUtil() {
        this(DEFAULT_URL, DEFAULT_KEY);
    }

    public XDocUtil(String url) {
        this(url, DEFAULT_KEY);
    }

    public XDocUtil(String url, String key) {
        this.url = url;
        this.key = key;
    }

    public void to(File xdoc, File file) throws IOException {
        to(xdoc.getAbsolutePath(), file);
    }

    public void to(String xdoc, File file) throws IOException {
        to(xdoc, new FileOutputStream(file), getFormat(file.getName()));
    }

    public void to(String xdoc, Object out, String format) throws IOException {
        Map param = new HashMap();
        param.put("_func", "to");
        param.put("_xdoc", xdoc);
        param.put("_format", format);
        invoke(checkParam(param), out);
    }

    public String to(String xdoc, String to, String format) throws IOException {
        Map param = new HashMap();
        param.put("_func", "to");
        param.put("_xdoc", xdoc);
        param.put("_to", to);
        param.put("_format", format);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(checkParam(param), out);
        return new String(out.toByteArray(), "UTF-8");
    }

    public void run(File xdoc, Map<String, Object> param, File file) throws IOException {
        if (!param.containsKey("_xformat")) {
            param.put("_xformat", getFormat(file.getName()));
        }
        run(xdoc.getAbsolutePath(), param, file);
    }

    public void run(String xdoc, Map<String, Object> param, File file) throws IOException {
        run(xdoc, param, new FileOutputStream(file), getFormat(file.getName()));
    }

    public void run(String xdoc, Map<String, Object> param, Object out, String format) throws IOException {
        param.put("_func", "run");
        param.put("_xdoc", xdoc);
        param.put("_format", format);
        invoke(checkParam(param), out);
    }

    public String run(String xdoc, Map<String, Object> param, String to, String format) throws IOException {
        param.put("_func", "run");
        param.put("_xdoc", xdoc);
        param.put("_to", to);
        param.put("_format", format);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(checkParam(param), out);
        return new String(out.toByteArray(), "UTF-8");
    }

    public void run(Object obj, File file) throws IOException {
        run(obj, new FileOutputStream(file), getFormat(file.getName()));
    }

    public void run(Object obj, Object out, String format) throws IOException {
        run(obj, out, null, format);
    }

    public void run(Object obj, String to, String format) throws IOException {
        run(obj, null, to, format);
    }
    private void run(Object obj, Object out, String to, String format) throws IOException {
        String xurl = "";
        XDoc xdoc = (XDoc)obj.getClass().getAnnotation(XDoc.class);
        if (xdoc != null) {
            xurl = xdoc.value();
        }
        if (xurl.length() == 0) {
            xurl = "./" + obj.getClass().getSimpleName() + ".xdoc";
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        boolean hasXParam = false;

        Map param = new HashMap();

        for (Field field : fields) {
            XParam xParam = (XParam)field.getAnnotation(XParam.class);
            if (xParam != null) {
                hasXParam = true;
                String name = xParam.value();
                if (name.length() == 0)
                    name = field.getName();
                try
                {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (name.equals("_xdoc"))
                        xurl = String.valueOf(value);
                    else
                        param.put(name, value);
                }
                catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
        if (!hasXParam) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    param.put(field.getName(), field.get(obj));
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
        if (out != null)
            run(xurl, param, out, format);
        else
            run(xurl, param, to, format);
    }

    public boolean hi() throws IOException {
        return invokeStringFunc("hi").equals("ok");
    }

    public String about() throws IOException {
        return invokeStringFunc("about");
    }

    public String dkey() throws IOException {
        return invokeStringFunc("dkey");
    }

    public String ckey() throws IOException {
        return invokeStringFunc("ckey");
    }

    public String reg(String mail) throws IOException {
        Map params = new HashMap();
        params.put("_func", "reg");
        params.put("_mail", mail);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (String)parse(out.toByteArray());
    }

    public Map<String, String> acc() throws IOException {
        Map params = new HashMap();
        params.put("_func", "acc");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (Map)parse(out.toByteArray());
    }

    public void sup(String id, File file) throws IOException {
        sup(id, toDataURI(file.getAbsolutePath()));
    }

    public void sup(String id, InputStream in) throws IOException {
        sup(id, toDataURI(in));
    }
    private void sup(String id, String dataUri) throws IOException {
        Map params = new HashMap();
        params.put("_func", "sup");
        params.put("_id", id);
        params.put("_data", dataUri);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        parse(out.toByteArray());
    }

    public void sdown(String id, File file) throws IOException {
        sdown(id, new FileOutputStream(file));
    }

    public void sdown(String id, Object out) throws IOException {
        Map params = new HashMap();
        params.put("_func", "sdown");
        params.put("_id", id);
        invoke(params, out);
    }

    public boolean sremove(String id) throws IOException {
        Map params = new HashMap();
        params.put("_func", "sremove");
        params.put("_id", id);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return parse(out.toByteArray()).equals("ok");
    }

    public boolean mkdir(String dir) throws IOException {
        Map params = new HashMap();
        params.put("_func", "mkdir");
        params.put("_dir", dir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return parse(out.toByteArray()).equals("ok");
    }

    public List<Map<String, String>> dirlist(String dir) throws IOException {
        Map params = new HashMap();
        params.put("_func", "dirlist");
        params.put("_dir", dir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (List)parse(out.toByteArray());
    }

    public List<Map<String, String>> filelist(String dir) throws IOException {
        Map params = new HashMap();
        params.put("_func", "filelist");
        params.put("_dir", dir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (List)parse(out.toByteArray());
    }

    public void up(String dir, File file) throws IOException {
        up(dir, toDataURI(file.getAbsolutePath()));
    }

    public void up(String dir, InputStream in) throws IOException {
        up(dir, toDataURI(in));
    }

    private void up(String dir, String dataUri) throws IOException {
        Map params = new HashMap();
        params.put("_func", "up");
        params.put("_dir", dir);
        params.put("_data", dataUri);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        parse(out.toByteArray());
    }

    public void down(String dir, File file)
            throws IOException
    {
        down(dir, new FileOutputStream(file));
    }

    public void down(String dir, Object out)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "down");
        params.put("_dir", dir);
        invoke(params, out);
    }

    public boolean remove(String dir)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "remove");
        params.put("_dir", dir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return parse(out.toByteArray()).equals("ok");
    }

    public boolean exists(String dir)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "exists");
        params.put("_dir", dir);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return parse(out.toByteArray()).equals("true");
    }

    public List<Map<String, String>> xquery(String xdoc, String keyword)
            throws IOException
    {
        return xquery(xdoc, keyword, -1, -1);
    }

    public List<Map<String, String>> xquery(String xdoc, String keyword, int offset, int rows)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "xquery");
        params.put("_xdoc", xdoc);
        params.put("_keyword", keyword);
        params.put("_offset", String.valueOf(offset));
        params.put("_rows", String.valueOf(rows));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (List)parse(out.toByteArray());
    }

    public String xdataById(String id, String format)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "xdata");
        params.put("_id", id);
        params.put("_format", format);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (String)parse(out.toByteArray());
    }

    public String xdata(String xdata, String format)
            throws IOException
    {
        Map params = new HashMap();
        params.put("_func", "xdata");
        params.put("_xdata", xdata);
        params.put("_format", format);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (String)parse(out.toByteArray());
    }

    public static void main(String[] args)
    {
        if ((args.length > 0) && (args[0].length() > 0)) {
            String url = args[0];
            if (url.charAt(0) == '@') {
                File cmdFile = new File(url.substring(1));
                try {
                    FileReader reader = new FileReader(cmdFile);
                    url = new BufferedReader(reader).readLine();
                    reader.close();
                    cmdFile.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            String server = DEFAULT_URL;
            int pos = url.indexOf('?');
            if (pos > 0) {
                server = url.substring(0, pos);
                if (server.endsWith("/xdoc")) {
                    server = server.substring(0, server.length() - 5);
                }
                url = url.substring(pos + 1);
            }
            String xkey = "";
            try {
                String[] params = url.split("&");
                Map map = new HashMap();

                String to = null;
                for (int i = 0; i < params.length; i++) {
                    pos = params[i].indexOf('=');
                    if (pos > 0) {
                        String key = decode(params[i].substring(0, pos));
                        String value = decode(params[i].substring(pos + 1));
                        if (isXDocData(key, value)) {
                            value = toDataURI(value);
                        } else if (key.indexOf("@file") > 0) {
                            key = key.substring(0, key.length() - 5);
                            value = toDataURI(value); } else {
                            if (key.equals("_key")) {
                                xkey = value;
                                continue;
                            }if ((key.equals("_to")) && (isFile(value))) {
                                to = value;
                                continue;
                            }
                        }
                        map.put(key, value);
                    }
                }
                if ((!map.containsKey("_format")) && (to != null) && (to.indexOf('.') > 0)) {
                    map.put("_format", to.substring(to.lastIndexOf('.') + 1));
                }
                XDocUtil client = new XDocUtil(server, xkey);
                OutputStream out;
                if (to != null)
                    out = new FileOutputStream(to);
                else {
                    out = System.out;
                }
                client.invoke(map, out);
                if (to != null) {
                    out.flush();
                    out.close();
                    System.out.println(">> " + to);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void invoke(Map<String, String> param, Object out) throws IOException { String xurl = this.url + (this.url.endsWith("/") ? "xdoc" : "/xdoc");
        HttpURLConnection httpConn = (HttpURLConnection)new URL(xurl).openConnection();
        httpConn.setDoOutput(true);
        OutputStream reqOut = httpConn.getOutputStream();
        reqOut.write("&_key=".getBytes());
        reqOut.write(encode(this.key).getBytes());
        Iterator it = param.keySet().iterator();

        while (it.hasNext()) {
            String key = (String)it.next();
            reqOut.write(("&" + encode(key) + "=").getBytes());
            reqOut.write(encode(param.get(key)).getBytes());
        }
        reqOut.flush();
        reqOut.close();
        OutputStream os = null;
        if ((out instanceof OutputStream))
            os = (OutputStream)out;
        else {
            try {
                Method method = out.getClass().getMethod("getOutputStream", new Class[0]);
                os = (OutputStream)method.invoke(out, new Object[0]);
                method = out.getClass().getMethod("setHeader", new Class[] { String.class, String.class });
                String[] headerNames = { "Content-Type", "Content-Disposition" };

                for (String headerName : headerNames) {
                    String headerValue = httpConn.getHeaderField(headerName);
                    if (headerValue != null)
                        method.invoke(out, new Object[] { headerName, headerValue });
                }
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        pipe(httpConn.getInputStream(), os); }

    private Object parse(byte[] data) throws IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(data));
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            if (root.getAttribute("success").equals("true")) {
                Element result = (Element)root.getElementsByTagName("result").item(0);
                String dataType = result.getAttribute("dataType");
                if (dataType.equals("string"))
                    return result.getTextContent();
                if (dataType.equals("map")) {
                    NodeList items = result.getElementsByTagName("value");
                    Map map = new HashMap();

                    for (int i = 0; i < items.getLength(); i++) {
                        Element value = (Element)items.item(i);
                        NamedNodeMap atts = value.getAttributes();
                        for (int j = 0; j < atts.getLength(); j++) {
                            map.put(atts.item(j).getNodeName(), atts.item(j).getNodeValue());
                        }
                    }
                    return map;
                }if (dataType.equals("rowset")) {
                    Map fieldMap = new HashMap();
                    String[] fields = result.getAttribute("fields").split(",");
                    String[] formerFields = fields;
                    if (result.hasAttribute("formerFields")) {
                        formerFields = csvSplit(result.getAttribute("formerFields"));
                    }
                    for (int j = 0; j < formerFields.length; j++) {
                        fieldMap.put(fields[j], formerFields[j]);
                    }
                    NodeList eleList = result.getElementsByTagName("row");

                    List List = new ArrayList();
                    for (int i = 0; i < eleList.getLength(); i++) {
                        Element ele = (Element)eleList.item(i);
                        Map map = new HashMap();
                        List.add(map);
                        for (int j = 0; j < fields.length; j++) {
                            map.put(formerFields[j], ele.getAttribute(fields[j]));
                        }
                    }
                    return List;
                }
                return "";
            }

            throw new IOException(root.getElementsByTagName("error").item(0).getTextContent());
        }
        catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    private String invokeStringFunc(String func) throws IOException { Map params = new HashMap();
        params.put("_func", func);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        invoke(params, out);
        return (String)parse(out.toByteArray()); }

    private Map<String, String> checkParam(Map<String, Object> param) throws IOException {
        Map map = new HashMap();

        Iterator it = param.keySet().iterator();
        while (it.hasNext()) {
            String key = (String)it.next();
            String value = toParamString(param.get(key));
            if (isXDocData(key, value)) {
                value = toDataURI(value);
            } else if (key.endsWith("@file")) {
                key = key.substring(0, key.length() - 5);
                value = toDataURI(value);
            }
            map.put(key, value);
        }
        return map;
    }

    private static String toParamString(Object obj)
            throws IOException
    {
        String str;
        if (obj == null) {
            str = "";
        }
        else
        {
            if ((obj.getClass().isPrimitive()) ||
                    ((obj instanceof Boolean)) ||
                    ((obj instanceof Number)) ||
                    ((obj instanceof CharSequence))) {
                str = obj.toString();
            }
            else
            {
                if ((obj instanceof Date)) {
                    str = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)obj);
                }
                else
                {
                    if ((obj instanceof File)) {
                        str = toDataURI(((File)obj).getAbsolutePath());
                    }
                    else
                    {
                        if ((obj instanceof InputStream)) {
                            str = toDataURI((InputStream)obj);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            Set chainSet = new HashSet();
                            writeParamString(sb, obj, chainSet);
                            str = sb.toString();
                        }
                    }
                }
            }
        }
        return str;
    }
    private static void writeParamString(StringBuilder sb, Object obj, Set<Object> set) throws IOException {
        if (obj == null) {
            sb.append("null");
        } else if ((obj.getClass().isPrimitive()) ||
                ((obj instanceof Boolean)) ||
                ((obj instanceof Number))) {
            sb.append(toParamString(obj));
        } else if (((obj instanceof CharSequence)) ||
                ((obj instanceof Date))) {
            jencode(toParamString(obj), sb);
        } else if ((obj instanceof Collection)) {
            sb.append("[");
            boolean b = false;
            Iterator it = ((Collection)obj).iterator();
            while (it.hasNext()) {
                if (b) sb.append(",");
                writeParamString(sb, it.next(), set);
                b = true;
            }
            sb.append("]");
        } else if (obj.getClass().isArray()) {
            sb.append("[");
            boolean b = false;
            int n = Array.getLength(obj);
            for (int i = 0; i < n; i++) {
                if (b) sb.append(",");
                writeParamString(sb, Array.get(obj, i), set);
                b = true;
            }
            sb.append("]");
        }
        else
        {
            Iterator it;
            if ((obj instanceof Map)) {
                sb.append("{");
                Map map = (Map)obj;
                boolean b = false;

                it = map.keySet().iterator();
                while (it.hasNext()) {
                    if (b) sb.append(",");
                    Object key = it.next();
                    jencode(key.toString(), sb);
                    sb.append(":");
                    writeParamString(sb, map.get(key), set);
                    b = true;
                }
                sb.append("}");
            } else {
                sb.append("{");
                if ((!set.contains(obj)) && (obj.getClass() != Object.class) && (obj.getClass() != Class.class)) {
                    set.add(obj);
                    try {
                        List<Method> getters = findGetters(obj);
                        boolean b = false;
                        for (Method method : getters) {
                            if (b) sb.append(",");
                            jencode(findGetterName(method), sb);
                            sb.append(":");
                            writeParamString(sb, method.invoke(obj, new Object[0]), set);
                            b = true;
                        }
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                    set.remove(obj);
                }
                sb.append("}"); }
        }
    }

    private static List<Method> findGetters(Object obj) { List getters = new ArrayList();

        for (Method method : obj.getClass().getMethods()) {
            String name = method.getName();
            if ((!Modifier.isStatic(method.getModifiers())) &&
                    (!method.getReturnType().equals(Void.TYPE)) &&
                    (method.getParameterTypes().length == 0) &&
                    (method.getReturnType() != ClassLoader.class))
            {
                if (((name.startsWith("get")) && (name.length() >= 4) && (!name.equals("getClass"))) || (
                        (name.startsWith("is")) && (name.length() >= 3)))
                    getters.add(method);
            }
        }
        return getters; }

    private static String findGetterName(Method method) {
        String name = method.getName();
        if (name.startsWith("get"))
            name = name.substring(3);
        else if (name.startsWith("is")) {
            name = name.substring(2);
        }
        if ((name.length() > 1) &&
                (Character.isUpperCase(name.charAt(1))) &&
                (Character.isUpperCase(name.charAt(0)))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    private static void jencode(String str, StringBuilder sb) {
        sb.append("\"");

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\')
                sb.append("\\\\");
            else if (c == '/')
                sb.append("\\/");
            else if (c == '\n')
                sb.append("\\n");
            else if (c == '\r')
                sb.append("\\r");
            else if (c == '\t')
                sb.append("\\t");
            else if (c == '\'')
                sb.append("\\'");
            else if (c == '"')
                sb.append("\\\"");
            else {
                sb.append(c);
            }
        }
        sb.append("\"");
    }
    private static boolean isXDocData(String name, String value) {
        if ((name.equals("_xdoc")) || (name.equals("_xdata"))) {
            if ((value.startsWith("./")) ||
                    (value.startsWith("<")) ||
                    (value.startsWith("{")) ||
                    (value.startsWith("[")) ||
                    (value.startsWith("data:")) || (
                    (name.equals("_xdoc")) && (value.startsWith("text:")))) {
                return false;
            }
            return true;
        }

        return false;
    }
    private static String getFormat(String url) {
        String format = "xdoc";
        int pos = url.lastIndexOf(".");
        if (pos > 0) {
            format = url.substring(pos + 1).toLowerCase();
            if (format.equals("zip")) {
                url = url.substring(0, pos);
                pos = url.lastIndexOf(".");
                if (pos > 0) {
                    format = url.substring(pos + 1).toLowerCase() + ".zip";
                }
            }
        }
        return format;
    }
    private static String encode(Object str) {
        try {
            return URLEncoder.encode(String.valueOf(str), "UTF-8"); } catch (UnsupportedEncodingException e) {
        }
        return String.valueOf(str);
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8"); } catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    private static void pipe(InputStream in, OutputStream out) throws IOException
    {
        byte[] buf = new byte[4096];
        while (true) {
            int len = in.read(buf);
            if (len <= 0) break;
            out.write(buf, 0, len);
        }
        int len;
        out.flush();
        out.close();
        in.close();
    }
    private static boolean isFile(String url) {
        int pos = url.indexOf(':');
        return (pos < 0) ||
                (pos == 1) || (
                (pos == 2) && (url.charAt(0) == '/'));
    }

    public static String toDataURI(String url) throws IOException {
        return toDataURI(url, null);
    }

    public static String toDataURI(String url, String format) throws IOException {
        if (url.length() > 0) {
            InputStream in = null;
            if ((isFile(url)) || (url.startsWith("class://"))) {
                if (format == null) {
                    int pos = url.lastIndexOf('.');
                    if (pos > 0) {
                        format = url.substring(pos + 1).toLowerCase();
                    }
                }
                if (url.startsWith("class://")) {
                    String cls = url.substring(8, url.indexOf("/", 8));
                    String path = url.substring(url.indexOf("/", 8) + 1);
                    try {
                        in = Class.forName(cls).getResourceAsStream(path);
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                } else {
                    in = new FileInputStream(url);
                }
            } else {
                URLConnection conn = new URL(url).openConnection();
                in = conn.getInputStream();
                if (format == null) {
                    format = conn.getContentType();
                }
            }
            return toDataURI(in, format);
        }
        return "";
    }

    public static String toDataURI(InputStream in) throws IOException {
        return toDataURI(in, null);
    }

    public static String toDataURI(InputStream in, String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        pipe(in, out);
        if (format != null) {
            if (format.indexOf('/') < 0) {
                if (format.equals("jpg"))
                    format = "jpeg";
                else if (format.equals("htm")) {
                    format = "html";
                }
                if ((format.equals("png")) || (format.equals("jpeg")) || (format.equals("gif")))
                    format = "image/" + format;
                else if ((format.equals("html")) || (format.equals("xml")))
                    format = "text/" + format;
                else
                    format = "application/" + format;
            }
        }
        else {
            format = "application/octet-stream";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("data:").append(format).append(";base64,");
        sb.append(toBase64(out.toByteArray()));
        return sb.toString();
    }

    private static String toBase64(byte[] data) {
        char[] out = new char[(data.length + 2) / 3 * 4];
        int i = 0; for (int index = 0; i < data.length; index += 4) {
            boolean quad = false;
            boolean trip = false;

            int val = 0xFF & data[i];
            val <<= 8;
            if (i + 1 < data.length) {
                val |= 0xFF & data[(i + 1)];
                trip = true;
            }
            val <<= 8;
            if (i + 2 < data.length) {
                val |= 0xFF & data[(i + 2)];
                quad = true;
            }
            out[(index + 3)] = alphabet[64];
            val >>= 6;
            out[(index + 2)] = alphabet[64];
            val >>= 6;
            out[(index + 1)] = alphabet[(val & 0x3F)];
            val >>= 6;
            out[(index + 0)] = alphabet[(val & 0x3F)];

            i += 3;
        }

        return new String(out);
    }

    private static String[] csvSplit(String str) {
        List list = csvList(str);
        if (list.size() > 0) {
            List cols = (List)list.get(0);
            String[] strs = new String[cols.size()];
            for (int i = 0; i < strs.length; i++) {
                strs[i] = ((String)cols.get(i));
            }
            return strs;
        }
        return new String[0];
    }

    private static List<List<String>> csvList(String txt) {
        if (txt.length() > 0) {
            ArrayList rows = new ArrayList();
            ArrayList cols = new ArrayList();
            rows.add(cols);

            boolean strBegin = false;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < txt.length(); i++) {
                char c = txt.charAt(i);
                if (strBegin) {
                    if (c == '"') {
                        if (i + 1 < txt.length()) {
                            if (txt.charAt(i + 1) == '"') {
                                sb.append(c);
                                i++;
                            } else {
                                strBegin = false;
                            }
                        }
                        else strBegin = false;
                    }
                    else {
                        sb.append(c);
                    }
                }
                else if (c == ',') {
                    cols.add(sb.toString());
                    sb.setLength(0);
                } else if (c == '\n') {
                    cols.add(sb.toString());
                    sb.setLength(0);
                    cols = new ArrayList();
                    rows.add(cols);
                } else if (c == '"') {
                    strBegin = true;
                } else if (c != '\r') {
                    sb.append(c);
                }
            }

            if (sb.length() > 0) {
                cols.add(sb.toString());
            }
            return rows;
        }
        return new ArrayList();
    }

    @Target({java.lang.annotation.ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface XDoc {
        public abstract String value();
    }

    @Target({java.lang.annotation.ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface XParam {
        public abstract String value();
    }
}