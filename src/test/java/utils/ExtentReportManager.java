package utils;

import java.io.File;
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

public class ExtentReportManager implements ITestListener {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();
    private static Map<String, ExtentTest> testSuiteMap = new ConcurrentHashMap<>();
    private static String reportName;

    public synchronized static ExtentReports getExtentInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            reportName = "Extent-Report-" + timestamp + ".html";
            String reportDirPath = System.getProperty("user.dir") + File.separator + "reports";
            File reportDir = new File(reportDirPath);

            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            String reportPath = reportDirPath + File.separator + reportName;
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            sparkReporter.config().setDocumentTitle("RestAssured Automation Project");
            sparkReporter.config().setReportName("Test Execution Summary");
            sparkReporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("Application", "RestAssuredDemo - Fake Store API");
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("CI Execution", "Jenkins");

            try {
                extent.setSystemInfo("HostName", InetAddress.getLocalHost().getHostName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return extent;
    }

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        ExtentTest suiteNode = getExtentInstance().createTest(suiteName);
        testSuiteMap.put(context.getName(), suiteNode);

        suiteNode.info("Suite Started: " + suiteName);
        suiteNode.assignCategory("Suite: " + suiteName);

        getExtentInstance().setSystemInfo("OS", context.getCurrentXmlTest().getParameter("os"));
        getExtentInstance().setSystemInfo("Browser", context.getCurrentXmlTest().getParameter("browser"));
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest suiteNode = testSuiteMap.get(result.getTestContext().getName());
        ExtentTest methodNode = suiteNode.createNode(result.getMethod().getMethodName());
        methodNode.assignCategory(result.getTestContext().getName());
        testNode.set(methodNode);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.PASS, result.getMethod().getMethodName() + " Passed.");
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.FAIL, result.getMethod().getMethodName() + " Failed.");
        test.log(Status.FAIL, result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.SKIP, result.getMethod().getMethodName() + " Skipped.");
        test.info("Reason: " + result.getThrowable());
        test.info("Execution Time: " + getExecutionTime(result) + " ms");
    }

    @Override
    public void onFinish(ITestContext testContext) {
        ExtentTest suiteNode = testSuiteMap.get(testContext.getName());
        suiteNode.info("Suite Finished: " + testContext.getSuite().getName());
        suiteNode.info("Passed: " + testContext.getPassedTests().size());
        suiteNode.info("Failed: " + testContext.getFailedTests().size());
        suiteNode.info("Skipped: " + testContext.getSkippedTests().size());

        getExtentInstance().flush();
    }

    private long getExecutionTime(ITestResult result) {
        return result.getEndMillis() - result.getStartMillis();
    }
}
