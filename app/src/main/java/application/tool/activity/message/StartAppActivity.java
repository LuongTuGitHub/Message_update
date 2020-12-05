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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView imageView = findViewById(R.id.imageView4);
        Animation animation = AnimationUtils.loadAnimation(StartAppActivity.this,R.anim.anim_start_app);
        imageView.startAnimation(animation);
        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> list = uri.getPathSegments();
            if(user!=null){
                Intent intent = new Intent(StartAppActivity.this,ViewProfilePersonActivity.class);
                intent.putExtra("email",list.get(list.size()-1));
                startActivity(intent);
            }
        }else {
            new Handler().postDelayed(() -> {
                Intent intent;
                if(user!=null){
                    intent = new Intent(StartAppActivity.this, ContentActivity.class);
                }else{
                    intent = new Intent(StartAppActivity.this, LoginActivity.class);
                }
                startActivity(intent);
            },TIME_DELAY);
        }
    }
}
