package com.example.salesforcemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DatabaseMHSHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "produkMHSManager";
    private static final String TABLE_PRODUK_MHS = "produkMHS";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "kode";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_QTY = "qty";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_PARTNER_ID = "partner_id";
    private static final String KEY_PCS = "pcs";
    private static final String KEY_SUGGESTION = "suggestion";
    ContentValues values = new ContentValues();

    public DatabaseMHSHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUK_MHS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CODE + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_BARCODE + " TEXT,"
                + KEY_PARTNER_ID + " TEXT,"
                + KEY_BRAND + " TEXT,"
                + KEY_STOCK + " TEXT,"
                + KEY_QTY + " TEXT,"
                + KEY_PCS + " TEXT,"
                + KEY_SUGGESTION + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK_MHS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addProduk(Spacecraft s) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, s.getKodeodoo());
        values.put(KEY_NAME, s.getNamaproduk());
        values.put(KEY_PRICE, s.getPrice());
        values.put(KEY_STOCK, s.getStock());
        values.put(KEY_QTY, s.getQty());
        values.put(KEY_CATEGORY, s.getCategory());
        values.put(KEY_BARCODE, s.getBarcode());
        values.put(KEY_PARTNER_ID, s.getPartner_id());
        values.put(KEY_BRAND, s.getBrand());
        values.put(KEY_PCS, s.getKoli());
        values.put(KEY_SUGGESTION, s.getSgtorder());

        // Inserting Row
        db.insert(TABLE_PRODUK_MHS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addAllProduk(JSONArray ja, int length) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        Spacecraft s = new Spacecraft();
        db.beginTransaction();
        JSONObject jo;
//        Log.e("JSON", ja.toString());

        try {
            for (int i = 0; i < length; i++) {
                jo = ja.getJSONObject(i);
//            Log.e("EBP_"+i, jo.toString());
//        ContentValues values = new ContentValues();
                values.put(KEY_CODE, jo.getString("default_code"));
                values.put(KEY_NAME, jo.getString("name"));
                values.put(KEY_PRICE, jo.getString("price"));
                values.put(KEY_STOCK, jo.getString("ba_stock_qty"));
                values.put(KEY_QTY, jo.getString("ba_order_qty"));
                values.put(KEY_CATEGORY, jo.getString("category"));
                values.put(KEY_BARCODE, jo.getString("barcode"));
                values.put(KEY_PARTNER_ID, jo.getString("partner_id"));
                values.put(KEY_BRAND, jo.getString("brand"));
                values.put(KEY_PCS, jo.getString("unit"));
                values.put(KEY_SUGGESTION, jo.getString("ba_order_qty"));

                // Inserting Row
                db.insert(TABLE_PRODUK_MHS, null, values);
//                Log.e("EBP_" + i, values.toString());
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
//        Log.e("DATABASE", db.toString());
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    Spacecraft getProduk(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Spacecraft s = new Spacecraft();

        Cursor cursor = db.query(TABLE_PRODUK_MHS, new String[]{KEY_ID, KEY_CODE, KEY_NAME, KEY_PRICE,
                        KEY_CATEGORY, KEY_BARCODE, KEY_PARTNER_ID, KEY_BRAND, KEY_STOCK, KEY_QTY, KEY_PCS,
                        KEY_SUGGESTION}, KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            s = new Spacecraft(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                    cursor.getString(cursor.getColumnIndex(KEY_CODE)), cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_PRICE)), cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)),
                    "", cursor.getString(cursor.getColumnIndex(KEY_BARCODE)), 0,
                    cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)), cursor.getString(cursor.getColumnIndex(KEY_BRAND)),
                    cursor.getString(cursor.getColumnIndex(KEY_STOCK)), cursor.getString(cursor.getColumnIndex(KEY_QTY)),
                    cursor.getString(cursor.getColumnIndex(KEY_PCS)), cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION)));
            cursor.close();
        }
        // return contact
        return s;
    }

    // code to get all contacts in a list view
    public ArrayList<Spacecraft> getAllProduk() {
        ArrayList<Spacecraft> listProduk = new ArrayList<Spacecraft>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_MHS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Spacecraft contact = new Spacecraft();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setKodeodoo(cursor.getString(1));
                contact.setNamaproduk(cursor.getString(2));
                contact.setPrice(cursor.getString(3));
                contact.setCategory(cursor.getString(4));
                contact.setBarcode(cursor.getString(5));
                contact.setPartner_id(cursor.getString(6));
                contact.setBrand(cursor.getString(7));
                contact.setStock(cursor.getString(8));
                contact.setQty(cursor.getString(9));
                contact.setKoli(cursor.getString(10));

                // Adding contact to list
                listProduk.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return listProduk;
    }

    public ArrayList<Spacecraft> getAllProdukToko(String partnerid, String brand) {
        ArrayList<Spacecraft> listProduk = new ArrayList<Spacecraft>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_MHS + " WHERE " + KEY_PARTNER_ID + " IS " + partnerid + " AND " + KEY_BRAND + " IS '" + brand + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Spacecraft contact = new Spacecraft();
                contact.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                contact.setKodeodoo(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                contact.setNamaproduk(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setPrice(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                contact.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                contact.setBarcode(cursor.getString(cursor.getColumnIndex(KEY_BARCODE)));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setBrand(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                contact.setStock(cursor.getString(cursor.getColumnIndex(KEY_STOCK)));
                contact.setQty(cursor.getString(cursor.getColumnIndex(KEY_QTY)));
                contact.setKoli(cursor.getString(cursor.getColumnIndex(KEY_PCS)));
                contact.setSgtorder(cursor.getString(cursor.getColumnIndex(KEY_SUGGESTION)));

                // Adding contact to list
                listProduk.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return listProduk;
    }

    // code to update the single contact
    public int updateProduk(Spacecraft s) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, s.getKodeodoo());
        values.put(KEY_NAME, s.getNamaproduk());
        values.put(KEY_PRICE, s.getPrice());
        values.put(KEY_STOCK, s.getStock());
        values.put(KEY_QTY, s.getQty());
        values.put(KEY_CATEGORY, s.getCategory());
        values.put(KEY_BARCODE, s.getBarcode());
        values.put(KEY_PARTNER_ID, s.getPartner_id());
        values.put(KEY_BRAND, s.getBrand());
        values.put(KEY_PCS, s.getKoli());

        // updating row
        return db.update(TABLE_PRODUK_MHS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(s.getId())});
    }

    // Deleting single contact
    public void deleteProduk(Spacecraft contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUK_MHS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PRODUK_MHS;
        db.execSQL(query);
        db.close();
    }

    // Getting contacts Count
    public int getProductCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUK_MHS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int copun = cursor.getCount();
        cursor.close();

        // return count
        return copun;
    }

}

