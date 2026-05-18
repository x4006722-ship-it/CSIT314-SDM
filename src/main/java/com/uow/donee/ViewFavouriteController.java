package com.uow.donee;
import org.springframework.stereotype.Component;

@Component
public class ViewFavouriteController {

    private final Favourite favourite = new Favourite();

    public Object viewFavourite(int fraId) {
        // Business Rule: Record ID must be a positive non-zero integer.
        if (fraId <= 0) {
            throw new IllegalArgumentException("Invalid Operation: Resource identifier must be positive.");
        }

        // Logic Pass: Identity and ID are valid, perform the fetch.
        return favourite.getViewFavourite(fraId);
    }
}