package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class UpdateFRAControllerTest {
    
    private UpdateFRAController controller;
    private FRA validFraData;
    private String existingRealId; 
    private String initialTitle;

    @Before
    public void setUp() {
        controller = new UpdateFRAController();
        
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA setupData = new FRA();
        initialTitle = "Initial Title " + System.nanoTime();
        setupData.setFraTitle(initialTitle);
        setupData.setFraTargetAmount(1000.0);
        setupData.setCategoryId("1");
        setupData.setDoneeId("1");
        setupData.setFundRaiserId("2");
        setupData.setStartedAt(LocalDate.now().plusDays(1).toString());
        setupData.setEndedAt(LocalDate.now().plusMonths(5).toString());
        setupData.setFraStatus("Pending");
        
        FRA savedFra = setupCreateCtrl.createFRA(setupData);
        assertNotNull("Test Setup Failed: Could not create base record", savedFra);
        this.existingRealId = savedFra.getFraId();

        validFraData = new FRA();
        validFraData.setFraTitle("Valid Updated Title " + System.nanoTime());
        validFraData.setFraTargetAmount(2500.0);
        validFraData.setCategoryId("1");
        validFraData.setDoneeId("1");
        validFraData.setFundRaiserId("2");
        validFraData.setStartedAt(LocalDate.now().plusDays(2).toString());
        validFraData.setEndedAt(LocalDate.now().plusMonths(6).toString());
        validFraData.setFraStatus("Pending");
    }

    @Test
    public void test_Update_succeeds_with_valid_data() {
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertTrue("Should return true when updating a real existing record with valid data", result);
    }

    @Test
    public void test_Update_fails_when_target_amount_exceeds_maximum() {
        validFraData.setFraTargetAmount(1000000000.01);
        assertFalse("Should fail when update target amount exceeds 1 Billion", controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_succeeds_when_title_is_unchanged() {
        validFraData.setFraTitle(initialTitle); 
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertTrue("Should succeed because duplicate check excludes self", result);
    }

    @Test
    public void test_Update_fails_when_title_is_duplicate_of_another_record() {
        CreateFRAController setupCreateCtrl = new CreateFRAController();
        FRA anotherData = new FRA();
        String anotherTitle = "Another Record Title " + System.nanoTime();
        anotherData.setFraTitle(anotherTitle);
        anotherData.setFraTargetAmount(1000.0);
        anotherData.setCategoryId("1");
        anotherData.setDoneeId("1");
        anotherData.setFundRaiserId("2");
        anotherData.setStartedAt(LocalDate.now().plusDays(1).toString());
        anotherData.setEndedAt(LocalDate.now().plusMonths(5).toString());
        setupCreateCtrl.createFRA(anotherData);

        validFraData.setFraTitle(anotherTitle);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when updating to a title owned by another record", result);
    }

    @Test
    public void test_Update_fails_when_fra_data_is_null() {
        boolean result = controller.updateFRA(existingRealId, null);
        assertFalse("Should fail when fraData is null", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_is_null_or_empty() {
        assertFalse(controller.updateFRA(null, validFraData));
        assertFalse(controller.updateFRA("", validFraData));
    }

    @Test
    public void test_Update_fails_when_title_is_null_or_empty() {
        validFraData.setFraTitle(null);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTitle(""); 
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_target_amount_is_null_zero_or_negative() {
        validFraData.setFraTargetAmount(null);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTargetAmount(0.0);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
        validFraData.setFraTargetAmount(-500.0);
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_started_at_is_empty() {
        validFraData.setStartedAt("");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when startedAt is empty", result);
    }

    @Test
    public void test_Update_fails_when_ended_at_is_empty() {
        validFraData.setEndedAt("");
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when endedAt is empty", result);
    }

    @Test
    public void test_Update_fails_when_start_date_equals_end_date() {
        String sameDate = LocalDate.now().plusDays(10).toString();
        validFraData.setStartedAt(sameDate);
        validFraData.setEndedAt(sameDate);
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when start date equals end date", result);
    }

    @Test
    public void test_Update_fails_when_date_logic_is_invalid() {
        validFraData.setStartedAt(LocalDate.now().plusMonths(2).toString());
        validFraData.setEndedAt(LocalDate.now().plusMonths(1).toString());
        boolean result = controller.updateFRA(existingRealId, validFraData);
        assertFalse("Should fail when start date is after end date", result);
    }

    @Test
    public void test_Update_fails_when_fra_id_does_not_exist() {
        boolean result = controller.updateFRA("99999999", validFraData);
        assertFalse("Should fail when trying to update a non-existent ID", result);
    }
}