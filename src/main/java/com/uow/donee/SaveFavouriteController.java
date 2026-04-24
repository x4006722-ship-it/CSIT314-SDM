package com.uow.donee;

import org.springframework.stereotype.Component;

@Component
public class SaveFavouriteController {

    private final Donee donee = new Donee();

    public Object saveFavourite(int userId, int fraId, boolean remove) {
        if (userId <= 0) {
            return java.util.Map.of("success", false, "error", "Not logged in.");
        }
        if (donee.keepSaveFavourite(fraId, userId, remove)) {
            if (remove) {
                return java.util.Map.of("success", true, "message", "Removed from your favourites.");
            }
            return java.util.Map.of("success", true, "message", "Saved to your favourites.");
        }
        String em = donee.lastErrorMessage;
        return java.util.Map.of(
                "success", false,
                "error", (em == null || em.isBlank()) ? "Could not update favourites." : em
        );
    }
}
