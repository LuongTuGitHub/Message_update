package application.tool.activity.message.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ExtensionAdapter;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.object.Extension;

import static application.tool.activity.message.module.Firebase.STATUS;
import static application.tool.activity.message.module.Notification.MESSAGE;
import static application.tool.activity.message.module.Notification.REQUEST;
import static application.tool.activity.message.module.Notification.RESPONSE;

public class ExtensionActivity extends AppCompatActivity implements ItemOnClickListener {
    private ArrayList<Extension> extensions;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private Button bt_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        extensions = new ArrayList<>();
        extensions.add(new Extension(R.drawable.padlock));
        extensions.add(new Extension(R.drawable.create));
        extensions.add(new Extension(R.drawable.ic_baseline_qr_code_24));
        extensions.add(new Extension(R.drawable.song));
        extensions.add(new Extension(R.drawable.logout));
        RecyclerView rvEx = findViewById(R.id.rvExtension);
        ExtensionAdapter adapter = new ExtensionAdapter(extensions, this);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvEx.setLayoutManager(manager);
        rvEx.setAdapter(adapter);
    }

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
                Intent scanQRCode = new Intent(ExtensionActivity.this, ScanQRCodeActivity.class);
                startActivity(scanQRCode);
                break;
        }
    }

}