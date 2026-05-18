package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
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
        setupData.setStartedAt("2026-01-01");
        setupData.setEndedAt("2026-05-01");
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
        validFraData.setStartedAt("2026-06-01");
        validFraData.setEndedAt("2026-12-31");
        validFraData.setFraStatus("Pending");
    }

    @Test
    public void test_Update_succeeds_with_valid_data() {
        assertTrue(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_succeeds_when_title_is_unchanged() {
        validFraData.setFraTitle(initialTitle);
        assertTrue("Should succeed because duplicate check excludes self",
                controller.updateFRA(existingRealId, validFraData));
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
        anotherData.setStartedAt("2026-01-01");
        anotherData.setEndedAt("2026-05-01");
        setupCreateCtrl.createFRA(anotherData);

        validFraData.setFraTitle(anotherTitle);
        assertFalse("Should fail when updating to a title owned by another record",
                controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_start_date_equals_end_date() {
        validFraData.setStartedAt("2026-10-10");
        validFraData.setEndedAt("2026-10-10");
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_date_logic_is_invalid() {
        validFraData.setStartedAt("2026-12-31");
        validFraData.setEndedAt("2026-01-01");
        assertFalse(controller.updateFRA(existingRealId, validFraData));
    }

    @Test
    public void test_Update_fails_when_fra_id_does_not_exist() {
        assertFalse(controller.updateFRA("99999999", validFraData));
    }
}
