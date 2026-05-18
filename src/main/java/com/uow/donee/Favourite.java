package com.uow.donee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.uow.util.DBUtils;

public class Favourite {

    // Save Favourite — returns null on success, error message string on failure
    public String saveFavourite(int fraId, int userId, boolean remove) {
        if (remove) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "DELETE FROM fra_favourite WHERE user_id = ? AND fra_id = ?")) {
                ps.setInt(1, userId);
                ps.setInt(2, fraId);
                ps.executeUpdate();
                return null;
            } catch (SQLException e) {
                return "DELETE failed: " + e.getMessage();
            }
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement check = c.prepareStatement(
                     "SELECT 1 FROM fra_favourite WHERE user_id = ? AND fra_id = ? LIMIT 1")) {
            check.setInt(1, userId);
            check.setInt(2, fraId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    return null; // already saved
                }
            }
        } catch (SQLException e) {
            return "SELECT failed: " + e.getMessage();
        }

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO fra_favourite (user_id, fra_id) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setInt(2, fraId);
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            return "INSERT failed: " + e.getMessage();
        }
    }

    // Search Favourite
    public Object getSearchFavourite(Object searchFavouriteData) {
        if (!(searchFavouriteData instanceof Map<?, ?> data)) {
            return List.of();
        }

        int userId = readInt(data.get("userId"));
        String title = readText(data.get("title"));
        String fraStatus = readText(data.get("fraStatus"));
        String categoryName = readText(data.get("categoryName"));

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.title, f.fra_status, f.category_id, IFNULL(TRIM(fc.category_name),'') AS category_name, f.current_amount, f.target_amount "
                        + "FROM fra f "
                        + "INNER JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE 1=1 ");
        if (!title.isBlank()) {
            sql.append("AND LOWER(f.title) LIKE LOWER(?) ");
        }
        if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) {
            sql.append("AND f.fra_status = ? ");
        }
        if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        }
        sql.append("ORDER BY f.fra_id");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (!title.isBlank()) {
                ps.setString(i++, "%" + title + "%");
            }
            if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) {
                ps.setString(i++, fraStatus);
            }
            if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) {
                ps.setString(i++, categoryName);
            }

            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fra_id", rs.getInt("fra_id"));
                    row.put("title", rs.getString("title"));
                    row.put("status", rs.getString("fra_status"));
                    row.put("categoryId", rs.getObject("category_id"));
                    row.put("category", rs.getString("category_name"));
                    row.put("currentAmount", rs.getObject("current_amount"));
                    row.put("targetAmount", rs.getObject("target_amount"));
                    out.add(row);
                }
            }
            return out;
        } catch (SQLException e) {
            return List.of();
        }
    }

    // View Favourite
    public Object getViewFavourite(int fraId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT f.title, f.fra_status, f.startedAt, f.viewCount, f.favoriteCount, "
                            + "f.current_amount AS current_amount, f.target_amount AS target_amount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '-') AS doneeName "
                             + "FROM fra f "
                             + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                             + "WHERE f.fra_id = ?")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("title", rs.getString("title"));
                out.put("status", rs.getString("fra_status"));
                out.put("createAt", rs.getString("startedAt"));
                out.put("viewCount", rs.getObject("viewCount"));
                out.put("favouriteCount", rs.getObject("favoriteCount"));
                out.put("currentAmount", rs.getObject("current_amount"));
                out.put("targetAmount", rs.getObject("target_amount"));
                out.put("doneeName", rs.getString("doneeName"));
                return out;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int readInt(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
