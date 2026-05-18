package com.uow.donee;

import org.springframework.stereotype.Component;

/**
 * Retrieves details of a specific favorite FRA.
 * 
 * Responsibilities:
 * - Query favorite FRA by FRA ID
 * - Return complete FRA information
 */
@Component
public class ViewFavouriteController {

    private final Favourite favourite = new Favourite();

    /**
     * Retrieves a favorite FRA's complete information.
     * 
     * @param fraId The FRA ID to retrieve details for
     * @return FRA details if found and is favorite; null or error otherwise
     */
    public Object viewFavourite(int fraId) {
        return favourite.getViewFavourite(fraId);
    }
}
