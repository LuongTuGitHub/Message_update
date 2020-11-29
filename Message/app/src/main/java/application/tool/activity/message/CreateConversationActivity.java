package application.tool.activity.message;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.adapter.FriendAdapter;
import application.tool.activity.message.check.CheckConversation;
import application.tool.activity.message.check.CheckSelected;
import application.tool.activity.message.check.CheckUserInConversation;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.DeniedSeenMessage;
import application.tool.activity.message.object.MessageForConversation;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;

public class CreateConversationActivity extends AppCompatActivity {
    FirebaseUser user;
    DatabaseReference reference;
    Button back, clear, confirm;
    ListView listFriend;
    TextView showList;
    FriendAdapter adapter;
    String list = "";
    ArrayList<ArrayList<PersonInConversation>> lists;
    ArrayList<PersonInConversation> person;
    ArrayList<String> friend;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conversation);
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        back = findViewById(R.id.returnCreateConversation);
        back.setOnClickListener(v -> finish());
        person = new ArrayList<>();
        lists = new ArrayList<>();
        showList = findViewById(R.id.showListSelect);
        friend = new ArrayList<>();
        listFriend = findViewById(R.id.listFriend);
        clear = findViewById(R.id.clearList);
        confirm = findViewById(R.id.confirmAddConversation);
        adapter = new FriendAdapter(CreateConversationActivity.this, friend);
        listFriend.setAdapter(adapter);
        clear.setOnClickListener(v -> {
            list = "";
            person = new ArrayList<>();
            showList.setText(list);
        });
        confirm.setOnClickListener(v -> {
            if (person.size() > 0) {
                if (!new CheckUserInConversation().returnResult(user.getEmail(), person)) {
                    person.add(new PersonInConversation(user.getEmail()));
                    if (new CheckConversation().conversationExist(person, lists)) {
                        ArrayList<MessageForConversation> messageForConversationArrayList = new ArrayList<>();
                        ArrayList<DeniedSeenMessage> deniedSeenMessageArrayList = new ArrayList<>();
                        reference.child("conversation").push().setValue(new Conversation(person, messageForConversationArrayList, deniedSeenMessageArrayList));
                        finish();
                    }
                }
            }
        });
        listFriend.setOnItemClickListener((parent, view, position, id) -> {
            if (new CheckSelected().Check(friend.get(position), person)) {
                list += (" + " + friend.get(position) + "\n");
                person.add(new PersonInConversation(friend.get(position)));
                showList.setText(list);
            }
        });
        loadFriend();
        loadConversation();
    }

    private void loadConversation() {
        ///////////////////////////////////
        reference.child("conversation").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Conversation conversation = snapshot.getValue(Conversation.class);
                    assert conversation != null;
                    if (new CheckConversation().conversationExist(conversation.getPersonInConversationArrayList(), lists)) {
                        lists.add(conversation.getPersonInConversationArrayList());
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

    private void loadFriend() {
        reference.child("friend" + Objects.requireNonNull(user.getEmail()).hashCode()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    assert person != null;
                    friend.add(person.getEmail());
                    adapter.notifyDataSetChanged();
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
