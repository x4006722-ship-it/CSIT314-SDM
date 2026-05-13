// package com.uow.userprofile;
// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class CreateUserProfileController {
    
//     public String createProfile(UserProfile profile) {
//         // 1. 基础校验 (防御性编程)
//         if (profile == null || profile.getRoleName() == null || profile.getRoleName().trim().isEmpty()) {
//             return "false";
//         }

//         // 2. 核心商业逻辑：查重校验 (Business Logic: Duplicate Check)
//         List<UserProfile> allProfiles = UserProfile.findAll(null, "all");
//         for (UserProfile p : allProfiles) {
//             if (p.getRoleName().equalsIgnoreCase(profile.getRoleName().trim())) {
//                 return "duplicate"; // 发现重复，拦截创建
//             }
//         }

//         // 3. 验证通过，交由 Entity 存储
//         profile.save();
//         return "true";
//     }
// }

package com.uow.userprofile;
import org.springframework.stereotype.Service;

@Service
public class CreateUserProfileController {
    
    // 严格返回 boolean
    public boolean createProfile(UserProfile profile) throws IllegalArgumentException {
        // 1. 防御性拦截
        if (profile == null || profile.getRoleName() == null || profile.getRoleName().trim().isEmpty()) {
            return false;
        }

        // 2. 商业逻辑决策：是否存在重名
        if (UserProfile.isDuplicateRole(profile.getRoleName(), null)) {
            // 违反业务规则，抛出异常
            throw new IllegalArgumentException("duplicate"); 
        }

        // 3. 决策通过，交由 Entity 存储，Entity 返回的也是 boolean
        return profile.save();
    }
}