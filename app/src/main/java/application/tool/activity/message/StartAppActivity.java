package application.tool.activity.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class StartAppActivity extends AppCompatActivity {
    private final static long TIME_DELAY = 2000;
    private FirebaseUser user;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ImageView imageView = findViewById(R.id.imageView4);
        Animation animation = AnimationUtils.loadAnimation(StartAppActivity.this,R.anim.anim_start_app);
        imageView.startAnimation(animation);
        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> list = uri.getPathSegments();
            if(user!=null){
                if(user.isEmailVerified()){
                    Intent intent = new Intent(StartAppActivity.this,ViewProfilePersonActivity.class);
                    intent.putExtra("email",list.get(list.size()-1));
                    startActivity(intent);
                }else {
                    user.sendEmailVerification();
                }
            }
        }else {
            new Handler().postDelayed(() -> {
                if(user!=null){
                    if(user.isEmailVerified()){
                       Intent intent = new Intent(StartAppActivity.this, ContentActivity.class);
                       startActivity(intent);
                    }else {
                        user.sendEmailVerification();
                        Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            },TIME_DELAY);
        }
    }
}
