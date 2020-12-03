package application.tool.activity.message;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
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

import application.tool.activity.message.object.Account;
import application.tool.activity.message.sqlite.AccountShare;

public class ChangePasswordPassword extends AppCompatActivity {
    TextInputEditText passwordCurrent, newPassword, repeatPassword;
    TextView message;
    Button confirm, exit;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    Account account;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Create();
        loadAccount();
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ChangePasswordPassword.this);
        View view = LayoutInflater.from(ChangePasswordPassword.this).inflate(R.layout.alert_delay, null);
        aBuilder.setView(view);
        final AlertDialog dialog = aBuilder.create();
        exit.setOnClickListener(v -> finish());
        confirm.setOnClickListener(v -> {
            dialog.show();
            if (account != null) {
                if ((passwordCurrent.getText().toString().equals("") || newPassword.getText().toString().equals("") || repeatPassword.getText().toString().equals(""))) {
                    dialog.dismiss();
                    message.setText("Field Is Empty");
                } else {
                    if (passwordCurrent.getText().toString().equals(account.getPassword())) {
                        if (newPassword.getText().toString().length() > 6) {
                            if (newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                                new Handler().postDelayed(() -> user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        message.setText("");
                                        reference.child("account" + user.getEmail().hashCode()).setValue(new Account(user.getEmail(), newPassword.getText().toString()));
                                        Toast.makeText(this, "Change Success !", Toast.LENGTH_SHORT).show();
                                        if (new AccountShare(ChangePasswordPassword.this).getAccount() != null) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordPassword.this);
                                            View keepLoginAlert = LayoutInflater.from(ChangePasswordPassword.this).inflate(R.layout.alert_keep_login, null);
                                            builder.setView(keepLoginAlert);
                                            final AlertDialog alert = builder.create();
                                            Button keepLogin = keepLoginAlert.findViewById(R.id.keepLogin);
                                            Button notKeep = keepLoginAlert.findViewById(R.id.notSave);
                                            keepLogin.setOnClickListener(v1 -> {
                                                alert.dismiss();
                                                new AccountShare(ChangePasswordPassword.this).addAccount(user.getEmail(),newPassword.getText().toString());
                                            });
                                            notKeep.setOnClickListener(v12 -> {
                                                new AccountShare(ChangePasswordPassword.this).dropAccount();
                                                alert.dismiss();
                                            });
                                            alert.show();
                                        }
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
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
    }

    public void loadAccount() {
        reference.child("account" + user.getEmail().hashCode()).addValueEventListener(new ValueEventListener() {
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
