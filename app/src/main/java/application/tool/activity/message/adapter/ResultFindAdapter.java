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
import application.tool.activity.message.object.FindResult;

public class ResultFindAdapter extends ArrayAdapter<FindResult> {
    ArrayList<FindResult> results;
    Context context;
    LayoutInflater layoutInflater;
     public ResultFindAdapter(Context context, ArrayList<FindResult> result){
        super(context, 0, result);
        this.context = context;
        this.results = result;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @SuppressLint({"InflateParams", "ViewHolder"}) View view = layoutInflater.inflate(R.layout.person,null);
        ImageView imageView = view.findViewById(R.id.imagePerson);
        TextView name = view.findViewById(R.id.viewEmail);
        name.setText(results.get(position).getName());
        new Avatar(results.get(position).getEmail()).setAvatar(imageView);
        return view;
    }
}
