package application.tool.activity.message.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import application.tool.activity.message.R;
import application.tool.activity.message.object.Extension;

public class ExtensionAdapter extends RecyclerView.Adapter<ExtensionAdapter.ExtensionHolder> {
    private ArrayList<Extension> extensions;
    private ItemOnClickListener itemOnClickListener;

    public ExtensionAdapter(ArrayList<Extension> extensions, ItemOnClickListener itemOnClickListener) {
        this.extensions = extensions;
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public ExtensionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_extension, parent, false);
        return new ExtensionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtensionHolder holder, int position) {
        holder.btEx.setBackgroundResource(extensions.get(position).getID());
        holder.btEx.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
    }

    @Override
    public int getItemCount() {
        return extensions.size();
    }

    public static class ExtensionHolder extends RecyclerView.ViewHolder {
        public Button btEx;

        public ExtensionHolder(@NonNull View itemView) {
            super(itemView);
            btEx = itemView.findViewById(R.id.btExtension);
        }
    }
}
