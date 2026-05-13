package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchDonationControllerTest {

    private SearchDonationController searchController;

    @Before
    public void setUp() {
        searchController = new SearchDonationController();
    }

    @Test
    public void testSearchDonation_WithInvalidDataType_ReturnsEmptyList() {
        // Pass a String instead of the expected Map
        String invalidData = "This is not a map";

        Object result = searchController.searchDonation(invalidData);

        // The entity's logic should catch this and return an empty List
        assertTrue("Result should be a List", result instanceof List);
        assertTrue("Result list should be empty for invalid input types", ((List<?>) result).isEmpty());
    }

    @Test
    public void testSearchDonation_WithNull_ReturnsEmptyList() {
        Object result = searchController.searchDonation(null);

        assertTrue("Result should be a List", result instanceof List);
        assertTrue("Result list should be empty when input is null", ((List<?>) result).isEmpty());
    }
}