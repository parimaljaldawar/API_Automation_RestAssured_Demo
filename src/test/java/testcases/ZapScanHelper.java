package testcases;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

/**
 * ZapScanHelper is a utility class integrating with OWASP ZAP via its Java Client API.
 * Capabilities:
 *  - Active scan execution on URLs
 *  - HTML/XML vulnerability reports generation
 *  - Report storage on disk
 *  - ZAP shutdown upon completion
 */
public class ZapScanHelper {

    private static final String ZAP_ADDRESS = "localhost";
    private static final int ZAP_PORT = 8081;
    private static final String ZAP_API_KEY = "klnrsnbqmvttb0ulkkiti3ia5p";

    private final ClientApi api;

    public ZapScanHelper() {
        this.api = new ClientApi(ZAP_ADDRESS, ZAP_PORT, ZAP_API_KEY);
    }

    public ZapScanHelper(String zapAddress, int zapPort, String zapApiKey) {
        this.api = new ClientApi(zapAddress, zapPort, zapApiKey);
    }

    /**
     * Starts an active scan on the provided URL and generates an HTML report after completion.
     */
    public String startActiveScan(String targetUrl) throws ClientApiException, InterruptedException {
        if (targetUrl == null || targetUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Target URL cannot be null or empty.");
        }

        System.out.println("Initiating Active Scan on: " + targetUrl);

        ApiResponse response = api.ascan.scan(targetUrl, "true", "false", "", "", "");
        if (!(response instanceof ApiResponseElement)) {
            throw new ClientApiException("Unexpected response type from ZAP API.");
        }

        String scanId = ((ApiResponseElement) response).getValue();
        System.out.println("Scan started with ID: " + scanId);

        int progress = 0;
        while (progress < 100) {
            Thread.sleep(5000);
            try {
                ApiResponse statusResponse = api.ascan.status(scanId);
                progress = Integer.parseInt(((ApiResponseElement) statusResponse).getValue());
            } catch (NumberFormatException | ClientApiException e) {
                System.err.println("Scan progress error: " + e.getMessage());
                throw new RuntimeException("Scan failed or progress could not be fetched.");
            }
            System.out.println("Scan progress: " + progress + "%");
        }

        System.out.println("Scan complete. Generating report...");

        try {
            String reportContent = getHTMLReport();
            saveReportToFile(reportContent, "zap-report.html");
            System.out.println("Report saved to zap-report.html");
        } catch (ClientApiException | IOException e) {
            System.err.println("Report generation error: " + e.getMessage());
        }

        return scanId;
    }

    /**
     * Fetches the XML report from ZAP and returns as a String.
     */
    public String getXMLReport() throws ClientApiException {
        byte[] responseBytes = api.core.xmlreport();
        if (responseBytes == null || responseBytes.length == 0) {
            throw new RuntimeException("ZAP returned empty XML report.");
        }
        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    /**
     * Fetches the HTML report from ZAP and returns as a String.
     */
    public String getHTMLReport() throws ClientApiException {
        byte[] responseBytes = api.core.htmlreport();
        if (responseBytes == null || responseBytes.length == 0) {
            throw new RuntimeException("ZAP returned empty HTML report.");
        }
        return new String(responseBytes, StandardCharsets.UTF_8);
    }

    /**
     * Saves the provided report content to a file on disk.
     */
    private void saveReportToFile(String reportContent, String fileName) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
            outputStream.write(reportContent.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Shuts down ZAP gracefully.
     */
    public void shutdownZap() {
        try {
            api.core.shutdown();
        } catch (Exception e) {
            System.err.println("Failed to shut down ZAP: " + e.getMessage());
        }
    }
}
