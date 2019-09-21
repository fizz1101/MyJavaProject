package com.fizz.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

public class ResultBuilder {

    private static String cglib_prefix = "$cglib_prop_";

    /**
     * @param data
     * @param schema
     * @return
     */

    public Object handle(String schema, Object... data)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        /* 无数据或者无模板 */
        if (data.length < 1 || schema == null) {
            return null;
        }
        Object dto = null;
        /* 单一数据处理 */
        if (data.length == 1) {
            dto = data[0];
            List<JSONObject> list = null;
            JSONObject jsonObject = null;
            try {
                list = JSON.parseObject(schema, List.class);
            } catch (Exception e) {
                jsonObject = JSON.parseObject(schema);
            }
            if (list != null) {
                if (CollectionUtils.isEmpty(list)) {
                    return new ArrayList<Map<String, Object>>();
                }
                jsonObject = list.get(0);
                List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                List<Object> datas = (List<Object>) dto;
                for (Object object : datas) {
                    result.add(objToObj(object, jsonObject));
                }
                return result;
            } else {
                return objToObj(dto, jsonObject);
            }
        }
        /* 组合数据处理 */
        if (data.length > 1) {
            /* 组装dto */
            /* 都是非list数据的组装 flag */
            boolean flag = false;
            for (Object object : data) {
                if (object instanceof List) {
                    flag = true;
                }
            }
            if (flag) {
                /* 多个list组合 list。size相同 */
                /* 先构造扁平的key视图 */
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (Object listObj : data) {
                    if (!(listObj instanceof List)) {
                        throw new IllegalArgumentException("Class type exception \"" + listObj + "\"");
                    }
                    Field[] fields = ((List) listObj).get(0).getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (map.containsKey(field.getName())) {
                            throw new IllegalArgumentException("Duplicate property name \"" + field.getName() + "\"");
                        }
                        map.put(field.getName(), field.getType());
                    }
                }
                /* 给生成的视图对象赋值 */
                Integer size = ((List) data[0]).size();
                List<DynamicBean> beans = new ArrayList<DynamicBean>();
                for (int i = 0; i < size; i++) {
                    DynamicBean dynamicBean = new DynamicBean(map);
                    for (Object listObj : data) {
                        Object obj = ((List) listObj).get(i);
                        Field[] fields = obj.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            dynamicBean.setValue(field.getName(), field.get(obj));
                        }
                    }
                    beans.add(dynamicBean);
                }
                return handle(schema, beans);
            } else {
                /* 没有任何的list 单纯的数据组装 不支持重复key */
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                for (Object obj : data) {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (map.containsKey(field.getName())) {
                            throw new IllegalArgumentException("Duplicate property name \"" + field.getName() + "\"");
                        }
                        map.put(field.getName(), field.getType());
                    }
                }
                DynamicBean object = new DynamicBean(map);

                for (Object obj : data) {
                    Field[] fields = obj.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        object.setValue(field.getName(), field.get(obj));
                    }
                }
                return handle(schema, object);
            }
        }
        return null;

    }

    /**
     * 自定义扩展
     *
     * @param data
     * @param hander
     * @return
     */
    public Object handle(IHander hander, Object... data) {
        return hander.hande(data);
    }

    /**
     * @param data
     * @param stream
     * @return
     */
    public Object handle(InputStream stream, Object... data) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException, IOException {
        if (data.length < 1 || stream == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        streamToStr(stream, buffer);
        return handle(buffer.toString(), data);
    }

    /**
     * 接收文件流处理成str
     *
     * @param stream
     * @param buffer
     * @throws Exception
     */
    public void streamToStr(InputStream stream, StringBuffer buffer) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String data = null;
        while ((data = br.readLine()) != null) {
            buffer.append(data);
        }
    }

    /**
     * 实现对象到模板的转换
     *
     * @param dto 数据对象
     * @param jsonObject 模板
     * @return 数据结果集
     */
    public Map<String, Object> objToObj(Object dto, JSONObject jsonObject)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Object obj = null;
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            Field field = null;
            if (dto instanceof DynamicBean) {
                field = ((DynamicBean) dto).getObject().getClass().getDeclaredField(cglib_prefix + key);
                obj = ((DynamicBean) dto).getObject();
            } else {
                field = dto.getClass().getDeclaredField(key);
                obj = dto;
            }

            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) {
                map.put(key, null);
            } else {
                if (value instanceof Integer) {
                    map.put(key, value);
                } else if (value instanceof Long) {
                    map.put(key, value);
                } else if (value instanceof Double) {
                    map.put(key, value);
                } else if (value instanceof Float) {
                    map.put(key, value);
                } else if (value instanceof String) {
                    map.put(key, value);
                } else if (value instanceof Collection) {
                    if (CollectionUtils.isEmpty((List<Object>) value)) {
                        map.put(key, new ArrayList());
                    } else {
                        map.put(key, handle(entry.getValue().toString(), (List<Object>) value));
                    }
                } else if (value instanceof Map) {
                    map.put(key, value);
                } else {
                    map.put(key, objToObj(value, jsonObject.getJSONObject(key)));
                }
            }
        }
        return map;
    }

    public static void main(String[] args) {
        JSONObject aaa = new JSONObject();
        aaa.put("username", "uname");
        aaa.put("password", "pwd");
        System.out.println(aaa.toJSONString());
        String schema = "{\"username\":\"uname\",\"password\":\"pwd\"}";
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("username", "fizz");
        jsonObj.put("password", "123456");
        jsonObj.put("code", "1101");
        ResultBuilder resultBuilder = new ResultBuilder();
        try {
            Object obj = resultBuilder.handle(schema, jsonObj);
            System.out.println(obj.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
