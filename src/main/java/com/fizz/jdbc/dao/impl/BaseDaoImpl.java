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

    public boolean saveBatch(String sql, List<T> list) {
        boolean flag = false;
        try {
            sql = sql.toUpperCase();
            String sql_head = sql.substring(0, sql.indexOf("VALUE"));
            int index_s = sql.indexOf("(");
            int index_e = sql.indexOf(")");
            if (index_s>=0 && index_e>=0) {
                sql_head = sql_head.substring(index_s+1, index_e);
            }
            String[] params = sql_head.split(",");
            T t = list.get(0);
            Class clazz = t.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String fileName = field.getName();
                String getMethodName = "get" + fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
                Method getMethod = clazz.getMethod(getMethodName, new Class[]{});
                Object value = getMethod.invoke(t, new Object[]{});

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

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
