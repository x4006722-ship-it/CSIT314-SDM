package com.uow.donee;

import java.util.List;
import java.util.Map;

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

    /** Active categories from {@code fra_category} (same list as Platform) — for search dropdowns. */
    @GetMapping(value = "/api/donee/fra/category-options", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> onFraCategoryOptions() {
        return FRA.findAllActiveFraCategoriesForDropdown();
    }

    // Search FRA
    @GetMapping(value = "/api/donee/fra/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FRA> onSearchFRA(
            @RequestParam(value = "criteria", defaultValue = "") String criteria,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus) {
        return searchFRAController.searchFRA(criteria, categoryName, fraStatus);
    }

    // View FRA
    @GetMapping(value = "/api/donee/fra/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFRA(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
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
            if (sid instanceof Number n) {
                userId = n.intValue();
            }
        }
        return saveFavouriteController.saveFavourite(fraId, userId, remove);
    }

    // View Favourite
    @GetMapping(value = "/api/donee/favourites/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFavourite(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewFavouriteController.viewFavourite(fraId);
    }

    // Search Favourite
    @PostMapping(value = "/api/donee/favourites/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFavourite(@RequestBody Object searchFavouriteData, HttpSession session) {
        if (searchFavouriteData instanceof Map<?, ?> raw) {
            Map<String, Object> payload = new HashMap<>();
            for (Map.Entry<?, ?> e : raw.entrySet()) {
                payload.put(String.valueOf(e.getKey()), e.getValue());
            }
            if (!payload.containsKey("userId") && session != null) {
                Object sid = session.getAttribute("userId");
                if (sid instanceof Number n) {
                    payload.put("userId", n.intValue());
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
                payload.put(String.valueOf(e.getKey()), e.getValue());
            }
            if (!payload.containsKey("userId") && session != null) {
                Object sid = session.getAttribute("userId");
                if (sid instanceof Number n) {
                    payload.put("userId", n.intValue());
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
        return viewDonationController.viewDonation(fraId);
    }
}
