package com.uow.donee;

import java.util.List;

import com.uow.fra.FRA;
import com.uow.fra.SearchFRAController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DoneePage {

    @Autowired private SearchFRAController searchFRAController;
    @Autowired private SaveFavouriteController saveFavouriteController;
    @Autowired private ViewFavouriteController viewFavouriteController;
    @Autowired private SearchFavouriteController searchFavouriteController;
    @Autowired private SearchDonationController searchDonationController;
    @Autowired private ViewDonationController viewDonationController;

    @GetMapping({ "/donee", "/showDoneePage" })
    public String showDoneePage() {
        return "forward:/DoneePage.html";
    }

    public String showSaveSuccessMessage() {
        return "Saved to favourite successfully.";
    }
    
    @GetMapping(value = "/api/donee/fra/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FRA> onSearchFRA(
            @RequestParam(value = "criteria", defaultValue = "") String criteria,
            @RequestParam(value = "categoryId", defaultValue = "all") String categoryId,
            @RequestParam(value = "status", defaultValue = "all") String status,
            @RequestParam(value = "startDate", defaultValue = "") String startDate, 
            @RequestParam(value = "endDate", defaultValue = "") String endDate,
            HttpSession session) {
    
        Object sid = (session != null) ? session.getAttribute("userId") : null;
        String userId = sid != null ? String.valueOf(sid).trim() : "";
    
        // Clean and sanitize string inputs before routing to business controller
        String cleanCriteria = criteria == null ? "" : criteria.trim();
        String cleanCategory = categoryId == null ? "all" : categoryId.trim();
        String cleanStatus = status == null ? "all" : status.trim();
        String cleanStart = startDate == null ? "" : startDate.trim();
        String cleanEnd = endDate == null ? "" : endDate.trim();

        return searchFRAController.searchFRA(cleanCriteria, cleanCategory, cleanStatus, "donee", userId, cleanStart, cleanEnd);
    }

    // View FRA
    @GetMapping(value = "/api/donee/fra/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFRA(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        // Fast-fail check for invalid resource identifier
        if (fraId <= 0) {
            return Map.of("error", "Invalid identifier identifier.");
        }
        return viewDonationController.viewDonation(fraId);
    }

    // Save Favourite
    @PostMapping(value = "/api/donee/favourites/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean onSaveFavourite(
            @RequestParam(value = "fraId", defaultValue = "0") int fraId,
            @RequestParam(value = "userId", defaultValue = "0") int userId,
            @RequestParam(value = "remove", defaultValue = "false") boolean remove,
            HttpSession session) {
            
        if (userId <= 0 && session != null) {
            Object sid = session.getAttribute("userId");
            if (sid != null) {
                try {
                    userId = Integer.parseInt(String.valueOf(sid).trim());
                } catch (NumberFormatException e) {
                    userId = 0;
                }
            } else {
                return false; 
            }
        }
        // Basic resource check before passing data down
        if (fraId <= 0 || userId <= 0) {
            return false;
        }
        return saveFavouriteController.saveFavourite(fraId, userId, remove);
    }

    // View Favourite
    @GetMapping(value = "/api/donee/favourites/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFavourite(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        if (fraId <= 0) {
            return null; 
        }
        return viewFavouriteController.viewFavourite(fraId);
    }

    // Search Favourite
    @PostMapping(value = "/api/donee/favourites/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFavourite(@RequestBody Object searchFavouriteData, HttpSession session) {
        if (searchFavouriteData instanceof Map<?, ?> raw) {
            Map<String, Object> payload = new HashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                Object val = e.getValue();
                // Clean and trim text entries inside the payload map
                if (val instanceof String str) {
                    val = str.trim();
                }
                payload.put(String.valueOf(e.getKey()).trim(), val);
            }
            if (!payload.containsKey("userId") && session != null) {
                Object sid = session.getAttribute("userId");
                if (sid != null) {
                    try {
                        payload.put("userId", Integer.parseInt(String.valueOf(sid).trim()));
                    } catch (NumberFormatException e) { }
                }
            }
            searchFavouriteData = payload;
        }
        return searchFavouriteController.searchFavourite(searchFavouriteData);
    }

    // Search Donation
    @PostMapping(value = "/api/donee/history/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchDonation(@RequestBody Object searchDonationData, HttpSession session) {
        if (searchDonationData instanceof Map<?, ?> raw) {
            Map<String, Object> payload = new HashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                Object val = e.getValue();
                // Clean and trim text entries inside the payload map
                if (val instanceof String str) {
                    val = str.trim();
                }
                payload.put(String.valueOf(e.getKey()).trim(), val);
            }
            if (!payload.containsKey("userId") && session != null) {
                Object sid = session.getAttribute("userId");
                if (sid != null) {
                    try {
                        payload.put("userId", Integer.parseInt(String.valueOf(sid).trim()));
                    } catch (NumberFormatException e) { }
                }
            }
            searchDonationData = payload;
        }
        return searchDonationController.searchDonation(searchDonationData);
    }

    // View Donation
    @GetMapping(value = "/api/donee/donation/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewDonation(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        if (fraId <= 0) {
            return null;
        }
        return viewDonationController.viewDonation(fraId);
    }
}