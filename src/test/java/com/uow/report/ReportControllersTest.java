package com.uow.report;

import org.junit.Test;
import java.util.Map;
import static org.junit.Assert.*;

public class ReportControllersTest {

    @Test
    public void test_Daily_report_controller_returns_correct_data_structure() {
        DailyReportController controller = new DailyReportController();
        Object result = controller.getDailyReport();
        
        assertTrue("Result should be a Map", result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("daily", report.get("period"));
        // 使用 containsKey 检查结构更严谨，因为实际数据可能刚好查询为空(null)
        assertTrue("Should contain fraStats", report.containsKey("fraStats"));
        assertTrue("Should contain userStats", report.containsKey("userStats"));
    }

    @Test
    public void test_Weekly_report_controller_returns_correct_data_structure() {
        WeeklyReportController controller = new WeeklyReportController();
        Object result = controller.getWeeklyReport();
        
        assertTrue("Result should be a Map", result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("weekly", report.get("period"));
        // 补全缺失的断言
        assertTrue("Should contain fraStats", report.containsKey("fraStats"));
        assertTrue("Should contain userStats", report.containsKey("userStats"));
    }

    @Test
    public void test_Monthly_report_controller_returns_correct_data_structure() {
        MonthlyReportController controller = new MonthlyReportController();
        Object result = controller.getMonthlyReport();
        
        assertTrue("Result should be a Map", result instanceof Map);
        Map<?, ?> report = (Map<?, ?>) result;
        assertEquals("monthly", report.get("period"));
        // 补全缺失的断言
        assertTrue("Should contain fraStats", report.containsKey("fraStats"));
        assertTrue("Should contain userStats", report.containsKey("userStats"));
    }
}
