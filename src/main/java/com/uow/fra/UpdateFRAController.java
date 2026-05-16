// package com.uow.fra;

// import org.springframework.stereotype.Service;
// import java.util.List;

// @Service
// public class UpdateFRAController {
    
//     public boolean updateFRA(String fraId, FRA fraData) {
//         // 静默边界防线
//         if (fraData == null) {
//             return false;
//         }

//         // 查重逻辑 (排除自身)
//         if (fraData.getFraTitle() != null && !fraData.getFraTitle().trim().isEmpty()) {
//             List<FRA> existingFRAs = FRA.findAllFRAs();
//             for (FRA existing : existingFRAs) {
//                 if (!existing.getFraId().equals(fraId) && 
//                     existing.getFraTitle().equalsIgnoreCase(fraData.getFraTitle().trim())) {
//                     return false;
//                 }
//             }
//         }

//         // 日期逻辑
//         if (fraData.getStartedAt() != null && fraData.getEndedAt() != null) {
//             if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
//                 return false;
//             }
//         }

//         fraData.setFraId(fraId);
//         return fraData.updateFRAData();
//     }
// }
package com.uow.fra;
import org.springframework.stereotype.Service;

@Service
public class UpdateFRAController {
    
    public boolean updateFRA(String fraId, FRA fraData) {
        // 1. 和 Create 保持同等强度的边界防御
        if (fraData == null || fraId == null || fraId.trim().isEmpty() ||
            fraData.getFraTitle() == null || fraData.getFraTitle().trim().isEmpty() ||
            fraData.getFraTargetAmount() == null || fraData.getFraTargetAmount() <= 0 ||
            fraData.getStartedAt() == null || fraData.getStartedAt().trim().isEmpty() ||
            fraData.getEndedAt() == null || fraData.getEndedAt().trim().isEmpty()) {
            return false;
        }

        // 2. 日期逻辑
        if (fraData.getStartedAt().compareTo(fraData.getEndedAt()) >= 0) {
            return false;
        }

        // 3. 高性能业务查重 (排除自身)
        if (FRA.isDuplicateTitle(fraData.getFraTitle(), fraId)) {
            return false;
        }

        fraData.setFraId(fraId);
        return fraData.updateFRAData();
    }
}