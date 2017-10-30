package com.fizz.jdbc.core;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleJdbc implements JdbcOperation {

    private static final boolean AUTO_COMMIT = true;

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SimpleJdbc() {
        if (this.dataSource == null) {
            this.dataSource = new SimpleDataSource();
        }
    }

    public SimpleJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return getConnection(AUTO_COMMIT);
    }

    public Connection getConnection(boolean autoCommit) {
        try {
            Connection conn = dataSource.getConnection();
            if (!autoCommit)
                conn.setAutoCommit(autoCommit);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean execute(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection(false);
        PreparedStatement stmt = null;
        boolean flag = false;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            stmt.execute();
            conn.commit();
            flag = true;
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            free(stmt);
            free(conn);
        }
        return flag;
    }

    public boolean execute(String sql) throws SQLException {
        return execute(sql, new Object[] {});
    }

    public int executeBatch(String sql, List<Object[]> params) throws SQLException {
        int result = 0;
        Connection conn = getConnection(false);
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                Object[] param = params.get(i);
                for (int j = 0; j < param.length; j++) {
                    stmt.setObject(j + 1, param[j]);
                }
                stmt.addBatch();
                if (i % 1000 == 0) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                }
            }
            stmt.executeBatch();
            conn.commit();
            result = params.size();
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        } finally {
            free(stmt);
            free(conn);
        }
        return result;
    }

    public int executeBatch(String sql) throws SQLException {
        return executeBatch(sql, new ArrayList<Object[]>());
    }

    public ResultSet queryForResultSet(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            return stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(stmt);
            free(conn);
        }
        return null;
    }

    public ResultSet queryForResultSet(String sql) throws SQLException {
        return queryForResultSet(sql, new Object[] {});
    }

    public int queryForInt(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(rs);
            free(stmt);
            free(conn);
        }
        return 0;
    }

    public int queryForInt(String sql) throws SQLException {
        return queryForInt(sql, new Object[] {});
    }

    public String queryForString(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(rs);
            free(stmt);
            free(conn);
        }
        return "";
    }

    public String queryForString(String sql) throws SQLException {
        return queryForString(sql,  new Object[] {});
    }

    public List<?> queryForEntity(String sql, Object[] params, Class<?> clazz) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Object> list = null;
        try {
            list = new ArrayList<Object>();
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(RowEntity.transToEntity(rs, clazz));
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

    public List<?> queryForEntity(String sql,  Class<?> clazz) throws SQLException {
        return queryForEntity(sql, new Object[] {}, clazz);
    }

    public List<?> queryForBean(String sql, Object[] params, RowMapper<?> mapper) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Object> list = null;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();
            list = new ArrayList<Object>();
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
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

    public List<?> queryForBean(String sql, RowMapper<?> mapper) throws SQLException {
        return queryForBean(sql, new Object[] {}, mapper);
    }

    public List<Map<String, Object>> queryForMap(String sql, Object[] params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createPreparedStatement(conn, sql, params);
            rs = stmt.executeQuery();

            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = null;
            ResultSetMetaData rsd = rs.getMetaData();
            int columnCount = rsd.getColumnCount();

            while (rs.next()) {
                map = new HashMap<String, Object>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsd.getColumnName(i), rs.getObject(i));
                }
                list.add(map);
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            free(rs);
            free(stmt);
            free(conn);
        }
        return null;
    }

    public List<Map<String, Object>> queryForMap(String sql) throws SQLException {
        return queryForMap(sql, new Object[] {});
    }

    protected PreparedStatement createPreparedStatement(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++)
            stmt.setObject(i + 1, params[i]);
        return stmt;
    }

    public void free(Connection x) {
        if (x != null)
            try {
                x.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void free(Statement x) {
        if (x != null)
            try {
                x.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void free(PreparedStatement x) {
        if (x != null)
            try {
                x.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public void free(ResultSet x) {
        if (x != null)
            try {
                x.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

}
