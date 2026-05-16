package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateFRAControllerTest {
    
    private UpdateFRAController controller;
    private FRA validFraData;
    private String existingRealId; // 用于存放测试前真实创建成功的 ID

    @Before
    public void setUp() {
        controller = new UpdateFRAController();
        
        // 1. 【前置准备】先在数据库创建一个真实的“靶子”，供后面所有测试方法蹂躏
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA setupData = new FRA();
        // 使用纳秒时间戳，绝对防止标题重复导致创建失败
        setupData.setFraTitle("Initial Title " + System.nanoTime());
        setupData.setFraTargetAmount(1000.0);
        setupData.setCategoryId("1");
        setupData.setDoneeId("1");
        setupData.setFundRaiserId("2");
        setupData.setStartedAt("2026-01-01");
        setupData.setEndedAt("2026-05-01");
        setupData.setFraStatus("Pending");
        
        FRA savedFra = setupCreateCtrl.createFRA(setupData);
        // 如果这里报错，说明数据库连不上或者基础创建逻辑有误
        assertNotNull("Test Setup Failed: Could not create base record", savedFra);
        this.existingRealId = savedFra.getFraId();

        // 2. 【准备更新模板】准备一份各字段都合法的数据，方便后面局部修改
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
        // 使用真实存在的 ID 进行合法更新
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertTrue("Should return true when updating a real existing record with valid data", result);
    }

    @Test
    public void test_Update_fails_when_fra_data_is_null() {
        // 即使 ID 对，数据是 null 也要拦截
        boolean result = controller.updateFRA(existingRealId, null);
        assertFalse("Should fail when fraData is null", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_is_null() {
        boolean result = controller.updateFRA(null, validFraData);
        assertFalse("Should fail when ID is null", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_is_empty() {
        boolean result = controller.updateFRA("", validFraData);
        assertFalse("Should fail when ID is empty string", result);
    }

    @Test
    public void test_Update_fails_when_title_is_empty() {
        validFraData.setFraTitle(""); // 抹掉标题
        // 传入真实 ID，看它是否能正确识别出标题为空的非法逻辑
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when title is empty", result);
    }

    @Test
    public void test_Update_fails_when_target_amount_is_null() {
        validFraData.setFraTargetAmount(null);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when target amount is null", result);
    }

    @Test
    public void test_Update_fails_when_target_amount_is_zero() {
        validFraData.setFraTargetAmount(0.0);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when target amount is zero", result);
    }

    @Test
    public void test_Update_fails_when_target_amount_is_negative() {
        validFraData.setFraTargetAmount(-500.0);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when target amount is negative", result);
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
        // 开始晚于结束
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when start date is after end date", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_does_not_exist() {
        // 故意传一个数据库绝对不可能有的超大 ID
        boolean result = controller.updateFRA("99999999", validFraData);
        assertFalse("Should fail when trying to update a non-existent ID", result);
    }
}