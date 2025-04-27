package routes;

/**
 * The {@code Routes} class contains constants for API endpoints used in the FakeStore API.
 * <p>
 * This class centralizes the management of URL paths required for various CRUD operations on products and categories.
 * These constants are used throughout the REST Assured test framework for building and sending HTTP requests.
 * </p>
 */
public class Routes {
    
    // Base URL for the FakeStore API.
    public static final String BASE_URL = "https://fakestoreapi.com";
    
    // =========================================================================
    // Product Modules - Endpoints for managing product resources.
    // =========================================================================

    /**
     * Retrieves all products available in the store.
     */
    public static final String GET_ALL_PRODUCTS = "/products";
    
    /**
     * Retrieves a single product specified by its ID.
     * <p>
     * Replace the placeholder {id} with the actual product ID.
     * </p>
     */
    public static final String GET_PRODUCT_BY_ID = "/products/{id}";
    
    /**
     * Retrieves a limited number of products.
     * <p>
     * The placeholder {limit} should be replaced with the desired number of products.
     * </p>
     */
    public static final String GET_PRODUCTS_WITH_LIMIT = "/products?limit={limit}";
    
    /**
     * Retrieves products sorted in the specified order.
     * <p>
     * The {order} parameter accepts values like "asc" for ascending and "desc" for descending order.
     * </p>
     */
    public static final String GET_PRODUCTS_SORTED = "/products?sort={order}";
    
    /**
     * Retrieves all product categories available in the store.
     */
    public static final String GET_ALL_CATEGORIES = "/products/categories";
    
    /**
     * Retrieves products belonging to a specific category.
     * <p>
     * Replace the {category} placeholder with the actual category name.
     * </p>
     */
    public static final String GET_PRODUCTS_BY_CATEGORY = "/products/category/{category}";
    
    /**
     * Creates a new product.
     * <p>
     * The new product details are typically provided in the body of the request.
     * </p>
     */
    public static final String CREATE_PRODUCT = "/products";
    
    /**
     * Updates an existing product.
     * <p>
     * Replace the {id} placeholder with the product ID of the product to be updated.
     * </p>
     */
    public static final String UPDATE_PRODUCT = "/products/{id}";
    
    /**
     * Deletes an existing product.
     * <p>
     * Replace the {id} placeholder with the product ID of the product to be deleted.
     * </p>
     */
    public static final String DELETE_PRODUCT = "/products/{id}";
    
    
    /**
     * Endpoint for user authentication.
     *
     * <p>URL: <code>/auth/login</code></p>
     * <p>Method: <code>POST</code></p>
     * 
     * <h4>Request Body</h4>
     * <pre>
     * {
     *   "username": "user@example.com",
     *   "password": "yourPassword"
     * }
     * </pre>
     *
     * <h4>Response</h4>
     * <pre>
     * {
     *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9…",
     *   "refreshToken": "d1d11f…",
     *   "expiresIn": 3600
     * }
     * </pre>
     *
     * <h4>Possible Error Codes</h4>
     * <ul>
     *   <li><code>400 Bad Request</code> – missing or malformed credentials</li>
     *   <li><code>401 Unauthorized</code> – invalid username/password</li>
     *   <li><code>500 Internal Server Error</code> – server-side failure</li>
     * </ul>
     */
    public static final String AUTH_LOGIN = "/auth/login";
    
    
}



