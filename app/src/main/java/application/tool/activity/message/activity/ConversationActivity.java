package application.tool.activity.message.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.MessageAdapter;
import application.tool.activity.message.module.Notification;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.module.TypeMessage;
import application.tool.activity.message.notification.APIService;
import application.tool.activity.message.notification.Client;
import application.tool.activity.message.notification.MyResponse;
import application.tool.activity.message.notification.SendNotification;
import application.tool.activity.message.notification.Sender;
import application.tool.activity.message.notification.Token;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Data;
import application.tool.activity.message.object.Message;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Firebase.TOKEN;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    private String key;
    private Button exit, menu, btSendMessage, btCall;
    private ListView lvChat;
    private String numberPhone;
    private EditText edtMessage;
    private MessageAdapter adapter;
    private TextView tvName;
    private ArrayList<Message> messages;
    private ImageView ivConversation;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private boolean status = false;
    private SQLiteImage image;
    ArrayList<PersonInConversation> people;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        image = new SQLiteImage(getApplicationContext());
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        status = getIntent().getBooleanExtra("status", false);
        key = getIntent().getStringExtra("key");
        Init();
        exit.setOnClickListener(v -> {
            if (status) {
                Intent intent = new Intent(ConversationActivity.this, ContentActivity.class);
                startActivity(intent);
            }
            finish();
        });
        if (key != null) {
            refDb.child(CONVERSATION).child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Conversation conversation = snapshot.getValue(Conversation.class);
                        if (conversation != null) {
                            people = conversation.getPersons();
                            if (conversation.getName() == null) {
                                for (int i = 0; i < conversation.getPersons().size(); i++) {
                                    if (!conversation.getPersons().get(i).getEmail().equals(fUser.getEmail())) {
                                        if (conversation.getPersons().get(i).getNickName() != null
                                                && (!conversation.getPersons().get(i).getNickName().equals(""))) {
                                            tvName.setText(conversation.getPersons().get(i).getNickName());
                                        } else {
                                            refDb.child(PERSON)
                                                    .child(conversation.getPersons().get(i).getEmail().hashCode() + "")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.getValue() != null) {
                                                                Person person = snapshot.getValue(Person.class);
                                                                if (person != null) {
                                                                    tvName.setText(person.getName());
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                            refDb.child(AVATAR).child(conversation.getPersons().get(i).getEmail().hashCode() + "")
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.getValue() != null) {
                                                                if (image.checkExist(snapshot.getValue().toString())) {
                                                                    byte[] bytes = image.getImage(snapshot.getValue().toString());
                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                    ivConversation.setImageBitmap(bitmap);
                                                                } else {
                                                                    refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                                                            .getBytes(Long.MAX_VALUE)
                                                                            .addOnCompleteListener(task -> {
                                                                                if (task.isSuccessful()) {
                                                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                                                    ivConversation.setImageBitmap(bitmap);
                                                                                    image.Add(snapshot.getValue().toString(), task.getResult());
                                                                                }
                                                                            });
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
                            } else {
                                tvName.setText(conversation.getName());
                                btCall.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            edtMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().isEmpty()) {
                        btSendMessage.setBackgroundResource(R.drawable.like);
                    } else {
                        btSendMessage.setBackgroundResource(R.drawable.send_icon);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            btSendMessage.setOnClickListener(v -> {
                if (edtMessage.getText().toString().trim().isEmpty()) {
                    for (int i = 0; i < people.size(); i++) {
                        if (!people.get(i).getEmail().equals(fUser.getEmail())) {
                            if (!people.get(i).getEmail().equals(fUser.getEmail())) {
                                new SendNotification().sendMessage(people.get(i).getEmail(),"like",key);
                            }
                        }
                    }
                    messages.add(new Message(fUser.getEmail(), "---like", TypeMessage.MESSAGE_TEXT, null, Calendar.getInstance().getTimeInMillis()));
                    refDb.child(CONVERSATION).child(key).child("messages")
                            .setValue(messages);
                    messages.remove(messages.size() - 1);
                } else {
                    for (int i = 0; i < people.size(); i++) {
                        if (!people.get(i).getEmail().equals(fUser.getEmail())) {
                            new SendNotification().sendMessage(people.get(i).getEmail(),edtMessage.getText().toString(),key);
                        }
                    }
                    messages.add(new Message(fUser.getEmail(), edtMessage.getText().toString(), TypeMessage.MESSAGE_TEXT, null, Calendar.getInstance().getTimeInMillis()));
                    refDb.child(CONVERSATION).child(key).child("messages")
                            .setValue(messages);
                    messages.remove(messages.size() - 1);
                    edtMessage.setText("");
                }
            });
        }
        adapter = new MessageAdapter(ConversationActivity.this, messages);
        lvChat.setAdapter(adapter);
        loadMessages();
    }

    private void loadMessages() {
        refDb.child(CONVERSATION).child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue()!=null){
                    Message message = snapshot.getValue(Message.class);
                    if(message!=null){
                        int index = Integer.parseInt(snapshot.getKey());
                        messages.set(index,message);
                        adapter.notifyDataSetChanged();
                    }
                }
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
        messages = new ArrayList<>();
        exit = findViewById(R.id.backConversation);
        lvChat = findViewById(R.id.rvListMessage);
        btCall = findViewById(R.id.btCall);
        tvName = findViewById(R.id.nameConversation);
        ivConversation = findViewById(R.id.avatarConversation);
        edtMessage = findViewById(R.id.bodyMessage);
        btSendMessage = findViewById(R.id.sendMessage);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        if (status) {
            Intent intent = new Intent(ConversationActivity.this, ContentActivity.class);
            startActivity(intent);
            finish();
        } else {
            ConversationActivity.super.onBackPressed();
        }
    }
}