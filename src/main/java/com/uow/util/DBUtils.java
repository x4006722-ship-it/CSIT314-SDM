package com.uow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
    private static final String HOST = " junction.proxy.rlwy.net"; 
    private static final String PORT = "28877";
    private static final String DATABASE = "railway";
    private static final String USER = "root";
    private static final String PASSWORD = "zIjUZrCmjqyCKfxybVgadqKHPsXeveHz";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver 没找到，请检查 pom.xml", e);
        }
    }
}