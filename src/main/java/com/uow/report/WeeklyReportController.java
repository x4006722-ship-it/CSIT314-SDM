package com.uow.report;

import com.uow.useraccount.UserAccount;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class WeeklyReportController {

    private final UserAccount userAccount = new UserAccount();

    public Object getWeeklyReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", "weekly");
        report.put("userStats", userAccount.getWeeklyUserStats());
        return report;
    }
}
