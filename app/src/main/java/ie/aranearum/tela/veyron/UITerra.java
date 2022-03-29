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


        /*String sqlNewCase = "select \n" +
                " sum(new_cases)\n" +
                " as NewCase\n" +
                " from Detail where date = (select max(date) from Detail where new_cases > 0)";
        Cursor cNewCase = db.rawQuery(sqlNewCase, null);
        cNewCase.moveToFirst();
        @SuppressLint("Range") Double NewCase = cNewCase.getDouble(cNewCase.getColumnIndex("NewCase"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case";
        metaField.value = String.valueOf(formatter.format(NewCase));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double NewCasePerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgNewCasePerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "new_cases_per_million";
        metaFields.add(metaField);

        /*String sqlNewCaseSmoothed = "select \n" +
                " sum(new_cases_smoothed)\n" +
                " as NewCaseSmoothed\n" +
                " from Detail where date = (select max(date) from Detail where new_cases_smoothed > 0)";
        Cursor cNewCaseSmoothed = db.rawQuery(sqlNewCaseSmoothed, null);
        cNewCaseSmoothed.moveToFirst();
        @SuppressLint("Range") Double NewCaseSmoothed = cNewCaseSmoothed.getDouble(cNewCaseSmoothed.getColumnIndex("NewCaseSmoothed"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.smoothed;
        metaField.value = String.valueOf(formatter.format(NewCaseSmoothed));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewCaseSmoothedPerMillion = "select \n" +
                " sum(new_cases_smoothed_per_million)/(select count(Id) from Country)\n" +
                " as NewCaseSmoothedPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_cases_smoothed_per_million > 0)";
        Cursor cNewCaseSmoothedPerMillion = db.rawQuery(sqlNewCaseSmoothedPerMillion, null);
        cNewCaseSmoothedPerMillion.moveToFirst();
        @SuppressLint("Range") Double NewCaseSmoothedPerMillion = cNewCaseSmoothedPerMillion.getDouble(cNewCaseSmoothedPerMillion.getColumnIndex("NewCaseSmoothedPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.smoothed + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCaseSmoothedPerMillion));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        /*String sqlTotalDeath = "select \n" +
                " sum(total_deaths)\n" +
                " as TotalDeath\n" +
                " from Detail where date = (select max(date) from Detail where total_deaths > 0)";
        Cursor cTotalDeath = db.rawQuery(sqlTotalDeath, null);
        cTotalDeath.moveToFirst();
        @SuppressLint("Range") Double TotalDeath = cTotalDeath.getDouble(cTotalDeath.getColumnIndex("TotalDeath"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Death";
        metaField.value = String.valueOf(formatter.format(TotalDeath));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double TotalDeathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgTotalDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "total_deaths_per_million";
        metaFields.add(metaField);

        /*String sqlNewDeath = "select \n" +
                " sum(new_deaths)\n" +
                " as NewDeath\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths > 0)";
        Cursor cNewDeath = db.rawQuery(sqlNewDeath, null);
        cNewDeath.moveToFirst();
        @SuppressLint("Range") Double NewDeath = cNewDeath.getDouble(cNewDeath.getColumnIndex("NewDeath"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death";
        metaField.value = String.valueOf(formatter.format(NewDeath));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double NewDeathPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgNewDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = false;
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "new_deaths_per_million";
        metaFields.add(metaField);

        /*String sqlNewDeathSmoothed = "select \n" +
                " sum(new_deaths_smoothed)\n" +
                " as NewDeathSmoothed\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths_smoothed > 0)";
        Cursor cNewDeathSmoothed = db.rawQuery(sqlNewDeathSmoothed, null);
        cNewDeathSmoothed.moveToFirst();
        @SuppressLint("Range") Double NewDeathSmoothed = cNewDeathSmoothed.getDouble(cNewDeathSmoothed.getColumnIndex("NewDeathSmoothed"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.smoothed;
        metaField.value = String.valueOf(formatter.format(NewDeathSmoothed));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewDeathSmoothedPerMillion = "select \n" +
                " sum(new_deaths_smoothed_per_million)/(select count(Id) from Country)\n" +
                " as NewDeathSmoothedPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths_smoothed_per_million > 0)";
        Cursor cNewDeathSmoothedPerMillion = db.rawQuery(sqlNewDeathSmoothedPerMillion, null);
        cNewDeathSmoothedPerMillion.moveToFirst();
        @SuppressLint("Range") Double NewDeathSmoothedPerMillion = cNewDeathSmoothedPerMillion.getDouble(cNewDeathSmoothedPerMillion.getColumnIndex("NewDeathSmoothedPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.smoothed + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathSmoothedPerMillion));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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
        metaField.executeSQL = "select continent, Country.location, Country.FK_Region, FK_Country, total_cases, population\n" +
                " from Detail join Country on Detail.FK_Country = Country.Id\n" +
                " where date = (select max(date) from Detail where total_cases > 0)";
        metaFields.add(metaField);


        /*String sqlICUPatient = "select \n" +
                " sum(icu_patients)\n" +
                " as ICUPatient\n" +
                " from Detail where date = (select max(date) from Detail where icu_patients > 0)";
        Cursor cICUPatient = db.rawQuery(sqlICUPatient, null);
        cICUPatient.moveToFirst();
        @SuppressLint("Range") Double ICUPatient = cICUPatient.getDouble(cICUPatient.getColumnIndex("ICUPatient"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients";
        metaField.value = String.valueOf(formatter.format(ICUPatient));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double ICUPatientPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgICUPatientsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "icu_patients_per_million";
        metaFields.add(metaField);

        /*String sqlHospitalPatient = "select \n" +
                " sum(hosp_patients)\n" +
                " as HospitalPatient\n" +
                " from Detail where date = (select max(date) from Detail where hosp_patients > 0)";
        Cursor cHospitalPatient = db.rawQuery(sqlHospitalPatient, null);
        cHospitalPatient.moveToFirst();
        @SuppressLint("Range") Double HospitalPatient = cHospitalPatient.getDouble(cHospitalPatient.getColumnIndex("HospitalPatient"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients";
        metaField.value = String.valueOf(formatter.format(HospitalPatient));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double HospitalPatientPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgHospitalPatientsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "hosp_patients_per_million";
        metaFields.add(metaField);

        /*String sqlWeeklyICUAdmission = "select \n" +
                " sum(weekly_icu_admissions)\n" +
                " as WeeklyICUAdmission\n" +
                " from Detail where date = (select max(date) from Detail where weekly_icu_admissions > 0)";
        Cursor cWeeklyICUAdmission = db.rawQuery(sqlWeeklyICUAdmission, null);
        cWeeklyICUAdmission.moveToFirst();
        @SuppressLint("Range") Double WeeklyICUAdmission = cWeeklyICUAdmission.getDouble(cWeeklyICUAdmission.getColumnIndex("WeeklyICUAdmission"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU";
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmission));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double WeeklyICUAdmissionPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgWeeklyICUAdmissionsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.CountryAndField;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaFields.add(metaField);

        /*String sqlWeeklyHospitalAdmission = "select \n" +
                " sum(weekly_hosp_admissions)\n" +
                " as WeeklyHospitalAdmission\n" +
                " from Detail where date = (select max(date) from Detail where weekly_hosp_admissions > 0)";
        Cursor cWeeklyHospitalAdmission = db.rawQuery(sqlWeeklyHospitalAdmission, null);
        cWeeklyHospitalAdmission.moveToFirst();
        @SuppressLint("Range") Double WeeklyHospitalAdmission = cWeeklyHospitalAdmission.getDouble(cWeeklyHospitalAdmission.getColumnIndex("WeeklyHospitalAdmission"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly Hospital";
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmission));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        @SuppressLint("Range") Double WeeklyHospitalAdmissionPerMillion = cTerra.getDouble(cTerra.getColumnIndex("avgWeeklyHospitalAdmissionsPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Weekly Hospital" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionPerMillion));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        /*String sqlNewTest = "select \n" +
                " sum(new_tests)\n" +
                " as NewTest\n" +
                " from Detail where date = (select max(date) from Detail where new_tests > 0)";
        Cursor cNewTest = db.rawQuery(sqlNewTest, null);
        cNewTest.moveToFirst();
        @SuppressLint("Range") Double NewTest = cNewTest.getDouble(cNewTest.getColumnIndex("NewTest"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Test";
        metaField.value = String.valueOf(formatter.format(NewTest));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalTest = "select \n" +
                " sum(total_tests)--/(select count(Id) from Country)\n" +
                " as TotalTest\n" +
                " from Detail where date = (select max(date) from Detail where total_tests > 0)";
        Cursor cTotalTest = db.rawQuery(sqlTotalTest, null);
        cTotalTest.moveToFirst();
        @SuppressLint("Range") Double TotalTest = cTotalTest.getDouble(cTotalTest.getColumnIndex("TotalTest"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Test";
        metaField.value = String.valueOf(formatter.format(TotalTest));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlNewTestSmoothed = "select \n" +
                " sum(new_tests_smoothed)--/(select count(Id) from Country)\n" +
                " as NewTestSmoothed\n" +
                " from Detail where date = (select max(date) from Detail where new_tests_smoothed > 0)";
        Cursor cNewTestSmoothed = db.rawQuery(sqlNewTestSmoothed, null);
        cNewTestSmoothed.moveToFirst();
        @SuppressLint("Range") Double NewTestSmoothed = cNewTestSmoothed.getDouble(cNewTestSmoothed.getColumnIndex("NewTestSmoothed"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.smoothed;
        metaField.value = String.valueOf(formatter.format(NewTestSmoothed));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewTestSmoothedPerThousand = "select \n" +
                " sum(new_tests_smoothed_per_thousand)/(select count(Id) from Country)\n" +
                " as NewTestSmoothedPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where new_tests_smoothed_per_thousand > 0)";
        Cursor cNewTestSmoothedPerThousand = db.rawQuery(sqlNewTestSmoothedPerThousand, null);
        cNewTestSmoothedPerThousand.moveToFirst();
        @SuppressLint("Range") Double NewTestSmoothedPerThousand = cNewTestSmoothedPerThousand.getDouble(cNewTestSmoothedPerThousand.getColumnIndex("NewTestSmoothedPerThousand"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.smoothed + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestSmoothedPerThousand));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlTotalVaccination = "select\n" +
                " sum(total_vaccinations)\n" +
                " as TotalVaccination\n" +
                " from Detail where date = (select max(date) from Detail where total_vaccinations > 0)";
        Cursor cTotalVaccination = db.rawQuery(sqlTotalVaccination, null);
        cTotalVaccination.moveToFirst();
        @SuppressLint("Range") Double TotalVaccination = cTotalVaccination.getDouble(cTotalVaccination.getColumnIndex("TotalVaccination"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Vaccinations";
        metaField.value = String.valueOf(formatter.format(TotalVaccination));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlPeopleVaccinated = "select\n" +
                " sum(people_vaccinated)\n" +
                " as PeopleVaccinated\n" +
                " from Detail where date = (select max(date) from Detail where people_vaccinated > 0)";
        Cursor cPeopleVaccinated = db.rawQuery(sqlPeopleVaccinated, null);
        cPeopleVaccinated.moveToFirst();
        @SuppressLint("Range") Double PeopleVaccinated = cPeopleVaccinated.getDouble(cPeopleVaccinated.getColumnIndex("PeopleVaccinated"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleVaccinated));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlPeopleFullyVaccinated = "select\n" +
                " sum(people_fully_vaccinated)\n" +
                " as PeopleFullyVaccinated\n" +
                " from Detail where date = (select max(date) from Detail where people_fully_vaccinated > 0)";
        Cursor cPeopleFullyVaccinated = db.rawQuery(sqlPeopleFullyVaccinated, null);
        cPeopleFullyVaccinated.moveToFirst();
        @SuppressLint("Range") Double PeopleFullyVaccinated = cPeopleFullyVaccinated.getDouble(cPeopleFullyVaccinated.getColumnIndex("PeopleFullyVaccinated"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Fully Vaccinated";
        metaField.value = String.valueOf(formatter.format(PeopleFullyVaccinated));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalBooster = "select\n" +
                " sum(total_boosters)\n" +
                " as TotalBooster\n" +
                " from Detail where date = (select max(date) from Detail where total_boosters > 0)";
        Cursor cTotalBooster = db.rawQuery(sqlTotalBooster, null);
        cTotalBooster.moveToFirst();
        @SuppressLint("Range") Double TotalBooster = cTotalBooster.getDouble(cTotalBooster.getColumnIndex("TotalBooster"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Booster";
        metaField.value = String.valueOf(formatter.format(TotalBooster));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewVaccination = "select\n" +
                " sum(new_vaccinations)\n" +
                " as NewVaccination\n" +
                " from Detail where date = (select max(date) from Detail where new_vaccinations > 0)";
        Cursor cNewVaccination = db.rawQuery(sqlNewVaccination, null);
        cNewVaccination.moveToFirst();
        @SuppressLint("Range") Double NewVaccination = cNewVaccination.getDouble(cNewVaccination.getColumnIndex("NewVaccination"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Vaccination";
        metaField.value = String.valueOf(formatter.format(NewVaccination));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        /*String sqlNewVaccinationSmoothed = "select\n" +
                " sum(new_vaccinations_smoothed)\n" +
                " as NewVaccinationSmoothed\n" +
                " from Detail where date = (select max(date) from Detail where new_vaccinations_smoothed > 0)";
        Cursor cNewVaccinationSmoothed = db.rawQuery(sqlNewVaccinationSmoothed, null);
        cNewVaccinationSmoothed.moveToFirst();
        @SuppressLint("Range") Double NewVaccinationSmoothed = cNewVaccinationSmoothed.getDouble(cNewVaccinationSmoothed.getColumnIndex("NewVaccinationSmoothed"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Vaccination" + Constants.smoothed;
        metaField.value = String.valueOf(formatter.format(NewVaccinationSmoothed));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlStringencyIndex = "select \n" +
                " sum(stringency_index)--/(select count(Id) from Country)\n" + // Use division
                " as StringencyIndex\n" +
                " from Detail where date = (select max(date) from Detail where stringency_index > 0)";
        Cursor cStringencyIndex = db.rawQuery(sqlStringencyIndex, null);
        cStringencyIndex.moveToFirst();
        @SuppressLint("Range") Double StringencyIndex = cStringencyIndex.getDouble(cStringencyIndex.getColumnIndex("StringencyIndex"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Stringency Index";
        metaField.value = String.valueOf(formatter.format(StringencyIndex));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlPopulationDensity = "select \n" +
                " sum(population_density)/(select count(Id) from Country)\n" +
                " as PopulationDensity\n" +
                " from Detail where date = (select max(date) from Detail where population_density > 0)";
        Cursor cPopulationDensity = db.rawQuery(sqlPopulationDensity, null);
        cPopulationDensity.moveToFirst();
        @SuppressLint("Range") Double PopulationDensity = cPopulationDensity.getDouble(cPopulationDensity.getColumnIndex("PopulationDensity"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Population Density";
        metaField.value = String.valueOf(formatter.format(PopulationDensity));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        /*String sqlMedianAge = "select \n" +
                " sum(median_age)/(select count(Id) from Country)\n" +
                " as MedianAge\n" +
                " from Detail where date = (select max(date) from Detail where median_age > 0)";
        Cursor cMedianAge = db.rawQuery(sqlMedianAge, null);
        cMedianAge.moveToFirst();
        @SuppressLint("Range") Double MedianAge = cMedianAge.getDouble(cMedianAge.getColumnIndex("MedianAge"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Median Age";
        metaField.value = String.valueOf(formatter.format(MedianAge));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlGDPPerCapita = "select \n" +
                " sum(gdp_per_capita)/(select count(Id) from Country)\n" +
                " as GDPPerCapita\n" +
                " from Detail where date = (select max(date) from Detail where gdp_per_capita > 0)";
        Cursor cGDPPerCapita = db.rawQuery(sqlGDPPerCapita, null);
        cGDPPerCapita.moveToFirst();
        @SuppressLint("Range") Double GDPPerCapita = cGDPPerCapita.getDouble(cGDPPerCapita.getColumnIndex("GDPPerCapita"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "GDP Per Capita";
        metaField.value = String.valueOf(formatter.format(GDPPerCapita));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlExtremePoverty = "select \n" +
                " sum(extreme_poverty)/(select count(Id) from Country)\n" +
                " as ExtremePoverty\n" +
                " from Detail where date = (select max(date) from Detail where extreme_poverty > 0)";
        Cursor cExtremePoverty = db.rawQuery(sqlExtremePoverty, null);
        cExtremePoverty.moveToFirst();
        @SuppressLint("Range") Double ExtremePoverty = cExtremePoverty.getDouble(cExtremePoverty.getColumnIndex("ExtremePoverty"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Extreme Poverty";
        metaField.value = String.valueOf(formatter.format(ExtremePoverty));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        /*String sqlCardiovascularDeathRate = "select \n" +
                " sum(cardiovasc_death_rate)/(select count(Id) from Country)\n" +
                " as CardiovascularDeathRate\n" +
                " from Detail where date = (select max(date) from Detail where cardiovasc_death_rate > 0)";
        Cursor cCardiovascularDeathRate = db.rawQuery(sqlCardiovascularDeathRate, null);
        cCardiovascularDeathRate.moveToFirst();
        @SuppressLint("Range") Double CardiovascularDeathRate = cCardiovascularDeathRate.getDouble(cCardiovascularDeathRate.getColumnIndex("CardiovascularDeathRate"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Cardiovascular Death Rate";
        metaField.value = String.valueOf(formatter.format(CardiovascularDeathRate));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlHandwashingFacilities = "select \n" +
                " sum(handwashing_facilities)/(select count(Id) from Country)\n" +
                " as HandwashingFacilities\n" +
                " from Detail where date = (select max(date) from Detail where handwashing_facilities > 0)";
        Cursor cHandwashingFacilities = db.rawQuery(sqlHandwashingFacilities, null);
        cHandwashingFacilities.moveToFirst();
        @SuppressLint("Range") Double HandwashingFacilities = cHandwashingFacilities.getDouble(cHandwashingFacilities.getColumnIndex("HandwashingFacilities"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Handwashing Facilities%";
        metaField.value = String.valueOf(formatter.format(HandwashingFacilities));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

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

        /*String sqlHumanDevelopmentIndex = "select \n" +
                " sum(human_development_index)/(select count(Id) from Country)\n" +
                " as HumanDevelopmentIndex\n" +
                " from Detail where date = (select max(date) from Detail where human_development_index > 0)";
        Cursor cHumanDevelopmentIndex = db.rawQuery(sqlHumanDevelopmentIndex, null);
        cHumanDevelopmentIndex.moveToFirst();
        @SuppressLint("Range") Double HumanDevelopmentIndex = cHumanDevelopmentIndex.getDouble(cHumanDevelopmentIndex.getColumnIndex("HumanDevelopmentIndex"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Human Development Index";
        metaField.value = String.valueOf(formatter.format(HumanDevelopmentIndex));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlExcessMortalityCumulativeAbsolute = "select \n" +
                " sum(excess_mortality_cumulative_absolute)/(select count(Id) from Country)\n" +
                " as ExcessMortalityCumulativeAbsolute\n" +
                " from Detail where date = (select max(date) from Detail where excess_mortality_cumulative_absolute > 0)";
        Cursor cExcessMortalityCumulativeAbsolute = db.rawQuery(sqlExcessMortalityCumulativeAbsolute, null);
        cExcessMortalityCumulativeAbsolute.moveToFirst();
        @SuppressLint("Range") Double ExcessMortalityCumulativeAbsolute = cExcessMortalityCumulativeAbsolute.getDouble(cExcessMortalityCumulativeAbsolute.getColumnIndex("ExcessMortalityCumulativeAbsolute"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative Absolute";
        metaField.value = String.valueOf(formatter.format(ExcessMortalityCumulativeAbsolute));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlExcessMortalityCumulative = "select \n" +
                " sum(excess_mortality_cumulative)/(select count(Id) from Country)\n" +
                " as ExcessMortalityCumulative\n" +
                " from Detail where date = (select max(date) from Detail where excess_mortality_cumulative > 0)";
        Cursor cExcessMortalityCumulative = db.rawQuery(sqlExcessMortalityCumulative, null);
        cExcessMortalityCumulative.moveToFirst();
        @SuppressLint("Range") Double ExcessMortalityCumulative = cExcessMortalityCumulative.getDouble(cExcessMortalityCumulative.getColumnIndex("ExcessMortalityCumulative"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative";
        metaField.value = String.valueOf(formatter.format(ExcessMortalityCumulative));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);
*/
        /*String sqlExcessMortality = "select \n" +
                " sum(excess_mortality)/(select count(Id) from Country)\n" +
                " as ExcessMortality\n" +
                " from Detail where date = (select max(date) from Detail where excess_mortality > 0)";
        Cursor cExcessMortality = db.rawQuery(sqlExcessMortality, null);
        cExcessMortality.moveToFirst();
        @SuppressLint("Range") Double ExcessMortality = cExcessMortality.getDouble(cExcessMortality.getColumnIndex("ExcessMortality"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality";
        metaField.value = String.valueOf(formatter.format(ExcessMortality));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        /*String sqlExcessMortalityCumulativePerMillion = "select \n" +
                " sum(excess_mortality_cumulative_per_million)/(select count(Id) from Country)\n" +
                " as ExcessMortalityCumulativePerMillion\n" +
                " from Detail where date = (select max(date) from Detail where excess_mortality_cumulative_per_million > 0)";
        Cursor cExcessMortalityCumulativePerMillion = db.rawQuery(sqlExcessMortalityCumulativePerMillion, null);
        cExcessMortalityCumulativePerMillion.moveToFirst();
        @SuppressLint("Range") Double ExcessMortalityCumulativePerMillion = cExcessMortalityCumulativePerMillion.getDouble(cExcessMortalityCumulativePerMillion.getColumnIndex("ExcessMortalityCumulativePerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Excess Mortality Cumulative" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ExcessMortalityCumulativePerMillion));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        metaField = new MetaField(0L, 0L, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }

}
















