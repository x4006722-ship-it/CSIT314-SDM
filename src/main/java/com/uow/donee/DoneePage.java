package com.uow.donee;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @Autowired private SearchFraController searchFraController;
    @Autowired private ViewFraController viewFraController;
    @Autowired private SaveFavouriteController saveFavouriteController;
    @Autowired private ViewFavouriteController viewFavouriteController;
    @Autowired private SearchFavouriteController searchFavouriteController;
    @Autowired private SearchDonationController searchDonationController;
    @Autowired private ViewDonationController viewDonationController;

    @GetMapping({ "/donee", "/showDoneePage" })
    public String showDoneePage() {
        return "forward:/DoneePage.html";
    }

    public String showSaveSuccessMessage(String message) {
        String msg = message == null ? "" : message;
        return "redirect:/DoneePage.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    public String showSaveErrorMessage(String message) {
        String msg = message == null || message.isBlank() ? "Failed to update favourites." : message;
        return "redirect:/DoneePage.html?toast=" + URLEncoder.encode(msg, StandardCharsets.UTF_8);
    }

    @GetMapping(value = "/api/donee/fra/browse", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchFra(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            HttpSession session) {
        Object _o = session == null ? null : session.getAttribute("userId");
        int uid = _o instanceof Number n ? n.intValue() : 0;
        return searchFraController.searchFra(uid, title, fraStatus, categoryName);
    }

    @GetMapping(value = "/api/donee/fra/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewFra(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewFraController.viewFra(fraId);
    }

    @PostMapping(value = "/api/donee/favourites/save")
    public String onSaveFavourite(
            @RequestParam(value = "fraId", defaultValue = "0") int fraId,
            @RequestParam(value = "remove", defaultValue = "false") boolean remove,
            HttpSession session) {
        Object _o = session == null ? null : session.getAttribute("userId");
        int uid = _o instanceof Number n ? n.intValue() : 0;
        boolean ok = saveFavouriteController.saveFavourite(uid, fraId, remove);
        return ok ? showSaveSuccessMessage(remove ? "Removed from favourites." : "Saved to favourites.")
                  : showSaveErrorMessage(saveFavouriteController.donee.lastErrorMessage);
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
        Object _o = session == null ? null : session.getAttribute("userId");
        int uid = _o instanceof Number n ? n.intValue() : 0;
        return searchFavouriteController.searchFavourite(uid, title, fraStatus, categoryName);
    }

    @GetMapping(value = "/api/donee/history/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onSearchDonation(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "categoryName", defaultValue = "all") String categoryName,
            @RequestParam(value = "fraStatus", defaultValue = "all") String fraStatus,
            HttpSession session) {
        Object _o = session == null ? null : session.getAttribute("userId");
        int uid = _o instanceof Number n ? n.intValue() : 0;
        return searchDonationController.searchDonation(uid, title, categoryName, fraStatus);
    }

    @GetMapping(value = "/api/donee/donation/view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object onViewDonation(@RequestParam(value = "fraId", defaultValue = "0") int fraId) {
        return viewDonationController.viewDonation(fraId);
    }
}
