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
        UIMessage.informationBox(context, "Terra");
        registerOnStack();

        uiHandler();
    }

    @Override
    public void registerOnStack() {
        uiHistory = new UIHistory(0, 0, Constants.UITerra);
        MainActivity.stack.add(uiHistory);
    }

    private void uiHandler() {
        populateTerra();
        setHeader("Terra", "Overview");
        UIMessage.informationBox(context, null);
    }

    private void populateTerra() {
        String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
        File csv = new File(filePath);
        String lastUpdated = new Date(csv.lastModified()).toString();
        String[] aDate = lastUpdated.split("-");
        LocalDate localDate = LocalDate.of(Integer.valueOf(aDate[0]), Integer.valueOf(aDate[1]), Integer.valueOf(aDate[2]));
        lastUpdated = localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        MetaField metaField = new MetaField(0, 0, Constants.UIRegion);
        String sqlPopulation = "select sum(population) as Population from Detail where date = (select max(date) from Detail)";
        Cursor cPopulation = db.rawQuery(sqlPopulation, null);
        cPopulation.moveToFirst();
        @SuppressLint("Range") Double Population = cPopulation.getDouble(cPopulation.getColumnIndex("Population"));
        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Math.round(Population)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIRegion;
        metaFields.add(metaField);

        String sqlTotalCase = "select \n" +
                " sum(total_cases) \n" +
                " as TotalCase\n" +
                " from Detail where date = (select max(date) from Detail where total_cases > 0)";
        Cursor cTotalCase = db.rawQuery(sqlTotalCase, null);
        cTotalCase.moveToFirst();
        @SuppressLint("Range") Double TotalCase = cTotalCase.getDouble(cTotalCase.getColumnIndex("TotalCase"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Case";
        metaField.value = String.valueOf(formatter.format(Math.round(TotalCase)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalCasePerMillion = "select \n" +
                " sum(total_cases_per_million)/(select count(Id) from Country)\n" +
                " as TotalCasePerMillion\n" +
                " from Detail where date = (select max(date) from Detail where total_cases_per_million > 0)";
        Cursor cTotalCasePerMillion = db.rawQuery(sqlTotalCasePerMillion, null);
        cTotalCasePerMillion.moveToFirst();
        @SuppressLint("Range") Double TotalCasePerMillion = cTotalCasePerMillion.getDouble(cTotalCasePerMillion.getColumnIndex("TotalCasePerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalCasePerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);


        String sqlNewCase = "select \n" +
                " sum(new_cases)\n" +
                " as NewCase\n" +
                " from Detail where date = (select max(date) from Detail where new_cases > 0)";
        Cursor cNewCase = db.rawQuery(sqlNewCase, null);
        cNewCase.moveToFirst();
        @SuppressLint("Range") Double NewCase = cNewCase.getDouble(cNewCase.getColumnIndex("NewCase"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case";
        metaField.value = String.valueOf(formatter.format(Math.round(NewCase)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewCasePerMillion = "select \n" +
                " sum(new_cases_per_million)/(select count(Id) from Country)\n" +
                " as NewCasePerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_cases_per_million > 0)";
        Cursor cNewCasePerMillion = db.rawQuery(sqlNewCasePerMillion, null);
        cNewCasePerMillion.moveToFirst();
        @SuppressLint("Range") Double NewCasePerMillion = cNewCasePerMillion.getDouble(cNewCasePerMillion.getColumnIndex("NewCasePerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewCaseSmoothed = "select \n" +
                " sum(new_cases_smoothed)\n" +
                " as NewCaseSmoothed\n" +
                " from Detail where date = (select max(date) from Detail where new_cases_smoothed > 0)";
        Cursor cNewCaseSmoothed = db.rawQuery(sqlNewCaseSmoothed, null);
        cNewCaseSmoothed.moveToFirst();
        @SuppressLint("Range") Double NewCaseSmoothed = cNewCaseSmoothed.getDouble(cNewCaseSmoothed.getColumnIndex("NewCaseSmoothed"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.smoothed;
        metaField.value = String.valueOf(formatter.format(NewCasePerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
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
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalDeath = "select \n" +
                " sum(total_deaths)\n" +
                " as TotalDeath\n" +
                " from Detail where date = (select max(date) from Detail where total_deaths > 0)";
        Cursor cTotalDeath = db.rawQuery(sqlTotalDeath, null);
        cTotalDeath.moveToFirst();
        @SuppressLint("Range") Double TotalDeath = cTotalDeath.getDouble(cTotalDeath.getColumnIndex("TotalDeath"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Death";
        metaField.value = String.valueOf(formatter.format(Math.round(TotalDeath)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalDeathPerMillion = "select \n" +
                " sum(total_deaths_per_million)/(select count(Id) from Country)\n" +
                " as TotalDeathPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where total_deaths_per_million > 0)";
        Cursor cTotalDeathPerMillion = db.rawQuery(sqlTotalDeathPerMillion, null);
        cTotalDeathPerMillion.moveToFirst();
        @SuppressLint("Range") Double TotalDeathPerMillion = cTotalDeathPerMillion.getDouble(cTotalDeathPerMillion.getColumnIndex("TotalDeathPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(TotalDeathPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewDeath = "select \n" +
                " sum(new_deaths)\n" +
                " as NewDeath\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths > 0)";
        Cursor cNewDeath = db.rawQuery(sqlNewDeath, null);
        cNewDeath.moveToFirst();
        @SuppressLint("Range") Double NewDeath = cNewDeath.getDouble(cNewDeath.getColumnIndex("NewDeath"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death";
        metaField.value = String.valueOf(formatter.format(Math.round(NewDeath)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewDeathPerMillion = "select \n" +
                " sum(new_deaths_per_million)/(select count(Id) from Country)\n" +
                " as NewDeathPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where new_deaths_per_million > 0)";
        Cursor cNewDeathPerMillion = db.rawQuery(sqlNewDeathPerMillion, null);
        cNewDeathPerMillion.moveToFirst();
        @SuppressLint("Range") Double NewDeathPerMillion = cNewDeathPerMillion.getDouble(cNewDeathPerMillion.getColumnIndex("NewDeathPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(NewDeathPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewDeathSmoothed = "select \n" +
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
        metaField.UI = Constants.UIFieldXHistory;
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
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlReproductionRate = "select \n" +
                " sum(reproduction_rate)/(select count(Id) from Detail where reproduction_rate > 0) \n" +
                " as ReproductionRate\n" +
                " from Detail ";
        Cursor cReproductionRate = db.rawQuery(sqlReproductionRate, null);
        cReproductionRate.moveToFirst();
        @SuppressLint("Range") Double ReproductionRate = cReproductionRate.getDouble(cReproductionRate.getColumnIndex("ReproductionRate"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(Math.round(ReproductionRate)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlICUPatient = "select \n" +
                " sum(icu_patients)\n" +
                " as ICUPatient\n" +
                " from Detail where date = (select max(date) from Detail where icu_patients > 0)";
        Cursor cICUPatient = db.rawQuery(sqlICUPatient, null);
        cICUPatient.moveToFirst();
        @SuppressLint("Range") Double ICUPatient = cICUPatient.getDouble(cICUPatient.getColumnIndex("ICUPatient"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients";
        metaField.value = String.valueOf(formatter.format(Math.round(ICUPatient)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlICUPatientPerMillion = "select \n" +
                " sum(icu_patients_per_million)/(select count(Id) from Country)\n" +
                " as ICUPatientPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where icu_patients_per_million > 0)";
        Cursor cICUPatientPerMillion = db.rawQuery(sqlICUPatientPerMillion, null);
        cICUPatientPerMillion.moveToFirst();
        @SuppressLint("Range") Double ICUPatientPerMillion = cICUPatientPerMillion.getDouble(cICUPatientPerMillion.getColumnIndex("ICUPatientPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(ICUPatientPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlHospitalPatient = "select \n" +
                " sum(hosp_patients)\n" +
                " as HospitalPatient\n" +
                " from Detail where date = (select max(date) from Detail where hosp_patients > 0)";
        Cursor cHospitalPatient = db.rawQuery(sqlHospitalPatient, null);
        cHospitalPatient.moveToFirst();
        @SuppressLint("Range") Double HospitalPatient = cHospitalPatient.getDouble(cHospitalPatient.getColumnIndex("HospitalPatient"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients";
        metaField.value = String.valueOf(formatter.format(Math.round(HospitalPatient)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlHospitalPatientPerMillion = "select \n" +
                " sum(hosp_patients_per_million)/(select count(Id) from Country)\n" +
                " as HospitalPatientPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where hosp_patients_per_million > 0)";
        Cursor cHospitalPatientPerMillion = db.rawQuery(sqlHospitalPatientPerMillion, null);
        cHospitalPatientPerMillion.moveToFirst();
        @SuppressLint("Range") Double HospitalPatientPerMillion = cHospitalPatientPerMillion.getDouble(cHospitalPatientPerMillion.getColumnIndex("HospitalPatientPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(HospitalPatientPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlWeeklyICUAdmission = "select \n" +
                " sum(weekly_icu_admissions)\n" +
                " as WeeklyICUAdmission\n" +
                " from Detail where date = (select max(date) from Detail where weekly_icu_admissions > 0)";
        Cursor cWeeklyICUAdmission = db.rawQuery(sqlWeeklyICUAdmission, null);
        cWeeklyICUAdmission.moveToFirst();
        @SuppressLint("Range") Double WeeklyICUAdmission = cWeeklyICUAdmission.getDouble(cWeeklyICUAdmission.getColumnIndex("WeeklyICUAdmission"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU";
        metaField.value = String.valueOf(formatter.format(Math.round(WeeklyICUAdmission)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlWeeklyICUAdmissionPerMillion = "select \n" +
                " sum(weekly_icu_admissions_per_million)/(select count(Id) from Country)\n" +
                " as WeeklyICUAdmissionPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where weekly_icu_admissions_per_million > 0)";
        Cursor cWeeklyICUAdmissionPerMillion = db.rawQuery(sqlWeeklyICUAdmissionPerMillion, null);
        cWeeklyICUAdmissionPerMillion.moveToFirst();
        @SuppressLint("Range") Double WeeklyICUAdmissionPerMillion = cWeeklyICUAdmissionPerMillion.getDouble(cWeeklyICUAdmissionPerMillion.getColumnIndex("WeeklyICUAdmissionPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly ICU" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyICUAdmissionPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlWeeklyHospitalAdmission = "select \n" +
                " sum(weekly_hosp_admissions)\n" +
                " as WeeklyHospitalAdmission\n" +
                " from Detail where date = (select max(date) from Detail where weekly_hosp_admissions > 0)";
        Cursor cWeeklyHospitalAdmission = db.rawQuery(sqlWeeklyHospitalAdmission, null);
        cWeeklyHospitalAdmission.moveToFirst();
        @SuppressLint("Range") Double WeeklyHospitalAdmission = cWeeklyHospitalAdmission.getDouble(cWeeklyHospitalAdmission.getColumnIndex("WeeklyHospitalAdmission"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly Hospital";
        metaField.value = String.valueOf(formatter.format(Math.round(WeeklyHospitalAdmission)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlWeeklyHospitalAdmissionPerMillion = "select \n" +
                " sum(weekly_hosp_admissions_per_million)/(select count(Id) from Country)\n" +
                " as WeeklyHospitalAdmissionPerMillion\n" +
                " from Detail where date = (select max(date) from Detail where weekly_hosp_admissions_per_million > 0)";
        Cursor cWeeklyHospitalAdmissionPerMillion = db.rawQuery(sqlWeeklyHospitalAdmissionPerMillion, null);
        cWeeklyHospitalAdmissionPerMillion.moveToFirst();
        @SuppressLint("Range") Double WeeklyHospitalAdmissionPerMillion = cWeeklyHospitalAdmissionPerMillion.getDouble(cWeeklyHospitalAdmissionPerMillion.getColumnIndex("WeeklyHospitalAdmissionPerMillion"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Weekly Hospital" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(WeeklyHospitalAdmissionPerMillion));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewTest = "select \n" +
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
        metaField.UI = Constants.UIFieldXHistory;
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
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTotalTestPerThousand = "select \n" +
                " sum(total_tests_per_thousand)/(select count(Id) from Country)\n" +
                " as TotalTestPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where total_tests_per_thousand > 0)";
        Cursor cTotalTestPerThousand = db.rawQuery(sqlTotalTestPerThousand, null);
        cTotalTestPerThousand.moveToFirst();
        @SuppressLint("Range") Double TotalTestPerThousand = cTotalTestPerThousand.getDouble(cTotalTestPerThousand.getColumnIndex("TotalTestPerThousand"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(TotalTestPerThousand));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewTestPerThousand = "select \n" +
                " sum(new_tests_per_thousand)/(select count(Id) from Country)\n" +
                " as NewTestPerThousand\n" +
                " from Detail where date = (select max(date) from Detail where new_tests_per_thousand > 0)";
        Cursor cNewTestPerThousand = db.rawQuery(sqlNewTestPerThousand, null);
        cNewTestPerThousand.moveToFirst();
        @SuppressLint("Range") Double NewTestPerThousand = cNewTestPerThousand.getDouble(cNewTestPerThousand.getColumnIndex("NewTestPerThousand"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Test" + Constants.roman1000;
        metaField.value = String.valueOf(formatter.format(NewTestPerThousand));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlNewTestSmoothed = "select \n" +
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
        metaField.UI = Constants.UIFieldXHistory;
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
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlPositiveRate = "select \n" +
                " sum(positive_rate)\n" +
                " as PositiveRate\n" +
                " from Detail where date = (select max(date) from Detail where positive_rate > 0)";
        Cursor cPositiveRate = db.rawQuery(sqlPositiveRate, null);
        cPositiveRate.moveToFirst();
        @SuppressLint("Range") Double PositiveRate = cPositiveRate.getDouble(cPositiveRate.getColumnIndex("PositiveRate"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Positive Rate";
        metaField.value = String.valueOf(formatter.format(PositiveRate));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        String sqlTestPerCase = "select \n" +
                " sum(tests_per_case)/(select count(Id) from Country)\n" +
                " as TestPerCase\n" +
                " from Detail where date = (select max(date) from Detail where tests_per_case > 0)";
        Cursor cTestPerCase = db.rawQuery(sqlTestPerCase, null);
        cTestPerCase.moveToFirst();
        @SuppressLint("Range") Double TestPerCase = cTestPerCase.getDouble(cTestPerCase.getColumnIndex("TestPerCase"));
        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Test Per Case";
        metaField.value = String.valueOf(formatter.format(TestPerCase));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }

}