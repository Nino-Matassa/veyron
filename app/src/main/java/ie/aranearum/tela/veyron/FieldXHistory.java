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
    private String lastUpdated = null;

    public FieldXHistory(Context context, Long countryId, LambdaXHistory lambdaXHistory) {
        super(context, Constants.UIFieldXHistory);
        this.context = context;
        this.CountryId = countryId;
        formatter = new DecimalFormat("#,###.##");
        UIMessage.informationBox(context, Country + " " + lastUpdated);
        registerOnStack();
        uiHandler(lambdaXHistory);
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UIFieldXHistory);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler(LambdaXHistory lambdaXHistory) {
        populateRegion(lambdaXHistory);
        setHeader(Region, Country);
        UIMessage.informationBox(context, null);
    }

    private void populateRegion(LambdaXHistory lambdaXHistory) {

    }
}
