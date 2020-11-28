package application.tool.activity.message;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

import application.tool.activity.message.check.CheckFormatDate;
import application.tool.activity.message.object.Profile;

public class EditActivity extends AppCompatActivity {
    TextInputEditText inputName, inputDay, inputAddress;
    Button button, confirm, dialogDay;
    FirebaseAuth firebaseAuth;
    TextView message;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    DatePickerDialog.OnDateSetListener listener;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        Create();
        loadProfileOld();
        button.setOnClickListener(v -> finish());
        confirm.setOnClickListener(v -> {
            if (new CheckFormatDate().checkFormat(Objects.requireNonNull(inputDay.getText()).toString())) {
                if ((!Objects.requireNonNull(inputName.getText()).toString().equals("")) && (!inputDay.getText().toString().equals("")) && (!Objects.requireNonNull(inputAddress.getText()).toString().equals(""))) {
                    if (inputName.getText().toString().length() < 40 && (inputName.getText().toString().length() > 6)) {
                        Profile profile = new Profile(inputName.getText().toString(), inputDay.getText().toString(), inputAddress.getText().toString());
                        reference.child("profile" + Objects.requireNonNull(user.getEmail()).hashCode()).setValue(profile).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(inputName.getText().toString()).build());
                                message.setVisibility(View.GONE);
                                Intent intent = new Intent();
                                intent.putExtra("AAA", "refresh");
                                setResult(2, intent);
                                Toast.makeText(EditActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Fail !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText("Name Disable");
                    }
                } else {
                    message.setVisibility(View.VISIBLE);
                    message.setText("Field Is Empty");
                }
            } else {
                message.setVisibility(View.VISIBLE);
                message.setText("Format Day Error");
            }
        });
        dialogDay.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMouth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(EditActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, listener, year, month, dayOfMouth);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });
        listener = (view, year, month, dayOfMonth) -> inputDay.setText(dayOfMonth + "/" + month + "/" + year);
    }

    public void Create() {
        button = findViewById(R.id.returnEdit);
        inputAddress = findViewById(R.id.inputAddress);
        inputName = findViewById(R.id.inputName);
        inputDay = findViewById(R.id.inputDay);
        confirm = findViewById(R.id.confirm);
        message = findViewById(R.id.errorFormat);
        dialogDay = findViewById(R.id.dialogDay);
    }

    private void loadProfileOld() {
        reference.child("profile" + Objects.requireNonNull(user.getEmail()).hashCode()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Profile profile = snapshot.getValue(Profile.class);
                    assert profile != null;
                    if (!profile.getName().equals("")) {
                        inputName.setText(profile.getName());
                    }
                    if (!profile.getAddress().equals("")) {
                        inputAddress.setText(profile.getAddress());
                    }
                    if (!profile.getDay().equals("")) {
                        inputDay.setText(profile.getDay());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
