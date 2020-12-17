package application.tool.activity.message.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;

public class SQLiteText extends SQLiteOpenHelper {
    private final static String NAME_TABLE = "avatar.db";
    private final static int VERSION = 1;

    public SQLiteText(Context context) {
        super(context, NAME_TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("CREATE TABLE IF NOT EXISTS AVATAR(INT PRIMARY KEY AUTOINCREMENT,EMAIL VARCHAR(255),UUID VARCHAR(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkExist(String key) {
        SQLiteDatabase database = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT * FROM AVATAR WHERE EMAIL LIKE ?", new String[]{key});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public String getKey(String email) {
        SQLiteDatabase database = getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT * FROM AVATAR WHERE EMAIL LIKE ?", new String[]{email});
        cursor.moveToFirst();
        return cursor.getString(2);
    }

    public void updateData(String email, String UUID) {
        SQLiteDatabase database = getReadableDatabase();
        if(checkExist(email)){
            database.execSQL("UPDATE AVATAR SET UUID = ? WHERE EMAIL = ?",new String[]{UUID,email});
        }else {
            database.execSQL("INSERT INTO AVATAR VALUES(null,?,?)",new String[]{email,UUID});
        }
    }
}
