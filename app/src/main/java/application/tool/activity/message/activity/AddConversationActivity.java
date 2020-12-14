package application.tool.activity.message.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;

import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.PERSON;

public class AddConversationActivity extends AppCompatActivity implements ItemOnClickListener {
    Button btExist, Create;
    ArrayList<Person> add;
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
        Create.setOnClickListener(v -> {
            if(add.size()>=2){
                BottomSheetDialog dialog = new BottomSheetDialog(AddConversationActivity.this);
                dialog.setContentView(R.layout.load);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.show();
                ArrayList<PersonInConversation> person = new ArrayList<>();
                for (int i = 0; i <add.size() ; i++) {
                    person.add(new PersonInConversation(null,add.get(i).getEmail()));
                }
                person.add(new PersonInConversation(null,fUser.getEmail()));
                refDb.child(PERSON).child(fUser.getEmail().hashCode()+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null){
                            Person ps = snapshot.getValue(Person.class);
                            if(ps!=null){
                                Conversation conversation = new Conversation(person,new ArrayList<>(),"Create by :"+ps.getName());
                                refDb.child(CONVERSATION).push().setValue(conversation).addOnCompleteListener(task -> {
                                    if(task.isSuccessful()){
                                        dialog.cancel();
                                        adapter.notifyDataSetChanged();
                                        add.clear();
                                        Toast.makeText(AddConversationActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.cancel();
                        Toast.makeText(AddConversationActivity.this, "Fail !", Toast.LENGTH_SHORT).show();
                    }
                });
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
        add = new ArrayList<>();
        adapter = new AddConversationAdapter(listFri,AddConversationActivity.this,this);
    }

    @Override
    public void onClickItem(View view, int position) {
        if(check(listFri.get(position))){
            add.remove(listFri.get(position));
        }else {
            add.add(listFri.get(position));
        }
    }
    public boolean check(Person person){
        for (int i = 0; i < add.size() ; i++) {
            if(add.get(i).getEmail().equals(person.getEmail())){
                return true;
            }
        }
        return false;
    }
}