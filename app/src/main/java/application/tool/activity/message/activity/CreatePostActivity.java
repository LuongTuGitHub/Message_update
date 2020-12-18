package application.tool.activity.message.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import application.tool.activity.message.R;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.object.Post;

import static application.tool.activity.message.module.Firebase.STATUS;

public class CreatePostActivity extends AppCompatActivity {
    private final static int SELECT_IMAGE = 99;
    private final static int CAPTURE_IMAGE = 100;
    private final static int PERMISSION = 11;
    private Button exit, create, btSelectImage;
    private EditText etTitle, etBody, etHashTag;
    private ImageView ivImage;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private byte[] bytes;
    private TextView tvError;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        Init();
        exit.setOnClickListener(v -> finish());
        btSelectImage.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(CreatePostActivity.this);
            dialog.setContentView(R.layout.bt_dialog_method);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            Button camera = dialog.findViewById(R.id.btCamera);
            Button gallery = dialog.findViewById(R.id.btMethodGallery);
            assert camera != null;
            camera.setOnClickListener(v12 -> {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAPTURE_IMAGE);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION);
                    }
                }
                dialog.cancel();
            });
            assert gallery != null;
            gallery.setOnClickListener(v1 -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, SELECT_IMAGE);
                dialog.cancel();
            });
            dialog.show();
        });
        create.setOnClickListener(v -> {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(CreatePostActivity.this);
            aBuilder.setView(R.layout.load);
            AlertDialog alertDialog = aBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            alertDialog.show();
            String key = UUID.randomUUID().toString();
            if ((!etTitle.getText().toString().equals("")) && (!etBody.getText().toString().equals(""))) {
                if (bytes != null) {
                    refStg.child("post/" + key + ".png").putBytes(bytes).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            refDb.child(Firebase.POST).child(key).setValue(new Post(
                                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), etTitle.getText().toString(), etBody.getText().toString(),
                                    key, etHashTag.getText().toString())).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    etBody.setText("");
                                    etTitle.setText("");
                                    etHashTag.setText("");
                                    ivImage.setImageResource(R.drawable.ic_launcher_foreground);
                                }
                                alertDialog.dismiss();
                            });
                        }
                    });
                } else {
                    refDb.child(Firebase.POST).child(key).setValue(new Post(
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(), etTitle.getText().toString(), etBody.getText().toString(),
                            null, etHashTag.getText().toString())).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Success !", Toast.LENGTH_SHORT).show();
                            etBody.setText("");
                            etTitle.setText("");
                            etHashTag.setText("");
                            ivImage.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                        alertDialog.dismiss();
                    });
                }
                tvError.setVisibility(View.INVISIBLE);
            } else {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Field Empty");
                alertDialog.dismiss();
            }
        });
    }

    private void Init() {
        exit = findViewById(R.id.btBack);
        create = findViewById(R.id.btCreate);
        tvError = findViewById(R.id.tvError);
        ivImage = findViewById(R.id.tvViewImage);
        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBodyText);
        etHashTag = findViewById(R.id.etHashTag);
        btSelectImage = findViewById(R.id.btSelectImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ivImage.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    bytes = stream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CAPTURE_IMAGE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ivImage.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bytes = stream.toByteArray();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("online");
    }
}