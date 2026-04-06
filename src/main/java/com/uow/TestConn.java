package com.uow;

import java.sql.Connection;

import com.uow.util.DBUtils;

public class TestConn {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtils.getConnection();
            if (conn != null) {
                System.out.println("Good");
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("noooo");
            e.printStackTrace();
        }
    }
}