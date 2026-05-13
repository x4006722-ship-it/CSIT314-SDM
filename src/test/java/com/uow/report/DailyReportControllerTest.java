package com.uow.report;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class DailyReportControllerTest {

    private DailyReportController dailyReportController;

    @Before
    public void setUp() {
        dailyReportController = new DailyReportController();
    }

    @Test
    public void testGetDailyReport_ReturnsValidMapStructure() {
        // Execution: This will trigger the real database query
        Object result = dailyReportController.getDailyReport();

        // Verification
        assertTrue("Result should be a Map", result instanceof Map);
        
        Map<?, ?> reportMap = (Map<?, ?>) result;
        assertEquals("The period should be marked as daily", "daily", reportMap.get("period"));
        assertTrue("Report must contain fraStats", reportMap.containsKey("fraStats"));
        assertTrue("Report must contain userStats", reportMap.containsKey("userStats"));
    }
}