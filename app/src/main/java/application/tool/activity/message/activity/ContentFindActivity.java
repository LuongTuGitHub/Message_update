package application.tool.activity.message.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SearchView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.adapter.PeopleAdapter;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.object.Person;

import static application.tool.activity.message.module.Firebase.STATUS;

public class ContentFindActivity extends AppCompatActivity {
    private TextInputEditText edt;
    private RecyclerView rv;
    private DatabaseReference refDb;
    private FirebaseUser fUser;
    private StorageReference refStg;
    private PeopleAdapter adapter;
    private ArrayList<Person> account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_content_find);
        account = new ArrayList<>();
        adapter = new PeopleAdapter(account);
        Init();
        FrameLayout fBackground = findViewById(R.id.fBackground);
        Button button = findViewById(R.id.btExit);
        button.setOnClickListener(v -> finish());
        rv.setVisibility(View.GONE);
        rv.setLayoutManager(new LinearLayoutManager(ContentFindActivity.this, RecyclerView.VERTICAL, false));
        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                rv.setVisibility(View.VISIBLE);
                fBackground.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadPerson();
    }

    private void Init() {
        edt = findViewById(R.id.edtEnter);
        rv = findViewById(R.id.rvViewResult);
    }


    private void loadPerson() {
        refDb.child(Firebase.LIST_ACCOUNT).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    refDb.child(Firebase.PERSON).child(snapshot.getValue().toString().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                account.add(snapshot.getValue(Person.class));
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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
    protected void onResume() {
        super.onResume();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue("online");
    }
}