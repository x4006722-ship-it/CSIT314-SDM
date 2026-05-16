package com.uow.login;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.*;
import com.uow.util.DBUtils;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class LoginTest {
    private Login loginDao;
    private final String UNIQUE_SEED = String.valueOf(System.currentTimeMillis());
    private final String TEST_USER = "USER_" + UNIQUE_SEED;
    private final String TEST_PASS = "PASS_" + UNIQUE_SEED;
    private int generatedProfileId = -1;

    @Before
    public void setUp() {
        loginDao = new Login();
        prepareResilientData();
    }

    @After
    public void tearDown() {
        cleanResilientData();
    }

    private void prepareResilientData() {
        try (Connection c = DBUtils.getConnection()) {
            // 1. 插入 Profile
            String sqlProf = "INSERT INTO user_profile (role, p_status) VALUES (?, 'Active')";
            try (PreparedStatement ps = c.prepareStatement(sqlProf, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "ROLE_" + UNIQUE_SEED);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) generatedProfileId = rs.getInt(1);
            }
            // 2. 插入 Account (补全所有必填字段：full_name, email, phone_number)
            String sqlUser = "INSERT INTO user_account (username, password, full_name, email, phone_number, a_status, profile_id) " +
                             "VALUES (?, ?, ?, ?, ?, 'Active', ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlUser)) {
                ps.setString(1, TEST_USER);
                ps.setString(2, TEST_PASS);
                ps.setString(3, "TDD Full Name"); // 修复：填入必填姓名
                ps.setString(4, TEST_USER + "@test.com"); // 修复：填入必填邮箱
                ps.setString(5, "12345678"); // 修复：填入必填电话
                ps.setInt(6, generatedProfileId);
                ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cleanResilientData() {
        try (Connection c = DBUtils.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM user_account WHERE username = ?")) {
                ps.setString(1, TEST_USER);
                ps.executeUpdate();
            }
            if (generatedProfileId != -1) {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM user_profile WHERE profile_id = ?")) {
                    ps.setInt(1, generatedProfileId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {}
    }

    @Test
    public void test_VerifyLogin_succeeds_with_correct_credentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USER);
        credentials.put("password", TEST_PASS);

        Object result = loginDao.verifyLogin(credentials);
        assertNotNull("Login must return a data map", result);
        assertEquals("Active", ((Map<?, ?>) result).get("a_status"));
    }

    @Test
    public void test_VerifyLogin_returns_null_for_wrong_password() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", TEST_USER);
        credentials.put("password", "WRONG_SECRET");
        assertNull(loginDao.verifyLogin(credentials));
    }
}