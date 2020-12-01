package application.tool.activity.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import application.tool.activity.message.sqlite.AccountShare;

public class StartAppActivity extends AppCompatActivity {
    private final static long TIME_DELAY = 2000;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        auth = FirebaseAuth.getInstance();
        ImageView imageView = findViewById(R.id.imageView4);
        Animation animation = AnimationUtils.loadAnimation(StartAppActivity.this,R.anim.anim_start_app);
        imageView.startAnimation(animation);
        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> list = uri.getPathSegments();
            if (list != null) {
                String email = list.get(list.size() - 1);
                if (email != null) {
                    if (new AccountShare(StartAppActivity.this).getAccount() != null) {
                        auth.signInWithEmailAndPassword(new AccountShare(StartAppActivity.this).getAccount()[0], new AccountShare(StartAppActivity.this).getAccount()[1]).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(StartAppActivity.this, ViewProfilePersonActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(StartAppActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        Intent intent = new Intent(StartAppActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }else {
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
}
