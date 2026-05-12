package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchUserAccountControllerTest {

    private SearchUserAccountController searchController;

    @Before
    public void setUp() {
        searchController = new SearchUserAccountController();
    }

    @Test
    public void testSearchAccount_WithNull_ReturnsEmptyList() {
        Object result = searchController.searchAccount(null);
        
        // Entity returns an empty list on Exception/Null
        assertTrue("Result should be a List", result instanceof List);
        assertTrue("Result list should be empty when input is null", ((List<?>) result).isEmpty());
    }
}