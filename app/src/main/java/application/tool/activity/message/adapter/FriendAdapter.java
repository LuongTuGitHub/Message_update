package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Avatar;

public class FriendAdapter extends ArrayAdapter<String> {
    ArrayList<String> arrayList;
    Context context;
    LayoutInflater layoutInflater;

    public FriendAdapter(Context context, ArrayList<String> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view = layoutInflater.inflate(R.layout.person, parent, false);
        TextView viewEmail = view.findViewById(R.id.viewEmail);
        if (arrayList.get(position) != null) {
            viewEmail.setText(arrayList.get(position));
            new Avatar(arrayList.get(position)).setAvatar(view.findViewById(R.id.imagePerson));
        }
        return view;
    }
}
