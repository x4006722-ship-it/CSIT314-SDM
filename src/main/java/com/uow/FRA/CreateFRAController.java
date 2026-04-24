package com.uow.FRA;
import org.springframework.stereotype.Service;

@Service
public class CreateFRAController {
    public String createFRA(FRA fraData) {
        if (fraData.getFraTitle() == null || fraData.getFraTitle().isEmpty()) {
            return "Title is required";
        }
        return fraData.saveFRA() ? "Success" : "Database Error";
    }
}
