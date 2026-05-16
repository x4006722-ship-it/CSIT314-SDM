package com.uow.report;

import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.Map;
import static org.junit.Assert.*;

public class ReportPageTest {

    private ReportPage reportPage;

    @Before
    public void setUp() throws Exception {
        reportPage = new ReportPage();
        
        // 使用反射手动注入依赖，防止 @Autowired 导致的 NPE (空指针异常)
        injectField("dailyReportController", new DailyReportController());
        injectField("weeklyReportController", new WeeklyReportController());
        injectField("monthlyReportController", new MonthlyReportController());
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = ReportPage.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(reportPage, value);
    }

    @Test
    public void test_Report_page_forwarding_path_is_correct() {
        assertEquals("forward:/PlatformPage.html", reportPage.showReportPage());
    }

    @Test
    public void test_Daily_report_api_endpoint_executes_safely() {
        Object result = reportPage.onGetDailyReport();
        assertNotNull(result);
        assertTrue(result instanceof Map);
        // 统一使用 assertEquals 以获得更好的错误报告
        assertEquals("daily", ((Map<?, ?>)result).get("period"));
    }

    @Test
    public void test_Weekly_report_api_endpoint_executes_safely() {
        Object result = reportPage.onGetWeeklyReport();
        assertNotNull(result);
        assertTrue(result instanceof Map);
        // 替换了原来的 assertTrue(....equals(...))
        assertEquals("weekly", ((Map<?, ?>)result).get("period"));
    }

    @Test
    public void test_Monthly_report_api_endpoint_executes_safely() {
        Object result = reportPage.onGetMonthlyReport();
        assertNotNull(result);
        assertTrue(result instanceof Map);
        // 替换了原来的 assertTrue(....equals(...))
        assertEquals("monthly", ((Map<?, ?>)result).get("period"));
    }
}
