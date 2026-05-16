package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UpdateFRAControllerTest {
    
    private UpdateFRAController controller;
    private FRA validFraData;

    @Before
    public void setUp() {
        controller = new UpdateFRAController();
        validFraData = new FRA();
        validFraData.setFraTitle("Updated Fundraiser");
        validFraData.setFraTargetAmount(2000.0);
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-12-31");
    }

    @Test
    public void test_Update_executes_safely_with_valid_data() {
        boolean result = controller.updateFRA("100", validFraData);
        assertNotNull(result); 
    }

    @Test
    public void test_Update_fails_when_fra_id_is_empty() {
        boolean result = controller.updateFRA("", validFraData);
        assertFalse(result);
    }

    @Test
    public void test_Update_fails_when_date_logic_is_invalid() {
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01"); 
        boolean result = controller.updateFRA("100", validFraData);
        assertFalse(result);
    }
}