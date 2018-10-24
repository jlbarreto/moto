package co.techmov.checkout.beans;

/**
 * Created by victor on 08-27-15.
 */
public class ProductBean {
    private String product;
    private int quantity;
    private Float price;
    private Float total;
    private String userComment;

    public ProductBean(String product, int quantity, Float price, Float total, String userComment) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.userComment = userComment;
    }

    public ProductBean() {
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", total=" + total +
                ", userComment='" + userComment + '\'' +
                '}';
    }
}
