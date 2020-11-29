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

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Avatar;

public class ConversationAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> arrayList;

    public ConversationAdapter(Context context, ArrayList<String> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.person, null);
        TextView name = convertView.findViewById(R.id.viewEmail);
        name.setText(arrayList.get(position));
        ImageView image = convertView.findViewById(R.id.imagePerson);
        if (!name.equals("Group Chat")) {
            new Avatar(arrayList.get(position)).setAvatar(image);
        }
        return convertView;
    }
}
