package ie.aranearum.tela.veyron;

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

    /*
    When populating only unpopulated data, which is most of the time, reset countryXDate for each country
    to the last used date for that country an add data where the date is > countryXDate.

    Open the CSV, skip the first line, and for every line populate a new Beanie() and call
    addRecordRequest().
     */
    private static boolean populate(Context context, boolean buildFromScratch) { // all records
        LocalDate countryXDate = LocalDate.of(1966, 12, 25); // All entries must be from and including than this date
        String currentCountry = null;
        String previousCountry = null;



        try {
            String csvFileName = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
            FileReader filereader = new FileReader(csvFileName);
            //CSVReader csvReader = new CSVReader(filereader);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            String[] nextRecord;

            // Stop after the first 1000 entries
            //Integer i = 0; // for debugging
            while ((nextRecord = csvReader.readNext()) != null) {
                /*i++; // for debugging
                if (i > 100) // for debugging
                    break; // for debugging*/
                if (!buildFromScratch) {
                    ; // And this is a new country then get last updated date
                }
                Beanie beanie = new Beanie();
                beanie.iso_code = nextRecord[0];
                beanie.continent = nextRecord[1];
                beanie.location = nextRecord[2];
                beanie.date = nextRecord[3];
                beanie.total_cases = nextRecord[4].isEmpty() ? 0d:Double.parseDouble(nextRecord[4]);
                beanie.new_cases = nextRecord[5].isEmpty() ? 0d:Double.parseDouble(nextRecord[5]);
                beanie.new_cases_smoothed = nextRecord[6].isEmpty() ? 0d:Double.parseDouble(nextRecord[6]);
                beanie.total_deaths = nextRecord[7].isEmpty() ? 0d:Double.parseDouble(nextRecord[7]);
                beanie.new_deaths = nextRecord[8].isEmpty() ? 0d:Double.parseDouble(nextRecord[8]);
                beanie.new_deaths_smoothed = nextRecord[9].isEmpty() ? 0d:Double.parseDouble(nextRecord[9]);
                beanie.total_cases_per_million = nextRecord[10].isEmpty() ? 0d:Double.parseDouble(nextRecord[10]);
                beanie.new_cases_per_million = nextRecord[11].isEmpty() ? 0d:Double.parseDouble(nextRecord[11]);
                beanie.new_cases_smoothed_per_million = nextRecord[12].isEmpty() ? 0d:Double.parseDouble(nextRecord[12]);
                beanie.total_deaths_per_million = nextRecord[13].isEmpty() ? 0d:Double.parseDouble(nextRecord[13]);
                beanie.new_deaths_per_million = nextRecord[14].isEmpty() ? 0d:Double.parseDouble(nextRecord[14]);
                beanie.new_deaths_smoothed_per_million = nextRecord[15].isEmpty() ? 0d:Double.parseDouble(nextRecord[15]);
                beanie.reproduction_rate = nextRecord[16].isEmpty() ? 0d:Double.parseDouble(nextRecord[16]);
                beanie.icu_patients = nextRecord[17].isEmpty() ? 0:Double.parseDouble(nextRecord[17]);
                beanie.icu_patients_per_million = nextRecord[18].isEmpty() ? 0d:Double.parseDouble(nextRecord[18]);
                beanie.hosp_patients = nextRecord[19].isEmpty() ? 0:Double.parseDouble(nextRecord[19]);
                beanie.hosp_patients_per_million = nextRecord[20].isEmpty() ? 0d:Double.parseDouble(nextRecord[20]);
                beanie.weekly_icu_admissions = nextRecord[21].isEmpty() ? 0:Double.parseDouble(nextRecord[21]);
                beanie.weekly_icu_admissions_per_million = nextRecord[22].isEmpty() ? 0d:Double.parseDouble(nextRecord[22]);
                beanie.weekly_hosp_admissions = nextRecord[24].isEmpty() ? 0:Double.parseDouble(nextRecord[23]);
                beanie.weekly_hosp_admissions_per_million = nextRecord[24].isEmpty() ? 0d:Double.parseDouble(nextRecord[24]);
                beanie.new_tests = nextRecord[25].isEmpty() ? 0:Double.parseDouble(nextRecord[25]);
                beanie.total_tests = nextRecord[26].isEmpty() ? 0:Double.parseDouble(nextRecord[26]);
                beanie.total_tests_per_thousand = nextRecord[27].isEmpty() ? 0d:Double.parseDouble(nextRecord[27]);
                beanie.new_tests_per_thousand = nextRecord[28].isEmpty() ? 0d:Double.parseDouble(nextRecord[28]);
                beanie.new_tests_smoothed = nextRecord[29].isEmpty() ? 0:Double.parseDouble(nextRecord[29]);
                beanie.new_tests_smoothed_per_thousand = nextRecord[30].isEmpty() ? 0d:Double.parseDouble(nextRecord[30]);
                beanie.positive_rate = nextRecord[31].isEmpty() ? 0d:Double.parseDouble(nextRecord[31]);
                beanie.tests_per_case = nextRecord[32].isEmpty() ? 0d:Double.parseDouble(nextRecord[32]);
                beanie.tests_units = nextRecord[33];
                beanie.total_vaccinations = nextRecord[34].isEmpty() ? 0:Double.parseDouble(nextRecord[34]);
                beanie.people_vaccinated = nextRecord[35].isEmpty() ? 0:Double.parseDouble(nextRecord[35]);
                beanie.people_fully_vaccinated = nextRecord[36].isEmpty() ? 0:Double.parseDouble(nextRecord[36]);
                beanie.total_boosters = nextRecord[37].isEmpty() ? 0:Double.parseDouble(nextRecord[37]);
                beanie.new_vaccinations = nextRecord[38].isEmpty() ? 0:Double.parseDouble(nextRecord[38]);
                beanie.new_vaccinations_smoothed = nextRecord[39].isEmpty() ? 0:Double.parseDouble(nextRecord[39]);
                beanie.total_vaccinations_per_hundred = nextRecord[40].isEmpty() ? 0d:Double.parseDouble(nextRecord[40]);
                beanie.people_vaccinated_per_hundred = nextRecord[41].isEmpty() ? 0d:Double.parseDouble(nextRecord[41]);
                beanie.people_fully_vaccinated_per_hundred = nextRecord[42].isEmpty() ? 0d:Double.parseDouble(nextRecord[42]);
                beanie.total_boosters_per_hundred = nextRecord[43].isEmpty() ? 0d:Double.parseDouble(nextRecord[43]);
                beanie.new_vaccinations_smoothed_per_million = nextRecord[44].isEmpty() ? 0:Double.parseDouble(nextRecord[44]);
                beanie.new_people_vaccinated_smoothed = nextRecord[45].isEmpty() ? 0:Double.parseDouble(nextRecord[45]);
                beanie.new_people_vaccinated_smoothed_per_hundred = nextRecord[46].isEmpty() ? 0d:Double.parseDouble(nextRecord[46]);
                beanie.stringency_index = nextRecord[47].isEmpty() ? 0d:Double.parseDouble(nextRecord[47]);
                beanie.population = nextRecord[48].isEmpty() ? 0:Double.parseDouble(nextRecord[48]);
                beanie.population_density = nextRecord[49].isEmpty() ? 0d:Double.parseDouble(nextRecord[49]);
                beanie.median_age = nextRecord[50].isEmpty() ? 0:Double.parseDouble(nextRecord[50]);
                beanie.aged_65_older = nextRecord[51].isEmpty() ? 0d:Double.parseDouble(nextRecord[51]);
                beanie.aged_70_older = nextRecord[52].isEmpty() ? 0d:Double.parseDouble(nextRecord[52]);
                beanie.gdp_per_capita = nextRecord[53].isEmpty() ? 0d:Double.parseDouble(nextRecord[53]);
                beanie.extreme_poverty = nextRecord[54].isEmpty() ? 0d:Double.parseDouble(nextRecord[54]);
                beanie.cardiovasc_death_rate = nextRecord[55].isEmpty() ? 0d:Double.parseDouble(nextRecord[55]);
                beanie.diabetes_prevalence = nextRecord[56].isEmpty() ? 0d:Double.parseDouble(nextRecord[56]);
                beanie.female_smokers = nextRecord[57].isEmpty() ? 0d:Double.parseDouble(nextRecord[57]);
                beanie.male_smokers = nextRecord[58].isEmpty() ? 0d:Double.parseDouble(nextRecord[58]);
                beanie.handwashing_facilities = nextRecord[59].isEmpty() ? 0d:Double.parseDouble(nextRecord[59]);
                beanie.hospital_beds_per_thousand = nextRecord[60].isEmpty() ? 0d:Double.parseDouble(nextRecord[60]);
                beanie.life_expectancy = nextRecord[61].isEmpty() ? 0d:Double.parseDouble(nextRecord[61]);
                beanie.human_development_index = nextRecord[62].isEmpty() ? 0d:Double.parseDouble(nextRecord[62]);
                beanie.excess_mortality_cumulative_absolute = nextRecord[63].isEmpty() ? 0d:Double.parseDouble(nextRecord[63]);
                beanie.excess_mortality_cumulative = nextRecord[64].isEmpty() ? 0d:Double.parseDouble(nextRecord[64]);
                beanie.excess_mortality = nextRecord[65].isEmpty() ? 0d:Double.parseDouble(nextRecord[65]);
                beanie.excess_mortality_cumulative_per_million = nextRecord[66].isEmpty() ? 0d:Double.parseDouble(nextRecord[66]);

                if (!buildFromScratch) {
                    continue; // and the date <= countryXDate then continue to next iteration
                }
                boolean b = addRecordRequest(beanie);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private static boolean addRecordRequest(Beanie beanie) {
        return false;
    }

    private Integer addRegion(String Region) {
        Integer Id = 0;
        return Id;
    }

    private Integer addCountry(String Country) {
        Integer Id = 0;
        return Id;
    }

    private boolean addDetail() {
        String sql = "insert into Detail"
                + ",iso_code"
                + ",continent"
                + ",location"
                + ",date"
                + ",total_cases"
                + ",new_cases"
                + ",new_cases_smoothed"
                + ",total_deaths"
                + ",new_deaths"
                + ",new_deaths_smoothed"
                + ",total_cases_per_million"
                + ",new_cases_per_million"
                + ",new_cases_smoothed_per_million"
                + ",total_deaths_per_million"
                + ",new_deaths_per_million"
                + ",new_deaths_smoothed_per_million"
                + ",reproduction_rate"
                + ",icu_patients"
                + ",icu_patients_per_million"
                + ",hosp_patients"
                + ",hosp_patients_per_million"
                + ",weekly_icu_admissions"
                + ",weekly_icu_admissions_per_million"
                + ",weekly_hosp_admissions"
                + ",weekly_hosp_admissions_per_million"
                + ",new_tests"
                + ",total_tests"
                + ",total_tests_per_thousand"
                + ",new_tests_per_thousand"
                + ",new_tests_smoothed"
                + ",new_tests_smoothed_per_thousand"
                + ",positive_rate"
                + ",tests_per_case"
                + ",tests_units"
                + ",total_vaccinations"
                + ",people_vaccinated"
                + ",people_fully_vaccinated"
                + ",total_boosters"
                + ",new_vaccinations"
                + ",new_vaccinations_smoothed"
                + ",total_vaccinations_per_hundred"
                + ",people_vaccinated_per_hundred"
                + ",people_fully_vaccinated_per_hundred"
                + ",total_boosters_per_hundred"
                + ",new_vaccinations_smoothed_per_million"
                + ",new_people_vaccinated_smoothed"
                + ",new_people_vaccinated_smoothed_per_hundred"
                + ",stringency_index"
                + ",population"
                + ",population_density"
                + ",median_age"
                + ",aged_65_older"
                + ",aged_70_older"
                + ",gdp_per_capita"
                + ",extreme_poverty"
                + ",cardiovasc_death_rate"
                + ",diabetes_prevalence"
                + ",female_smokers"
                + ",male_smokers"
                + ",handwashing_facilities"
                + ",hospital_beds_per_thousand"
                + ",life_expectancy"
                + ",human_development_index"
                + ",excess_mortality_cumulative_absolute"
                + ",excess_mortality_cumulative"
                + ",excess_mortality"
                + ",excess_mortality_cumulative_per_million";
        return true;
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
    public String tests_units = null;
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




























