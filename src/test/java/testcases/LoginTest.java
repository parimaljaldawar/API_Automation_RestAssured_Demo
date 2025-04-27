package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import payloads.payloads;
import pojo.Login;
import routes.Routes;

/**
 * Contains test cases for the authentication (login) API endpoint.
 * <p>
 * Extends BaseClass to inherit REST‑Assured setup (base URI, filters, etc.).
 * Renamed to avoid collision with the Login POJO.
 * </p>
 */
public class LoginTest extends BaseClass {

	/**
	 * Default no‑arg constructor so TestNG can instantiate this class.
	 * 
	 * @param pass
	 * @param user
	 */

	/**
	 * Test case: Attempt to log in with invalid credentials.
	 * <p>
	 * Uses payloads.loginpayload() to generate random username/password, then
	 * asserts that the API returns HTTP 401 and the correct error message.
	 * </p>
	 */
	@Test
	public void testInvalidUserLogin() {

		// Generate bogus credentials
		pojo.Login fake = payloads.loginpayload();

		given().contentType(ContentType.JSON) // we’re sending JSON
				.body(fake) // this is the Login POJO
				.when().post(Routes.AUTH_LOGIN) // POST to /auth/login
				.then().statusCode(401) // must be Unauthorized
				.body(equalTo("username or password is incorrect")).log().body(); // helpful for debugging
	}

	/**
	 * Test case: Log in with valid credentials from config.
	 * <p>
	 * Reads `username` and `password` from config.properties, sends them, and
	 * asserts HTTP 200 with a non‑null token.
	 * </p>
	 */
	@Test
	public void testValidUserLogin() {
		// Read real credentials
		String username = configReader.getProperty("username");
		String password = configReader.getProperty("password");

		Login login = new Login(username, password);

		given()
		.contentType(ContentType.JSON)
			.body(login)
		.when()
			.post(Routes.AUTH_LOGIN)
		.then()
			.log().body()
			.statusCode(200).body("token", notNullValue());
	}
}
