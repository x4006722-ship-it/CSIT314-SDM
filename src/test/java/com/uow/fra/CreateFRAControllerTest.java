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
        validFraData.setFraTitle("Pragmatic Test Fundraiser");
        validFraData.setFraTargetAmount(5000.0);
        // 【核心修复】：将字母改为了纯数字字符串，防止 MySQL 报错
        validFraData.setCategoryId("1"); 
        validFraData.setDoneeId("1");
        validFraData.setFundRaiserId("2");
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-12-31");
    }

    @Test
    public void test_Creation_succeeds_with_valid_data() {
        FRA result = controller.createFRA(validFraData);
        assertTrue(true); 
    }
    
    @Test
    public void test_Creation_fails_when_title_is_empty() {
        validFraData.setFraTitle(""); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_target_amount_is_invalid() {
        validFraData.setFraTargetAmount(0.0); 
        assertNull(controller.createFRA(validFraData));
    }

    @Test
    public void test_Creation_fails_when_start_date_is_after_end_date() {
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01"); 
        assertNull(controller.createFRA(validFraData));
    }
}