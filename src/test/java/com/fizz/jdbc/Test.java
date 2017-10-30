package com.fizz.jdbc;

import com.fizz.jdbc.core.RowMapper;
import com.fizz.jdbc.core.SimpleJdbc;
import com.fizz.jdbc.dao.impl.UserDaoImpl;
import com.fizz.jdbc.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws SQLException {
//		SimpleDataSource dataSource = new SimpleDataSource();
        SimpleJdbc jdbc = new SimpleJdbc();
        String sql = "select * from user";
//		int count = jdbc.queryForInt(sql);
//		System.out.println(count);
        /**方式一*/
        List<User> list1 = (List<User>) jdbc.queryForBean(sql, new RowMapper<User>() {
            User user = null;

            public User mapRow(ResultSet rs) throws SQLException {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                return user;
            }
        });
        System.out.println("方式一结果--------------------");
        for (User user : list1) {
            System.out.println(user.getId() + "---" + user.getUsername());
        }

        /**方式二*/
        List<User> list2 = (List<User>) jdbc.queryForEntity(sql, User.class);
        System.out.println("方式二结果--------------------");
        for (User user : list2) {
            System.out.println(user.getId() + "---" + user.getUsername());
        }

        /**方式三*/
        List<Map<String, Object>> list3 = jdbc.queryForMap(sql);
        System.out.println("方式三结果--------------------");
        for (Map<String, Object> map : list3) {
            for (String key : map.keySet()) {
                System.out.println(key + "---" + map.get(key));
            }
        }

        /**方式四*/
        UserDaoImpl userDaoImpl = new UserDaoImpl();
        List<User> list4 = userDaoImpl.queryForEntity(User.class, sql);
        System.out.println("方式四结果--------------------");
        for (User user : list4) {
            System.out.println(user.getId() + "---" + user.getUsername());
        }
    }

}
