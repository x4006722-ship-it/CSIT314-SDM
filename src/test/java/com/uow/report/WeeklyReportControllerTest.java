package com.uow.report;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class WeeklyReportControllerTest {

    private WeeklyReportController weeklyReportController;

    @Before
    public void setUp() {
        weeklyReportController = new WeeklyReportController();
    }

    @Test
    public void testGetWeeklyReport_ReturnsValidMapStructure() {
        Object result = weeklyReportController.getWeeklyReport();

        assertTrue("Result should be a Map", result instanceof Map);
        
        Map<?, ?> reportMap = (Map<?, ?>) result;
        assertEquals("The period should be marked as weekly", "weekly", reportMap.get("period"));
        assertTrue("Report must contain fraStats", reportMap.containsKey("fraStats"));
        assertTrue("Report must contain userStats", reportMap.containsKey("userStats"));
    }
}