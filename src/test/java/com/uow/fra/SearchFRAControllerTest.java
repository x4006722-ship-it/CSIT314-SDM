package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchFRAControllerTest {

    private SearchFRAController searchController;

    @Before
    public void setUp() {
        searchController = new SearchFRAController();
    }

    @Test
    public void testSearchFRA_WithNullCriteria_ReturnsList() {
        // Execution: Pass null to see if the controller handles it gracefully
        List<FRA> result = searchController.searchFRA(null);

        // Verification: It should not throw a NullPointerException, but return a List
        assertNotNull("Result should not be null; it should return a List (possibly empty)", result);
    }

    @Test
    public void testSearchFRA_WithEmptyCriteria_ReturnsList() {
        // Execution: Pass an empty string
        List<FRA> result = searchController.searchFRA("");

        // Verification: It should return a List (likely containing all items based on your SQL query)
        assertNotNull("Result should not be null when criteria is an empty string", result);
    }
}
