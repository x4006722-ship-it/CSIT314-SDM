package com.uow.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.uow.util.DBUtils;

public class UserAdminLoginAccount {

    private int user_id;
    private String username;
    private String password;
    private String a_status;
    private int profile_id;
    private String role;

    public boolean verifyCredentials(String username, String password) {
        String sql = """
                SELECT ua.user_id, ua.username, ua.password, ua.a_status, ua.profile_id, up.role
                FROM user_account ua
                JOIN user_profile up ON ua.profile_id = up.profile_id
                WHERE ua.username = ? AND ua.password = ?
                """;

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    this.user_id = resultSet.getInt("user_id");
                    this.username = resultSet.getString("username");
                    this.password = resultSet.getString("password");
                    this.a_status = resultSet.getString("a_status");
                    this.profile_id = resultSet.getInt("profile_id");
                    this.role = resultSet.getString("role");
                    return true;
                }
            }

        } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    }

        return false;
    }

    public boolean checkAccountStatus() {
        return "Active".equalsIgnoreCase(a_status);
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
}