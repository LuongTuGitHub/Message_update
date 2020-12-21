package application.tool.activity.message.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.adapter.MessageAdapter;
import application.tool.activity.message.adapter.OnClickScrollListener;
import application.tool.activity.message.adapter.OnLongClickItemListener;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.module.TypeMessage;
import application.tool.activity.message.notification.SendNotification;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Message;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.PERSON;

public class ConversationActivity extends AppCompatActivity implements OnClickScrollListener, ItemOnClickListener, OnLongClickItemListener {
    private final static int PERMISSION_CAMERA = 100;
    private final static int IMAGE_GALLERY = 98;
    private final static int IMAGE_CAPTURE = 102;
    private Button exit, btSendMessage, btCall, btSendImage;
    private RecyclerView rv_show_message;
    private String numberPhone, key;
    private EditText edtMessage;
    private MessageAdapter adapter;
    private TextView tvName;
    private List<Message> message;
    private ImageView ivConversation;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private boolean status = false;
    private SQLiteImage image;
    ArrayList<PersonInConversation> people;
    private static int positionReply = -1;
    private ConstraintLayout layoutReply;
    private Button cancelReply;
    private TextView messageReply;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        messageReply = findViewById(R.id.tv_show_text_reply);
        layoutReply = findViewById(R.id.constraintLayout4);
        cancelReply = findViewById(R.id.bt_cancel_reply);
        cancelReply.setOnClickListener(v -> {
            positionReply = -1;
            layoutReply.setVisibility(View.GONE);
        });
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
                                                                    numberPhone = person.getPhone();
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
                                btCall.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:" + numberPhone));

