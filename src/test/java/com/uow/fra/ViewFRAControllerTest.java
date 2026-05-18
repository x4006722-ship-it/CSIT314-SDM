package com.uow.fra;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class ViewFRAControllerTest {

    private ViewFRAController controller;

    @Before
    public void setUp() {
        controller = new ViewFRAController();
    }

    @Test
    public void test_View_all_returns_a_list_for_fundraiser() {
        List<FRA> result = controller.viewAllFRAs("user123");
        assertNotNull(result);
    }
}
