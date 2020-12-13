package application.tool.activity.message.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import application.tool.activity.message.object.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private ArrayList<Notification> notifications;
    private ItemOnClickListener itemOnClickListener;

    public NotificationAdapter(ArrayList<Notification> notifications, ItemOnClickListener itemOnClickListener) {
        this.notifications = notifications;
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {


        holder.itemView.setOnClickListener(v -> itemOnClickListener.onClickItem(v,position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder{

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
