package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchFRAControllerTest {
    
    private SearchFRAController controller;

    @Before
    public void setUp() {
        controller = new SearchFRAController();
    }

    @Test
    public void test_Search_returns_a_list_of_results() {
        // 【修改】补充了第7个参数 endDate 为 ""
        List<FRA> result = controller.searchFRA("Education", "all", "all", "fundRaiser", "1", "", "");
        assertNotNull(result);
    }

    // --- 核心越权防御边界测试 ---
    @Test
    public void test_Search_fails_when_role_is_null() {
        List<FRA> result = controller.searchFRA("Education", "all", "all", null, "1", "", "");
        assertTrue("Should return empty list to prevent unauthorized access", result.isEmpty());
    }

    @Test
    public void test_Search_fails_when_userId_is_null() {
        List<FRA> result = controller.searchFRA("Education", "all", "all", "fundRaiser", null, "", "");
        assertTrue("Should return empty list to prevent unauthorized access", result.isEmpty());
    }

    @Test
    public void test_Search_fails_when_userId_is_empty() {
        List<FRA> result = controller.searchFRA("Education", "all", "all", "fundRaiser", "   ", "", "");
        assertTrue("Should return empty list if userId is blank", result.isEmpty());
    }

    // --- 新增：日期范围逻辑异常拦截测试 ---
    @Test
    public void test_Search_fails_when_startDate_is_after_endDate() {
        // 如果 startDate(2026-05-10) 晚于 endDate(2026-04-10)
        List<FRA> result = controller.searchFRA("Education", "all", "all", "fundRaiser", "1", "2026-05-10", "2026-04-10");
        assertTrue("Should return empty list if start date is strictly after end date", result.isEmpty());
    }
}
