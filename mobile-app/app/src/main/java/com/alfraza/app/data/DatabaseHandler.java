package com.alfraza.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alfraza.app.models.Wishlist;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "3lfraza.db";

    // Table Name
    private static final String TABLE_WISH_LIST = "wish_list";

    // Table Columns names TABLE_WISH_LIST
    private static final String COL_WISH_PRODUCT_ID = "COL_WISH_PRODUCT_ID";
    private static final String COL_WISH_NAME = "COL_WISH_NAME";
    private static final String COL_WISH_IMAGE = "COL_WISH_IMAGE";
    private static final String COL_WISH_CREATED_AT = "COL_WISH_CREATED_AT";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = getWritableDatabase();
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase d) {
        createTableWishlist(d);
    }

    private void createTableWishlist(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_WISH_LIST + " ("
                + COL_WISH_PRODUCT_ID + " INTEGER PRIMARY KEY, "
                + COL_WISH_NAME + " TEXT, "
                + COL_WISH_IMAGE + " TEXT, "
                + COL_WISH_CREATED_AT + " NUMERIC "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void saveWishlist(Wishlist wishlist) {
        ContentValues values = getWishlistValue(wishlist);
        // Inserting or Update Row
        db.insertWithOnConflict(TABLE_WISH_LIST, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private ContentValues getWishlistValue(Wishlist model) {
        ContentValues values = new ContentValues();
        values.put(COL_WISH_PRODUCT_ID, model.product_id);
        values.put(COL_WISH_NAME, model.name);
        values.put(COL_WISH_IMAGE, model.image);
        values.put(COL_WISH_CREATED_AT, model.created_at);
        return values;
    }

    public Wishlist getWishlist(long id) {
        Wishlist obj = null;
        String query = "SELECT * FROM " + TABLE_WISH_LIST + " w WHERE w." + COL_WISH_PRODUCT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            obj = getWishlistByCursor(cursor);
        }
        return obj;
    }

    private Wishlist getWishlistByCursor(Cursor cur) {
        Wishlist obj = new Wishlist();
        obj.product_id = cur.getLong(cur.getColumnIndex(COL_WISH_PRODUCT_ID));
        obj.name = cur.getString(cur.getColumnIndex(COL_WISH_NAME));
        obj.image = cur.getString(cur.getColumnIndex(COL_WISH_IMAGE));
        obj.created_at = cur.getLong(cur.getColumnIndex(COL_WISH_CREATED_AT));
        return obj;
    }

    public List<Wishlist> getWishlistByPage(int limit, int offset) {
        List<Wishlist> items = new ArrayList<>();
        Cursor cursor = db.rawQuery((" SELECT * FROM " + TABLE_WISH_LIST + " w ORDER BY w." + COL_WISH_CREATED_AT + " DESC LIMIT " + limit + " OFFSET " + offset + " "), null);
        if (cursor.moveToFirst()) {
            items = getListWishlistByCursor(cursor);
        }
        return items;
    }

    private List<Wishlist> getListWishlistByCursor(Cursor cur) {
        List<Wishlist> items = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                items.add(getWishlistByCursor(cur));
            } while (cur.moveToNext());
        }
        return items;
    }

    // delete all records

    public void deleteWishlist(Long id) {
        db.delete(TABLE_WISH_LIST, COL_WISH_PRODUCT_ID + " = ?", new String[]{id.toString()});
    }

    // delete all records
    public void deleteWishlist() {
        db.execSQL("DELETE FROM " + TABLE_WISH_LIST);
    }

    public int getWishlistSize() {
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_WISH_LIST);
    }
}
