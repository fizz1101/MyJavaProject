package com.fizz.jdbc.mapper;

import com.fizz.jdbc.core.RowMapper;
import com.fizz.jdbc.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        return user;
    }

}
