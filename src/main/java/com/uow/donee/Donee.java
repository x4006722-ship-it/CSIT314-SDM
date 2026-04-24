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

    //Search FRA
    public Object getSearchFra(int userId, String title, String fraStatus, String categoryName) {
        lastErrorMessage = "";
        this.userId = userId;
        this.title = title;
        this.fraStatus = fraStatus;
        this.categoryName = categoryName;

        String t = this.title == null ? "" : this.title.trim();
        String st = this.fraStatus == null ? "all" : this.fraStatus.trim();
        String cat = this.categoryName == null ? "all" : this.categoryName.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, IFNULL(TRIM(fc.category_name),'') AS category_name, "
                        + "IF(ff.fra_id IS NULL, 0, 1) AS saved "
                        + "FROM fra f "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "LEFT JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                        + "WHERE 1=1 ");
        if (!t.isEmpty()) {
            sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        }
        if (!"all".equalsIgnoreCase(st)) {
            sql.append("AND f.fra_status = ? ");
        }
        if (!"all".equalsIgnoreCase(cat)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        }
        sql.append("ORDER BY f.fra_id LIMIT 500");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, this.userId);
            if (!t.isEmpty()) {
                ps.setString(i++, "%" + t + "%");
            }
            if (!"all".equalsIgnoreCase(st)) {
                ps.setString(i++, st);
            }
            if (!"all".equalsIgnoreCase(cat)) {
                ps.setString(i++, cat.trim());
            }
            List<Object> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tStr = rs.getString("fra_title");
                    String rTitle = tStr == null || tStr.isBlank() ? "—" : tStr;
                    String sStr = rs.getString("fra_status");
                    String rStatus = sStr == null || sStr.isBlank() ? "—" : sStr;
                    String cStr = rs.getString("category_name");
                    String rCat = cStr == null || cStr.isBlank() ? "—" : cStr;
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id", rs.getInt("fra_id"));
                    m.put("title", rTitle);
                    m.put("status", rStatus);
                    m.put("category", rCat);
                    m.put("saved", rs.getInt("saved") != 0);
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    //View FRA
    public Object getViewFra(int fraId) {
        lastErrorMessage = "";
        this.fraId = fraId;

        String sql = "SELECT f.fra_id, f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
                + "f.current_amount, f.donee_id, ua.full_name, ua.username "
                + "FROM fra f "
                + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                + "WHERE f.fra_id = ? LIMIT 1";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, this.fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tStr = rs.getString("fra_title");
                    String vTitle = tStr == null || tStr.isBlank() ? "—" : tStr;
                    String stStr = rs.getString("fra_status");
                    String vSt = stStr == null || stStr.isBlank() ? "—" : stStr;
                    Object cAt = rs.getObject("fra_createdAt");
                    String createAt = cAt == null ? "—" : cAt.toString();
                    Object oVc = rs.getObject("fra_viewCount");
                    String vc = oVc == null ? "—" : oVc.toString();
                    Object oFc = rs.getObject("fra_favouriteCount");
                    String fco = oFc == null ? "—" : oFc.toString();
                    Object oCa = rs.getObject("current_amount");
                    String currentAmt = oCa == null ? "—" : oCa.toString();
                    String fn = rs.getString("full_name");
                    String un = rs.getString("username");
                    this.doneeName = (fn != null && !fn.isBlank()) ? fn.trim()
                            : (un != null && !un.isBlank() ? un.trim() : "—");
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("title", vTitle);
                    out.put("status", vSt);
                    out.put("createAt", createAt);
                    out.put("viewCount", vc);
                    out.put("favouriteCount", fco);
                    out.put("currentAmount", currentAmt);
                    out.put("doneeName", this.doneeName);
                    return out;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return null;
    }

    //Save Favourite
    public boolean keepSaveFavourite(int fraId, int userId, boolean removing) {
        lastErrorMessage = "";
        this.fraId = fraId;
        this.userId = userId;
        if (this.fraId <= 0 || this.userId <= 0) {
            lastErrorMessage = "Invalid FRA or user.";
            return false;
        }
        if (!removing) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "SELECT 1 FROM fra_favourite WHERE user_id = ? AND fra_id = ? LIMIT 1")) {
                ps.setInt(1, this.userId);
                ps.setInt(2, this.fraId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            } catch (SQLException e) {
                lastErrorMessage = e.getMessage();
                return false;
            }
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "INSERT INTO fra_favourite (user_id, fra_id) VALUES (?, ?)")) {
                ps.setInt(1, this.userId);
                ps.setInt(2, this.fraId);
                if (ps.executeUpdate() > 0) {
                    return true;
                }
                lastErrorMessage = "Failed to save favourite.";
                return false;
            } catch (SQLException e) {
                lastErrorMessage = e.getMessage();
                return false;
            }
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM fra_favourite WHERE user_id = ? AND fra_id = ?")) {
            ps.setInt(1, this.userId);
            ps.setInt(2, this.fraId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    //Search Favourite
    public Object getSearchFavourite(int userId, String title, String fraStatus, String categoryName) {
        lastErrorMessage = "";
        this.userId = userId;
        this.title = title;
        this.fraStatus = fraStatus;
        this.categoryName = categoryName;

        if (this.userId <= 0) {
            return new ArrayList<>();
        }
        String t = this.title == null ? "" : this.title.trim();
        String st = this.fraStatus == null ? "all" : this.fraStatus.trim();
        String cnm = this.categoryName == null ? "all" : this.categoryName.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, IFNULL(TRIM(fc.category_name),'') AS category_name "
                        + "FROM fra f "
                        + "INNER JOIN fra_favourite ff ON ff.fra_id = f.fra_id AND ff.user_id = ? "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE 1=1 ");
        if (!t.isEmpty()) {
            sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        }
        if (!"all".equalsIgnoreCase(st)) {
            sql.append("AND f.fra_status = ? ");
        }
        if (!"all".equalsIgnoreCase(cnm)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        }
        sql.append("ORDER BY f.fra_id");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, this.userId);
            if (!t.isEmpty()) {
                ps.setString(i++, "%" + t + "%");
            }
            if (!"all".equalsIgnoreCase(st)) {
                ps.setString(i++, st);
            }
            if (!"all".equalsIgnoreCase(cnm)) {
                ps.setString(i++, cnm.trim());
            }
            List<Object> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String tStr = rs.getString("fra_title");
                    String rTitle = tStr == null || tStr.isBlank() ? "—" : tStr;
                    String sStr = rs.getString("fra_status");
                    String rStatus = sStr == null || sStr.isBlank() ? "—" : sStr;
                    String cStr = rs.getString("category_name");
                    String rCat = cStr == null || cStr.isBlank() ? "—" : cStr;
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id", rs.getInt("fra_id"));
                    m.put("title", rTitle);
                    m.put("status", rStatus);
                    m.put("category", rCat);
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    //View Favourite
    public Object getViewFavourite(int fraId) {
        return getViewFra(fraId);
    }

    //Search Donation
    public Object getSearchDonation(int userId, String title, String categoryName, String fraStatus) {
        lastErrorMessage = "";
        this.userId = userId;
        this.title = title;
        this.categoryName = categoryName;
        this.fraStatus = fraStatus;

        if (this.userId <= 0) {
            return new ArrayList<>();
        }
        String t = this.title == null ? "" : this.title.trim();
        String stF = this.fraStatus == null ? "all" : this.fraStatus.trim();
        String catF = this.categoryName == null ? "all" : this.categoryName.trim();

        this.doneeName = "—";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT full_name, username FROM user_account WHERE user_id = ? LIMIT 1")) {
            ps.setInt(1, this.userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fn = rs.getString(1);
                    String un = rs.getString(2);
                    this.doneeName = (fn != null && !fn.isBlank()) ? fn.trim()
                            : (un != null && !un.isBlank() ? un.trim() : "—");
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }

        StringBuilder sql = new StringBuilder(
                "SELECT f.fra_id, f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
                        + "f.current_amount, f.fra_targetAmount, IFNULL(TRIM(fc.category_name),'') AS category_name "
                        + "FROM fra f "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE f.donee_id = ? ");
        if (!t.isEmpty()) {
            sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        }
        if (!"all".equalsIgnoreCase(stF)) {
            sql.append("AND f.fra_status = ? ");
        }
        if (!"all".equalsIgnoreCase(catF)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        }
        sql.append("ORDER BY f.fra_id DESC LIMIT 2000");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setString(i++, String.valueOf(this.userId));
            if (!t.isEmpty()) {
                ps.setString(i++, "%" + t + "%");
            }
            if (!"all".equalsIgnoreCase(stF)) {
                ps.setString(i++, stF);
            }
            if (!"all".equalsIgnoreCase(catF)) {
                ps.setString(i++, catF.trim());
            }
            List<Object> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double cur = rs.getDouble("current_amount");
                    double tgt = rs.getDouble("fra_targetAmount");
                    int progress = tgt > 0 ? (int) Math.min(100, Math.round(100.0 * cur / tgt)) : 0;
                    Object co = rs.getObject("fra_createdAt");
                    String createdStr = co == null ? "—" : co.toString();
                    String tStr = rs.getString("fra_title");
                    String rTitle = tStr == null || tStr.isBlank() ? "—" : tStr;
                    String sStr = rs.getString("fra_status");
                    String rStatus = sStr == null || sStr.isBlank() ? "—" : sStr;
                    Object oVc = rs.getObject("fra_viewCount");
                    String vc = oVc == null ? "—" : oVc.toString();
                    Object oFav = rs.getObject("fra_favouriteCount");
                    String favc = oFav == null ? "—" : oFav.toString();
                    String curStr = Double.isNaN(cur) ? "—" : String.valueOf(cur);
                    String tgtStr = Double.isNaN(tgt) ? "—" : String.valueOf(tgt);
                    String catStr = rs.getString("category_name");
                    String rCat = catStr == null || catStr.isBlank() ? "—" : catStr;
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("fra_id", rs.getInt("fra_id"));
                    m.put("title", rTitle);
                    m.put("status", rStatus);
                    m.put("createAt", createdStr);
                    m.put("viewCount", vc);
                    m.put("favouriteCount", favc);
                    m.put("currentAmount", curStr);
                    m.put("targetAmount", tgtStr);
                    m.put("progressPercent", progress);
                    m.put("category", rCat);
                    m.put("doneeName", this.doneeName);
                    out.add(m);
                }
            }
            return out;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return new ArrayList<>();
        }
    }

    //View Donation
    public Object getViewDonation(int fraId) {
        return getViewFra(fraId);
    }
}
