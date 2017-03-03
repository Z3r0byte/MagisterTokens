package eu.z3r0byteapps.magistertokens.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import eu.z3r0byteapps.magistertokens.Container.List;

/**
 * Created by bas on 15-2-17.
 */

public class ListDatabase extends SQLiteOpenHelper {

    private static final String TAG = "ListDatabase";

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "Database";
    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_TOKENS = "tokens";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IS_PREFERRED = "isPreferred";
    private static final String KEY_AMOUNT_OF_TOKENS = "amountOfTokens";

    private static final String KEY_TOKEN_ID = "tokenId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LIST_NAME = "listname";

    public ListDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_LISTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " STRING,"
                + KEY_IS_PREFERRED + " BOOLEAN,"
                + KEY_AMOUNT_OF_TOKENS + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_TOKENS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TOKEN_ID + " INTEGER,"
                + KEY_TOKEN + " STRING,"
                + KEY_LIST_NAME + " STRING"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    public void addList(List list) {
        if (list == null) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_NAME, list.getName());
        contentValues.put(KEY_IS_PREFERRED, list.isPreferred());
        contentValues.put(KEY_AMOUNT_OF_TOKENS, list.getAmountOfTokens());
        db.insert(TABLE_LISTS, null, contentValues);
        db.close();
    }

    public Integer getAmountOfLists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_LISTS;
        Cursor cursor = db.rawQuery(Query, null);
        int amount = cursor.getCount();
        db.close();
        return amount;
    }

    public Boolean listExists(List list) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_LISTS + " where " + KEY_NAME + " = '" + list.getName() + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() > 0) {
            db.close();
            return true;
        }
        db.close();
        return false;
    }

    public String getPreferredList() {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_LISTS + " where " + KEY_IS_PREFERRED + " = '" + 1 + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String name;
                do {
                    name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
                return name;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        }
        cursor.close();
        db.close();
        return null;
    }

    public ArrayList<List> getLists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_LISTS;
        Cursor cursor = db.rawQuery(Query, null);
        ArrayList<List> result = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    List list = new List();
                    list.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    list.setAmountOfTokens(cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT_OF_TOKENS)));
                    if (cursor.getInt(cursor.getColumnIndex(KEY_IS_PREFERRED)) == 1) {
                        list.setPreferred(true);
                    } else {
                        list.setPreferred(false);
                    }
                    result.add(list);
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
                return result;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        }
        cursor.close();
        db.close();
        return null;
    }

    public void setPreferred(List list) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_IS_PREFERRED, false);
        database.update(TABLE_LISTS, contentValues, KEY_IS_PREFERRED + " = 1", null);
        contentValues.clear();
        contentValues.put(KEY_IS_PREFERRED, true);

        database.update(TABLE_LISTS, contentValues, KEY_NAME + " = '" + list.getName() + "'", null);
        database.close();
    }

    public void deleteList(List list) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_LISTS, KEY_NAME + " = '" + list.getName() + "'", null);
        database.close();
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: New Version!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        onCreate(db);
    }
}
