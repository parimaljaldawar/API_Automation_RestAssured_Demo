package testcases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.testng.annotations.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.config.HeaderConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import routes.Routes;
import utils.ConfigReader;

import org.testng.annotations.AfterSuite;
import utils.ZapReportGenerator;


import io.restassured.specification.ProxySpecification;


/**
 * BaseClass provides the common setup for REST Assured tests.
 * <p>
 * It configures the base URI for API calls, initializes a configuration reader, 
 * and sets up filters to log HTTP requests and responses.
 * In addition, it includes utility methods for verifying the sorting order of integer lists.
 * </p>
 */
public class BaseClass {

    // Global configuration reader instance to load properties or settings.
    protected ConfigReader configReader;
    
    // Filters to log HTTP requests and responses during API testing.
    protected RequestLoggingFilter requestLoggingFilter;
    protected ResponseLoggingFilter responseLoggingFilter;
    
    /**
     * The setup method initializes the test configuration and logging mechanisms.
     * <p>
     * It sets the base URI for REST Assured using the value provided by {@link Routes},
     * instantiates the configuration reader, ensures that the logging directory exists,
     * and configures the logging filters to capture API request and response details.
     * </p>
     *
     * @throws FileNotFoundException if the log file cannot be created or accessed.
     */
    @BeforeClass
    public void setup() throws FileNotFoundException {
        // Set the base URI for the API calls.
        RestAssured.baseURI = Routes.BASE_URL;

        // Initialize the configuration reader to load application properties.
        configReader = new ConfigReader();
        
        // Ensure the logs directory exists; if not, create it.
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();  // Create the directory if it does not already exist.
        }
        
        // Create a FileOutputStream for capturing logs in the designated file.
        File logFile = new File(logDir, "test_logging.log");
        FileOutputStream fos = new FileOutputStream(logFile);
        
        // PrintStream for auto-flushing log outputs.
        PrintStream logStream = new PrintStream(fos, true);
        
        // Set up request and response logging filters using the PrintStream.
        requestLoggingFilter = new RequestLoggingFilter(logStream);
        responseLoggingFilter = new ResponseLoggingFilter(logStream);
        
        // Apply the logging filters to all REST Assured requests.
        RestAssured.filters(requestLoggingFilter, responseLoggingFilter);
        
        // Configure Rest Assured to use OWASP ZAP as the proxy
        //RestAssured.proxy("localhost", 8081);
        //RestAssured.config = RestAssured.config().headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("API-Key"));
    }
    
    /**
     * Checks if a list of integers is sorted in descending order.
     * <p>
     * The method iterates through the list and compares each element with its predecessor.
     * If any element is greater than the previous element, the list is not sorted in descending order.
     * </p>
     *
     * @param list the list of integers to be checked.
     * @return {@code true} if the list is sorted in descending order, otherwise {@code false}.
     */
    public boolean isSortedDescending(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            // If an element is found that is greater than the previous element, list is not descending.
            if (list.get(i) > list.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if a list of integers is sorted in ascending order.
     * <p>
     * The method iterates through the list and compares each element with its predecessor.
     * If any element is found that is less than the previous element, the list is not sorted in ascending order.
     * </p>
     *
     * @param list the list of integers to be checked.
     * @return {@code true} if the list is sorted in ascending order, otherwise {@code false}.
     */
    public boolean isSortedAscending(List<Integer> list) {
        for (int i = 1; i < list.size(); i++) {
            // If an element is less than the previous element, list is not ascending.
            if (list.get(i) < list.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
    
}
