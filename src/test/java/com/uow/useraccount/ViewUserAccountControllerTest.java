package com.uow.useraccount;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViewUserAccountControllerTest {

    private ViewUserAccountController viewController;

    @Before
    public void setUp() {
        viewController = new ViewUserAccountController();
    }

    @Test
    public void testViewAccount_NonExistentId_ReturnsNull() {
        Object result = viewController.viewAccount(-1);
        assertNull("Result should be null for a non-existent user ID", result);
    }
}