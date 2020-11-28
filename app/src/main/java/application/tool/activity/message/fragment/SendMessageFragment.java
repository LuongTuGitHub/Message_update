package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import application.tool.activity.message.R;
import application.tool.activity.message.object.MessageForConversation;

public class SendMessageFragment extends Fragment {
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    String key;
    Button send;
    public EditText inputMessage;
    ArrayList<MessageForConversation> messageForConversationArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        messageForConversationArrayList = new ArrayList<>();
        inputMessage = view.findViewById(R.id.enterMessage);
        send = view.findViewById(R.id.sendMessage);
        key = getActivity().getIntent().getStringExtra("key");
        send.setOnClickListener(v -> {
            if (inputMessage.getText().toString().trim().length() > 0) {
                messageForConversationArrayList.add(new MessageForConversation(user.getEmail(), inputMessage.getText().toString(), Calendar.getInstance().getTimeInMillis()));
                reference.child("conversation/" + key + "/messageForConversationArrayList").setValue(messageForConversationArrayList);
                inputMessage.setText("");
            }
        });
        loadMessage();
        return view;
    }

    public void loadMessage() {
        reference.child("conversation/" + key + "/messageForConversationArrayList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    messageForConversationArrayList.add(snapshot.getValue(MessageForConversation.class));
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
