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

import java.util.EnumMap;
import java.util.Map;

public class Main extends Activity    implements LoaderManager.LoaderCallbacks<Cursor>
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

        String[] fromColumns = {
                Barcode.COLUMN_NAME_BARCODE_NAME
        };

        int[] toViews = {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        listView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);

    }

    // Called when a new Loader needs to be created
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(this, ContactsContract.Data.CONTENT_URI,
                PROJECTION, SELECTION, null, null);
    }

    // Called when a previously created loader has finished loading
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    // Called when a previously created loader is reset, making the data unavailable
    public void onLoaderReset(Loader<Cursor> loader)
    {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }

    public void onListItemClick(ListView l, View v, int position, long id)
    {
        // Do something when a list item is clicked
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
