package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SaveFavouriteControllerTest {

    private SaveFavouriteController saveFavouriteController;

    @Before
    public void setUp() {
        saveFavouriteController = new SaveFavouriteController();
    }

    @Test
    public void testSaveFavourite_InvalidIds_ReturnsFalse() {
        // Test with IDs that definitely do not exist (0 or negative)
        int invalidFraId = -1;
        int invalidUserId = 0;
        boolean removeAction = false;

        boolean result = saveFavouriteController.saveFavourite(invalidFraId, invalidUserId, removeAction);

        assertFalse("Result should be false when trying to save with invalid IDs", result);
    }
}