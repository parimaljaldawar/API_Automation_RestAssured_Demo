package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ExtentReportManager provides a custom TestNG listener implementation for generating Extent Reports.
 * <p>
 * This class implements the {@link ITestListener} interface to provide reporting functionality,
 * including logging test start, success, failure, and skip events. An HTML report is generated using the 
 * ExtentReports framework, and additional system information is included.
 */
public class ExtentReportManager implements ITestListener {

    // Singleton instance of ExtentReports.
    private static ExtentReports extent;
    // Spark reporter for generating a user-friendly HTML report.
    private static ExtentSparkReporter sparkReporter;
    // ThreadLocal to ensure thread-safe access to ExtentTest objects.
    private static ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();
    // ConcurrentHashMap for storing suite-level ExtentTest nodes.
    private static Map<String, ExtentTest> testSuiteMap = new ConcurrentHashMap<>();
    // Name of the generated report file.
    private static String reportName;

    /**
     * Provides a singleton instance of {@link ExtentReports}. 
     * <p>
     * If the ExtentReports instance is not already created, it initializes the reporter, configures the report
     * settings (e.g., document title, report name, theme), and attaches necessary system info.
     * 
     * @return A singleton instance of ExtentReports.
     */
    public synchronized static ExtentReports getExtentInstance() {
        if (extent == null) {
            // Generate a timestamp to create a unique report file name.
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            reportName = "Extent-Report-" + timestamp + ".html";
            // Construct the absolute path of the report file using the system property "user.dir".
            String reportPath = System.getProperty("user.dir") + "\\reports\\" + reportName;

            // Initialize the ExtentSparkReporter with the report path.
            sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("RestAssuredAutomationProject"); // Title of the report.
            sparkReporter.config().setReportName("Test Execution Summary");          // Name of the report.
            sparkReporter.config().setTheme(Theme.DARK);                             // Set the theme for the report.

            // Initialize ExtentReports and attach the SparkReporter.
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            // Set additional system information for more context in the report.
            extent.setSystemInfo("Application", "RestAssuredDemo - Fake Store API");
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("user", "Parimal");

            // Try to add the hostname; log the exception if it occurs.
            try {
                extent.setSystemInfo("HostName", InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return extent;
    }

    /**
     * Called at the start of a TestNG suite.
     * <p>
     * Sets up the suite-level node in the Extent Report and logs suite start information. 
     * Also, extracts and sets additional system information (like OS and Browser) from TestNG XML parameters.
     *
     * @param context The ITestContext containing details about the test run.
     */
    @Override
    public void onStart(ITestContext context) {
        // Retrieve the suite name from the TestNG context.
        String suiteName = context.getSuite().getName();

        // Create a suite node in the Extent Report.
        ExtentTest suiteNode = getExtentInstance().createTest(suiteName);
        // Store the suite node using the TestNG context name as the key.
        testSuiteMap.put(context.getName(), suiteNode);

        // Log the suite start and assign a category.
        suiteNode.info("Suite Started: " + suiteName);
        suiteNode.assignCategory("Suite: " + suiteName);

        // Retrieve the ExtentReports instance and add additional system info from the XML parameters.
        ExtentReports extentReports = getExtentInstance();
        extentReports.setSystemInfo("OS", context.getCurrentXmlTest().getParameter("os"));
        extentReports.setSystemInfo("Browser", context.getCurrentXmlTest().getParameter("browser"));
    }

    /**
     * Called each time a test method is started.
     * <p>
     * Creates a node within the current suite node for the individual test method so that its results 
     * can be logged separately in the report.
     *
     * @param result The ITestResult providing details about the test method.
     */
    @Override
    public void onTestStart(ITestResult result) {
        // Get the suite node for the current test.
        ExtentTest suiteNode = testSuiteMap.get(result.getTestContext().getName());
        // Create a test method node under the suite node.
        ExtentTest methodNode = suiteNode.createNode(result.getMethod().getMethodName());
        methodNode.assignCategory(result.getTestContext().getName());
        // Store the test method node in a thread-safe manner.
        testNode.set(methodNode);
    }

    /**
     * Called when a test method succeeds.
     * <p>
     * Logs the success status and execution time for the test method in the Extent Report.
     *
     * @param result The ITestResult containing information about the test method.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.PASS, result.getMethod().getMethodName() + " Passed.");
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    /**
     * Called when a test method fails.
     * <p>
     * Logs the failure status, the throwable error, and the execution time in the Extent Report.
     *
     * @param result The ITestResult containing information about the test method.
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
     * <p>
     * Logs the skip status along with the reason (if provided) and the execution time in the Extent Report.
     *
     * @param result The ITestResult containing information about the test method.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.SKIP, result.getMethod().getMethodName() + " Skipped.");
        test.info("Reason: " + result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    /**
     * Called at the end of the TestNG suite.
     * <p>
     * Logs the overall suite results (number of passed, failed, and skipped tests), flushes the report data
     * to disk, and attempts to automatically open the generated report in the default browser.
     *
     * @param testContext The ITestContext containing the test run details.
     */
    @Override
    public void onFinish(ITestContext testContext) {
        // Retrieve the suite node using the context name.
        ExtentTest suiteNode = testSuiteMap.get(testContext.getName());
        suiteNode.info("Suite Finished: " + testContext.getSuite().getName());
        suiteNode.info("Passed: " + testContext.getPassedTests().size());
        suiteNode.info("Failed: " + testContext.getFailedTests().size());
        suiteNode.info("Skipped: " + testContext.getSkippedTests().size());
        
        // Flush the ExtentReports to write all information to the HTML file.
        getExtentInstance().flush();
        
        // Attempt to open the generated report automatically.
        openReport();
    }

    /**
     * Opens the generated HTML report using the default system browser.
     * <p>
     * Leverages the {@link Desktop} API; if an IOException occurs, the exception is printed to help with debugging.
     */
    private void openReport() {
        try {
            // Construct the report file using the current working directory and report name.
            File reportFile = new File(System.getProperty("user.dir") + "\\reports\\" + reportName);
            // Open the report file in the default system browser.
            Desktop.getDesktop().browse(reportFile.toURI());
        } catch (IOException e) {
            // Print the stack trace in case of any IO exceptions.
            e.printStackTrace();
        }
    }

    /**
     * Calculates and returns the execution time for a test method.
     * <p>
     * Execution time is determined by subtracting the test start time from its end time.
     *
     * @param result The ITestResult containing the start and end times.
     * @return The execution time in milliseconds.
     */
    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
