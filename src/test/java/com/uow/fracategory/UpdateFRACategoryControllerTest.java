package com.uow.fracategory;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class UpdateFRACategoryControllerTest {

    private UpdateFRACategoryController updateController;

    @Before
    public void setUp() {
        updateController = new UpdateFRACategoryController();
    }

    @Test
    public void testUpdateCategory_WithNullData_ReturnsFalse() {
        boolean result = updateController.updateCategory(null);
        assertFalse("Result should be false when the input data is null", result);
    }

    @Test
    public void testUpdateCategory_WithMissingId_ReturnsFalse() {
        // Provide a Map, but intentionally leave out the "categoryId"
        Map<String, Object> incompleteData = new HashMap<>();
        incompleteData.put("categoryName", "Education");
        
        boolean result = updateController.updateCategory(incompleteData);
        assertFalse("Result should be false when categoryId is missing or 0", result);
    }

    @Test
    public void testUpdateCategory_WithNegativeId_ReturnsFalse() {
        Map<String, Object> badIdData = new HashMap<>();
        badIdData.put("categoryId", -5); // Invalid ID
        
        boolean result = updateController.updateCategory(badIdData);
        assertFalse("Result should be false when categoryId is less than or equal to 0", result);
    }

    @Test
    public void testUpdateCategory_WithNonExistentId_ReturnsFalse() {
        Map<String, Object> fakeIdData = new HashMap<>();
        fakeIdData.put("categoryId", 999999); // An ID that definitely does not exist
        fakeIdData.put("categoryName", "Healthcare");

        // The controller will call fraCategory.getViewCategory(), which will return null,
        // causing the controller to safely abort and return false.
        boolean result = updateController.updateCategory(fakeIdData);
        assertFalse("Result should be false when the target category does not exist in the database", result);
    }
}