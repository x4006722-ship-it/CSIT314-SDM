package com.uow.fra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.uow.util.DBUtils;

public class FRA {
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

    private String doneeName;              
    private String fundRaiserName;         

    public FRA() {}

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

    public FRA saveFRA() {
        String sql = "INSERT INTO fra (title, target_amount, category_id, fra_status, current_amount, viewCount, favoriteCount, startedAt, endedAt, donee_id, fundRaiser_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount != null ? this.fraTargetAmount : 0.0);
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
        } catch (SQLException e) {
            e.printStackTrace(); 
            return null;
        }
    }

    public boolean updateFRAData() {
        String sql = "UPDATE fra SET title = ?, target_amount = ?, category_id = ?, fra_status = ?, startedAt = ?, endedAt = ? WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount);
            pstmt.setString(3, this.categoryId);
            pstmt.setString(4, this.fraStatus);
            pstmt.setString(5, this.fraStartedAt);
            pstmt.setString(6, this.fraEndedAt);
            pstmt.setString(7, this.fraId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public static List<FRA> findFRAsByCriteria(String criteria, String categoryId, String status, String role, String userId, String startDate, String endDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id LEFT JOIN fra_category fc ON f.category_id = fc.category_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (criteria != null) { sql.append(" AND f.title LIKE ?"); params.add("%" + criteria + "%"); }
        if (categoryId != null) { sql.append(" AND (f.category_id = ? OR fc.category_name = ?)"); params.add(categoryId); params.add(categoryId); }
        if (status != null) { sql.append(" AND f.fra_status = ?"); params.add(status); }
        
        if (startDate != null) { 
            sql.append(" AND DATE(f.startedAt) >= ?"); 
            params.add(startDate); 
        }
        if (endDate != null) { 
            sql.append(" AND DATE(f.startedAt) <= ?"); 
            params.add(endDate); 
        }
        
        if ("donee".equalsIgnoreCase(role)) { sql.append(" AND f.fra_status IN ('Pending', 'Completed')"); }
        else if ("fundRaiser".equalsIgnoreCase(role) && userId != null) { sql.append(" AND f.fundRaiser_id = ?"); params.add(userId); }
        
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    private static FRA fromResultSet(ResultSet rs) throws SQLException {
        return new FRA(
            rs.getString("fra_id"), rs.getString("title"), rs.getDouble("target_amount"), rs.getString("category_id"),
            rs.getString("fra_status"), rs.getDouble("current_amount"), rs.getInt("viewCount"), rs.getInt("favoriteCount"),
            rs.getString("startedAt"), rs.getString("endedAt"), rs.getString("donee_id"), rs.getString("donee_name"),
            rs.getString("fundRaiser_id"), rs.getString("fundRaiser_name")
        );
    }

    public static List<FRA> findFRAsForViewEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.viewCount DESC";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findFRAsForFavoriteEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id ORDER BY f.favoriteCount DESC";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(fromResultSet(rs));
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findCompletedFRAsByCategoryAndDatePeriod(String categoryId, String startDate, String endDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE LOWER(f.fra_status) = 'completed'");
        List<Object> params = new ArrayList<>();
    
        if (categoryId != null && !categoryId.isBlank()) { sql.append(" AND f.category_id = ?"); params.add(categoryId); }
        if (startDate != null && !startDate.isBlank()) { sql.append(" AND DATE(f.endedAt) >= ?"); params.add(startDate); }
        if (endDate != null && !endDate.isBlank()) { sql.append(" AND DATE(f.endedAt) <= ?"); params.add(endDate); }
        sql.append(" ORDER BY f.endedAt DESC");
        
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

    public static List<FRA> findAllFRAs(String fundRaiserId) {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE f.fundRaiser_id = ?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fundRaiserId);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(fromResultSet(rs)); }
        } catch (SQLException e) { }
        return list;
    }

   public static Object getSearchDonation(Object searchDonationData) {
    if (!(searchDonationData instanceof Map<?, ?> data)) {
        return List.of();
    }

    // 1. Extract and validate parameters
    int userId = parseInt(data.get("userId"));
    String title = parseText(data.get("title"));
    String fraStatus = parseText(data.get("fraStatus"));
    String categoryName = parseText(data.get("categoryName"));
    String startDate = parseText(data.get("startDate"));
    String endDate = parseText(data.get("endDate"));

    // 2. Build dynamic SQL query with proper joins and conditions
    StringBuilder sql = new StringBuilder(
        "SELECT f.fra_id, f.title, f.fra_status, f.current_amount, f.target_amount, " +
        "IFNULL(TRIM(fc.category_name), '') AS category_name " +
        "FROM fra f " +
        "LEFT JOIN fra_category fc ON f.category_id = fc.category_id " +
        "WHERE f.donee_id = ? " // Ensure the user only sees their own donations
    );

    List<Object> params = new ArrayList<>();
    params.add(userId);

    // 3. (Only add conditions if parameters are provided
    if (!title.isBlank()) {
        sql.append("AND LOWER(f.title) LIKE LOWER(?) ");
        params.add("%" + title + "%");
    }

    // status
    if (!fraStatus.isBlank() && !"all".equalsIgnoreCase(fraStatus)) {
        sql.append("AND f.fra_status = ? ");
        params.add(fraStatus);
    }

    // categoryName
    if (!categoryName.isBlank() && !"all".equalsIgnoreCase(categoryName)) {
        sql.append("AND TRIM(LOWER(fc.category_name)) = LOWER(?) ");
        params.add(categoryName);
    }
    // [startDate]  [endDate] 
    if (!startDate.isBlank()) {
        sql.append("AND DATE(f.startedAt) >= ? ");
        params.add(startDate);
    }
    if (!endDate.isBlank()) {
        sql.append("AND DATE(f.startedAt) <= ? ");
        params.add(endDate);
    }

    sql.append(" ORDER BY f.fra_id DESC");

    // 4. 执行数据库操作
    try (Connection conn = DBUtils.getConnection(); 
         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
        
        for (int i = 0; i < params.size(); i++) {
            pstmt.setObject(i + 1, params.get(i));
        }

        List<Map<String, Object>> out = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("fra_id", rs.getInt("fra_id")); 
                row.put("title", rs.getString("title"));
                row.put("status", rs.getString("fra_status"));
                row.put("currentAmount", rs.getObject("current_amount")); 
                row.put("targetAmount", rs.getObject("target_amount"));
                row.put("category", rs.getString("category_name"));
                out.add(row);
            }
        }
        return out;

    } catch (SQLException e) {
        e.printStackTrace(); //  Print error for debugging
        return List.of();
    }
}


    public static Object getViewDonation(int fraId) {
        String sql = "SELECT f.title, f.fra_status, f.startedAt, f.endedAt, f.viewCount, f.favoriteCount, f.current_amount, f.target_amount, COALESCE(NULLIF(TRIM(ua.full_name),''), ua.username, '-') AS doneeName FROM fra f LEFT JOIN user_account ua ON f.donee_id = ua.user_id WHERE f.fra_id = ? LIMIT 1";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fraId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                Map<String, Object> out = new LinkedHashMap<>();
                out.put("title", rs.getString("title")); 
                out.put("status", rs.getString("fra_status")); 
                out.put("createAt", rs.getString("startedAt"));
                out.put("endedAt", rs.getString("endedAt")); 
                out.put("viewCount", rs.getObject("viewCount")); 
                out.put("favouriteCount", rs.getObject("favoriteCount")); // Frontend uses favouriteCount
                out.put("currentAmount", rs.getObject("current_amount")); 
                out.put("targetAmount", rs.getObject("target_amount")); 
                out.put("doneeName", rs.getString("doneeName"));
                return out;
            }
        } catch (SQLException e) { return null; }
    }

    public boolean removeFRA() {
        // Logic: Delete associated records first to avoid Foreign Key violations
        try (Connection conn = DBUtils.getConnection()) {
            // Step 1: Clean up favorites associated with this FRA
            try (PreparedStatement p1 = conn.prepareStatement("DELETE FROM fra_favourite WHERE fra_id = ?")) {
                p1.setString(1, this.fraId);
                p1.executeUpdate();
            }
            
            // Step 2: Delete the FRA itself
            try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM fra WHERE fra_id = ?")) {
                p2.setString(1, this.fraId);
                int rows = p2.executeUpdate();
                return rows > 0; // Return true if at least one row was deleted
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Useful for debugging in console
            return false;
        }
    }

    @JsonIgnore public Object getDailyFraStats() { return getStatsByFilter("DATE(f.startedAt) = CURDATE()"); }
    @JsonIgnore public Object getWeeklyFraStats() { return getStatsByFilter("YEARWEEK(DATE(f.startedAt), 1) = YEARWEEK(CURDATE(), 1)"); }
    @JsonIgnore public Object getMonthlyFraStats() { return getStatsByFilter("YEAR(DATE(f.startedAt)) = YEAR(CURDATE()) AND MONTH(DATE(f.startedAt)) = MONTH(CURDATE())"); }

    private Object getStatsByFilter(String filter) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalFraCount", 0);
        out.put("totalTargetAmount", 0.0);
        out.put("totalCurrentAmount", 0.0);
        out.put("fraByCategory", new ArrayList<>());

        String whereClause = filter.isBlank() ? "" : " WHERE " + filter;

        try (Connection conn = DBUtils.getConnection()) {
            String sql1 = "SELECT COUNT(*) as total, SUM(target_amount) as totalTarget, SUM(current_amount) as totalCurrent FROM fra f" + whereClause;
            try (PreparedStatement ps = conn.prepareStatement(sql1); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    out.put("totalFraCount", rs.getInt("total"));
                    out.put("totalTargetAmount", rs.getDouble("totalTarget"));
                    out.put("totalCurrentAmount", rs.getDouble("totalCurrent"));
                }
            }
            String sql2 = "SELECT COALESCE(fc.category_name, 'Unknown') as categoryName, COUNT(f.fra_id) as fraCount " +
                          "FROM fra f LEFT JOIN fra_category fc ON f.category_id = fc.category_id " +
                          whereClause + " GROUP BY fc.category_name ORDER BY fraCount DESC";
            List<Map<String, Object>> byCat = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql2); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("categoryName", rs.getString("categoryName"));
                    row.put("count", rs.getInt("fraCount"));
                    byCat.add(row);
                }
            }
            out.put("fraByCategory", byCat);
        } catch (SQLException e) { }
        return out;
    }

    public static boolean isDuplicateTitle(String title, String excludeFraId) {
        String sql = "SELECT 1 FROM fra WHERE LOWER(TRIM(title)) = LOWER(TRIM(?))" + (excludeFraId != null ? " AND fra_id != ?" : "") + " LIMIT 1";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title); if (excludeFraId != null) pstmt.setString(2, excludeFraId);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { return false; }
    }

    private static String parseText(Object v) { return v == null ? "" : String.valueOf(v).trim(); }
    private static int parseInt(Object v) { if (v instanceof Number n) return n.intValue(); try { return Integer.parseInt(parseText(v)); } catch (Exception e) { return 0; } }
}