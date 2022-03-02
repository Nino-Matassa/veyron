package ie.aranearum.tela.veyron;

import android.content.Context;
import android.icu.text.DecimalFormat;

public class FieldXHistory extends UI implements IRegisterOnStack  {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long RegionId = 0l;
    private Long CountryId = 0l;
    private String Region = null;
    private String Country = null;

    public FieldXHistory(Context context, Long regionId, Long countryId, String region, String country, LambdaXHistory lambdaXHistory, String fieldName) {
        super(context, Constants.UIFieldXHistory);
        this.context = context;
        this.RegionId = regionId;
        this.CountryId = countryId;
        this.Region = region;
        this.Country = country;
        formatter = new DecimalFormat("#,###.##");
        UIMessage.informationBox(context, Country + "->" + fieldName);
        registerOnStack();
        uiHandler(lambdaXHistory, fieldName);
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UIFieldXHistory);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler(LambdaXHistory lambdaXHistory, String fieldName) {
        populateRegion(lambdaXHistory, fieldName);
        setHeader(Region, Country);
        UIMessage.informationBox(context, null);
    }

    private void populateRegion(LambdaXHistory lambdaXHistory, String fieldName) {
        setTableLayout(populateTable(lambdaXHistory.populateHistory(fieldName)));
    }
}
