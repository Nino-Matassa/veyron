package ie.aranearum.tela.veyron;

public class Constants {
    public static final String OWIDCSV = "https://covid.ourworldindata.org/data/owid-covid-data.csv";
    public static final String NameCSV = "OWID.csv";
    public static final String dbName = "OWID.db";
    public static final String roman1000000 = "M\u0305";
    public static final String roman1000 = "M";
    public static final String percent = "%";
    public static final String rNought = "R\u2080";
    public static final String house = "\u2302";
    public static final String copyright = "\u00A9";
    public static final String smoothed = "\u03D0"; // Use \beta to show smoothed data
    public static final int abbreviate = 99;//15; Needs to be based on dynamic column width...
    public static final int delayMilliSeconds = 1000;
    public static final String roman100000 = "C\u0305"; // Roman numeral for 100,000
    public static final Integer backNDays = 21;
    public static final String beta = "\u03D0";
    public static final String delta = "\u03B4";

    // UI translations
    public static final String UITerra = "UITerra";
    public static final String UIRegion = "UIRegion";
    public static final String UICountry = "UICountry";
    public static final String UICountryX = "UICountryX";
    public static final String UIFieldXHistory = "UIFieldXHistory";

    // Database field name translations...
    public static final String iso_code = "Code";
    public static final String continent = "Region";
    public static final String location = "Country";
    public static final String date = "Date";
    public static final String total_cases = "Total Cases";
    public static final String new_cases = "New Cases";
    public static final String new_cases_smoothed = "New Cases" + smoothed;
    public static final String total_deaths = "Total Deaths";
    public static final String new_deaths = "New Deaths";
    public static final String new_deaths_smoothed = "New Deaths" + smoothed;
    public static final String total_cases_per_million = "";
    public static final String new_cases_per_million = "";
    public static final String new_cases_smoothed_per_million = "";
    public static final String total_deaths_per_million = "";
    public static final String new_deaths_per_million = "";
    public static final String new_deaths_smoothed_per_million = "";
    public static final String reproduction_rate = "";
    public static final String icu_patients = "";
    public static final String icu_patients_per_million = "";
    public static final String hosp_patients = "";
    public static final String hosp_patients_per_million = "";
    public static final String weekly_icu_admissions = "";
    public static final String weekly_icu_admissions_per_million = "";
    public static final String weekly_hosp_admissions = "";
    public static final String weekly_hosp_admissions_per_million = "";
    public static final String new_tests = "";
    public static final String total_tests = "";
    public static final String total_tests_per_thousand = "";
    public static final String new_tests_per_thousand = "";
    public static final String new_tests_smoothed = "";
    public static final String new_tests_smoothed_per_thousand = "";
    public static final String positive_rate = "";
    public static final String tests_per_case = "";
    public static final String tests_units = "";
    public static final String total_vaccinations = "";
    public static final String people_vaccinated = "";
    public static final String people_fully_vaccinated = "";
    public static final String total_boosters = "";
    public static final String new_vaccinations = "";
    public static final String new_vaccinations_smoothed = "";
    public static final String total_vaccinations_per_hundred = "";
    public static final String people_vaccinated_per_hundred = "";
    public static final String people_fully_vaccinated_per_hundred = "";
    public static final String total_boosters_per_hundred = "";
    public static final String new_vaccinations_smoothed_per_million = "";
    public static final String new_people_vaccinated_smoothed = "";
    public static final String new_people_vaccinated_smoothed_per_hundred = "";
    public static final String stringency_index = "";
    public static final String population = "";
    public static final String population_density = "";
    public static final String median_age = "";
    public static final String aged_65_older = "";
    public static final String aged_70_older = "";
    public static final String gdp_per_capita = "";
    public static final String extreme_poverty = "";
    public static final String cardiovasc_death_rate = "";
    public static final String diabetes_prevalence = "";
    public static final String female_smokers = "";
    public static final String male_smokers = "";
    public static final String handwashing_facilities = "";
    public static final String hospital_beds_per_thousand = "";
    public static final String life_expectancy = "";
    public static final String human_development_index = "";
    public static final String excess_mortality_cumulative_absolute = "";
    public static final String excess_mortality_cumulative = "";
    public static final String excess_mortality = "";
    public static final String excess_mortality_cumulative_per_million = "";

    public static enum FieldXHistoryType {
        DateAndField,
        CountryAndField,
        PercentInfected,
        PercentInfectedTerra
    }
}
