package application.tool.activity.message.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ContentFindActivity;
import application.tool.activity.message.activity.ViewProfileActivity;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Person;

import static application.tool.activity.message.module.Firebase.AVATAR;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.PeopleHolder> implements Filterable {
    private ArrayList<Person> people;
    private ArrayList<Person> peopleAll;
    private FirebaseUser fUser;
    private DatabaseReference refDb;
    private StorageReference refStg;
    private SQLiteImage image;

    public PeopleAdapter(ArrayList<Person> people) {
        this.peopleAll = people;
        this.people = new ArrayList<>(peopleAll);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public PeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        image = new SQLiteImage(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_people, parent, false);
        return new PeopleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeopleHolder holder, int position) {
        holder.tv.setText(people.get(position).getName());
        refDb.child(AVATAR).child(people.get(position).getEmail().hashCode() + "")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            if (image.checkExist(snapshot.getValue().toString())) {
                                byte[] bytes = image.getImage(snapshot.getValue().toString());
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                holder.iv.setImageBitmap(bitmap);
                            } else {
                                refStg.child("avatar/" + snapshot.getValue().toString() + ".png")
                                        .getBytes(Long.MAX_VALUE)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                holder.iv.setImageBitmap(bitmap);
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
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ViewProfileActivity.class);
            intent.putExtra("email", people.get(position).getEmail());
            intent.putExtra("status", false);
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        if (people == null) {
            return 0;
        }
        return people.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Person> personArrayList = new ArrayList<>();
            if (constraint.toString().isEmpty()) {
                personArrayList.addAll(peopleAll);
            } else {
                for (int i = 0; i < peopleAll.size(); i++) {
                    if (peopleAll.get(i).getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || peopleAll.get(i).getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        personArrayList.add(peopleAll.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = personArrayList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            people.clear();
            people.addAll((Collection<? extends Person>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class PeopleHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView iv;

        public PeopleHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);
        }

    }
}
