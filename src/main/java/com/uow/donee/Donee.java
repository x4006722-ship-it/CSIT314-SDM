package com.uow.donee;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.uow.util.DBUtils;

public class Donee {

    public String lastErrorMessage = "";

    public Set<Integer> getFavouritedFraIdsForUser(int userId) {
        lastErrorMessage = "";
        if (userId <= 0) {
            return Set.of();
        }
        Set<Integer> out = new HashSet<>();
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT fra_id FROM fra_favourite WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("fra_id");
                    if (!rs.wasNull()) {
                        out.add(id);
                    }
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return out;
    }

    public boolean saveToFavourite(int fraId, int userId) {
        lastErrorMessage = "";
        if (fraId <= 0 || userId <= 0) {
            lastErrorMessage = "Invalid FRA or user.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ex = c.prepareStatement(
                     "SELECT 1 FROM fra_favourite WHERE user_id = ? AND fra_id = ? LIMIT 1")) {
            ex.setInt(1, userId);
            ex.setInt(2, fraId);
            try (ResultSet rs = ex.executeQuery()) {
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
            ps.setInt(1, userId);
            ps.setInt(2, fraId);
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

    public boolean removeFromFavourite(int fraId, int userId) {
        lastErrorMessage = "";
        if (fraId <= 0 || userId <= 0) {
            lastErrorMessage = "Invalid FRA or user.";
            return false;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM fra_favourite WHERE user_id = ? AND fra_id = ?")) {
            ps.setInt(1, userId);
            ps.setInt(2, fraId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    public Map<String, Object> getFraRecordByFraId(int fraId) {
        lastErrorMessage = "";
        if (fraId <= 0) {
            return null;
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM fra WHERE fra_id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowToMap(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM fra WHERE id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return rowToMap(rs);
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return null;
        }
    }

    public Map<String, Object> getFraViewForModal(int fraId) {
        lastErrorMessage = "";
        Map<String, Object> raw = getFraRecordByFraId(fraId);
        if (raw == null) {
            return null;
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("title", nzText(strFromRow(raw, "title", "fra_title", "fra_Title", "name", "f_title", "activity_title")));
        out.put("status", nzText(strFromRow(raw, "status", "fra_status", "fraStatus", "f_status", "fra_state")));
        out.put("createAt", formatDateTimeValue(anyFromRow(raw, "fra_createdAt", "fra_created_at", "created_at", "createdAt", "created")));
        out.put("viewCount", formatNumberValue(anyFromRow(raw, "fra_viewCount", "view_count", "viewCount")));
        out.put("favouriteCount", formatNumberValue(anyFromRow(raw, "fra_favouriteCount", "favourite_count", "favouriteCount", "fraFavouriteCount")));
        out.put("currentAmount", formatNumberValue(anyFromRow(raw, "current_amount", "currentAmount")));
        out.put("doneeName", resolveDoneeName(doneeIdFromFraRow(raw)));
        return out;
    }

    private String resolveDoneeName(Integer userId) {
        if (userId == null || userId <= 0) {
            return "—";
        }
        try (Connection c = DBUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT full_name, username FROM user_account WHERE user_id = ? LIMIT 1")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String fn = rs.getString(1);
                    if (fn != null && !fn.isBlank()) {
                        return fn.trim();
                    }
                    String un = rs.getString(2);
                    if (un != null && !un.isBlank()) {
                        return un.trim();
                    }
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return "—";
    }

    private static Integer doneeIdFromFraRow(Map<String, Object> raw) {
        Object v = anyFromRow(raw, "donee_id", "doneeId", "Donee_ID");
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        try {
            return Integer.parseInt(Objects.toString(v, "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String nzText(String s) {
        return (s == null || s.isBlank()) ? "—" : s.trim();
    }

    private static String formatNumberValue(Object o) {
        if (o == null) {
            return "—";
        }
        if (o instanceof Number) {
            double d = ((Number) o).doubleValue();
            if (!Double.isFinite(d)) {
                return "—";
            }
            if (d == Math.rint(d) && d >= Long.MIN_VALUE && d <= Long.MAX_VALUE) {
                return String.valueOf((long) d);
            }
            return String.valueOf(o);
        }
        String s = Objects.toString(o).trim();
        return s.isEmpty() ? "—" : s;
    }

    private static String formatDateTimeValue(Object o) {
        if (o == null) {
            return "—";
        }
        if (o instanceof Timestamp) {
            return formatOneInstant(((Timestamp) o).toInstant());
        }
        if (o instanceof java.util.Date) {
            return formatOneInstant(((java.util.Date) o).toInstant());
        }
        if (o instanceof String) {
            String s = ((String) o).trim();
            if (s.isEmpty()) {
                return "—";
            }
            try {
                return formatOneInstant(Instant.parse(s));
            } catch (DateTimeException e) {
                return s;
            }
        }
        return Objects.toString(o);
    }

    private static String formatOneInstant(Instant i) {
        return DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(i);
    }

    public List<Map<String, Object>> searchFavourites(int userId, String keyword, String status, String category) {
        lastErrorMessage = "";
        if (userId <= 0) {
            return List.of();
        }
        List<Map<String, Object>> joined = tryLoadFavouritesJoined(userId);
        if (joined == null) {
            joined = tryLoadFavouritesGeneric(userId);
        }
        if (joined == null) {
            return List.of();
        }
        Map<Integer, String> categoryById = loadCategoryIdToNameMap();
        Map<Integer, String> doneeNames = loadAllDoneeDisplayNames();
        return applyFiltersAndProject(joined, keyword, status, category, categoryById, doneeNames);
    }

    public List<Map<String, Object>> searchFraCatalog(String keyword, String status, String category) {
        lastErrorMessage = "";
        Map<Integer, String> categoryById = loadCategoryIdToNameMap();
        Map<Integer, String> doneeNames = loadAllDoneeDisplayNames();
        List<Map<String, Object>> all = tryLoadFraWithCategoryJoin();
        if (all == null) {
            all = tryLoadFraSelectStar();
        }
        if (all == null) {
            return List.of();
        }
        return applyFiltersAndProject(all, keyword, status, category, categoryById, doneeNames);
    }

    public Map<String, List<String>> getBrowseFilterOptions() {
        lastErrorMessage = "";
        Map<Integer, String> byId = loadCategoryIdToNameMap();
        TreeSet<String> unique = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String n : byId.values()) {
            if (n != null && !n.isBlank()) {
                unique.add(n.trim());
            }
        }
        List<String> categories = new ArrayList<>(unique);
        List<String> statuses = List.of("Pending", "Completed");
        return Map.of("categories", categories, "statuses", statuses);
    }

    private Map<Integer, String> loadCategoryIdToNameMap() {
        Map<Integer, String> m = new HashMap<>();
        String sql = "SELECT category_id, category_name FROM fra_category";
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt(1);
                if (rs.wasNull()) {
                    continue;
                }
                String n = rs.getString(2);
                if (n != null && !n.isBlank()) {
                    m.put(id, n.trim());
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return m;
    }

    private Map<Integer, String> loadAllDoneeDisplayNames() {
        Map<Integer, String> m = new HashMap<>();
        try (Connection c = DBUtils.getConnection();
                PreparedStatement ps = c.prepareStatement(
                        "SELECT user_id, full_name, username FROM user_account");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt(1);
                if (rs.wasNull()) {
                    continue;
                }
                String fn = rs.getString(2);
                String un = rs.getString(3);
                String d = (fn != null && !fn.isBlank()) ? fn.trim() : (un != null && !un.isBlank() ? un.trim() : null);
                if (d != null) {
                    m.put(id, d);
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return m;
    }

    private static boolean keywordMatchesFraRow(
            String kw,
            Map<String, Object> raw,
            int fid,
            String title,
            String stVal,
            String catVal,
            Map<Integer, String> doneeNameById) {
        if (kw == null || kw.isEmpty()) {
            return true;
        }
        StringBuilder blob = new StringBuilder();
        blob.append(' ').append(fid).append(' ');
        if (title != null) {
            blob.append(' ').append(title);
        }
        if (stVal != null) {
            blob.append(' ').append(stVal);
        }
        if (catVal != null) {
            blob.append(' ').append(catVal);
        }
        Integer catId = categoryIdFromRow(raw);
        if (catId != null) {
            blob.append(' ').append(catId);
        }
        Object created = anyFromRow(raw, "fra_createdAt", "fra_created_at", "created_at", "createdAt", "created");
        if (created != null) {
            String cr = Objects.toString(created);
            blob.append(' ').append(cr);
            blob.append(' ').append(formatDateTimeValue(created));
        }
        Object v1 = anyFromRow(raw, "fra_viewCount", "view_count", "viewCount");
        if (v1 != null) {
            blob.append(' ').append(Objects.toString(v1));
        }
        Object v2 = anyFromRow(raw, "fra_favouriteCount", "favourite_count", "favouriteCount", "fraFavouriteCount");
        if (v2 != null) {
            blob.append(' ').append(Objects.toString(v2));
        }
        Object v3 = anyFromRow(raw, "current_amount", "currentAmount");
        if (v3 != null) {
            blob.append(' ').append(Objects.toString(v3));
        }
        Object tgt = anyFromRow(raw, "fra_targetAmount", "target_amount", "targetAmount", "goal");
        if (tgt != null) {
            blob.append(' ').append(Objects.toString(tgt));
        }
        String extraTitle = strFromRow(raw, "fra_title", "fra_Title", "f_title", "activity_title");
        if (extraTitle != null && (title == null || !extraTitle.equals(title))) {
            blob.append(' ').append(extraTitle);
        }
        Integer did = doneeIdFromFraRow(raw);
        if (doneeNameById != null && did != null) {
            String nm = doneeNameById.get(did);
            if (nm != null) {
                blob.append(' ').append(nm);
            }
            blob.append(' ').append(did);
        }
        return blob.toString().toLowerCase(Locale.ROOT).contains(kw);
    }

    private static String resolveCategoryNameForRow(Map<String, Object> raw, Map<Integer, String> categoryById) {
        Integer cid = categoryIdFromRow(raw);
        if (cid != null && cid > 0 && categoryById != null) {
            String n = categoryById.get(cid);
            if (n != null && !n.isBlank()) {
                return n;
            }
        }
        String fromJoin = strFromRow(raw, "_category_name", "category_name", "cat_name");
        if (fromJoin != null && !fromJoin.isBlank()) {
            return fromJoin.trim();
        }
        String legacy = strFromRow(raw, "category");
        if (legacy != null && !legacy.isBlank()) {
            return legacy.trim();
        }
        if (cid != null) {
            return String.valueOf(cid);
        }
        return null;
    }

    private static Integer categoryIdFromRow(Map<String, Object> raw) {
        Object v = anyFromRow(raw, "category_id", "Category_ID", "cat_id");
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        try {
            return Integer.parseInt(Objects.toString(v, "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static List<Map<String, Object>> applyFiltersAndProject(
            List<Map<String, Object>> rows,
            String keyword,
            String status,
            String category,
            Map<Integer, String> categoryById,
            Map<Integer, String> doneeNameById) {
        if (categoryById == null) {
            categoryById = Map.of();
        }
        if (doneeNameById == null) {
            doneeNameById = Map.of();
        }
        String kw = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        String st = status == null ? "all" : status.trim();
        String cat = category == null ? "all" : category.trim();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> raw : rows) {
            int fid = idFromRow(raw);
            String title = strFromRow(raw, "title", "fra_title", "name", "f_title", "activity_title");
            String stVal = strFromRow(raw, "status", "fra_status", "f_status", "fra_state");
            String catVal = resolveCategoryNameForRow(raw, categoryById);
            if (!kw.isEmpty() && !keywordMatchesFraRow(kw, raw, fid, title, stVal, catVal, doneeNameById)) {
                continue;
            }
            if (st != null && !st.isEmpty() && !"all".equalsIgnoreCase(st)) {
                if (stVal == null || !stVal.trim().equalsIgnoreCase(st)) {
                    continue;
                }
            }
            if (cat != null && !cat.isEmpty() && !"all".equalsIgnoreCase(cat)) {
                if (catVal == null || !catVal.trim().equalsIgnoreCase(cat)) {
                    continue;
                }
            }
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("fra_id", fid);
            m.put("title", title != null ? title : "—");
            m.put("status", stVal != null ? stVal : "—");
            m.put("category", catVal != null ? catVal : "—");
            out.add(m);
        }
        return out;
    }

    public List<Map<String, Object>> loadFrasForDonee(int userId) {
        lastErrorMessage = "";
        if (userId <= 0) {
            return List.of();
        }
        String[] ownerCols = { "donee_id", "user_id" };
        String[] orderBy = { "f.fra_id", "f.id" };
        String lastErr = null;
        for (String owner : ownerCols) {
            for (String order : orderBy) {
                String sql = "SELECT f.*, fc.category_name AS _category_name FROM fra f "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id "
                        + "WHERE f." + owner + " = ? ORDER BY " + order + " DESC LIMIT 2000";
                try (Connection c = DBUtils.getConnection();
                        PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setInt(1, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        List<Map<String, Object>> rows = readAllRows(rs);
                        if (!rows.isEmpty()) {
                            return rows;
                        }
                    }
                } catch (SQLException e) {
                    lastErr = e.getMessage();
                }
            }
        }
        if (lastErr != null) {
            lastErrorMessage = lastErr;
        }
        return List.of();
    }

    public Map<Integer, String> getCategoryNameById() {
        return loadCategoryIdToNameMap();
    }

    public Map<Integer, String> getDoneeDisplayNameById() {
        return loadAllDoneeDisplayNames();
    }

    public String categoryLabelForRow(Map<String, Object> raw, Map<Integer, String> categoryById) {
        return resolveCategoryNameForRow(raw, categoryById);
    }

    public String asDisplayDateTime(Object o) {
        return formatDateTimeValue(o);
    }

    public String asDisplayNumber(Object o) {
        return formatNumberValue(o);
    }

    public static int fraIdFromMap(Map<String, Object> raw) {
        return idFromRow(raw);
    }

    public static String strFromMap(Map<String, Object> raw, String... keys) {
        return strFromRow(raw, keys);
    }

    public static Object anyFromMap(Map<String, Object> raw, String... keys) {
        return anyFromRow(raw, keys);
    }

    public static boolean matchesKeywordInRow(
            String kw,
            Map<String, Object> raw,
            int fid,
            String title,
            String stVal,
            String catVal,
            Map<Integer, String> doneeNameById) {
        return keywordMatchesFraRow(kw, raw, fid, title, stVal, catVal, doneeNameById);
    }

    public static Instant createdInstantFromRow(Map<String, Object> raw) {
        Object o = anyFromRow(raw, "fra_createdAt", "fra_created_at", "created_at", "createdAt", "created");
        if (o == null) {
            return null;
        }
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toInstant();
        }
        if (o instanceof java.util.Date) {
            return ((java.util.Date) o).toInstant();
        }
        if (o instanceof String) {
            try {
                return Instant.parse(((String) o).trim());
            } catch (DateTimeException e) {
                return null;
            }
        }
        return null;
    }

    private List<Map<String, Object>> tryLoadFraWithCategoryJoin() {
        String base = "SELECT f.*, fc.category_name AS _category_name FROM fra f "
                + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id";
        try {
            return queryToMaps(base + " LIMIT 500");
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
        }
        return null;
    }

    private List<Map<String, Object>> tryLoadFraSelectStar() {
        try {
            return queryToMaps("SELECT * FROM fra LIMIT 500");
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return null;
        }
    }

    private List<Map<String, Object>> tryLoadFavouritesJoined(int userId) {
        String[] sqls = {
                "SELECT f.*, fc.category_name AS _category_name FROM fra f "
                        + "INNER JOIN fra_favourite ff ON f.fra_id = ff.fra_id AND ff.user_id = ? "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id",
                "SELECT f.*, fc.category_name AS _category_name FROM fra f "
                        + "INNER JOIN fra_favourite ff ON f.id = ff.fra_id AND ff.user_id = ? "
                        + "LEFT JOIN fra_category fc ON f.category_id = fc.category_id",
        };
        for (String s : sqls) {
            try (Connection c = DBUtils.getConnection();
                 PreparedStatement ps = c.prepareStatement(s)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    return readAllRows(rs);
                }
            } catch (SQLException e) {
                lastErrorMessage = e.getMessage();
            }
        }
        return null;
    }

    private List<Map<String, Object>> tryLoadFavouritesGeneric(int userId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT * FROM fra_favourite WHERE user_id = ?")) {
            ps.setInt(1, userId);
                try (ResultSet rff = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rff.next()) {
                    int fid = rff.getInt("fra_id");
                    if (rff.wasNull()) {
                        continue;
                    }
                    ids.add(fid);
                }
                if (ids.isEmpty()) {
                    return List.of();
                }
                List<Map<String, Object>> out = new ArrayList<>();
                for (int fid : ids) {
                    Map<String, Object> one = getFraRecordByFraIdForList(fid);
                    if (one != null) {
                        out.add(one);
                    }
                }
                return out;
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            return null;
        }
    }

    private Map<String, Object> getFraRecordByFraIdForList(int fraId) {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM fra WHERE fra_id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readRowToMapSafe(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM fra WHERE id = ? LIMIT 1")) {
            ps.setInt(1, fraId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readRowToMapSafe(rs);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    private static Map<String, Object> readRowToMapSafe(ResultSet rs) {
        try {
            return rowToMap(rs);
        } catch (SQLException e) {
            return null;
        }
    }

    private static List<Map<String, Object>> queryToMaps(String sql) throws SQLException {
        try (Connection c = DBUtils.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return readAllRows(rs);
        }
    }

    private static List<Map<String, Object>> readAllRows(ResultSet rs) throws SQLException {
        List<Map<String, Object>> all = new ArrayList<>();
        while (rs.next()) {
            all.add(rowToMap(rs));
        }
        return all;
    }

    private static Map<String, Object> rowToMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int n = meta.getColumnCount();
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= n; i++) {
            String label = meta.getColumnLabel(i);
            row.put(label, cellValue(rs, i));
        }
        return row;
    }

    private static Object cellValue(ResultSet rs, int i) throws SQLException {
        Object o = rs.getObject(i);
        if (o == null) {
            return null;
        }
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toInstant().toString();
        }
        if (o instanceof java.sql.Date) {
            return o.toString();
        }
        if (o instanceof java.sql.Time) {
            return o.toString();
        }
        if (o instanceof byte[]) {
            return null;
        }
        return o;
    }

    private static int idFromRow(Map<String, Object> raw) {
        Object v = anyFromRow(raw, "fra_id", "id", "FRA_id", "fraId");
        if (v == null) {
            return 0;
        }
        if (v instanceof Number) {
            return ((Number) v).intValue();
        }
        try {
            return Integer.parseInt(Objects.toString(v, "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String strFromRow(Map<String, Object> raw, String... keys) {
        Object v = anyFromRow(raw, keys);
        return v == null ? null : Objects.toString(v, null);
    }

    private static Object anyFromRow(Map<String, Object> raw, String... keys) {
        for (String k : keys) {
            if (raw.containsKey(k) && raw.get(k) != null) {
                return raw.get(k);
            }
        }
        for (String k : keys) {
            for (Map.Entry<String, Object> e : raw.entrySet()) {
                if (e.getKey() != null && e.getKey().equalsIgnoreCase(k) && e.getValue() != null) {
                    return e.getValue();
                }
            }
        }
        return null;
    }

    public List<Map<String, Object>> searchMyFraHistory(
            int userId,
            String keyword,
            String category,
            String dateFrom,
            String dateTo,
            String status) {
        return runMyFraHistory(userId, keyword, category, dateFrom, dateTo, status, true);
    }

    public List<Map<String, Object>> viewMyFraHistory(
            int userId,
            String category,
            String dateFrom,
            String dateTo,
            String status) {
        return runMyFraHistory(userId, "", category, dateFrom, dateTo, status, false);
    }

    private List<Map<String, Object>> runMyFraHistory(
            int userId,
            String keyword,
            String category,
            String dateFrom,
            String dateTo,
            String status,
            boolean withKeyword) {
        lastErrorMessage = "";
        if (userId <= 0) {
            return List.of();
        }
        List<Map<String, Object>> rawRows = loadFrasForDonee(userId);
        if (rawRows == null) {
            rawRows = List.of();
        }
        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        if (!withKeyword) {
            kw = "";
        }
        Map<Integer, String> categoryById = getCategoryNameById();
        Map<Integer, String> doneeNameById = getDoneeDisplayNameById();
        String myName = doneeNameById.getOrDefault(userId, "—");
        String stF = status == null ? "all" : status.trim();
        String catF = category == null ? "all" : category.trim();
        LocalDate dFrom = parseHistoryDate(dateFrom);
        LocalDate dTo = parseHistoryDate(dateTo);
        ZoneId z = ZoneId.systemDefault();
        List<Map<String, Object>> out = new ArrayList<>();
        for (Map<String, Object> raw : rawRows) {
            int fid = fraIdFromMap(raw);
            String title = strFromMap(raw, "title", "fra_title", "name", "f_title", "activity_title");
            String stVal = strFromMap(raw, "status", "fra_status", "f_status", "fra_state");
            String catVal = categoryLabelForRow(raw, categoryById);
            if (!"all".equalsIgnoreCase(stF)) {
                if (stVal == null || !stVal.trim().equalsIgnoreCase(stF)) {
                    continue;
                }
            }
            if (!"all".equalsIgnoreCase(catF)) {
                if (catVal == null || !catVal.trim().equalsIgnoreCase(catF)) {
                    continue;
                }
            }
            Instant inst = createdInstantFromRow(raw);
            if (!inHistoryDateRange(inst, dFrom, dTo, z)) {
                continue;
            }
            if (kw.length() > 0 && !matchesKeywordInRow(
                    kw, raw, fid, title, stVal, catVal, doneeNameById)) {
                continue;
            }
            out.add(projectHistoryRow(
                    raw, fid, title, stVal, catVal, myName));
        }
        return out;
    }

    private static LocalDate parseHistoryDate(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(t);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean inHistoryDateRange(Instant t, LocalDate from, LocalDate to, ZoneId z) {
        if (from == null && to == null) {
            return true;
        }
        if (t == null) {
            return false;
        }
        if (from != null) {
            Instant fromStart = from.atStartOfDay(z).toInstant();
            if (t.isBefore(fromStart)) {
                return false;
            }
        }
        if (to != null) {
            Instant toEndExclusive = to.plusDays(1).atStartOfDay(z).toInstant();
            if (!t.isBefore(toEndExclusive)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Object> projectHistoryRow(
            Map<String, Object> raw,
            int fid,
            String title,
            String stVal,
            String catVal,
            String doneeName) {
        Object created = anyFromMap(raw, "fra_createdAt", "fra_created_at", "created_at", "createdAt", "created");
        Object v1 = anyFromMap(raw, "fra_viewCount", "view_count", "viewCount");
        Object v2 = anyFromMap(raw, "fra_favouriteCount", "favourite_count", "favouriteCount", "fraFavouriteCount");
        Object v3 = anyFromMap(raw, "current_amount", "currentAmount");
        Object tgt = anyFromMap(raw, "fra_targetAmount", "target_amount", "targetAmount", "goal");
        double current = toDoubleForHistory(v3);
        double target = toDoubleForHistory(tgt);
        int progress = 0;
        if (target > 0) {
            progress = (int) Math.min(100, Math.round(100.0 * current / target));
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("fra_id", fid);
        m.put("title", title != null && !title.isBlank() ? title : "—");
        m.put("status", stVal != null && !stVal.isBlank() ? stVal : "—");
        m.put("createAt", asDisplayDateTime(created));
        m.put("viewCount", asDisplayNumber(v1));
        m.put("favouriteCount", asDisplayNumber(v2));
        m.put("currentAmount", asDisplayNumber(v3));
        m.put("targetAmount", asDisplayNumber(tgt));
        m.put("progressPercent", progress);
        m.put("category", catVal != null && !catVal.isBlank() ? catVal : "—");
        m.put("doneeName", doneeName);
        return m;
    }

    private static double toDoubleForHistory(Object o) {
        if (o == null) {
            return 0d;
        }
        if (o instanceof Number) {
            return ((Number) o).doubleValue();
        }
        try {
            return Double.parseDouble(Objects.toString(o).trim());
        } catch (NumberFormatException e) {
            return 0d;
        }
    }
}
