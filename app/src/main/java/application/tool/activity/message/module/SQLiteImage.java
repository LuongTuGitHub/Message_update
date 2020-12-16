package application.tool.activity.message.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

import application.tool.activity.message.R;

public class SQLiteImage extends SQLiteOpenHelper {
    private final static String DB_IMAGE = "image.db";
    private final static int VERSION = 1;
    Context context;

    public SQLiteImage(@Nullable Context context) {
        super(context, DB_IMAGE, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS IMAGE(ID INTEGER PRIMARY KEY AUTOINCREMENT,UUID VARCHAR(255),BYTES BLOG)");
    }

    public void Add(String key, byte[] bytes) {
        String sql = "INSERT INTO IMAGE VALUES(null,?,?)";
        SQLiteStatement statement = getWritableDatabase().compileStatement(sql);
        statement.bindString(1, key);
        statement.bindBlob(2, bytes);
        statement.executeInsert();
    }

    public byte[] getImage(String key) {
        byte[] bytes = new byte[0];
        SQLiteDatabase database = getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = database
                .rawQuery("SELECT UUID,BYTES FROM IMAGE WHERE UUID LIKE ?",
                        new String[]{key});
        cursor.moveToFirst();
        bytes = cursor.getBlob(1);
        if (bytes == null) {
            Resources res = context.getResources();
            @SuppressLint("UseCompatLoadingForDrawables") Drawable d = res.getDrawable(R.drawable.image_placeholder);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bytes = stream.toByteArray();
        }
        return bytes;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean checkExist(String key) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='{IMAGE}'", null);
        if(c.getCount()>0){
            @SuppressLint("Recycle") Cursor cursor = database.rawQuery("SELECT UUID FROM IMAGE WHERE UUID LIKE ?", new String[]{key});
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                return true;
            }
            return false;
        }else {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL("DROP TABLE  IF EXISTS IMAGE");
            sqLiteDatabase.execSQL("CREATE TABLE IMAGE(ID INTEGER PRIMARY KEY AUTOINCREMENT,UUID VARCHAR(255),BYTES BLOG)");
        }
        return  false; //didjt mej ngu a t quen lenh :)))) ok chwa
    }
}