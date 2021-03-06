package ie.aranearum.tela.veyron;

import android.app.Activity;
import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;

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
        registerOnStack();
        String title =  "Veyron - ";
        ((Activity)context).setTitle(title + fieldName);
        UIMessage.eyeCandy(context, fieldName);
        UI.setMenubarTitleToUpdating(context, false);
        if(RegionId == 0 && CountryId == 0) { // Then the call came from UITerra
            // Add dummy values for Region & Country for the header
            Region = "Terra";
            Country = "Collation";
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uiHandler();
            }
        }, Constants.delayMilliSeconds);
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UIFieldXHistory);
        uiHistory.region = Region;
        uiHistory.country = Country;
        uiHistory.fieldXName = fieldName;
        uiHistory.ILambdaXHistory = ILambdaXHistory;
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateRegion();
        setHeader(Region, Country);
        UIMessage.eyeCandy(context, null);
    }

    private void populateRegion() {
        setTableLayout(populateTable(ILambdaXHistory.populateHistory()));
    }
}
