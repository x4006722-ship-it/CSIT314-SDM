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
    public void test_Deletion_executes_safely() {
        // Note: Using a non-existent ID to test graceful handling
        boolean result = controller.deleteFRA("999");
        // Just verify it doesn't crash
        assertNotNull(result);
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
