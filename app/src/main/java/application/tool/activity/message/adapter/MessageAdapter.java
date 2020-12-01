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
        ImageView avatar = convertView.findViewById(R.id.avatarTo);
        CardView layout = convertView.findViewById(R.id.layout);
        if (position == 0) {
            if (list.get(position).getFrom().equals(user.getEmail())) {
                layout.setVisibility(View.INVISIBLE);
            } else {
                new Avatar(list.get(position).getFrom()).setAvatar(avatar);
            }
        }
        if (position > 0) {
            if (!list.get(position).getFrom().equals(user.getEmail())) {
                if (list.get(position).getFrom().equals(list.get(position - 1).getFrom())) {
                    layout.setVisibility(View.INVISIBLE);
                } else {
                    new Avatar(list.get(position).getFrom()).setAvatar(avatar);
                }
            } else {
                layout.setVisibility(View.INVISIBLE);
            }
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
                new Avatar().getMessageImage(list.get(position).getBody(),imageSend);
            } else {
                new Avatar().getMessageImage(list.get(position).getBody(),imageTo);
                layoutSend.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
}
