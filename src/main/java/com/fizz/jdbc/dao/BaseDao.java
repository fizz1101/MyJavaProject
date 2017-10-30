package com.fizz.jdbc.dao;

import java.util.List;

public interface BaseDao<T> {

    /**
     * select功能
     *
     * @param sql
     * @param params
     * @return List<?>数据集合
     */
    public abstract List<T> queryForEntity(Class<T> clazz, String sql, Object[] params);

    /**
     * select功能
     *
     * @param sql
     * @return List<?>数据集合
     */
    public abstract List<T> queryForEntity(Class<T> clazz, String sql);

}
