package ie.aranearum.tela.veyron;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

class SQLHelper extends SQLiteOpenHelper {
    public SQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private final String sqlTableRegion =
            "create table Region ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "continent TEXT NOT NULL"
                    + ");";
    private final String sqlTableCountry =
            "create table Country ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "FK_Region INT NOT NULL DEFAULT 0, "
                    + "iso_code TEXT NOT NULL, "
                    + "location TEXT NOT NULL, "
                    + "FOREIGN KEY (FK_Region) REFERENCES Region(Id)"
                    + ");";
    private final String sqlTableDetail =
            "create table Detail ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "FK_Country INT NOT NULL, "
                    + "iso_code TEXT NOT NULL, "
                    + "continent TEXT NOT NULL, "
                    + "location TEXT NOT NULL, "
                    + "date TEXT NOT NULL, "
                    + "total_cases INT NOT NULL, "
                    + "new_cases INT NOT NULL, "
                    + "new_cases_smoothed DECIMAL(10, 5) NOT NULL, "
                    + "total_deaths INT NOT NULL, "
                    + "new_deaths INT NOT NULL, "
                    + "new_deaths_smoothed DECIMAL(10, 5) NOT NULL, "
                    + "total_cases_per_million DECIMAL(10, 5) NOT NULL, "
                    + "new_cases_per_million DECIMAL(10, 5) NOT NULL, "
                    + "new_cases_smoothed_per_million DECIMAL(10, 5) NOT NULL, "
                    + "total_deaths_per_million DECIMAL(10, 5) NOT NULL, "
                    + "new_deaths_per_million DECIMAL(10, 5) NOT NULL, "
                    + "new_deaths_smoothed_per_million DECIMAL(10, 5) NOT NULL, "
                    + "reproduction_rate DECIMAL(10, 5) NOT NULL, "
                    + "icu_patients INT NOT NULL, "
                    + "icu_patients_per_million DECIMAL(10, 5) NOT NULL, "
                    + "hosp_patients INT NOT NULL, "
                    + "hosp_patients_per_million DECIMAL(10, 5) NOT NULL, "
                    + "weekly_icu_admissions INT NOT NULL, "
                    + "weekly_icu_admissions_per_million DECIMAL(10, 5) NOT NULL, "
                    + "weekly_hosp_admissions INT NOT NULL, "
                    + "weekly_hosp_admissions_per_million DECIMAL(10, 5) NOT NULL, "
                    + "new_tests INT NOT NULL, "
                    + "total_tests INT NOT NULL, "
                    + "total_tests_per_thousand DECIMAL(10, 5) NOT NULL, "
                    + "new_tests_per_thousand DECIMAL(10, 5) NOT NULL, "
                    + "new_tests_smoothed INT NOT NULL, "
                    + "new_tests_smoothed_per_thousand DECIMAL(10, 5) NOT NULL, "
                    + "positive_rate DECIMAL(10, 5) NOT NULL, "
                    + "tests_per_case DECIMAL(10, 5) NOT NULL, "
                    + "tests_units TEXT NOT NULL, "
                    + "total_vaccinations INT NOT NULL, "
                    + "people_vaccinated INT NOT NULL, "
                    + "people_fully_vaccinated INT NOT NULL, "
                    + "total_boosters INT NOT NULL, "
                    + "new_vaccinations INT NOT NULL, "
                    + "new_vaccinations_smoothed INT NOT NULL, "
                    + "total_vaccinations_per_hundred DECIMAL(10, 5) NOT NULL, "
                    + "people_vaccinated_per_hundred DECIMAL(10, 5) NOT NULL, "
                    + "people_fully_vaccinated_per_hundred DECIMAL(10, 5) NOT NULL, "
                    + "total_boosters_per_hundred DECIMAL(10, 5) NOT NULL, "
                    + "new_vaccinations_smoothed_per_million INT NOT NULL, "
                    + "new_people_vaccinated_smoothed INT NOT NULL, "
                    + "new_people_vaccinated_smoothed_per_hundred DECIMAL(10, 5) NOT NULL, "
                    + "stringency_index DECIMAL(10, 5) NOT NULL, "
                    + "population INT NOT NULL, "
                    + "population_density DECIMAL(10, 5) NOT NULL, "
                    + "median_age INT NOT NULL, "
                    + "aged_65_older DECIMAL(10, 5) NOT NULL, "
                    + "aged_70_older DECIMAL(10, 5) NOT NULL, "
                    + "gdp_per_capita DECIMAL(10, 5) NOT NULL, "
                    + "extreme_poverty DECIMAL(10, 5) NOT NULL, "
                    + "cardiovasc_death_rate DECIMAL(10, 5) NOT NULL, "
                    + "diabetes_prevalence DECIMAL(10, 5) NOT NULL, "
                    + "female_smokers DECIMAL(10, 5) NOT NULL, "
                    + "male_smokers DECIMAL(10, 5) NOT NULL, "
                    + "handwashing_facilities DECIMAL(10, 5) NOT NULL, "
                    + "hospital_beds_per_thousand DECIMAL(10, 5) NOT NULL, "
                    + "life_expectancy DECIMAL(10, 5) NOT NULL, "
                    + "human_development_index DECIMAL(10, 5) NOT NULL, "
                    + "excess_mortality_cumulative_absolute DECIMAL(10, 5) NOT NULL, "
                    + "excess_mortality_cumulative DECIMAL(10, 5) NOT NULL, "
                    + "excess_mortality DECIMAL(10, 5) NOT NULL, "
                    + "excess_mortality_cumulative_per_million DECIMAL(10, 5) NOT NULL, "
                    + "FOREIGN KEY (FK_Country) REFERENCES Country(Id)"
                    + ");";

