package com.uow.donee;

import org.springframework.stereotype.Component;

/**
 * Handles saving/removing FRAs from user favorites.
 * 
 * Responsibilities:
 * - Validate FRA ID and user ID
 * - Support both adding and removing favorites
 * - Delegate to Favourite entity for database operations
 */
@Component
public class SaveFavouriteController {

    private final Favourite favourite = new Favourite();

    /**
     * Adds or removes an FRA from a user's favorites.
     * 
     * @param fraId The FRA ID to favorite/unfavorite
     * @param userId The user ID managing their favorites
     * @param remove If true, removes from favorites; if false, adds to favorites
     * @return true if operation succeeded, false if validation fails
     */
    public boolean saveFavourite(int fraId, int userId, boolean remove) {
        // 边界验证
        if (fraId <= 0 || userId <= 0) {
            return false;
        }
        return favourite.saveFavourite(fraId, userId, remove);
    }
}
