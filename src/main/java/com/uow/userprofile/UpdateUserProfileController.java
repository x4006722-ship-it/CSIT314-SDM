// package com.uow.userprofile;
// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class UpdateUserProfileController {
    
//     public String updateProfile(String profileID, String newRoleName) {
//         // 1. 基础校验
//         if (newRoleName == null || newRoleName.trim().isEmpty() || profileID == null) {
//             return "false";
//         }

//         // 2. 核心商业逻辑：查重校验 (Business Logic: Duplicate Check excluding self)
//         List<UserProfile> allProfiles = UserProfile.findAll(null, "all");
//         for (UserProfile p : allProfiles) {
//             // 如果名字相同，且不是当前正在修改的 ID，则判定为重名
//             if (p.getRoleName().equalsIgnoreCase(newRoleName.trim()) && !p.getProfileId().equals(profileID)) {
//                 return "duplicate";
//             }
//         }

//         // 3. 验证通过，交由 Entity 执行更新
//         UserProfile profile = UserProfile.findByID(profileID);
//         if (profile == null) {
//             return "false";
//         }
        
//         return profile.updateRoleName(newRoleName.trim()) ? "true" : "false";
//     }
// }

package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserProfileController {
    
    // 严格返回 boolean
    public boolean updateProfile(String profileID, String newRoleName) throws IllegalArgumentException {
        // 1. 防御性拦截
        if (newRoleName == null || newRoleName.trim().isEmpty() || profileID == null) {
            return false;
        }

        // 2. 商业逻辑决策：排除自身后，是否与别人重名
        if (UserProfile.isDuplicateRole(newRoleName, profileID)) {
            // 违反业务规则，抛出异常
            throw new IllegalArgumentException("duplicate");
        }

        // 3. 决策通过，交由 Entity 执行更新
        UserProfile profile = UserProfile.findByID(profileID);
        if (profile == null) return false;
        
        return profile.updateRoleName(newRoleName.trim());
    }
}