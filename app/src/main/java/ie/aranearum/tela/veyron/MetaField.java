package ie.aranearum.tela.veyron;

public class MetaField {
    public String key = null;
    public String value = null;
    public boolean underlineKey = false;
    public boolean underlineValue = false;
    public Long regionId = 0L;
    public Long countryId = 0L;
    public String UI = null;

    public MetaField(Long regionId, Long countryId, String UI) {
        this.regionId = regionId;
        this.countryId = countryId;
        this.UI = UI;
    }

    public MetaField() {}
}