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

    // Save Favourite
    public boolean saveFavourite(int fraId, int userId, boolean remove) {
        if (remove) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "DELETE FROM fra_favourite WHERE user_id = ? AND fra_id = ?")) {
                ps.setInt(1, userId);
                ps.setInt(2, fraId);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                return false;
            }
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement check = c.prepareStatement(
                     "SELECT 1 FROM fra_favourite WHERE user_id = ? AND fra_id = ? LIMIT 1")) {
            check.setInt(1, userId);
            check.setInt(2, fraId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO fra_favourite (user_id, fra_id) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setInt(2, fraId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
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
                "SELECT f.fra_id, f.fra_title, f.fra_status, IFNULL(TRIM(fc.category_name),'') AS category_name "
                        + "FROM fra f "
                        + "INNER JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE 1=1 ");
        if (!title.isBlank()) {
            sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
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
                    row.put("title", rs.getString("fra_title"));
                    row.put("status", rs.getString("fra_status"));
                    row.put("category", rs.getString("category_name"));
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
                     "SELECT f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
                            + "f.current_amount AS current_amount, f.fra_targetAmount AS target_amount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '-') AS doneeName "
                             + "FROM fra f "
                             + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                             + "WHERE f.fra_id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("title", rs.getString("fra_title"));
                out.put("status", rs.getString("fra_status"));
                out.put("createAt", rs.getString("fra_createdAt"));
                out.put("viewCount", rs.getObject("fra_viewCount"));
                out.put("favouriteCount", rs.getObject("fra_favouriteCount"));
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
