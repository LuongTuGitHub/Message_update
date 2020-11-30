package application.tool.activity.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.adapter.ConversationAdapter;
import application.tool.activity.message.check.CheckConversation;
import application.tool.activity.message.check.CheckKeyConversation;
import application.tool.activity.message.check.CheckUserInConversation;
import application.tool.activity.message.fragment.ToolbarFragment;
import application.tool.activity.message.fragment.UserFragment;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.KeyConversation;
import application.tool.activity.message.object.PersonInConversation;

public class ContentActivity extends AppCompatActivity {
    private final static int TIME_LOAD_CONVERSATION = 3000;
    private final static int SELECT_IMAGE_AVATAR_CODE = 88;
    private final static int SELECT_IMAGE_BACKGROUND_CODE = 89;
    private final static int CAMERA_CAPTURE_AVATAR = 90;
    private final static int CAMERA_CAPTURE_BACKGROUND = 91;
    private final static int APPLY_PERMISSION_AVATAR = 92;
    private final static int APPLY_PERMISSION_BACKGROUND = 93;
    ToolbarFragment toolbarFragment;
    UserFragment userFragment;
    DrawerLayout layout;
    ListView listView;
    ArrayList<KeyConversation> keyConversations;
    ArrayList<Integer> amountPerson;
    ArrayList<ArrayList<PersonInConversation>> lists;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    Button createConversation;
    StorageReference storageReference;
    AlertDialog alertDialog;
    ConversationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        amountPerson = new ArrayList<>();
        keyConversations = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        lists = new ArrayList<>();
        createConversation = findViewById(R.id.sendMessage);
        layout = findViewById(R.id.drawer_layout);
        listView = findViewById(R.id.listFriend);
        userFragment = (UserFragment) getFragmentManager().findFragmentById(R.id.fragment3);
        toolbarFragment = (ToolbarFragment) getFragmentManager().findFragmentById(R.id.fragment5);
        toolbarFragment.openMenu.setOnClickListener(v -> layout.openDrawer(GravityCompat.START));
        toolbarFragment.scanQrCode.setOnClickListener(v -> {
        });
        createConversation.setOnClickListener(v -> {
            Intent intent = new Intent(ContentActivity.this, CreateConversationActivity.class);
            startActivity(intent);
        });
        userFragment.editAvatar.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(ContentActivity.this).inflate(R.layout.alert_select_image, null);
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(ContentActivity.this);
            aBuilder.setView(view);
            alertDialog = aBuilder.create();
            Button selectFromLibrary = view.findViewById(R.id.selectLibrary);
            Button captureCamera = view.findViewById(R.id.captureCamera);
            Button close = view.findViewById(R.id.closeSelectMethod);
            close.setOnClickListener(v13 -> alertDialog.dismiss());
            selectFromLibrary.setOnClickListener(v12 -> selectImageAvatar());
            captureCamera.setOnClickListener(v1 -> captureAvatar());
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            alertDialog.show();
        });
        userFragment.editBackground.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(ContentActivity.this).inflate(R.layout.alert_select_image, null);
            AlertDialog.Builder aBuilder = new AlertDialog.Builder(ContentActivity.this);
            aBuilder.setView(view);
            alertDialog = aBuilder.create();
            Button selectFromLibrary = view.findViewById(R.id.selectLibrary);
            Button captureCamera = view.findViewById(R.id.captureCamera);
            Button close = view.findViewById(R.id.closeSelectMethod);
            close.setOnClickListener(v13 -> alertDialog.dismiss());
            selectFromLibrary.setOnClickListener(v12 -> selectImageBackground());
            captureCamera.setOnClickListener(v1 -> captureBackground());
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            alertDialog.show();
        });
        adapter = new ConversationAdapter(ContentActivity.this, keyConversations);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ContentActivity.this, MessageActivity.class);
            intent.putExtra("key", keyConversations.get(position).getKey());
            intent.putExtra("person", keyConversations.get(position).getName());
            startActivity(intent);
        });
        loadConversation();

        new Handler().postDelayed(() -> {
            if (keyConversations.size() == 0) {
                AlertDialog.Builder addConversation = new AlertDialog.Builder(ContentActivity.this);
                View viewAddConversation = LayoutInflater.from(ContentActivity.this).inflate(R.layout.alert_start_conversation, null);
                Button startAdd = viewAddConversation.findViewById(R.id.addConversation);
                addConversation.setView(viewAddConversation);
                final AlertDialog dialog = addConversation.create();
                startAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(ContentActivity.this, CreateConversationActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                });
                dialog.show();
            }
        }, TIME_LOAD_CONVERSATION);
    }

    private void loadConversation() {
        ///////////////////////////////////
        reference.child("conversation").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Conversation conversation = snapshot.getValue(Conversation.class);
                    assert conversation != null;
                    if (new CheckUserInConversation().returnResult(user.getEmail(), conversation.getPersonInConversationArrayList())) {
                        if (new CheckConversation().conversationExist(conversation.getPersonInConversationArrayList(), lists)) {
                            if (conversation.getName() != null) {
                                keyConversations.add(new KeyConversation(new CheckUserInConversation().getNameConversation(user.getEmail(), conversation.getPersonInConversationArrayList()), snapshot.getKey(), conversation.getName()));
                            } else {
                                keyConversations.add(new KeyConversation(new CheckUserInConversation().getNameConversation(user.getEmail(), conversation.getPersonInConversationArrayList()), snapshot.getKey(), ""));
                            }
                            lists.add(conversation.getPersonInConversationArrayList());
                            adapter.notifyDataSetChanged();
                        }
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


    private void selectImageBackground() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, SELECT_IMAGE_BACKGROUND_CODE);
    }

    private void selectImageAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent, SELECT_IMAGE_AVATAR_CODE);
    }


    private void uploadImage(Uri uri, String type) {
        storageReference.child(type + "/" + Objects.requireNonNull(user.getEmail()).hashCode() + ".png").putFile(uri).addOnFailureListener(e -> uploadImage(uri, type));
    }

    @SuppressLint("WrongConstant")
    private void captureAvatar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, APPLY_PERMISSION_AVATAR);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_AVATAR);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == APPLY_PERMISSION_AVATAR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureAvatar();
            }
        }
        if (requestCode == APPLY_PERMISSION_BACKGROUND) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureBackground();
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void captureBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, APPLY_PERMISSION_BACKGROUND);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_CAPTURE_BACKGROUND);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_AVATAR_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                uploadImage(uri, "avatar");
                alertDialog.dismiss();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    userFragment.avatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == SELECT_IMAGE_BACKGROUND_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                uploadImage(uri, "background");
                alertDialog.dismiss();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    userFragment.background.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == CAMERA_CAPTURE_AVATAR && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadCapture(bitmap, "avatar");
                userFragment.avatar.setImageBitmap(bitmap);
                alertDialog.dismiss();
            }
        }
        if (requestCode == CAMERA_CAPTURE_BACKGROUND && resultCode == RESULT_OK) {
            if (data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                uploadCapture(bitmap, "background");
                userFragment.background.setImageBitmap(bitmap);
                alertDialog.dismiss();
            }
        }
    }

    private void uploadCapture(Bitmap bitmap, String type) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        UploadTask uploadTask = storageReference.child(type + "/" + Objects.requireNonNull(user.getEmail()).hashCode() + ".png").putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
        }).addOnSuccessListener(taskSnapshot -> {
        });
    }

}
