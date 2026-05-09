package com.uow.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReportPage {

    @Autowired
    private DailyReportController dailyReportController;

    @Autowired
    private WeeklyReportController weeklyReportController;

    @Autowired
    private MonthlyReportController monthlyReportController;

    @GetMapping({"/report", "/showReportPage"})
    public String showReportPage() {
        return "forward:/PlatformPage.html";
    }

    @GetMapping("/api/report/daily")
    @ResponseBody
    public Object onGetDailyReport() {
        return dailyReportController.getDailyReport();
    }

    @GetMapping("/api/report/weekly")
    @ResponseBody
    public Object onGetWeeklyReport() {
        return weeklyReportController.getWeeklyReport();
    }

    @GetMapping("/api/report/monthly")
    @ResponseBody
    public Object onGetMonthlyReport() {
        return monthlyReportController.getMonthlyReport();
    }
}
