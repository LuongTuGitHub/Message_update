package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.algorithm.PositionTo;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.MessageForConversation;

public class MessageAdapter extends ArrayAdapter<MessageForConversation> {
    FirebaseUser user;
    ArrayList<MessageForConversation> list;
    Context context;
    LayoutInflater layoutInflater;

    public MessageAdapter(Context context, ArrayList<MessageForConversation> list) {
        super(context, 0, list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.list = list;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.message, null);
        TextView send = convertView.findViewById(R.id.send);
        TextView to = convertView.findViewById(R.id.to);
        ImageView likeTo = convertView.findViewById(R.id.likeTo);
        ImageView likeSend = convertView.findViewById(R.id.likeSend);
        ImageView imageTo = convertView.findViewById(R.id.imageTo);
        CardView layoutTo = convertView.findViewById(R.id.layoutTo);
        CardView layoutSend = convertView.findViewById(R.id.layoutSend);
        ImageView imageSend = convertView.findViewById(R.id.pictureSend);
        ImageView avatar = convertView.findViewById(R.id.avatarTo);/////////////////////////  avatar to
        CardView layout = convertView.findViewById(R.id.layout);
        //////////////
        if(new PositionTo().checkPosition(position,user.getEmail(),list)){
            layout.setVisibility(View.VISIBLE);
            new Avatar().getIconImage(list.get(position).getFrom(),avatar);
        }else {
            layout.setVisibility(View.INVISIBLE);
        }
        if (list.get(position).getType() == 2) {
            /// to likeTo likeSend imageTo imageSend layoutTo layoutSend avatar layout send convertView
            layoutSend.setVisibility(View.GONE);
            layoutTo.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            convertView.setVisibility(View.GONE);
            avatar.setVisibility(View.GONE);
            imageSend.setVisibility(View.GONE);
            imageTo.setVisibility(View.GONE);
        }
        if (list.get(position).getType() == 0) {
            if (!list.get(position).getBody().equals("---like")) {
                if (list.get(position).getFrom().equals(user.getEmail())) {
                    send.setText(list.get(position).getBody());
                    to.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    to.setText(list.get(position).getBody());
                }
                likeSend.setVisibility(View.GONE);
                likeTo.setVisibility(View.GONE);
            } else {
                if (list.get(position).getFrom().equals(user.getEmail())) {
                    likeTo.setVisibility(View.GONE);
                } else {
                    likeSend.setVisibility(View.GONE);
                }
                send.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
            }
            layoutTo.setVisibility(View.GONE);
            layoutSend.setVisibility(View.GONE);
        }
        if (list.get(position).getType() == 1) {
            send.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            if (list.get(position).getFrom().equals(user.getEmail())) {
                layoutTo.setVisibility(View.GONE);
                new Avatar().getMessageImage(list.get(position).getBody(), imageSend);
            } else {
                new Avatar().getMessageImage(list.get(position).getBody(), imageTo);
                layoutSend.setVisibility(View.GONE);
            }
        }
        if (list.get(position).getType() == 3) {
            ArrayList<String> denied = list.get(position).getDenied();
            for (int i = 0; i < denied.size(); i++) {
                if (denied.get(i).equals(user.getEmail())) {
                    layoutSend.setVisibility(View.GONE);
                    layoutTo.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    likeSend.setVisibility(View.GONE);
                    likeTo.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    convertView.setVisibility(View.GONE);
                }
            }
            if (!list.get(position).getBody().equals("---like")) {
                if (list.get(position).getFrom().equals(user.getEmail())) {
                    send.setText(list.get(position).getBody());
                    to.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    to.setText(list.get(position).getBody());
                }
                likeSend.setVisibility(View.GONE);
                likeTo.setVisibility(View.GONE);
            } else {
                if (list.get(position).getFrom().equals(user.getEmail())) {
                    likeTo.setVisibility(View.GONE);
                } else {
                    likeSend.setVisibility(View.GONE);
                }
                send.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
            }
            layoutTo.setVisibility(View.GONE);
            layoutSend.setVisibility(View.GONE);
        }
        if (list.get(position).getType() == 4) {
            ArrayList<String> denied = list.get(position).getDenied();
            for (int i = 0; i < denied.size(); i++) {
                if (denied.get(i).equals(user.getEmail())) {
                    layoutSend.setVisibility(View.GONE);
                    layoutTo.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    likeSend.setVisibility(View.GONE);
                    likeTo.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    convertView.setVisibility(View.GONE);
                }
            }
            send.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            if (list.get(position).getFrom().equals(user.getEmail())) {
                layoutTo.setVisibility(View.GONE);
                new Avatar().getMessageImage(list.get(position).getBody(), imageSend);
            } else {
                new Avatar().getMessageImage(list.get(position).getBody(), imageTo);
                layoutSend.setVisibility(View.GONE);
            }
        }
        if(new PositionTo().checkPosition(position,user.getEmail(),list)){
            layout.setVisibility(View.VISIBLE);
            new Avatar(list.get(position).getFrom()).setAvatar(avatar);
        }else {
            layout.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
