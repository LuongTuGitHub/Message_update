package application.tool.activity.message.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.transform.Result;

import application.tool.activity.message.R;
import application.tool.activity.message.module.Firebase;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Person;

public class AddConversationAdapter extends RecyclerView.Adapter<AddConversationAdapter.AddConversationHolder> {
    private ArrayList<Person> list;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private SQLiteImage image;
    private Context context;
    private ItemOnClickListener itemOnClickListener;

    public AddConversationAdapter(ArrayList<Person> list, Context context, ItemOnClickListener itemOnClickListener) {
        this.list = list;
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        image = new SQLiteImage(context);
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public AddConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_conversation, parent, false);
        return new AddConversationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddConversationHolder holder, int position) {
        refDb.child(Firebase.AVATAR).child(Objects.requireNonNull(list.get(position)).getEmail().hashCode() + "")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (image.checkExist(snapshot.getValue().toString())) {
                                byte[] bytes = image.getImage(snapshot.getValue().toString());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.ivAvatar.setImageBitmap(bitmap);
                            } else {
                                refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                        .getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                        holder.ivAvatar.setImageBitmap(bitmap);
                                        image.Add(snapshot.getValue().toString(), task.getResult());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.tvName.setText(list.get(position).getName());
        holder.check.setOnClickListener(v -> {
            holder.check.setChecked(!holder.check.isChecked());
            itemOnClickListener.onClickItem(v, position);
        });
        holder.ivAvatar.setOnClickListener(v -> {
            holder.check.setChecked(!holder.check.isChecked());
            itemOnClickListener.onClickItem(v, position);
        });
        holder.tvName.setOnClickListener(v -> {
            holder.check.setChecked(!holder.check.isChecked());
            itemOnClickListener.onClickItem(v, position);
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public static class AddConversationHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;
        public TextView tvName;
        public CheckBox check;

        public AddConversationHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.check);
            ivAvatar = itemView.findViewById(R.id.iv_fri);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
