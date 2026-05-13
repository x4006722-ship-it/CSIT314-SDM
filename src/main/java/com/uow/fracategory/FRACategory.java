package com.uow.fracategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.uow.util.DBUtils;

public class FRACategory {

    //Create Category
    public boolean saveCreateCategory(Object newCategoryData) {
        if (!(newCategoryData instanceof Map<?, ?> data)) {
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO fra_category (category_name, category_status) VALUES (?, ?)")) {
            ps.setString(1, readText(data.get("categoryName")));
            ps.setString(2, readText(data.get("categoryStatus")));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    //View Category
    public Object getViewCategory(int categoryId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category_id, category_name, category_status FROM fra_category WHERE category_id = ? LIMIT 1")) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Map<String, Object> obj = new LinkedHashMap<>();
                obj.put("categoryID", rs.getInt("category_id"));
                obj.put("categoryName", rs.getString("category_name"));
                obj.put("categoryStatus", rs.getString("category_status"));
                return obj;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    //Update Category
    public boolean saveUpdateCategory(Object updatedCategoryData) {
        if (!(updatedCategoryData instanceof Map<?, ?> data)) {
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE fra_category SET category_name = ?, category_status = ? WHERE category_id = ?")) {
            ps.setString(1, readText(data.get("categoryName")));
            ps.setString(2, readText(data.get("categoryStatus")));
            ps.setInt(3, readInt(data.get("categoryId")));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    //Suspend Category
    public boolean saveSuspendCategory(int categoryId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE fra_category SET category_status = CASE WHEN LOWER(TRIM(category_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE category_id=?")) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    //Search Category
    public Object getSearchCategory(Object searchCategoryData) {
        String name = "";
        String status = "";
        if (searchCategoryData instanceof Map<?, ?> data) {
            name = readText(data.get("categoryName"));
            status = readText(data.get("categoryStatus"));
        }

        StringBuilder sql = new StringBuilder(
                "SELECT category_id, category_name, category_status FROM fra_category WHERE 1=1 ");
        if (!name.isEmpty()) sql.append("AND category_name LIKE ? ");
        if (!status.isEmpty()) sql.append("AND category_status = ? ");
        sql.append("ORDER BY category_id LIMIT 500");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!name.isEmpty()) ps.setString(idx++, "%" + name + "%");
            if (!status.isEmpty()) ps.setString(idx++, status);

            List<Map<String, Object>> out = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("categoryID", rs.getInt("category_id"));
                    row.put("categoryName", rs.getString("category_name"));
                    row.put("categoryStatus", rs.getString("category_status"));
                    out.add(row);
                }
            }
            return out;
        } catch (SQLException e) {
            return List.of();
        }
    }

    private String readText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int readInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(readText(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
