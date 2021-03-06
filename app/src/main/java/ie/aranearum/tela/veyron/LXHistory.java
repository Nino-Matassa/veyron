package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class LXHistory {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
    private MetaField metaField = null;
    private ILambdaXHistory ILambdaXHistory;
    private DecimalFormat formatter = new DecimalFormat("#,###.##");
    private SQLiteDatabase db = null;
    private Context context = null;

    public LXHistory(Context context) {
        this.context = context;
        db = Database.getInstance(context);
    }

    public ILambdaXHistory defineLambda(@NonNull Constants.FieldXHistoryType fieldXType, String fieldName,
                                        Long RegionId, Long CountryId, String Region, String Country) {
        switch (fieldXType) {
            case DateAndField:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlFieldXHistory = "select date, fieldX from Detail where FK_Country = '#1' order by date desc";
                    sqlFieldXHistory = sqlFieldXHistory.replace("#1", String.valueOf(CountryId));
                    sqlFieldXHistory = sqlFieldXHistory.replace("fieldX", fieldName);
                    Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                    cFieldXHistory.moveToFirst();

                    do {
                        String strDate = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("date"));
                        strDate = LocalDate.parse(strDate, dateTimeFormatter).toString();
                        strDate += " : " + LocalDate.parse(strDate, dateTimeFormatter).getDayOfWeek().toString().substring(0, 3);
                        Double fieldXValue = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex(fieldName));
                        metaField = new MetaField(RegionId, CountryId, Constants.UIFieldXHistory);
                        metaField.region = Region;
                        metaField.country = Country;
                        metaField.key = strDate;
                        metaField.value = String.valueOf(formatter.format(fieldXValue));
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());

                    return metaFields;
                };
                break;
            case CountryAndField:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlCountryList = "select distinct FK_Country from Detail where #1 > 0";
                    sqlCountryList = sqlCountryList.replace("#1", fieldName);
                    Cursor cCountryList = db.rawQuery(sqlCountryList, null);
                    cCountryList.moveToFirst();

                    do {
                        String sqlFieldXHistory = "select Detail.location, #1, Country.FK_Region, FK_Country, continent, date from Detail\n" +
                                "join Country on Country.id = Detail.FK_Country\n" +
                                "where #1 > 0 and FK_Country = #2\n" +
                                "order by date DESC\n" +
                                "limit 1";
                        sqlFieldXHistory = sqlFieldXHistory.replace("#1", fieldName);
                        sqlFieldXHistory = sqlFieldXHistory.replace("#2", String.valueOf(cCountryList.getLong(cCountryList.getColumnIndex("FK_Country"))));
                        Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                        cFieldXHistory.moveToFirst();
                        Double fieldXValue = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex(fieldName));
                        metaField = new MetaField(RegionId, CountryId, Constants.UICountryX);
                        metaField.region = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("continent"));
                        metaField.country = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("location"));
                        metaField.key = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("location"));
                        metaField.value = String.valueOf(formatter.format(fieldXValue));
                        metaField.regionId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Region"));
                        metaField.countryId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Country"));
                        metaField.underlineKey = true;
                        metaFields.add(metaField);
                    } while (cCountryList.moveToNext());
                    metaFields.sort(new sortStats());
                    return metaFields;
                };
                break;
            case PercentInfected:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlFieldXHistory = "select date, total_cases, population from Detail where FK_Country = '#1' and total_cases > 0 order by date desc";
                    sqlFieldXHistory = sqlFieldXHistory.replace("#1", String.valueOf(CountryId));
                    Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                    cFieldXHistory.moveToFirst();
                    Double Population = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("population"));
                    do {
                        String strDate = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("date"));
                        strDate = LocalDate.parse(strDate, dateTimeFormatter).toString();
                        strDate += " : " + LocalDate.parse(strDate, dateTimeFormatter).getDayOfWeek().toString().substring(0, 3);
                        Double TotalCase = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("total_cases"));
                        metaField = new MetaField(RegionId, CountryId, Constants.UICountryX);
                        metaField.region = Region;
                        metaField.country = Country;
                        metaField.key = strDate;
                        metaField.value = String.valueOf(formatter.format(TotalCase / Population * 100));
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());

                    return metaFields;
                };
                break;
            case PercentInfectedTerra:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlFieldXHistory = "select continent, Country.location, Country.FK_Region, FK_Country, total_cases, population\n" +
                            " from Detail join Country on Detail.FK_Country = Country.Id\n" +
                            " where date = (select max(date) from Detail where total_cases > 0)";
                    Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                    cFieldXHistory.moveToFirst();
                    do {
                        String region = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("continent"));
                        String country = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("location"));
                        Double TotalCase = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("total_cases"));
                        Double Population = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("population"));
                        Long regionId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Region"));
                        Long countryId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Country"));
                        metaField = new MetaField(regionId, countryId, Constants.UICountryX);
                        metaField.underlineKey = true;
                        metaField.region = region;
                        metaField.country = country;
                        metaField.key = country;
                        metaField.value = String.valueOf(formatter.format(TotalCase / Population * 100));
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());
                    metaFields.sort(new sortStats());
                    return metaFields;
                };
                break;
        }
        return ILambdaXHistory;
    }
}

class sortStats implements Comparator<MetaField>
{
    @Override
    public int compare(MetaField mfA, MetaField mfB) {
        Double dA = Double.parseDouble(mfA.value.replace(",", ""));
        Double dB = Double.parseDouble(mfB.value.replace(",", ""));
        return dB.compareTo(dA);
    }
}
