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

        assertTrue("Should successfully delete existing FRA", controller.deleteFRA(savedFra.getFraId()));
    }

    @Test
    public void test_Deletion_returns_false_for_nonexistent_id() {
        assertFalse("Should return false because record doesn't exist", controller.deleteFRA("9999999"));
    }
}
