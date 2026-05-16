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
        // 【修改点】加上时间戳，防止因为数据库已存在同名标题而导致查重拦截
        validFraData.setFraTitle("Test Fundraiser " + System.currentTimeMillis());
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
    public void test_Creation_fails_when_category_id_is_empty() {
        validFraData.setCategoryId("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_donee_id_is_empty() {
        validFraData.setDoneeId("");
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_fund_raiser_id_is_empty() {
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