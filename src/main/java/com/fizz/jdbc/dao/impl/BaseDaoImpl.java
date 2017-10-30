package com.fizz.jdbc.dao.impl;

import com.fizz.jdbc.core.SimpleJdbc;
import com.fizz.jdbc.dao.BaseDao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDaoImpl<T> extends SimpleJdbc implements BaseDao<T> {

    public List<T> queryForEntity(Class<T> clazz, String sql, Object[] params) {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> list = null;
        try {
            list = new ArrayList<T>();
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(transToEntity(rs, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(rs);
            free(stmt);
            free(conn);
        }
        return list;
    }

    public List<T> queryForEntity(Class<T> clazz, String sql) {
        return queryForEntity(clazz, sql, new Object[] {});
    }

    public T transToEntity(ResultSet rs, Class<T> clazz) throws SQLException {
        T t = null;
        try {
            t = clazz.newInstance();
            Map<String, Class<?>> map_field = initEntityField(clazz);
            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();
            for (int i=1; i<=columnCount; i++) {
                String columnName = rsd.getColumnName(i);
                Object columnValue = rs.getObject(columnName);
                if (map_field.containsKey(columnName)) {
                    String setMethodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
                    Method setMethod = clazz.getMethod(setMethodName, map_field.get(columnName));
                    setMethod.invoke(t, columnValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    private Map<String, Class<?>> initEntityField(Class<T> clazz) {
        Map<String, Class<?>> map = new HashMap<String, Class<?>>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            map.put(field.getName(), field.getType());
        }
        return map;
    }

}
