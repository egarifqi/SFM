package com.example.sfmtesting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;

import java.util.ArrayList;

public class DatabaseOrderHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "produkOrderManager";
    private static final String TABLE_PRODUK_ORDER = "produkOrder";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "kode";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_STOCK = "stock";
    private static final String KEY_SGT_QTY = "sgt_qty";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_QTY = "final_qty";
    private static final String KEY_BRAND = "brand";
    private static final String KEY_PARTNER_ID = "partner_id";
    private static final String KEY_REF = "reference";
    private static final String KEY_STORE = "nama_toko";
    private static final String KEY_DO_ID = "do_id";
    ContentValues values = new ContentValues();

    public DatabaseOrderHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUK_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CODE + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_QTY + " INTEGER,"
                + KEY_PARTNER_ID + " TEXT,"
                + KEY_BRAND + " TEXT,"
                + KEY_STOCK + " INTEGER,"
                + KEY_SGT_QTY + " INTEGER,"
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
    void addProduk(OrderedProduct op) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, op.getKode_odoo());
        values.put(KEY_NAME, op.getNama_produk());
        values.put(KEY_PRICE, op.getHarga_produk());
        values.put(KEY_STOCK, op.getStock_produk());
        values.put(KEY_SGT_QTY, op.getSgtorder_produk());
        values.put(KEY_CATEGORY, op.getKategori_produk());
        values.put(KEY_QTY, op.getFinalorder_produk());
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
                values.put(KEY_CODE, oplist.get(i).getKode_odoo());
                values.put(KEY_NAME, oplist.get(i).getNama_produk());
                values.put(KEY_PRICE, oplist.get(i).getHarga_produk());
                values.put(KEY_STOCK, oplist.get(i).getStock_produk());
                values.put(KEY_SGT_QTY, oplist.get(i).getSgtorder_produk());
                values.put(KEY_CATEGORY, oplist.get(i).getKategori_produk());
                values.put(KEY_QTY, oplist.get(i).getFinalorder_produk());
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
    OrderedProduct getProduk(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        OrderedProduct op = new OrderedProduct();

        Cursor cursor = db.query(TABLE_PRODUK_ORDER, new String[] { KEY_ID, KEY_CODE, KEY_NAME, KEY_PRICE,
                        KEY_CATEGORY, KEY_QTY, KEY_PARTNER_ID, KEY_BRAND, KEY_STOCK, KEY_SGT_QTY, KEY_REF,
                        KEY_DO_ID, KEY_STORE},KEY_ID + "=?", new String[] { String.valueOf(id) },
                        null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            op = new OrderedProduct(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_CODE)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_PRICE)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STOCK)),
                    cursor.getInt(cursor.getColumnIndex(KEY_SGT_QTY)),
                    cursor.getInt(cursor.getColumnIndex(KEY_QTY)),
                    cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)),
                    cursor.getString(cursor.getColumnIndex(KEY_BRAND)),
                    cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_REF)),
                    cursor.getInt(cursor.getColumnIndex(KEY_DO_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_STORE)));
            cursor.close();
        }
        // return contact
        return op;
    }

    // code to get all contacts in a list view
    public ArrayList<OrderedProduct> getAllProduk() {
        ArrayList<OrderedProduct> listProduk = new ArrayList<OrderedProduct>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OrderedProduct contact = new OrderedProduct();
                contact.setId_produk(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                contact.setKode_odoo(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                contact.setNama_produk(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setHarga_produk(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                contact.setKategori_produk(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                contact.setBrand_produk(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setReference(cursor.getString(cursor.getColumnIndex(KEY_REF)));
                contact.setStock_produk(cursor.getInt(cursor.getColumnIndex(KEY_STOCK)));
                contact.setSgtorder_produk(cursor.getInt(cursor.getColumnIndex(KEY_SGT_QTY)));
                contact.setFinalorder_produk(cursor.getInt(cursor.getColumnIndex(KEY_QTY)));
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

    public ArrayList<OrderedProduct> getAllProdukToko(int do_id) {
        ArrayList<OrderedProduct> listProduk = new ArrayList<OrderedProduct>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_ORDER + " WHERE " + KEY_DO_ID + " IS " +do_id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                OrderedProduct contact = new OrderedProduct();
                contact.setId_produk(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                contact.setKode_odoo(cursor.getString(cursor.getColumnIndex(KEY_CODE)));
                contact.setNama_produk(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                contact.setHarga_produk(cursor.getString(cursor.getColumnIndex(KEY_PRICE)));
                contact.setKategori_produk(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                contact.setFinalorder_produk(cursor.getInt(cursor.getColumnIndex(KEY_QTY)));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setBrand_produk(cursor.getString(cursor.getColumnIndex(KEY_BRAND)));
                contact.setStock_produk(cursor.getInt(cursor.getColumnIndex(KEY_STOCK)));
                contact.setSgtorder_produk(cursor.getInt(cursor.getColumnIndex(KEY_SGT_QTY)));
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

    // code to update the single contact
    public int updateProduk(com.example.sfmtesting.Spacecraft s) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, s.getKodeodoo());
        values.put(KEY_NAME, s.getNamaproduk());
        values.put(KEY_PRICE, s.getPrice());
        values.put(KEY_STOCK, s.getStock());
        values.put(KEY_SGT_QTY, s.getQty());
        values.put(KEY_CATEGORY, s.getCategory());
        values.put(KEY_QTY, s.getBarcode());
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


