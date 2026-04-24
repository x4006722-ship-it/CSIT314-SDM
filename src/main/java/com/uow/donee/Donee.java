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

public class Donee {

    public int fraId;
    public int userId;
    public String title;
    public String fraStatus;
    public String categoryName;
    public String doneeName;
    public String lastErrorMessage = "";

    // Search Fra
    public Object getSearchFra(int userId, String title, String fraStatus, String categoryName) {
        lastErrorMessage = "";
        this.userId = userId;
        String t   = title        == null ? ""    : title.trim();
        String st  = fraStatus    == null ? "all" : fraStatus.trim();
        String cat = categoryName == null ? "all" : categoryName.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, IFNULL(TRIM(fc.category_name),'') AS category_name, "
                + "IF(ff.fra_id IS NULL, 0, 1) AS saved "
                + "FROM fra f "
                + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                + "LEFT JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                + "WHERE 1=1 ");
        if (!t.isEmpty())              sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        if (!"all".equalsIgnoreCase(st))  sql.append("AND f.fra_status = ? ");
        if (!"all".equalsIgnoreCase(cat)) sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        sql.append("ORDER BY f.fra_id LIMIT 500");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (!t.isEmpty())              ps.setString(i++, "%" + t + "%");
            if (!"all".equalsIgnoreCase(st))  ps.setString(i++, st);
            if (!"all".equalsIgnoreCase(cat)) ps.setString(i++, cat);
            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id",   rs.getInt("fra_id"));
                    m.put("title",    rs.getString("fra_title"));
                    m.put("status",   rs.getString("fra_status"));
                    m.put("category", rs.getString("category_name"));
                    m.put("saved",    rs.getInt("saved") != 0);
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    // View Fra
    public Object getViewFra(int fraId) {
        lastErrorMessage = "";
        this.fraId = fraId;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
                     + "f.current_amount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '—') AS doneeName "
                     + "FROM fra f "
                     + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                     + "WHERE f.fra_id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("title",          rs.getString("fra_title"));
                    out.put("status",         rs.getString("fra_status"));
                    out.put("createAt",       rs.getString("fra_createdAt"));
                    out.put("viewCount",      rs.getObject("fra_viewCount"));
                    out.put("favouriteCount", rs.getObject("fra_favouriteCount"));
                    out.put("currentAmount",  rs.getObject("current_amount"));
                    out.put("doneeName",      rs.getString("doneeName"));
                    return out;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return null;
    }

    // Save Favourite
    public boolean keepSaveFavourite(int fraId, int userId, boolean removing) {
        lastErrorMessage = "";
        this.fraId = fraId;
        this.userId = userId;
        if (fraId <= 0 || userId <= 0) { lastErrorMessage = "Invalid FRA or user."; return false; }
        if (!removing) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "SELECT 1 FROM fra_favourite WHERE user_id = ? AND fra_id = ? LIMIT 1")) {
                ps.setInt(1, userId); ps.setInt(2, fraId);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return true; }
            } catch (SQLException e) { lastErrorMessage = e.getMessage(); return false; }
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "INSERT INTO fra_favourite (user_id, fra_id) VALUES (?, ?)")) {
                ps.setInt(1, userId); ps.setInt(2, fraId);
                if (ps.executeUpdate() > 0) return true;
                lastErrorMessage = "Failed to save favourite.";
                return false;
            } catch (SQLException e) { lastErrorMessage = e.getMessage(); return false; }
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM fra_favourite WHERE user_id = ? AND fra_id = ?")) {
            ps.setInt(1, userId); ps.setInt(2, fraId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { lastErrorMessage = e.getMessage(); return false; }
    }

    // Search Favourite
    public Object getSearchFavourite(int userId, String title, String fraStatus, String categoryName) {
        lastErrorMessage = "";
        this.userId = userId;
        if (userId <= 0) return new ArrayList<>();
        String t   = title        == null ? ""    : title.trim();
        String st  = fraStatus    == null ? "all" : fraStatus.trim();
        String cat = categoryName == null ? "all" : categoryName.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, IFNULL(TRIM(fc.category_name),'') AS category_name "
                + "FROM fra f "
                + "INNER JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                + "WHERE 1=1 ");
        if (!t.isEmpty())              sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        if (!"all".equalsIgnoreCase(st))  sql.append("AND f.fra_status = ? ");
        if (!"all".equalsIgnoreCase(cat)) sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        sql.append("ORDER BY f.fra_id");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (!t.isEmpty())              ps.setString(i++, "%" + t + "%");
            if (!"all".equalsIgnoreCase(st))  ps.setString(i++, st);
            if (!"all".equalsIgnoreCase(cat)) ps.setString(i++, cat);
            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id",   rs.getInt("fra_id"));
                    m.put("title",    rs.getString("fra_title"));
                    m.put("status",   rs.getString("fra_status"));
                    m.put("category", rs.getString("category_name"));
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    // View Favourite
    public Object getViewFavourite(int fraId) {
        return getViewFra(fraId);
    }

    // Search Donation
    public Object getSearchDonation(int userId, String title, String categoryName, String fraStatus) {
        lastErrorMessage = "";
        this.userId = userId;
        if (userId <= 0) return new ArrayList<>();
        String t   = title        == null ? ""    : title.trim();
        String cat = categoryName == null ? "all" : categoryName.trim();
        String st  = fraStatus    == null ? "all" : fraStatus.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
                + "f.current_amount, f.fra_targetAmount, IFNULL(TRIM(fc.category_name),'') AS category_name, "
                + "COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '—') AS doneeName "
                + "FROM fra f "
                + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                + "WHERE f.donee_id = ? ");
        if (!t.isEmpty())              sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        if (!"all".equalsIgnoreCase(st))  sql.append("AND f.fra_status = ? ");
        if (!"all".equalsIgnoreCase(cat)) sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        sql.append("ORDER BY f.fra_id DESC LIMIT 2000");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (!t.isEmpty())              ps.setString(i++, "%" + t + "%");
            if (!"all".equalsIgnoreCase(st))  ps.setString(i++, st);
            if (!"all".equalsIgnoreCase(cat)) ps.setString(i++, cat);
            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double cur = rs.getDouble("current_amount");
                    double tgt = rs.getDouble("fra_targetAmount");
                    int progress = tgt > 0 ? (int) Math.min(100, Math.round(100.0 * cur / tgt)) : 0;
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id",          rs.getInt("fra_id"));
                    m.put("title",           rs.getString("fra_title"));
                    m.put("status",          rs.getString("fra_status"));
                    m.put("createAt",        rs.getString("fra_createdAt"));
                    m.put("viewCount",       rs.getObject("fra_viewCount"));
                    m.put("favouriteCount",  rs.getObject("fra_favouriteCount"));
                    m.put("currentAmount",   cur);
                    m.put("targetAmount",    tgt);
                    m.put("progressPercent", progress);
                    m.put("category",        rs.getString("category_name"));
                    m.put("doneeName",       rs.getString("doneeName"));
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    // View Donation
    public Object getViewDonation(int fraId) {
        return getViewFra(fraId);
    }
}
