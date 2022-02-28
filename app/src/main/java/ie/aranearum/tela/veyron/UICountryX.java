package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;

public class UICountryX extends UI implements IRegisterOnStack {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long regionId = 0l;
    private Long countryId = 0l;
    private String Region = null;
    private String Country = null;
    private String lastUpdated = null;

    public UICountryX(Context context, Long countryId) {
        super(context, Constants.UICountryX);
        this.context = context;
        this.countryId = countryId;
        formatter = new DecimalFormat("#,###.##");
        UIMessage.informationBox(context, Country + " " + lastUpdated);
        registerOnStack();
        uiHandler();
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(regionId, countryId, Constants.UIRegion);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateRegion();
        setHeader(Region, Country);
        UIMessage.informationBox(context, null);
    }

    private void populateRegion() {
        ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
        MetaField metaField = null;
        String sqlCountry = "select \n" +
                " Country.Id as CountryId,\n" +
                " Region.Id as RegionId,\n" +
                " Country.location as Country,\n" +
                " Region.continent as Region,\n" +
                " * from Detail\n" +
                " join Country on Country.id = Detail.FK_Country\n" +
                " join Region on Region.Id = Country.FK_Region\n" +
                " where Country.Id = '#1' order by date desc limit 1";
        sqlCountry = sqlCountry.replace("#1", String.valueOf(countryId));
        Cursor cCountry = db.rawQuery(sqlCountry, null);
        cCountry.moveToFirst();
        Region = cCountry.getString(cCountry.getColumnIndex("Region"));
        Country = cCountry.getString(cCountry.getColumnIndex("Country"));

        Double Population = cCountry.getDouble(cCountry.getColumnIndex("population"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Population));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        Double TotalCase = cCountry.getDouble(cCountry.getColumnIndex("total_cases"));
        Double TotalDeath = cCountry.getDouble(cCountry.getColumnIndex("total_deaths"));

        metaField = new MetaField(0L, 0L, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }
 }
