package com.uow.report;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReportPageTest {

    private ReportPage reportPage;

    @Before
    public void setUp() {
        reportPage = new ReportPage();
    }

    @Test
    public void testShowReportPage_ReturnsCorrectForwardRoute() {
        // Execution
        String route = reportPage.showReportPage();
        
        // Verification: Ensure it routes to the correct Platform Page HTML
        assertEquals("Should forward the user to the PlatformPage.html", "forward:/PlatformPage.html", route);
    }
}
