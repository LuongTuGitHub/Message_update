package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Information;

public class InformationAdapter extends BaseAdapter {
    ArrayList<Information> information;

    public InformationAdapter(ArrayList<Information> information) {
        this.information = information;
    }

    @Override
    public int getCount() {
        return information.size();
    }

    @Override
    public Object getItem(int position) {
        return information.get(position);
    }

    @Override
    public long getItemId(int position) {
        return information.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint({"InflateParams", "ViewHolder"}) View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_information,null);
        View vIcon = view.findViewById(R.id.vIcon);
        TextView tvContent = view.findViewById(R.id.tvContent);
        vIcon.setBackgroundResource(information.get(position).getId());
        tvContent.setText(information.get(position).getContent());
        return view;
    }
}
