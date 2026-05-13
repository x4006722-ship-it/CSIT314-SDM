package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViewDonationControllerTest {

    private ViewDonationController viewController;

    @Before
    public void setUp() {
        viewController = new ViewDonationController();
    }

    @Test
    public void testViewDonation_NonExistentId_ReturnsNull() {
        // Pass an ID that does not exist
        int fakeId = -999;

        Object result = viewController.viewDonation(fakeId);

        // The SQL query will return empty, so the entity should return null
        assertNull("Result should be null when the FRA ID does not exist", result);
    }
}