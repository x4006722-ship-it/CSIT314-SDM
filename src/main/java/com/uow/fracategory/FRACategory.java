package com.uow.fracategory;

import java.sql.*;
import java.util.*;
import com.uow.util.DBUtils;

public class FRACategory {

    public Object getViewCategory(int categoryId) {
        String sql = "SELECT category_id, category_name, category_status FROM fra_category WHERE category_id = ? LIMIT 1";
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                // Use HashMap for JDK 8 compatibility
                Map<String, Object> row = new HashMap<>();
                row.put("categoryID", rs.getInt("category_id"));
                row.put("categoryName", rs.getString("category_name"));
                row.put("categoryStatus", rs.getString("category_status"));
                return row;
            }
        } catch (SQLException e) { return null; }
    }

    public boolean saveCreateCategory(Map<String, Object> data) {
        String sql = "INSERT INTO fra_category (category_name, category_status) VALUES (?, ?)";
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(data.get("categoryName")));
            ps.setString(2, String.valueOf(data.get("categoryStatus")));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean saveUpdateCategory(Map<String, Object> data) {
        String sql = "UPDATE fra_category SET category_name = ?, category_status = ? WHERE category_id = ?";
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(data.get("categoryName")));
            ps.setString(2, String.valueOf(data.get("categoryStatus")));
            ps.setInt(3, Integer.parseInt(String.valueOf(data.get("categoryId"))));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean saveSuspendCategory(int categoryId) {
        String sql = "UPDATE fra_category SET category_status = CASE WHEN LOWER(TRIM(category_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE category_id=?";
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public Object getSearchCategory(Object searchData) {
        String name = "";
        String status = "";
        if (searchData instanceof Map<?, ?> data) {
            name = data.get("categoryName") == null ? "" : String.valueOf(data.get("categoryName")).trim();
            status = data.get("categoryStatus") == null ? "" : String.valueOf(data.get("categoryStatus")).trim();
        }

        String sql = "SELECT category_id, category_name, category_status FROM fra_category " +
                     "WHERE (? = '' OR category_name LIKE ?) AND (? = '' OR category_status = ?)";
        
        try (Connection c = DBUtils.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, "%" + name + "%");
            ps.setString(3, status);
            ps.setString(4, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<Map<String, Object>> list = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("categoryID", rs.getInt("category_id"));
                    row.put("categoryName", rs.getString("category_name"));
                    row.put("categoryStatus", rs.getString("category_status"));
                    list.add(row);
                }
                return list;
            }
        } catch (SQLException e) {
            return new ArrayList<Map<String, Object>>(); // Return empty ArrayList for JDK 8
        }
    }
}