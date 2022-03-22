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

        MetaField metaField = new MetaField(0L, 0L, Constants.UIRegion);
        String sqlPopulation = "select sum(population) as Population from Detail where date = (select max(date) from Detail)";
        Cursor cPopulation = db.rawQuery(sqlPopulation, null);
        cPopulation.moveToFirst();
        @SuppressLint("Range") Double Population = cPopulation.getDouble(cPopulation.getColumnIndex("Population"));
        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Population));
        metaField.underlineKey = true;
        //metaField.UI = Constants.UIRegion;
        metaFields.add(metaField);

        /*String sqlTotalCase = "select \n" +
                " sum(total_cases) \n" +
                " as TotalCase\n" +
                " from Detail where date = (select max(date) from Detail where total_cases > 0)";
        Cursor cTotalCase = db.rawQuery(sqlTotalCase, null);
        cTotalCase.moveToFirst();
        @SuppressLint("Range") Double TotalCase = cTotalCase.getDouble(cTotalCase.getColumnIndex("TotalCase"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Case";
        metaField.value = String.valueOf(formatter.format(TotalCase));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);*/

        String sqlTotalCasePerMillion = "select \n" +
                " sum(total_cases_per_million)/(select count(Id) from Country)\n" +
                " as TotalCasePerMillion\n" +
                " from Detail where date = (select max(date) from Detail where total_cases_per_million > 0)";
        Cursor cTotalCasePerMillion = db.rawQuery(sqlTotalCasePerMillion, null);
        cTotalCasePerMillion.moveToFirst();
        @SuppressLint("Range") Double TotalCasePerMillion = cTotalCasePerMillion.getDouble(cTotalCasePerMillion.getColumnIndex("TotalCasePerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "total_cases_per_million";
        metaField.executeSQL = "select Detail.location, total_cases_per_million, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by total_cases_per_million desc";
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

        String sqlNewCasePerMillion = "select \n" +
                " sum(new_cases_per_million)/(select count(Id) from Country)\n" +
                " as NewCasePerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_cases_per_million > 0)";
        Cursor cNewCasePerMillion = db.rawQuery(sqlNewCasePerMillion, null);
        cNewCasePerMillion.moveToFirst();
        @SuppressLint("Range") Double NewCasePerMillion = cNewCasePerMillion.getDouble(cNewCasePerMillion.getColumnIndex("NewCasePerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "new_cases_per_million";
        metaField.executeSQL = "select Detail.location, new_cases_per_million, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by new_cases_per_million desc";
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

        String sqlTotalDeathPerMillion = "select \n" +
                " sum(total_deaths_per_million)/(select count(Id) from Country)\n" +
                " as TotalDeathPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where total_deaths_per_million > 0)";
        Cursor cTotalDeathPerMillion = db.rawQuery(sqlTotalDeathPerMillion, null);
        cTotalDeathPerMillion.moveToFirst();
        @SuppressLint("Range") Double TotalDeathPerMillion = cTotalDeathPerMillion.getDouble(cTotalDeathPerMillion.getColumnIndex("TotalDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "total_deaths_per_million";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "total_deaths_per_million");
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

        String sqlNewDeathPerMillion = "select \n" +
                " sum(new_deaths_per_million)/(select count(Id) from Country)\n" +
                " as NewDeathPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths_per_million > 0)";
        Cursor cNewDeathPerMillion = db.rawQuery(sqlNewDeathPerMillion, null);
        cNewDeathPerMillion.moveToFirst();
        @SuppressLint("Range") Double NewDeathPerMillion = cNewDeathPerMillion.getDouble(cNewDeathPerMillion.getColumnIndex("NewDeathPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = false;
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "new_deaths_per_million";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "new_deaths_per_million");
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

        String sqlReproductionRate = "select \n" +
                " sum(reproduction_rate)/(select count(Id) from Detail where reproduction_rate > 0) \n" +
                " as ReproductionRate\n" +
                " from Detail ";
        Cursor cReproductionRate = db.rawQuery(sqlReproductionRate, null);
        cReproductionRate.moveToFirst();
        @SuppressLint("Range") Double ReproductionRate = cReproductionRate.getDouble(cReproductionRate.getColumnIndex("ReproductionRate"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(ReproductionRate));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "reproduction_rate";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "reproduction_rate");
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

        String sqlICUPatientPerMillion = "select \n" +
                " sum(icu_patients_per_million)/(select count(Id) from Country)\n" +
                " as ICUPatientPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where icu_patients_per_million > 0)";
        Cursor cICUPatientPerMillion = db.rawQuery(sqlICUPatientPerMillion, null);
        cICUPatientPerMillion.moveToFirst();
        @SuppressLint("Range") Double ICUPatientPerMillion = cICUPatientPerMillion.getDouble(cICUPatientPerMillion.getColumnIndex("ICUPatientPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "icu_patients_per_million";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "icu_patients_per_million");
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

        String sqlHospitalPatientPerMillion = "select \n" +
                " sum(hosp_patients_per_million)/(select count(Id) from Country)\n" +
                " as HospitalPatientPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where hosp_patients_per_million > 0)";
        Cursor cHospitalPatientPerMillion = db.rawQuery(sqlHospitalPatientPerMillion, null);
        cHospitalPatientPerMillion.moveToFirst();
        @SuppressLint("Range") Double HospitalPatientPerMillion = cHospitalPatientPerMillion.getDouble(cHospitalPatientPerMillion.getColumnIndex("HospitalPatientPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "hosp_patients_per_million";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "hosp_patients_per_million");
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

        String sqlWeeklyICUAdmissionPerMillion = "select \n" +
                " sum(weekly_icu_admissions_per_million)/(select count(Id) from Country)\n" +
                " as WeeklyICUAdmissionPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where weekly_icu_admissions_per_million > 0)";
        Cursor cWeeklyICUAdmissionPerMillion = db.rawQuery(sqlWeeklyICUAdmissionPerMillion, null);
        cWeeklyICUAdmissionPerMillion.moveToFirst();
        @SuppressLint("Range") Double WeeklyICUAdmissionPerMillion = cWeeklyICUAdmissionPerMillion.getDouble(cWeeklyICUAdmissionPerMillion.getColumnIndex("WeeklyICUAdmissionPerMillion"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionPerMillion));
        metaField.underlineKey = false;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "weekly_icu_admissions_per_million";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "weekly_icu_admissions_per_million");
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

        String sqlWeeklyHospitalAdmissionPerMillion = "select \n" +
                " sum(weekly_hosp_admissions_per_million)/(select count(Id) from Country)\n" +
                " as WeeklyHospitalAdmissionPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where weekly_hosp_admissions_per_million > 0)";
        Cursor cWeeklyHospitalAdmissionPerMillion = db.rawQuery(sqlWeeklyHospitalAdmissionPerMillion, null);
        cWeeklyHospitalAdmissionPerMillion.moveToFirst();
        @SuppressLint("Range") Double WeeklyHospitalAdmissionPerMillion = cWeeklyHospitalAdmissionPerMillion.getDouble(cWeeklyHospitalAdmissionPerMillion.getColumnIndex("WeeklyHospitalAdmissionPerMillion"));
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

        String sqlTotalTestPerThousand = "select \n" +
                " sum(total_tests_per_thousand)/(select count(Id) from Country)\n" +
                " as TotalTestPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where total_tests_per_thousand > 0)";
        Cursor cTotalTestPerThousand = db.rawQuery(sqlTotalTestPerThousand, null);
        cTotalTestPerThousand.moveToFirst();
        @SuppressLint("Range") Double TotalTestPerThousand = cTotalTestPerThousand.getDouble(cTotalTestPerThousand.getColumnIndex("TotalTestPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(TotalTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "total_tests_per_thousand";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "total_tests_per_thousand");
        metaFields.add(metaField);

        String sqlNewTestPerThousand = "select \n" +
                " sum(new_tests_per_thousand)/(select count(Id) from Country)\n" +
                " as NewTestPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where new_tests_per_thousand > 0)";
        Cursor cNewTestPerThousand = db.rawQuery(sqlNewTestPerThousand, null);
        cNewTestPerThousand.moveToFirst();
        @SuppressLint("Range") Double NewTestPerThousand = cNewTestPerThousand.getDouble(cNewTestPerThousand.getColumnIndex("NewTestPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestPerThousand));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "new_tests_per_thousand";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select max(date) from Detail) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "new_tests_per_thousand");
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

        String sqlPositiveRate = "select \n" +
                " sum(positive_rate)\n" +
                " as PositiveRate\n" +
                " from Detail where date = (select max(date) from Detail where positive_rate > 0)";
        Cursor cPositiveRate = db.rawQuery(sqlPositiveRate, null);
        cPositiveRate.moveToFirst();
        @SuppressLint("Range") Double PositiveRate = cPositiveRate.getDouble(cPositiveRate.getColumnIndex("PositiveRate"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Positive Rate%";
        metaField.value = String.valueOf(formatter.format(PositiveRate));
        metaField.underlineKey = true;
        metaField.fieldXHistoryType = Constants.FieldXHistoryType.ByCountry;
        metaField.fieldXName = "positive_rate";
        metaField.executeSQL = "select Detail.location, #X, Country.FK_Region, FK_Country, continent from Detail\n" +
                "join Country on Country.id = Detail.FK_Country\n" +
                "where date = (select date from Detail where positive_rate > 0) order by #X desc";
        metaField.executeSQL = metaField.executeSQL.replace("#X", "positive_rate");
        metaFields.add(metaField);

        String sqlTestPerCase = "select \n" +
                " sum(tests_per_case)/(select count(Id) from Country)\n" +
                " as TestPerCase\n" +
                " from Detail where date = (select max(date) from Detail where tests_per_case > 0)";
        Cursor cTestPerCase = db.rawQuery(sqlTestPerCase, null);
        cTestPerCase.moveToFirst();
        @SuppressLint("Range") Double TestPerCase = cTestPerCase.getDouble(cTestPerCase.getColumnIndex("TestPerCase"));
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

        String sqlTotalVaccinationPerHundred = "select\n" +
                " sum(total_vaccinations_per_hundred)/(select count(Id) from Country)\n" +
                " as TotalVaccinationPerHundred\n" +
                " from Detail where date = (select max(date) from Detail where total_vaccinations_per_hundred > 0)";
        Cursor cTotalVaccinationPerHundred = db.rawQuery(sqlTotalVaccinationPerHundred, null);
        cTotalVaccinationPerHundred.moveToFirst();
        @SuppressLint("Range") Double TotalVaccinationPerHundred = cTotalVaccinationPerHundred.getDouble(cTotalVaccinationPerHundred.getColumnIndex("TotalVaccinationPerHundred"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Total Vaccination" + Constants.roman100;
        metaField.value = String.valueOf(formatter.format(TotalVaccinationPerHundred));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlPeopleVaccinatedPerHundred = "select\n" +
                " sum(people_vaccinated_per_hundred)/(select count(Id) from Country)\n" +
                " as PeopleVaccinatedPerHundred\n" +
                " from Detail where date = (select max(date) from Detail where people_vaccinated_per_hundred > 0)";
        Cursor cPeopleVaccinatedPerHundred = db.rawQuery(sqlPeopleVaccinatedPerHundred, null);
        cPeopleVaccinatedPerHundred.moveToFirst();
        @SuppressLint("Range") Double PeopleVaccinatedPerHundred = cPeopleVaccinatedPerHundred.getDouble(cPeopleVaccinatedPerHundred.getColumnIndex("PeopleVaccinatedPerHundred"));
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

        String sqlAged65Older = "select \n" +
                " sum(aged_65_older)/(select count(Id) from Country)\n" +
                " as Aged65Older\n" +
                " from Detail where date = (select max(date) from Detail where aged_65_older > 0)";
        Cursor cAged65Older = db.rawQuery(sqlAged65Older, null);
        cAged65Older.moveToFirst();
        @SuppressLint("Range") Double Aged65Older = cAged65Older.getDouble(cAged65Older.getColumnIndex("Aged65Older"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Aged65Older%";
        metaField.value = String.valueOf(formatter.format(Aged65Older));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlAged70Older = "select \n" +
                " sum(aged_70_older)/(select count(Id) from Country)\n" +
                " as Aged70Older\n" +
                " from Detail where date = (select max(date) from Detail where aged_70_older > 0)";
        Cursor cAged70Older = db.rawQuery(sqlAged70Older, null);
        cAged70Older.moveToFirst();
        @SuppressLint("Range") Double Aged70Older = cAged70Older.getDouble(cAged70Older.getColumnIndex("Aged70Older"));
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

        String sqlDiabetesPrevalence = "select \n" +
                " sum(diabetes_prevalence)/(select count(Id) from Country)\n" +
                " as DiabetesPrevalence\n" +
                " from Detail where date = (select max(date) from Detail where diabetes_prevalence > 0)";
        Cursor cDiabetesPrevalence = db.rawQuery(sqlDiabetesPrevalence, null);
        cDiabetesPrevalence.moveToFirst();
        @SuppressLint("Range") Double DiabetesPrevalence = cDiabetesPrevalence.getDouble(cDiabetesPrevalence.getColumnIndex("DiabetesPrevalence"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Diabetes Prevalence%";
        metaField.value = String.valueOf(formatter.format(DiabetesPrevalence));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlFemaleSmoker = "select \n" +
                " sum(female_smokers)/(select count(Id) from Country)\n" +
                " as FemaleSmoker\n" +
                " from Detail where date = (select max(date) from Detail where female_smokers > 0)";
        Cursor cFemaleSmoker = db.rawQuery(sqlFemaleSmoker, null);
        cFemaleSmoker.moveToFirst();
        @SuppressLint("Range") Double FemaleSmoker = cFemaleSmoker.getDouble(cFemaleSmoker.getColumnIndex("FemaleSmoker"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "Female Smoker%";
        metaField.value = String.valueOf(formatter.format(FemaleSmoker));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlMaleSmoker = "select \n" +
                " sum(male_smokers)/(select count(Id) from Country)\n" +
                " as MaleSmoker\n" +
                " from Detail where date = (select max(date) from Detail where male_smokers > 0)";
        Cursor cMaleSmoker = db.rawQuery(sqlMaleSmoker, null);
        cMaleSmoker.moveToFirst();
        @SuppressLint("Range") Double MaleSmoker = cMaleSmoker.getDouble(cMaleSmoker.getColumnIndex("MaleSmoker"));
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

        String sqlHospitalBedsPerThousand = "select \n" +
                " sum(hospital_beds_per_thousand)/(select count(Id) from Country)\n" +
                " as HospitalBedsPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where hospital_beds_per_thousand > 0)";
        Cursor cHospitalBedsPerThousand = db.rawQuery(sqlHospitalBedsPerThousand, null);
        cHospitalBedsPerThousand.moveToFirst();
        @SuppressLint("Range") Double HospitalBedsPerThousand = cHospitalBedsPerThousand.getDouble(cHospitalBedsPerThousand.getColumnIndex("HospitalBedsPerThousand"));
        metaField = new MetaField(0L, 0L, Constants.UIFieldXHistory);
        metaField.key = "HospitalBeds" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(HospitalBedsPerThousand));
        metaField.underlineKey = false;
        //metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlLifeExpectancy = "select \n" +
                " sum(life_expectancy)/(select count(Id) from Country)\n" +
                " as LifeExpectancy\n" +
                " from Detail where date = (select max(date) from Detail where life_expectancy > 0)";
        Cursor cLifeExpectancy = db.rawQuery(sqlLifeExpectancy, null);
        cLifeExpectancy.moveToFirst();
        @SuppressLint("Range") Double LifeExpectancy = cLifeExpectancy.getDouble(cLifeExpectancy.getColumnIndex("LifeExpectancy"));
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
















