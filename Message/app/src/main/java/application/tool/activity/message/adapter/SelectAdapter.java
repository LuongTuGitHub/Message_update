package application.tool.activity.message.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Select;

public class SelectAdapter extends BaseAdapter {
    ArrayList<Select> arrayList;

    public SelectAdapter(ArrayList<Select> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = View.inflate(parent.getContext(), R.layout.select, null);
        } else {
            view = convertView;
        }
        View v = view.findViewById(R.id.labelSelect);
        TextView text = view.findViewById(R.id.textSelect);
        v.setBackgroundResource(arrayList.get(position).getId());
        text.setText(arrayList.get(position).getBody());
        return view;
    }
}
