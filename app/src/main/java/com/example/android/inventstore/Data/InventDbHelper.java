package com.example.android.inventstore.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventstore.Data.InventContract.InventEntry;

public class InventDbHelper extends SQLiteOpenHelper {

    public final String LOG_TAG = InventDbHelper.class.getSimpleName();

    //naming the database and setting its version number to 1
    public static final String DATABASE_NAME = "products.db";
    public static final int DATABASE_VERSION = 1;

    //overriding the constructor
    public InventDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + InventEntry.TABLE_NAME + " ("
                + InventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + InventEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + InventEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT NOT NULL);";

        Log.i(LOG_TAG,SQL_CREATE_PRODUCTS_TABLE);
        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
