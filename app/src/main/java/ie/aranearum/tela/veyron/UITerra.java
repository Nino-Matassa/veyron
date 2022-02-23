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

        String sqlPopulation = "select sum(population) as Population from Detail where date = (select max(date) from Detail)";
        Cursor cPopulation = db.rawQuery(sqlPopulation, null);
        cPopulation.moveToFirst();
        @SuppressLint("Range") Double Population = cPopulation.getDouble(cPopulation.getColumnIndex("Population"));

        String sqlOverview = "select sum(new_cases) as TotalCase,\n" +
                " sum(new_cases_per_million) as TotalCasePerMillion,\n" +
                " sum(new_deaths) as TotalDeath,\n" +
                " sum(new_deaths_per_million) as TotalDeathPerMillion,\n" +
                " sum(reproduction_rate) as ReproductionRate,\n" +
                " count(Id) as n\n" +
                " from Detail";
        Cursor cOverview = db.rawQuery(sqlOverview, null);
        cOverview.moveToFirst();

        @SuppressLint("Range") Double TotalCase = cOverview.getDouble(cOverview.getColumnIndex("TotalCase"));
        @SuppressLint("Range") Double TotalCasePerMillion = cOverview.getDouble(cOverview.getColumnIndex("TotalCasePerMillion"));
        @SuppressLint("Range") Double TotalDeath = cOverview.getDouble(cOverview.getColumnIndex("TotalDeath"));
        @SuppressLint("Range") Double TotalDeathPerMillion = cOverview.getDouble(cOverview.getColumnIndex("TotalDeathPerMillion"));
        @SuppressLint("Range") Double ReproductionRate = cOverview.getDouble(cOverview.getColumnIndex("ReproductionRate"));
        @SuppressLint("Range") Double CountId = cOverview.getDouble(cOverview.getColumnIndex("n"));

        String sqlNewCases = "select sum(new_cases) as NewCase,\n" +
                " sum(new_cases_per_million) as NewCasePerMillion,\n" +
                " sum(new_deaths) as NewDeath,\n" +
                " sum(new_deaths_per_million) as NewDeathPerMillion,\n" +
                " sum(icu_patients) as ICUPatient,\n" +
                " sum(icu_patients_per_million) as ICUPatientPerMillion,\n" +
                " sum(hosp_patients) as HospitalPatient,\n" +
                " sum(hosp_patients_per_million) as HospitalPatientPerMillion\n" +
                " from Detail where date = (select max(date) from Detail)";
        Cursor cNewCases = db.rawQuery(sqlNewCases, null);
        cNewCases.moveToFirst();
        @SuppressLint("Range") Double NewCase = cNewCases.getDouble(cNewCases.getColumnIndex("NewCase"));
        @SuppressLint("Range") Double NewCasePerMillion = cNewCases.getDouble(cNewCases.getColumnIndex("NewCasePerMillion"));
        @SuppressLint("Range") Double NewDeath = cNewCases.getDouble(cNewCases.getColumnIndex("NewDeath"));
        @SuppressLint("Range") Double NewDeathPerMillion = cNewCases.getDouble(cNewCases.getColumnIndex("NewDeathPerMillion"));
        @SuppressLint("Range") Double ICUPatient = cNewCases.getDouble(cNewCases.getColumnIndex("ICUPatient"));
        @SuppressLint("Range") Double ICUPatientPerMillion = cNewCases.getDouble(cNewCases.getColumnIndex("ICUPatientPerMillion"));
        @SuppressLint("Range") Double HospitalPatient = cNewCases.getDouble(cNewCases.getColumnIndex("HospitalPatient"));
        @SuppressLint("Range") Double HospitalPatientPerMillion = cNewCases.getDouble(cNewCases.getColumnIndex("HospitalPatientPerMillion"));

        MetaField metaField = new MetaField();

        metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Math.round(Population)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIRegion;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Case";
        metaField.value = String.valueOf(formatter.format(Math.round(TotalCase)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(TotalCasePerMillion)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case";
        metaField.value = String.valueOf(formatter.format(Math.round(NewCase)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Case" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(NewCasePerMillion)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Death";
        metaField.value = String.valueOf(formatter.format(Math.round(TotalDeath)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Total Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(TotalDeathPerMillion)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death";
        metaField.value = String.valueOf(formatter.format(Math.round(NewDeath)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "New Death" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(NewDeathPerMillion)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(Math.round(ReproductionRate/CountId)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients";
        metaField.value = String.valueOf(formatter.format(Math.round(ICUPatient)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "ICU Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(ICUPatientPerMillion)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients";
        metaField.value = String.valueOf(formatter.format(Math.round(HospitalPatient)));
        metaField.underlineKey = false;
        metaField.UI = Constants.UIFieldXHistory;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UIFieldXHistory);
        metaField.key = "Hospital Patients" + Constants.roman1000000;
        metaField.value = String.valueOf(formatter.format(Math.round(HospitalPatientPerMillion)));
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