package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.check.CheckUserInConversation;
import application.tool.activity.message.object.Avatar;
import application.tool.activity.message.object.KeyConversation;
import application.tool.activity.message.object.PersonInConversation;

public class ConversationAdapter extends ArrayAdapter<KeyConversation> {
    Context context;
    LayoutInflater layoutInflater;
    DatabaseReference reference;
    TextView name;
    FirebaseUser user;
    ImageView image;
    ArrayList<KeyConversation> keyConversations;

    public ConversationAdapter(Context context, ArrayList<KeyConversation> keyConversations) {
        super(context, 0, keyConversations);
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.keyConversations = keyConversations;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.person, null);
        name = convertView.findViewById(R.id.viewEmail);
        image = convertView.findViewById(R.id.imagePerson);
        if (!keyConversations.get(position).getNameGroup().equals("")) {
            name.setText(keyConversations.get(position).getNameGroup());
        }
        if (!keyConversations.get(position).getName().equals("Group Chat")) {
            name.setText(keyConversations.get(position).getName());
            new Avatar(keyConversations.get(position).getName(), "avatar").setAvatar(image);
        } else if (keyConversations.get(position).getNameGroup().equals("")) {
            name.setText(keyConversations.get(position).getName());
        }
        return convertView;
    }


}
