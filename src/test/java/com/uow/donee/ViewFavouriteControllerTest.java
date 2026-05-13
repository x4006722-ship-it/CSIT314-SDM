package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViewFavouriteControllerTest {

    private ViewFavouriteController viewController;

    @Before
    public void setUp() {
        viewController = new ViewFavouriteController();
    }

    @Test
    public void testViewFavourite_NonExistentId_ReturnsNull() {
        // Pass an ID that does not exist
        int fakeId = -999;

        Object result = viewController.viewFavourite(fakeId);

        assertNull("Result should be null when the Favourite FRA ID does not exist", result);
    }
}