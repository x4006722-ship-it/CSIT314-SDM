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
    public List<FRA> onSearchInput(
            @RequestParam(value="criteria", required=false) String criteria,
            @RequestParam(value="categoryId", required=false) String categoryId,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="startDate", required=false) String startDate, 
            @RequestParam(value="endDate", required=false) String endDate, // 【新增】接收 endDate
            HttpSession session) {
    
        Object userIdObj = session.getAttribute("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : "";
    
        // 传递全部参数给 SearchController
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
    public FRA submitFRACreationData(@RequestBody FRA fraData, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return null; 
        }
        fraData.setFundRaiserId(String.valueOf(userIdObj));
        return createController.createFRA(fraData);
    }

    @PostMapping("/update/{fraId}")
    public boolean submitFRAUpdateData(@PathVariable("fraId") String fraId, @RequestBody FRA fraData) {
        return updateController.updateFRA(fraId, fraData);
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
}
