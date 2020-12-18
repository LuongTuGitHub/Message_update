package application.tool.activity.message.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Profile;

import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Firebase.PROFILE;
import static application.tool.activity.message.module.Firebase.STATUS;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private EditText name, phone, day, address;
    private Button confirm, btExit;
    private boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        status = getIntent().getBooleanExtra("status", false);
        Init();
        confirm.setOnClickListener(v -> {
            if ((!name.getText().toString().equals("")) && (!phone.getText().toString().equals("")) && (!day.getText().toString().equals(""))
                    && (!address.getText().toString().equals(""))) {
                refDb.child(Firebase.PROFILE).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(
                        new Profile(name.getText().toString(), phone.getText().toString(), day.getText().toString(), address.getText().toString()));
                refDb.child(PERSON).child(fUser.getEmail().hashCode() + "").setValue(
                        new Person(name.getText().toString(), phone.getText().toString(), day.getText().toString()
                                , address.getText().toString(), fUser.getEmail()));
                Intent intent = new Intent(ProfileActivity.this, ContentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btExit.setOnClickListener(v -> {
            refDb.child(PROFILE).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if (status) {
                            Intent intent = new Intent(ProfileActivity.this, ContentActivity.class);
                            startActivity(intent);
                        }
                    }
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private void Init() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        day = findViewById(R.id.day);
        address = findViewById(R.id.address);
        confirm = findViewById(R.id.confirmChangeProfile);
        btExit = findViewById(R.id.bt_exit);
    }

    @Override
    public void onBackPressed() {
        refDb.child(PROFILE).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (status) {
                        Intent intent = new Intent(ProfileActivity.this, ContentActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ProfileActivity.super.onBackPressed();
                    }
                } else {
                    ProfileActivity.super.onBackPressed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("online");
    }
}