package com.uow.fra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.uow.util.DBUtils;

public class FRA {
    // 12个核心属性
    private String fraId;
    private String fraTitle;
    private String fraStatus;
    private String categoryId;
    private String fraStartedAt;
    private String fraEndedAt;
    private double currentAmount = 0.0;
    private Double fraTargetAmount;
    private int viewCount = 0;
    private int favoriteCount = 0;
    private String doneeId;
    private String fundRaiserId;

    // 辅助显示属性（LEFT JOIN 查出来的）
    private String doneeName;
    private String fundRaiserName;

    public FRA() {}

    // 构造函数
    public FRA(String fraId, String title, Double target, String categoryId, String status, double currentAmount, int viewCount, int favoriteCount, String startedAt, String endedAt, String doneeId, String doneeName, String fundRaiserId, String fundRaiserName) {
        this.fraId = fraId;
        this.fraTitle = title;
        this.fraTargetAmount = target;
        this.categoryId = categoryId;
        this.fraStatus = status;
        this.currentAmount = currentAmount;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        this.fraStartedAt = startedAt;
        this.fraEndedAt = endedAt;
        this.doneeId = doneeId;
        this.doneeName = doneeName;
        this.fundRaiserId = fundRaiserId;
        this.fundRaiserName = fundRaiserName;
    }

