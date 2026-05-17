package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    private final Favourite favourite = new Favourite();

    public boolean saveFavourite(int fraId, int userId, boolean remove) {
        // Boundary validation
        if (fraId <= 0 || userId <= 0) {
            return false;
        }
        return favourite.saveFavourite(fraId, userId, remove);
    }
}
