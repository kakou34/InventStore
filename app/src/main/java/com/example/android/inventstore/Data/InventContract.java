package com.example.android.inventstore.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class InventContract {

    //This class should not be instantiated so we provide a private constructor
    private InventContract(){}

    //package name for the app
    public static final String CONTENT_AUTHORITY = "com.example.android.inventstore";

    //create the base of all URI's which apps will use to contact
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible path to retrieve products data
    public static final String PATH_PRODUCTS = "products";

    //Inner Entry class to declare constants for columns' names and values
    public static final class InventEntry implements BaseColumns {

        // The content URI to access the product data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //constants for table's and columns' names
        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";


    }


}
