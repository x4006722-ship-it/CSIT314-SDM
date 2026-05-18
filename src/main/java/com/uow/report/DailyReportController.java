package com.uow.report;

import com.uow.fra.FRA;
import com.uow.useraccount.UserAccount;
import org.springframework.stereotype.Controller;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class DailyReportController {

    private final FRA fra = new FRA();
    private final UserAccount userAccount = new UserAccount();

    public Object getDailyReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("period", "daily");
        report.put("fraStats", fra.getDailyFraStats());
        report.put("userStats", userAccount.getDailyUserStats());
        return report;
    }
}

