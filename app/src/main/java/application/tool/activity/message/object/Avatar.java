package application.tool.activity.message.object;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Avatar {
    private String email, type;
    FirebaseStorage storage;
    StorageReference reference;
    public Avatar(){
        reference = FirebaseStorage.getInstance().getReference();
    }
    public Avatar(String email) {
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        this.email = email;
    }

    public Avatar(String email, String type) {
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        this.type = type;
        this.email = email;
    }

    public void setAvatar(ImageView avatar) {
        if (type != null) {
            reference.child(type + "/" + email.hashCode() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    byte[] bytes = task.getResult();
                    if (bytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        avatar.setImageBitmap(bitmap);
                    }
                }
            });
        } else {
            reference.child("avatar/" + email.hashCode() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    byte[] bytes = task.getResult();
                    if (bytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        avatar.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }
    public  void getMessageImage(String key,ImageView view){
        reference.child("image/" + key+ ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                byte[] bytes = task.getResult();
                if (bytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    view.setImageBitmap(bitmap);
                }
            }
        });
    }
}
