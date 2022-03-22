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
                "(select count(Id) from Detail where FK_Country = '#1') as countId,\n" +
                " sum(icu_patients) as sumICUPatient," +
                " sum(icu_patients_per_million) as sumICUPatientPerMillion," +
                " sum(hosp_patients) as sumHospitalPatient," +
                " sum(hosp_patients_per_million) as sumHospitalPatientPerMillion," +
                " sum(weekly_icu_admissions) as sumWeeklyICUAdmissions," +
                " sum(weekly_icu_admissions_per_million) as sumWeeklyICUAdmissionsPerMillion," +
                " sum(weekly_hosp_admissions) as sumWeeklyHospitalAdmissions," +
                " sum(weekly_hosp_admissions_per_million) as sumWeeklyHospitalAdmissionsPerMillion," +
                " sum(positive_rate) as sumPositivityRate," +
                " sum(tests_per_case) as sumTestPerCase," +
                " sum(tests_units) as sumUnitTests," +
                " sum(total_vaccinations) as sumTotalVaccinations," +
                " sum(people_vaccinated) as sumPeopleVaccinated," +
                " sum(people_fully_vaccinated) as sumPeopleFullyVaccinated," +
                " sum(total_boosters) as sumTotalBoosters," +
                " sum(new_vaccinations) as sumNewVaccinations," +
                " sum(total_vaccinations_per_hundred) as sumtotal_vaccinations_per_hundred," +
                " sum(people_vaccinated_per_hundred) as sumpeople_vaccinated_per_hundred," +
                " sum(people_fully_vaccinated_per_hundred) as sumpeople_fully_vaccinated_per_hundred," +
                " sum(total_boosters_per_hundred) as sumtotal_boosters_per_hundred," +
                " sum(stringency_index) as sumstringency_index," +
                " sum(excess_mortality_cumulative_absolute) as sumexcess_mortality_cumulative_absolute," +
                " sum(excess_mortality_cumulative) as sumexcess_mortality_cumulative," +
                " sum(excess_mortality) as sumexcess_mortality," +
                " sum(excess_mortality_cumulative_per_million) as sumexcess_mortality_cumulative_per_million," +
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

        //Double ICUPatients = cCountry.getDouble(cCountry.getColumnIndex("icu_patients"));
        Long CountId = cCountry.getLong(cCountry.getColumnIndex("countId"));
        Double ICUPatients = cCountry.getDouble(cCountry.getColumnIndex("sumICUPatient"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*ICU Patients";
        metaField.value = String.valueOf(formatter.format(ICUPatients/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double ICUPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("sumICUPatientPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientsPerMillion/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatients = cCountry.getDouble(cCountry.getColumnIndex("sumHospitalPatient"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Hospital Patients";
        metaField.value = String.valueOf(formatter.format(HospitalPatients/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("sumHospitalPatientPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientsPerMillion/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissions = cCountry.getDouble(cCountry.getColumnIndex("sumWeeklyICUAdmissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*ICU Admission7D";
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissions/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("sumWeeklyICUAdmissionsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*ICU Admission7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionsPerMillion/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissions = cCountry.getDouble(cCountry.getColumnIndex("sumWeeklyHospitalAdmissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Hospital Admissions7D";
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissions/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_hosp_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("sumWeeklyHospitalAdmissionsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Hospital Admissions7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionsPerMillion/CountId));
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

        Double PositivityRate = cCountry.getDouble(cCountry.getColumnIndex("sumPositivityRate"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Positivity Rate";
        metaField.value = String.valueOf(formatter.format(PositivityRate/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "positive_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TestPerCase = cCountry.getDouble(cCountry.getColumnIndex("sumTestPerCase"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Test/Case";
        metaField.value = String.valueOf(formatter.format(TestPerCase/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "tests_per_case";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double UnitTests = cCountry.getDouble(cCountry.getColumnIndex("sumUnitTests"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Unit Test";
        metaField.value = String.valueOf(formatter.format(UnitTests/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "tests_units";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalVaccinations = cCountry.getDouble(cCountry.getColumnIndex("sumTotalVaccinations"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Total Vaccinations";
        metaField.value = String.valueOf(formatter.format(TotalVaccinations/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_vaccinations";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PeopleVaccinated = cCountry.getDouble(cCountry.getColumnIndex("sumPeopleVaccinated"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleVaccinated/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_vaccinated";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PeopleFullyVaccinated = cCountry.getDouble(cCountry.getColumnIndex("sumPeopleFullyVaccinated"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Fully Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleFullyVaccinated/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_fully_vaccinated";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalBoosters = cCountry.getDouble(cCountry.getColumnIndex("sumTotalBoosters"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Total Boosters";
        metaField.value = String.valueOf(formatter.format(TotalBoosters/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_boosters";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewVaccinations = cCountry.getDouble(cCountry.getColumnIndex("sumNewVaccinations"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*New Vaccinations" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewVaccinations/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_vaccinations";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double total_vaccinations_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("sumtotal_vaccinations_per_hundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Total Vaccinations" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(total_vaccinations_per_hundred/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_vaccinations_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double people_vaccinated_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("sumpeople_vaccinated_per_hundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*People Vaccinated" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(people_vaccinated_per_hundred/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_vaccinated_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double people_fully_vaccinated_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("sumpeople_fully_vaccinated_per_hundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Fully Vaccinated" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(people_fully_vaccinated_per_hundred/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_fully_vaccinated_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double total_boosters_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("sumtotal_boosters_per_hundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Total Boosters" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(total_boosters_per_hundred/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_boosters_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double stringency_index = cCountry.getDouble(cCountry.getColumnIndex("sumstringency_index"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Stringency Index";
        metaField.value = String.valueOf(formatter.format(stringency_index/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "stringency_index";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double population_density = cCountry.getDouble(cCountry.getColumnIndex("population_density"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Population Density";
        metaField.value = String.valueOf(formatter.format(population_density));
        metaField.underlineKey = false;
        metaField.fieldXName = "population_density";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double median_age = cCountry.getDouble(cCountry.getColumnIndex("median_age"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Median Age";
        metaField.value = String.valueOf(formatter.format(median_age));
        metaField.underlineKey = false;
        metaField.fieldXName = "median_age";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double aged_65_older = cCountry.getDouble(cCountry.getColumnIndex("aged_65_older"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Age 65+";
        metaField.value = String.valueOf(formatter.format(aged_65_older));
        metaField.underlineKey = false;
        metaField.fieldXName = "aged_65_older";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double aged_70_older = cCountry.getDouble(cCountry.getColumnIndex("aged_70_older"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Age 70+";
        metaField.value = String.valueOf(formatter.format(aged_70_older));
        metaField.underlineKey = false;
        metaField.fieldXName = "aged_70_older";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double gdp_per_capita = cCountry.getDouble(cCountry.getColumnIndex("gdp_per_capita"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "GDP/Capita";
        metaField.value = String.valueOf(formatter.format(gdp_per_capita));
        metaField.underlineKey = false;
        metaField.fieldXName = "gdp_per_capita";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double extreme_poverty = cCountry.getDouble(cCountry.getColumnIndex("extreme_poverty"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Extreme Poverty";
        metaField.value = String.valueOf(formatter.format(extreme_poverty));
        metaField.underlineKey = false;
        metaField.fieldXName = "extreme_poverty";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double cardiovasc_death_rate = cCountry.getDouble(cCountry.getColumnIndex("cardiovasc_death_rate"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Cardiovascular Death Rate";
        metaField.value = String.valueOf(formatter.format(cardiovasc_death_rate));
        metaField.underlineKey = false;
        metaField.fieldXName = "cardiovasc_death_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double diabetes_prevalence = cCountry.getDouble(cCountry.getColumnIndex("diabetes_prevalence"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Diabetes Prevalence";
        metaField.value = String.valueOf(formatter.format(diabetes_prevalence));
        metaField.underlineKey = false;
        metaField.fieldXName = "diabetes_prevalence";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double female_smokers = cCountry.getDouble(cCountry.getColumnIndex("female_smokers"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Female Smokers";
        metaField.value = String.valueOf(formatter.format(female_smokers));
        metaField.underlineKey = false;
        metaField.fieldXName = "female_smokers";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double male_smokers = cCountry.getDouble(cCountry.getColumnIndex("male_smokers"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Male Smokers";
        metaField.value = String.valueOf(formatter.format(male_smokers));
        metaField.underlineKey = false;
        metaField.fieldXName = "male_smokers";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double handwashing_facilities = cCountry.getDouble(cCountry.getColumnIndex("handwashing_facilities"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Handwashing Facilities";
        metaField.value = String.valueOf(formatter.format(handwashing_facilities));
        metaField.underlineKey = false;
        metaField.fieldXName = "handwashing_facilities";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double hospital_beds_per_thousand = cCountry.getDouble(cCountry.getColumnIndex("hospital_beds_per_thousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Bed" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(hospital_beds_per_thousand));
        metaField.underlineKey = false;
        metaField.fieldXName = "hospital_beds_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double life_expectancy = cCountry.getDouble(cCountry.getColumnIndex("life_expectancy"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Life Expectancy";
        metaField.value = String.valueOf(formatter.format(life_expectancy));
        metaField.underlineKey = false;
        metaField.fieldXName = "life_expectancy";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double human_development_index = cCountry.getDouble(cCountry.getColumnIndex("human_development_index"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Human Development Index";
        metaField.value = String.valueOf(formatter.format(human_development_index));
        metaField.underlineKey = false;
        metaField.fieldXName = "human_development_index";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative_absolute = cCountry.getDouble(cCountry.getColumnIndex("sumexcess_mortality_cumulative_absolute"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Excess Mortality Cumulative Absolute";
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative_absolute/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative_absolute";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative = cCountry.getDouble(cCountry.getColumnIndex("sumexcess_mortality_cumulative"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Excess Mortality Cumulative";
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality = cCountry.getDouble(cCountry.getColumnIndex("sumexcess_mortality"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Excess Mortality";
        metaField.value = String.valueOf(formatter.format(excess_mortality/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.Simple;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative_per_million = cCountry.getDouble(cCountry.getColumnIndex("sumexcess_mortality_cumulative_per_million"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "*Excess Mortality Cumulative" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative_per_million/CountId));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative_per_million";
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


