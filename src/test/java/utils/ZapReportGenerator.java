package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * Utility class for generating an OWASP ZAP HTML report.
 * <p>
 * This class uses the ZAP Client API to connect to a running ZAP instance, generate
 * an HTML report based on the current scan data, and then write the report to a file.
 * </p>
 */
public class ZapReportGenerator {

    // ZAP configuration: update these if you use a different host, port or API key.
    private static final String ZAP_ADDRESS = "localhost";
    private static final int ZAP_PORT = 8080;
    // If ZAP is configured with an API key, set it here; otherwise, leave as an empty string.
    private static final String ZAP_API_KEY = "";

    /**
     * Generates an OWASP ZAP HTML report and writes it to the specified output file.
     *
     * @param outputFilePath the file system path where the report will be saved
     */
    public static void generateReport(String outputFilePath) {
        ClientApi api = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);
        try {
            // Retrieve the HTML report from ZAP as a byte array
            byte[] report = api.core.htmlreport();
            // Write the byte array to the specified file
            FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
            fos.write(report);
            fos.close();
            System.out.println("OWASP ZAP report generated at: " + outputFilePath);
        } catch (ClientApiException | IOException e) {
            System.err.println("Failed to generate OWASP ZAP report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
