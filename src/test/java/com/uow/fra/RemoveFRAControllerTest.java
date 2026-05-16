package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RemoveFRAControllerTest {
    
    private RemoveFRAController controller;

    @Before
    public void setUp() {
        controller = new RemoveFRAController();
    }

    // --- 新增：真实的成功删除逻辑 ---
    @Test
    public void test_Deletion_succeeds_with_existing_id() {
        // 先创建一个真实记录用来测试删除
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA setupData = new FRA();
        setupData.setFraTitle("To Be Deleted " + System.nanoTime());
        setupData.setFraTargetAmount(100.0);
        setupData.setCategoryId("1");
        setupData.setDoneeId("1");
        setupData.setFundRaiserId("2");
        setupData.setStartedAt("2026-01-01");
        setupData.setEndedAt("2026-05-01");
        FRA savedFra = setupCreateCtrl.createFRA(setupData);
        assertNotNull(savedFra);

        // 测试删除刚创建的真实记录
        boolean result = controller.deleteFRA(savedFra.getFraId());
        assertTrue("Should successfully delete existing FRA", result);
    }

    @Test
    public void test_Deletion_executes_safely_on_nonexistent_id() {
        boolean result = controller.deleteFRA("9999999");
        assertFalse("Should return false because record doesn't exist", result); // 修改：对于不存在的数据，executeUpdate 应该返回 false
    }

    @Test
    public void test_Deletion_fails_when_fra_id_is_null() {
        boolean result = controller.deleteFRA(null);
        assertFalse(result);
    }

    @Test
    public void test_Deletion_fails_when_fra_id_is_empty() {
        boolean result = controller.deleteFRA("");
        assertFalse(result);
    }

    @Test
    public void test_Deletion_fails_when_fra_id_is_blank() {
        boolean result = controller.deleteFRA("   ");
        assertFalse(result);
    }
}
