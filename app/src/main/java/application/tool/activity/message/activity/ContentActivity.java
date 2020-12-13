package application.tool.activity.message.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ContentViewPagerAdapter;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.notification.Token;

import static application.tool.activity.message.module.Firebase.STATUS;
import static application.tool.activity.message.module.Firebase.TOKEN;

public class ContentActivity extends AppCompatActivity {
    private BottomNavigationView navigation;
    private ViewPager vpContent;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Init();
        ContentViewPagerAdapter adapter =
                new ContentViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpContent.setAdapter(adapter);
        vpContent.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigation.getMenu().findItem(R.id.homeFragment).setChecked(true);
                        break;
                    case 1:
                        navigation.getMenu().findItem(R.id.conversationFragment).setChecked(true);
                        break;
                    case 2:
                        navigation.getMenu().findItem(R.id.notificationFragment).setChecked(true);
                        break;
                    case 3:
                        navigation.getMenu().findItem(R.id.extensionsFragment).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homeFragment:
                    vpContent.setCurrentItem(0);
                    break;
                case R.id.conversationFragment:
                    vpContent.setCurrentItem(1);
                    break;
                case R.id.notificationFragment:
                    vpContent.setCurrentItem(2);
                    break;
                case R.id.extensionsFragment:
                    vpContent.setCurrentItem(3);
                    break;
            }
            return true;
        });
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").setValue("online");
    }

    @SuppressLint("NonConstantResourceId")
    private void Init() {
        navigation = findViewById(R.id.bottomNavigationView);
        vpContent = findViewById(R.id.vpContent);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        refreshToken();
    }

    private void refreshToken() {
        refDb.child(TOKEN).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                .setValue(new Token(FirebaseInstanceId.getInstance().getToken()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("online");
    }

    /***
     * Functions onResume,onDestroy and onPause update status online or offline;
     * backPress back activity Content Activity if Person exist
     */
    @Override
    protected void onPause() {
        super.onPause();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("offline");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("online");
    }
}
