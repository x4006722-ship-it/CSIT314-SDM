package com.uow.FRA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public List<FRA> onViewAll() {
        return viewController.viewAllFRAs();
    }

    @GetMapping("/search")
    public List<FRA> onSearchInput(@RequestParam(value="criteria", required=false) String criteria) {
        return searchController.searchFRA(criteria);
    }

    @PostMapping("/create")
    public String submitFRACreationData(@RequestBody FRA fraData) {
        return createController.createFRA(fraData);
    }

    @PostMapping("/update/{fraId}")
    public String submitFRAUpdateData(@PathVariable String fraId, @RequestBody FRA fraData) {
        return updateController.updateFRA(fraId, fraData);
    }

    @DeleteMapping("/delete/{fraId}")
    public String confirmFRADeletion(@PathVariable String fraId) {
        return removeController.deleteFRA(fraId);
    }

    /** US: Fund raiser views FRA view counts to measure interest. */
    @GetMapping("/engagement/views")
    public List<FRA> onViewFRAViewCounts() {
        return viewFRAViewCountController.viewFRAViewCounts();
    }

    /** US: Fund raiser views favourite-save counts to track donor engagement. */
    @GetMapping("/engagement/favorites")
    public List<FRA> onViewFRAFavoriteCounts() {
        return viewFRAFavoriteCountController.viewFRAFavoriteCounts();
    }

    /** US: Search completed FRAs by category and date period (ended date). */
    @GetMapping("/completed/search")
    public List<FRA> onSearchCompletedByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return searchCompletedFRAByCategoryAndDateController.searchCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }

    /** US: View completed FRAs by category and date period for performance review. */
    @GetMapping("/completed/view")
    public List<FRA> onViewCompletedByCategoryAndDate(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        return viewCompletedFRAByCategoryAndDateController.viewCompletedByCategoryAndDatePeriod(
                categoryId, startDate, endDate);
    }
}