package co.techmov.checkout.beans;

/**
 * Created by victor on 08-27-15.
 */
public class RestaurantBean {
    private String name;
    private Float commisionPercentage;
    private String address;
    private String phone;
    private String coordinates;

    public RestaurantBean() {
    }

    /**
     *
     * @param name
     * @param commisionPercentage
     * @param address
     * @param phone
     * @param coordinates
     */
    public RestaurantBean(String name, Float commisionPercentage, String address, String phone, String coordinates) {
        this.name = name;
        this.commisionPercentage = commisionPercentage;
        this.address = address;
        this.phone = phone;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getCommisionPercentage() {
        return commisionPercentage;
    }

    public void setCommisionPercentage(Float commisionPercentage) {
        this.commisionPercentage = commisionPercentage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "RestaurantBean{" +
                "name='" + name + '\'' +
                ", commisionPercentage=" + commisionPercentage +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", coordinates='" + coordinates + '\'' +
                '}';
    }
}
