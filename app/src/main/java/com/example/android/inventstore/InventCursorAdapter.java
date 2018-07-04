package com.example.android.inventstore;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.android.inventstore.Data.InventContract.InventEntry;

public class InventCursorAdapter extends CursorAdapter {


    //overriding the constructor
    public InventCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        //finding views from the list item
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);


        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        // These will be used in sale method. They're final, as we are calling
        // MainActivity class directly
        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(InventEntry._ID));
        final int quantity = Integer.parseInt(productQuantity);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        // Handle sale button click
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreActivity storeActivity = (StoreActivity) context;
                storeActivity.saleProduct(id,quantity);
            }
        });

    }
}
