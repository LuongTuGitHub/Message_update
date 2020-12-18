package application.tool.activity.message.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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
import java.util.List;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ContentFindActivity;
import application.tool.activity.message.activity.CreatePostActivity;
import application.tool.activity.message.activity.ExtensionActivity;
import application.tool.activity.message.activity.NotificationActivity;
import application.tool.activity.message.activity.ViewImageActivity;
import application.tool.activity.message.activity.ViewProfileActivity;
import application.tool.activity.message.adapter.FriendAdapter;
import application.tool.activity.message.adapter.ItemOnClickListener;
import application.tool.activity.message.adapter.OnClickShowImage;
import application.tool.activity.message.adapter.PostAdapter;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Post;

public class HomeFragment extends Fragment implements ItemOnClickListener, OnClickShowImage {
    public TextView tvNameUser;
    public Button btCreatePost, btViewProfile, btSearch, bt_extension, bt_notification;
    public RecyclerView rvViewPost, rvFriend;
    public ArrayList<String> key;
    public PostAdapter adapter;
    public FriendAdapter friendAdapter;
    public ImageView ivAvatar;
    public FirebaseUser fUser;
    public DatabaseReference refDb;
    public StorageReference refStg;
    public ArrayList<String> alFriend;
    public SQLiteImage image;
    private final static int SCROLL = 60;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        image = new SQLiteImage(view.getContext());
        Init(view);

        ImageSlider slider = view.findViewById(R.id.sliderCode);
        List<SlideModel> list = new ArrayList<>();
        list.add(new SlideModel(R.drawable._5, null));
        list.add(new SlideModel(R.drawable.pikmail_emails_to_pictures_using_kotlin_lede, null));
        list.add(new SlideModel(R.drawable.def40d80_cb4c_11e9_971a_7434089990ed, null));
        list.add(new SlideModel(R.drawable.git_reset_origin_to_commit, null));
        list.add(new SlideModel(R.drawable._288755792019456, null));
        list.add(new SlideModel(R.drawable.tong_quan_nodejs_trungquandev_02, null));
        list.add(new SlideModel(R.drawable.cafedev_angularjs_profile, null));
        list.add(new SlideModel(R.drawable.s3uitx6rdv7sod1g2acz, null));
        list.add(new SlideModel(R.drawable.vuejs, null));
        slider.setImageList(list, ScaleTypes.CENTER_CROP);
        btViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
            intent.putExtra("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            intent.putExtra("status", false);
            requireActivity().startActivity(intent);
        });
        btCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            requireActivity().startActivity(intent);
        });
        btSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ContentFindActivity.class);
            startActivity(intent);
        });
        refDb.child(Firebase.PERSON)
                .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Person person = snapshot.getValue(Person.class);
                    if (person != null) {
                        tvNameUser.setText(person.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        NestedScrollView nestedScrollView = view.findViewById(R.id.scView);
        nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if ((scrollY - oldScrollY) > SCROLL) {
                linearLayout.setVisibility(View.GONE);
            }
            if ((oldScrollY - scrollY) > SCROLL) {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        refDb.child(Firebase.AVATAR).child(fUser.getEmail().hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (image.checkExist(snapshot.getValue().toString())) {
                                byte[] bytes = image.getImage(snapshot.getValue().toString());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                ivAvatar.setImageBitmap(bitmap);
                            } else {
                                refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                        .getBytes(Long.MAX_VALUE)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                ivAvatar.setImageBitmap(bitmap);
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
        bt_extension.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExtensionActivity.class);
            startActivity(intent);
        });
        bt_notification.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });
        return view;
    }

    public void Init(View view) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refStg = FirebaseStorage.getInstance().getReference();
        refDb = FirebaseDatabase.getInstance().getReference();
        alFriend = new ArrayList<>();
        alFriend.add(fUser.getEmail());
        friendAdapter = new FriendAdapter(alFriend, this);
        rvFriend = view.findViewById(R.id.rvFriend);
        rvFriend.setAdapter(friendAdapter);
        bt_extension = view.findViewById(R.id.bt_extension);
        bt_notification = view.findViewById(R.id.btNotification);
        rvFriend.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        btCreatePost = view.findViewById(R.id.btCreatePost);
        btViewProfile = view.findViewById(R.id.btView);
        btSearch = view.findViewById(R.id.btSearch);
        ivAvatar = view.findViewById(R.id.ivAvatarUser);
        tvNameUser = view.findViewById(R.id.tvNameUser);
        rvViewPost = view.findViewById(R.id.rvPost);
        loadFriend();
        key = new ArrayList<>();
        adapter = new PostAdapter(key, this);
        rvViewPost.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rvViewPost.setAdapter(adapter);
        loadPost();
    }

    @Override
    public void OnClick(View view, String key) {
        Intent intent = new Intent(getActivity(), ViewImageActivity.class);
        intent.putExtra("bitmap", key);
        intent.putExtra("method", "post");
        startActivity(intent);
    }

    @Override
    public void onClickItem(View view, int position) {
        Intent intent = new Intent(getActivity(), ViewProfileActivity.class);
        intent.putExtra("email", alFriend.get(position));
        intent.putExtra("status", false);
        startActivity(intent);
    }

    private void loadPost() {
        refDb.child(Firebase.POST)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getValue() != null) {
                            Post post = snapshot.getValue(Post.class);
                            if (post != null) {
                                if (isFriend(post.getEmail())) {
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

    public boolean isFriend(String email) {
        for (int i = 0; i < alFriend.size(); i++) {
            if (alFriend.get(i).equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void loadFriend() {
        refDb.child(Firebase.LIST_FRIEND).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.getValue() != null) {
                            alFriend.add(snapshot.getValue().toString());
                            friendAdapter.notifyDataSetChanged();
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