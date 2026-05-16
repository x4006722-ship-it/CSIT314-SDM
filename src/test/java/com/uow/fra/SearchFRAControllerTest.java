package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SearchFRAControllerTest {
    
    private SearchFRAController controller;

    @Before
    public void setUp() {
        controller = new SearchFRAController();
    }

    @Test
    public void test_Search_returns_a_list_of_results() {
        // 【修改】在末尾加上一个空字符串 "" 作为第 6 个参数 (startDate)
        List<FRA> result = controller.searchFRA("Education", "all", "all", "fundRaiser", "1", "");
        assertNotNull(result);
    }
}
