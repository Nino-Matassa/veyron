package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;

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
                "(select reproduction_rate from Detail where reproduction_rate > 0 and FK_Country = '#1' order by date desc limit 1) as rNought\n" +
                ", (select total_cases from Detail where total_cases > 0 and FK_Country = '#1' order by date desc limit 1) as TotalCase\n" +
                ", (select new_cases from Detail where new_cases > 0 and FK_Country = '#1' order by date desc limit 1) as NewCase\n" +
                ", (select total_deaths from Detail where total_deaths > 0 and FK_Country = '#1' order by date desc limit 1) as TotalDeath\n" +
                ", (select new_deaths from Detail where new_deaths > 0 and FK_Country = '#1' order by date desc limit 1) as NewDeath\n" +
                ", (select total_cases_per_million from Detail where total_cases_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as TotalCasePerMillion\n" +
                ", (select total_deaths_per_million from Detail where total_deaths_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as TotalDeathPerMillion\n" +
                ", (select new_cases_per_million from Detail where new_cases_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as NewCasePerMillion\n" +
                ", (select new_deaths_per_million from Detail where new_deaths_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as NewDeathPerMillion\n" +
                ", (select icu_patients from Detail where icu_patients > 0 and FK_Country = '#1' order by date desc limit 1) as ICUPatients\n" +
                ", (select icu_patients_per_million from Detail where icu_patients_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as ICUPatientsPerMillion\n" +
                ", (select hosp_patients from Detail where hosp_patients > 0 and FK_Country = '#1' order by date desc limit 1) as HospitalPatients\n" +
                ", (select hosp_patients_per_million from Detail where hosp_patients_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as HospitalPatientsPerMillion\n" +
                ", (select weekly_icu_admissions from Detail where weekly_icu_admissions > 0 and FK_Country = '#1' order by date desc limit 1) as WeeklyICUAdmissions\n" +
                ", (select weekly_icu_admissions_per_million from Detail where weekly_icu_admissions_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as WeeklyICUAdmissionsPerMillion\n" +
                ", (select weekly_hosp_admissions from Detail where weekly_hosp_admissions > 0 and FK_Country = '#1' order by date desc limit 1) as WeeklyHospitalAdmissions\n" +
                ", (select weekly_hosp_admissions_per_million from Detail where weekly_hosp_admissions_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as WeeklyHospitalAdmissionsPerMillion\n" +
                ", (select positive_rate from Detail where positive_rate > 0 and FK_Country = '#1' order by date desc limit 1) as PositivityRate\n" +
                ", (select tests_per_case from Detail where tests_per_case > 0 and FK_Country = '#1' order by date desc limit 1) as TestPerCase\n" +
                "--, (select tests_units from Detail where tests_units > 0 and FK_Country = '#1' order by date desc limit 1) as tests_units\n" +
                ", (select total_vaccinations from Detail where total_vaccinations > 0 and FK_Country = '#1' order by date desc limit 1) as TotalVaccination\n" +
                ", (select people_vaccinated from Detail where people_vaccinated > 0 and FK_Country = '#1' order by date desc limit 1) as PeopleVaccinated\n" +
                ", (select people_fully_vaccinated from Detail where people_fully_vaccinated > 0 and FK_Country = '#1' order by date desc limit 1) as PeopleFullyVaccinated\n" +
                ", (select total_boosters from Detail where total_boosters > 0 and FK_Country = '#1' order by date desc limit 1) as TotalBooster\n" +
                ", (select new_vaccinations from Detail where new_vaccinations > 0 and FK_Country = '#1' order by date desc limit 1) as NewVaccination\n" +
                ", (select total_vaccinations_per_hundred from Detail where total_vaccinations_per_hundred > 0 and FK_Country = '#1' order by date desc limit 1) as TotalVaccinationPerHundred\n" +
                ", (select people_vaccinated_per_hundred from Detail where people_vaccinated_per_hundred > 0 and FK_Country = '#1' order by date desc limit 1) as PeopleVaccinatedPerHundred\n" +
                ", (select people_fully_vaccinated_per_hundred from Detail where people_fully_vaccinated_per_hundred > 0 and FK_Country = '#1' order by date desc limit 1) as PeopleFullyVaccinatedPerHundred\n" +
                ", (select total_boosters_per_hundred from Detail where total_boosters_per_hundred > 0 and FK_Country = '#1' order by date desc limit 1) as TotalBoostersPerHundred\n" +
                ", (select stringency_index from Detail where stringency_index > 0 and FK_Country = '#1' order by date desc limit 1) as StringencyIndex\n" +
                ", (select excess_mortality_cumulative_absolute from Detail where excess_mortality_cumulative_absolute > 0 and FK_Country = '#1' order by date desc limit 1) as ExcessMortalityCumulativeAbsolute\n" +
                ", (select excess_mortality_cumulative from Detail where excess_mortality_cumulative > 0 and FK_Country = '#1' order by date desc limit 1) as ExcessMortalityCumulative\n" +
                ", (select excess_mortality from Detail where excess_mortality > 0 and FK_Country = '#1' order by date desc limit 1) as ExcessMortality\n" +
                ", (select excess_mortality_cumulative_per_million from Detail where excess_mortality_cumulative_per_million > 0 and FK_Country = '#1' order by date desc limit 1) as ExcessMortalityCumulativePerMillion\n" +
                ", (select new_tests from Detail where new_tests > 0 and FK_Country = '#1' order by date desc limit 1) as NewTest\n" +
                ", (select total_tests from Detail where total_tests > 0 and FK_Country = '#1' order by date desc limit 1) as TotalTest\n" +
                ", (select new_tests_per_thousand from Detail where new_tests_per_thousand > 0 and FK_Country = '#1' order by date desc limit 1) as NewTestPerThousand\n" +
                ", (select total_tests_per_thousand from Detail where total_tests_per_thousand > 0 and FK_Country = '#1' order by date desc limit 1) as TotalTestsPerThousand\n" +
                ", * from Detail\n" +
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

        Double TotalCase = cCountry.getDouble(cCountry.getColumnIndex("TotalCase"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Case";
        metaField.value = String.valueOf(formatter.format(TotalCase));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_cases";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewCase = cCountry.getDouble(cCountry.getColumnIndex("NewCase"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Case";
        metaField.value = String.valueOf(formatter.format(NewCase));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_cases";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalDeath = cCountry.getDouble(cCountry.getColumnIndex("TotalDeath"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Death";
        metaField.value = String.valueOf(formatter.format(TotalDeath));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_deaths";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewDeath = cCountry.getDouble(cCountry.getColumnIndex("NewDeath"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Death";
        metaField.value = String.valueOf(formatter.format(NewDeath));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_deaths";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalCasePerMillion = cCountry.getDouble(cCountry.getColumnIndex("TotalCasePerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_cases_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalDeathPerMillion = cCountry.getDouble(cCountry.getColumnIndex("TotalDeathPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_deaths_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewCasePerMillion = cCountry.getDouble(cCountry.getColumnIndex("NewCasePerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_cases_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewDeathPerMillion = cCountry.getDouble(cCountry.getColumnIndex("NewDeathPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_deaths_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double ReproductionRate = cCountry.getDouble(cCountry.getColumnIndex("rNought"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(ReproductionRate));
        metaField.underlineKey = true;
        metaField.fieldXName = "reproduction_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PercentInfected = TotalCase/Population*100;
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Percent Infected";
        metaField.value = String.valueOf(formatter.format(PercentInfected));
        metaField.underlineKey = true;
        metaField.fieldXName = "Percent Infected";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.PercentInfected;
        metaField.region = Region;
        metaField.country = Country;
        metaField.executeSQL = "select date, total_cases, population from Detail where FK_Country = '#1' and total_cases > 0 order by date desc";
        metaField.executeSQL = metaField.executeSQL.replace("#1", String.valueOf(CountryId));
        metaFields.add(metaField);

        Double ICUPatients = cCountry.getDouble(cCountry.getColumnIndex("ICUPatients"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients";
        metaField.value = String.valueOf(formatter.format(ICUPatients));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double ICUPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("ICUPatientsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "icu_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatients = cCountry.getDouble(cCountry.getColumnIndex("HospitalPatients"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients";
        metaField.value = String.valueOf(formatter.format(HospitalPatients));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double HospitalPatientsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("HospitalPatientsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "hosp_patients_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissions = cCountry.getDouble(cCountry.getColumnIndex("WeeklyICUAdmissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Admission7D";
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissions));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyICUAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("WeeklyICUAdmissionsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "ICU Admission7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissions = cCountry.getDouble(cCountry.getColumnIndex("WeeklyHospitalAdmissions"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Admissions7D";
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissions));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_hosp_admissions";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double WeeklyHospitalAdmissionsPerMillion = cCountry.getDouble(cCountry.getColumnIndex("WeeklyHospitalAdmissionsPerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Admissions7D" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionsPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXName = "weekly_hosp_admissions_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewTests = cCountry.getDouble(cCountry.getColumnIndex("NewTest"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Tests";
        metaField.value = String.valueOf(formatter.format(NewTests));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_tests";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalTests = cCountry.getDouble(cCountry.getColumnIndex("TotalTest"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Tests";
        metaField.value = String.valueOf(formatter.format(TotalTests));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_tests";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalTestPerThousand = cCountry.getDouble(cCountry.getColumnIndex("TotalTestsPerThousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(TotalTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_tests_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewTestPerThousand = cCountry.getDouble(cCountry.getColumnIndex("NewTestPerThousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_tests_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PositivityRate = cCountry.getDouble(cCountry.getColumnIndex("PositivityRate"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Positivity Rate";
        metaField.value = String.valueOf(formatter.format(PositivityRate));
        metaField.underlineKey = true;
        metaField.fieldXName = "positive_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TestPerCase = cCountry.getDouble(cCountry.getColumnIndex("TestPerCase"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Test/Case";
        metaField.value = String.valueOf(formatter.format(TestPerCase));
        metaField.underlineKey = true;
        metaField.fieldXName = "tests_per_case";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        /*Double UnitTests = cCountry.getDouble(cCountry.getColumnIndex("tests_units"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Unit Test";
        metaField.value = String.valueOf(formatter.format(UnitTests));
        metaField.underlineKey = true;
        metaField.fieldXName = "tests_units";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.regionId = RegionId;
        metaField.countryId = CountryId;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);*/

        Double TotalVaccinations = cCountry.getDouble(cCountry.getColumnIndex("TotalVaccination"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Vaccinations";
        metaField.value = String.valueOf(formatter.format(TotalVaccinations));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_vaccinations";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PeopleVaccinated = cCountry.getDouble(cCountry.getColumnIndex("PeopleVaccinated"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleVaccinated));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_vaccinated";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double PeopleFullyVaccinated = cCountry.getDouble(cCountry.getColumnIndex("PeopleFullyVaccinated"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Fully Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleFullyVaccinated));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_fully_vaccinated";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double TotalBoosters = cCountry.getDouble(cCountry.getColumnIndex("TotalBooster"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Boosters";
        metaField.value = String.valueOf(formatter.format(TotalBoosters));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_boosters";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double NewVaccinations = cCountry.getDouble(cCountry.getColumnIndex("NewVaccination"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "New Vaccinations" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewVaccinations));
        metaField.underlineKey = true;
        metaField.fieldXName = "new_vaccinations";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double total_vaccinations_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("TotalVaccinationPerHundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Vaccinations" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(total_vaccinations_per_hundred));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_vaccinations_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double people_vaccinated_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("PeopleVaccinatedPerHundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "People Vaccinated" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(people_vaccinated_per_hundred));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_vaccinated_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double people_fully_vaccinated_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("PeopleFullyVaccinatedPerHundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Fully Vaccinated" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(people_fully_vaccinated_per_hundred));
        metaField.underlineKey = true;
        metaField.fieldXName = "people_fully_vaccinated_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double total_boosters_per_hundred = cCountry.getDouble(cCountry.getColumnIndex("TotalBoostersPerHundred"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Total Boosters" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(total_boosters_per_hundred));
        metaField.underlineKey = true;
        metaField.fieldXName = "total_boosters_per_hundred";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double stringency_index = cCountry.getDouble(cCountry.getColumnIndex("StringencyIndex"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Stringency Index";
        metaField.value = String.valueOf(formatter.format(stringency_index));
        metaField.underlineKey = true;
        metaField.fieldXName = "stringency_index";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double population_density = cCountry.getDouble(cCountry.getColumnIndex("population_density"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Population Density";
        metaField.value = String.valueOf(formatter.format(population_density));
        metaField.underlineKey = false;
        metaField.fieldXName = "population_density";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double median_age = cCountry.getDouble(cCountry.getColumnIndex("median_age"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Median Age";
        metaField.value = String.valueOf(formatter.format(median_age));
        metaField.underlineKey = false;
        metaField.fieldXName = "median_age";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double aged_65_older = cCountry.getDouble(cCountry.getColumnIndex("aged_65_older"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Age 65+";
        metaField.value = String.valueOf(formatter.format(aged_65_older));
        metaField.underlineKey = false;
        metaField.fieldXName = "aged_65_older";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double aged_70_older = cCountry.getDouble(cCountry.getColumnIndex("aged_70_older"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Age 70+";
        metaField.value = String.valueOf(formatter.format(aged_70_older));
        metaField.underlineKey = false;
        metaField.fieldXName = "aged_70_older";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double gdp_per_capita = cCountry.getDouble(cCountry.getColumnIndex("gdp_per_capita"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "GDP/Capita";
        metaField.value = String.valueOf(formatter.format(gdp_per_capita));
        metaField.underlineKey = false;
        metaField.fieldXName = "gdp_per_capita";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double extreme_poverty = cCountry.getDouble(cCountry.getColumnIndex("extreme_poverty"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Extreme Poverty";
        metaField.value = String.valueOf(formatter.format(extreme_poverty));
        metaField.underlineKey = false;
        metaField.fieldXName = "extreme_poverty";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double cardiovasc_death_rate = cCountry.getDouble(cCountry.getColumnIndex("cardiovasc_death_rate"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Cardiovascular Death Rate";
        metaField.value = String.valueOf(formatter.format(cardiovasc_death_rate));
        metaField.underlineKey = false;
        metaField.fieldXName = "cardiovasc_death_rate";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double diabetes_prevalence = cCountry.getDouble(cCountry.getColumnIndex("diabetes_prevalence"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Diabetes Prevalence";
        metaField.value = String.valueOf(formatter.format(diabetes_prevalence));
        metaField.underlineKey = false;
        metaField.fieldXName = "diabetes_prevalence";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double female_smokers = cCountry.getDouble(cCountry.getColumnIndex("female_smokers"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Female Smokers";
        metaField.value = String.valueOf(formatter.format(female_smokers));
        metaField.underlineKey = false;
        metaField.fieldXName = "female_smokers";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double male_smokers = cCountry.getDouble(cCountry.getColumnIndex("male_smokers"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Male Smokers";
        metaField.value = String.valueOf(formatter.format(male_smokers));
        metaField.underlineKey = false;
        metaField.fieldXName = "male_smokers";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double handwashing_facilities = cCountry.getDouble(cCountry.getColumnIndex("handwashing_facilities"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Handwashing Facilities";
        metaField.value = String.valueOf(formatter.format(handwashing_facilities));
        metaField.underlineKey = false;
        metaField.fieldXName = "handwashing_facilities";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double hospital_beds_per_thousand = cCountry.getDouble(cCountry.getColumnIndex("hospital_beds_per_thousand"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Hospital Bed" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(hospital_beds_per_thousand));
        metaField.underlineKey = false;
        metaField.fieldXName = "hospital_beds_per_thousand";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double life_expectancy = cCountry.getDouble(cCountry.getColumnIndex("life_expectancy"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Life Expectancy";
        metaField.value = String.valueOf(formatter.format(life_expectancy));
        metaField.underlineKey = false;
        metaField.fieldXName = "life_expectancy";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double human_development_index = cCountry.getDouble(cCountry.getColumnIndex("human_development_index"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Human Development Index";
        metaField.value = String.valueOf(formatter.format(human_development_index));
        metaField.underlineKey = false;
        metaField.fieldXName = "human_development_index";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative_absolute = cCountry.getDouble(cCountry.getColumnIndex("ExcessMortalityCumulativeAbsolute"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative Absolute";
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative_absolute));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative_absolute";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative = cCountry.getDouble(cCountry.getColumnIndex("ExcessMortalityCumulative"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative";
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality = cCountry.getDouble(cCountry.getColumnIndex("ExcessMortality"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality";
        metaField.value = String.valueOf(formatter.format(excess_mortality));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
        metaField.region = Region;
        metaField.country = Country;
        metaFields.add(metaField);

        Double excess_mortality_cumulative_per_million = cCountry.getDouble(cCountry.getColumnIndex("ExcessMortalityCumulativePerMillion"));
        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(excess_mortality_cumulative_per_million));
        metaField.underlineKey = true;
        metaField.fieldXName = "excess_mortality_cumulative_per_million";
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.DateAndField;
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


