package com.uow.donee;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.sql.*;
import java.util.*;
import com.uow.util.DBUtils;
import static org.junit.Assert.*;

public class FavouriteTest {
    private Favourite favouriteDao;
    private int userId = -1, profileId = -1, catId = -1, fraId = -1;
    private final String SEED = String.valueOf(System.currentTimeMillis());

    @Before
    public void setUp() {
        favouriteDao = new Favourite();
        buildResilientChain();
    }

    @After
    public void tearDown() {
        destroyResilientChain();
    }

    private void buildResilientChain() {
        try (Connection c = DBUtils.getConnection()) {
            // 1. Profile
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO user_profile (role, p_status) VALUES (?, 'Active')", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "FAV_R_" + SEED);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) profileId = rs.getInt(1);
            }
            // 2. User (补齐必填的 full_name, email, phone_number)
            String sqlUser = "INSERT INTO user_account (username, password, full_name, email, phone_number, a_status, profile_id) VALUES (?, '123', 'Fav Test Name', 'fav@test.com', '111', 'Active', ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "FAV_U_" + SEED);
                ps.setInt(2, profileId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) userId = rs.getInt(1);
            }
            // 3. Category
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO fra_category (category_name, category_status) VALUES (?, 'Active')", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "FAV_C_" + SEED);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) catId = rs.getInt(1);
            }
            // 4. FRA
            String sqlFra = "INSERT INTO fra (fra_title, fra_status, category_id, donee_id, fundRaiser_id, fra_targetAmount, current_amount) VALUES (?, 'Pending', ?, ?, ?, 100, 0)";
            try (PreparedStatement ps = c.prepareStatement(sqlFra, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "FAV_FRA_" + SEED);
                ps.setInt(2, catId);
                ps.setInt(3, userId);
                ps.setInt(4, userId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) fraId = rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void destroyResilientChain() {
        try (Connection c = DBUtils.getConnection(); Statement s = c.createStatement()) {
            if (userId != -1) s.executeUpdate("DELETE FROM fra_favourite WHERE user_id = " + userId);
            if (fraId != -1) s.executeUpdate("DELETE FROM fra WHERE fra_id = " + fraId);
            if (userId != -1) s.executeUpdate("DELETE FROM user_account WHERE user_id = " + userId);
            if (profileId != -1) s.executeUpdate("DELETE FROM user_profile WHERE profile_id = " + profileId);
            if (catId != -1) s.executeUpdate("DELETE FROM fra_category WHERE category_id = " + catId);
        } catch (SQLException e) {}
    }

    @Test
    public void test_Favourite_lifecycle_save_search_and_remove_succeeds() {
        if (userId == -1 || fraId == -1) fail("Chain setup failed due to SQL constraints");
        assertTrue("Save failed", favouriteDao.saveFavourite(fraId, userId, false));
        Map<String, Object> query = new HashMap<>();
        query.put("userId", userId);
        Object results = favouriteDao.getSearchFavourite(query);
        assertFalse("Search should find record", ((List<?>) results).isEmpty());
        assertTrue("Remove failed", favouriteDao.saveFavourite(fraId, userId, true));
    }

    @Test
    public void test_View_favourite_returns_null_for_invalid_id() {
        assertNull(favouriteDao.getViewFavourite(-1));
    }
}