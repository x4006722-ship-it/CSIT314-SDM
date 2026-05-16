package com.uow.util;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * JDBC access for legacy entity code. Uses a shared HikariCP pool so each request
 * does not open a new TCP connection to the database (critical for remote hosts).
 */
public final class DBUtils {

    private static final String HOST = System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "junction.proxy.rlwy.net";
    private static final String PORT = System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "28877";
    private static final String DATABASE = System.getenv("MYSQLDATABASE") != null ? System.getenv("MYSQLDATABASE") : "railway";
    private static final String USER = System.getenv("MYSQLUSER") != null ? System.getenv("MYSQLUSER") : "root";
    private static final String PASSWORD = System.getenv("MYSQLPASSWORD") != null ? System.getenv("MYSQLPASSWORD") : "zIjUZrCmjqyCKfxybVgadqKHPsXeveHz";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static volatile HikariDataSource dataSource;

    private DBUtils() {}

    public static Connection getConnection() throws SQLException {
        return dataSource().getConnection();
    }

    private static HikariDataSource dataSource() {
        if (dataSource == null) {
            synchronized (DBUtils.class) {
                if (dataSource == null) {
                    HikariConfig cfg = new HikariConfig();
                    cfg.setJdbcUrl(URL);
                    cfg.setUsername(USER);
                    cfg.setPassword(PASSWORD);
                    cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    cfg.setPoolName("csit314-main");
                    cfg.setMaximumPoolSize(16);
                    cfg.setMinimumIdle(2);
                    cfg.setConnectionTimeout(25_000);
                    cfg.setInitializationFailTimeout(60_000);
                    cfg.setIdleTimeout(300_000);
                    cfg.setMaxLifetime(1_800_000);
                    dataSource = new HikariDataSource(cfg);
                }
            }
        }
        return dataSource;
    }
}
