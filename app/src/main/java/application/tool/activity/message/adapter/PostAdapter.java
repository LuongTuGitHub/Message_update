package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ViewImageActivity;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.Post;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Firebase.POST;
import static application.tool.activity.message.module.Firebase.REACT_TIME;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    private ArrayList<String> key;
    private final FirebaseUser fUser;
    private final DatabaseReference refDb;
    private final StorageReference refStg;
    private OnClickShowImage onClickShowImage;
    private SQLiteImage image;
    public PostAdapter(ArrayList<String> key,OnClickShowImage onClickShowImage) {
        this.key = key;
        this.onClickShowImage = onClickShowImage;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        image = new SQLiteImage(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_post, parent, false);
        return new PostHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        final int[] count = {0};
        holder.reactTime.setText(count[0] + "");
        refDb.child(POST).child(key.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        String email = post.getEmail();
                        refDb.child(PERSON).child(email.hashCode() + "").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    Person person = snapshot.getValue(Person.class);
                                    assert person != null;
                                    holder.nameAuthor.setText(person.getName());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        refDb.child(AVATAR).child(email.hashCode() + "").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (image.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = image.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        holder.avatarAuthor.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                                .getBytes(Long.MAX_VALUE)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                        holder.avatarAuthor.setImageBitmap(bitmap);
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
                        if ((post.getTitle() != null) && (!post.getTitle().equals(""))) {
                            holder.title.setText(post.getTitle());
                        } else {
                            holder.title.setVisibility(View.GONE);
                        }
                        if ((post.getBodyImage() != null) && (!post.getBodyImage().equals(""))) {
                            if (image.checkExist(post.getBodyImage())) {
                                byte[] bytes = image.getImage(post.getBodyImage());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.bodyImage.setImageBitmap(bitmap);
                            } else {
                                refStg.child("post/" + post.getBodyImage() + ".png")
                                        .getBytes(Long.MAX_VALUE)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                holder.bodyImage.setImageBitmap(bitmap);
                                                image.Add(post.getBodyImage(), task.getResult());
                                            }
                                        });
                            }
                            holder.bodyImage.setOnClickListener(v -> {
                                onClickShowImage.OnClick(v,post.getBodyImage());
                            });
                        } else {
                            holder.bodyImage.setVisibility(View.GONE);
                        }
                        if ((post.getBodyText() != null) && (!post.getBodyText().equals(""))) {
                            holder.bodyText.setText(post.getBodyText());
                        } else {
                            holder.bodyText.setVisibility(View.GONE);
                        }
                        if ((post.getHashTag() != null) && (!post.getHashTag().equals(""))) {
                            holder.hashTag.setText(post.getHashTag());
                        } else {
                            holder.hashTag.setVisibility(View.GONE);
                        }
                        final boolean[] click = {false};
                        refDb.child(REACT_TIME).child(key.get(position)).addChildEventListener(new ChildEventListener() {
                            @SuppressLint("SetTextI18n")
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if (snapshot.getValue() != null) {
                                    if (snapshot.getValue().toString().equals(fUser.getEmail())) {
                                        holder.react.setBackgroundResource(R.drawable.like);
                                        click[0] = true;
                                    }
                                    count[0]++;
                                }
                                holder.reactTime.setText(count[0] + "");
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                                    previousChildName) {

                            }

                            @SuppressLint("SetTextI18n")
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (Objects.equals(snapshot.getKey(), Objects.requireNonNull(fUser.getEmail()).hashCode() + "")) {
                                        click[0] = false;
                                        holder.react.setBackgroundResource(R.drawable.ic_baseline_add_reaction_24);
                                    }
                                    count[0]--;
                                    holder.reactTime.setText(count[0] + "");
                                }
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.react.setOnClickListener(v -> {
                            if (click[0]) {
                                refDb.child(REACT_TIME).child(key.get(position))
                                        .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").removeValue();
                                click[0] = false;
                            } else {
                                click[0] = true;
                                refDb.child(REACT_TIME).child(key.get(position))
                                        .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(fUser.getEmail());
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

    @Override
    public int getItemCount() {
        if (key != null) {
            return key.size();
        }
        return 0;
    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        public ImageView avatarAuthor, bodyImage;
        public TextView nameAuthor, title, bodyText, reactTime, hashTag;
        public Button react;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            avatarAuthor = itemView.findViewById(R.id.avatarAuthor);
            bodyImage = itemView.findViewById(R.id.ivBodyImagePost);
            nameAuthor = itemView.findViewById(R.id.vNameAuthor);
            title = itemView.findViewById(R.id.tvTitle);
            bodyText = itemView.findViewById(R.id.tvBodyTextPost);
            reactTime = itemView.findViewById(R.id.tvReactTime);
            hashTag = itemView.findViewById(R.id.tvHashTagPost);
            react = itemView.findViewById(R.id.btReactPost);
        }
    }
}
