package application.tool.activity.message.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import application.tool.activity.message.R;
import application.tool.activity.message.module.SQLiteImage;

public class ViewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        String key = getIntent().getStringExtra("bitmap");

        Button bt_exit = findViewById(R.id.bt_exit_view_image);
        Button bt_download = findViewById(R.id.bt_download_image);
        ImageView iv_show_image = findViewById(R.id.iv_show_image);
        SQLiteImage image = new SQLiteImage(getApplicationContext());
        StorageReference refStg = FirebaseStorage.getInstance().getReference();

        bt_exit.setOnClickListener(v -> finish());
        bt_download.setOnClickListener(v -> {
            String keyUUID = UUID.randomUUID()+"";
            MediaStore.Images.Media.insertImage(getContentResolver(),((BitmapDrawable) iv_show_image.getDrawable()).getBitmap(),keyUUID,null);
        });

        if(key!=null){
            if (image.checkExist(key)) {
                byte[] bytes = image.getImage(key);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv_show_image.setImageBitmap(bitmap);
            } else {
                refStg.child("post/" + key + ".png")
                        .getBytes(Long.MAX_VALUE)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                iv_show_image.setImageBitmap(bitmap);
                                image.Add(key, task.getResult());
                            }
                        });
            }
        }
    }
}