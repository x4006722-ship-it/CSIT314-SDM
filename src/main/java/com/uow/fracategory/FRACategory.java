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

    public int categoryID;
    public String categoryName;
    public String categoryStatus;
    public String lastErrorMessage = "";

    //Create Category
    public boolean saveCreateCategory() {
        lastErrorMessage = "";
        categoryID = 0;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement dup = c.prepareStatement(
                     "SELECT 1 FROM fra_category WHERE LOWER(TRIM(category_name)) = LOWER(TRIM(?)) LIMIT 1")) {
            dup.setString(1, categoryName.trim());
            try (ResultSet drs = dup.executeQuery()) {
                if (drs.next()) {
                    lastErrorMessage = "Category name already exists.";
                    return false;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = "Failed to create category.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO fra_category (category_name, category_status) VALUES (?, ?)")) {
            ps.setString(1, categoryName.trim());
            ps.setString(2, categoryStatus == null ? "" : categoryStatus.trim());
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) lastErrorMessage = "Failed to create category.";
            return ok;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to create category.";
            return false;
        }
    }

    //View Category
    public Object getViewCategory(int categoryID) {
        lastErrorMessage = "";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT category_id, category_name, category_status FROM fra_category WHERE category_id = ? LIMIT 1")) {
            ps.setInt(1, categoryID);
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
    public boolean saveUpdateCategory(int categoryID) {
        lastErrorMessage = "";
        this.categoryID = categoryID;
        if (!categoryName.isBlank()) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement dup = c.prepareStatement(
                         "SELECT 1 FROM fra_category WHERE LOWER(TRIM(category_name)) = LOWER(TRIM(?)) AND category_id <> ? LIMIT 1")) {
                dup.setString(1, categoryName);
                dup.setInt(2, categoryID);
                try (ResultSet drs = dup.executeQuery()) {
                    if (drs.next()) {
                        lastErrorMessage = "Category name already exists.";
                        return false;
                    }
                }
            } catch (SQLException e) {
                lastErrorMessage = "Failed to update category.";
                return false;
            }
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE fra_category SET category_name = ?, category_status = ? WHERE category_id = ?")) {
            ps.setString(1, categoryName);
            ps.setString(2, categoryStatus);
            ps.setInt(3, categoryID);
            int n = ps.executeUpdate();
            if (n > 0) return true;
            if (getViewCategory(categoryID) != null) return true;
            lastErrorMessage = "Failed to update category.";
            return false;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to update category.";
            return false;
        }
    }

    //Suspend Category
    public boolean saveSuspendCategory(int categoryID) {
        lastErrorMessage = "";
        this.categoryID = categoryID;
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE fra_category SET category_status = CASE WHEN LOWER(TRIM(category_status))='suspended' THEN 'Active' ELSE 'Suspended' END WHERE category_id=?")) {
            ps.setInt(1, categoryID);
            boolean ok = ps.executeUpdate() > 0;
            if (!ok) lastErrorMessage = "Failed to update category status.";
            return ok;
        } catch (SQLException e) {
            lastErrorMessage = "Failed to update category status.";
            return false;
        }
    }

    //Search Category
    public Object getSearchCategory(String keyword, String status) {
        lastErrorMessage = "";
        String name = keyword == null ? "" : keyword.trim();
        String st = status == null ? "" : status.trim();

        StringBuilder sql = new StringBuilder(
                "SELECT category_id, category_name, category_status FROM fra_category WHERE 1=1 ");
        if (!name.isEmpty()) sql.append("AND category_name LIKE ? ");
        if (!st.isEmpty())   sql.append("AND category_status = ? ");
        sql.append("ORDER BY category_id LIMIT 2000");

        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!name.isEmpty()) ps.setString(idx++, "%" + name + "%");
            if (!st.isEmpty())   ps.setString(idx++, st);

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
}
