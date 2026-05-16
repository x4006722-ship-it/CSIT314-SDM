package com.uow.report;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestRunner {
    public static void main(String[] args) {
        JUnitCore core = new JUnitCore();
        core.addListener(new PytestStyleListener());

        long startTime = System.currentTimeMillis();
        
        Result result = core.run(
            ReportControllersTest.class,
            ReportPageTest.class
        );
        
        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;
        
        System.out.println("");
        System.out.printf("=========================== %d passed in %.2fs ===========================\n", 
                result.getRunCount() - result.getFailureCount(), seconds);
    }
}

class PytestStyleListener extends RunListener {
    private boolean testFailed = false;

    @Override
    public void testStarted(Description description) {
        testFailed = false;
    }

    @Override
    public void testFailure(Failure failure) {
        testFailed = true;
        printTestResult("FAILED", failure.getDescription());
    }

    @Override
    public void testFinished(Description description) {
        if (!testFailed) {
            printTestResult("PASSED", description);
        }
    }

    private void printTestResult(String status, Description description) {
        String className = description.getTestClass().getSimpleName();
        String methodName = description.getMethodName();
        
        String readableName = methodName;
        if (readableName.startsWith("test_")) {
            readableName = readableName.substring(5);
        }
        readableName = readableName.replace("_", " ");
        
        if (readableName.length() > 0) {
            readableName = readableName.substring(0, 1).toUpperCase() + readableName.substring(1);
        }

        System.out.println("TEST " + status + ": " + className + " - " + readableName);
        System.out.println(status); 
        System.out.println("tests/" + className + ".java::" + methodName);
        System.out.println("-------------------------------- live log call ---------------------------------");
    }
}