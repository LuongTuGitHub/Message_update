package application.tool.activity.message.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ExtensionAdapter;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.object.Extension;

import static application.tool.activity.message.module.Firebase.STATUS;

public class ExtensionActivity extends AppCompatActivity implements ItemOnClickListener {
    private final static int PERMISSION_CAMERA = 12;
    private ArrayList<Extension> extensions;
    private FirebaseUser fUser;
    private DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        Button bt_exit = findViewById(R.id.bt_exit_extension);
        ImageSlider slider = findViewById(R.id.slider);

        List<SlideModel> list = new ArrayList<>();
        list.add(new SlideModel(R.drawable._5,null));
        list.add(new SlideModel(R.drawable.pikmail_emails_to_pictures_using_kotlin_lede,null));
        list.add(new SlideModel(R.drawable.def40d80_cb4c_11e9_971a_7434089990ed,null));
        list.add(new SlideModel(R.drawable.git_reset_origin_to_commit,null));
        list.add(new SlideModel(R.drawable._288755792019456,null));
        list.add(new SlideModel(R.drawable.tong_quan_nodejs_trungquandev_02,null));
        list.add(new SlideModel(R.drawable.cafedev_angularjs_profile,null));
        list.add(new SlideModel(R.drawable.s3uitx6rdv7sod1g2acz,null));
        list.add(new SlideModel(R.drawable.vuejs,null));
        slider.setImageList(list,ScaleTypes.CENTER_CROP);


        bt_exit.setOnClickListener(v -> finish());

        extensions = new ArrayList<>();
        extensions.add(new Extension(R.drawable.padlock));
        extensions.add(new Extension(R.drawable.create));
        extensions.add(new Extension(R.drawable.ic_baseline_qr_code_24));
        extensions.add(new Extension(R.drawable.song));
        extensions.add(new Extension(R.drawable.logout));
        RecyclerView rvEx = findViewById(R.id.rvExtension);
        ExtensionAdapter adapter = new ExtensionAdapter(extensions, this);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rvEx.setLayoutManager(manager);
        rvEx.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClickItem(View view, int position) {
        switch (extensions.get(position).getID()) {
            case R.drawable.logout:
                refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                        .setValue("offline")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(ExtensionActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                break;
            case R.drawable.create:
                Intent intent = new Intent(ExtensionActivity.this, CreateQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.drawable.padlock:
                Intent changePassword = new Intent(ExtensionActivity.this, ChangePasswordActivity.class);
                startActivity(changePassword);
                break;
            case R.drawable.ic_baseline_qr_code_24:
                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    Intent scanQRCode = new Intent(ExtensionActivity.this, ScanQRCodeActivity.class);
                    startActivity(scanQRCode);
                }else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA);
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CAMERA){
            if(checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
                Intent scanQRCode = new Intent(ExtensionActivity.this, ScanQRCodeActivity.class);
                startActivity(scanQRCode);
            }
        }
    }
}