package com.uow.userprofile;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchUserProfileControllerTest {

    private SearchUserProfileController searchController;

    @Before
    public void setUp() {
        searchController = new SearchUserProfileController();
    }

    @Test
    public void testSearchProfiles_WithNullParameters_ReturnsList() {
        List<UserProfile> result = searchController.searchProfiles(null, null);
        
        // It should handle nulls gracefully and return a List (even if the list is empty)
        assertNotNull("Result should not be null; it must return a List", result);
    }
}