package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojo.Product;
import routes.Routes;

/**
 * Test class to validate the creation and subsequent deletion of a new product using the REST API.
 * <p>
 * This class uses data-driven tests where product details are supplied via a JSON data provider. 
 * The test performs the following operations:
 * <ol>
 *   <li>Extract test data and create a Product object.</li>
 *   <li>Send a POST request to create the product.</li>
 *   <li>Validate the response ensuring successful creation (status code, non-empty response, valid product ID, matching title).</li>
 *   <li>Extract and log the product ID from the response.</li>
 *   <li>Send a DELETE request to remove the created product from the system.</li>
 *   <li>Log the deletion confirmation.</li>
 * </ol>
 */
public class ProductDataDrivenTest extends BaseClass {

    /**
     * Creates a new product using data provided by the JSON data provider and then deletes it to clean up.
     * <p>
     * The test method:
     * <ul>
     *     <li>Extracts required product details from the provided data map.</li>
     *     <li>Constructs a {@link Product} object using the data.</li>
     *     <li>Sends a POST request to add the new product.</li>
     *     <li>Validates the response to ensure the product is successfully created.</li>
     *     <li>Extracts the product ID from the response.</li>
     *     <li>Sends a DELETE request to remove the product from the system.</li>
     * </ul>
     *
     * @param data A map containing product details (keys: title, price, category, description, image)
     */
	@Test(dataProvider = "jsonDataProvider", dataProviderClass = utils.DataProviders.class)
    public void testAddNewProduct(Map<String, String> data) {
        // Extract product data from the provided map.
        String title = data.get("title");
        double price = Double.parseDouble(data.get("price"));
        String category = data.get("category");
        String description = data.get("description");
        String image = data.get("image");

        // Create a new Product object using the extracted data.
        Product newProduct = new Product(title, price, description, image, category);

        // Send a POST request to create the product.
        // The request includes the product object in the request body as JSON.
        Response response = given()
                .contentType(ContentType.JSON)
                .body(newProduct)
            .when()
                .post(Routes.CREATE_PRODUCT)
            .then()
                // Validate that the response status and data are correct.
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("id", notNullValue())
                .body("title", equalTo(newProduct.getTitle()))
                .log().body()       // Log the response body for debugging purposes.
                .extract()
                .response();       // Extract the complete response.

        // Extract the product ID from the response using JsonPath.
        int productId = response.jsonPath().getInt("id");

        // Log the complete response body and the extracted product ID for verification.
        System.out.println("Response Body: " + response.getBody().asString());
        System.out.println("Created Product ID==>: " + productId);

        // ---------------------------
        // Delete the created product
        // ---------------------------
        // Send a DELETE request using the extracted productId as a path parameter.
        // This helps in cleaning up test data after validation.
        given()
            .pathParam("id", productId)
        .when()
            .delete(Routes.DELETE_PRODUCT)
        .then()
            .statusCode(200);  // Validate that the deletion was successful (HTTP 200).

        // Log the deletion outcome.
        System.out.println("Deleted Product ID==>: " + productId);
    }
}
