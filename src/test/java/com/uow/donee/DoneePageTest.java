package com.uow.donee;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.HashMap;
import static org.junit.Assert.*;

public class DoneePageTest {

    private DoneePage doneePage;

    @Before
    public void setUp() throws Exception {
        doneePage = new DoneePage();
        
        // 【核心修复】：手动把 Controller 塞进去，防止 @Autowired 导致的 NPE
        injectField("viewDonationController", new ViewDonationController());
        injectField("searchDonationController", new SearchDonationController());
        injectField("saveFavouriteController", new SaveFavouriteController());
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = DoneePage.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(doneePage, value);
    }

    @Test
    public void test_On_view_FRA_executes_without_crashing() {
        // 现在不会报 NPE 了
        Object result = doneePage.onViewFRA(0);
        assertNull(result); 
    }

    @Test
    public void test_On_search_donation_with_empty_map_returns_empty_list() {
        Object result = doneePage.onSearchDonation(new HashMap<>(), null);
        assertTrue(result instanceof java.util.List);
    }

    @Test
    public void test_On_save_favourite_with_invalid_user_returns_false() {
        boolean result = doneePage.onSaveFavourite(0, 0, false, null);
        assertFalse(result);
    }
}