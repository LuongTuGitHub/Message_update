package application.tool.activity.message.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

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
    public Button sendImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        sendImage = view.findViewById(R.id.sendImage);
        messageForConversationArrayList = new ArrayList<>();
        inputMessage = view.findViewById(R.id.enterMessage);
        send = view.findViewById(R.id.sendMessage);
        key = getActivity().getIntent().getStringExtra("key");
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    send.setBackgroundResource(R.drawable.like);
                } else {
                    send.setBackgroundResource(R.drawable.send_icon);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                sendImage.setVisibility(View.GONE);
            }else {
                sendImage.setVisibility(View.VISIBLE);
            }
        });
        send.setOnClickListener(v -> {
            if (inputMessage.getText().toString().trim().length() > 0) {
                messageForConversationArrayList.add(new MessageForConversation(user.getEmail(), inputMessage.getText().toString(), 0,Calendar.getInstance().getTimeInMillis(),new ArrayList<>()));
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(key).addData("from",user.getEmail()).addData("body",inputMessage.getText().toString()).build());
                inputMessage.setFocusable(false);
            } else {
                messageForConversationArrayList.add(new MessageForConversation(user.getEmail(), "---like", 0,Calendar.getInstance().getTimeInMillis(),new ArrayList<>()));
                FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(key).addData("from",user.getEmail()).addData("body","like").build());
            }
            reference.child("conversation/" + key + "/messageForConversationArrayList").setValue(messageForConversationArrayList);
            messageForConversationArrayList.remove(messageForConversationArrayList.size() - 1);
            inputMessage.setText("");
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