    // --- 标准 Getters 和 Setters (严格对应你的字段名) ---
    public String getFraId() { return fraId; }
    public void setFraId(String fraId) { this.fraId = fraId; }
    public String getFraTitle() { return fraTitle; }
    public void setFraTitle(String fraTitle) { this.fraTitle = fraTitle; }
    public String getFraStatus() { return fraStatus; }
    public void setFraStatus(String fraStatus) { this.fraStatus = fraStatus; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getStartedAt() { return fraStartedAt; }
    public void setStartedAt(String startedAt) { this.fraStartedAt = startedAt; }
    public String getEndedAt() { return fraEndedAt; }
    public void setEndedAt(String endedAt) { this.fraEndedAt = endedAt; }
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public Double getFraTargetAmount() { return fraTargetAmount; }
    public void setFraTargetAmount(Double target) { this.fraTargetAmount = target; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }
    public String getDoneeId() { return doneeId; }
    public void setDoneeId(String doneeId) { this.doneeId = doneeId; }
    public String getDoneeName() { return doneeName; }
    public void setDoneeName(String doneeName) { this.doneeName = doneeName; }
    public String getFundRaiserId() { return fundRaiserId; }
    public void setFundRaiserId(String fundRaiserId) { this.fundRaiserId = fundRaiserId; }
    public String getFundRaiserName() { return fundRaiserName; }
    public void setFundRaiserName(String fundRaiserName) { this.fundRaiserName = fundRaiserName; }

    // 1. saveFRA
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
            pstmt.setString(8, this.fraStartedAt);
            pstmt.setString(9, this.fraEndedAt);
            pstmt.setString(10, this.doneeId);
            pstmt.setString(11, this.fundRaiserId);
            if (pstmt.executeUpdate() > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) this.fraId = String.valueOf(rs.getInt(1));
                return this; 
            }
            return null;
        } catch (SQLException e) { return null; }
    }

    // 2. updateFRAData
    public boolean updateFRAData() {
        String sql = "UPDATE fra SET fra_title = ?, fra_targetAmount = ?, category_id = ?, fra_status = ?, fra_startedAt = ?, fra_endedAt = ?, donee_id = ?, fundRaiser_id = ? WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount);
            pstmt.setString(3, this.categoryId);
            pstmt.setString(4, this.fraStatus);
            pstmt.setString(5, this.fraStartedAt);
            pstmt.setString(6, this.fraEndedAt);
            pstmt.setString(7, this.doneeId);
            pstmt.setString(8, this.fundRaiserId);
            pstmt.setString(9, this.fraId); 
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            return false; 
        }
    }

    // 3. findFRAsByCriteria
    public static List<FRA> findFRAsByCriteria(String criteria, String categoryId, String status, String role, String userId, String startDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id LEFT JOIN fra_category fc ON f.category_id = fc.category_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (criteria != null && !criteria.isBlank()) { sql.append(" AND f.fra_title LIKE ?"); params.add("%" + criteria + "%"); }
        if (categoryId != null && !categoryId.isBlank() && !"all".equalsIgnoreCase(categoryId)) { sql.append(" AND (f.category_id = ? OR fc.category_name = ?)"); params.add(categoryId); params.add(categoryId); }
        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) { sql.append(" AND f.fra_status = ?"); params.add(status); }
        if (startDate != null && !startDate.isBlank()) { sql.append(" AND DATE(f.fra_startedAt) = ?"); params.add(startDate); }
        if ("donee".equalsIgnoreCase(role)) { sql.append(" AND f.fra_status IN ('Pending', 'Completed')"); }
        else if ("fundRaiser".equalsIgnoreCase(role) && userId != null) { sql.append(" AND f.fundRaiser_id = ?"); params.add(userId); }
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    // 4. fromResultSet
    private static FRA fromResultSet(ResultSet rs) throws SQLException {
        int favoriteCount;
        try { favoriteCount = rs.getInt("fra_favouriteCount"); } catch (SQLException e) { favoriteCount = rs.getInt("fra_favoriteCount"); }
        return new FRA(
            rs.getString("fra_id"), rs.getString("fra_title"), rs.getDouble("fra_targetAmount"), rs.getString("category_id"),
            rs.getString("fra_status"), rs.getDouble("current_amount"), rs.getInt("fra_viewCount"), favoriteCount,
            rs.getString("fra_startedAt"), rs.getString("fra_endedAt"), rs.getString("donee_id"), rs.getString("donee_name"),
            rs.getString("fundRaiser_id"), rs.getString("fundRaiser_name")
        );
    }

    // 5. findFRAsForViewEngagementReport
    public static List<FRA> findFRAsForViewEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.fra_viewCount DESC";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    // 6. findFRAsForFavoriteEngagementReport
    public static List<FRA> findFRAsForFavoriteEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.fra_favouriteCount DESC";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    // 7. findCompletedFRAsByCategoryAndDatePeriod
    public static List<FRA> findCompletedFRAsByCategoryAndDatePeriod(String categoryId, String startDate, String endDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE LOWER(f.fra_status) = 'completed'");
        List<Object> params = new ArrayList<>();
        if (categoryId != null && !categoryId.isBlank()) { sql.append(" AND f.category_id = ?"); params.add(categoryId); }
        if (startDate != null && !startDate.isBlank()) { sql.append(" AND f.fra_endedAt >= ?"); params.add(startDate); }
        if (endDate != null && !endDate.isBlank()) { sql.append(" AND f.fra_endedAt <= ?"); params.add(endDate); }
        sql.append(" ORDER BY f.fra_endedAt DESC");
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    // 8. findAllFRAs
    public static List<FRA> findAllFRAs(String fundRaiserId) {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE f.fundRaiser_id = ?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fundRaiserId);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    // 9. getSearchDonation
    public static Object getSearchDonation(Object searchDonationData) {
        if (!(searchDonationData instanceof Map<?, ?> data)) return List.of();
        int userId = parseInt(data.get("userId"));
        String title = parseText(data.get("title"));
        String fraStatus = parseText(data.get("fraStatus"));
        String categoryName = parseText(data.get("categoryName"));
        String startDate = parseText(data.get("startDate"));
        String endDate = parseText(data.get("endDate"));
        StringBuilder sql = new StringBuilder("SELECT f.fra_id, f.fra_title, f.fra_status, f.current_amount, f.fra_targetAmount, IFNULL(TRIM(fc.category_name), '') AS category_name FROM fra f LEFT JOIN fra_category fc ON f.category_id = fc.category_id WHERE f.donee_id = ? AND 1=1 ");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (!title.isBlank()) { sql.append("AND LOWER(f.fra_title) LIKE LOWER(?) "); params.add("%" + title + "%"); }
        if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) { sql.append("AND f.fra_status = ? "); params.add(fraStatus); }
        if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) { sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) "); params.add(categoryName); }
        if (!startDate.isBlank()) { sql.append("AND DATE(f.fra_startedAt) >= ? "); params.add(startDate); }
        if (!endDate.isBlank()) { sql.append("AND DATE(f.fra_startedAt) <= ? "); params.add(endDate); }
        sql.append("ORDER BY f.fra_id");
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fra_id", rs.getInt("fra_id")); row.put("title", rs.getString("fra_title")); row.put("status", rs.getString("fra_status"));
                    row.put("currentAmount", rs.getObject("current_amount")); row.put("targetAmount", rs.getObject("fra_targetAmount")); row.put("category", rs.getString("category_name"));
                    out.add(row);
                }
            }
            return out;
        } catch (SQLException e) { return List.of(); }
    }

    // 10. getViewDonation
    public static Object getViewDonation(int fraId) {
        String sql = "SELECT f.fra_title, f.fra_status, f.fra_startedAt, f.fra_viewCount, f.fra_favouriteCount, f.current_amount, f.fra_targetAmount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '-') AS doneeName FROM fra f LEFT JOIN user_account ua ON f.donee_id = ua.user_id WHERE f.fra_id = ? LIMIT 1";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fraId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("title", rs.getString("fra_title")); out.put("status", rs.getString("fra_status")); out.put("createAt", rs.getString("fra_startedAt"));
                out.put("viewCount", rs.getObject("fra_viewCount")); out.put("favouriteCount", rs.getObject("fra_favouriteCount"));
                out.put("currentAmount", rs.getObject("current_amount")); out.put("targetAmount", rs.getObject("fra_targetAmount")); out.put("doneeName", rs.getString("doneeName"));
                return out;
            }
        } catch (SQLException e) { return null; }
    }

    // 11. removeFRA
    public boolean removeFRA() {
        try (Connection conn = DBUtils.getConnection()) {
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM fra_favourite WHERE fra_id = ?")) { p1.setString(1, this.fraId); p1.executeUpdate(); }
            try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM fra WHERE fra_id = ?")) { p2.setString(1, this.fraId); return p2.executeUpdate() > 0; }
        } catch (SQLException e) { return false; }
    }

    // 12-14. Stats
    @JsonIgnore public Object getDailyFraStats() { return getStatsByFilter("DATE(f.fra_startedAt) = CURDATE()"); }
    @JsonIgnore public Object getWeeklyFraStats() { return getStatsByFilter("YEARWEEK(DATE(f.fra_startedAt), 1) = YEARWEEK(CURDATE(), 1)"); }
    @JsonIgnore public Object getMonthlyFraStats() { return getStatsByFilter("YEAR(DATE(f.fra_startedAt)) = YEAR(CURDATE()) AND MONTH(DATE(f.fra_startedAt)) = MONTH(CURDATE())"); }

    private Object getStatsByFilter(String filter) {
        Map<String, Object> out = new LinkedHashMap<>();
        try (Connection conn = DBUtils.getConnection()) {
            String sql = "SELECT COUNT(*) as total FROM fra f WHERE " + filter;
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) out.put("totalFraCount", rs.getInt("total"));
            }
        } catch (SQLException e) { }
        return out;
    }

    // 15. Duplicate Check
    public static boolean isDuplicateTitle(String title, String excludeFraId) {
        String sql = "SELECT 1 FROM fra WHERE LOWER(TRIM(fra_title)) = LOWER(TRIM(?))" + (excludeFraId != null ? " AND fra_id != ?" : "") + " LIMIT 1";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title); if (excludeFraId != null) pstmt.setString(2, excludeFraId);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { return false; }
    }

    // Utils
    private static String parseText(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
    private static int parseInt(Object v) { if (v instanceof Number n) return n.intValue(); try { return Integer.parseInt(parseText(v)); } catch (Exception e) { return 0; } }
}