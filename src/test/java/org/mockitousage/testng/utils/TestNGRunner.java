package org.mockitousage.testng.utils;

import org.testng.IConfigurationListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TestNGRunner {

    public Result run(Class<?> ... testClasses) {
        TestNG testNG = new TestNG();
        testNG.setVerbose(0);
        testNG.setUseDefaultListeners(false);

        FailureRecordingListener failureRecorder = new FailureRecordingListener();
        testNG.addListener(failureRecorder);

        testNG.setTestClasses(testClasses);

        // run test in separate thread in order to avoid conflict MockitoSession from parent test
        Thread testThread = new Thread(testNG::run);
        testThread.start();
        try {
            testThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new Result(testNG, failureRecorder);
    }

    public static class Result {
        private final TestNG testNG;
        private final FailureRecordingListener failureRecorder;

        public Result(TestNG testNG, FailureRecordingListener failureRecorder) {
            this.testNG = testNG;
            this.failureRecorder = failureRecorder;
        }

        public Throwable getFailure() {
            Throwable failure = failureRecorder.lastThrowable();
            if (failure != null && !testNG.hasFailure()) {
                throw new AssertionError("Failure was recorded but TestNG does not indicate a failure.");
            }
            return failure;
        }
    }

    /**
     * <strong>Not thread-safe</strong> listener that record only failures, either on the test or on a configuration method.
     */
    public static class FailureRecordingListener implements ITestListener, IConfigurationListener {

        public List<ITestResult> failedTestResults = new ArrayList<>();

        public void onTestFailure(ITestResult result) {
            failedTestResults.add(result);
        }

        public void onConfigurationFailure(ITestResult result) {
            failedTestResults.add(result);
        }

        public Throwable lastThrowable() {
            ListIterator<ITestResult> iterator = failedTestResults.listIterator(failedTestResults.size());
            return iterator.hasPrevious() ? iterator.previous().getThrowable() : null;
        }

        public void clear() {
            failedTestResults.clear();
        }

        // don't care bellow


        public void onConfigurationSuccess(ITestResult itr) { }
        public void onConfigurationSkip(ITestResult itr) { }
        public void onTestStart(ITestResult result) { }
        public void onTestSuccess(ITestResult result) { }
        public void onTestSkipped(ITestResult result) { }
        public void onTestFailedButWithinSuccessPercentage(ITestResult result) { }
        public void onStart(ITestContext context) { }
        public void onFinish(ITestContext context) { }
    }
}
