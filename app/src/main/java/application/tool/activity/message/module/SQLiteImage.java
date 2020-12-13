package application.tool.activity.message.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SQLiteImage extends SQLiteOpenHelper {
    private final static String DB_IMAGE = "image.db";
    private final static int VERSION = 1;
    public SQLiteImage(@Nullable Context context) {
        super(context, DB_IMAGE,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS IMAGE(ID INTEGER PRIMARY KEY AUTOINCREMENT,UUID VARCHAR(255),BYTES BLOG)");
    }
    public void Add(String key,byte[] bytes){
        String sql = "INSERT INTO IMAGE VALUES(null,?,?)";
        SQLiteStatement statement = getWritableDatabase().compileStatement(sql);
        statement.bindString(1,key);
        statement.bindBlob(2,bytes);
        statement.executeInsert();
    }
    public byte[] getImage(String key){
        byte[] bytes = null;
        SQLiteDatabase database = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT BYTES FROM IMAGE WHERE UUID LIKE ?",new String[]{key});
        cursor.moveToFirst();
        bytes = cursor.getBlob(0);
        return bytes;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean checkExist(String key){
        SQLiteDatabase database = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT * FROM IMAGE WHERE UUID LIKE ?",new String[]{key});
        if(cursor!=null){
            return cursor.getCount() != 0;
        }
        return false;
    }
}
