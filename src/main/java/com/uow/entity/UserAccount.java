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

    public static String getRoleIfValid(String username, String password) {
        String sql = "SELECT p.role FROM user_account u " +
                     "JOIN user_profile p ON u.profile_id = p.profile_id " +
                     "WHERE u.username = ? AND u.password = ? " +
                     "AND u.a_status = 'Active' AND p.p_status = 'Active'";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}