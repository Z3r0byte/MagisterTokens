package eu.z3r0byteapps.magistertokens.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import eu.z3r0byteapps.magistertokens.Container.List;
import eu.z3r0byteapps.magistertokens.Container.Token;

/**
 * Created by bas on 14-2-17.
 */

public class TokenDatabase extends SQLiteOpenHelper {

    private static final String TAG = "TokenDatabase";

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "Database";

    private static final String TABLE_LISTS = "lists";
    private static final String TABLE_TOKENS = "tokens";

    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN_ID = "tokenId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LIST_NAME = "listname";

    private static final String KEY_NAME = "name";
    private static final String KEY_IS_PREFERRED = "isPreferred";
    private static final String KEY_AMOUNT_OF_TOKENS = "amountOfTokens";

    public TokenDatabase(Context context) {
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

    public void addItems(Token[] tokens, String listname) {
        if (tokens.length == 0 || tokens == null) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Log.d(TAG, "addItems: amount of items: " + tokens.length);

        for (Token item :
                tokens) {

            contentValues.put(KEY_TOKEN_ID, item.getId());
            contentValues.put(KEY_TOKEN, item.getToken());
            contentValues.put(KEY_LIST_NAME, listname);


            db.insert(TABLE_TOKENS, null, contentValues);

        }
        db.close();
    }

    public Integer getAmountOfTokens(String listname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_TOKENS + " WHERE " + KEY_LIST_NAME + " = '" + listname + "'";
        Cursor cursor = db.rawQuery(Query, null);
        int amount = cursor.getCount();
        cursor.close();
        db.close();
        return amount;
    }

    public Token[] getTokens(String searchQuery, String listname) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TABLE_TOKENS + " where " + KEY_LIST_NAME + " = '" + listname + "' AND " + KEY_TOKEN_ID + " LIKE '%" + searchQuery + "%'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                Token[] tokens = new Token[cursor.getCount()];
                int i = 0;
                do {
                    String token = cursor.getString(cursor.getColumnIndex(KEY_TOKEN));
                    Integer id = cursor.getInt(cursor.getColumnIndex(KEY_TOKEN_ID));
                    tokens[i] = new Token(id, token);

                    i++;
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
                return tokens;
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

    public void deleteTokensOfList(List list) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_TOKENS, KEY_LIST_NAME + " = '" + list.getName() + "'", null);
        database.close();
    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: New Version!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKENS);
        onCreate(db);
    }
}
