package com.uow.FRA;
import org.springframework.stereotype.Service;

@Service
public class RemoveFRAController {
    public String deleteFRA(String fraId) {
        try {
            FRA target = new FRA();
            target.setFraId(fraId);
            target.removeFRA();
            return "Success";
        } catch (Exception e) { 
            System.err.println("Delete failed: " + e.getMessage());
            return "Error"; 
        }
    }
}
