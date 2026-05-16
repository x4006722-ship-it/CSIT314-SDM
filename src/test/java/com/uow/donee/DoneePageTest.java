package com.uow.donee;

import com.uow.fra.SearchFRAController;
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
        
        // 【核心修复】：注入全部依赖，防止引发 NPE
        injectField("viewDonationController", new ViewDonationController());
        injectField("searchDonationController", new SearchDonationController());
        injectField("saveFavouriteController", new SaveFavouriteController());
        injectField("searchFRAController", new SearchFRAController()); 
        injectField("searchFavouriteController", new SearchFavouriteController());
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = DoneePage.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(doneePage, value);
    }

    @Test
    public void test_On_search_FRA_executes_safely_with_all_params() {
        // 正常用例：测试刚添加了 startDate 的搜索接口
        Object result = doneePage.onSearchFRA("test", "1", "Pending", "2023-01-01", null);
        assertTrue("Should return a list", result instanceof java.util.List);
    }

    @Test
    public void test_On_view_FRA_executes_without_crashing() {
        Object result = doneePage.onViewFRA(0);
        assertNull(result); 
    }

    @Test
    public void test_On_search_donation_with_empty_map_returns_access_denied() {
        // 边界修复：由于 Controller 规则 1 的存在，空 map 会触发无 userId 的报错，而不是空 List
        Object result = doneePage.onSearchDonation(new HashMap<>(), null);
        assertTrue("Should return error map", result instanceof java.util.Map);
        assertEquals("Access Denied: User identity is missing.", ((java.util.Map<?,?>) result).get("error"));
    }

    @Test
    public void test_On_save_favourite_with_invalid_user_returns_false() {
        boolean result = doneePage.onSaveFavourite(0, 0, false, null);
        assertFalse(result);
    }
}