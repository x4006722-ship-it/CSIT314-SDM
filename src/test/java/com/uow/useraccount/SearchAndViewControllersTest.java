package com.uow.useraccount;

import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class SearchAndViewControllersTest {

    @Test
    public void test_Search_executes_safely_and_returns_list() {
        SearchUserAccountController controller = new SearchUserAccountController();
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put("username", "admin");

        Object result = controller.searchAccount(searchParams);
        assertNotNull("Should safely return a list (empty or populated)", result);
    }

    @Test
    public void test_View_returns_null_for_invalid_id() {
        ViewUserAccountController controller = new ViewUserAccountController();
        
        Object result = controller.viewAccount(-9999);
        assertNull("Should safely return null for a non-existent ID", result);
    }
}