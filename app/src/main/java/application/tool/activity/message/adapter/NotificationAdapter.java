package application.tool.activity.message.adapter;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ViewProfileActivity;
import application.tool.activity.message.object.Conversation;
import application.tool.activity.message.object.Notification;
import application.tool.activity.message.object.PersonInConversation;

import static application.tool.activity.message.module.Firebase.CONVERSATION;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND;
import static application.tool.activity.message.module.Firebase.LIST_FRIEND_REQUEST;
import static application.tool.activity.message.module.Notification.MESSAGE;
import static application.tool.activity.message.module.Notification.REQUEST;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private ArrayList<Notification> notifications;
    private ItemOnClickListener itemOnClickListener;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private final static int MESSAGE_CODE = 1;
    private final static int REQUEST_CODE = 0;
    public NotificationAdapter(ArrayList<Notification> notifications, ItemOnClickListener itemOnClickListener) {
        this.notifications = notifications;
        this.itemOnClickListener = itemOnClickListener;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MESSAGE_CODE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_message,parent,false);
            return new NotificationHolder(view);
        }
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_friend_adapter,parent,false);
        return new NotificationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
        holder.tvFrom.setText(notifications.get(position).getFrom());
        holder.body.setText(notifications.get(position).getBody());
        if(getItemViewType(position)==MESSAGE_CODE){
            holder.denied.setVisibility(View.GONE);
            holder.confirm.setVisibility(View.GONE);
        }else {
            refDb.child(REQUEST).child(fUser.getEmail().hashCode()+"")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(snapshot.getValue()!=null){
                                if (snapshot.getValue().toString().equals(notifications.get(position).getFrom())){
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
            refDb.child(CONVERSATION).push().setValue(new Conversation(person, new ArrayList<>(),null))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            refDb.child(LIST_FRIEND_REQUEST)
                                    .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(notifications.get(position).getFrom().hashCode() + "").removeValue();
                            dialog.cancel();
                        }
                    });
        });
        holder.denied.setOnClickListener(v -> refDb.child(LIST_FRIEND_REQUEST)
                .child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").child(notifications.get(position).getFrom().hashCode() + "").removeValue());
        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickItem(v,position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder{
        public Button confirm,denied;
        public TextView tvFrom,body;
        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
            confirm = itemView.findViewById(R.id.button2);
            denied = itemView.findViewById(R.id.button3);
            body = itemView.findViewById(R.id.tvBody);
            tvFrom  = itemView.findViewById(R.id.tvFrom);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(notifications.get(position).getType().equals(MESSAGE))
            return MESSAGE_CODE;
        return REQUEST_CODE;
    }
}
