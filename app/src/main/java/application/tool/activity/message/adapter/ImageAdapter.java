package application.tool.activity.message.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import application.tool.activity.message.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private ArrayList<Uri> data;

    public ImageAdapter(ArrayList<Uri> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_image,parent,false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(holder.itemView.getContext().getContentResolver(),data.get(position));
            holder.ivImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder{
        public ImageView ivImage;
        public ConstraintLayout clBackground;
        public CardView cvSelect;
        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            clBackground = itemView.findViewById(R.id.vBackground);
            cvSelect = itemView.findViewById(R.id.cvSelect);
        }
    }
}
