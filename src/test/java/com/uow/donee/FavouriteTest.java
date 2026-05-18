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
    private final String SEED = UUID.randomUUID().toString().substring(0, 8);

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
            
            try (PreparedStatement ps = c.prepareStatement("SELECT profile_id FROM user_profile WHERE role = 'Donee' LIMIT 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        profileId = rs.getInt("profile_id");
                    } else {
                        try (PreparedStatement insertPs = c.prepareStatement("INSERT INTO user_profile (role, p_status) VALUES ('Donee', 'Active')", Statement.RETURN_GENERATED_KEYS)) {
                            insertPs.executeUpdate();
                            ResultSet insertRs = insertPs.getGeneratedKeys();
                            if (insertRs.next()) profileId = insertRs.getInt(1);
                        }
                    }
                }
            }

            String sqlUser = "INSERT INTO user_account (username, password, full_name, email, phone_number, a_status, profile_id) VALUES (?, '123', 'Fav Test', ?, '111', 'Active', ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "U_" + SEED);
                ps.setString(2, SEED + "@test.com"); 
                ps.setInt(3, profileId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) userId = rs.getInt(1);
            }
            
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO fra_category (category_name, category_status) VALUES (?, 'Active')", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "C_" + SEED);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) catId = rs.getInt(1);
            }
            
            String sqlFra = "INSERT INTO fra (title, fra_status, category_id, donee_id, fundRaiser_id, target_amount, current_amount, viewCount, favoriteCount) VALUES (?, 'Pending', ?, ?, ?, 100, 0, 0, 0)";
            try (PreparedStatement ps = c.prepareStatement(sqlFra, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "FRA_" + SEED);
                ps.setInt(2, catId);
                ps.setInt(3, userId);
                ps.setInt(4, userId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) fraId = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Database setup failed: " + e.getMessage(), e);
        }
    }

    private void destroyResilientChain() {
        try (Connection c = DBUtils.getConnection(); Statement s = c.createStatement()) {
            if (userId != -1) s.executeUpdate("DELETE FROM fra_favourite WHERE user_id = " + userId);
            if (fraId != -1) s.executeUpdate("DELETE FROM fra WHERE fra_id = " + fraId);
            if (userId != -1) s.executeUpdate("DELETE FROM user_account WHERE user_id = " + userId);
            if (catId != -1) s.executeUpdate("DELETE FROM fra_category WHERE category_id = " + catId);
        } catch (SQLException e) {}
    }

    @Test
    public void test_Favourite_lifecycle_save_search_and_remove_succeeds() {
        if (userId == -1 || fraId == -1) {
            fail("Test skipped: Chain setup failed.");
        }

        assertNull("Save should succeed (null = no error)", favouriteDao.saveFavourite(fraId, userId, false));

        Map<String, Object> query = new HashMap<>();
        query.put("userId", userId);
        Object results = favouriteDao.getSearchFavourite(query);
        assertFalse(((List<?>) results).isEmpty());

        assertNull("Remove should succeed (null = no error)", favouriteDao.saveFavourite(fraId, userId, true));
    }

    @Test
    public void test_Search_favourite_with_invalid_data_type_returns_empty_list() {
        Object results = favouriteDao.getSearchFavourite("Invalid Input Type");
        assertTrue(((List<?>) results).isEmpty());
    }

    @Test
    public void test_View_favourite_returns_null_for_invalid_id() {
        assertNull(favouriteDao.getViewFavourite(-1));
    }
}