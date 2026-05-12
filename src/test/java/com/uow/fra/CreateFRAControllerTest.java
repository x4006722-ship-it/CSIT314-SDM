package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateFRAControllerTest {

    private CreateFRAController createController;

    @Before
    public void setUp() {
        createController = new CreateFRAController();
    }

    @Test
    public void testCreateFRA_EmptyTitle_ReturnsNull() {
        FRA data = createValidBaseFRA();
        data.setFraTitle(""); // Intentionally set to an empty string

        FRA result = createController.createFRA(data);
        assertNull("Result should be null when the title is empty", result);
    }

    @Test
    public void testCreateFRA_ZeroAmount_ReturnsNull() {
        FRA data = createValidBaseFRA();
        data.setFraTargetAmount(0.0); // Intentionally set to 0

        FRA result = createController.createFRA(data);
        assertNull("Result should be null when the target amount is 0", result);
    }

    @Test
    public void testCreateFRA_NegativeAmount_ReturnsNull() {
        FRA data = createValidBaseFRA();
        data.setFraTargetAmount(-100.0); // Intentionally set to a negative number

        FRA result = createController.createFRA(data);
        assertNull("Result should be null when the target amount is negative", result);
    }

    // Helper method: Quickly create an FRA object that passes basic validation
    private FRA createValidBaseFRA() {
        FRA data = new FRA();
        data.setFraTitle("Valid Title Test");
        data.setFraTargetAmount(1000.0);
        data.setCategoryId("CAT123");
        data.setDoneeId("DONEE123");
        data.setFundRaiserId("FR123");
        data.setStartedAt("2024-01-01");
        data.setEndedAt("2024-12-31");
        return data;
    }
}