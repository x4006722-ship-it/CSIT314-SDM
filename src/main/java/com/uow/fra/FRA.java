package com.uow.fra;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.sql.*;
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

    // public static List<FRA> findFRAsByCriteria(String criteria) {
    //     List<FRA> list = new ArrayList<>();
    //     String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE f.fra_title LIKE ?";
    //     try (Connection conn = DBUtils.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //         pstmt.setString(1, "%" + (criteria == null ? "" : criteria) + "%");
    //         try (ResultSet rs = pstmt.executeQuery()) {
    //             while (rs.next()) list.add(fromResultSet(rs));
    //         }
    //     } catch (SQLException e) { 
    //         // Silent handling: return an empty list
    //     }
    //     return list;
    // }
    // public static List<FRA> findFRAsByCriteria(String criteria, String categoryId, String status, String role, String userId) {
    //     List<FRA> list = new ArrayList<>();
    //     StringBuilder sql = new StringBuilder(
    //         "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name " +
    //         "FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id " +
    //         "LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id WHERE 1=1"
    //     );
    //     List<Object> params = new ArrayList<>();

    //     // 1. 标题模糊搜索
    //     if (criteria != null && !criteria.isBlank()) {
    //         sql.append(" AND f.fra_title LIKE ?");
    //         params.add("%" + criteria + "%");
    //     }
    //     // 2. 类别过滤
    //     if (categoryId != null && !categoryId.isBlank() && !"all".equalsIgnoreCase(categoryId)) {
    //         sql.append(" AND f.category_id = ?");
    //         params.add(categoryId);
    //     }
    //     // 3. 状态过滤
    //     if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
    //         sql.append(" AND f.fra_status = ?");
    //         params.add(status);
    //     }

    //     // 4. 【核心修复】角色数据隔离
    //     if ("donee".equalsIgnoreCase(role)) {
    //         // 受赠人：绝对不允许看到 Draft 或 Cancelled 的活动，强制底层过滤
    //         sql.append(" AND f.fra_status IN ('Pending', 'Completed', 'Active')");
    //     } else if ("fundRaiser".equalsIgnoreCase(role)) {
    //         // 筹款人：只能搜索和管理属于自己创建的活动
    //         if (userId != null && !userId.isBlank()) {
    //             sql.append(" AND f.fundRaiser_id = ?");
    //             params.add(userId);
    //         }
    //     }

    //     try (Connection conn = DBUtils.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
    //         for (int i = 0; i < params.size(); i++) {
    //             pstmt.setObject(i + 1, params.get(i));
    //         }
    //         try (ResultSet rs = pstmt.executeQuery()) {
    //             while (rs.next()) list.add(fromResultSet(rs));
    //         }
    //     } catch (SQLException e) { 
    //         // Silent handling
    //     }
    //     return list;
    // }
    // ================= 修复 Story #1 & #5：加入角色权限和多条件搜索 =================
    public static List<FRA> findFRAsByCriteria(String criteria, String categoryId, String status, String role, String userId) {
        List<FRA> list = new ArrayList<>();
        // 修复：加入了 fra_category 连表，以便支持前端传 Category Name 过来搜索
        StringBuilder sql = new StringBuilder(
            "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name " +
            "FROM fra f " +
            "LEFT JOIN user_account d ON f.donee_id = d.user_id " +
            "LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id " +
            "LEFT JOIN fra_category fc ON f.category_id = fc.category_id " +
            "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        // 1. 标题模糊搜索
        if (criteria != null && !criteria.isBlank()) {
            sql.append(" AND f.fra_title LIKE ?");
            params.add("%" + criteria + "%");
        }
        // 2. 类别过滤（支持匹配 ID 或 Name）
        if (categoryId != null && !categoryId.isBlank() && !"all".equalsIgnoreCase(categoryId)) {
            sql.append(" AND (f.category_id = ? OR fc.category_name = ?)");
            params.add(categoryId);
            params.add(categoryId);
        }
        // 3. 状态过滤
        if (status != null && !status.isBlank() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND f.fra_status = ?");
            params.add(status);
        }

        // 4. 【核心】角色数据隔离
        if ("donee".equalsIgnoreCase(role)) {
            // 受赠人：只能看到系统内允许公开的状态
            sql.append(" AND f.fra_status IN ('Pending', 'Completed')");
        } else if ("fundRaiser".equalsIgnoreCase(role)) {
            // 筹款人：只能搜索和管理属于自己创建的活动
            if (userId != null && !userId.isBlank()) {
                sql.append(" AND f.fundRaiser_id = ?");
                params.add(userId);
            }
        }

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(fromResultSet(rs));
            }
        } catch (SQLException e) { 
            // Silent handling
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
        return fra;
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

    // public static List<FRA> findAllFRAs() {
    //     List<FRA> list = new ArrayList<>();
    //     String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id";
    //     try (Connection conn = DBUtils.getConnection();
    //          PreparedStatement pstmt = conn.prepareStatement(sql);
    //          ResultSet rs = pstmt.executeQuery()) {
    //         while (rs.next()) list.add(fromResultSet(rs));
    //     } catch (SQLException e) { }
    //     return list;
    // }

    public static List<FRA> findAllFRAs(String fundRaiserId) {
        List<FRA> list = new ArrayList<>();
        String sql = "SELECT f.*, d.full_name AS donee_name, fr.full_name AS fundRaiser_name " +
                     "FROM fra f LEFT JOIN user_account d ON f.donee_id = d.user_id " +
                     "LEFT JOIN user_account fr ON f.fundRaiser_id = fr.user_id " +
                     "WHERE f.fundRaiser_id = ?"; // 强制过滤当前筹款人
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fundRaiserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(fromResultSet(rs));
            }
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
            "SELECT f.fra_id, f.fra_title, f.fra_status, f.current_amount, f.fra_targetAmount, IFNULL(TRIM(fc.category_name), '') AS category_name "
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
        if (!startDate.isBlank()) {
            sql.append("AND DATE(f.fra_createdAt) >= ? ");
        }
        if (!endDate.isBlank()) {
            sql.append("AND DATE(f.fra_createdAt) <= ? ");
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
            if (!startDate.isBlank()) {
                pstmt.setString(i++, startDate);
            }
            if (!endDate.isBlank()) {
                pstmt.setString(i++, endDate);
            }

            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("fra_id", rs.getInt("fra_id"));
                    row.put("title", rs.getString("fra_title"));
                    row.put("status", rs.getString("fra_status"));
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

    public static Object getViewDonation(int fraId) {
        String sql =
            "SELECT f.fra_title, f.fra_status, f.fra_createdAt, f.fra_viewCount, f.fra_favouriteCount, "
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
    
    public static boolean isDuplicateTitle(String title, String excludeFraId) {
        String sql = "SELECT 1 FROM fra WHERE LOWER(TRIM(fra_title)) = LOWER(TRIM(?))";
        if (excludeFraId != null) {
            sql += " AND fra_id != ?";
        }
        sql += " LIMIT 1";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            if (excludeFraId != null) {
                pstmt.setString(2, excludeFraId);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // 查到数据说明重名
            }
        } catch (SQLException e) { return false; }
    }
}