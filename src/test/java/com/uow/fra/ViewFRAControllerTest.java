package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ViewFRAControllerTest {
    
    private ViewFRAController controller;

    @Before
    public void setUp() {
        controller = new ViewFRAController();
    }

    @Test
    public void test_View_all_returns_a_list_for_fundraiser() {
        List<FRA> result = controller.viewAllFRAs("user123");
        assertNotNull(result);
    }

    // --- 新增：核心越权防御边界测试 ---
    @Test
    public void test_View_fails_when_fundRaiserId_is_null() {
        List<FRA> result = controller.viewAllFRAs(null);
        assertTrue("Should return empty list if fundRaiserId is null", result.isEmpty());
    }

    @Test
    public void test_View_fails_when_fundRaiserId_is_empty() {
        List<FRA> result = controller.viewAllFRAs("   ");
        assertTrue("Should return empty list if fundRaiserId is empty or blank", result.isEmpty());
    }
}