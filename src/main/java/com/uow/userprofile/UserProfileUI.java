// // package com.uow.userprofile;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.web.bind.annotation.*;
// // import java.util.List;

// // @RestController
// // @RequestMapping("/api/profiles")
// // @CrossOrigin(originPatterns = "*") 
// // public class UserProfileUI {

// //     private final CreateUserProfileController createController;
// //     private final ViewUserProfileController viewController;
// //     private final SearchUserProfileController searchController;
// //     private final UpdateUserProfileController updateController;
// //     private final SuspendUserProfileController suspendController;

// //     @Autowired
// //     public UserProfileUI(
// //             CreateUserProfileController createController,
// //             ViewUserProfileController viewController,
// //             SearchUserProfileController searchController,
// //             UpdateUserProfileController updateController,
// //             SuspendUserProfileController suspendController) {
// //         this.createController = createController;
// //         this.viewController = viewController;
// //         this.searchController = searchController;
// //         this.updateController = updateController;
// //         this.suspendController = suspendController;
// //     }

// //     @PostMapping("/create")
// //     public String onCreateProfile(
// //             @RequestParam("roleName") String roleName,
// //             @RequestParam("status") String status) {
// //         // Boundary 只组装数据，直接交给 Controller 去做逻辑判断和创建
// //         UserProfile newProfile = new UserProfile(roleName, status);
// //         return createController.createProfile(newProfile);
// //     }

// //     @GetMapping("/view/{profileID}")
// //     public UserProfile onViewProfileClick(@PathVariable("profileID") String profileID) {
// //         return viewController.getProfileDetails(profileID);
// //     }

// //     @GetMapping({"/list", "/search"})
// //     public List<UserProfile> onSearchInput(
// //             @RequestParam(value = "keyword", required = false) String keyword,
// //             @RequestParam(value = "status", required = false) String status) {
// //         return searchController.searchProfiles(keyword, status);
// //     }

// //     @PostMapping("/update-role/{profileID}")
// //     public String onUpdateProfileClick(
// //             @PathVariable("profileID") String profileID,
// //             @RequestParam("newRoleName") String newRoleName) {
// //         // Boundary 直接交由 Controller 处理更新与查重
// //         return updateController.updateProfile(profileID, newRoleName);
// //     }

// //     @PostMapping("/suspend/{profileID}")
// //     public String onSuspendProfileClick(@PathVariable("profileID") String profileID) {
// //         return suspendController.suspendProfile(profileID) ? "true" : "false";
// //     }

// //     @PostMapping("/reactivate/{profileID}")
// //     public String onReactivateProfileClick(@PathVariable("profileID") String profileID) {
// //         return suspendController.reactivateProfile(profileID) ? "true" : "false";
// //     }
// // }
// package com.uow.userprofile;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.util.ArrayList;
// import java.util.List;
// import com.uow.util.DBUtils;

// public class UserProfile {
//     private String profileId;
//     private String roleName;
//     private String status;

//     public UserProfile() {}

//     public UserProfile(String roleName, String status) {
//         this.roleName = roleName;
//         this.status = status;
//     }

//     public UserProfile(String profileId, String roleName, String status) {
//         this.profileId = profileId;
//         this.roleName = roleName;
//         this.status = status;
//     }

//     public String getProfileId() { return profileId; } 
//     public String getRoleName() { return roleName; }
//     public String getStatus() { return status; }

