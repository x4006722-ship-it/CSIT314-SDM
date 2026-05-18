package com.uow.userprofile;
import org.springframework.stereotype.Service;

/**
 * Handles updates to user profiles (role name changes).
 * 
 * Responsibilities:
 * - Validate profile ID and new role name
 * - Check for duplicate role names (excluding current profile)
 * - Persist role name changes to database
 */
@Service
public class UpdateUserProfileController {
    
    // 严格返回 boolean
    /**
     * Updates a user profile's role name.
     * 
     * @param profileID The profile ID to update
     * @param newRoleName The new role name
     * @return true if profile was successfully updated
     * @throws IllegalArgumentException if new role name is a duplicate (excluding this profile)
     */
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