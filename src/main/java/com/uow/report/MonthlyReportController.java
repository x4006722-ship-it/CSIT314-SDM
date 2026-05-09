package com.uow.report;

import com.uow.fra.FRA;
import com.uow.useraccount.UserAccount;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class MonthlyReportController {

    private final FRA fra = new FRA();
    private final UserAccount userAccount = new UserAccount();

    public Object getMonthlyReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", "monthly");
        report.put("fraStats", fra.getMonthlyFraStats());
        report.put("userStats", userAccount.getMonthlyUserStats());
        return report;
    }
}
