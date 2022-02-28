package ie.aranearum.tela.veyron;

public class UIHistory {
    private Long regionId = 0L;
    private Long countryId = 0L;
    private String UIX = null;

    public UIHistory(Long regionId, Long countryId, String uiX) {
        this.regionId = regionId;
        this.countryId = countryId;
        this.UIX = uiX;
    }

    public Long getRegionId() {
        return regionId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public String getUIX() {
        return UIX;
    }
}