package com.fizz.temp;

import com.fizz.jdbc.core.SimpleJdbc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private static String sql_insert_user = "insert into system_user(ID, USERNAME, PASSWORD, CONTACT_NAME, CONTACT_TELE, BUSINESS_ID, USER_STATUS, LATEST_LOGIN_TIME, CREATE_TIME, UPDATE_TIME, SALT, DELETE_FLAG) values(?, ?, '578bdbce5a94ab2f22631e0133b52a85', ?, ?, ?, 1, 1563152400000, 1563152400000, 1563152400000, 'b2a16d86532740c54fbabb9ed19531a3', 0)";
    private static String sql_insert_role = "insert into system_user_role(ID, USER_ID, ROLE_ID, DELETE_FLAG) values(?, ?, ?, 0)";
    private static Long id_user_start = 150000000000125L;
    private static  Long id_role_start = 151000000000125L;


    private static void inserUser(String filePath, int role) throws IOException, SQLException {
        SimpleJdbc jdbc = new SimpleJdbc();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<Object[]> list_user = new ArrayList<>();
        List<Object[]> list_role = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            id_user_start++;
            id_role_start++;
            Object[] obj = line.split(",");
            Object[] data_user = new Object[obj.length+1];
            data_user[0] = id_user_start;
            System.arraycopy(obj, 0, data_user, 1, obj.length);
            list_user.add(data_user);

            Object userId = data_user[0];
            Object[] data_role = new Object[3];
            data_role[0] = id_role_start;
            data_role[1] = userId;
            data_role[2] = role;
            list_role.add(data_role);
        }
        int count_user = jdbc.executeBatch(sql_insert_user, list_user);
        int count_role = jdbc.executeBatch(sql_insert_role, list_role);
        System.out.println("插入用户数：" + count_user + "；插入角色数：" + count_role);
    }

    public static void main(String[] args) throws IOException, SQLException {
        //插入客户经理
        inserUser("E:\\ttt\\user_manager.txt", 1);
        //插入工程师
//        inserUser("E:\\ttt\\user_engineer.txt", 2);
    }

}
