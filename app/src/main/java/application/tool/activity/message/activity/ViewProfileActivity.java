package application.tool.activity.message.activity;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import application.tool.activity.message.R;
import application.tool.activity.message.adapter.InformationAdapter;
import application.tool.activity.message.adapter.OnClickShowImage;
import application.tool.activity.message.adapter.PostAdapter;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Information;
import application.tool.activity.message.object.Notification;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;
import application.tool.activity.message.object.Post;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.BACKGROUND;
import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND_REQUEST;
import static application.tool.activity.message.module.Firebase.NOTIFICATION;
import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Firebase.POST;
import static application.tool.activity.message.module.Firebase.STATUS;
import static application.tool.activity.message.module.Notification.REQUEST;
import static application.tool.activity.message.module.Notification.RESPONSE;

public class ViewProfileActivity extends AppCompatActivity implements OnClickShowImage {
    private final static int RESULT_IMAGE = 99;
    private final static int RESULT_CAPTURE = 100;
    private final static int PERMISSION_CAMERA = 101;
    private static String method = "";
    private Button exit, confirm, denied, btEditProfile, btAddFriend,btSearch;
    private String email;
    private boolean status = false;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private ImageView backgroundProfile, avatarProfile;
    private TextView name;
    private ArrayList<String> key;
    private PostAdapter adapter;
    private RecyclerView rvPost;
    private ListView lvInformation;
    private ArrayList<Information> information;
    private InformationAdapter informationAdapter;
    private SQLiteImage image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        image = new SQLiteImage(getApplicationContext());
        refStg  = FirebaseStorage.getInstance().getReference();
        information = new ArrayList<>();
        informationAdapter = new InformationAdapter(information);
        key = new ArrayList<>();
        adapter = new PostAdapter(key,this);
        Init();
        status = getIntent().getBooleanExtra("status", false);
        email = getIntent().getStringExtra("email");
        exit.setOnClickListener(v -> {
            if (status) {
                Intent intent = new Intent(ViewProfileActivity.this, ContentActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        });
        if (email != null) {
            if (email.equals(fUser.getEmail())) {
                btEditProfile.setVisibility(View.VISIBLE);
                denied.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                btAddFriend.setVisibility(View.GONE);
                btEditProfile.setOnClickListener(v -> {
                    Intent intent = new Intent(ViewProfileActivity.this, ProfileActivity.class);
                    intent.putExtra("status", false);
                    startActivity(intent);
                });
            } else {
                denied.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                btEditProfile.setVisibility(View.GONE);
                refDb.child(LIST_FRIEND).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getValue() != null) {
                            if (snapshot.getValue().toString().equals(email)) {
                                btAddFriend.setVisibility(View.GONE);
                                denied.setVisibility(View.GONE);
                                confirm.setVisibility(View.GONE);
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
                refDb.child(LIST_FRIEND_REQUEST).child(fUser.getEmail().hashCode() + "").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getValue() != null) {
                            if (snapshot.getValue().toString().equals(email)) {
                                btAddFriend.setVisibility(View.GONE);
                                denied.setVisibility(View.VISIBLE);
                                confirm.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (snapshot.getValue().toString().equals(email)) {
                                refDb.child(LIST_FRIEND).child(fUser.getEmail().hashCode() + "").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                        if (snapshot.getValue() != null) {
                                            if (snapshot.getValue().toString().equals(email)) {
                                                btAddFriend.setVisibility(View.GONE);
                                                denied.setVisibility(View.GONE);
                                                confirm.setVisibility(View.GONE);
                                            } else {
                                                btAddFriend.setVisibility(View.VISIBLE);
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
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                refDb.child(LIST_FRIEND_REQUEST).child(email.hashCode() + "").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getValue() != null) {
                            if (snapshot.getValue().toString().equals(fUser.getEmail())) {
                                denied.setVisibility(View.GONE);
                                confirm.setVisibility(View.GONE);
                                btAddFriend.setVisibility(View.VISIBLE);
                                btAddFriend.setText(R.string.cancel_add_friend);
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
            btAddFriend.setOnClickListener(v -> {
                long key = Calendar.getInstance().getTimeInMillis();
                Notification notification = new Notification(REQUEST,fUser.getEmail(),"Gửi lời mời kết bạn",null,Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                ,Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR),key);
                refDb.child(NOTIFICATION).child(email.hashCode()+"").child(key+"").setValue(notification);
                if (btAddFriend.getText().toString().equals("Cancel Add Friend")) {
                    refDb.child(LIST_FRIEND_REQUEST)
                            .child(email.hashCode() + "").child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").removeValue();
                    btAddFriend.setText(R.string.add_friend);
                } else {
                    refDb.child(LIST_FRIEND_REQUEST)
                            .child(email.hashCode() + "").child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(fUser.getEmail());
                    btAddFriend.setText(R.string.cancel_add_friend);
                }
            });
            confirm.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
                builder.setView(R.layout.load);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.show();
                long key = Calendar.getInstance().getTimeInMillis();
                Notification notification = new Notification(RESPONSE,fUser.getEmail(),"Chấp nhận lời mời kết bạn",null,Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        ,Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR),key);
                refDb.child(NOTIFICATION).child(email.hashCode()+"").child(key+"").setValue(notification);
                refDb.child(LIST_FRIEND).child(email.hashCode() + "")
                        .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(fUser.getEmail());
                refDb.child(LIST_FRIEND)
                        .child(fUser.getEmail().hashCode() + "").child(email.hashCode() + "").setValue(email);
                ArrayList<PersonInConversation> person = new ArrayList<>();
                person.add(new PersonInConversation("", email));
                person.add(new PersonInConversation("", fUser.getEmail()));
                refDb.child(CONVERSATION).push().setValue(new Conversation(person, new ArrayList<>(),null))
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                refDb.child(LIST_FRIEND_REQUEST)
                                        .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(email.hashCode() + "").removeValue();
                                dialog.cancel();
                            }
                        });
            });
            denied.setOnClickListener(v -> {
                long key = Calendar.getInstance().getTimeInMillis();
                refDb.child(LIST_FRIEND_REQUEST)
                    .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(email.hashCode() + "").removeValue();
                Notification notification = new Notification(RESPONSE,fUser.getEmail(),"Từ chối lời mời kết bạn",null,Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                        ,Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.YEAR),key);
                refDb.child(NOTIFICATION).child(email.hashCode()+"").child(key+"").setValue(notification);
            });
            refDb.child(BACKGROUND).child(email.hashCode() + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        if(image.checkExist(snapshot.getValue().toString())){
                            byte[] bytes = image.getImage(snapshot.getValue().toString());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            backgroundProfile.setImageBitmap(bitmap);
                        }else {
                            refStg.child("background/" + snapshot.getValue().toString() + ".png")
                                    .getBytes(Long.MAX_VALUE)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                            backgroundProfile.setImageBitmap(bitmap);
                                            image.Add(snapshot.getValue().toString(),task.getResult());
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            refDb.child(AVATAR).child(email.hashCode() + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                       if(image.checkExist(snapshot.getValue().toString())){
                           byte[] bytes = image.getImage(snapshot.getValue().toString());
                           Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                           avatarProfile.setImageBitmap(bitmap);
                       }else {
                           refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                   .getBytes(Long.MAX_VALUE)
                                   .addOnCompleteListener(task -> {
                                       if(task.isSuccessful()){
                                           Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                           avatarProfile.setImageBitmap(bitmap);
                                           image.Add(snapshot.getValue().toString(),task.getResult());
                                       }
                                   });
                       }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            refDb.child(PERSON).child(email.hashCode() + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        Person person = snapshot.getValue(Person.class);
                        assert person != null;
                        name.setText(person.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        backgroundProfile.setOnClickListener(v -> {
            if (email.equals(fUser.getEmail())) {
                BottomSheetDialog dialog = new BottomSheetDialog(ViewProfileActivity.this);
                dialog.setContentView(R.layout.bt_dialog_method);
                dialog.setTitle(R.string.method_select_image);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                Button gallery = dialog.findViewById(R.id.btMethodGallery);
                Button camera = dialog.findViewById(R.id.btCamera);
                method = "background";
                assert gallery != null;
                gallery.setOnClickListener(v14 -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/");
                    startActivityForResult(intent, RESULT_IMAGE);
                    dialog.cancel();
                });
                assert camera != null;
                camera.setOnClickListener(v13 -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, RESULT_CAPTURE);
                    dialog.cancel();
                });
                dialog.show();
            }
        });
        avatarProfile.setOnClickListener(v -> {
            if (email.equals(fUser.getEmail())) {
                BottomSheetDialog dialog = new BottomSheetDialog(ViewProfileActivity.this);
                dialog.setContentView(R.layout.bt_dialog_method);
                dialog.setTitle(R.string.method_select_image);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                Button gallery = dialog.findViewById(R.id.btMethodGallery);
                Button camera = dialog.findViewById(R.id.btCamera);
                method = "avatar";
                assert gallery != null;
                gallery.setOnClickListener(v12 -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/");
                    startActivityForResult(intent, RESULT_IMAGE);
                    dialog.cancel();
                });
                assert camera != null;
                camera.setOnClickListener(v1 -> {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, RESULT_CAPTURE);
                    dialog.cancel();
                });
                dialog.show();
            }
        });
        btSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this,ContentFindActivity.class);
            startActivity(intent);
        });
        LinearLayoutManager manager = new LinearLayoutManager(ViewProfileActivity.this, RecyclerView.VERTICAL, false);
        rvPost.setLayoutManager(manager);
        rvPost.setAdapter(adapter);
        loadPost();
        lvInformation.setAdapter(informationAdapter);
        loadInformation();
    }

    @Override
    public void OnClick(View view, String key) {
        Intent intent = new Intent(ViewProfileActivity.this,ViewImageActivity.class);
        intent.putExtra("bitmap",key);
        intent.putExtra("method","post");
        startActivity(intent);
    }

    private void loadInformation() {
        refDb.child(PERSON).child(email.hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person personView = snapshot.getValue(Person.class);
                    if (personView != null) {
                        information.clear();
                        information.add(new Information(R.drawable.ic_baseline_email_24, personView.getEmail()));
                        information.add(new Information(R.drawable.ic_baseline_phone_24, personView.getPhone()));
                        information.add(new Information(R.drawable.ic_baseline_calendar_today_24, personView.getDay()));
                        information.add(new Information(R.drawable.ic_baseline_location_on_24, personView.getAddress()));
                        informationAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPost() {
        refDb.child(POST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        if (post.getEmail().equals(email)) {
                            key.add(snapshot.getKey());
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

    private void Init() {
        lvInformation = findViewById(R.id.lvInformation);
        exit = findViewById(R.id.btBackProfile);
        confirm = findViewById(R.id.btConfirmAddFriend);
        denied = findViewById(R.id.btDeniedAddFriend);
        btEditProfile = findViewById(R.id.btEditProfile);
        btAddFriend = findViewById(R.id.btAddFriend);
        backgroundProfile = findViewById(R.id.backgroundProfile);
        avatarProfile = findViewById(R.id.avatarProfile);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.nameProfile);
        rvPost = findViewById(R.id.rvViewPost);
        btSearch = findViewById(R.id.btSearchProfile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE) {
            if (resultCode == RESULT_OK) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
                builder.setView(R.layout.load);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                String key = UUID.randomUUID().toString();
                assert data != null;
                Uri uri = data.getData();
                refStg.child(method + "/" + key + ".png").putFile(uri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (method.equals("avatar")) {
                                    refDb.child(AVATAR).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(key)
                                            .addOnCompleteListener(task14 -> {
                                                if (task14.isSuccessful()) {
                                                    refStg.child("post/" + key + ".png").putFile(uri)
                                                            .addOnCompleteListener(task17 -> {
                                                                if (task17.isSuccessful()) {
                                                                    refDb.child(POST).child(key)
                                                                            .setValue(new Post(fUser.getEmail(), "Update Avatar", "", key, ""))
                                                                            .addOnCompleteListener(task15 -> {
                                                                                if (task15.isSuccessful()) {
                                                                                    alertDialog.dismiss();
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                } else if (method.equals("background")) {
                                    refDb.child(BACKGROUND).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(key)
                                            .addOnCompleteListener(task13 -> {
                                                if (task13.isSuccessful()) {
                                                    refStg.child("post/" + key + ".png").putFile(uri)
                                                            .addOnCompleteListener(task18 -> {
                                                                if (task18.isSuccessful()) {
                                                                    refDb.child(POST).child(key).setValue(new Post(fUser.getEmail(), "Update Background", "", key, ""))
                                                                            .addOnCompleteListener(task16 -> {
                                                                                if (task16.isSuccessful()) {
                                                                                    alertDialog.dismiss();
                                                                                    adapter.notifyDataSetChanged();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        });
            }
        }
        if (requestCode == RESULT_CAPTURE) {
            if (resultCode == RESULT_OK) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);
                builder.setView(R.layout.load);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                String key = UUID.randomUUID().toString();
                assert data != null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bytes = stream.toByteArray();
                refStg.child(method + "/" + key + ".png")
                        .putBytes(bytes).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (method.equals("avatar")) {
                            refDb.child(AVATAR).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                                    .setValue(key).addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    refStg.child("post/" + key + ".png").putBytes(bytes)
                                            .addOnCompleteListener(task19 -> {
                                                if (task19.isSuccessful()) {
                                                    refDb.child(POST).child(key).setValue(new Post(fUser.getEmail(), "Update Avatar", "", key, ""))
                                                            .addOnCompleteListener(task15 -> {
                                                                if (task15.isSuccessful()) {
                                                                    alertDialog.dismiss();
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        } else if (method.equals("background")) {
                            refDb.child(BACKGROUND).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                                    .setValue(key).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    refStg.child("post/" + key + ".png").putBytes(bytes)
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    refDb.child(POST).child(key).setValue(new Post(fUser.getEmail(), "Update Background", "", key, ""))
                                                            .addOnCompleteListener(task16 -> {
                                                                if (task16.isSuccessful()) {
                                                                    alertDialog.dismiss();
                                                                    adapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, RESULT_CAPTURE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (status) {
            Intent intent = new Intent(ViewProfileActivity.this, ContentActivity.class);
            startActivity(intent);
        } else {
            ViewProfileActivity.super.onBackPressed();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        refDb.child(STATUS).child(Objects.requireNonNull(fUser.getEmail()).hashCode()+"").setValue("online");
    }
}