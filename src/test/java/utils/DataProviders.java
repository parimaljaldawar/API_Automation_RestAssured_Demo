package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to provide test data from JSON and CSV files.
 * <p>
 * Each method returns data in a two-dimensional Object array,
 * which is useful for test frameworks like TestNG's @DataProvider.
 */

public class DataProviders {

    /**
     * Reads data from a JSON file and returns it as an Object[][].
     * <p>
     * The JSON file is expected to contain an array of objects.
     * Each object is mapped to a Map<String, String> and then placed into a single row
     * of the returned Object[][].
     *
     * @return a two-dimensional Object array containing maps of the JSON data
     * @throws IOException if the file is not found or parsing fails
     */
	
	@DataProvider(name = "jsonDataProvider")
	public Object[][] jsonDataProvider() throws IOException {
	    String filepath = ".\\testData\\Product.json";
	    ObjectMapper objectMapper = new ObjectMapper();

	    try {
	        // Read the JSON file and convert its contents to a list of maps
	        List<Map<String, String>> dataList = objectMapper.readValue(new File(filepath),
	                new TypeReference<List<Map<String, String>>>() {});

	        // Convert the List<Map<String, String>> into Object[][]
	        Object[][] dataArray = new Object[dataList.size()][];
	        for (int i = 0; i < dataList.size(); i++) {
	            dataArray[i] = new Object[] { dataList.get(i) };
	        }
	        return dataArray;

	    } catch (IOException e) {
	        // Log the exception and throw it back for handling in the test framework
	        System.err.println("Error reading JSON file: " + e.getMessage());
	        throw e;
	    }
	}
    /**
     * Reads data from a CSV file and returns it as an Object[][].
     * <p>
     * The CSV file is expected to have a header row, which will be skipped.
     * Each subsequent row is split on commas and stored as a String array in one row of the returned Object[][].
     *
     * @return a two-dimensional Object array containing CSV rows as String arrays
     * @throws IOException if the file is not found or reading fails
     */
    public Object[][] csvDataProvider() throws IOException {

        // Define the path to your CSV file. Update this path if needed.
        String filepath = ".\\testdata\\Product.csv";

        // Create a list to store each row of CSV data. Each row is a String array.
        List<String[]> dataList = new ArrayList<>();

        // Use try-with-resources to automatically close the BufferedReader after reading.
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            // Read and skip the header line since it contains column names.
            br.readLine();

            // Read each subsequent line of the CSV file.
            String line;
            while ((line = br.readLine()) != null) {

                // Split the line into individual values using commas as separators.
                String[] data = line.split(",");
                dataList.add(data);
            }
        }

        // Convert the List<String[]> into a two-dimensional Object array for data provider usage.
        Object[][] dataArray = new Object[dataList.size()][];
        for (int i = 0; i < dataList.size(); i++) {
            
        	// Each row of CSV data is wrapped in an Object array for test data usage.
            dataArray[i] = new Object[] { dataList.get(i) };
        }
        return dataArray;
    }
}