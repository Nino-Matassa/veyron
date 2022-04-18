package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class UICountry extends UI implements IRegisterOnStack {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long regionId = 0L;
    private Long countryId = 0L;
    private String region = null;
    Cursor cCountry = null;

    public UICountry(Context context, Long regionId) {
        super(context, Constants.UICountry);
        this.context = context;
        this.regionId = regionId;
        formatter = new DecimalFormat("#,###.##");
        registerOnStack();

        String sqlCountry = "select Region.continent as Region, Country.location as Country, Country.id as CountryId, population as Population from Detail\n" +
                " join Country on Country.Id = Detail.FK_Country\n" +
                " join Region on Region.Id = Country.FK_Region\n" +
                " where Region.Id = '#1' group by Country.Id";
        sqlCountry = sqlCountry.replace("#1", String.valueOf(regionId));
        cCountry = db.rawQuery(sqlCountry, null);
        cCountry.moveToFirst();
        region = cCountry.getString(cCountry.getColumnIndex("Region"));

        UIMessage.eyeCandy(context, region);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uiHandler();
            }
        }, Constants.delayMilliSeconds);
    }

    private void uiHandler() {
        populateRegion();
        setHeader(region, "Population");
        UIMessage.eyeCandy(context, null);
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(regionId, countryId, Constants.UICountry);
        MainActivity.stack.add(uiHistory);
    }


    private void populateRegion() {
        ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
        MetaField metaField = null;
        /*String sqlCountry = "select Region.continent as Region, Country.location as Country, Country.id as CountryId, population as Population from Detail\n" +
                " join Country on Country.Id = Detail.FK_Country\n" +
                " join Region on Region.Id = Country.FK_Region\n" +
                " where Region.Id = '#1' and date = (select max(date) from Detail)";
        sqlCountry = sqlCountry.replace("#1", String.valueOf(regionId));
        Cursor cCountry = db.rawQuery(sqlCountry, null);
        cCountry.moveToFirst();
        region = cCountry.getString(cCountry.getColumnIndex("Region"));*/
        try {
            do {
                metaField = new MetaField(regionId, countryId, Constants.UICountryX);
                String key = cCountry.getString(cCountry.getColumnIndex("Country"));
                Double value = cCountry.getDouble(cCountry.getColumnIndex("Population"));
                countryId = cCountry.getLong(cCountry.getColumnIndex("CountryId"));
                metaField.key = key;
                metaField.value = String.valueOf(formatter.format(Math.round(value)));
                metaField.underlineKey = true;
                metaField.regionId = regionId;
                metaField.countryId = countryId;
                metaFields.add(metaField);
            } while(cCountry.moveToNext());
        } catch (Exception e) {
            Log.d("UICountry", e.toString());
        }
        metaFields.sort(new sortStats());
        setTableLayout(populateTable(metaFields));
    }
    class sortStats implements Comparator<MetaField> {
        @Override
        public int compare(@NonNull MetaField mfA, MetaField mfB) {
            Double dA = Double.parseDouble(mfA.value.replace(",", ""));
            Double dB = Double.parseDouble(mfB.value.replace(",", ""));
            return dB.compareTo(dA);
        }
    }
}