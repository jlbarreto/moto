package co.techmov.checkout.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 08-27-15.
 */
public class OrderBean {
    private int id;
    private int restaurantId;
    private int addressId;
    private String code;
    private String address;
    private Float orderTotal;
    private Float shippingCharge;
    private Float payChange;
    private String createAt; // in dd/mm/yyyy hh:mmpm
    private String updateAt; // in yyyy-mm-dd hh;mm:ss
    private AddressDetailBean addressDetailBean;
    private UserBean user;
    private RestaurantBean restaurantBean;
    private OrderStatusBean orderStatusBean;
    private Float total;
    private Float payToRestaurant;
    private String payment_method_id;
    private String customer_phone;
    private List<ProductBean> productBeans;

    public OrderBean() {
    }



    /**
     *
     * @param id
     * @param restaurantId
     * @param addressId
     * @param code
     * @param address
     * @param orderTotal
     * @param shippingCharge
     * @param payChange
     * @param createAt
     * @param updateAt
     * @param total
     * @param payToRestaurant
     * @param payment_method_id
     * @param customer_phone
     */
    public OrderBean(int id, int restaurantId, int addressId, String code, String address,
                     Float orderTotal, Float shippingCharge, Float payChange, String createAt,
                     String updateAt, Float total, Float payToRestaurant, String payment_method_id , String customer_phone) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.addressId = addressId;
        this.code = code;
        this.address = address;
        this.orderTotal = orderTotal;
        this.shippingCharge = shippingCharge;
        this.payChange = payChange;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.total = total;
        this.payToRestaurant = payToRestaurant;
        this.payment_method_id = payment_method_id;// agregando el metodo de pago del cliente.
        this.customer_phone = customer_phone;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Float orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Float getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(Float shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public Float getPayChange() {
        return payChange;
    }

    public void setPayChange(Float payChange) {
        this.payChange = payChange;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public RestaurantBean getRestaurantBean() {
        return restaurantBean;
    }

    public void setRestaurantBean(RestaurantBean restaurantBean) {
        this.restaurantBean = restaurantBean;
    }

    public OrderStatusBean getOrderStatusBean() {
        return orderStatusBean;
    }

    public void setOrderStatusBean(OrderStatusBean orderStatusBean) {
        this.orderStatusBean = orderStatusBean;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getPayToRestaurant() {
        return payToRestaurant;
    }

    public void setPayToRestaurant(Float payToRestaurant) {
        this.payToRestaurant = payToRestaurant;
    }

    public List<ProductBean> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(List<ProductBean> productBeans) {
        this.productBeans = productBeans;
    }

    public boolean add(ProductBean object) {
        if(productBeans == null)
            productBeans = new ArrayList<>();
        return productBeans.add(object);
    }

    public AddressDetailBean getAddressDetailBean() {
        return addressDetailBean;
    }

    public void setAddressDetailBean(AddressDetailBean addressDetailBean) {
        this.addressDetailBean = addressDetailBean;
    }

    public String getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(String payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id=" + id +
                ", restaurantId=" + restaurantId +
                ", addressId=" + addressId +
                ", payment_method_id=" + payment_method_id +
                ", total=" + total +
                ", code='" + code + '\'' +
                ", address='" + address + '\'' +
                ", orderTotal=" + orderTotal +
                ", payChange=" + payChange +
                ", shippingCharge=" + shippingCharge +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
