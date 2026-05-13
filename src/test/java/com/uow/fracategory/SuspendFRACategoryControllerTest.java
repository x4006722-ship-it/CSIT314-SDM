package com.uow.fracategory;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SuspendFRACategoryControllerTest {

    private SuspendFRACategoryController suspendController;

    @Before
    public void setUp() {
        suspendController = new SuspendFRACategoryController();
    }

    @Test
    public void testSuspendCategory_NonExistentId_ReturnsFalse() {
        // Attempt to suspend a fake ID
        int fakeId = -999;
        
        boolean result = suspendController.suspendCategory(fakeId);
        assertFalse("Result should be false when attempting to suspend a non-existent category", result);
    }
}