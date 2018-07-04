package com.example.android.inventstore;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.android.inventstore.Data.InventContract.InventEntry;


public class StoreActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the data loader
    private static final int PRODUCTS_LOADER = 0;

    // Adapter for the ListView
    InventCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView productsListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productsListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of products data in the Cursor.
        // There is no products data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new InventCursorAdapter(this, null);
        productsListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(StoreActivity.this, EditorActivity.class);

                Uri currentproductUri = ContentUris.withAppendedId(InventEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentproductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PRODUCTS_LOADER, null, this);

    }

    //Helper method to delete all products in the database.
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(InventEntry.CONTENT_URI, null, null);
        Log.v("StoreActivity", rowsDeleted + " rows deleted from products database");
    }

    private void insertProduct() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's product attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventEntry.COLUMN_PRODUCT_NAME, "Headphones");
        values.put(InventEntry.COLUMN_PRODUCT_PRICE, 20);
        values.put(InventEntry.COLUMN_PRODUCT_QUANTITY, 2);
        values.put(InventEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Kaouther");
        values.put(InventEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "00222558877");
        Uri newUri = getContentResolver().insert(InventEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.store_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all products" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;

            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                InventEntry._ID,
                InventEntry.COLUMN_PRODUCT_NAME,
                InventEntry.COLUMN_PRODUCT_PRICE,
                InventEntry.COLUMN_PRODUCT_QUANTITY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                InventEntry.CONTENT_URI,            // Provider content URI to query
                projection,                         // Columns to include in the resulting Cursor
                null,                      // No selection clause
                null,                   // No selection arguments
                null);                     // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link InventCursorAdapter} with this new cursor containing updated products data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    public void saleProduct(long id, int quantity) {

        // Decrement item quantity
        if (quantity > 0) {
            quantity--;
            // Construct new uri and content values
            Uri updateUri = ContentUris.withAppendedId(InventEntry.CONTENT_URI, id);
            ContentValues values = new ContentValues();
            values.put(InventEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, R.string.sale_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sale_successful, Toast.LENGTH_SHORT).show();
            }

        } else {
            //  Out of stock
            Toast.makeText(this, R.string.out_of_stock, Toast.LENGTH_LONG).show();
        }
    }
}
