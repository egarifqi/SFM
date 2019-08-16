package com.example.sfmtesting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DatabaseTokoHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tokoManager";
    private static final String TABLE_TOKO = "toko";
    private static final String KEY_ID = "id";
    private static final String KEY_CODE = "kode";
    private static final String KEY_NAME = "name";
    private static final String KEY_SALES_ID = "s_id";
    private static final String KEY_PARTNER_ID = "p_id";
    private static final String KEY_KONSTANTA = "konst";
    private static final String KEY_STATUS = "status";
    ContentValues values = new ContentValues();

    public DatabaseTokoHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TOKO + "(" + KEY_ID
                + " INTEGER PRIMARY KEY," + KEY_CODE + " TEXT," + KEY_NAME + " TEXT," + KEY_SALES_ID
                + " TEXT," + KEY_PARTNER_ID+" TEXT,"+ KEY_KONSTANTA+ " TEXT,"+ KEY_STATUS+ " TEXT"+")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKO);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addToko(com.example.sfmtesting.TokoDalamRute tdr) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, tdr.getKode());
        values.put(KEY_NAME, tdr.getNama());
        values.put(KEY_SALES_ID, tdr.getSalesid());
        values.put(KEY_PARTNER_ID, tdr.getPartnerId());
        values.put(KEY_KONSTANTA, tdr.getFrekuensi());
        values.put(KEY_STATUS, tdr.getStatus());

        // Inserting Row
        db.insert(TABLE_TOKO, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addToko2(com.example.sfmtesting.TokoDalamRute tdr) {
        String sql = "INSERT OR REPLACE INTO " + TABLE_TOKO + " ( "
                + KEY_CODE + ", "
                + KEY_NAME + ", "
                + KEY_SALES_ID + ", "
                + KEY_PARTNER_ID + ", "
                + KEY_KONSTANTA + ", "
                + KEY_STATUS + " ) VALUES ( ?, ?, ?, ?, ?, ? )";

        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);


        stmt.bindString(1, tdr.getKode());
        stmt.bindString(2, tdr.getNama());
        stmt.bindString(3, tdr.getSalesid());
        stmt.bindString(4, tdr.getPartnerId());
        stmt.bindString(5, tdr.getFrekuensi());
        stmt.bindString(6, tdr.getStatus());

        stmt.execute();
        stmt.clearBindings();


        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    void addAllProduk(ArrayList<com.example.sfmtesting.TokoDalamRute> s, int length) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        for (int i = 0; i < length; i++) {
//        ContentValues values = new ContentValues();
            values.put(KEY_CODE, s.get(i).getKode());
            values.put(KEY_NAME, s.get(i).getNama());
            values.put(KEY_SALES_ID, s.get(i).getSalesid());
            values.put(KEY_PARTNER_ID, s.get(i).getPartnerId());
            values.put(KEY_KONSTANTA, s.get(i).getFrekuensi());
            values.put(KEY_STATUS, s.get(i).getStatus());

            // Inserting Row
            db.insert(TABLE_TOKO, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    com.example.sfmtesting.TokoDalamRute getToko(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TOKO, new String[] { KEY_ID, KEY_CODE, KEY_NAME, KEY_SALES_ID,
                        KEY_PARTNER_ID, KEY_KONSTANTA, KEY_STATUS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        com.example.sfmtesting.TokoDalamRute tdr = new com.example.sfmtesting.TokoDalamRute(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6));
        cursor.close();
        // return contact
        return tdr;
    }

    // code to get all contacts in a list view
    public ArrayList<com.example.sfmtesting.TokoDalamRute> getAllToko() {
        ArrayList<com.example.sfmtesting.TokoDalamRute> listToko = new ArrayList<com.example.sfmtesting.TokoDalamRute>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TOKO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                com.example.sfmtesting.TokoDalamRute contact = new com.example.sfmtesting.TokoDalamRute();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setKode(cursor.getString(1));
                contact.setNama(cursor.getString(2));
                contact.setSalesid(cursor.getString(3));
                contact.setPartnerId(cursor.getString(4));
                contact.setFrekuensi(cursor.getString(5));
                contact.setStatus(cursor.getString(6));
                // Adding contact to list
                listToko.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // return contact list
        return listToko;
    }

    // code to update the single contact
    public int updateToko(com.example.sfmtesting.TokoDalamRute tdr) {
        SQLiteDatabase db = this.getWritableDatabase();

//        ContentValues values = new ContentValues();
        values.put(KEY_CODE, tdr.getKode());
        values.put(KEY_NAME, tdr.getNama());
        values.put(KEY_SALES_ID, tdr.getSalesid());
        values.put(KEY_PARTNER_ID, tdr.getPartnerId());
        values.put(KEY_KONSTANTA, tdr.getFrekuensi());
        values.put(KEY_STATUS, tdr.getStatus());

        // updating row
        return db.update(TABLE_TOKO, values, KEY_ID + " = ?",
                new String[] { String.valueOf(tdr.getId()) });
    }

    // Deleting single contact
    public void deleteContact(com.example.sfmtesting.TokoDalamRute contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TOKO, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TOKO;
        db.execSQL(query);
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TOKO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        // return count
        return cursor.getCount();
    }

}
