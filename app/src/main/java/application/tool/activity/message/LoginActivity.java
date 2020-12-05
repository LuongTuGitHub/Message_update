package application.tool.activity.message;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import application.tool.activity.message.object.Account;

public class LoginActivity extends AppCompatActivity {
    Button login, signUp, resetPassword;
    EditText email, password;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        login.setOnClickListener(v -> {
            if (password.getVisibility() == View.INVISIBLE) {
                password.setVisibility(View.VISIBLE);
            } else {
                if ((!email.getText().toString().isEmpty()) && (!password.getText().toString().isEmpty())) {
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            assert user != null;
                            if(user.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, ContentActivity.class);
                                startActivity(intent);
                            }else {
                                user.sendEmailVerification();
                                Toast.makeText(this, "Please! Check Verify Email. Open Box Inbox Your", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        signUp.setOnClickListener(v -> {
            if (password.getVisibility() == View.INVISIBLE) {
                password.setVisibility(View.VISIBLE);
            } else {
                if ((!email.getText().toString().isEmpty()) && (!password.getText().toString().isEmpty())) {
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                            //reference.child("user").push().setValue(email.getText().toString());
                            reference.child("list_account").push().setValue(email.getText().toString());
                            reference.child("account" + email.getText().toString().hashCode()).setValue(new Account(email.getText().toString(), password.getText().toString()));
                        }
                    });
                }
            }
        });
        resetPassword.setOnClickListener(v -> {
            if (password.getVisibility() == View.VISIBLE) {
                password.setVisibility(View.INVISIBLE);
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
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
    }
}