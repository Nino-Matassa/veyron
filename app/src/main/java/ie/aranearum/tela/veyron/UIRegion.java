package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class UIRegion extends UI implements IRegisterOnStack {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long regionId = 0L;
    private Long countryId = 0L;
    private String Region = "Terra";

    public UIRegion(Context context) {
        super(context, Constants.UIRegion);
        this.context = context;
        formatter = new DecimalFormat("#,###.##");
        UIMessage.eyeCandy(context, "Regions");
        registerOnStack();
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
        setHeader(Region, "Population");
        UIMessage.eyeCandy(context, null);
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(regionId, countryId, Constants.UIRegion);
        MainActivity.stack.add(uiHistory);
    }

    private void populateRegion() {
        ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
        String sqlRegion = "select Id, continent as Region from Region";
        Cursor cRegion = db.rawQuery(sqlRegion, null);
        cRegion.moveToFirst();
        MetaField metaField = null;
        do {
            metaField = new MetaField(regionId, countryId, Constants.UICountry);
            metaField.key = cRegion.getString(cRegion.getColumnIndex("Region"));
            metaField.underlineKey = true;
            metaField.regionId = cRegion.getLong(cRegion.getColumnIndex("Id"));
            String sqlOverview = "select\n" +
                    " sum(population) as population from Detail\n" +
                    " join Country on Country.Id = Detail.FK_Country\n" +
                    " join Region on Region.Id = Country.FK_Region\n" +
                    " where Region.Id = '#1' and date = (select max(date) from Detail)".replace("#1", String.valueOf(metaField.regionId));
            Cursor cOverview = db.rawQuery(sqlOverview, null);
            cOverview.moveToFirst();
            double population = cOverview.getDouble(cOverview.getColumnIndex("population"));
            metaField.value = String.valueOf(formatter.format(population));
            metaFields.add(metaField);
        } while(cRegion.moveToNext());
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
