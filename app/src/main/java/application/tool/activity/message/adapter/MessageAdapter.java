package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

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

import application.tool.activity.message.PositionTo;
import application.tool.activity.message.R;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.object.Message;

import static application.tool.activity.message.module.Firebase.AVATAR;

public class MessageAdapter extends ArrayAdapter<Message> {
    FirebaseUser fUser;
    DatabaseReference refDb;
    StorageReference refStg;
    ArrayList<Message> messages;
    Context context;
    LayoutInflater layoutInflater;
    private SQLiteImage image;
    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.messages = messages;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        image = new SQLiteImage(context);
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.message, null);
        TextView send = convertView.findViewById(R.id.send);
        TextView to = convertView.findViewById(R.id.to);
        ImageView likeTo = convertView.findViewById(R.id.likeTo);
        ImageView likeSend = convertView.findViewById(R.id.likeSend);
        ImageView imageTo = convertView.findViewById(R.id.imageTo);
        CardView layoutTo = convertView.findViewById(R.id.layoutTo);
        CardView layoutSend = convertView.findViewById(R.id.layoutSend);
        ImageView imageSend = convertView.findViewById(R.id.pictureSend);
        ImageView avatar = convertView.findViewById(R.id.avatarTo);
        CardView layout = convertView.findViewById(R.id.layout);
        if (new PositionTo().checkPosition(position, fUser.getEmail(), messages)) {
            layout.setVisibility(View.VISIBLE);
            refDb.child(AVATAR).child(messages.get(position).getFrom().hashCode() + "")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                               if(image.checkExist(snapshot.getValue().toString())){
                                   byte[] bytes = image.getImage(snapshot.getValue().toString());
                                   Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                   avatar.setImageBitmap(bitmap);
                               }else {
                                   refStg.child("avatar" + snapshot.getValue().toString() + ".png")
                                           .getBytes(Long.MAX_VALUE)
                                           .addOnCompleteListener(task -> {
                                               if(task.isSuccessful()){
                                                   Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                                   avatar.setImageBitmap(bitmap);
                                                   image.Add(snapshot.getValue().toString(),task.getResult());
                                               }
                                           });
                               }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            layout.setVisibility(View.INVISIBLE);
        }
        if (messages.get(position).getType() == 2) {
            layoutSend.setVisibility(View.GONE);
            layoutTo.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
            convertView.setVisibility(View.GONE);
            avatar.setVisibility(View.GONE);
            imageSend.setVisibility(View.GONE);
            imageTo.setVisibility(View.GONE);
        }
        if (messages.get(position).getType() == 0) {
            if (!messages.get(position).getBody().equals("---like")) {
                if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                    send.setText(messages.get(position).getBody());
                    to.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    to.setText(messages.get(position).getBody());
                }
                likeSend.setVisibility(View.GONE);
                likeTo.setVisibility(View.GONE);
            } else {
                if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                    likeTo.setVisibility(View.GONE);
                } else {
                    likeSend.setVisibility(View.GONE);
                }
                send.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
            }
            layoutTo.setVisibility(View.GONE);
            layoutSend.setVisibility(View.GONE);
        }
        if (messages.get(position).getType() == 1) {
            send.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                layoutTo.setVisibility(View.GONE);
                if(image.checkExist(messages.get(position).getBody())){
                    byte[] bytes = image.getImage(messages.get(position).getBody());
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageSend.setImageBitmap(bitmap);
                }else {
                    refStg.child("messages/" + messages.get(position).getBody() + ".png")
                            .getBytes(Long.MAX_VALUE)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                    imageSend.setImageBitmap(bitmap);
                                    image.Add(messages.get(position).getBody(),task.getResult());
                                }
                            });
                }
            } else {
                if(image.checkExist(messages.get(position).getBody())){
                    byte[] bytes = image.getImage(messages.get(position).getBody());
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageTo.setImageBitmap(bitmap);
                }else {
                    refStg.child("messages/" + messages.get(position).getBody() + ".png")
                            .getBytes(Long.MAX_VALUE)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                    imageTo.setImageBitmap(bitmap);
                                    image.Add(messages.get(position).getBody(),task.getResult());
                                }
                            });
                }
                layoutSend.setVisibility(View.GONE);
            }
        }
        if (messages.get(position).getType() == 3) {
            ArrayList<String> denied = messages.get(position).getDenied();
            for (int i = 0; i < denied.size(); i++) {
                if (denied.get(i).equals(fUser.getEmail())) {
                    layoutSend.setVisibility(View.GONE);
                    layoutTo.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    likeSend.setVisibility(View.GONE);
                    likeTo.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    convertView.setVisibility(View.GONE);
                }
            }
            if (!messages.get(position).getBody().equals("---like")) {
                if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                    send.setText(messages.get(position).getBody());
                    to.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    to.setText(messages.get(position).getBody());
                }
                likeSend.setVisibility(View.GONE);
                likeTo.setVisibility(View.GONE);
            } else {
                if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                    likeTo.setVisibility(View.GONE);
                } else {
                    likeSend.setVisibility(View.GONE);
                }
                send.setVisibility(View.GONE);
                to.setVisibility(View.GONE);
            }
            layoutTo.setVisibility(View.GONE);
            layoutSend.setVisibility(View.GONE);
        }
        if (messages.get(position).getType() == 4) {
            ArrayList<String> denied = messages.get(position).getDenied();
            for (int i = 0; i < denied.size(); i++) {
                if (denied.get(i).equals(fUser.getEmail())) {
                    layoutSend.setVisibility(View.GONE);
                    layoutTo.setVisibility(View.GONE);
                    layout.setVisibility(View.GONE);
                    likeSend.setVisibility(View.GONE);
                    likeTo.setVisibility(View.GONE);
                    to.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    convertView.setVisibility(View.GONE);
                }
            }
            send.setVisibility(View.GONE);
            to.setVisibility(View.GONE);
            likeSend.setVisibility(View.GONE);
            likeTo.setVisibility(View.GONE);
            if (messages.get(position).getFrom().equals(fUser.getEmail())) {
                layoutTo.setVisibility(View.GONE);
                if(image.checkExist(messages.get(position).getBody())){
                    byte[] bytes = image.getImage(messages.get(position).getBody());
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageSend.setImageBitmap(bitmap);
                }else {
                    refStg.child("messages/" + messages.get(position).getBody() + ".png")
                            .getBytes(Long.MAX_VALUE)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                    imageSend.setImageBitmap(bitmap);
                                    image.Add(messages.get(position).getBody(),task.getResult());
                                }
                            });
                }
            } else {
               if(image.checkExist(messages.get(position).getBody())){
                   byte[] bytes = image.getImage(messages.get(position).getBody());
                   Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                   imageTo.setImageBitmap(bitmap);
               }else {
                   refStg.child("messages/" + messages.get(position).getBody() + ".png")
                           .getBytes(Long.MAX_VALUE)
                           .addOnCompleteListener(task -> {
                               if(task.isSuccessful()){
                                   Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                   imageTo.setImageBitmap(bitmap);
                                   image.Add(messages.get(position).getBody(),task.getResult());
                               }
                           });
               }
                layoutSend.setVisibility(View.GONE);
            }
        }
        return convertView;
    }
}