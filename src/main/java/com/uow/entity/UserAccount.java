package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.uow.util.DBUtils;

public class UserAccount {
    private String username;
    private String password;
    private String a_status; 

    public UserAccount(String username, String password, String a_status) {
        this.username = username;
        this.password = password;
        this.a_status = a_status;
    }

    public static UserAccount fetchFromDB(String inputUser) {
        String sql = "SELECT * FROM user_account WHERE username = ?";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, inputUser);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new UserAccount(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("a_status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; 
    }


    public boolean verifyCredentials(String inputPass) {
        return this.password.equals(inputPass);
    }

    public boolean checkAccountStatus() {
        return "Active".equalsIgnoreCase(this.a_status);
    }
}