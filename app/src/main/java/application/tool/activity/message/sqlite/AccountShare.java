package application.tool.activity.message.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountShare extends SQLiteOpenHelper {
    private final static String _NAME = "account.db";
    private final static int _VERSION = 1;

    public AccountShare(Context context) {
        super(context, _NAME, null, _VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS user_current");
        db.execSQL("CREATE TABLE user_current(user varchar(255),password varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void addAccount(String email, String password) {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteDatabase read = getReadableDatabase();
        if (read.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{"user_current"}).getCount() == 0) {
            database.execSQL("CREATE TABLE user_current(user varchar(255),password varchar(255))");
        } else {
            database.execSQL("DROP TABLE IF EXISTS user_current");
            database.execSQL("CREATE TABLE user_current(user varchar(255),password varchar(255))");
            database.execSQL("INSERT INTO user_current VALUES(?,?)", new String[]{email, password});
        }
    }

    @SuppressLint("Recycle")
    public String[] getAccount() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor count = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{"user_current"});
        if ((count != null) && (count.getCount() != 0)) {
            String[] array = new String[2];
            Cursor cursor = database.rawQuery("SELECT USER,PASSWORD FROM user_current ", null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                array[0] = cursor.getString(0);
                array[1] = cursor.getString(1);
                return array;
            }
        }
        return null;
    }

    public void dropAccount() {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS user_current");
        database.execSQL("CREATE TABLE user_current(USER VARCHAR(255),PASSWORD VARCHAR(255))");
    }
}
