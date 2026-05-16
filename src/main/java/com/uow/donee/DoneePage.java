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

    // Search FRA
    @GetMapping(value = "/api/donee/fra/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<FRA> onSearchFRA(
            @RequestParam(value = "criteria", defaultValue = "") String criteria,
            @RequestParam(value = "categoryId", defaultValue = "all") String categoryId,
            @RequestParam(value = "status", defaultValue = "all") String status,
            HttpSession session) {
        
        // 获取当前受赠人ID（即使没登录也允许搜索，只是 userId 传空字符串）
        Object sid = session.getAttribute("userId");
        String userId = sid != null ? String.valueOf(sid) : "";
        
        // 传入 5 个参数，并标记角色为 "donee"
        return searchFRAController.searchFRA(criteria, categoryId, status, "donee", userId);
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