    @Override
    public void onCreate(SQLiteDatabase db) throws SQLiteFullException {
        try {
            db.execSQL(sqlTableRegion);
            db.execSQL(sqlTableCountry);
            db.execSQL(sqlTableDetail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteFullException {
//    db.execSQL("DROP TABLE IF EXISTS " + tblRegion);
//    db.execSQL("DROP TABLE IF EXISTS " + tblCountry);
//    db.execSQL("DROP TABLE IF EXISTS " + sqlTableDetail);
//    onCreate(db);
    }
}

class Database {
    private Database() {
    }

    private static SQLiteDatabase instance = null;
    private static HashMap<String, Long> hashMapRegion = null;
    private static HashMap<String, Long> hashMapCountry = null;
    private static HashMap<String, LocalDate> hashMapCountryXDate = null;
    private static boolean buildFromScratch = false;

    public static SQLiteDatabase getInstance(Context context, boolean invalidate, boolean populate) {
        if (invalidate) {
            delete(context);
        }
        if (instance == null) {
            if (exists(context)) {
                File fPathDB = context.getDatabasePath(Constants.dbName);
                instance = SQLiteDatabase.openDatabase(fPathDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            } else {
                instance = new SQLHelper(context, Constants.dbName, null, 1).getWritableDatabase();
            }
        }
        if(invalidate || CSV.isCsvIsUpdated() || populate) {
            CSV.setCsvIsUpdated(false);
            populateRequest(context);
        }

        return instance;
    }

    @SuppressLint("Range")
    private static boolean populateRequest(Context context) {
        LocalDate DBDate = null;
        Long RegionId = 0L;
        Long CountryId = 0L;
        String Code = null;
        String Region = null;
        String Country = null;
        String strDate = null;
        hashMapCountry = new HashMap<String, Long>();
        hashMapRegion = new HashMap<String, Long>();
        hashMapCountryXDate = new HashMap<String, LocalDate>();
        try {
            String csvFileName = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
            FileReader filereader = new FileReader(csvFileName);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            String[] nextRecord;

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

            while ((nextRecord = csvReader.readNext()) != null) {
                Code = nextRecord[0];
                Region = nextRecord[1];
                Country = nextRecord[2].replace("'", "-");
                strDate = nextRecord[3];
                if(Region.isEmpty())
                    continue;
                if (hashMapRegion.containsKey(Region)) {
                    RegionId = hashMapRegion.get(Region);
                } else {
                    String sqlRegionExists = "select count(Id) as n from Region where continent = '#1'".replace("#1", Region);
                    Cursor cRegionExists = instance.rawQuery(sqlRegionExists, null);
                    cRegionExists.moveToFirst();
                    if(cRegionExists.getInt(cRegionExists.getColumnIndex("n")) == 0) {
                        RegionId = addRegion(Region);
                        hashMapRegion.put(Region, RegionId);
                    } else {
                        String sqlRegion = "select Id from Region where continent = '#1'".replace("#1", Region);
                        Cursor cRegion = instance.rawQuery(sqlRegion, null);
                        cRegion.moveToFirst();
                        RegionId = cRegion.getLong(cRegion.getColumnIndex("Id"));
                        hashMapRegion.put(Region, RegionId);
                    }
                }
                if (hashMapCountry.containsKey(Country)) {
                    CountryId = hashMapCountry.get(Country);
                } else {
                    String sqlCountryExists = "select count(Id) as n from Country where location = '#1'".replace("#1", Country);
                    Cursor cCountryExists = instance.rawQuery(sqlCountryExists, null);
                    cCountryExists.moveToFirst();
                    if(cCountryExists.getInt(cCountryExists.getColumnIndex("n")) == 0) {
                        CountryId = addCountry(Country, Code, RegionId);
                        hashMapCountry.put(Country, CountryId);
                    } else {
                        String sqlCountry = "select Id from Country where location = '#1'".replace("#1", Country);
                        Cursor cCountry = instance.rawQuery(sqlCountry, null);
                        cCountry.moveToFirst();
                        CountryId = cCountry.getLong(cCountry.getColumnIndex("Id"));
                        hashMapCountry.put(Country, CountryId);
                    }
                }
                if (hashMapCountryXDate.containsKey(Country)) {
                    DBDate = hashMapCountryXDate.get(Country);
                } else {
                    DBDate = getLastUpdate(Country);
                }

                // If the difference between the CSV & DB date is greater than Constants.backNDays then continue
                if(!Database.isBuildFromScratch()) {
                    LocalDate CSVDate = LocalDate.parse(strDate, dateTimeFormatter);
                    long difference = ChronoUnit.DAYS.between(DBDate, CSVDate);
                    if(Math.abs(difference) > Constants.backNDays)
                        continue;
                }

                Beanie beanie = new Beanie();
                beanie.iso_code = nextRecord[0];
                beanie.continent = nextRecord[1];
                beanie.location = nextRecord[2].replace("'", "-");
                beanie.date = nextRecord[3];
                beanie.total_cases = nextRecord[4].isEmpty() ? 0d : Double.parseDouble(nextRecord[4]);
                beanie.new_cases = nextRecord[5].isEmpty() ? 0d : Double.parseDouble(nextRecord[5]);
                beanie.new_cases_smoothed = nextRecord[6].isEmpty() ? 0d : Double.parseDouble(nextRecord[6]);
                beanie.total_deaths = nextRecord[7].isEmpty() ? 0d : Double.parseDouble(nextRecord[7]);
                beanie.new_deaths = nextRecord[8].isEmpty() ? 0d : Double.parseDouble(nextRecord[8]);
                beanie.new_deaths_smoothed = nextRecord[9].isEmpty() ? 0d : Double.parseDouble(nextRecord[9]);
                beanie.total_cases_per_million = nextRecord[10].isEmpty() ? 0d : Double.parseDouble(nextRecord[10]);
                beanie.new_cases_per_million = nextRecord[11].isEmpty() ? 0d : Double.parseDouble(nextRecord[11]);
                beanie.new_cases_smoothed_per_million = nextRecord[12].isEmpty() ? 0d : Double.parseDouble(nextRecord[12]);
                beanie.total_deaths_per_million = nextRecord[13].isEmpty() ? 0d : Double.parseDouble(nextRecord[13]);
                beanie.new_deaths_per_million = nextRecord[14].isEmpty() ? 0d : Double.parseDouble(nextRecord[14]);
                beanie.new_deaths_smoothed_per_million = nextRecord[15].isEmpty() ? 0d : Double.parseDouble(nextRecord[15]);
                beanie.reproduction_rate = nextRecord[16].isEmpty() ? 0d : Double.parseDouble(nextRecord[16]);
                beanie.icu_patients = nextRecord[17].isEmpty() ? 0 : Double.parseDouble(nextRecord[17]);
                beanie.icu_patients_per_million = nextRecord[18].isEmpty() ? 0d : Double.parseDouble(nextRecord[18]);
                beanie.hosp_patients = nextRecord[19].isEmpty() ? 0 : Double.parseDouble(nextRecord[19]);
                beanie.hosp_patients_per_million = nextRecord[20].isEmpty() ? 0d : Double.parseDouble(nextRecord[20]);
                beanie.weekly_icu_admissions = nextRecord[21].isEmpty() ? 0 : Double.parseDouble(nextRecord[21]);
                beanie.weekly_icu_admissions_per_million = nextRecord[22].isEmpty() ? 0d : Double.parseDouble(nextRecord[22]);
                beanie.weekly_hosp_admissions = nextRecord[24].isEmpty() ? 0 : Double.parseDouble(nextRecord[23]);
                beanie.weekly_hosp_admissions_per_million = nextRecord[24].isEmpty() ? 0d : Double.parseDouble(nextRecord[24]);
                beanie.new_tests = nextRecord[25].isEmpty() ? 0 : Double.parseDouble(nextRecord[25]);
                beanie.total_tests = nextRecord[26].isEmpty() ? 0 : Double.parseDouble(nextRecord[26]);
                beanie.total_tests_per_thousand = nextRecord[27].isEmpty() ? 0d : Double.parseDouble(nextRecord[27]);
                beanie.new_tests_per_thousand = nextRecord[28].isEmpty() ? 0d : Double.parseDouble(nextRecord[28]);
                beanie.new_tests_smoothed = nextRecord[29].isEmpty() ? 0 : Double.parseDouble(nextRecord[29]);
                beanie.new_tests_smoothed_per_thousand = nextRecord[30].isEmpty() ? 0d : Double.parseDouble(nextRecord[30]);
                beanie.positive_rate = nextRecord[31].isEmpty() ? 0d : Double.parseDouble(nextRecord[31]);
                beanie.tests_per_case = nextRecord[32].isEmpty() ? 0d : Double.parseDouble(nextRecord[32]);
                beanie.tests_units = nextRecord[33];
                beanie.total_vaccinations = nextRecord[34].isEmpty() ? 0 : Double.parseDouble(nextRecord[34]);
                beanie.people_vaccinated = nextRecord[35].isEmpty() ? 0 : Double.parseDouble(nextRecord[35]);
                beanie.people_fully_vaccinated = nextRecord[36].isEmpty() ? 0 : Double.parseDouble(nextRecord[36]);
                beanie.total_boosters = nextRecord[37].isEmpty() ? 0 : Double.parseDouble(nextRecord[37]);
                beanie.new_vaccinations = nextRecord[38].isEmpty() ? 0 : Double.parseDouble(nextRecord[38]);
                beanie.new_vaccinations_smoothed = nextRecord[39].isEmpty() ? 0 : Double.parseDouble(nextRecord[39]);
                beanie.total_vaccinations_per_hundred = nextRecord[40].isEmpty() ? 0d : Double.parseDouble(nextRecord[40]);
                beanie.people_vaccinated_per_hundred = nextRecord[41].isEmpty() ? 0d : Double.parseDouble(nextRecord[41]);
                beanie.people_fully_vaccinated_per_hundred = nextRecord[42].isEmpty() ? 0d : Double.parseDouble(nextRecord[42]);
                beanie.total_boosters_per_hundred = nextRecord[43].isEmpty() ? 0d : Double.parseDouble(nextRecord[43]);
                beanie.new_vaccinations_smoothed_per_million = nextRecord[44].isEmpty() ? 0 : Double.parseDouble(nextRecord[44]);
                beanie.new_people_vaccinated_smoothed = nextRecord[45].isEmpty() ? 0 : Double.parseDouble(nextRecord[45]);
                beanie.new_people_vaccinated_smoothed_per_hundred = nextRecord[46].isEmpty() ? 0d : Double.parseDouble(nextRecord[46]);
                beanie.stringency_index = nextRecord[47].isEmpty() ? 0d : Double.parseDouble(nextRecord[47]);
                beanie.population = nextRecord[48].isEmpty() ? 0 : Double.parseDouble(nextRecord[48]);
                beanie.population_density = nextRecord[49].isEmpty() ? 0d : Double.parseDouble(nextRecord[49]);
                beanie.median_age = nextRecord[50].isEmpty() ? 0 : Double.parseDouble(nextRecord[50]);
                beanie.aged_65_older = nextRecord[51].isEmpty() ? 0d : Double.parseDouble(nextRecord[51]);
                beanie.aged_70_older = nextRecord[52].isEmpty() ? 0d : Double.parseDouble(nextRecord[52]);
                beanie.gdp_per_capita = nextRecord[53].isEmpty() ? 0d : Double.parseDouble(nextRecord[53]);
                beanie.extreme_poverty = nextRecord[54].isEmpty() ? 0d : Double.parseDouble(nextRecord[54]);
                beanie.cardiovasc_death_rate = nextRecord[55].isEmpty() ? 0d : Double.parseDouble(nextRecord[55]);
                beanie.diabetes_prevalence = nextRecord[56].isEmpty() ? 0d : Double.parseDouble(nextRecord[56]);
                beanie.female_smokers = nextRecord[57].isEmpty() ? 0d : Double.parseDouble(nextRecord[57]);
                beanie.male_smokers = nextRecord[58].isEmpty() ? 0d : Double.parseDouble(nextRecord[58]);
                beanie.handwashing_facilities = nextRecord[59].isEmpty() ? 0d : Double.parseDouble(nextRecord[59]);
                beanie.hospital_beds_per_thousand = nextRecord[60].isEmpty() ? 0d : Double.parseDouble(nextRecord[60]);
                beanie.life_expectancy = nextRecord[61].isEmpty() ? 0d : Double.parseDouble(nextRecord[61]);
                beanie.human_development_index = nextRecord[62].isEmpty() ? 0d : Double.parseDouble(nextRecord[62]);
                beanie.excess_mortality_cumulative_absolute = nextRecord[63].isEmpty() ? 0d : Double.parseDouble(nextRecord[63]);
                beanie.excess_mortality_cumulative = nextRecord[64].isEmpty() ? 0d : Double.parseDouble(nextRecord[64]);
                beanie.excess_mortality = nextRecord[65].isEmpty() ? 0d : Double.parseDouble(nextRecord[65]);
                beanie.excess_mortality_cumulative_per_million = nextRecord[66].isEmpty() ? 0d : Double.parseDouble(nextRecord[66]);

                Long Id = addDetail(beanie, CountryId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setBuildFromScratch(false);
        return true;
    }

    public static boolean exists(@NonNull Context context) {
        File fPathDB = context.getDatabasePath(Constants.dbName);
        return fPathDB.exists();
    }

    private static boolean delete(@NonNull Context context) {
        File fPathDB = context.getDatabasePath(Constants.dbName);
        if (fPathDB.exists()) {
            return fPathDB.delete();
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    private static LocalDate getLastUpdate(String Country) {
        Long Id = hashMapCountry.get(Country);
        String sqlCheckForRecords = "select count(Id) as n from Detail where FK_Country = " + Id.toString();
        Cursor cCount = instance.rawQuery(sqlCheckForRecords, null);
        cCount.moveToFirst();
        if (cCount.getInt(cCount.getColumnIndex("n")) == 0) {
            return LocalDate.of(1966, 12, 25);
        } else {
            String sql = "select max(date) as date from Detail where FK_Country = " + Id.toString();
            Cursor cMaxDate = instance.rawQuery(sql, null);
            cMaxDate.moveToFirst();
            String strDate = cMaxDate.getString(cMaxDate.getColumnIndex("date"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
            LocalDate localDate = LocalDate.parse(strDate, dateTimeFormatter);
            hashMapCountryXDate.put(Country, localDate);
            return localDate;
        }
    }

    private static Long addRegion(@NonNull String Region) {
        ContentValues values = new ContentValues();
        values.put("continent", Region);
        Long Id = null;
        try {
            Id = instance.insert("Region", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    private static Long addCountry(@NonNull String Country, @NonNull String Code, @Nullable Long RegionId) {
        ContentValues values = new ContentValues();
        values.put("FK_Region", RegionId);
        values.put("location", Country);
        values.put("iso_code", Code);
        Long Id = null;
        try {
            Id = instance.insert("Country", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    private static Long addDetail(Beanie beanie, Long FK_Country) {
        ContentValues values = new ContentValues();
        values.put("FK_Country", FK_Country);
        values.put("iso_code", beanie.iso_code);
        values.put("continent", beanie.continent);
        values.put("location", beanie.location);
        values.put("date", beanie.date);
        values.put("total_cases", beanie.total_cases);
        values.put("new_cases", beanie.new_cases);
        values.put("new_cases_smoothed", beanie.new_cases_smoothed);
        values.put("total_deaths", beanie.total_deaths);
        values.put("new_deaths", beanie.new_deaths);
        values.put("new_deaths_smoothed", beanie.new_deaths_smoothed);
        values.put("total_cases_per_million", beanie.total_cases_per_million);
        values.put("new_cases_per_million", beanie.new_cases_per_million);
        values.put("new_cases_smoothed_per_million", beanie.new_cases_smoothed_per_million);
        values.put("total_deaths_per_million", beanie.total_deaths_per_million);
        values.put("new_deaths_per_million", beanie.new_deaths_per_million);
        values.put("new_deaths_smoothed_per_million", beanie.new_deaths_smoothed_per_million);
        values.put("reproduction_rate", beanie.reproduction_rate);
        values.put("icu_patients", beanie.icu_patients);
        values.put("icu_patients_per_million", beanie.icu_patients_per_million);
        values.put("hosp_patients", beanie.hosp_patients);
        values.put("hosp_patients_per_million", beanie.hosp_patients_per_million);
        values.put("weekly_icu_admissions", beanie.weekly_icu_admissions);
        values.put("weekly_icu_admissions_per_million", beanie.weekly_icu_admissions_per_million);
        values.put("weekly_hosp_admissions", beanie.weekly_hosp_admissions);
        values.put("weekly_hosp_admissions_per_million", beanie.weekly_hosp_admissions_per_million);
        values.put("new_tests", beanie.new_tests);
        values.put("total_tests", beanie.total_tests);
        values.put("total_tests_per_thousand", beanie.total_tests_per_thousand);
        values.put("new_tests_per_thousand", beanie.new_tests_per_thousand);
        values.put("new_tests_smoothed", beanie.new_tests_smoothed);
        values.put("new_tests_smoothed_per_thousand", beanie.new_tests_smoothed_per_thousand);
        values.put("positive_rate", beanie.positive_rate);
        values.put("tests_per_case", beanie.tests_per_case);
        values.put("tests_units", beanie.tests_units);
        values.put("total_vaccinations", beanie.total_vaccinations);
        values.put("people_vaccinated", beanie.people_vaccinated);
        values.put("people_fully_vaccinated", beanie.people_fully_vaccinated);
        values.put("total_boosters", beanie.total_boosters);
        values.put("new_vaccinations", beanie.new_vaccinations);
        values.put("new_vaccinations_smoothed", beanie.new_vaccinations_smoothed);
        values.put("total_vaccinations_per_hundred", beanie.total_vaccinations_per_hundred);
        values.put("people_vaccinated_per_hundred", beanie.people_vaccinated_per_hundred);
        values.put("people_fully_vaccinated_per_hundred", beanie.people_fully_vaccinated_per_hundred);
        values.put("total_boosters_per_hundred", beanie.total_boosters_per_hundred);
        values.put("new_vaccinations_smoothed_per_million", beanie.new_vaccinations_smoothed_per_million);
        values.put("new_people_vaccinated_smoothed", beanie.new_people_vaccinated_smoothed);
        values.put("new_people_vaccinated_smoothed_per_hundred", beanie.new_people_vaccinated_smoothed_per_hundred);
        values.put("stringency_index", beanie.stringency_index);
        values.put("population", beanie.population);
        values.put("population_density", beanie.population_density);
        values.put("median_age", beanie.median_age);
        values.put("aged_65_older", beanie.aged_65_older);
        values.put("aged_70_older", beanie.aged_70_older);
        values.put("gdp_per_capita", beanie.gdp_per_capita);
        values.put("extreme_poverty", beanie.extreme_poverty);
        values.put("cardiovasc_death_rate", beanie.cardiovasc_death_rate);
        values.put("diabetes_prevalence", beanie.diabetes_prevalence);
        values.put("female_smokers", beanie.female_smokers);
        values.put("male_smokers", beanie.male_smokers);
        values.put("handwashing_facilities", beanie.handwashing_facilities);
        values.put("hospital_beds_per_thousand", beanie.hospital_beds_per_thousand);
        values.put("life_expectancy", beanie.life_expectancy);
        values.put("human_development_index", beanie.human_development_index);
        values.put("excess_mortality_cumulative_absolute", beanie.excess_mortality_cumulative_absolute);
        values.put("excess_mortality_cumulative", beanie.excess_mortality_cumulative);
        values.put("excess_mortality", beanie.excess_mortality);
        values.put("excess_mortality_cumulative_per_million", beanie.excess_mortality_cumulative_per_million);

        Long Id = null;
        try {
            if(isBuildFromScratch()) {
                Id = instance.insert("Detail", null, values);
            } else {
                String sqlWhereFragment = "FK_Country = #1 and date = '#2'";
                sqlWhereFragment = sqlWhereFragment.replace("#1", String.valueOf(FK_Country));
                sqlWhereFragment = sqlWhereFragment.replace("#2", beanie.date);
                int nRows = instance.update("Detail", values, sqlWhereFragment, null);
                if(nRows == 0)
                    Id = instance.insert("Detail", null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Id;
    }

    public static boolean isBuildFromScratch() {
        return buildFromScratch;
    }

    public static void setBuildFromScratch(boolean buildFromScratch) {
        Database.buildFromScratch = buildFromScratch;
    }
}

class Beanie {
    public String iso_code = null;
    public String continent = null;
    public String location = null;
    public String date = null;
    public Double total_cases = 0d;
    public Double new_cases = 0d;
    public Double new_cases_smoothed = 0d;
    public Double total_deaths = 0d;
    public Double new_deaths = 0d;
    public Double new_deaths_smoothed = 0d;
    public Double total_cases_per_million = 0d;
    public Double new_cases_per_million = 0d;
    public Double new_cases_smoothed_per_million = 0d;
    public Double total_deaths_per_million = 0d;
    public Double new_deaths_per_million = 0d;
    public Double new_deaths_smoothed_per_million = 0d;
    public Double reproduction_rate = 0d;
    public Double icu_patients = 0d;
    public Double icu_patients_per_million = 0d;
    public Double hosp_patients = 0d;
    public Double hosp_patients_per_million = 0d;
    public Double weekly_icu_admissions = 0d;
    public Double weekly_icu_admissions_per_million = 0d;
    public Double weekly_hosp_admissions = 0d;
    public Double weekly_hosp_admissions_per_million = 0d;
    public Double new_tests = 0d;
    public Double total_tests = 0d;
    public Double total_tests_per_thousand = 0d;
    public Double new_tests_per_thousand = 0d;
    public Double new_tests_smoothed = 0d;
    public Double new_tests_smoothed_per_thousand = 0d;
    public Double positive_rate = 0d;
    public Double tests_per_case = 0d;
    public String tests_units = null; // for country data only...
    public Double total_vaccinations = 0d;
    public Double people_vaccinated = 0d;
    public Double people_fully_vaccinated = 0d;
    public Double total_boosters = 0d;
    public Double new_vaccinations = 0d;
    public Double new_vaccinations_smoothed = 0d;
    public Double total_vaccinations_per_hundred = 0d;
    public Double people_vaccinated_per_hundred = 0d;
    public Double people_fully_vaccinated_per_hundred = 0d;
    public Double total_boosters_per_hundred = 0d;
    public Double new_vaccinations_smoothed_per_million = 0d;
    public Double new_people_vaccinated_smoothed = 0d;
    public Double new_people_vaccinated_smoothed_per_hundred = 0d;
    public Double stringency_index = 0d;
    public Double population = 0d;
    public Double population_density = 0d;
    public Double median_age = 0d;
    public Double aged_65_older = 0d;
    public Double aged_70_older = 0d;
    public Double gdp_per_capita = 0d;
    public Double extreme_poverty = 0d;
    public Double cardiovasc_death_rate = 0d;
    public Double diabetes_prevalence = 0d;
    public Double female_smokers = 0d;
    public Double male_smokers = 0d;
    public Double handwashing_facilities = 0d;
    public Double hospital_beds_per_thousand = 0d;
    public Double life_expectancy = 0d;
    public Double human_development_index = 0d;
    public Double excess_mortality_cumulative_absolute = 0d;
    public Double excess_mortality_cumulative = 0d;
    public Double excess_mortality = 0d;
    public Double excess_mortality_cumulative_per_million = 0d;
}




























