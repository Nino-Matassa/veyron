package ie.aranearum.tela.veyron;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.DecimalFormat;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UI {
    private Context context = null;
    private String UIX = null;

    private TableLayout tableLayout = null;
    private TableLayout tableLayoutHeader = null;
    private TableLayout tableLayoutFooter = null;
    protected SQLiteDatabase db = null;
    private Vibrator vibrator = null;

    public UI(Context context, String UIX) {
        this.context = context;
        this.UIX = UIX;

        setTitlebar();
//        if (UIX.equals(Constants.UITerra)) {
//            MainActivity.stack.clear();
//        }

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;
        vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)); // guess work

        db = Database.getInstance(context, false, false);
        ((Activity)context).setContentView(R.layout.ui_table);

        tableLayout = (TableLayout) ((Activity)context).findViewById(R.id.layoutTable);
        tableLayoutHeader = (TableLayout)((Activity)context).findViewById(R.id.layoutTableHeader);
        tableLayoutFooter = (TableLayout)((Activity)context).findViewById(R.id.layoutTableFooter);

        String filePath = context.getFilesDir().getPath().toString() + "/" + Constants.NameCSV;
        File csv = new File(filePath);
        String lastUpdated = new Date(csv.lastModified()).toString();
        String[] aDate = lastUpdated.split("-");
        LocalDate localDate = LocalDate.of(Integer.valueOf(aDate[0]), Integer.valueOf(aDate[1]), Integer.valueOf(aDate[2]));
        lastUpdated = localDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        setFooter(lastUpdated);
        //UIMessage.informationBox(context, null);
    }

    private void setTitlebar() {
        switch(UIX) {
            case Constants.UITerra:
                ((Activity)context).setTitle("Veyron - Terra");
                break;
            case Constants.UIRegion:
                ((Activity)context).setTitle("Veyron - Regions");
                break;
            case Constants.UICountry:
                ((Activity)context).setTitle("Veyron - Region");
                break;
            case Constants.UICountryX:
                ((Activity)context).setTitle("Veyron - Country Details");
                break;
            case Constants.UIFieldXHistory:
                ((Activity)context).setTitle("Veyron - Field X");
                break;
            default:
            ((Activity)context).setTitle("Veyron");
        }
    }

    protected ArrayList<TableRow> populateTable(ArrayList<MetaField> metaFields) {
        ArrayList<TableRow> tableRows = new ArrayList<TableRow>();
        boolean bColourSwitch = true;
        for (final MetaField metaField: metaFields) {
            TableRow tableRow = new TableRow(context);
            LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowParams);

            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
            cellParams.weight = 9;
            TextView textViewKey = new TextView(context);
            textViewKey.setTextSize(18);
            TextView textViewValue = new TextView(context);
            textViewKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(metaField.underlineKey) {
                        if (metaField.UI.equals(Constants.UIRegion)) {
                            new UIRegion(context);
                        }
                        if(metaField.UI.equals(Constants.UICountry)) {
                            new UICountry(context, metaField.regionId);
                        }
                        if(metaField.UI.equals(Constants.UICountryX)) {
                            new UICountryX(context, metaField.countryId);
                        }
                        if(metaField.UI.equals(Constants.UIFieldXHistory)) {
                            defineLambda(metaField.fieldXHistoryType, metaField.fieldXName, metaField.regionId, metaField.countryId,
                                    metaField.region, metaField.country, metaField.executeSQL);
                            new UIFieldXHistory(context, metaField.regionId, metaField.countryId, metaField.region, metaField.country,
                                    ILambdaXHistory, metaField.fieldXName, metaField.executeSQL);
                        }
                    }
                }
            });
            textViewValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (metaField.underlineValue) {

                    }
                    /*if (metaField.underlineValue) {
                        *//*metaField.UI is always set to lhs, so when control arrives here the rhs field has been clicked*//*
                        if (metaField.UI.equals(Constants.UITerraRNought)) {
                            new UIRHSTerraRNought(context, metaField.regionId, metaField.countryId);
                        }
                        if (metaField.UI.equals(Constants.UITerraActiveCases)) {
                            new UIRHSTerraActiveCases(context, metaField.regionId, metaField.countryId);
                        }
                        if (metaField.UI.equals(Constants.UITerraActiveCasesPerX)) {
                            new UIRHSTerraActiveCasesPerX(context, metaField.regionId, metaField.countryId);
                        }
                    }*/
                }
            });
            textViewValue.setTextSize(18);
            textViewKey.setLayoutParams(cellParams);
            textViewValue.setLayoutParams(cellParams);
            textViewKey.setText(metaField.key);
            textViewValue.setText(metaField.value);
            tableRow.addView(textViewKey);
            tableRow.addView(textViewValue);

            if (metaField.underlineKey)
                textViewKey.setPaintFlags(textViewKey.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            if (metaField.underlineValue)
                textViewValue.setPaintFlags(textViewValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            if (bColourSwitch) {
                bColourSwitch = !bColourSwitch;
                tableRow.setBackgroundColor(Color.parseColor("#F7FAFD"));
            }
            else {
                bColourSwitch = !bColourSwitch;
                tableRow.setBackgroundColor(Color.parseColor("#ECF8F6"));
            }
            tableRows.add(tableRow);
        }
        return tableRows;
    }

    protected void setHeader(String keyDescription, String valueDescription) {
        TableRow tableRow = new TableRow(context);
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(tableRowParams);

        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        cellParams.weight = 9;
        TextView textViewL = new TextView(context);
        TextView textViewR = new TextView(context);
        textViewL.setTextSize(18);
        textViewR.setTextSize(18);
        textViewL.setLayoutParams(cellParams);
        textViewR.setLayoutParams(cellParams);
        textViewL.setText(keyDescription);
        textViewL.setTypeface(null, Typeface.BOLD);
        textViewR.setText(valueDescription);
        textViewR.setTypeface(null, Typeface.BOLD);
        tableRow.addView(textViewL);
        tableRow.addView(textViewR);
        tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
        tableLayoutHeader.addView(tableRow);
    }

    protected void setTableLayout(ArrayList<TableRow> tableRows) {
        for (TableRow tableRow: tableRows) {
            tableLayout.addView(tableRow);
        }
    }
    private void setFooter(String lastUpdated) {
        TableRow tableRow = new TableRow(context);
        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(tableRowParams);

        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
        cellParams.weight = 9;
        cellParams.gravity = Gravity.CENTER;
        TextView textView = new TextView(context);
        textView.setTextSize(18);
        textView.setLayoutParams(cellParams);
        textView.setText(lastUpdated);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.CENTER);
        tableRow.addView(textView);
        tableRow.setBackgroundColor(Color.parseColor("#E6E6CA"));
        tableLayoutFooter.addView(tableRow);
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
    private MetaField metaField = null;
    private ILambdaXHistory ILambdaXHistory;
    private DecimalFormat formatter = new DecimalFormat("#,###.##");
    private void defineLambda(Constants.FieldXHistoryType fieldXType, String fieldName,
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
                    } while(cFieldXHistory.moveToNext());

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
                    } while(cFieldXHistory.moveToNext());

                    return metaFields;
                };
                break;
        }
    }
}