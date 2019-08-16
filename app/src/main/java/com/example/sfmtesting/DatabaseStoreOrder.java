package com.example.sfmtesting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;

import java.util.ArrayList;

public class DatabaseStoreOrder extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "storeOrderManager";
    private static final String TABLE_PRODUK_ORDER = "storeOrder";
    private static final String KEY_ID = "id";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_PARTNER_ID = "partner_id";
    private static final String KEY_REF = "reference";
    private static final String KEY_STORE = "nama_toko";
    private static final String KEY_DO_ID = "do_id";
    ContentValues values = new ContentValues();

    public DatabaseStoreOrder(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUK_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PARTNER_ID + " TEXT,"
                + KEY_BRAND + " TEXT,"
                + KEY_REF + " TEXT,"
                + KEY_DO_ID + " INTEGER,"
                + KEY_STORE + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUK_ORDER);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addProduk(StoreOrderList op) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_PARTNER_ID, op.getPartner_id());
        values.put(KEY_BRAND, op.getBrand_produk());
        values.put(KEY_REF, op.getReference());
        values.put(KEY_DO_ID, op.getDo_id());
        values.put(KEY_STORE, op.getNama_toko());

        // Inserting Row
        db.insert(TABLE_PRODUK_ORDER, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addAllProduk(ArrayList<OrderedProduct> oplist, int length) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
//        Log.e("JSON", ja.toString());

        try {
            for (int i = 0; i < length; i++) { ;
//            Log.e("EBP_"+i, jo.toString());
                ContentValues values = new ContentValues();
                values.put(KEY_PARTNER_ID, oplist.get(i).getPartner_id());
                values.put(KEY_BRAND, oplist.get(i).getBrand_produk());
                values.put(KEY_REF, oplist.get(i).getReference());
                values.put(KEY_DO_ID, oplist.get(i).getDo_id());
                values.put(KEY_STORE, oplist.get(i).getNama_toko());

                // Inserting Row
                db.insert(TABLE_PRODUK_ORDER, null, values);
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
    StoreOrderList getProduk(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        StoreOrderList op = new StoreOrderList();

        Cursor cursor = db.query(TABLE_PRODUK_ORDER, new String[] { KEY_ID, KEY_PARTNER_ID, KEY_BRAND, KEY_REF,
                        KEY_DO_ID, KEY_STORE},KEY_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            op = new StoreOrderList(cursor.getInt(cursor.getColumnIndex(KEY_DO_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_BRAND)),
                    cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_REF)),
                    cursor.getString(cursor.getColumnIndex(KEY_STORE)));
            cursor.close();
        }
        // return contact
        return op;
    }

    // code to get all contacts in a list view
    public ArrayList<StoreOrderList> getAllProduk() {
        ArrayList<StoreOrderList> listProduk = new ArrayList<StoreOrderList>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                StoreOrderList contact = new StoreOrderList();
                contact.setBrand_produk(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setReference(cursor.getString(cursor.getColumnIndex(KEY_REF)));
                contact.setDo_id(cursor.getInt(cursor.getColumnIndex(KEY_DO_ID)));
                contact.setNama_toko(cursor.getString(cursor.getColumnIndex(KEY_STORE)));

                // Adding contact to list
                listProduk.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return listProduk;
    }

    public ArrayList<OrderedProduct> getAllProdukToko(String partnerid, String brand) {
        ArrayList<OrderedProduct> listProduk = new ArrayList<OrderedProduct>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_ORDER + " WHERE " + KEY_PARTNER_ID + " IS " + partnerid + " AND " + KEY_BRAND + " IS '" + brand + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OrderedProduct contact = new OrderedProduct();
                contact.setId_produk(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setBrand_produk(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                contact.setReference(cursor.getString(cursor.getColumnIndex(KEY_REF)));

                // Adding contact to list
                listProduk.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return listProduk;
    }

    // code to update the single contact
    public int updateProduk(com.example.sfmtesting.Spacecraft s) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_PARTNER_ID, s.getPartner_id());
        values.put(KEY_BRAND, s.getBrand());
        values.put(KEY_REF, s.getKoli());

        // updating row
        return db.update(TABLE_PRODUK_ORDER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(s.getId())});
    }

    // Deleting single contact
    public void deleteProduk(com.example.sfmtesting.Spacecraft contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUK_ORDER, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PRODUK_ORDER;
        db.execSQL(query);
        db.close();
    }

    // Getting contacts Count
    public int getProductCount() {
        String countQuery = "SELECT  * FROM " + TABLE_PRODUK_ORDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }
}
