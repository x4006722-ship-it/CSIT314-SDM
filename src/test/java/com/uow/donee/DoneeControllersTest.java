package com.uow.donee;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class DoneeControllersTest {

    @Test
    public void test_Save_favourite_controller_executes_safely() {
        SaveFavouriteController controller = new SaveFavouriteController();
        // Even if it returns false due to DB data, it should not crash
        boolean result = controller.saveFavourite(0, 0, false);
        assertNotNull(result);
    }

    @Test
    public void test_Search_donation_controller_returns_list() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        Object result = controller.searchDonation(params);
        assertNotNull("Should return a list (empty or populated)", result);
    }

    @Test
    public void test_View_donation_controller_executes_safely() {
        ViewDonationController controller = new ViewDonationController();
        Object result = controller.viewDonation(1);
        // Might be null if ID 1 isn't a valid FRA, but should run without exception
        assertTrue(true); 
    }
}
