package com.example.salesforcemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;

public class DatabaseVisitHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "storeVisitManager";
    private static final String TABLE_PRODUK_ORDER = "storeVisit";
    private static final String KEY_ID = "id";
    private static final String KEY_SALES_ID = "sales_id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PARTNER_ID = "partner_id";
    private static final String KEY_PARTNER_REF = "partner_ref";
    private static final String KEY_INROUTE = "dalam_rute";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_STORE = "nama_toko";
    private static final String KEY_REF = "reference";
    private static final String KEY_START = "waktu_mulai";
    private static final String KEY_FINISH = "waktu_selesai";
    private static final String KEY_REASON = "alasan";
    private static final String KEY_STATE = "state";
//    ContentValues values = new ContentValues();

    public DatabaseVisitHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUK_ORDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SALES_ID + " TEXT,"
                + KEY_USER_ID + " TEXT,"
                + KEY_PARTNER_ID + " TEXT,"
                + KEY_PARTNER_REF + " TEXT,"
                + KEY_INROUTE + " INTEGER,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_STORE + " TEXT,"
                + KEY_REF + " TEXT,"
                + KEY_START + " TEXT,"
                + KEY_FINISH + " TEXT,"
                + KEY_REASON + " TEXT,"
                + KEY_STATE + " TEXT"
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
    void addProduk(StoreVisitList op) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SALES_ID, op.getSales_id());
        values.put(KEY_USER_ID, op.getUser_id());
        values.put(KEY_PARTNER_ID, op.getPartner_id());
        values.put(KEY_PARTNER_REF, op.getPartner_ref());
        if (op.getInroute()){
            values.put(KEY_INROUTE, 1);
        } else {
            values.put(KEY_INROUTE, 0);
        }
        values.put(KEY_LATITUDE, op.getLatitude());
        values.put(KEY_LONGITUDE, op.getLongitude());
        values.put(KEY_STORE, op.getNama_toko());
        values.put(KEY_REF, op.getReference());
        values.put(KEY_START, op.getStart_time());
        values.put(KEY_FINISH, op.getFinish_time());
        values.put(KEY_REASON, op.getReason());
        values.put(KEY_STATE, op.getState());


        // Inserting Row
        db.insert(TABLE_PRODUK_ORDER, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    void addAllProduk(ArrayList<StoreVisitList> oplist, int length) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
//        Log.e("JSON", ja.toString());

        try {
            for (int i = 0; i < length; i++) { ;
//            Log.e("EBP_"+i, jo.toString());
                ContentValues values = new ContentValues();
                values.put(KEY_SALES_ID, oplist.get(i).getSales_id());
                values.put(KEY_USER_ID, oplist.get(i).getUser_id());
                values.put(KEY_PARTNER_ID, oplist.get(i).getPartner_id());
                values.put(KEY_PARTNER_REF, oplist.get(i).getPartner_ref());
                if (oplist.get(i).getInroute()){
                    values.put(KEY_INROUTE, 1);
                } else {
                    values.put(KEY_INROUTE, 0);
                }
                values.put(KEY_LATITUDE, oplist.get(i).getLatitude());
                values.put(KEY_LONGITUDE, oplist.get(i).getLongitude());
                values.put(KEY_STORE, oplist.get(i).getNama_toko());
                values.put(KEY_REF, oplist.get(i).getReference());
                values.put(KEY_START, oplist.get(i).getStart_time());
                values.put(KEY_FINISH, oplist.get(i).getFinish_time());
                values.put(KEY_REASON, oplist.get(i).getReason());
                values.put(KEY_STATE, oplist.get(i).getState());


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
    StoreVisitList getProduk(String ref) {
        SQLiteDatabase db = this.getReadableDatabase();
        StoreVisitList op = new StoreVisitList();
        boolean in_route = true;
        ref = ref.replaceAll("/","\\/");
        ref = "'"+ref+"'";

        String query = "SELECT * FROM "+ TABLE_PRODUK_ORDER +" WHERE "+KEY_REF+" LIKE "+ref;

        Cursor cursor = db.rawQuery(query, null);
        Log.e("QUERY", query);
        Log.e("REF", ref);
        Log.e("CURSOR1", DatabaseUtils.dumpCursorToString(cursor));

//        Cursor cursor = db.query(TABLE_PRODUK_ORDER, new String[] { KEY_ID, KEY_SALES_ID, KEY_USER_ID,
//                        KEY_PARTNER_ID, KEY_PARTNER_REF, KEY_INROUTE, KEY_LATITUDE, KEY_LONGITUDE,
//                        KEY_STORE, KEY_REF, KEY_START, KEY_FINISH, KEY_REASON, KEY_STATE},KEY_REF + "=?",
//                new String[] { ref }, null, null, null, null);

        Log.e("CURSOR2", String.valueOf(cursor));
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex(KEY_INROUTE))==1){
                in_route = true;
            } else if (cursor.getInt(cursor.getColumnIndex(KEY_INROUTE))==0){
                in_route = false;
            }
            op = new StoreVisitList(cursor.getString(cursor.getColumnIndex(KEY_SALES_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_PARTNER_REF)),
                    cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)), in_route,
                    cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)),
                    cursor.getString(cursor.getColumnIndex(KEY_STORE)),
                    cursor.getString(cursor.getColumnIndex(KEY_REF)),
                    cursor.getString(cursor.getColumnIndex(KEY_START)),
                    cursor.getString(cursor.getColumnIndex(KEY_FINISH)),
                    cursor.getString(cursor.getColumnIndex(KEY_REASON)),
                    cursor.getString(cursor.getColumnIndex(KEY_STATE)));
            cursor.close();
        }
        // return contact
        return op;
    }

    // code to get all contacts in a list view
    public ArrayList<StoreVisitList> getAllProduk() {
        ArrayList<StoreVisitList> listProduk = new ArrayList<StoreVisitList>();
//        Spacecraft contact = new Spacecraft();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PRODUK_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                boolean in_route = true;
                if (cursor.getInt(cursor.getColumnIndex(KEY_INROUTE))==1){
                    in_route = true;
                } else if(cursor.getInt(cursor.getColumnIndex(KEY_INROUTE))==0){
                    in_route = false;
                }
                StoreVisitList contact = new StoreVisitList();
                contact.setSales_id(cursor.getString(cursor.getColumnIndex(KEY_SALES_ID)));
                contact.setUser_id(cursor.getString(cursor.getColumnIndex(KEY_USER_ID)));
                contact.setPartner_ref(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_REF)));
                contact.setPartner_id(cursor.getString(cursor.getColumnIndex(KEY_PARTNER_ID)));
                contact.setInroute(in_route);
                contact.setLatitude(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                contact.setLongitude(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                contact.setNama_toko(cursor.getString(cursor.getColumnIndex(KEY_STORE)));
                contact.setReference(cursor.getString(cursor.getColumnIndex(KEY_REF)));
                contact.setStart_time(cursor.getString(cursor.getColumnIndex(KEY_START)));
                contact.setFinish_time(cursor.getString(cursor.getColumnIndex(KEY_FINISH)));
                contact.setReason(cursor.getString(cursor.getColumnIndex(KEY_REASON)));
                contact.setState(cursor.getString(cursor.getColumnIndex(KEY_STATE)));


                // Adding contact to list
                listProduk.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return listProduk;
    }



    // code to update the single contact
    public int updateProduk(StoreVisitList op) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SALES_ID, op.getSales_id());
        values.put(KEY_USER_ID, op.getUser_id());
        values.put(KEY_PARTNER_ID, op.getPartner_id());
        values.put(KEY_PARTNER_REF, op.getPartner_ref());
        if (op.getInroute()){
            values.put(KEY_INROUTE, 1);
        } else {
            values.put(KEY_INROUTE, 0);
        }
        values.put(KEY_LATITUDE, op.getLatitude());
        values.put(KEY_LONGITUDE, op.getLongitude());
        values.put(KEY_STORE, op.getNama_toko());
        values.put(KEY_REF, op.getReference());
        values.put(KEY_START, op.getStart_time());
        values.put(KEY_FINISH, op.getFinish_time());
        values.put(KEY_REASON, op.getReason());
        values.put(KEY_STATE, op.getState());

        // updating row
        return db.update(TABLE_PRODUK_ORDER, values, KEY_ID + " = ?",
                new String[]{String.valueOf(op.getId())});
    }

    // Deleting single contact
    public void deleteProduk(Spacecraft contact) {
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
