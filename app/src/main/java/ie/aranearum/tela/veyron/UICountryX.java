package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UICountryX extends UI implements IRegisterOnStack {
    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;
    private Long RegionId = 0l;
    private Long CountryId = 0l;
    private String Region = null;
    private String Country = null;
    private String lastUpdated = null;

    ILambdaXHistory ILambdaXHistory;

    public UICountryX(Context context, Long countryId) {
        super(context, Constants.UICountryX);
        this.context = context;
        this.CountryId = countryId;
        formatter = new DecimalFormat("#,###.##");
        UIMessage.informationBox(context, Country + " " + lastUpdated);
        registerOnStack();
        uiHandler();
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UICountryX);
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
        sqlCountry = sqlCountry.replace("#1", String.valueOf(CountryId));
        Cursor cCountry = db.rawQuery(sqlCountry, null);
        cCountry.moveToFirst();
        Region = cCountry.getString(cCountry.getColumnIndex("Region"));
        RegionId = cCountry.getLong(cCountry.getColumnIndex("RegionId"));
        Country = cCountry.getString(cCountry.getColumnIndex("Country"));
        CountryId = cCountry.getLong(cCountry.getColumnIndex("CountryId"));

        String Date = cCountry.getString(cCountry.getColumnIndex("date"));
        LocalDate localDate = LocalDate.parse(Date);
        Date = localDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Date";
        metaField.value = Date;
        metaField.underlineKey = false;
        metaFields.add(metaField);

        Double Population = cCountry.getDouble(cCountry.getColumnIndex("population"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Population));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        Double TotalCase = cCountry.getDouble(cCountry.getColumnIndex("total_cases"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Case";
        metaField.value = String.valueOf(formatter.format(TotalCase));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_cases";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewCase = cCountry.getDouble(cCountry.getColumnIndex("new_cases"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Case";
        metaField.value = String.valueOf(formatter.format(NewCase));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_cases";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalDeath = cCountry.getDouble(cCountry.getColumnIndex("total_deaths"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Death";
        metaField.value = String.valueOf(formatter.format(TotalDeath));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_deaths";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewDeath = cCountry.getDouble(cCountry.getColumnIndex("new_deaths"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Death";
        metaField.value = String.valueOf(formatter.format(NewDeath));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_deaths";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalCasePerMillion = cCountry.getDouble(cCountry.getColumnIndex("total_cases_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_cases_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalDeathPerMillion = cCountry.getDouble(cCountry.getColumnIndex("total_deaths_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_deaths_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewCasePerMillion = cCountry.getDouble(cCountry.getColumnIndex("new_cases_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_cases_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewDeathPerMillion = cCountry.getDouble(cCountry.getColumnIndex("new_deaths_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_deaths_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        String sqlRNought = "select \n" +
                " Country.Id as CountryId,\n" +
                " Region.Id as RegionId,\n" +
                " Country.location as Country,\n" +
                " Region.continent as Region,\n" +
                " * from Detail\n" +
                " join Country on Country.id = Detail.FK_Country\n" +
                " join Region on Region.Id = Country.FK_Region\n" +
                " where Country.Id = '#1' and reproduction_rate > 0 order by date desc limit 1";
        sqlRNought = sqlRNought.replace("#1", String.valueOf(CountryId));
        Cursor cReproductionRate = db.rawQuery(sqlRNought, null);
        Double ReproductionRate = 0d;

        try {
            cReproductionRate.moveToFirst();
            ReproductionRate = cReproductionRate.getDouble(cReproductionRate.getColumnIndex("reproduction_rate"));
        } catch (Exception e) {
            Log.d("UICountryX", e.toString());
        }


        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(ReproductionRate));
        metaField.underlineKey = true;
        metaField.fieldXName = "reproduction_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }
}


