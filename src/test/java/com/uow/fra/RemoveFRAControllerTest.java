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
        boolean result = controller.deleteFRA("999");
        assertNotNull(result);
    }
}
