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

    @Test
    public void test_Deletion_succeeds_with_existing_id() {
        // Create a temporary record specifically for this test to ensure it exists
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA setupData = new FRA();
        setupData.setFraTitle("Temporary Delete Me " + System.nanoTime());
        setupData.setFraTargetAmount(100.0);
        setupData.setCategoryId("1");
        setupData.setDoneeId("1");
        setupData.setFundRaiserId("2");
        setupData.setStartedAt(java.time.LocalDate.now().plusDays(1).toString());
        setupData.setEndedAt(java.time.LocalDate.now().plusMonths(1).toString());
        
        FRA savedFra = setupCreateCtrl.createFRA(setupData);
        
        // If save failed, the problem is in Create, not Delete.
        assertNotNull("Pre-condition failed: Could not create test FRA", savedFra); 

        // Perform the actual test
        boolean result = controller.deleteFRA(savedFra.getFraId());
        assertTrue("Should successfully delete the freshly created FRA", result);
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
