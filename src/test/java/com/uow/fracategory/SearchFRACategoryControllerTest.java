package com.uow.fracategory;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchFRACategoryControllerTest {

    private SearchFRACategoryController searchController;

    @Before
    public void setUp() {
        searchController = new SearchFRACategoryController();
    }

    @Test
    public void testSearchCategory_WithInvalidDataType_ReturnsEmptyList() {
        // Pass a String instead of a Map. The entity logic safely catches this.
        Object result = searchController.searchCategory("Invalid Type");
        
        assertTrue("Result should still be a List even with bad input", result instanceof List);
        assertTrue("Result list should be empty when given invalid input", ((List<?>) result).isEmpty());
    }
}
