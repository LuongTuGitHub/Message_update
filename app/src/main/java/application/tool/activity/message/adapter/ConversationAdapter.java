package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Message;
import application.tool.activity.message.object.Person;

import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.PERSON;
import static application.tool.activity.message.module.Firebase.STATUS;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {
    private ArrayList<String> conversations;
    private final FirebaseUser user;
    private final DatabaseReference refDb;
    private final StorageReference refStg;
    private final ItemOnClickListener imItemOnClickListener;
    private SQLiteImage image;
    public ConversationAdapter(ArrayList<String> conversations, ItemOnClickListener imItemOnClickListener) {
        this.conversations = conversations;
        user = FirebaseAuth.getInstance().getCurrentUser();
        refStg = FirebaseStorage.getInstance().getReference();
        refDb = FirebaseDatabase.getInstance().getReference();
        this.imItemOnClickListener = imItemOnClickListener;
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        image = new SQLiteImage(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation, parent, false);
        return new ConversationHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        holder.messageCurrent.setText("Hãy gửi lời chào, để bắt đầu cuộc trò chuyện mới ");
        refDb.child(CONVERSATION).child(conversations.get(position)).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getValue() != null) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        if (message.getFrom().equals(user.getEmail())) {
                            if (message.getBody().equals("---like")) {
                                holder.messageCurrent.setText("Bạn: like");
                            } else {
                                holder.messageCurrent.setText("Bạn: " + message.getBody());
                            }
                        } else {
                            if (message.getBody().equals("---like")) {
                                holder.messageCurrent.setText("like");
                            } else {
                                holder.messageCurrent.setText(message.getBody());
                            }
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
        refDb.child(CONVERSATION).child(conversations.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Conversation conversation = snapshot.getValue(Conversation.class);
                    if (conversation != null) {
                        if (conversation.getName() == null) {
                            for (int i = 0; i < conversation.getPersons().size(); i++) {
                                if (!conversation.getPersons().get(i).getEmail().equals(user.getEmail())) {
                                    if (conversation.getPersons().get(i).getNickName() != null && (!conversation.getPersons().get(i).getNickName().equals(""))) {
                                        holder.nameConversation.setText(conversation.getPersons().get(i).getNickName());
                                    } else {
                                        refDb.child(PERSON).child(conversation.getPersons().get(i).getEmail().hashCode() + "")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getValue() != null) {
                                                            Person person = snapshot.getValue(Person.class);
                                                            if (person != null) {
                                                                if (person.getName() != null && (!person.getName().equals(""))) {
                                                                    holder.nameConversation.setText(person.getName());
                                                                } else {
                                                                    holder.nameConversation.setText(person.getEmail());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                    }
                                    refDb.child(AVATAR).child(conversation.getPersons().get(i).getEmail().hashCode() + "")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.getValue() != null) {
                                                        if(image.checkExist(snapshot.getValue().toString())){
                                                            byte[] bytes = image.getImage(snapshot.getValue().toString());
                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                            holder.avatar.setImageBitmap(bitmap);
                                                        }else {
                                                            refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                                                    .getBytes(Long.MAX_VALUE)
                                                                    .addOnCompleteListener(task -> {
                                                                        if(task.isSuccessful()){
                                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                                                            holder.avatar.setImageBitmap(bitmap);
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
                                    refDb.child(STATUS).child(conversation.getPersons().get(i).getEmail().hashCode() + "")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.getValue() != null) {
                                                        if (snapshot.getValue().toString().equals("online")) {
                                                            holder.status.setBackground(new ColorDrawable(Color.GREEN));
                                                        } else {
                                                            holder.status.setBackground(new ColorDrawable(Color.GRAY));
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                            }
                        } else {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setOnClickListener(v -> imItemOnClickListener.onClickItem(v, position));
    }

    @Override
    public int getItemCount() {
        if (conversations != null) {
            return conversations.size();
        }
        return 0;
    }

    public static class ConversationHolder extends RecyclerView.ViewHolder {
        public TextView messageCurrent, nameConversation;
        public ImageView status, avatar;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);
            messageCurrent = itemView.findViewById(R.id.messageCurrent);
            nameConversation = itemView.findViewById(R.id.textView2);
            status = itemView.findViewById(R.id.statusConversation);
            avatar = itemView.findViewById(R.id.avatarConversation);
        }
    }
}
