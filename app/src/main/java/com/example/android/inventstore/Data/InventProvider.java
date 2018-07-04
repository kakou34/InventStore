package com.example.android.inventstore.Data;

import com.example.android.inventstore.Data.InventContract.InventEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class InventProvider extends ContentProvider {

    private final String LOG_TAG = getClass().getSimpleName();

    //URI matcher code for the content URI for the products table
    private static final int PRODUCTS = 0;


    //URI matcher code for the content URI for a single product in the products table
    private static final int PRODUCT_ID = 1;

    //UriMatcher object to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        //URI for the whole products table
        sUriMatcher.addURI(InventContract.CONTENT_AUTHORITY, InventContract.PATH_PRODUCTS, PRODUCTS);
        //URI for a specific row of the products table
        sUriMatcher.addURI(InventContract.CONTENT_AUTHORITY, InventContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    //Database helper object
    private InventDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the products table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table.
                cursor = database.query(InventEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    //helper method to insert a product with the given content values to the data base
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the product requires a name");
        }

        //check that the price is valid
        Integer price = values.getAsInteger(InventEntry.COLUMN_PRODUCT_PRICE);
        if( price == null || price < 0){
            throw new IllegalArgumentException("The price cannot be negative");
        }

        // Check that the quantity is valid
        Integer quantity = values.getAsInteger(InventEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity<0) {
            throw new IllegalArgumentException("The quantity cannot be negative");
        }

        //check that the supplier's name is given
        String supplierName = values.getAsString(InventEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)){
            throw new IllegalArgumentException("Supplier name required");
        }

        //check that the supplier's phone number is given
        String supplierNumber = values.getAsString(InventEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if(TextUtils.isEmpty(supplierNumber)){
            throw new IllegalArgumentException("Supplier's phone number required");
        }

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(InventEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //update all rows
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                //update a single row with the given ID
                selection = InventEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    //helper method to update data at the given URI with the given content values
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check that the name value is not null if values contains the product name key.
        if (values.containsKey(InventEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(InventEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("the product requires a name");
            }
        }


        // check that the price is valid if values contains the price key.
        if (values.containsKey(InventEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(InventEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price<0) {
                throw new IllegalArgumentException("invalid price entered");
            }
        }

        // check that the quantity value is valid.
        if (values.containsKey(InventEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(InventEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity==null || quantity<0) {
                throw new IllegalArgumentException("Invalid quantity entered");
            }
        }

        // check that the supplier's name value is not null if values contains the supplier's name key.
        if (values.containsKey(InventEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(InventEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("the product requires a supplier's name");
            }
        }

        // check that the supplier's number value is not null if values contains the supplier's number key.
        if (values.containsKey(InventEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierNumber = values.getAsString(InventEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierNumber)) {
                throw new IllegalArgumentException("the product requires supplier's phone number");
            }
        }



        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(InventEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}
