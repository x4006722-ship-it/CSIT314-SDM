package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchFavouriteControllerTest {

    private SearchFavouriteController searchController;

    @Before
    public void setUp() {
        searchController = new SearchFavouriteController();
    }

    @Test
    public void testSearchFavourite_WithInvalidDataType_ReturnsEmptyList() {
        // Pass a String instead of a Map
        String invalidData = "Bad Data format";

        Object result = searchController.searchFavourite(invalidData);

        // The Favourite entity should safely return an empty list
        assertTrue("Result should be a List", result instanceof List);
        assertTrue("Result list should be empty for invalid input types", ((List<?>) result).isEmpty());
    }
}
