package ie.aranearum.tela.veyron;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LXHistory {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
    private MetaField metaField = null;
    private ILambdaXHistory ILambdaXHistory;
    private DecimalFormat formatter = new DecimalFormat("#,###.##");
    private SQLiteDatabase db = null;
    private Context context = null;

    public LXHistory(Context context) {
        this.context = context;
        db = Database.getInstance(context, false, false);
    }

    public ILambdaXHistory defineLambda(Constants.FieldXHistoryType fieldXType, String fieldName,
                              Long RegionId, Long CountryId, String Region, String Country,
                              String executeSQL) {
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
                    String sqlFieldXHistory = executeSQL;
                    Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                    cFieldXHistory.moveToFirst();

                    do {
                        Double fieldXValue = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex(fieldName));
                        metaField = new MetaField(RegionId, CountryId, Constants.UICountryX);
                        metaField.region = Region;
                        metaField.country = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("location"));
                        metaField.key = cFieldXHistory.getString(cFieldXHistory.getColumnIndex("location"));
                        metaField.value = String.valueOf(formatter.format(fieldXValue));
                        metaField.regionId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Region"));
                        metaField.countryId = cFieldXHistory.getLong(cFieldXHistory.getColumnIndex("FK_Country"));
                        metaField.underlineKey = true;
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());

                    return metaFields;
                };
/*case PercentInfectedCountryX:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlFieldXHistory = executeSQL;
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
                        metaField.value = String.valueOf(formatter.format(TotalCase/Population*100));
                        metaField.regionId = RegionId;
                        metaField.countryId = CountryId;
                        metaField.underlineKey = false;
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());

                    return metaFields;
                };

case PercentInfectedTerra:
                ILambdaXHistory = () -> {
                    ArrayList<MetaField> metaFields = new ArrayList<MetaField>();
                    String sqlFieldXHistory = executeSQL;
                    Cursor cFieldXHistory = db.rawQuery(sqlFieldXHistory, null);
                    cFieldXHistory.moveToFirst();
                    Double Population = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("population"));

                    do {
                        Double TotalCase = cFieldXHistory.getDouble(cFieldXHistory.getColumnIndex("total_cases"));

                        metaField = new MetaField(RegionId, CountryId, Constants.UICountryX);
                        metaField.region = Region;
                        metaField.country = Country;
                        metaField.key = Country;
                        metaField.value = String.valueOf(formatter.format(TotalCase/Population*100));
                        metaField.regionId = RegionId;
                        metaField.countryId = CountryId;
                        metaField.underlineKey = false;
                        metaFields.add(metaField);
                    } while (cFieldXHistory.moveToNext());

                    return metaFields;
                };*/

                break;
        }
        return ILambdaXHistory;
    }
}
