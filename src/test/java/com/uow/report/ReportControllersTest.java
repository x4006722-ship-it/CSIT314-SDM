package com.uow.report;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class ReportControllersTest {

    @Test
    public void test_Daily_report_controller_returns_correct_period_metadata() {
        DailyReportController controller = new DailyReportController();
        Object result = controller.getDailyReport();
        
        assertTrue(result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("daily", report.get("period"));
        assertNotNull("Should contain fraStats", report.get("fraStats"));
        assertNotNull("Should contain userStats", report.get("userStats"));
    }

    @Test
    public void test_Weekly_report_controller_returns_correct_period_metadata() {
        WeeklyReportController controller = new WeeklyReportController();
        Object result = controller.getWeeklyReport();
        
        assertTrue(result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("weekly", report.get("period"));
    }

    @Test
    public void test_Monthly_report_controller_returns_correct_period_metadata() {
        MonthlyReportController controller = new MonthlyReportController();
        Object result = controller.getMonthlyReport();
        
        assertTrue(result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("monthly", report.get("period"));
    }
}
