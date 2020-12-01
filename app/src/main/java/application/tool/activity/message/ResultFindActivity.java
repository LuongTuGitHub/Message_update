package application.tool.activity.message;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import application.tool.activity.message.adapter.ResultFindAdapter;
import application.tool.activity.message.algorithm.Find;
import application.tool.activity.message.object.FindResult;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Profile;

public class ResultFindActivity extends AppCompatActivity {
    Button exit, find;
    EditText input;
    ListView showResult;
    ArrayList<FindResult> findResults;
    ArrayList<FindResult> listPerson;
    DatabaseReference reference;
    ResultFindAdapter adapter;
    FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_find);
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        findResults = new ArrayList<>();
        listPerson = new ArrayList<>();
        loadAccount();
        adapter = new ResultFindAdapter(ResultFindActivity.this, findResults);
        String content = getIntent().getStringExtra("content");
        exit = findViewById(R.id.exitFind);
        find = findViewById(R.id.find);
        input = findViewById(R.id.inputContentFind);
        showResult = findViewById(R.id.showListResult);
        showResult.setAdapter(adapter);
        exit.setOnClickListener(v -> finish());
        if (content != null) {
            new Handler().postDelayed(() -> {
                for (int i = 0; i < listPerson.size(); i++) {
                    if (listPerson.get(i).getName().toLowerCase().contains(input.getText().toString().toLowerCase())) {
                        findResults.add(listPerson.get(i));
                        adapter.notifyDataSetChanged();
                    }
                }
            }, 2000);
        }
        find.setOnClickListener(v -> {
            if (input.getVisibility() == View.VISIBLE) {
                if (input.getText().toString().length() == 0) {
                    input.setVisibility(View.INVISIBLE);
                } else {
                    findResults.clear();
                    for (int i = 0; i < listPerson.size(); i++) {
                        if (listPerson.get(i).getName().toLowerCase().contains(input.getText().toString().toLowerCase())) {
                            findResults.add(listPerson.get(i));
                            adapter.notifyDataSetChanged();
                        }
                    }
                    input.setText("");
                    /////////////////// TODO
                }
            } else {
                input.setVisibility(View.VISIBLE);
            }
        });

    }

    public void loadAccount() {
        reference.child("list_account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        if (!person.getEmail().equals(user.getEmail())) {
                            reference.child("profile" + person.getEmail().hashCode()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        Profile profile = snapshot.getValue(Profile.class);
                                        if (profile != null) {
                                            listPerson.add(new FindResult(profile.getName(), person.getEmail()));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
}
