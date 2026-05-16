package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateFRAControllerTest {
    
    private UpdateFRAController controller;
    private FRA validFraData;
    private String existingRealId; 
    private String initialTitle; // 存一下原来的名字

    @Before
    public void setUp() {
        controller = new UpdateFRAController();
        
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA setupData = new FRA();
        initialTitle = "Initial Title " + System.nanoTime();
        setupData.setFraTitle(initialTitle);
        setupData.setFraTargetAmount(1000.0);
        setupData.setCategoryId("1");
        setupData.setDoneeId("1");
        setupData.setFundRaiserId("2");
        setupData.setStartedAt("2026-01-01");
        setupData.setEndedAt("2026-05-01");
        setupData.setFraStatus("Pending");
        
        FRA savedFra = setupCreateCtrl.createFRA(setupData);
        assertNotNull("Test Setup Failed: Could not create base record", savedFra);
        this.existingRealId = savedFra.getFraId();

        validFraData = new FRA();
        validFraData.setFraTitle("Valid Updated Title " + System.nanoTime());
        validFraData.setFraTargetAmount(2500.0);
        validFraData.setCategoryId("1");
        validFraData.setDoneeId("1");
        validFraData.setFundRaiserId("2");
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-12-31");
        validFraData.setFraStatus("Pending");
    }

    @Test
    public void test_Update_succeeds_with_valid_data() {
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertTrue("Should return true when updating a real existing record with valid data", result);
    }

    // --- 新增：排除自身查重测试 ---
    @Test
    public void test_Update_succeeds_when_title_is_unchanged() {
        validFraData.setFraTitle(initialTitle); // 保持原Title不变
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertTrue("Should succeed because duplicate check excludes self", result);
    }

    // --- 新增：和别人重名查重测试 ---
    @Test
    public void test_Update_fails_when_title_is_duplicate_of_another_record() {
        // 先创建第二条无关的数据
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA anotherData = new FRA();
        String anotherTitle = "Another Record Title " + System.nanoTime();
        anotherData.setFraTitle(anotherTitle);
        anotherData.setFraTargetAmount(1000.0);
        anotherData.setCategoryId("1");
        anotherData.setDoneeId("1");
        anotherData.setFundRaiserId("2");
        anotherData.setStartedAt("2026-01-01");
        anotherData.setEndedAt("2026-05-01");
        setupCreateCtrl.createFRA(anotherData);

        // 试图把第一条数据的Title改得和第二条一样
        validFraData.setFraTitle(anotherTitle);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when updating to a title owned by another record", result);
    }

    @Test
    public void test_Update_fails_when_fra_data_is_null() {
        boolean result = controller.updateFRA(existingRealId, null);
        assertFalse("Should fail when fraData is null", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_is_null_or_empty() {
        assertFalse(controller.updateFRA(null, validFraData));
        assertFalse(controller.updateFRA("", validFraData));
    }

    @Test
    public void test_Update_fails_when_title_is_null_or_empty() {
        validFraData.setFraTitle(null);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTitle(""); 
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_target_amount_is_null_zero_or_negative() {
        validFraData.setFraTargetAmount(null);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTargetAmount(0.0);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTargetAmount(-500.0);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_started_at_is_empty() {
        validFraData.setStartedAt("");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when startedAt is empty", result);
    }

    @Test
    public void test_Update_fails_when_ended_at_is_empty() {
        validFraData.setEndedAt("");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when endedAt is empty", result);
    }

    @Test
    public void test_Update_fails_when_start_date_equals_end_date() {
        validFraData.setStartedAt("2026-10-10");
        validFraData.setEndedAt("2026-10-10");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when start date equals end date", result);
    }

    @Test
    public void test_Update_fails_when_date_logic_is_invalid() {
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when start date is after end date", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_does_not_exist() {
        boolean result = controller.updateFRA("99999999", validFraData);
        assertFalse("Should fail when trying to update a non-existent ID", result);
    }
}