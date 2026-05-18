package com.uow.userprofile;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Handles searching for user profiles with filter criteria.
 * 
 * Responsibilities:
 * - Accept search/filter parameters
 * - Delegate to UserProfile entity for database query
 * - Return list of matching profiles
 */
@Service
public class SearchUserProfileController {
    /**
     * Searches for user profiles with optional filter criteria.
     * 
     * @param keyword Role name search keyword (partial match); null to skip
     * @param status Profile status filter ("Active", "Suspended", or "all")
     * @return List of UserProfile objects matching the criteria
     */
    public List<UserProfile> searchProfiles(String keyword, String status) {
        return UserProfile.findAll(keyword, status);
    }
}
