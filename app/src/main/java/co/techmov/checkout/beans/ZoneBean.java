package co.techmov.checkout.beans;

/**
 * Created by victor on 08-28-15.
 */
public class ZoneBean {
    private int id;
    private String zone;
    private int municipalityId;

    public ZoneBean(int id, String zone, int municipalityId) {
        this.id = id;
        this.zone = zone;
        this.municipalityId = municipalityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
    }
}
