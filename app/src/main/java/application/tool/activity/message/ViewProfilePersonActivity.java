package application.tool.activity.message;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.adapter.SelectAdapter;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Profile;
import application.tool.activity.message.object.Select;

public class ViewProfilePersonActivity extends AppCompatActivity {
    private String email;
    FirebaseUser user;
    DatabaseReference reference;
    ListView listProfile;
    Button addFriend, exit;
    SelectAdapter adapter;
    ArrayList<Select> arrayList;
    ImageView avatar, background;
    String to = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile_person);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        arrayList = new ArrayList<>();
        adapter = new SelectAdapter(arrayList);
        String email = getIntent().getStringExtra("email");
        to = getIntent().getStringExtra("to");
        if (email == null) {
            Intent intent = new Intent(ViewProfilePersonActivity.this, StartAppActivity.class);
            startActivity(intent);
            finish();
        }
        this.email = email;
        Reference();
        new Avatar(email, "avatar").setAvatar(avatar);
        new Avatar(email, "background").setAvatar(background);
        reference.child("profile" + email.hashCode()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Profile profile = snapshot.getValue(Profile.class);
                    arrayList.add(new Select(R.drawable.ic_baseline_person_24, profile.getName()));
                    String year = profile.getDay().substring(profile.getDay().length() - 4);
                    arrayList.add(new Select(R.drawable.age, Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(year) + " Year Old"));
                    arrayList.add(new Select(R.drawable.ic_baseline_location_on_24, profile.getAddress()));
                    arrayList.add(new Select(R.drawable.email_view_profile, email));
                    arrayList.add(new Select(R.drawable.ic_baseline_calendar_today_24, profile.getDay()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (user.getEmail().equals(email)) {
            addFriend.setVisibility(View.GONE);
        }
        loadFriend();
        exit.setOnClickListener(v -> {
            if (to.equals("")) {
                Intent intent = new Intent(ViewProfilePersonActivity.this, ContentActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        });
        addFriend.setOnClickListener(v -> {
            reference.child("friend" + Objects.requireNonNull(user.getEmail()).hashCode()).push().setValue(new Person(1, email));
            reference.child("friend" + email).push().setValue(new Person(1, user.getEmail()));
            Intent intent = new Intent(ViewProfilePersonActivity.this, StartAppActivity.class);
            startActivity(intent);
            finish();
        });


    }

    private void Reference() {
        exit = findViewById(R.id.exitView);
        addFriend = findViewById(R.id.addFriend);
        avatar = findViewById(R.id.avatarPerson);
        background = findViewById(R.id.backgroundPerson);
        listProfile = findViewById(R.id.listProfilePerson);
        listProfile.setAdapter(adapter);
    }


    public void loadFriend() {
        reference.child("friend" + email.hashCode()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (!user.getEmail().equals(email)) {
                            if (person.getEmail().equals(user.getEmail())) {
                                addFriend.setVisibility(View.GONE);
                            }
                        } else {
                            addFriend.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (to.equals("")) {
            Intent intent = new Intent(ViewProfilePersonActivity.this, ContentActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
