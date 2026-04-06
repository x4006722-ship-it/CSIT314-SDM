package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class UserAccount {
    private String username;
    private String password;
    private String a_status; 
    private int profile_id;

    public UserAccount(String username, String password, String a_status, int profile_id) {
        this.username = username;
        this.password = password;
        this.a_status = a_status;
        this.profile_id = profile_id;
    }

    public static boolean verifyCredentials(String username, String password) {
        String sql = "SELECT * FROM user_account WHERE username = ? AND password = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkAccountStatus(String username) {
        String sql = "SELECT a_status FROM user_account WHERE username = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "Active".equalsIgnoreCase(rs.getString("a_status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}