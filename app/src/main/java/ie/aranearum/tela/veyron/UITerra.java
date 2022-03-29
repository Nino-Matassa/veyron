package ie.aranearum.tela.veyron;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UITerra extends UI implements IRegisterOnStack {

    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;

    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
    public UITerra(Context context) {
        super(context, Constants.UITerra);
        this.context = context;
        formatter = new DecimalFormat("#,###.##");
        registerOnStack();
        UIMessage.eyeCandy(context, "Terra");
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
        uiHistory = new UIHistory(0L, 0L, Constants.UITerra);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateTerra();
        setHeader("Terra", "Overview");
        UIMessage.eyeCandy(context, null);
    }

    private void populateTerra() {
        String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
        File csv = new File(filePath);
        String lastUpdated = new Date(csv.lastModified()).toString();
        String[] aDate = lastUpdated.split("-");
        LocalDate localDate = LocalDate.of(Integer.valueOf(aDate[0]), Integer.valueOf(aDate[1]), Integer.valueOf(aDate[2]));
        lastUpdated = localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        String sqlTerra = "select \n" +
                " (select sum(population) from Detail where date = (select max(date) from Detail)) as sumPopulation\n" +
                " , (select sum(total_cases_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where total_cases_per_million > 0)) as avgTotalCasePerMillion\n" +
                " , (select sum(new_cases_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where new_cases_per_million > 0)) as avgNewCasePerMillion\n" +
                " , (select sum(total_deaths_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where total_deaths_per_million > 0)) as avgTotalDeathPerMillion\n" +
                " , (select sum(new_deaths_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where new_deaths_per_million > 0)) as avgNewDeathPerMillion\n" +
                " , (select sum(reproduction_rate)/(select count(Id) from Detail where reproduction_rate > 0)) as avgReproductionRate\n" +
                " , (select sum(icu_patients_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where icu_patients_per_million > 0)) as avgICUPatientsPerMillion\n" +
                " , (select sum(hosp_patients_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where hosp_patients_per_million > 0)) as avgHospitalPatientsPerMillion\n" +
                " , (select sum(weekly_icu_admissions_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where weekly_icu_admissions_per_million > 0)) as avgWeeklyICUAdmissionsPerMillion\n" +
                " , (select sum(weekly_hosp_admissions_per_million)/(select count(Id) from Country) from Detail where date = (select max(date) from Detail where weekly_hosp_admissions_per_million > 0)) as avgWeeklyHospitalAdmissionsPerMillion\n" +
                " , (select sum(total_tests_per_thousand)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where total_tests_per_thousand > 0)) as avgTotalTestsPerThousand\n" +
                " , (select sum(new_tests_per_thousand)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where new_tests_per_thousand > 0)) as avgNewTestsPerThousand\n" +
                " , (select sum(positive_rate)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where positive_rate > 0)) as avgPositivityRate\n" +
                " , (select sum(tests_per_case)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where tests_per_case > 0)) as avgTestPerCase\n" +
                " , (select sum(total_vaccinations_per_hundred)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where total_vaccinations_per_hundred > 0)) as avgTotaVvaccinationsPerHundred\n" +
                " , (select sum(people_vaccinated_per_hundred)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where people_vaccinated_per_hundred > 0)) as avgPeopleVaccinatedPerHundred\n" +
                " , (select sum(aged_65_older)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where aged_65_older > 0)) as avgAged65Older\n" +
                " , (select sum(aged_70_older)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where aged_70_older > 0)) as avgAged70Older\n" +
                " , (select sum(diabetes_prevalence)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where diabetes_prevalence > 0)) as avgDiabetesPrevalence\n" +
                " , (select sum(female_smokers)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where female_smokers > 0)) as avgFemaleSmokers\n" +
                " , (select sum(male_smokers)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where male_smokers > 0)) as avgMaleSmokers\n" +
                " , (select sum(hospital_beds_per_thousand)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where hospital_beds_per_thousand > 0)) as avgHospitalBedsPerThousand\n" +
                " , (select sum(life_expectancy)/(select count(Id) from Country)  from Detail where date = (select max(date) from Detail where life_expectancy > 0)) as avgLifeExpectancy\n" +
                " , * from Detail\n";

        Cursor cTerra = db.rawQuery(sqlTerra, null);
        cTerra.moveToFirst();


        MetaField metaField = new MetaField(0L, 0L, Constants.UIRegion);
        @SuppressLint("Range") Double Population = cTerra.getDouble(cTerra.getColumnIndex("sumPopulation"));
        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Population));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        @SuppressLint("Range") Double TotalCasePerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgTotalCasePerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "total_cases_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double NewCasePerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgNewCasePerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "new_cases_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double TotalDeathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgTotalDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "total_deaths_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double NewDeathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgNewDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = false;
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "new_deaths_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double ReproductionRate = cTerra.getDouble(cTerra.getColumnIndex("avgReproductionRate"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(ReproductionRate));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "reproduction_rate";
        metaFields.add(metaField);

        Double TotalCase = TotalCasePerMillion*100;
        Double PercentageInfected = TotalCase/Population*100;
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Percentage Infected";
        metaField.value = String.valueOf(formatter.format(PercentageInfected));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.PercentInfectedTerra;
        metaField.fieldXName = "Percentage Infected";
        metaFields.add(metaField);


        @SuppressLint("Range") Double ICUPatientPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgICUPatientsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "icu_patients_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double HospitalPatientPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgHospitalPatientsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "hosp_patients_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double WeeklyICUAdmissionPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgWeeklyICUAdmissionsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaFields.add(metaField);

        @SuppressLint("Range") Double WeeklyHospitalAdmissionPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgWeeklyHospitalAdmissionsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Weekly Hospital" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionPerMillion));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double TotalTestPerThousand = cTerra.getDouble(cTerra.getColumnIndex("avgTotalTestsPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(TotalTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "total_tests_per_thousand";
        metaFields.add(metaField);

        @SuppressLint("Range") Double NewTestPerThousand = cTerra.getDouble(cTerra.getColumnIndex("avgNewTestsPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "new_tests_per_thousand";
        metaFields.add(metaField);

        @SuppressLint("Range") Double PositiveRate = cTerra.getDouble(cTerra.getColumnIndex("avgPositivityRate"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Positive Rate%";
        metaField.value = PositiveRate.toString().substring(0, 4);//String.valueOf(formatter.format(PositiveRate));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "positive_rate";
        metaFields.add(metaField);

        @SuppressLint("Range") Double TestPerCase = cTerra.getDouble(cTerra.getColumnIndex("avgTestPerCase"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Test Per Case";
        metaField.value = String.valueOf(formatter.format(TestPerCase));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double TotalVaccinationPerHundred = cTerra.getDouble(cTerra.getColumnIndex("avgTotaVvaccinationsPerHundred"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Vaccination" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(TotalVaccinationPerHundred));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double PeopleVaccinatedPerHundred = cTerra.getDouble(cTerra.getColumnIndex("avgPeopleVaccinatedPerHundred"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Vaccinated" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(PeopleVaccinatedPerHundred));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double Aged65Older = cTerra.getDouble(cTerra.getColumnIndex("avgAged65Older"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Aged65Older%";
        metaField.value = String.valueOf(formatter.format(Aged65Older));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double Aged70Older = cTerra.getDouble(cTerra.getColumnIndex("avgAged70Older"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Aged70Older%";
        metaField.value = String.valueOf(formatter.format(Aged70Older));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double DiabetesPrevalence = cTerra.getDouble(cTerra.getColumnIndex("avgDiabetesPrevalence"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Diabetes Prevalence%";
        metaField.value = String.valueOf(formatter.format(DiabetesPrevalence));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double FemaleSmoker = cTerra.getDouble(cTerra.getColumnIndex("avgFemaleSmokers"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Female Smoker%";
        metaField.value = String.valueOf(formatter.format(FemaleSmoker));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double MaleSmoker = cTerra.getDouble(cTerra.getColumnIndex("avgMaleSmokers"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Male Smoker%";
        metaField.value = String.valueOf(formatter.format(MaleSmoker));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double HospitalBedsPerThousand = cTerra.getDouble(cTerra.getColumnIndex("avgHospitalBedsPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "HospitalBeds" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(HospitalBedsPerThousand));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        @SuppressLint("Range") Double LifeExpectancy = cTerra.getDouble(cTerra.getColumnIndex("avgLifeExpectancy"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Life Expectancy";
        metaField.value = String.valueOf(formatter.format(LifeExpectancy));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0L, 0L, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }

}
















