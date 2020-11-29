package application.tool.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import application.tool.activity.message.sqlite.AccountShare;

public class StartAppActivity extends AppCompatActivity {
    private final static long TIME_DELAY = 1500;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        auth = FirebaseAuth.getInstance();
        new Handler().postDelayed(() -> {
            String[] account = new AccountShare(StartAppActivity.this).getAccount();
            if (account != null) {
                auth.signInWithEmailAndPassword(account[0], account[1]).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(StartAppActivity.this, ContentActivity.class);
                        intent.putExtra("email", account[0]);
                        intent.putExtra("password", account[1]);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(StartAppActivity.this, MainActivity.class);
                        new AccountShare(StartAppActivity.this).dropAccount();
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                Intent intent = new Intent(StartAppActivity.this, MainActivity.class);
                new AccountShare(StartAppActivity.this).dropAccount();
                startActivity(intent);
                finish();
            }
        }, TIME_DELAY);
    }
}
