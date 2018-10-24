package co.techmov.checkout.beans;

/**
 * Created by victor on 08-28-15.
 */
public class AddressDetailBean {
    private int userId;
    private int zoneId;
    private ZoneBean zoneBean;
    private String reference;
    private String coordinates;

    /**
     *
     * @param userId
     * @param zoneId
     * @param zoneBean
     * @param reference
     */
    public AddressDetailBean(int userId, int zoneId, ZoneBean zoneBean, String reference, String coordinates) {
        this.userId = userId;
        this.zoneId = zoneId;
        this.zoneBean = zoneBean;
        this.reference = reference;
        this.coordinates = coordinates;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public ZoneBean getZoneBean() {
        return zoneBean;
    }

    public void setZoneBean(ZoneBean zoneBean) {
        this.zoneBean = zoneBean;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }
}
