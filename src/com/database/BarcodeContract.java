package com.database;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: lzhou
 * Date: 1/09/2014
 * Time: 2:54 PM
 */
public class BarcodeContract
{
    public static abstract class Barcode implements BaseColumns
    {
        public static final String TABLE_NAME = "barcode";
        public static final String COLUMN_NAME_BARCODE_NAME = "name";
        public static final String COLUMN_NAME_BARCODE_FORMAT = "format";
        public static final String COLUMN_NAME_BARCODE_CONTENT = "content";
    }
}
