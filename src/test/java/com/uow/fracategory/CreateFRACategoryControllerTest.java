package com.uow.fracategory;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateFRACategoryControllerTest {

    private CreateFRACategoryController createController;

    @Before
    public void setUp() {
        createController = new CreateFRACategoryController();
    }

    @Test
    public void testCreateCategory_WithNullData_ReturnsFalse() {
        // Test Rule 1: Reject null inputs
        boolean result = createController.createCategory(null);
        assertFalse("Result should be false when the input data is null", result);
    }

    @Test
    public void testCreateCategory_WithInvalidDataType_ReturnsFalse() {
        // Test Rule 1: Reject data that is not a Map
        String invalidData = "Just a plain string, not a Map";
        boolean result = createController.createCategory(invalidData);
        assertFalse("Result should be false when the input data is not a Map", result);
    }
}