package com.uow.fracategory;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ViewFRACategoryControllerTest {

    private ViewFRACategoryController viewController;

    @Before
    public void setUp() {
        viewController = new ViewFRACategoryController();
    }

    @Test
    public void testViewCategory_NonExistentId_ReturnsNull() {
        // Provide an ID that does not exist
        Object result = viewController.viewCategory(-500);
        
        assertNull("Result should be null when viewing a non-existent category ID", result);
    }
}