package com.uow.fra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.uow.util.DBUtils;

public class FRA {
    private String fraId;
    private String categoryId;
    private String fraTitle;
    private Double fraTargetAmount;
    private String fraStatus;
    
    private String startedAt;
    private String createdAt; 
    private String endedAt;   
    private String doneeId;
    private String doneeName;
    private String fundRaiserId;
    private String fundRaiserName;

    /** Joined category label for API lists (not persisted on entity). */
    private String categoryName;

    private String completedAt;
    private String updatedAt;

    private double currentAmount = 0.0;
    private int viewCount = 0;
    private int favoriteCount = 0;

    public FRA() {}

    public FRA(String fraId, String title, Double target, String categoryId, String status, double currentAmount, int viewCount, int favoriteCount, String createdAt, String endedAt, String doneeId, String doneeName, String fundRaiserId, String fundRaiserName, String completedAt, String updatedAt) {
        this.fraId = fraId;
        this.fraTitle = title;
        this.fraTargetAmount = target;
        this.categoryId = categoryId;
        this.fraStatus = "Completed".equalsIgnoreCase(status) ? "Completed" : "Pending"; 
        this.currentAmount = currentAmount;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.startedAt = createdAt;
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.doneeId = doneeId;
        this.doneeName = doneeName;
        this.fundRaiserId = fundRaiserId;
        this.fundRaiserName = fundRaiserName;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
    }

