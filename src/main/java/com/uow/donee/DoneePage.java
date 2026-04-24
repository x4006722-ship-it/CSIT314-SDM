package com.uow.donee;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class DoneePage {

    @Autowired
    private SearchFraController searchFraController;

    @Autowired
    private ViewFraController viewFraController;

    @Autowired
    private ViewFavouriteFraController viewFavouriteFraController;

    @Autowired
    private SaveFavouriteController saveFavouriteController;

    @Autowired
    private SearchFavouriteController searchFavouriteController;

    @Autowired
    private SearchDoneeFraHistoryController searchDoneeFraHistoryController;

    @Autowired
    private ViewDoneeFraHistoryController viewDoneeFraHistoryController;

    @GetMapping(value = "/api/donee/fra/browse", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onBrowseFra(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "category", defaultValue = "all") String category,
            HttpSession session) {
        List<Map<String, Object>> rows = searchFraController.searchCatalog(keyword, status, category);
        String err = searchFraController.getLastErrorMessage();
        if (rows.isEmpty() && err != null && !err.isBlank()) {
            return Map.of("error", err, "rows", rows);
        }
        int userId = currentUserId(session);
        if (userId > 0) {
            Set<Integer> saved = searchFraController.getFavouritedFraIdsForUser(userId);
            for (Map<String, Object> row : rows) {
                Object idObj = row.get("fra_id");
                int fid = idObj instanceof Number ? ((Number) idObj).intValue() : 0;
                row.put("saved", saved.contains(fid));
            }
        } else {
            for (Map<String, Object> row : rows) {
                row.put("saved", false);
            }
        }
        return rows;
    }

    @GetMapping(value = "/api/donee/fra/filters", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onBrowseFilterOptions() {
        return searchFraController.getFilterOptions();
    }

    @GetMapping(value = "/api/donee/fra/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFra(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        Object data = viewFraController.viewFraDetails(fraId);
        if (data == null) {
            return Map.of("error", "FRA not found.");
        }
        return data;
    }

    @PostMapping(value = "/api/donee/favourites/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSaveFavourite(@RequestParam(value = "fraId", defaultValue = "0") int fraId, HttpSession session) {
        int userId = currentUserId(session);
        if (userId <= 0) {
            return Map.of("success", false, "error", "Not logged in.");
        }
        if (saveFavouriteController.saveToFavourite(fraId, userId)) {
            return Map.of("success", true, "message", "Saved to your favourites.");
        }
        return Map.of(
                "success", false,
                "error", defaultString(saveFavouriteController.getErrorMessage(), "Could not save.")
        );
    }

    @PostMapping(value = "/api/donee/favourites/unsave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onUnsaveFavourite(@RequestParam(value = "fraId", defaultValue = "0") int fraId, HttpSession session) {
        int userId = currentUserId(session);
        if (userId <= 0) {
            return Map.of("success", false, "error", "Not logged in.");
        }
        if (saveFavouriteController.removeFromFavourite(fraId, userId)) {
            return Map.of("success", true, "message", "Removed from your favourites.");
        }
        return Map.of(
                "success", false,
                "error", defaultString(saveFavouriteController.getErrorMessage(), "Could not remove.")
        );
    }

    @GetMapping(value = "/api/donee/favourites/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFavouriteFra(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        Object data = viewFavouriteFraController.viewFavouriteFraDetails(fraId);
        if (data == null) {
            return Map.of("error", "FRA not found.");
        }
        return data;
    }

    @GetMapping(value = "/api/donee/favourites/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFavourite(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "category", defaultValue = "all") String category,
            HttpSession session) {
        int userId = currentUserId(session);
        if (userId <= 0) {
            return Map.of("error", "Not logged in.");
        }
        return searchFavouriteController.searchFavourite(userId, keyword, status, category);
    }

    @GetMapping(value = "/api/donee/history/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchDoneeHistory(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "category", defaultValue = "all") String category,
            @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") String dateTo,
            @RequestParam(value = "status", defaultValue = "all") String status,
            HttpSession session) {
        int userId = currentUserId(session);
        if (userId <= 0) {
            return Map.of("error", "Not logged in.");
        }
        List<Map<String, Object>> rows = searchDoneeFraHistoryController.searchHistory(
                userId, keyword, category, dateFrom, dateTo, status);
        String err = defaultString(searchDoneeFraHistoryController.getErrorMessage(), "");
        if (rows.isEmpty() && !err.isBlank()) {
            return java.util.Map.of("error", err, "rows", rows);
        }
        return rows;
    }

    @GetMapping(value = { "/api/donee/history/list", "/api/donee/history/view" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewDoneeHistory(
            @RequestParam(value = "category", defaultValue = "all") String category,
            @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") String dateTo,
            @RequestParam(value = "status", defaultValue = "all") String status,
            HttpSession session) {
        int userId = currentUserId(session);
        if (userId <= 0) {
            return Map.of("error", "Not logged in.");
        }
        List<Map<String, Object>> rows = viewDoneeFraHistoryController.viewHistory(
                userId, category, dateFrom, dateTo, status);
        String err = defaultString(viewDoneeFraHistoryController.getErrorMessage(), "");
        if (rows.isEmpty() && !err.isBlank()) {
            return java.util.Map.of("error", err, "rows", rows);
        }
        return rows;
    }

    private static String defaultString(String s, String d) {
        return (s == null || s.isBlank()) ? d : s;
    }

    private static int currentUserId(HttpSession session) {
        if (session == null) {
            return 0;
        }
        Object o = session.getAttribute("userId");
        if (o == null) {
            return 0;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        try {
            return Integer.parseInt(Objects.toString(o, "0").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
