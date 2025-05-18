package utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentReportManager sets up and manages the Extent Report generation lifecycle for TestNG test runs.
 * Compatible with Jenkins pipeline executions and local test executions.
 */
public class ExtentReportManager implements ITestListener {

    // Shared ExtentReports instance across the suite
    private static ExtentReports extent;

    // Thread-safe instance to handle parallel execution
    private static ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    // Map to store ExtentTest references at the suite level
    private static Map<String, ExtentTest> testSuiteMap = new ConcurrentHashMap<>();

    // Report file name with timestamp
    private static String reportName;

    // Full path of the generated report file
    private static String reportPath;

    /**
     * Initializes and returns a singleton ExtentReports instance.
     * Sets up the report path and applies UI configurations.
     */
    public synchronized static ExtentReports getExtentInstance() {
        if (extent == null) {
            try {
                // Generate timestamped report name
                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                reportName = "Extent-Report-" + timestamp + ".html";

                // Set the directory for reports under project root
                String reportDirPath = System.getProperty("user.dir") + File.separator + "reports";
                File reportDir = new File(reportDirPath);

                // Create directory if it doesn't exist
                if (!reportDir.exists()) {
                    boolean created = reportDir.mkdirs();
                    if (!created) {
                        System.err.println("Failed to create report directory at: " + reportDirPath);
                    }
                }

                // Create full report path with canonical resolution
                reportPath = new File(reportDir, reportName).getCanonicalPath();

                // Setup ExtentSparkReporter (HTML Reporter)
                ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
                sparkReporter.config().setDocumentTitle("RestAssured Automation Project");
                sparkReporter.config().setReportName("Test Execution Summary");
                sparkReporter.config().setTheme(Theme.DARK); // Options: DARK, STANDARD

                // Attach the reporter to ExtentReports
                extent = new ExtentReports();
                extent.attachReporter(sparkReporter);

                // Add common system/environment info to the report
                extent.setSystemInfo("Application", "RestAssuredDemo - Fake Store API");
                extent.setSystemInfo("Operating System", System.getProperty("os.name"));
                extent.setSystemInfo("User Name", System.getProperty("user.name"));
                extent.setSystemInfo("Environment", "QA");
                extent.setSystemInfo("CI Execution", "Jenkins");

                // Add hostname (useful in multi-node Jenkins environments)
                try {
                    extent.setSystemInfo("HostName", InetAddress.getLocalHost().getHostName());
                } catch (Exception e) {
                    System.err.println("Failed to get HostName: " + e.getMessage());
                }

                System.out.println("Extent report will be generated at: " + reportPath);

            } catch (IOException e) {
                System.err.println("Exception while setting up report path: " + e.getMessage());
            }
        }
        return extent;
    }

    /**
     * Triggered before a suite starts.
     * Creates a parent node in the report for the test suite.
     */
    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        ExtentTest suiteNode = getExtentInstance().createTest(suiteName);
        testSuiteMap.put(context.getName(), suiteNode);

        suiteNode.info("Suite Started: " + suiteName);
        suiteNode.assignCategory("Suite: " + suiteName);

        // Optional: Add dynamic environment info passed via testng.xml
        try {
            String osParam = context.getCurrentXmlTest().getParameter("os");
            String browserParam = context.getCurrentXmlTest().getParameter("browser");

            getExtentInstance().setSystemInfo("OS", osParam != null ? osParam : "NA");
            getExtentInstance().setSystemInfo("Browser", browserParam != null ? browserParam : "NA");
        } catch (Exception e) {
            System.err.println("Warning: Parameters for OS/Browser not found.");
        }
    }

    /**
     * Called before each test method starts.
     * Creates a test method node under the suite.
     */
    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest suiteNode = testSuiteMap.get(result.getTestContext().getName());
        ExtentTest methodNode = suiteNode.createNode(result.getMethod().getMethodName());
        methodNode.assignCategory(result.getTestContext().getName());
        testNode.set(methodNode);
    }

    /**
     * Called when a test method passes.
     * Logs status and duration to report.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.PASS, result.getMethod().getMethodName() + " Passed.");
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    /**
     * Called when a test method fails.
     * Logs error and duration.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.FAIL, result.getMethod().getMethodName() + " Failed.");
        test.log(Status.FAIL, result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    /**
     * Called when a test method is skipped.
     * Logs reason and duration.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.SKIP, result.getMethod().getMethodName() + " Skipped.");
        test.info("Reason: " + result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    /**
     * Called after all tests in the suite have finished.
     * Logs summary statistics and flushes the report.
     */
    @Override
    public void onFinish(ITestContext testContext) {
        ExtentTest suiteNode = testSuiteMap.get(testContext.getName());
        suiteNode.info("Suite Finished: " + testContext.getSuite().getName());
        suiteNode.info("Passed: " + testContext.getPassedTests().size());
        suiteNode.info("Failed: " + testContext.getFailedTests().size());
        suiteNode.info("Skipped: " + testContext.getSkippedTests().size());

        // Flush the report to write all data to the file system
        getExtentInstance().flush();
        System.out.println("Extent Report successfully flushed. Check at: " + reportPath);
    }

    /**
     * Utility method to calculate execution time of test methods.
     */
    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
