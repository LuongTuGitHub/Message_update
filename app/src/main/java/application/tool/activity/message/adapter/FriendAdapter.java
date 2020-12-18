package application.tool.activity.message.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Person;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {
    private ArrayList<String> alFriend;
    private final ItemOnClickListener imItemOnClickListener;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private SQLiteImage image;

    public FriendAdapter(ArrayList<String> alFriend, ItemOnClickListener imItemOnClickListener) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        this.alFriend = alFriend;
        this.imItemOnClickListener = imItemOnClickListener;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        image = new SQLiteImage(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friend_adapter, parent, false);
        return new FriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        refDb.child(Firebase.STATUS).child(alFriend.get(position).hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            String status = snapshot.getValue().toString();
                            if (status.equals("online")) {
                                holder.fStatus.setBackgroundColor(Color.GREEN);
                            } else {
                                holder.fStatus.setBackgroundColor(Color.GRAY);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        refDb.child(Firebase.AVATAR).child(alFriend.get(position).hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (image.checkExist(snapshot.getValue().toString())) {
                                byte[] bytes = image.getImage(snapshot.getValue().toString());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.ivAvatarFriend.setImageBitmap(bitmap);
                            } else {
                                refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                        .getBytes(Long.MAX_VALUE)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                holder.ivAvatarFriend.setImageBitmap(bitmap);
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
        refDb.child(Firebase.PERSON).child(alFriend.get(position).hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Person person = snapshot.getValue(Person.class);
                            if (person != null) {
                                holder.tvName.setText(person.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.ivAvatarFriend.setOnClickListener(v -> imItemOnClickListener.onClickItem(v, position));
    }

    @Override
    public int getItemCount() {
        return alFriend.size();
    }

    public static class FriendHolder extends RecyclerView.ViewHolder {
        public FrameLayout fStatus;
        public ImageView ivAvatarFriend;
        public TextView tvName;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            fStatus = itemView.findViewById(R.id.fStatus);
            ivAvatarFriend = itemView.findViewById(R.id.ivAvatarFriend);
            tvName = itemView.findViewById(R.id.tvNameFriend);
        }
    }
}
