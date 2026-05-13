package com.uow.report;

import org.junit.Before;
import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class MonthlyReportControllerTest {

    private MonthlyReportController monthlyReportController;

    @Before
    public void setUp() {
        monthlyReportController = new MonthlyReportController();
    }

    @Test
    public void testGetMonthlyReport_ReturnsValidMapStructure() {
        Object result = monthlyReportController.getMonthlyReport();

        assertTrue("Result should be a Map", result instanceof Map);
        
        Map<?, ?> reportMap = (Map<?, ?>) result;
        assertEquals("The period should be marked as monthly", "monthly", reportMap.get("period"));
        assertTrue("Report must contain fraStats", reportMap.containsKey("fraStats"));
        assertTrue("Report must contain userStats", reportMap.containsKey("userStats"));
    }
}
