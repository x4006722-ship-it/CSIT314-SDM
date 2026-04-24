package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    private final Donee donee = new Donee();

    public boolean saveToFavourite(int fraId, int userId) {
        return donee.saveToFavourite(fraId, userId);
    }

    public boolean removeFromFavourite(int fraId, int userId) {
        return donee.removeFromFavourite(fraId, userId);
    }

    public String getErrorMessage() {
        return donee.lastErrorMessage;
    }
}
