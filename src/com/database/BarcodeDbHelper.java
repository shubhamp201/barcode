package com.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.database.BarcodeContract.*;

/**
 * Created with IntelliJ IDEA.
 * User: lzhou
 * Date: 1/09/2014
 * Time: 2:59 PM
 */
public class BarcodeDbHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Barcode.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Barcode.TABLE_NAME + " (" +
                    Barcode._ID + " INTEGER PRIMARY KEY," +
                    Barcode.COLUMN_NAME_BARCODE_NAME + TEXT_TYPE + COMMA_SEP +
                    Barcode.COLUMN_NAME_BARCODE_CONTENT + TEXT_TYPE + COMMA_SEP +
                    Barcode.COLUMN_NAME_BARCODE_FORMAT + TEXT_TYPE + COMMA_SEP +
            " )";

    public BarcodeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
