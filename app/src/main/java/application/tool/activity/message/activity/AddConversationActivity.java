package application.tool.activity.message.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.AddConversationAdapter;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.object.Person;

import static application.tool.activity.message.module.Firebase.PERSON;

public class AddConversationActivity extends AppCompatActivity {
    Button btExist, Create;
    EditText edtSearch;
    RecyclerView rv;
    ArrayList<Person> listFri;
    ArrayList<String> list;
    AddConversationAdapter adapter;
    FirebaseUser fUser;
    DatabaseReference refDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_conversation);
        Init();
        btExist.setOnClickListener(v -> finish());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        rv.setHasFixedSize(true);
        loadFriend();
    }

    private void loadFriend() {
        refDb.child(Firebase.LIST_FRIEND).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.getValue()!=null){
                            refDb.child(PERSON).child(snapshot.getValue().toString().hashCode()+"")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.getValue()!=null){
                                                Person person = snapshot.getValue(Person.class);
                                                if(person!=null){
                                                    listFri.add(person);
                                                    adapter.notifyDataSetChanged();
                                                }
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

    private void Init() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb  = FirebaseDatabase.getInstance().getReference();
        btExist = findViewById(R.id.bt_cancel_exit);
        Create = findViewById(R.id.bt_create);
        rv = findViewById(R.id.rvFriend);
        listFri = new ArrayList<>();
        list = new ArrayList<>();
        adapter = new AddConversationAdapter(listFri,AddConversationActivity.this);
    }
}