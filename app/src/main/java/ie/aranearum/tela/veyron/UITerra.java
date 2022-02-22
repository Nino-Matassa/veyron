package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;

public class UITerra extends UI implements IRegisterOnStack {

    private Context context = null;
    private DecimalFormat formatter = null;
    private UIHistory uiHistory = null;

    private String country = null;
    private Integer totalCase = null;
    private Double casePer100000 = 0.0;
    private Integer case7Day = 0;
    private Integer case24Hour = 0;
    private Integer totalDeath = 0;
    private Double deathPer100000 = 0.0;
    private Integer death7Day = 0;
    private Integer death24Hour = 0;
    private String lastUpdated = null;
    private Double precentInfected = 0.0;
    private Double population = 0.0;
    private Double infectionsCurve = 0.0;
    private Integer nCountry = 0;


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
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                populateTerra();
                setHeader("Terra", "General");
                UIMessage.informationBox(context, null);
            }
        }, Constants.delayMilliSeconds);
    }

    private void populateTerra() {
        String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
        File csv = new File(filePath);
        lastUpdated = new Date(csv.lastModified()).toString();
        String[] arrDate = lastUpdated.split(" ");
        lastUpdated = arrDate[0] + " " + arrDate[2] + " " + arrDate[3] + " " + arrDate[5];

        String sql = "select (select count(Id) from country) as nCountry, Country, TotalCase, CasePer100000, Case7Day, Case24Hour, TotalDeath, DeathPer100000, Death7Day, Death24Hour, Source from overview where region = 'Terra'";
        Cursor cTerra = db.rawQuery(sql, null);
        cTerra.moveToFirst();

        country = cTerra.getString(cTerra.getColumnIndex("Country"));
        totalCase = cTerra.getInt(cTerra.getColumnIndex("TotalCase"));
        casePer100000 = cTerra.getDouble(cTerra.getColumnIndex("CasePer100000"));
        case7Day = cTerra.getInt(cTerra.getColumnIndex("Case7Day"));
        case24Hour = cTerra.getInt(cTerra.getColumnIndex("Case24Hour"));
        totalDeath = cTerra.getInt(cTerra.getColumnIndex("TotalDeath"));
        deathPer100000 = cTerra.getDouble(cTerra.getColumnIndex("DeathPer100000"));
        death7Day = cTerra.getInt(cTerra.getColumnIndex("Death7Day"));
        death24Hour = cTerra.getInt(cTerra.getColumnIndex("Death24Hour"));
        population = totalCase / casePer100000 * Constants.oneHundredThousand;
        precentInfected = totalCase / population * 100;
        infectionsCurve = Math.log((double)case24Hour);
        nCountry = cTerra.getInt(cTerra.getColumnIndex("nCountry"));

        MetaField metaField = new MetaField();
        /*metaField.key = "Population";
        metaField.value = String.valueOf(formatter.format(Math.round(population)));
        metaField.underlineKey = true;
        metaField.UI = Constants.UIRegion;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraActiveCases);
        metaField.key = "Active Cases";
        String sqlActiveCases = "select Date, sum(NewCase) as CaseX from Detail group by date order by date desc limit 29";
        Cursor cActiveCases = db.rawQuery(sqlActiveCases, null);
        ArrayList<CaseRangeTotal> fieldTotals = new CaseRangeCalculation().calculate(cActiveCases, Constants.moonPhase);
        int activeCases = fieldTotals.get(0).total;

        metaField.value = String.valueOf(formatter.format(activeCases));
        metaField.underlineKey = true;
        metaField.underlineValue = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraActiveCasesPerX);
        metaField.key = "Active Cases/" + Constants.roman100000;
        Double activeCases_C = (activeCases / population * Constants.oneHundredThousand) / nCountry;
        metaField.value = String.valueOf(formatter.format(activeCases_C));
        metaField.underlineKey = true;
        metaField.underlineValue = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraTotalCases);
        metaField.key = "Total Cases";
        metaField.value = String.valueOf(formatter.format(totalCase));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraCase24H);
        metaField.key = "Case24H";
        metaField.value = String.valueOf(formatter.format(case24Hour));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraCase7D);
        metaField.key = "Case7D";
        metaField.value = String.valueOf(formatter.format(case7Day));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraTotalDeaths);
        metaField.key = "Total Deaths";
        metaField.value = String.valueOf(formatter.format(totalDeath));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraDeath24H);
        metaField.key = "Death24H";
        metaField.value = String.valueOf(formatter.format(death24Hour));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraDeath7D);
        metaField.key = "Death7D";
        metaField.value = String.valueOf(formatter.format(death7Day));
        metaField.underlineKey = true;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraTotalInfected);
        metaField.key = "Total Infected";
        metaField.value = String.valueOf(formatter.format(precentInfected)) + "%";
        metaField.underlineKey = true;
        metaFields.add(metaField);

        String sqlRNought = "select Date, sum(NewCase) as CaseX from Detail group by Date order by Date desc limit 29";
        Cursor cRNought = db.rawQuery(sqlRNought, null);

        ArrayList<RNoughtAverage> rNoughtAverage = new RNoughtCalculation().calculate(cRNought, Constants.moonPhase);
        Double rNought = rNoughtAverage.get(0).average;
        metaField = new MetaField(0, 0, Constants.UITerraRNought);
        metaField.key = Constants.rNought;
        metaField.value = String.valueOf(formatter.format(rNought));
        metaField.underlineKey = true;
        metaField.underlineValue = true;
        metaFields.add(metaField);
        metaField = new MetaField();

        String sqlVaccineEntries = "select count(Id) as Id from Vaccine";
        Cursor cVaccineEntries = db.rawQuery(sqlVaccineEntries, null);
        cVaccineEntries.moveToFirst();
        Long nEntries = cVaccineEntries.getLong(cVaccineEntries.getColumnIndex("Id"));
        String sqlVaccine = "select sum(totalvaccinations) as TotalVaccinations, sum(totalpersons) as TotalPersons, sum(totalvaccinationsper100) as TotalVaccinationsPer100, sum(totalpersonsper100) as TotalPersonsPer100, sum(FullyVaccinated) as FullyVaccinated, sum(fullyvaccinatedper100) as FullyVaccinatedPer100 from vaccine";
        Cursor cVaccine = db.rawQuery(sqlVaccine, null);
        cVaccine.moveToFirst();

        Long TotalVaccinations = 0L;
        Long TotalPersons = 0L;
        Double TotalVaccinationsPer100 = 0.0;
        Double TotalPersonsPer100 = 0.0;
        Long FullyVaccinated = 0L;
        Double FullyVaccinatedPer100 = 0.0;

        TotalVaccinations = cVaccine.getLong(cVaccine.getColumnIndex("TotalVaccinations"));
        TotalPersons = cVaccine.getLong(cVaccine.getColumnIndex("TotalPersons"));
        TotalVaccinationsPer100 = cVaccine.getDouble(cVaccine.getColumnIndex("TotalVaccinationsPer100"))/nEntries;
        TotalPersonsPer100 = cVaccine.getDouble(cVaccine.getColumnIndex("TotalPersonsPer100"))/nEntries;
        FullyVaccinated = cVaccine.getLong(cVaccine.getColumnIndex("FullyVaccinated"));
        FullyVaccinatedPer100 = cVaccine.getDouble(cVaccine.getColumnIndex("FullyVaccinatedPer100"))/nEntries;

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Vaccine Information";
        metaField.value = "Data";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Total Doses";
        metaField.value = String.valueOf(formatter.format(TotalVaccinations));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Dose/100";
        metaField.value = String.valueOf(formatter.format(TotalVaccinationsPer100));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Vaccinated";
        metaField.value = String.valueOf(formatter.format(TotalPersons));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Fully Vaccinated";
        metaField.value = String.valueOf(formatter.format(FullyVaccinated));
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "Vaccinated";
        metaField.value = String.valueOf(formatter.format(TotalPersonsPer100)) + "%";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        metaField = new MetaField(0, 0, Constants.UITerraFullyVaccinated);
        metaField.key = "Fully Vaccinated";
        metaField.value = String.valueOf(formatter.format(FullyVaccinatedPer100)) + "%";
        metaField.underlineKey = true;
        metaFields.add(metaField);*/

        metaField = new MetaField(0, 0, Constants.UITerra);
        metaField.key = "";
        metaField.value = "";
        metaField.underlineKey = false;
        metaFields.add(metaField);

        setTableLayout(populateTable(metaFields));
    }

}