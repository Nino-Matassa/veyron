package ie.aranearum.tela.veyron;

import android.content.Context;
import android.icu.text.DecimalFormat;

public class UIFieldXHistory extends UI implements IRegisterOnStack  {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long RegionId = 0l;
    private Long CountryId = 0l;
    private String Region = null;
    private String Country = null;
    private String fieldName = null;
    private ILambdaXHistory ILambdaXHistory;

    public UIFieldXHistory(Context context, Long regionId, Long countryId, String region, String country,
                           ILambdaXHistory ILambdaXHistory, String fieldName) {
        super(context, Constants.UIFieldXHistory);
        this.context = context;
        this.RegionId = regionId;
        this.CountryId = countryId;
        this.Region = region;
        this.Country = country;
        this.fieldName = fieldName;
        this.ILambdaXHistory = ILambdaXHistory;
        formatter = new DecimalFormat("#,###.##");
        //UIMessage.informationBox(context, Country + "->" + fieldName);
        registerOnStack();
        uiHandler();
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UIFieldXHistory);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateRegion();
        setHeader(Region, Country);
        //UIMessage.informationBox(context, null);
    }

    private void populateRegion() {
        setTableLayout(populateTable(ILambdaXHistory.populateHistory(/*fieldName, RegionId, CountryId, Region, Country*/)));
    }
}
