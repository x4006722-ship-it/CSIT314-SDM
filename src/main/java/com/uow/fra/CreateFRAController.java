package com.uow.fra;
import org.springframework.stereotype.Service;

@Service
public class CreateFRAController {
    public FRA createFRA(FRA fraData) {
        return fraData.saveFRA();
    }
}
