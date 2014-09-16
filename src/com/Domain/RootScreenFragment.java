package com.Domain;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.database.BarcodeContract;
import com.database.BarcodeDbHelper;
import com.example.barcode.R;
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

/**
 * Created by Luke on 10/09/2014.
 */
public class RootScreenFragment extends Fragment {



    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.default_screen_pager, container, false);

        dbHelper = new BarcodeDbHelper(getActivity());
        db = dbHelper.getReadableDatabase();

        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        loadList(listView);

        return rootView;
    }


    private void loadList(ListView listView) {
        String[] projection = {
                BarcodeContract.Barcode._ID,
                BarcodeContract.Barcode.COLUMN_NAME_BARCODE_NAME
        };

        Cursor c = db.query(
                BarcodeContract.Barcode.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<String> list = new ArrayList<String>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String barcodeName = c.getString(c.getColumnIndex(BarcodeContract.Barcode.COLUMN_NAME_BARCODE_NAME));
            list.add(barcodeName);

        }

        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }







}
