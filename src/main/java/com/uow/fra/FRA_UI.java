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

    // @GetMapping("/view")
    // public List<FRA> onViewAll() {
    //     return viewController.viewAllFRAs();
    // }

    // @GetMapping("/search")
    // public List<FRA> onSearchInput(@RequestParam(value="criteria", required=false) String criteria) {
    //     return searchController.searchFRA(criteria);
    // }
    // @GetMapping("/view")
    // public List<FRA> onViewAll(HttpSession session) {
    //     // 修复：必须传入当前登录的 fundraiser ID，防止越权看到别人的数据
    //     Object userIdObj = session.getAttribute("userId");
    //     if (userIdObj == null) return List.of(); 
    //     return FRA.findAllFRAs(String.valueOf(userIdObj));
    // }
    @GetMapping("/view")
    public List<FRA> onViewAll(HttpSession session) {
        // 1. 从 Session 获取当前登录的筹款人 ID
        Object userIdObj = session.getAttribute("userId");
        
        // 2. 如果没登录，返回空列表（安全兜底）
        if (userIdObj == null) {
            return List.of(); 
        }
        
        // 3. 将 ID 传给你刚修改好的 viewController
        return viewController.viewAllFRAs(String.valueOf(userIdObj));
    }

    @GetMapping("/search")
    public List<FRA> onSearchInput(
            @RequestParam(value="criteria", required=false) String criteria,
            @RequestParam(value="categoryId", required=false) String categoryId,
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="startDate", required=false) String startDate, // 【新增】接收前端传来的 startDate
            HttpSession session) {
    
        Object userIdObj = session.getAttribute("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : "";
    
        // 【修改】把 startDate 作为第 6 个参数传给 searchController
        return searchController.searchFRA(criteria, categoryId, status, "fundRaiser", userId, startDate);
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
    // 1. 从 Session 中获取当前登录用户的 ID
    // （注意：这里的 "userId" 必须与你登录时存入 Session 的键名一致，有可能是 Integer 或者 String，根据你的实际情况强转）
        Object userIdObj = session.getAttribute("userId");
    
    // 2. 安全校验：如果用户没登录，直接拒绝创建
        if (userIdObj == null) {
            return null; // 或者抛出个异常，前端会收到错误
        }
    
        fraData.setFundRaiserId(String.valueOf(userIdObj));
    
    // 4. 保存到数据库
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
