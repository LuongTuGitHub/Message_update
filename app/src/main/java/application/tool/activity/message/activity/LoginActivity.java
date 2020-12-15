package application.tool.activity.message.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.notification.Token;
import application.tool.activity.message.object.Account;

import static application.tool.activity.message.module.Firebase.ACCOUNT;
import static application.tool.activity.message.module.Firebase.LIST_ACCOUNT;
import static application.tool.activity.message.module.Firebase.PROFILE;
import static application.tool.activity.message.module.Firebase.TOKEN;

public class LoginActivity extends AppCompatActivity {
    public Button login, signUp, resetPassword;
    public TextInputEditText email;
    public TextInputEditText password;
    public TextInputLayout layout;
    public FirebaseAuth auth;
    public FirebaseUser fUser;
    public DatabaseReference refDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        login.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
            dialog.setView(R.layout.load);
            AlertDialog alertDialog = dialog.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            if (layout.getVisibility() == View.INVISIBLE) {
                layout.setVisibility(View.VISIBLE);
            } else {
                if ((!email.getText().toString().isEmpty()) && (!Objects.requireNonNull(password.getText()).toString().isEmpty())) {
                    alertDialog.show();
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fUser = auth.getCurrentUser();
                            assert fUser != null;
                            if (fUser.isEmailVerified()) {
                                refDb.child(PROFILE).child(email.getText().toString().hashCode() + "").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getValue() == null) {
                                            alertDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                            intent.putExtra("status",true);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            alertDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, ContentActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        alertDialog.dismiss();
                                    }
                                });
                            } else {
                                fUser.sendEmailVerification();
                                AlertDialog.Builder aBuilder = new AlertDialog.Builder(LoginActivity.this);
                                aBuilder.setView(R.layout.dr_verify_email);
                                alertDialog.dismiss();
                                aBuilder.create().show();
                            }
                        }else {
                            alertDialog.dismiss();
                        }
                    });
                }
            }
        });
        signUp.setOnClickListener(v -> {
            if (layout.getVisibility() == View.INVISIBLE) {
                layout.setVisibility(View.VISIBLE);
            } else {
                if ((!email.getText().toString().isEmpty()) && (!Objects.requireNonNull(password.getText()).toString().isEmpty())) {
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                            refDb.child(LIST_ACCOUNT).push().setValue(email.getText().toString());
                            refDb.child(ACCOUNT).child(email.getText().toString().hashCode() + "").setValue(new Account(email.getText().toString(), password.getText().toString()));
                            refDb.child(TOKEN).child(email.getText().toString().hashCode() + "").setValue(new Token(FirebaseInstanceId.getInstance().getToken()));
                        }
                    });
                }
            }
        });
        resetPassword.setOnClickListener(v -> {
            if (layout.getVisibility() == View.VISIBLE) {
                layout.setVisibility(View.INVISIBLE);
            } else {
                if (!email.getText().toString().isEmpty()) {
                    auth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void Init() {
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        resetPassword = findViewById(R.id.resetPassword);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        layout = findViewById(R.id.layoutPassword);
        auth = FirebaseAuth.getInstance();
        refDb = FirebaseDatabase.getInstance().getReference();
    }
}