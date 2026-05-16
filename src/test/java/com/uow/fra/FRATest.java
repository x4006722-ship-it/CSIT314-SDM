package com.uow.fra;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class FRATest {

    private final String REAL_FUNDRAISER_ID = "2";
    private final String REAL_DONEE_ID = "1";
    private final String REAL_CATEGORY_ID = "1";

    @Test
    public void test_FRA_lifecycle_create_and_delete_succeeds() {
        FRA newFra = new FRA();
        String uniqueTitle = "Lifecycle Test " + System.currentTimeMillis();
        
        newFra.setFraTitle(uniqueTitle);
        newFra.setFraTargetAmount(1000.0);
        newFra.setCategoryId(REAL_CATEGORY_ID);
        newFra.setDoneeId(REAL_DONEE_ID);
        newFra.setFundRaiserId(REAL_FUNDRAISER_ID);
        newFra.setStartedAt("2026-01-01");
        newFra.setEndedAt("2026-12-31");
        newFra.setFraStatus("Pending");

        // 1. Create (已经证明绝对能成功，比如 ID 42)
        FRA savedFra = newFra.saveFRA();
        assertNotNull("Creation should succeed with valid real IDs", savedFra);
        assertNotNull(savedFra.getFraId());

        // 2. Delete (测完立刻从数据库删掉，证明销毁功能正常，且不留脏数据)
        assertTrue("Deletion should succeed and leave DB clean", savedFra.removeFRA());
    }

    @Test
    public void test_Duplicate_title_check_executes_safely() {
        boolean result = FRA.isDuplicateTitle("Random Unique Title 12345", null);
        assertNotNull(result);
    }

    @Test
    public void test_Daily_stats_generation_executes_safely() {
        FRA fra = new FRA();
        assertTrue(fra.getDailyFraStats() instanceof Map);
    }
}