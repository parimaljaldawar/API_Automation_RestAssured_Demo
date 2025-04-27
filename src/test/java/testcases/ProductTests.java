package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.payloads;
import pojo.Product;
import routes.Routes;
import utils.ConfigReader;

/**
 * ProductTests contains API tests for performing CRUD operations on products.
 * <p>
 * This class validates various endpoints such as retrieving all products, getting a single product
 * (by ID), retrieving products with limits or sorted by order, as well as creating, updating, and
 * deleting products. The tests are built using REST Assured and TestNG.
 * </p>
 */
public class ProductTests extends BaseClass {

    // Logger instance for logging information during test execution.
    private static final Logger logger = LogManager.getLogger(ProductTests.class);

    /**
     * Test to retrieve all products from the API.
     * <p>
     * This test sends a GET request to the endpoint that returns all products,
     * and validates that the status code is 200 and that the response body has one or more entries.
     * </p>
     */
    @Test(priority = 1)
    public void testGetAllProducts() {
        // Send a GET request to retrieve all products and validate the response.
        given()
        .when()
            .get(Routes.GET_ALL_PRODUCTS)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().body();
    }

    /**
     * Test to retrieve a single product by its ID.
     * <p>
     * The product ID is obtained from the configuration properties. This test sends a GET request
     * using the product ID as a path parameter and validates that the product is retrieved successfully.
     * </p>
     */
    @Test(priority = 2)
    public void testGetSingleProductById() {
        // Retrieve the product ID from configuration.
        int productId = configReader.getIntProperty("productID");

        // Send a GET request for a single product and validate the response.
        given()
            .pathParam("id", productId)
        .when()
            .get(Routes.GET_PRODUCT_BY_ID)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().body();
    }

    /**
     * Test to retrieve a limited number of products.
     * <p>
     * This test sends a GET request with a limit (e.g., 5) as a path parameter, and validates that
     * the response contains a non-empty list.
     * </p>
     */
    @Test(priority = 3)
    public void testGetLimitedProduct() {
        // Send a GET request with a limit on the number of products returned.
        given()
            .pathParam("limit", 5)
        .when()
            .get(Routes.GET_PRODUCTS_WITH_LIMIT)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().body();
    }

    /**
     * Test to retrieve products sorted in descending order by ID.
     * <p>
     * This test sends a GET request with the query parameter for descending order,
     * extracts the list of product IDs from the response, and validates that the IDs are sorted in descending order.
     * It uses the utility method {@code isSortedDescending} (inherited from BaseClass) to perform the check.
     * </p>
     */
    @Test(priority = 4)
    public void testGetSortedProducts() {
        // Send a GET request for products sorted in descending order.
        Response response = given()
                .pathParam("order", "desc")
        .when()
                .get(Routes.GET_PRODUCTS_SORTED)
        .then()
                .statusCode(200)
                .extract().response();

        // Extract the list of product IDs from the JSON response.
        List<Integer> productIDs = response.jsonPath().getList("id", Integer.class);
        // Assert that the list is sorted in descending order using the inherited method.
        assertThat("Product IDs should be sorted in descending order", isSortedDescending(productIDs), is(true));
    }

    /**
     * Test to retrieve products sorted in ascending order by ID.
     * <p>
     * Similar to the descending order test, this test sends a GET request for products
     * sorted in ascending order and validates the sort order using the {@code isSortedAscending} utility method.
     * </p>
     */
    @Test(priority = 5)
    public void testGetSortedProductsAsc() {
        // Send a GET request for products sorted in ascending order.
        Response response = given()
                .pathParam("order", "asc")
        .when()
                .get(Routes.GET_PRODUCTS_SORTED)
        .then()
                .statusCode(200)
                .extract().response();

        // Extract the list of product IDs and verify ascending order.
        List<Integer> productIDs = response.jsonPath().getList("id", Integer.class);
        assertThat("Product IDs should be sorted in ascending order", isSortedAscending(productIDs), is(true));
    }

