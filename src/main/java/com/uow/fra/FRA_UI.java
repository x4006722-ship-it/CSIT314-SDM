package com.uow.fra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/fra")
public class FRA_UI {

    @Autowired private CreateFRAController createController;
    @Autowired private RemoveFRAController removeController;
    @Autowired private SearchFRAController searchController;
    @Autowired private UpdateFRAController updateController;
    @Autowired private ViewFRAController viewController;
    @Autowired private ViewFRAViewCountController viewFRAViewCountController;
    @Autowired private ViewFRAFavoriteCountController viewFRAFavoriteCountController;
    @Autowired private SearchCompletedFRAByCategoryAndDateController searchCompletedFRAByCategoryAndDateController;

    public String showFRAPage() {
        return "forward:/FundRaiserPage.html";
    }

    public String showFRASuccessMessage(String message) {
        return message == null || message.isBlank() ? "Operation successful." : message;
    }

    public String showFRAErrorMessage(String message) {
        return message == null || message.isBlank() ? "Operation failed." : message;
    }

    @GetMapping("/view")
    public List<FRA> onViewAll(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return List.of();
        }
        return viewController.viewAllFRAs(String.valueOf(userIdObj));
    }

    @GetMapping("/search")
    public List<FRA> onSearchInput(
            @RequestParam(value = "criteria",  required = false) String criteria,
            @RequestParam(value = "categoryId",required = false) String categoryId,
            @RequestParam(value = "status",    required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : "";
        return searchController.searchFRA(criteria, categoryId, status, "fundRaiser", userId, startDate);
    }

    @GetMapping("/donee-options")
    public Object onGetDoneeOptions() {
        return viewController.getDoneeOptions();
    }

    @GetMapping("/fund-raiser-options")
    public Object onGetFundRaiserOptions() {
        return viewController.getFundRaiserOptions();
    }

    @PostMapping("/create")
    public FRA submitFRACreationData(@RequestBody FRA fraData, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return null;
        }
        if (fraData.getFraTitle() == null || fraData.getFraTitle().isBlank()
                || fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0
                || fraData.getCategoryId() == null || fraData.getCategoryId().isBlank()
                || fraData.getDoneeId() == null || fraData.getDoneeId().isBlank()
                || fraData.getStartedAt() == null || fraData.getStartedAt().isBlank()
                || fraData.getEndedAt() == null || fraData.getEndedAt().isBlank()) {
            return null;
        }
        fraData.setFundRaiserId(String.valueOf(userIdObj));
        return createController.createFRA(fraData);
    }

    @PostMapping("/update/{fraId}")
    public boolean submitFRAUpdateData(@PathVariable("fraId") String fraId, @RequestBody FRA fraData) {
        if (fraId == null || fraId.isBlank()
                || fraData == null
                || fraData.getFraTitle() == null || fraData.getFraTitle().isBlank()
                || fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0
                || fraData.getStartedAt() == null || fraData.getStartedAt().isBlank()
                || fraData.getEndedAt() == null || fraData.getEndedAt().isBlank()) {
            return false;
        }
        return updateController.updateFRA(fraId, fraData);
    }

    @DeleteMapping("/delete/{fraId}")
    public boolean confirmFRADeletion(@PathVariable("fraId") String fraId) {
        if (fraId == null || fraId.isBlank()) {
            return false;
        }
        return removeController.deleteFRA(fraId);
    }

    @GetMapping("/engagement/views")
    public List<FRA> onViewFRAViewCounts() {
        return viewFRAViewCountController.viewFRAViewCounts();
    }

    @GetMapping("/engagement/favorites")
    public List<FRA> onViewFRAFavoriteCounts() {
        return viewFRAFavoriteCountController.viewFRAFavoriteCounts();
    }

    @GetMapping("/completed/search")
    public List<FRA> onSearchCompletedByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "startDate",  required = false) String startDate,
            @RequestParam(value = "endDate",    required = false) String endDate) {
        return searchCompletedFRAByCategoryAndDateController.searchCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }

    @GetMapping("/completed/view")
    public List<FRA> onViewCompletedByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "startDate",  required = false) String startDate,
            @RequestParam(value = "endDate",    required = false) String endDate) {
        return searchCompletedFRAByCategoryAndDateController.searchCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }
}
