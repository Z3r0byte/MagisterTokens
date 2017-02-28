package eu.z3r0byteapps.magistertokens.Util;

import android.content.ContentValues;
import android.content.Context;
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

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Tokens";
    private static final String TABLE_TOKENS = "tokens";

    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN_ID = "tokenId";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_LIST_NAME = "listname";

    public TokenDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CALENDAR_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_TOKENS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TOKEN_ID + " INTEGER,"
                + KEY_TOKEN + " STRING,"
                + KEY_LIST_NAME + " STRING"
                + ")";
        db.execSQL(CREATE_CALENDAR_TABLE);
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

    }


    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: New Version!");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKENS);
        onCreate(db);
    }
}
