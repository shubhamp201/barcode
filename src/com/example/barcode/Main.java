package com.example.barcode;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.database.BarcodeContract.Barcode;
import com.database.BarcodeDbHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class Main extends Activity
{
    private SQLiteOpenHelper dbHelper;
    private SimpleCursorAdapter mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ListView listView = (ListView) findViewById(R.id.listView);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(params);
        progressBar.setIndeterminate(true);
        listView.setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        dbHelper = new BarcodeDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                Barcode._ID,
                Barcode.COLUMN_NAME_BARCODE_NAME
        };

        Cursor c = db.query(
                Barcode.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<String> list = new ArrayList<String>();
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            String barcodeName = c.getString(c.getColumnIndex(Barcode.COLUMN_NAME_BARCODE_NAME));
            list.add(barcodeName);

        }

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);


    }

    public void scanBarcode(View view)
    {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null)
        {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            TextView formatTxt = (TextView) findViewById(R.id.scan_format);
            TextView contentTxt = (TextView) findViewById(R.id.scan_content);
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);

            ContentValues values = new ContentValues();
            values.put(Barcode.COLUMN_NAME_BARCODE_CONTENT, scanContent);
            values.put(Barcode.COLUMN_NAME_BARCODE_FORMAT, scanFormat);
            values.put(Barcode.COLUMN_NAME_BARCODE_NAME, "FlyBuyer");

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert(Barcode.TABLE_NAME, null, values);
            db.close();

            // barcode image
            ImageView iv = (ImageView) findViewById(R.id.imageView);

            try
            {
                Bitmap bitmap = encodeAsBitmap(scanContent, BarcodeFormat.EAN_13, 600, 300);
                iv.setImageBitmap(bitmap);

            }
            catch (WriterException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

private static final int WHITE = 0xFFFFFFFF;
private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException
    {
        String contentsToEncode = contents;
        if (contentsToEncode == null)
        {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null)
        {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try
        {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        }
        catch (IllegalArgumentException iae)
        {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++)
        {
            int offset = y * width;
            for (int x = 0; x < width; x++)
            {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents)
    {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++)
        {
            if (contents.charAt(i) > 0xFF)
            {
                return "UTF-8";
            }
        }
        return null;
    }
}
