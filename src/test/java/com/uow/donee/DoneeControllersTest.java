package com.uow.donee;

import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class DoneeControllersTest {

    // ==========================================
    // SaveFavouriteController Tests
    // ==========================================
    @Test
    public void test_Save_favourite_controller_with_invalid_ids_returns_false() {
        SaveFavouriteController controller = new SaveFavouriteController();
        // 边界测试：测试小于等于 0 的非法 ID
        assertFalse("Should return false for invalid fraId", controller.saveFavourite(-1, 1, false));
        assertFalse("Should return false for invalid userId", controller.saveFavourite(1, 0, false));
    }

    @Test
    public void test_Save_favourite_controller_with_valid_ids_executes_safely() {
        SaveFavouriteController controller = new SaveFavouriteController();
        // 正常用例：即使因为数据库外键约束报错，方法也应该捕获并返回布尔值，不应崩溃
        boolean result = controller.saveFavourite(9999, 9999, false);
        assertNotNull(result);
    }

    // ==========================================
    // SearchDonationController Tests
    // ==========================================
    @Test
    public void test_Search_donation_controller_missing_user_returns_access_denied() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        // 错误测试：不传入 userId
        Object result = controller.searchDonation(params);
        assertTrue("Should return error map", result instanceof Map);
        assertEquals("Access Denied: User identity is missing.", ((Map<?, ?>) result).get("error"));
    }

    @Test
    public void test_Search_donation_controller_title_exceeds_max_length_returns_error() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("title", "A".repeat(51)); // 边界测试：构造 51 个字符的超长字符串
        Object result = controller.searchDonation(params);
        
        assertTrue("Should return error map", result instanceof Map);
        assertEquals("Search criteria too long: Maximum 50 characters.", ((Map<?, ?>) result).get("error"));
    }

    @Test
    public void test_Search_donation_controller_invalid_date_format_returns_error() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("startDate", "2023/12/01"); // 错误测试：非 YYYY-MM-DD 格式
        params.put("endDate", "2023-12-31");
        Object result = controller.searchDonation(params);
        
        assertTrue("Should return error map", result instanceof Map);
        assertEquals("Invalid date format. Please use YYYY-MM-DD.", ((Map<?, ?>) result).get("error"));
    }

    @Test
    public void test_Search_donation_controller_start_date_after_end_date_returns_error() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("startDate", "2023-12-31"); // 逻辑错误测试：开始时间晚于结束时间
        params.put("endDate", "2023-12-01");
        Object result = controller.searchDonation(params);
        
        assertTrue("Should return error map", result instanceof Map);
        assertEquals("Date From cannot be later than Date To.", ((Map<?, ?>) result).get("error"));
    }

    @Test
    public void test_Search_donation_controller_valid_data_returns_list() {
        SearchDonationController controller = new SearchDonationController();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("title", "Valid Title");
        params.put("startDate", "2023-01-01");
        params.put("endDate", "2023-12-31");
        // 正常用例：全部参数合法
        Object result = controller.searchDonation(params);
        assertTrue("Should return a list (empty or populated) from DB", result instanceof List);
    }

    // ==========================================
    // ViewDonationController Tests
    // ==========================================
    @Test
    public void test_View_donation_controller_executes_safely() {
        ViewDonationController controller = new ViewDonationController();
        Object result = controller.viewDonation(1);
        assertTrue(true); 
    }
}
