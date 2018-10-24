package co.techmov.checkout.beans;

/**
 * Created by victor on 08-27-15.
 */
public class OrderStatusBean {
    private int id;
    private String comment;
    private String createAt;

    public OrderStatusBean(int id, String comment, String createAt) {
        this.id = id;
        this.comment = comment;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "OrderStatusBean{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", createAt='" + createAt + '\'' +
                '}';
    }
}