                                        if (ActivityCompat.checkSelfPermission(ConversationActivity.this,
                                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(ConversationActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                                        }
                                        startActivity(callIntent);
                                    }
                                });
                            } else {
                                tvName.setText(conversation.getName());
                                btCall.setVisibility(View.GONE);
                                ivConversation.setImageResource(R.drawable.teamwork);
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
                        new SendNotification().sendMessage(people.get(i).getEmail(), "like", key);
                    }
                    if (layoutReply.getVisibility() == View.GONE) {
                        message.add(new Message(fUser.getEmail(), "---like", TypeMessage.MESSAGE_TEXT, null, Calendar.getInstance().getTimeInMillis()));
                    } else {
                        message.add(new Message(fUser.getEmail(), "---like", TypeMessage.MESSAGE_REPLY_TEXT, null, Calendar.getInstance().getTimeInMillis(), positionReply + ""));
                        layoutReply.setVisibility(View.GONE);
                        positionReply = -1;
                    }
                    refDb.child(CONVERSATION).child(key).child("messages")
                            .setValue(message);
                    message.remove(message.size() - 1);
                } else {
                    for (int i = 0; i < people.size(); i++) {
                        new SendNotification().sendMessage(people.get(i).getEmail(), edtMessage.getText().toString(), key);
                    }
                    if (layoutReply.getVisibility() == View.GONE) {
                        message.add(new Message(fUser.getEmail(), edtMessage.getText().toString(), TypeMessage.MESSAGE_TEXT, null, Calendar.getInstance().getTimeInMillis()));
                    } else {
                        message.add(new Message(fUser.getEmail(), edtMessage.getText().toString(), TypeMessage.MESSAGE_REPLY_TEXT, null, Calendar.getInstance().getTimeInMillis(), positionReply + ""));
                        layoutReply.setVisibility(View.GONE);
                        positionReply = -1;
                    }
                    refDb.child(CONVERSATION).child(key).child("messages")
                            .setValue(message);
                    message.remove(message.size() - 1);
                    edtMessage.setText("");
                }
            });
        }
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        manager.setStackFromEnd(true);
        rv_show_message.setLayoutManager(manager);
        adapter = new MessageAdapter(message, ConversationActivity.this, this, this);
        adapter.setOnClickScrollListener(this);
        rv_show_message.setAdapter(adapter);
        loadMessage();
        btSendImage.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(ConversationActivity.this);
            dialog.setContentView(R.layout.bt_dialog_method);
            Button btCamera = dialog.findViewById(R.id.btCamera);
            Button btGallery = dialog.findViewById(R.id.btMethodGallery);
            assert btCamera != null;
            btCamera.setOnClickListener(v1 -> {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, IMAGE_CAPTURE);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                }
                dialog.cancel();
            });
            assert btGallery != null;
            btGallery.setOnClickListener(v12 -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/");
                startActivityForResult(intent, IMAGE_GALLERY);
                dialog.cancel();
            });
            dialog.show();
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                positionReply = viewHolder.getAdapterPosition();
                layoutReply.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                if (positionReply >= 0) {
                    Message messages = message.get(positionReply);
                    if (messages.getType() == TypeMessage.MESSAGE_REPLY_TEXT || messages.getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE || messages.getType() == TypeMessage.MESSAGE_TEXT_HIDE || messages.getType() == TypeMessage.MESSAGE_TEXT) {
                        if (messages.getBody().equals("---like")) {
                            messageReply.setText("like");
                        } else {
                            messageReply.setText(messages.getBody());
                        }
                    }
                    if (messages.getType() == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE || messages.getType() == TypeMessage.MESSAGE_REPLY_IMAGE || messages.getType() == TypeMessage.MESSAGE_IMAGE_HIDE || messages.getType() == TypeMessage.MESSAGE_IMAGE) {
                        messageReply.setText("Trả lời một ảnh");
                    }
                }
            }
        });
        touchHelper.attachToRecyclerView(rv_show_message);
    }

    private void Init() {
        message = new ArrayList<>();
        exit = findViewById(R.id.backConversation);
        rv_show_message = findViewById(R.id.rvListMessage);
        btCall = findViewById(R.id.btCall);
        tvName = findViewById(R.id.nameConversation);
        ivConversation = findViewById(R.id.avatarConversation);
        edtMessage = findViewById(R.id.bodyMessage);
        btSendMessage = findViewById(R.id.sendMessage);
        btSendImage = findViewById(R.id.sendImage);
    }

    private void loadMessage() {
        refDb.child(CONVERSATION).child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Message messages = snapshot.getValue(Message.class);
                    if (messages != null) {
                        message.add(messages);
                        adapter.notifyDataSetChanged();
                        rv_show_message.scrollToPosition(message.size() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Message messages = snapshot.getValue(Message.class);
                    if (messages != null) {
                        int index = Integer.parseInt(Objects.requireNonNull(snapshot.getKey()));
                        message.set(index, messages);
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

    @Override
    public void onClickItem(View view, int position) {
        int type = message.get(position).getType();
        if (message.get(position).getType() == TypeMessage.MESSAGE_IMAGE_HIDE || message.get(position).getType() == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
            Intent intent = new Intent(ConversationActivity.this, ViewImageActivity.class);
            intent.putExtra("bitmap", message.get(position).getBody());
            intent.putExtra("method", "messages");
            startActivity(intent);
        }
    }

    @Override
    public boolean OnLongClick(View view, int position) {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(ConversationActivity.this);
        View v = LayoutInflater.from(ConversationActivity.this).inflate(R.layout.ad_message_delete, null);
        aBuilder.setView(v);
        AlertDialog dialog = aBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button bt_delete = v.findViewById(R.id.bt_delete);
        Button bt_hide = v.findViewById(R.id.bt_hide);
        if (!message.get(position).getFrom().equals(fUser.getEmail())) {
            bt_delete.setVisibility(View.GONE);
        }
        ArrayList<String> denied = message.get(position).getDenied();
        bt_delete.setOnClickListener(v1 -> {
            message.get(position).setType(TypeMessage.MESSAGE_DELETE);
            refDb.child(CONVERSATION).child(key).child("messages").child(position + "").setValue(message.get(position));
            dialog.dismiss();
        });
        bt_hide.setOnClickListener(v1 -> {
            if (message.get(position).getType() == TypeMessage.MESSAGE_TEXT || message.get(position).getType() == TypeMessage.MESSAGE_TEXT_HIDE) {
                message.get(position).setType(TypeMessage.MESSAGE_TEXT_HIDE);
            }
            if (message.get(position).getType() == TypeMessage.MESSAGE_IMAGE || message.get(position).getType() == TypeMessage.MESSAGE_IMAGE_HIDE) {
                message.get(position).setType(TypeMessage.MESSAGE_IMAGE_HIDE);
            }
            if (message.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE || message.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                message.get(position).setType(TypeMessage.MESSAGE_REPLY_IMAGE_HIDE);
            }
            if (message.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT || message.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                message.get(position).setType(TypeMessage.MESSAGE_REPLY_TEXT_HIDE);
            }
            denied.add(fUser.getEmail());
            message.get(position).setDenied(denied);
            refDb.child(CONVERSATION).child(key).child("messages").child(position + "").setValue(message.get(position));
            dialog.dismiss();
        });
        dialog.show();
        return true;
    }

    @Override
    public void onScrollToPosition(View view, int position) {
        rv_show_message.scrollToPosition(position);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ConversationActivity.this);
                builder.setView(R.layout.load);
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                String keyMessage = UUID.randomUUID().toString();
                assert data != null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                refStg.child("messages/" + keyMessage + ".png").putBytes(bytes)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (layoutReply.getVisibility() == View.GONE) {
                                    Message messages = new Message(fUser.getEmail(), keyMessage, TypeMessage.MESSAGE_IMAGE, new ArrayList<>(), Calendar.getInstance().getTimeInMillis());
                                    message.add(messages);
                                } else {
                                    Message messages = new Message(fUser.getEmail(), keyMessage, TypeMessage.MESSAGE_REPLY_IMAGE, new ArrayList<>(), Calendar.getInstance().getTimeInMillis(), positionReply + "");
                                    message.add(messages);
                                    layoutReply.setVisibility(View.GONE);
                                    positionReply = -1;
                                }
                                refDb.child(CONVERSATION).child(key).child("messages").setValue(message);
                                message.remove(message.size() - 1);
                                for (int i = 0; i < people.size(); i++) {
                                    new SendNotification().sendMessage(people.get(i).getEmail(), "Đã gửi một hình ảnh", key);
                                }
                            }
                            alertDialog.dismiss();
                        });
            }
        }
        if (requestCode == IMAGE_GALLERY) {
            if (resultCode == RESULT_OK) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ConversationActivity.this);
                builder.setView(R.layout.load);
                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                String keyMessage = UUID.randomUUID().toString();
                assert data != null;
                Uri uri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    alertDialog.dismiss();
                    Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                refStg.child("messages/" + keyMessage + ".png").putBytes(bytes)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (layoutReply.getVisibility() == View.GONE) {
                                    Message messages = new Message(fUser.getEmail(), keyMessage, TypeMessage.MESSAGE_IMAGE, new ArrayList<>(), Calendar.getInstance().getTimeInMillis());
                                    message.add(messages);
                                } else {
                                    Message messages = new Message(fUser.getEmail(), keyMessage, TypeMessage.MESSAGE_REPLY_IMAGE, new ArrayList<>(), Calendar.getInstance().getTimeInMillis(), positionReply + "");
                                    message.add(messages);
                                    layoutReply.setVisibility(View.GONE);
                                    positionReply = -1;
                                }
                                refDb.child(CONVERSATION).child(key).child("messages").setValue(message);
                                message.remove(message.size() - 1);
                                for (int i = 0; i < people.size(); i++) {
                                    new SendNotification().sendMessage(people.get(i).getEmail(), "Đã gửi một hình ảnh", key);
                                }
                            }
                            alertDialog.dismiss();
                        });
            }
        }
    }
}