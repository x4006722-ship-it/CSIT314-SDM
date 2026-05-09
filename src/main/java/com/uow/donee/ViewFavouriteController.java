package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class ViewFavouriteController {

    private final Favourite favourite = new Favourite();

    public Object viewFavourite(int fraId) {
        return favourite.getViewFavourite(fraId);
    }
}
