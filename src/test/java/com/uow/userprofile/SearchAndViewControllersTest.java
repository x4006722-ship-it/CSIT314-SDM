package com.uow.userprofile;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchAndViewControllersTest {

    @Test
    public void test_Search_executes_safely_and_returns_list() {
        SearchUserProfileController controller = new SearchUserProfileController();
        
        List<UserProfile> results = controller.searchProfiles("System", "all");
        assertNotNull("Search should safely return a list", results);
    }

    @Test
    public void test_View_returns_null_for_invalid_id() {
        ViewUserProfileController controller = new ViewUserProfileController();
        
        UserProfile result = controller.getProfileDetails("-9999");
        assertNull("Should safely return null for a non-existent ID", result);
    }
}