package application.tool.activity.message.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import static application.tool.activity.message.module.Firebase.PROFILE;

public class StartAppActivity extends AppCompatActivity {
    private final static long TIME_DELAY = 2000;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        ImageView imageView = findViewById(R.id.imageView4);
        Animation animation = AnimationUtils.loadAnimation(StartAppActivity.this, R.anim.anim_start_app);
        imageView.startAnimation(animation);
        new Handler().postDelayed(() -> {
            if (fUser != null) {
                if (fUser.isEmailVerified()) {
                    refDb.child(PROFILE).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() == null) {
                                        Intent intent = new Intent(StartAppActivity.this, ProfileActivity.class);
                                        intent.putExtra("status", true);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(StartAppActivity.this, ContentActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                } else {
                    fUser.sendEmailVerification();
                    Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Intent intent = new Intent(StartAppActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, TIME_DELAY);
    }
}
