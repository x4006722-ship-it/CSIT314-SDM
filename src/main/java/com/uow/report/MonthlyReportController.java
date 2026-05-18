package com.uow.report;

import com.uow.useraccount.UserAccount;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class MonthlyReportController {

    private final UserAccount userAccount = new UserAccount();

    public Object getMonthlyReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", "monthly");
        report.put("userStats", userAccount.getMonthlyUserStats());
        return report;
    }
}
