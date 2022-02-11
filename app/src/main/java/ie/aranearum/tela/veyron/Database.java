package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.time.LocalDate;

class SQLHelper extends SQLiteOpenHelper {
    public SQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private final String sqlTableRegion =
            "create table Region ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "Region TEXT NOT NULL"
                    + ");";
    private final String sqlTableCountry =
            "create table Country ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "FK_Region INT NOT NULL, "
                    + "Country TEXT NOT NULL, "
                    + "FOREIGN KEY (FK_Region) REFERENCES Region(Id)"
                    + ");";
    private final String sqlTableDetail =
            "create table Detail ("
                    + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "FK_Country INT NOT NULL, "
                    + "Date TEXT NOT NULL, "
                    + "Code TEXT NOT NULL, "
                    + "Country TEXT NOT NULL, "
                    + "Region TEXT NOT NULL, "
                    + "NewCase INT NOT NULL, "
                    + "TotalCase INT NOT NULL, "
                    + "NewDeath INT NOT NULL, "
                    + "TotalDeath INT NOT NULL, "
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
    private static boolean dbHasContent = true;

    public static SQLiteDatabase getInstance(Context context, boolean invalidate) {
        if (invalidate) {
            delete(context);
        }
        File fPathDB = null;
        if (instance == null) {
            if (exists(context)) {
                fPathDB = context.getDatabasePath(Constants.dbName);
                instance = SQLiteDatabase.openDatabase(fPathDB.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            } else {
                instance = new SQLHelper(context, null, null, 1).getWritableDatabase();
                dbHasContent = false;
            }
        }
        populateRequest(context);
        return instance;
    }

    private static boolean populate(Context context, boolean buildFromScratch) { // all records
        LocalDate countryXDate = LocalDate.of(1966, 12, 25); // All entries must be from and including than this date
        String currentCountry = null;
        String previousCountry = null;
        if (!buildFromScratch) { // then set the date to the last date updated for that country
            ; // countryXDate = ?
        }
        return true;
    }

    private static boolean populateUnpopulated(Context context) { // some records
        populate(context, false);
        return true;
    }

    private static boolean populateRequest(Context context) {
        // unless it's a new build call populateUnpopulated regardless and let it do nothing
        if (dbHasContent) {
            populateUnpopulated(context);
        } else {
            populate(context, true);
            dbHasContent = true;
        }
        return true;
    }

    private static boolean exists(@NonNull Context context) {
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

    private boolean addRecordRequest() {
        return false;
    }

    private boolean addRegion() {
        return true;
    }

    private boolean addCountry() {
        return true;
    }

    private boolean addDetail() {
        return true;
    }
}

class Beanie {
    public String iso_code = null;
    public String continent = null;
    public String location = null;
    public String date = null;
    public Integer total_cases = 0;
    public Integer new_cases = 0;
    public Double new_cases_smoothed = 0d;
    public Integer total_deaths = 0;
    public Integer new_deaths = 0;
    public Double new_deaths_smoothed = 0d;
    public Double total_cases_per_million = 0d;
    public Double new_cases_per_million = 0d;
    public Double new_cases_smoothed_per_million = 0d;
    public Double total_deaths_per_million = 0d;
    public Double new_deaths_per_million = 0d;
    public Double new_deaths_smoothed_per_million = 0d;
    public Double reproduction_rate = 0d;
    public Integer icu_patients = 0;
    public Double icu_patients_per_million = 0d;
    public Integer hosp_patients = 0;
    public Double hosp_patients_per_million = 0d;
    public Integer weekly_icu_admissions = 0;
    public Double weekly_icu_admissions_per_million = 0d;
    public Integer weekly_hosp_admissions = 0;
    public Double weekly_hosp_admissions_per_million = 0d;
    public Integer new_tests = 0;
    public Integer total_tests = 0;
    public Double total_tests_per_thousand = 0d;
    public Double new_tests_per_thousand = 0d;
    public Integer new_tests_smoothed = 0;
    public Double new_tests_smoothed_per_thousand = 0d;
    public Double positive_rate = 0d;
    public Double tests_per_case = 0d;
    public String tests_units = null;
    public Integer total_vaccinations = 0;
    public Integer people_vaccinated = 0;
    public Integer people_fully_vaccinated = 0;
    public Integer total_boosters = 0;
    public Integer new_vaccinations = 0;
    public Integer new_vaccinations_smoothed = 0;
    public Double total_vaccinations_per_hundred = 0d;
    public Double people_vaccinated_per_hundred = 0d;
    public Double people_fully_vaccinated_per_hundred = 0d;
    public Double total_boosters_per_hundred = 0d;
    public Integer new_vaccinations_smoothed_per_million = 0;
    public Integer new_people_vaccinated_smoothed = 0;
    public Double new_people_vaccinated_smoothed_per_hundred = 0d;
    public Double stringency_index = 0d;
    public Integer population = 0;
    public Double population_density = 0d;
    public Integer median_age = 0;
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




























