package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The {@code ConfigReader} class provides methods to load and retrieve configuration properties
 * from a properties file. This is used to centralize configuration values for the test framework.
 * <p>
 * On instantiation, the configuration file specified by {@code CONFIG_FILE_PATH} is loaded into
 * a {@link Properties} object, making it accessible throughout the application.
 * </p>
 */
public class ConfigReader {

    // Properties object holds the configuration key-value pairs loaded from the properties file.
    private static Properties properties;

    // The file path to the configuration properties file. It is specified relative to the project root.
    private static final String CONFIG_FILE_PATH = ".\\src\\test\\resources\\config.properties";

    /**
     * Constructs a new {@code ConfigReader} instance and loads the configuration properties.
     * <p>
     * The constructor attempts to read the configuration file using a {@link FileInputStream}
     * and loads the properties into a {@link Properties} object. If the file cannot be loaded,
     * an IOException is caught, the stack trace is printed, and a {@link RuntimeException} is thrown.
     * </p>
     *
     * @throws RuntimeException if the properties file cannot be found or loaded.
     */
    public ConfigReader() {
        // Initialize the properties object.
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            // Load the properties from the file.
            properties.load(fileInputStream);
        } catch (IOException e) {
            // Print error details and stop program execution by throwing a RuntimeException.
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties file");
        }
    }

    /**
     * Retrieves the value of the specified configuration property as a string.
     * <p>
     * This method searches for the property using the provided key. If the property is not found,
     * {@code null} is returned.
     * </p>
     *
     * @param key the key for the desired configuration property.
     * @return the property value as a string, or {@code null} if the property is not found.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Retrieves the value of the specified configuration property as an integer.
     * <p>
     * This method converts the property value associated with the given key into an integer.
     * If the property value is missing or not a valid integer, a {@link NumberFormatException} is caught,
     * its stack trace is printed, and {@code 0} is returned as a default value.
     * </p>
     *
     * @param key the key for the desired configuration property.
     * @return the property value as an integer; returns 0 if the value is not a valid integer.
     */
    public static int getIntProperty(String key) {
        try {
            // Attempt to parse the property value as an integer.
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            // Print error details and return a default value of 0 if parsing fails.
            e.printStackTrace();
        }
        return 0;
    }
}