    public String getFraId() { return fraId; }
    public void setFraId(String fraId) { this.fraId = fraId; }
    public String getFraTitle() { return fraTitle; }
    public void setFraTitle(String fraTitle) { this.fraTitle = fraTitle; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public Double getFraTargetAmount() { return fraTargetAmount; }
    public void setFraTargetAmount(Double fraTargetAmount) { this.fraTargetAmount = fraTargetAmount; }
    public String getFraStatus() { return fraStatus; }
    public void setFraStatus(String fraStatus) { this.fraStatus = "Completed".equalsIgnoreCase(fraStatus) ? "Completed" : "Pending"; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public String getStartedAt() { return startedAt; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getEndedAt() { return endedAt; }
    public void setEndedAt(String endedAt) { this.endedAt = endedAt; }
    public String getDoneeId() { return doneeId; }
    public void setDoneeId(String doneeId) { this.doneeId = doneeId; }
    public String getDoneeName() { return doneeName; }
    public void setDoneeName(String doneeName) { this.doneeName = doneeName; }
    public String getFundRaiserId() { return fundRaiserId; }
    public void setFundRaiserId(String fundRaiserId) { this.fundRaiserId = fundRaiserId; }
    public String getFundRaiserName() { return fundRaiserName; }
    public void setFundRaiserName(String fundRaiserName) { this.fundRaiserName = fundRaiserName; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public FRA saveFRA() {
        String sql = "INSERT INTO fra (fra_title, fra_targetAmount, category_id, fra_status, current_amount, fra_viewCount, fra_favouriteCount, fra_startedAt, fra_endedAt, donee_id, fundRaiser_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, this.fraTitle);
                pstmt.setDouble(2, this.fraTargetAmount);
                pstmt.setString(3, this.categoryId);
                pstmt.setString(4, "Pending");
                pstmt.setDouble(5, 0.0);
                pstmt.setInt(6, 0);
                pstmt.setInt(7, 0);
                pstmt.setString(8, this.startedAt);
                pstmt.setString(9, this.endedAt);
                pstmt.setString(10, this.doneeId);
                pstmt.setString(11, this.fundRaiserId);
            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) this.fraId = String.valueOf(rs.getInt(1));
                System.out.println("[FRA.saveFRA] FRA created successfully with ID: " + this.fraId);
                return this; 
            }
            System.err.println("[FRA.saveFRA] Insert failed - no rows updated");
            return null;
        } catch (SQLException e) { 
            System.err.println("[FRA.saveFRA] Database error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<FRA> findFRAsByCriteria(String criteria, String categoryName, String fraStatus) {
        List<FRA> list = new ArrayList<>();
        String crit = criteria == null ? "" : criteria.trim();
        String cat = categoryName == null ? "" : categoryName.trim();
        String stat = fraStatus == null ? "" : fraStatus.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name, "
                        + "IFNULL(TRIM(fc.category_name), '') AS category_name "
                        + "FROM fra f "
                        + "LEFT JOIN user_account d ON f.donee_id = d.user_id "
                        + "LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (!crit.isBlank()) {
            sql.append("AND f.fra_title LIKE ? ");
            params.add("%" + crit + "%");
        }
        if (!stat.isBlank() && !"all".equalsIgnoreCase(stat)) {
            sql.append("AND f.fra_status = ? ");
            params.add(stat);
        }
        if (!cat.isBlank() && !"all".equalsIgnoreCase(cat)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
            params.add(cat);
        }
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            // Silent handling: return an empty list
        }
        return list;
    }

    private static FRA fromResultSet(ResultSet rs) throws SQLException {
        int favouriteCount;
        String startedAt;
        String completedAt;
        String updatedAt;
        try {
            favouriteCount = rs.getInt("fra_favouriteCount");
        } catch (SQLException e) {
            favouriteCount = rs.getInt("fra_favoriteCount");
        }
        try {
            startedAt = rs.getString("fra_startedAt");
        } catch (SQLException e) {
            startedAt = rs.getString("fra_createdAt");
        }
        try {
            completedAt = rs.getString("completed_at");
        } catch (SQLException e) {
            completedAt = "";
        }
        try {
            updatedAt = rs.getString("updated_at");
        } catch (SQLException e) {
            updatedAt = "";
        }
        FRA fra = new FRA(
            rs.getString("fra_id"), rs.getString("fra_title"), rs.getDouble("fra_targetAmount"), rs.getString("category_id"),
            rs.getString("fra_status"), rs.getDouble("current_amount"), rs.getInt("fra_viewCount"), favouriteCount,
            rs.getString("fra_createdAt"), rs.getString("fra_endedAt"), rs.getString("donee_id"), rs.getString("donee_name"),
            rs.getString("fundRaiser_id"), rs.getString("fundRaiser_name"), completedAt, updatedAt
        );
        fra.setStartedAt(startedAt);
        try {
            String cn = rs.getString("category_name");
            fra.setCategoryName(cn != null ? cn.trim() : "");
        } catch (SQLException e) {
            fra.setCategoryName("");
        }
        return fra;
    }

    /**
     * All rows in {@code fra_category} with Active status (id + name), for search dropdowns.
     * Matches platform category management so names like Sports Development always appear when Active.
     */
    public static List<Map<String, Object>> findAllActiveFraCategoriesForDropdown() {
        List<Map<String, Object>> out = new ArrayList<>();
        String sql = "SELECT category_id AS category_id, TRIM(category_name) AS category_name "
                + "FROM fra_category "
                + "WHERE LOWER(TRIM(IFNULL(category_status, ''))) = 'active' "
                + "AND TRIM(IFNULL(category_name, '')) <> '' "
                + "ORDER BY category_name";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("categoryID", rs.getInt("category_id"));
                row.put("categoryName", rs.getString("category_name"));
                out.add(row);
            }
        } catch (SQLException e) {
            return List.of();
        }
        return out;
    }

    public static List<FRA> findFRAsForViewEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.fra_viewCount DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findFRAsForFavoriteEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.fra_favouriteCount DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findCompletedFRAsByCategoryAndDatePeriod(String categoryId, String startDate, String endDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE LOWER(f.fra_status) = 'completed'");
        List<Object> params = new ArrayList<>();
        if (categoryId != null && !categoryId.isBlank()) { sql.append(" AND f.category_id = ?"); params.add(categoryId); }
        if (startDate != null && !startDate.isBlank()) { sql.append(" AND f.fra_endedAt >= ?"); params.add(startDate); }
        if (endDate != null && !endDate.isBlank()) { sql.append(" AND f.fra_endedAt <= ?"); params.add(endDate); }
        sql.append(" ORDER BY f.fra_endedAt DESC");
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(fromResultSet(rs));
            }
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findAllFRAs() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    // Key update: this method now also returns a boolean
    public static Object getSearchDonation(Object searchDonationData) {
        if (!(searchDonationData instanceof Map<?, ?> data)) {
            return List.of();
        }

        int userId = parseInt(data.get("userId"));
        String title = parseText(data.get("title"));
        String fraStatus = parseText(data.get("fraStatus"));
        String categoryName = parseText(data.get("categoryName"));
        String startDate = parseText(data.get("startDate"));
        String endDate = parseText(data.get("endDate"));

        StringBuilder sql = new StringBuilder(
            "SELECT f.fra_id, f.fra_title, f.fra_status, f.category_id, f.current_amount, f.fra_targetAmount, "
                + "IFNULL(TRIM(fc.category_name), '') AS category_name "
                + "FROM fra f "
                + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                + "WHERE f.donee_id = ? "
                + "AND 1=1 "
        );
        if (!title.isBlank()) {
            sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) ");
        }
        if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) {
            sql.append("AND f.fra_status = ? ");
        }
        if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) {
            sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        }
        /* Date range on campaign window (not fra_createdAt). */
        if (!startDate.isBlank() && !endDate.isBlank()) {
            /* Overlap: [campaignStart, campaignEnd] intersects [startDate, endDate] */
            sql.append("AND DATE(COALESCE(f.fra_startedAt, f.fra_createdAt)) <= DATE(?) ");
            sql.append("AND DATE(COALESCE(f.fra_endedAt, f.fra_startedAt, f.fra_createdAt)) >= DATE(?) ");
        } else if (!startDate.isBlank()) {
            /* Only "from": campaigns that start on or after this date */
            sql.append("AND DATE(COALESCE(f.fra_startedAt, f.fra_createdAt)) >= DATE(?) ");
        } else if (!endDate.isBlank()) {
            /* Only "to": campaigns that start on or before this date */
            sql.append("AND DATE(COALESCE(f.fra_startedAt, f.fra_createdAt)) <= DATE(?) ");
        }
        sql.append("ORDER BY f.fra_id");

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int i = 1;
            pstmt.setInt(i++, userId);
            if (!title.isBlank()) {
                pstmt.setString(i++, "%" + title + "%");
            }
            if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) {
                pstmt.setString(i++, fraStatus);
            }
            if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) {
                pstmt.setString(i++, categoryName);
            }
            if (!startDate.isBlank() && !endDate.isBlank()) {
                pstmt.setString(i++, endDate);
                pstmt.setString(i++, startDate);
            } else if (!startDate.isBlank()) {
                pstmt.setString(i++, startDate);
            } else if (!endDate.isBlank()) {
                pstmt.setString(i++, endDate);
            }

            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fra_id", rs.getInt("fra_id"));
                    row.put("title", rs.getString("fra_title"));
                    row.put("status", rs.getString("fra_status"));
                    row.put("categoryId", rs.getObject("category_id"));
                    row.put("currentAmount", rs.getObject("current_amount"));
                    row.put("targetAmount", rs.getObject("fra_targetAmount"));
                    row.put("category", rs.getString("category_name"));
                    out.add(row);
                }
            }
            return out;
        } catch (SQLException e) {
            return List.of();
        }
    }

    /**
     * Reads a DATETIME/TIMESTAMP column as a display string (MySQL + JDBC often return null from {@link ResultSet#getString} for these types).
     */
    public static String readResultSetDateTime(ResultSet rs, String columnLabel) {
        try {
            Timestamp ts = rs.getTimestamp(columnLabel);
            if (ts != null) {
                return ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (SQLException ignored) {
        }
        try {
            String s = rs.getString(columnLabel);
            return (s == null || s.isBlank()) ? null : s.trim();
        } catch (SQLException e) {
            return null;
        }
    }

    /** Tries labels in order until a non-blank value is found (handles alias / naming differences). */
    public static String readResultSetDateTimeFirst(ResultSet rs, String... columnLabels) {
        for (String label : columnLabels) {
            String v = readResultSetDateTime(rs, label);
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    public static Object getViewDonation(int fraId) {
        String sql =
            "SELECT f.fra_title, f.fra_status, "
                + "f.fra_startedAt AS dt_start, f.fra_endedAt AS dt_end, f.fra_createdAt AS dt_create, "
                + "f.fra_viewCount, f.fra_favouriteCount, "
                + "f.current_amount AS current_amount, f.fra_targetAmount AS target_amount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '-') AS doneeName "
                + "FROM fra f "
                + "LEFT JOIN user_account ua ON f.donee_id = ua.user_id "
                + "WHERE f.fra_id = ? LIMIT 1";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fraId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("title", rs.getString("fra_title"));
                out.put("status", rs.getString("fra_status"));
                String started = readResultSetDateTimeFirst(rs, "dt_start", "fra_startedAt", "fra_startedat");
                if (started == null) {
                    started = readResultSetDateTimeFirst(rs, "dt_create", "fra_createdAt", "fra_createdat");
                }
                out.put("startedAt", started);
                out.put("endedAt", readResultSetDateTimeFirst(rs, "dt_end", "fra_endedAt", "fra_endedat"));
                out.put("viewCount", rs.getObject("fra_viewCount"));
                int favCount;
                try {
                    favCount = rs.getInt("fra_favouriteCount");
                } catch (SQLException e) {
                    favCount = rs.getInt("fra_favoriteCount");
                }
                out.put("favouriteCount", favCount);
                out.put("currentAmount", rs.getObject("current_amount"));
                out.put("targetAmount", rs.getObject("target_amount"));
                out.put("doneeName", rs.getString("doneeName"));
                return out;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    private static String parseText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static int parseInt(Object value) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(parseText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean updateFRAData() {
        String sql = "UPDATE fra SET fra_title = ?, fra_targetAmount = ?, category_id = ?, fra_status = ?, fra_startedAt = ?, fra_endedAt = ?, donee_id = ?, fundRaiser_id = ?, completed_at = ?, updated_at = ? WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount);
            pstmt.setString(3, this.categoryId);
            pstmt.setString(4, this.fraStatus);
            pstmt.setString(5, this.startedAt == null || this.startedAt.isBlank() ? this.createdAt : this.startedAt);
            pstmt.setString(6, this.endedAt);
            pstmt.setString(7, this.doneeId);
            pstmt.setString(8, this.fundRaiserId);
            pstmt.setString(9, this.completedAt);
            pstmt.setString(10, this.updatedAt);
            pstmt.setString(11, this.fraId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false; 
        }
    }

    public boolean removeFRA() {
        String deleteFavSql = "DELETE FROM fra_favourite WHERE fra_id = ?";
        String deleteFraSql = "DELETE FROM fra WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection()) {
            try (PreparedStatement pstmtFav = conn.prepareStatement(deleteFavSql)) {
                pstmtFav.setString(1, this.fraId);
                pstmtFav.executeUpdate(); 
            }
            try (PreparedStatement pstmtFra = conn.prepareStatement(deleteFraSql)) {
                pstmtFra.setString(1, this.fraId);
                return pstmtFra.executeUpdate() > 0; 
            }
        } catch (SQLException e) { 
            return false; 
        }
    }

    @JsonIgnore
    public Object getDailyFraStats() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalFraCount", 0);
        out.put("completedCount", 0);
        out.put("pendingCount", 0);
        out.put("totalViewCount", 0);
        out.put("totalFavouriteCount", 0);
        out.put("fraByCategory", new ArrayList<Map<String, Object>>());
        String filter = "DATE(f.fra_createdAt) = CURDATE()";
        try (Connection conn = DBUtils.getConnection()) {
            String summarySql =
                "SELECT COUNT(*) AS totalFraCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='completed' THEN 1 ELSE 0 END) AS completedCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='pending' THEN 1 ELSE 0 END) AS pendingCount, "
                    + "SUM(IFNULL(f.fra_viewCount, 0)) AS totalViewCount, "
                    + "SUM(IFNULL(f.fra_favouriteCount, 0)) AS totalFavouriteCount "
                    + "FROM fra f WHERE " + filter;
            try (PreparedStatement ps = conn.prepareStatement(summarySql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalFraCount", rs.getInt("totalFraCount"));
                    out.put("completedCount", rs.getInt("completedCount"));
                    out.put("pendingCount", rs.getInt("pendingCount"));
                    out.put("totalViewCount", rs.getLong("totalViewCount"));
                    out.put("totalFavouriteCount", rs.getLong("totalFavouriteCount"));
                }
            }

            List<Map<String, Object>> byCategory = new ArrayList<>();
            String categorySql =
                "SELECT COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') AS categoryName, "
                    + "COUNT(*) AS fraCount "
                    + "FROM fra f "
                    + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                    + "WHERE " + filter + " "
                    + "GROUP BY COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') "
                    + "ORDER BY fraCount DESC, categoryName ASC";
            try (PreparedStatement ps = conn.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("categoryName", rs.getString("categoryName"));
                    row.put("count", rs.getInt("fraCount"));
                    byCategory.add(row);
                }
            }
            out.put("fraByCategory", byCategory);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }

    @JsonIgnore
    public Object getWeeklyFraStats() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalFraCount", 0);
        out.put("completedCount", 0);
        out.put("pendingCount", 0);
        out.put("totalViewCount", 0);
        out.put("totalFavouriteCount", 0);
        out.put("fraByCategory", new ArrayList<Map<String, Object>>());
        String filter = "YEARWEEK(DATE(f.fra_createdAt), 1) = YEARWEEK(CURDATE(), 1)";
        try (Connection conn = DBUtils.getConnection()) {
            String summarySql =
                "SELECT COUNT(*) AS totalFraCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='completed' THEN 1 ELSE 0 END) AS completedCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='pending' THEN 1 ELSE 0 END) AS pendingCount, "
                    + "SUM(IFNULL(f.fra_viewCount, 0)) AS totalViewCount, "
                    + "SUM(IFNULL(f.fra_favouriteCount, 0)) AS totalFavouriteCount "
                    + "FROM fra f WHERE " + filter;
            try (PreparedStatement ps = conn.prepareStatement(summarySql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalFraCount", rs.getInt("totalFraCount"));
                    out.put("completedCount", rs.getInt("completedCount"));
                    out.put("pendingCount", rs.getInt("pendingCount"));
                    out.put("totalViewCount", rs.getLong("totalViewCount"));
                    out.put("totalFavouriteCount", rs.getLong("totalFavouriteCount"));
                }
            }

            List<Map<String, Object>> byCategory = new ArrayList<>();
            String categorySql =
                "SELECT COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') AS categoryName, "
                    + "COUNT(*) AS fraCount "
                    + "FROM fra f "
                    + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                    + "WHERE " + filter + " "
                    + "GROUP BY COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') "
                    + "ORDER BY fraCount DESC, categoryName ASC";
            try (PreparedStatement ps = conn.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("categoryName", rs.getString("categoryName"));
                    row.put("count", rs.getInt("fraCount"));
                    byCategory.add(row);
                }
            }
            out.put("fraByCategory", byCategory);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }

    @JsonIgnore
    public Object getMonthlyFraStats() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalFraCount", 0);
        out.put("completedCount", 0);
        out.put("pendingCount", 0);
        out.put("totalViewCount", 0);
        out.put("totalFavouriteCount", 0);
        out.put("fraByCategory", new ArrayList<Map<String, Object>>());
        String filter = "YEAR(DATE(f.fra_createdAt)) = YEAR(CURDATE()) AND MONTH(DATE(f.fra_createdAt)) = MONTH(CURDATE())";
        try (Connection conn = DBUtils.getConnection()) {
            String summarySql =
                "SELECT COUNT(*) AS totalFraCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='completed' THEN 1 ELSE 0 END) AS completedCount, "
                    + "SUM(CASE WHEN LOWER(TRIM(f.fra_status))='pending' THEN 1 ELSE 0 END) AS pendingCount, "
                    + "SUM(IFNULL(f.fra_viewCount, 0)) AS totalViewCount, "
                    + "SUM(IFNULL(f.fra_favouriteCount, 0)) AS totalFavouriteCount "
                    + "FROM fra f WHERE " + filter;
            try (PreparedStatement ps = conn.prepareStatement(summarySql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalFraCount", rs.getInt("totalFraCount"));
                    out.put("completedCount", rs.getInt("completedCount"));
                    out.put("pendingCount", rs.getInt("pendingCount"));
                    out.put("totalViewCount", rs.getLong("totalViewCount"));
                    out.put("totalFavouriteCount", rs.getLong("totalFavouriteCount"));
                }
            }

            List<Map<String, Object>> byCategory = new ArrayList<>();
            String categorySql =
                "SELECT COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') AS categoryName, "
                    + "COUNT(*) AS fraCount "
                    + "FROM fra f "
                    + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                    + "WHERE " + filter + " "
                    + "GROUP BY COALESCE(NULLIF(TRIM(fc.category_name),''), 'Uncategorized') "
                    + "ORDER BY fraCount DESC, categoryName ASC";
            try (PreparedStatement ps = conn.prepareStatement(categorySql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("categoryName", rs.getString("categoryName"));
                    row.put("count", rs.getInt("fraCount"));
                    byCategory.add(row);
                }
            }
            out.put("fraByCategory", byCategory);
        } catch (SQLException e) {
            return out;
        }
        return out;
    }
}