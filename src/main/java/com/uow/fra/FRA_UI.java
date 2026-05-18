package com.uow.fra;

import com.uow.useraccount.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/fra")
public class FRA_UI {

    private final CreateFRAController createController;
    private final RemoveFRAController removeController; 
    private final SearchFRAController searchController;
    private final UpdateFRAController updateController;
    private final ViewFRAController viewController;
    private final ViewFRAViewCountController viewFRAViewCountController;
    private final ViewFRAFavoriteCountController viewFRAFavoriteCountController;
    private final SearchCompletedFRAByCategoryAndDateController searchCompletedFRAByCategoryAndDateController;
    private final ViewCompletedFRAByCategoryAndDateController viewCompletedFRAByCategoryAndDateController;
    private final UserAccount userAccount = new UserAccount();

    @Autowired
    public FRA_UI(CreateFRAController createController, 
                  RemoveFRAController removeController, 
                  SearchFRAController searchController, 
                  UpdateFRAController updateController, 
                  ViewFRAController viewController,
                  ViewFRAViewCountController viewFRAViewCountController,
                  ViewFRAFavoriteCountController viewFRAFavoriteCountController,
                  SearchCompletedFRAByCategoryAndDateController searchCompletedFRAByCategoryAndDateController,
                  ViewCompletedFRAByCategoryAndDateController viewCompletedFRAByCategoryAndDateController) {
        this.createController = createController;
        this.removeController = removeController;
        this.searchController = searchController;
        this.updateController = updateController;
        this.viewController = viewController;
        this.viewFRAViewCountController = viewFRAViewCountController;
        this.viewFRAFavoriteCountController = viewFRAFavoriteCountController;
        this.searchCompletedFRAByCategoryAndDateController = searchCompletedFRAByCategoryAndDateController;
        this.viewCompletedFRAByCategoryAndDateController = viewCompletedFRAByCategoryAndDateController;
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
    public Object onSearchInput(
            @RequestParam(value="criteria", required=false) String criteria,
            @RequestParam(value="categoryId", required=false) String categoryId,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="startDate", required=false) String startDate, 
            @RequestParam(value="endDate", required=false) String endDate, 
            HttpSession session) {

        if ((!isBlank(startDate) && isBlank(endDate)) || (isBlank(startDate) && !isBlank(endDate))) {
            return java.util.Map.of("error", "Please provide both Start and End dates for range search.");
        }

        if (!isBlank(startDate) && !isBlank(endDate) && startDate.compareTo(endDate) > 0) {
            return java.util.Map.of("error", "The 'From' date cannot be later than the 'To' date.");
        }

        Object userIdObj = session.getAttribute("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : "";
    
        return searchController.searchFRA(criteria, categoryId, status, "fundRaiser", userId, startDate, endDate);
    }
    

    @GetMapping("/donee-options")
    public Object onGetDoneeOptions() {
        return userAccount.getDoneeOptions();
    }

    @GetMapping("/fund-raiser-options")
    public Object onGetFundRaiserOptions() {
        return userAccount.getFundRaiserOptions();
    }

    @PostMapping("/create")
    public Object submitFRACreationData(@RequestBody FRA fraData, HttpSession session) {
        
        // STEP 1: Boundary Validation & Uniqueness Check
        if (fraData == null) {
            return java.util.Map.of("error", "FRA data is missing.");
        }
        if (isBlank(fraData.getFraTitle())) {
            return java.util.Map.of("error", "Campaign title cannot be empty.");
        }
        
        // Precise Duplicate Title Check for Creation
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), null)) {
            return java.util.Map.of("error", "This campaign title already exists. Please choose a unique name.");
        }

        if (fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0) {
            return java.util.Map.of("error", "Target amount must be greater than zero.");
        }
        if (fraData.getFraTargetAmount() > 1000000000.0) {
            return java.util.Map.of("error", "Target amount exceeds the maximum limit of $1,000,000,000.");
        }
        if (isBlank(fraData.getDoneeId())) {
            return java.util.Map.of("error", "Please select a valid donee.");
        }
        if (isBlank(fraData.getCategoryId())) {
            return java.util.Map.of("error", "Please select a campaign category.");
        }
        if (isBlank(fraData.getStartedAt()) || isBlank(fraData.getEndedAt())) {
            return java.util.Map.of("error", "Both start date and end date are required.");
        }

        // Validate that the start date is not in the past
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate startDate = java.time.LocalDate.parse(fraData.getStartedAt().substring(0, 10));
            if (startDate.isBefore(today)) {
                return java.util.Map.of("error", "Start date cannot be earlier than today.");
            }
        } catch (Exception e) {
            return java.util.Map.of("error", "Invalid date format submitted.");
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return java.util.Map.of("error", "Session expired. Please log in again.");
        }
        fraData.setFundRaiserId(String.valueOf(userIdObj));

        // STEP 2: Execute Business Logic
        FRA result = createController.createFRA(fraData);

        if (result != null) {
            return result; 
        } else {
            if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
                return java.util.Map.of("error", "Invalid dates: The start date must be before the end date.");
            }
            return java.util.Map.of("error", "System error: Failed to save the campaign.");
        }
    }

    @PostMapping("/update/{fraId}")
    public Object submitFRAUpdateData(@PathVariable("fraId") String fraId, @RequestBody FRA fraData) {
        // STEP 1: Boundary & Duplicate Checks for Update 
  
        if (fraData == null || isBlank(fraId)) {
            return java.util.Map.of("error", "Update data is missing.");
        }
        if (isBlank(fraData.getFraTitle())) {
            return java.util.Map.of("error", "Campaign title cannot be empty.");
        }

        // Precise Duplicate Title Check for Update (Excluding current campaign itself)
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return java.util.Map.of("error", "This campaign title already exists on another campaign. Please choose a unique name.");
        }

        if (fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0) {
            return java.util.Map.of("error", "Target amount must be greater than zero.");
        }
        if (fraData.getFraTargetAmount() > 1000000000.0) {
            return java.util.Map.of("error", "Target amount exceeds the maximum limit of $1,000,000,000.");
        }
        if (isBlank(fraData.getStartedAt()) || isBlank(fraData.getEndedAt())) {
            return java.util.Map.of("error", "Both start date and end date are required.");
        }
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return java.util.Map.of("error", "Invalid dates: The start date must be before the end date.");
        }

        // Validate start date is not in the past during update
        try {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate startDate = java.time.LocalDate.parse(fraData.getStartedAt().substring(0, 10));
            if (startDate.isBefore(today)) {
                return java.util.Map.of("error", "Start date cannot be earlier than today.");
            }
        } catch (Exception e) {
            return java.util.Map.of("error", "Invalid date format submitted.");
        }

        // STEP 2: Execute Update via Controller
        boolean success = updateController.updateFRA(fraId, fraData);
        if (success) {
            return java.util.Map.of("success", true);
        } else {
            return java.util.Map.of("error", "Failed to update campaign. The record might have been removed.");
        }
    }

    @DeleteMapping("/delete/{fraId}")
    public boolean confirmFRADeletion(@PathVariable("fraId") String fraId) {
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
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return searchCompletedFRAByCategoryAndDateController.searchCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }

    @GetMapping("/completed/view")
    public List<FRA> onViewCompletedByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return viewCompletedFRAByCategoryAndDateController.viewCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
