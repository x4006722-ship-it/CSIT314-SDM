package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateFRAControllerTest {
    
    private CreateFRAController controller;
    private FRA validFraData;

    @Before
    public void setUp() {
        controller = new CreateFRAController();
        validFraData = new FRA();
        validFraData.setFraTitle("Test Fundraiser " + System.nanoTime()); // 使用nanoTime减少冲突概率
        validFraData.setFraTargetAmount(5000.0);
        validFraData.setCategoryId("1"); 
        validFraData.setDoneeId("1");
        validFraData.setFundRaiserId("2");
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-12-31");
    }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        FRA result = controller.createFRA(validFraData);
        assertNotNull("Should return FRA object on success", result);
        assertNotNull("FRA ID should be set", result.getFraId());
    }

    // --- 新增：金额有效性的极值下边界 ---
    @Test
    public void test_Creation_succeeds_with_minimum_valid_amount() {
        validFraData.setFraTargetAmount(0.01); 
        FRA result = controller.createFRA(validFraData);
        assertNotNull("Should succeed with boundary amount strictly > 0", result);
    }

    // --- 新增：重名拦截逻辑 ---
    @Test
    public void test_Creation_fails_when_title_is_duplicate() {
        String sharedTitle = "Duplicate Title " + System.nanoTime();
        validFraData.setFraTitle(sharedTitle);
        // 第一次创建成功
        assertNotNull(controller.createFRA(validFraData));
        
        // 试图用相同的Title再次创建
        FRA duplicateFra = new FRA();
        duplicateFra.setFraTitle(sharedTitle);
        duplicateFra.setFraTargetAmount(1000.0);
        duplicateFra.setCategoryId("1"); 
        duplicateFra.setDoneeId("1");
        duplicateFra.setFundRaiserId("2");
        duplicateFra.setStartedAt("2026-06-01");
        duplicateFra.setEndedAt("2026-12-31");
        
        assertNull("Should fail due to duplicate title", controller.createFRA(duplicateFra));
    }
    
    @Test
    public void test_Creation_fails_when_fra_data_is_null() {
        FRA result = controller.createFRA(null);
        assertNull(result);
    }

    @Test
    public void test_Creation_fails_when_title_is_null() {
        validFraData.setFraTitle(null); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_title_is_empty() {
        validFraData.setFraTitle(""); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_category_id_is_null_or_empty() {
        validFraData.setCategoryId(null);
        assertNull(controller.createFRA(validFraData));
        validFraData.setCategoryId("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_donee_id_is_null_or_empty() {
        validFraData.setDoneeId(null);
        assertNull(controller.createFRA(validFraData));
        validFraData.setDoneeId("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_fund_raiser_id_is_null_or_empty() {
        validFraData.setFundRaiserId(null);
        assertNull(controller.createFRA(validFraData));
        validFraData.setFundRaiserId("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_target_amount_is_null() {
        validFraData.setFraTargetAmount(null); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_target_amount_is_zero() {
        validFraData.setFraTargetAmount(0.0); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_target_amount_is_negative() {
        validFraData.setFraTargetAmount(-100.0); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_started_at_is_empty() {
        validFraData.setStartedAt("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_ended_at_is_empty() {
        validFraData.setEndedAt("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_start_date_equals_end_date() {
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-06-01"); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_start_date_is_after_end_date() {
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01"); 
        assertNull(controller.createFRA(validFraData));
    }
}