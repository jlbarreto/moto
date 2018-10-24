package co.techmov.checkout.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by victor on 09-03-15.
 */
public class ShowView extends RealmObject {

    private int orderId;
    private String fragmentClassName;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getFragmentClassName() {
        return fragmentClassName;
    }

    public void setFragmentClassName(String fragmentClassName) {
        this.fragmentClassName = fragmentClassName;
    }
}
