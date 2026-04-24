package com.uow.FRA;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ViewFRAController {
    public List<FRA> viewAllFRAs() {
        return FRA.findAllFRAs(); 
    }
}