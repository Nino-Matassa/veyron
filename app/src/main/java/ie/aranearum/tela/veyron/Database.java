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

