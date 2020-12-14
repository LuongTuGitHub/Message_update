package application.tool.activity.message.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Account;

import static application.tool.activity.message.module.Firebase.ACCOUNT;

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputEditText passwordCurrent, newPassword, repeatPassword;
    TextView message;
    Button confirm, exit;
    FirebaseUser fUser;
    FirebaseAuth fAuth;
    DatabaseReference reference;
    Account account;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Create();
        loadAccount();
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
        aBuilder.setView(R.layout.load);
        final AlertDialog dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        exit.setOnClickListener(v -> finish());
        confirm.setOnClickListener(v -> {
            dialog.show();
            if (account != null) {
                if ((Objects.requireNonNull(passwordCurrent.getText()).toString().equals("") || Objects.requireNonNull(newPassword.getText()).toString().equals("") || Objects.requireNonNull(repeatPassword.getText()).toString().equals(""))) {
                    dialog.dismiss();
                    message.setText("Field Is Empty");
                } else {
                    if (passwordCurrent.getText().toString().equals(account.getPassword())) {
                        if (newPassword.getText().toString().length() > 6) {
                            if (newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                                new Handler().postDelayed(() -> fUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.cancel();
                                        message.setText("");
                                        reference.child(ACCOUNT).child("" + Objects.requireNonNull(fUser.getEmail()).hashCode()).setValue(new Account(fUser.getEmail(), newPassword.getText().toString()));
                                        Toast.makeText(this, "Change Success !", Toast.LENGTH_SHORT).show();
                                    }
                                }), 1500);
                            } else {
                                dialog.dismiss();
                                message.setText("New Password Different");
                            }
                        } else {
                            dialog.dismiss();
                            message.setText("Length Password Less Than 6");
                        }
                    } else {
                        dialog.dismiss();
                        message.setText("Password Current Wrong");
                    }
                }
            }
        });
    }

    private void Create() {
        passwordCurrent = findViewById(R.id.inputPasswordCurrent);
        newPassword = findViewById(R.id.inputNewPassword);
        repeatPassword = findViewById(R.id.repeatPassword);
        message = findViewById(R.id.messageChange);
        confirm = findViewById(R.id.confirmChangePasswordMessage);
        exit = findViewById(R.id.exitChangPassword);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadAccount() {
        reference.child(ACCOUNT).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    account = snapshot.getValue(Account.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}