package com.fizz.jdbc.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RowEntity {

    public static Object transToEntity(ResultSet rs, Class<?> clazz) throws SQLException {
        Object obj = null;
        try {
            obj = clazz.newInstance();
            Map<String, Class<?>> map_field = initEntityField(clazz);
            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();
            for (int i=1; i<=columnCount; i++) {
                String columnName = rsd.getColumnName(i);
                Object columnValue = rs.getObject(columnName);
                if (map_field.containsKey(columnName)) {
                    String setMethodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method setMethod = clazz.getMethod(setMethodName, map_field.get(columnName));
                    setMethod.invoke(obj, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static Map<String, Class<?>> initEntityField(Class<?> clazz) {
        Map<String, Class<?>> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            map.put(field.getName(), field.getType());
        }
        return map;
    }

}
