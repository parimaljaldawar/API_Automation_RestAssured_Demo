package pojo;

/**
 * The {@code Product} class represents a product entity with attributes such as title, price,
 * description, image, and category.
 * <p>
 * This Plain Old Java Object (POJO) is used throughout the application to encapsulate product data,
 * and it can be leveraged for API testing, payload generation, and other business operations.
 * </p>
 */
public class Product {

    // The name or title of the product.
    private String title;
    
    // The price of the product.
    private double price;
    
    // A brief description of the product.
    private String description;
    
    // The URL or identifier for the product's image.
    private String image;
    
    // The category to which the product belongs.
    private String category;
    
    /**
     * Constructs a new {@code Product} instance with the specified attributes.
     * <p>
     * This constructor initializes the product's title, price, description, image URL, and category.
     * </p>
     *
     * @param title       the name or title of the product.
     * @param price       the price of the product.
     * @param description a brief description of the product.
     * @param image       the URL or path to the product's image.
     * @param category    the category of the product.
     */
    public Product(String title, double price, String description, String image, String category) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.image = image;
        this.category = category;
    }
    
    /**
     * Returns the title of the product.
     *
     * @return the product title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the product.
     *
     * @param title the new title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Returns the price of the product.
     *
     * @return the product price.
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Sets the price of the product.
     *
     * @param price the new price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Returns the description of the product.
     *
     * @return the product description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the product.
     *
     * @param description the new description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the image URL or identifier of the product.
     *
     * @return the product image URL.
     */
    public String getImage() {
        return image;
    }
    
    /**
     * Sets the image URL or identifier of the product.
     *
     * @param image the new image URL or identifier to set.
     */
    public void setImage(String image) {
        this.image = image;
    }
    
    /**
     * Returns the category of the product.
     *
     * @return the product category.
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Sets the category of the product.
     *
     * @param category the new category to set.
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
