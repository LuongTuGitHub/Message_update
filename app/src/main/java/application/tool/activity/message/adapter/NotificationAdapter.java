package application.tool.activity.message.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Notification;
import application.tool.activity.message.object.Person;
import application.tool.activity.message.object.PersonInConversation;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND_REQUEST;
import static application.tool.activity.message.module.Firebase.NOTIFICATION;
import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Notification.MESSAGE;
import static application.tool.activity.message.module.Notification.RESPONSE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private ArrayList<Notification> notifications;
    private ItemOnClickListener itemOnClickListener;
    private FirebaseUser fUser;
    private StorageReference refStg;
    private DatabaseReference refDb;
    private SQLiteImage image;

    public NotificationAdapter(ArrayList<Notification> notifications, ItemOnClickListener itemOnClickListener) {
        this.notifications = notifications;
        this.itemOnClickListener = itemOnClickListener;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        image = new SQLiteImage(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_message, parent, false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.tvFrom.setText(notifications.get(position).getFrom());
        refDb.child(PERSON).child(notifications.get(position).getFrom().hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            Person person = snapshot.getValue(Person.class);
                            if (person != null) {
                                holder.tvFrom.setText(person.getName());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        refDb.child(AVATAR).child(notifications.get(position).getFrom().hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (image.checkExist(snapshot.getValue().toString())) {
                                byte[] bytes = image.getImage(snapshot.getValue().toString());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.ivAvatar.setImageBitmap(bitmap);
                            } else {
                                refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                        .getBytes(Long.MAX_VALUE)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                holder.ivAvatar.setImageBitmap(bitmap);
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
        holder.body.setText(notifications.get(position).getBody());
        if (notifications.get(position).getType().equals(MESSAGE) || notifications.get(position).getType().equals(RESPONSE)) {
            holder.denied.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
        } else {
            refDb.child(LIST_FRIEND_REQUEST).child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.getValue() != null) {
                                if (snapshot.getValue().toString().equals(notifications.get(position).getFrom())) {
                                    holder.confirm.setVisibility(View.VISIBLE);
                                    holder.denied.setVisibility(View.VISIBLE);
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
        holder.confirm.setOnClickListener(v -> {
            long key = Calendar.getInstance().getTimeInMillis();
            Notification notification = new Notification(RESPONSE, fUser.getEmail(), "Chấp nhận lời mời kết bạn", null, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    , Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR), key);
            refDb.child(NOTIFICATION).child(notifications.get(position).getFrom().hashCode() + "").child(key + "").setValue(notification);
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setView(R.layout.load);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.show();
            refDb.child(LIST_FRIEND).child(notifications.get(position).getFrom().hashCode() + "")
                    .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(fUser.getEmail());
            refDb.child(LIST_FRIEND)
                    .child(fUser.getEmail().hashCode() + "").child(notifications.get(position).getFrom().hashCode() + "").setValue(notifications.get(position).getFrom());
            ArrayList<PersonInConversation> person = new ArrayList<>();
            person.add(new PersonInConversation("", notifications.get(position).getFrom()));
            person.add(new PersonInConversation("", fUser.getEmail()));
            refDb.child(CONVERSATION).push().setValue(new Conversation(person, new ArrayList<>(), null))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            refDb.child(LIST_FRIEND_REQUEST)
                                    .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(notifications.get(position).getFrom().hashCode() + "").removeValue();
                            holder.denied.setVisibility(View.GONE);
                            holder.confirm.setVisibility(View.GONE);
                            dialog.cancel();
                        }
                    });
        });
        holder.denied.setOnClickListener(v -> {
            long key = Calendar.getInstance().getTimeInMillis();
            Notification notification = new Notification(RESPONSE, fUser.getEmail(), "Từ chối lời mời kết bạn", null, Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    , Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR), key);
            refDb.child(NOTIFICATION).child(notifications.get(position).getFrom().hashCode() + "").child(key + "").setValue(notification);
            holder.denied.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
            refDb.child(LIST_FRIEND_REQUEST)
                    .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(notifications.get(position).getFrom().hashCode() + "").removeValue();
        });
        holder.body.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
        holder.tvFrom.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
        holder.ivAvatar.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {
        public Button confirm, denied;
        public TextView tvFrom, body;
        public ImageView ivAvatar;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            confirm = itemView.findViewById(R.id.button2);
            denied = itemView.findViewById(R.id.button3);
            body = itemView.findViewById(R.id.tvBody);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            ivAvatar = itemView.findViewById(R.id.iv_avatar_notification);
        }
    }
}