    /**
     * Test to retrieve all product categories.
     * <p>
     * This test sends a GET request to retrieve all categories and asserts that the response
     * contains at least one entry.
     * </p>
     */
    @Test(priority = 6)
    public void testGetAllCategories() {
        // Send GET request for all categories and validate that at least one category is returned.
        given()
        .when()
            .get(Routes.GET_ALL_CATEGORIES)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().body();
    }

    /**
     * Test to retrieve products by a specific category.
     * <p>
     * This test sends a GET request to retrieve products within a given category (e.g., "electronics")
     * and validates that all returned products belong to the specified category.
     * </p>
     */
    @Test(priority = 7)
    public void testGetProductByCategory() {
        // For this test, the category "electronics" is hard-coded. It can be parameterized as needed.
        given()
            .pathParam("category", "electronics")
        .when()
            .get(Routes.GET_PRODUCTS_BY_CATEGORY)
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .log().body()
            // Validate that every returned product has a non-null category and equals "electronics".
            .body("category", everyItem(notNullValue()))
            .body("category", everyItem(equalTo("electronics")));
    }

    /**
     * Test to add a new product.
     * <p>
     * This test creates a new product using a payload generated by {@code payloads.productpayload()},
     * sends a POST request to create the product, and validates the response by checking for a non-null ID
     * and matching title. The extracted product ID is then logged.
     * </p>
     */
    @Test(priority = 8)
    public void testAddNewProduct() {
        // Generate payload for a new product.
        Product newProduct = payloads.productpayload();

        // Make the POST request to add the new product and extract the response.
        Response response = 
            given()
                .contentType(ContentType.JSON)
                .body(newProduct)
            .when()
                .post(Routes.CREATE_PRODUCT)
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("id", notNullValue())
                .body("title", equalTo(newProduct.getTitle()))
                .log().body()
                .extract().response();

        // Extract the product ID from the response body using JsonPath.
        int productId = response.jsonPath().getInt("id");
        // Log the response details for debugging and reporting purposes.
        logger.info("Response Body: " + response.getBody().asString());
        logger.info("Extracted Product ID: " + productId);
    }

    /**
     * Test to update an existing product.
     * <p>
     * This test retrieves the product ID from configuration, generates an updated payload,
     * and sends a PUT request to update the product. The response is validated to ensure the update was successful.
     * </p>
     */
    @Test(priority = 9)
    public void testUpdateProduct() {
        // Retrieve the existing product ID from configuration.
        int productID = configReader.getIntProperty("productID");

        // Generate the payload for the updated product data.
        Product updatedPayload = payloads.productpayload();

        // Send a PUT request to update the product with the new payload.
        Response response = 
            given()
                .contentType(ContentType.JSON)
                .body(updatedPayload)
                .pathParam("id", productID)
            .when()
                .put(Routes.UPDATE_PRODUCT)
            .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("id", notNullValue())
                .body("title", equalTo(updatedPayload.getTitle()))
                .log().body()
                .extract().response();

        // Extract the updated product ID from the response.
        int extractedProductID = response.jsonPath().getInt("id");
        // Log the response details for transparency in test results.
        logger.info("Response Body: " + response.getBody().asString());
        logger.info("Extracted Product ID: " + extractedProductID);
    }

    /**
     * Test to delete a product.
     * <p>
     * This test retrieves a product ID from configuration and sends a DELETE request to remove
     * the product. It validates the response status code to ensure the deletion was successful.
     * </p>
     */
    @Test(priority = 10)
    public void testDeleteProduct() {
        // Retrieve the product ID from configuration that is to be deleted.
        int productID = configReader.getIntProperty("productID");

        // Send a DELETE request to remove the product and validate the successful deletion status code.
        given()
            .pathParam("id", productID)
        .when()
            .delete(Routes.DELETE_PRODUCT)
        .then()
            .statusCode(200);
    }
}
