// package com.uow.fra;
// import org.springframework.stereotype.Service;
// import java.util.List;
// @Service
// public class ViewFRAController {
//     public List<FRA> viewAllFRAs() {
//         return FRA.findAllFRAs(); 
//     }
// }
package com.uow.fra;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ViewFRAController {
    // 加上 fundRaiserId 参数，并传递给底层
    public List<FRA> viewAllFRAs(String fundRaiserId) {
        return FRA.findAllFRAs(fundRaiserId); 
    }
}