//     // 返回 boolean
//     public boolean save() {
//         String sql = "INSERT INTO user_profile (role, p_status) VALUES (?, ?)";
//         try (Connection conn = DBUtils.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, this.roleName);
//             pstmt.setString(2, this.status);
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) { 
//             return false; 
//         }
//     }

//     // ====== 新增的底层查重接口，返回 boolean ======
//     public static boolean isDuplicateRole(String roleName, String excludeProfileId) {
//         String sql = "SELECT COUNT(*) FROM user_profile WHERE LOWER(role) = LOWER(?)";
//         if (excludeProfileId != null) {
//             sql += " AND profile_id != ?";
//         }
        
//         try (Connection conn = DBUtils.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
//             pstmt.setString(1, roleName.trim());
//             if (excludeProfileId != null) {
//                 pstmt.setString(2, excludeProfileId);
//             }
            
//             try (ResultSet rs = pstmt.executeQuery()) {
//                 if (rs.next()) {
//                     return rs.getInt(1) > 0;
//                 }
//             }
//         } catch (SQLException e) { }
//         return false;
//     }
//     // ==============================================

//     public static UserProfile findByID(String profileID) {
//         String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE profile_id = ?";
//         try (Connection conn = DBUtils.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, profileID);
//             try (ResultSet rs = pstmt.executeQuery()) {
//                 if (rs.next()) {
//                     return new UserProfile(rs.getString("profile_id"), rs.getString("role"), rs.getString("p_status"));
//                 }
//             }
//         } catch (SQLException e) { }
//         return null;
//     }

//     public static List<UserProfile> findAll(String keyword, String status) {
//         List<UserProfile> list = new ArrayList<>();
//         String sql = "SELECT profile_id, role, p_status FROM user_profile WHERE 1=1";
        
//         if (keyword != null && !keyword.trim().isEmpty()) {
//             sql += " AND role LIKE '%" + keyword.trim() + "%'";
//         }
//         if (status != null && !status.equals("all")) {
//             sql += " AND p_status = '" + status + "'";
//         }

//         try (Connection conn = DBUtils.getConnection();
//              Statement stmt = conn.createStatement();
//              ResultSet rs = stmt.executeQuery(sql)) {
//             while (rs.next()) {
//                 list.add(new UserProfile(rs.getString("profile_id"), rs.getString("role"), rs.getString("p_status")));
//             }
//         } catch (SQLException e) { }
//         return list;
//     }

//     // 返回 boolean
//     public boolean updateRoleName(String newRoleName) {
//         String sql = "UPDATE user_profile SET role = ? WHERE profile_id = ?";
//         try (Connection conn = DBUtils.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, newRoleName);
//             pstmt.setString(2, this.profileId);
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) { 
//             return false;
//         }
//     }

//     // 返回 boolean
//     public boolean updateStatus(String newStatus) {
//         String sql = "UPDATE user_profile SET p_status = ? WHERE profile_id = ?";
//         try (Connection conn = DBUtils.getConnection();
//              PreparedStatement pstmt = conn.prepareStatement(sql)) {
//             pstmt.setString(1, newStatus);
//             pstmt.setString(2, this.profileId);
//             return pstmt.executeUpdate() > 0;
//         } catch (SQLException e) { 
//             return false;
//         }
//     }
// }
package com.uow.userprofile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(originPatterns = "*") 
public class UserProfileUI {

    private final CreateUserProfileController createController;
    private final ViewUserProfileController viewController;
    private final SearchUserProfileController searchController;
    private final UpdateUserProfileController updateController;
    private final SuspendUserProfileController suspendController;

    @Autowired
    public UserProfileUI(
            CreateUserProfileController createController,
            ViewUserProfileController viewController,
            SearchUserProfileController searchController,
            UpdateUserProfileController updateController,
            SuspendUserProfileController suspendController) {
        this.createController = createController;
        this.viewController = viewController;
        this.searchController = searchController;
        this.updateController = updateController;
        this.suspendController = suspendController;
    }

    @PostMapping("/create")
    public String onCreateProfile(
            @RequestParam("roleName") String roleName,
            @RequestParam("status") String status) {
            
        UserProfile newProfile = new UserProfile(roleName, status);
        try {
            // Controller 如今返回的是纯纯的 boolean
            boolean success = createController.createProfile(newProfile);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            // Controller 抛出异常，Boundary 负责将其翻译成前端能懂的字符串
            return e.getMessage(); // 输出 "duplicate"
        }
    }

    @GetMapping("/view/{profileID}")
    public UserProfile onViewProfileClick(@PathVariable("profileID") String profileID) {
        return viewController.getProfileDetails(profileID);
    }

    @GetMapping({"/list", "/search"})
    public List<UserProfile> onSearchInput(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        return searchController.searchProfiles(keyword, status);
    }

    @PostMapping("/update-role/{profileID}")
    public String onUpdateProfileClick(
            @PathVariable("profileID") String profileID,
            @RequestParam("newRoleName") String newRoleName) {
            
        try {
            // Controller 如今返回的是纯粹的 boolean
            boolean success = updateController.updateProfile(profileID, newRoleName);
            return success ? "true" : "false";
        } catch (IllegalArgumentException e) {
            return e.getMessage(); // 输出 "duplicate"
        }
    }

    @PostMapping("/suspend/{profileID}")
    public String onSuspendProfileClick(@PathVariable("profileID") String profileID) {
        return suspendController.suspendProfile(profileID) ? "true" : "false";
    }

    @PostMapping("/reactivate/{profileID}")
    public String onReactivateProfileClick(@PathVariable("profileID") String profileID) {
        return suspendController.reactivateProfile(profileID) ? "true" : "false";
    }
}