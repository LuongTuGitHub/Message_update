package application.tool.activity.message;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import application.tool.activity.message.adapter.MessageAdapter;
import application.tool.activity.message.fragment.SendMessageFragment;
import application.tool.activity.message.fragment.ToolbarMessageFragment;
import application.tool.activity.message.notification.SendNotification;
import application.tool.activity.message.notification.Token;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.MessageForConversation;
import application.tool.activity.message.object.PersonInConversation;

public class MessageActivity extends AppCompatActivity {
    private final int TYPE_TEXT = 0;
    private final int TYPE_IMAGE = 1;
    private final int TYPE_DELETE = 2;
    private final int TYPE_HIDE_TEXT = 3;
    private final int TYPE_HIDE_IMAGE = 4;
    ToolbarMessageFragment toolbarMessageFragment;
    SendMessageFragment sendMessageFragment;
    ListView listView;
    private final static int SELECT_IMAGE_SEND = 100;
    ArrayList<MessageForConversation> arrayList;
    MessageAdapter adapter;
    ArrayList<PersonInConversation> personList;
    DatabaseReference reference;
    StorageReference storageReference;
    FirebaseUser user;
    String person;
    String keyConversation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        personList = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference();
        sendMessageFragment = (SendMessageFragment) getFragmentManager().findFragmentById(R.id.fragment9);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        listView = findViewById(R.id.showMessage);
        arrayList = new ArrayList<>();
        if (intent != null) {
            person = intent.getStringExtra("person");
            keyConversation = intent.getStringExtra("key");
        }
        sendMessageFragment.sendImage.setOnClickListener(v -> {
            Intent selectImage = new Intent(Intent.ACTION_PICK);
            selectImage.setType("image/");
            startActivityForResult(selectImage, SELECT_IMAGE_SEND);
        });
        toolbarMessageFragment = (ToolbarMessageFragment) getFragmentManager().findFragmentById(R.id.fragment8);
        sendMessageFragment = (SendMessageFragment) getFragmentManager().findFragmentById(R.id.fragment9);
        toolbarMessageFragment.back.setOnClickListener(v -> {
            if (sendMessageFragment.inputMessage.getText().toString().trim().length() > 0) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(MessageActivity.this);
                View view = LayoutInflater.from(MessageActivity.this).inflate(R.layout.alert_warning, null);
                aBuilder.setView(view);
                final AlertDialog alertDialog = aBuilder.create();
                Button confirm = view.findViewById(R.id.confirmExit);
                Button cancel = view.findViewById(R.id.cancel);
                cancel.setOnClickListener(v12 -> alertDialog.dismiss());
                confirm.setOnClickListener(v1 -> finish());
                alertDialog.show();
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
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(MessageActivity.this);
            View selectDelete = LayoutInflater.from(MessageActivity.this).inflate(R.layout.alert_delete_message, null);
            aBuilder.setView(selectDelete);
            final AlertDialog dialog = aBuilder.create();
            Button delete = selectDelete.findViewById(R.id.deleteMessage);
            Button hide = selectDelete.findViewById(R.id.hideMessage);
            if (arrayList.get(position).getFrom().equals(user.getEmail())) {
                delete.setOnClickListener(v -> {
                    MessageForConversation message = arrayList.get(position);
                    message.setType(TYPE_DELETE);
                    reference.child("conversation/" + keyConversation + "/messageForConversationArrayList/" + position).setValue(message);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
                hide.setOnClickListener(v -> {
                    if (arrayList.get(position).getType() == TYPE_TEXT) {
                        MessageForConversation message = arrayList.get(position);
                        ArrayList<String> denied;
                        if (message.getDenied() != null) {
                            denied = message.getDenied();
                        } else {
                            denied = new ArrayList<>();
                        }
                        dialog.dismiss();
                        denied.add(user.getEmail());
                        message.setDenied(denied);
                        message.setType(TYPE_HIDE_TEXT);
                        reference.child("conversation/" + keyConversation + "/messageForConversationArrayList/" + position).setValue(message);
                        adapter.notifyDataSetChanged();
                    }
                    if (arrayList.get(position).getType() == TYPE_IMAGE) {
                        MessageForConversation message = arrayList.get(position);
                        ArrayList<String> denied;
                        if (message.getDenied() != null) {
                            denied = message.getDenied();
                        } else {
                            denied = new ArrayList<>();
                        }
                        dialog.dismiss();
                        denied.add(user.getEmail());
                        message.setDenied(denied);
                        message.setType(TYPE_HIDE_IMAGE);
                        reference.child("conversation/" + keyConversation + "/messageForConversationArrayList/" + position).setValue(message);
                        adapter.notifyDataSetChanged();
                    }
                });
            } else {
                delete.setVisibility(View.GONE);
                hide.setOnClickListener(v -> {
                    if (arrayList.get(position).getType() == TYPE_TEXT) {
                        MessageForConversation message = arrayList.get(position);
                        ArrayList<String> denied;
                        if (message.getDenied() != null) {
                            denied = message.getDenied();
                        } else {
                            denied = new ArrayList<>();
                        }
                        dialog.dismiss();
                        denied.add(user.getEmail());
                        message.setDenied(denied);
                        message.setType(TYPE_HIDE_TEXT);
                        reference.child("conversation/" + keyConversation + "/messageForConversationArrayList/" + position).setValue(message);
                        adapter.notifyDataSetChanged();
                    }
                    if (arrayList.get(position).getType() == TYPE_IMAGE) {
                        MessageForConversation message = arrayList.get(position);
                        ArrayList<String> denied;
                        if (message.getDenied() != null) {
                            denied = message.getDenied();
                        } else {
                            denied = new ArrayList<>();
                        }
                        dialog.dismiss();
                        denied.add(user.getEmail());
                        message.setDenied(denied);
                        message.setType(TYPE_HIDE_IMAGE);
                        reference.child("conversation/" + keyConversation + "/messageForConversationArrayList/" + position).setValue(message);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            dialog.show();
            return true;
        });
        notification();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_SEND) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri uri = data.getData();
                uploadFile(uri);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadFile(Uri uri) {
        String key = UUID.randomUUID().toString();
        storageReference.child("image/" + key + ".png").putFile(uri).addOnFailureListener(e -> uploadFile(uri)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                arrayList.add(new MessageForConversation(user.getEmail(), key, 1, Calendar.getInstance().getTimeInMillis(), new ArrayList<>()));
                for (int i = 0; i < personList.size(); i++) {
                    if (!personList.get(i).getPerson().equals(user.getEmail())) {
                        new SendNotification().sendMessage(personList.get(i).getPerson(), "Đã gửi một ảnh");
                    }
                }
                reference.child("conversation/" + keyConversation + "/messageForConversationArrayList").setValue(arrayList);
                arrayList.remove(arrayList.size() - 1);
            }
        });
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
                if (snapshot.getValue() != null) {
                    int index = Integer.parseInt(Objects.requireNonNull(snapshot.getKey()));
                    arrayList.set(index, snapshot.getValue(MessageForConversation.class));
                    adapter.notifyDataSetChanged();
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

    private void updateToken(String tk) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tk);
        reference.child(Objects.requireNonNull(user.getEmail()).hashCode() + "").setValue(token);
    }

    private void notification() {
        reference.child("conversation/" + keyConversation + "/personInConversationArrayList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    PersonInConversation personInConversation = snapshot.getValue(PersonInConversation.class);
                    assert personInConversation != null;
                    personList.add(personInConversation);
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
