package application.tool.activity.message;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

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

import application.tool.activity.message.adapter.MessageAdapter;
import application.tool.activity.message.fragment.SendMessageFragment;
import application.tool.activity.message.fragment.ToolbarMessageFragment;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.MessageForConversation;

public class MessageActivity extends AppCompatActivity {
    ToolbarMessageFragment toolbarMessageFragment;
    SendMessageFragment sendMessageFragment;
    ListView listView;
    ArrayList<MessageForConversation> arrayList;
    MessageAdapter adapter;
    DatabaseReference reference;
    FirebaseUser user;
    String person;
    String keyConversation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        listView = findViewById(R.id.showMessage);
        arrayList = new ArrayList<>();
        if (intent != null) {
            person = intent.getStringExtra("person");
            keyConversation = intent.getStringExtra("key");
        }
        toolbarMessageFragment = (ToolbarMessageFragment) getFragmentManager().findFragmentById(R.id.fragment8);
        sendMessageFragment = (SendMessageFragment) getFragmentManager().findFragmentById(R.id.fragment9);
        toolbarMessageFragment.back.setOnClickListener(v -> {
            if (sendMessageFragment.inputMessage.getText().toString().trim().length() > 0) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(MessageActivity.this);
                aBuilder.setTitle("Warning !");
                aBuilder.setIcon(R.drawable.ic_baseline_warning);
                aBuilder.setMessage("Have not sent the message you are sure you want to exit !");
                aBuilder.setPositiveButton("Confirm", (dialog, which) -> finish());
                aBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                });
                aBuilder.create().show();
            } else {
                finish();
            }
        });
        adapter = new MessageAdapter(MessageActivity.this, arrayList);
        if (!person.equals("Group Chat")) {
            new Avatar(person).setAvatar(toolbarMessageFragment.image);
        }
        listView.setAdapter(adapter);
        loadMessage();
    }

    private void loadMessage() {
        reference.child("conversation/" + keyConversation + "/messageForConversationArrayList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    arrayList.add(snapshot.getValue(MessageForConversation.class));
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
