package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class Login {

    private int user_id;
    private String username;
    private String userPassword;
    private String a_status;
    private int profile_id;
    private String role;
    private String p_status;

    public boolean verifyCredentials(String username, String userPassword) {
        String sql = """
                SELECT ua.user_id, ua.username, ua.password, ua.a_status, ua.profile_id, up.role, up.p_status
                FROM user_account ua
                JOIN user_profile up ON ua.profile_id = up.profile_id
                WHERE ua.username = ? AND ua.password = ?
                """;

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, userPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.user_id = resultSet.getInt("user_id");
                    this.username = resultSet.getString("username");
                    this.userPassword = resultSet.getString("password");
                    this.a_status = resultSet.getString("a_status");
                    this.profile_id = resultSet.getInt("profile_id");
                    this.role = resultSet.getString("role");
                    this.p_status = resultSet.getString("p_status");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error occurred while verifying credentials: " + e.getMessage());
        }

        return false;
    }

    public boolean checkAccountStatus() {
        return "Active".equalsIgnoreCase(a_status);
    }

    public boolean checkProfileStatus() {
        return "Active".equalsIgnoreCase(p_status);
    }

    public String getRedirectPageByRole() {
        if ("User Admin".equalsIgnoreCase(role)) {
            return "/AdminPage.html";
        } else if ("Fund Raiser".equalsIgnoreCase(role)) {
            return "/FundRaiserPage.html";
        } else if ("Donee".equalsIgnoreCase(role)) {
            return "/DoneePage.html";
        } else if ("Platform Management".equalsIgnoreCase(role)) {
            return "/PlatformPage.html";
        }
        return null;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getA_status() {
        return a_status;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public String getRole() {
        return role;
    }

    public String getP_status() {
        return p_status;
    }
}