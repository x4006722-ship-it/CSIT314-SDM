package com.uow.donee;

import java.util.Objects;

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
    private ViewFavouriteController viewFavouriteController;

    @Autowired
    private SaveFavouriteController saveFavouriteController;

    @Autowired
    private SearchFavouriteController searchFavouriteController;

    @Autowired
    private SearchDonationController searchDonationController;

    @Autowired
    private ViewDonationController viewDonationController;

    @GetMapping({ "/donee", "/showDoneePage" })
    public String showDoneePage() {
        return "forward:/DoneePage.html";
    }

    private int userId(HttpSession session) {
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

    @GetMapping(value = "/api/donee/fra/browse", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFra(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            HttpSession session) {
        return searchFraController.searchFra(userId(session), title, fraStatus, categoryName);
    }

    @GetMapping(value = "/api/donee/fra/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFra(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewFraController.viewFra(fraId);
    }

    @PostMapping(value = "/api/donee/favourites/save", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSaveFavourite(
            @RequestParam(value = "fraId", defaultValue = "0") int fraId,
            @RequestParam(value = "remove", defaultValue = "false") boolean remove,
            HttpSession session) {
        return saveFavouriteController.saveFavourite(userId(session), fraId, remove);
    }

    @GetMapping(value = "/api/donee/favourites/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFavourite(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewFavouriteController.viewFavourite(fraId);
    }

    @GetMapping(value = "/api/donee/favourites/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFavourite(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            HttpSession session) {
        return searchFavouriteController.searchFavourite(
                userId(session), title, fraStatus, categoryName);
    }

    @GetMapping(value = "/api/donee/history/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchDonation(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus,
            HttpSession session) {
        return searchDonationController.searchDonation(
                userId(session), title, categoryName, fraStatus);
    }

    @GetMapping(value = "/api/donee/donation/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewDonation(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewDonationController.viewDonation(fraId);
    }
}
