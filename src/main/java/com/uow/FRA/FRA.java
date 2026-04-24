package com.uow.FRA;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.uow.util.DBUtils;

public class FRA {
    private String fraId;
    private String categoryId;
    private String fraTitle;
    private Double fraTargetAmount;
    private String fraStatus;
    
    // for database column names
    private String createdAt; 
    private String endedAt;   
    private String doneeId;   
    private String doneeName; 

    // statistics data
    private double currentAmount = 0.0;
    private int viewCount = 0;
    private int favoriteCount = 0;

    public FRA() {}

    /** Full constructor including extended fields (dates, donee). */
    public FRA(String fraId, String title, Double target, String categoryId, String status, double currentAmount, int viewCount, int favoriteCount, String createdAt, String endedAt, String doneeId, String doneeName) {
        this.fraId = fraId;
        this.fraTitle = title;
        this.fraTargetAmount = target;
        this.categoryId = categoryId;
        
        // Normalise status to Pending or Completed
        this.fraStatus = "Completed".equalsIgnoreCase(status) ? "Completed" : "Pending"; 
        
        this.currentAmount = currentAmount;
        this.viewCount = viewCount;
        this.favoriteCount = favoriteCount;
        
        this.createdAt = createdAt;
        this.endedAt = endedAt;
        this.doneeId = doneeId;
        this.doneeName = doneeName;
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
    public void setFraStatus(String fraStatus) { 
        this.fraStatus = "Completed".equalsIgnoreCase(fraStatus) ? "Completed" : "Pending"; 
    }
    
    public double getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(double currentAmount) { this.currentAmount = currentAmount; }
    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    // Getters and setters for date and donee fields
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getEndedAt() { return endedAt; }
    public void setEndedAt(String endedAt) { this.endedAt = endedAt; }
    public String getDoneeId() { return doneeId; }
    public void setDoneeId(String doneeId) { this.doneeId = doneeId; }
    public String getDoneeName() { return doneeName; }
    public void setDoneeName(String doneeName) { this.doneeName = doneeName; }

    public Boolean saveFRA() {
        // INSERT with date columns and donee_id
        String sql = "INSERT INTO fra (fra_title, fra_targetAmount, category_id, fra_status, current_amount, fra_viewCount, fra_favouriteCount, fra_createdAt, fra_endedAt, donee_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount);
            pstmt.setString(3, this.categoryId);
            pstmt.setString(4, "Pending"); // new rows always start as Pending
            pstmt.setDouble(5, 0.0);
            pstmt.setInt(6, 0);
            pstmt.setInt(7, 0);
            pstmt.setString(8, this.createdAt);
            pstmt.setString(9, this.endedAt);
            pstmt.setString(10, this.doneeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("[DATABASE ERROR] Save failed: " + e.getMessage());
            return false; 
        }
    }

    public static List<FRA> findFRAsByCriteria(String criteria) {
        List<FRA> list = new ArrayList<>();
        // LEFT JOIN donee for donee_name
        String sql = "SELECT f.*, d.donee_name AS donee_name FROM fra f LEFT JOIN donee d ON f.donee_id = d.donee_id WHERE f.fra_title LIKE ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + (criteria == null ? "" : criteria) + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(fromResultSet(rs));
                }
            }
        } catch (SQLException e) { 
            System.err.println("[DATABASE ERROR] Search failed: " + e.getMessage());
        }
        return list;
    }

    private static FRA fromResultSet(ResultSet rs) throws SQLException {
        return new FRA(
            rs.getString("fra_id"),
            rs.getString("fra_title"),
            rs.getDouble("fra_targetAmount"),
            rs.getString("category_id"),
            rs.getString("fra_status"),
            rs.getDouble("current_amount"),
            rs.getInt("fra_viewCount"),
            rs.getInt("fra_favouriteCount"),
            rs.getString("fra_createdAt"),
            rs.getString("fra_endedAt"),
            rs.getString("donee_id"),
            rs.getString("donee_name")
        );
    }

    /** Fund raiser: measure interest — campaigns ordered by view count (highest first). */
    public static List<FRA> findFRAsForViewEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.donee_name AS donee_name FROM fra f LEFT JOIN donee d ON f.donee_id = d.donee_id ORDER BY f.fra_viewCount DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] View engagement report failed: " + e.getMessage());
        }
        return list;
    }

    /** Fund raiser: track favourite saves — campaigns ordered by favourite count (highest first). */
    public static List<FRA> findFRAsForFavoriteEngagementReport() {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.donee_name AS donee_name FROM fra f LEFT JOIN donee d ON f.donee_id = d.donee_id ORDER BY f.fra_favouriteCount DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Favorite engagement report failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Completed FRAs filtered by optional category and optional end-date period ({@code fra_endedAt}).
     * Empty or null {@code categoryId}, {@code startDate}, {@code endDate} skips that filter.
     */
    public static List<FRA> findCompletedFRAsByCategoryAndDatePeriod(String categoryId, String startDate, String endDate) {
        List<FRA> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT f.*, d.donee_name AS donee_name FROM fra f LEFT JOIN donee d ON f.donee_id = d.donee_id WHERE LOWER(f.fra_status) = 'completed'");
        List<Object> params = new ArrayList<>();
        if (categoryId != null && !categoryId.isBlank()) {
            sql.append(" AND f.category_id = ?");
            params.add(categoryId);
        }
        if (startDate != null && !startDate.isBlank()) {
            sql.append(" AND f.fra_endedAt >= ?");
            params.add(startDate);
        }
        if (endDate != null && !endDate.isBlank()) {
            sql.append(" AND f.fra_endedAt <= ?");
            params.add(endDate);
        }
        sql.append(" ORDER BY f.fra_endedAt DESC");
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
            System.err.println("[DATABASE ERROR] Completed FRA by category/date failed: " + e.getMessage());
        }
        return list;
    }

    public static List<FRA> findAllFRAs() {
        List<FRA> list = new ArrayList<>();
        // LEFT JOIN donee for donee_name
        String sql = "SELECT f.*, d.donee_name AS donee_name FROM fra f LEFT JOIN donee d ON f.donee_id = d.donee_id";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DATABASE ERROR] Find all failed: " + e.getMessage());
        }
        return list;
    }

    public void updateFRAData() {
        // UPDATE fra_status, date fields, and donee_id
        String sql = "UPDATE fra SET fra_title = ?, fra_targetAmount = ?, category_id = ?, fra_status = ?, fra_createdAt = ?, fra_endedAt = ?, donee_id = ? WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraTitle);
            pstmt.setDouble(2, this.fraTargetAmount);
            pstmt.setString(3, this.categoryId);
            pstmt.setString(4, this.fraStatus);
            pstmt.setString(5, this.createdAt);
            pstmt.setString(6, this.endedAt);
            pstmt.setString(7, this.doneeId);
            pstmt.setString(8, this.fraId);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void removeFRA() {
        String sql = "DELETE FROM fra WHERE fra_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.fraId);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            System.err.println("[DATABASE ERROR] Delete failed: " + e.getMessage());
        }
    }
}