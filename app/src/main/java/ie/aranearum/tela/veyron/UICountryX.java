package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;
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
    Cursor cCountry = null;

    //ILambdaXHistory ILambdaXHistory;

    public UICountryX(Context context, Long countryId) {
        super(context, Constants.UICountryX);
        this.context = context;
        this.CountryId = countryId;
        formatter = new DecimalFormat("#,###.##");

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
        cCountry = db.rawQuery(sqlCountry, null);
        cCountry.moveToFirst();
        Region = cCountry.getString(cCountry.getColumnIndex("Region"));
        RegionId = cCountry.getLong(cCountry.getColumnIndex("RegionId"));
        Country = cCountry.getString(cCountry.getColumnIndex("Country"));
        CountryId = cCountry.getLong(cCountry.getColumnIndex("CountryId"));

        UIMessage.eyeCandy(context, Country);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                uiHandler();
            }
        }, Constants.delayMilliSeconds);
        registerOnStack();
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(RegionId, CountryId, Constants.UICountryX);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateRegion();
        setHeader(Region, Country);
        UIMessage.eyeCandy(context, null);
    }

    private void populateRegion() {
        ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
        MetaField metaField = null;

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

        Double ICUPatients = cCountry.getDouble(cCountry.getColumnIndex("icu_patients"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients";
        metaField.value = String.valueOf(formatter.format(ICUPatients));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double ICUPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("icu_patients_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatients = cCountry.getDouble(cCountry.getColumnIndex("hosp_patients"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients";
        metaField.value = String.valueOf(formatter.format(ICUPatients));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("hosp_patients_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissions = cCountry.getDouble(cCountry.getColumnIndex("weekly_icu_admissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Admission7D";
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissions));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("weekly_icu_admissions_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Admission7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissions = cCountry.getDouble(cCountry.getColumnIndex("weekly_hosp_admissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Admissions7D";
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissions));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_hosp_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("weekly_hosp_admissions_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Admissions7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_hosp_admissions_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewTests = cCountry.getDouble(cCountry.getColumnIndex("new_tests"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Tests";
        metaField.value = String.valueOf(formatter.format(NewTests));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_tests";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalTests = cCountry.getDouble(cCountry.getColumnIndex("total_tests"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Tests";
        metaField.value = String.valueOf(formatter.format(TotalTests));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_tests";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalTestPerThousand = cCountry.getDouble(cCountry.getColumnIndex("total_tests_per_thousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(TotalTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_tests_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewTestPerThousand = cCountry.getDouble(cCountry.getColumnIndex("new_tests_per_thousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_tests_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PositivityRate = cCountry.getDouble(cCountry.getColumnIndex("positive_rate"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Positivity Rate";
        metaField.value = String.valueOf(formatter.format(PositivityRate));
        metaField.underlineKey = true;
        metaField.fieldXName = "positive_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        /*Double X = cCountry.getDouble(cCountry.getColumnIndex("fieldX"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "DisplayName" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(X));
        metaField.underlineKey = true;
        metaField.fieldXName = "fieldX";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);*/

        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }
}


