package application.tool.activity.message.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import application.tool.activity.message.PositionTo;
import application.tool.activity.message.R;
import application.tool.activity.message.module.SQLiteImage;
import application.tool.activity.message.module.TypeMessage;
import application.tool.activity.message.object.Message;
import de.hdodenhof.circleimageview.CircleImageView;

import static application.tool.activity.message.module.Firebase.AVATAR;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    FirebaseUser fUser;
    DatabaseReference refDb;
    StorageReference refStg;
    List<Message> messages;
    private Context context;
    private SQLiteImage sqLite;
    private final static int MESSAGE_TEXT_LEFT = 1;
    private final static int MESSAGE_TEXT_RIGHT = 4;
    private final static int MESSAGE_IMAGE_LEFT = 10;
    private final static int MESSAGE_IMAGE_RIGHT = 15;
    private final static int MESSAGE_LIKE_LEFT = 20;
    private final static int MESSAGE_LIKE_RIGHT = 100;
    private final static int MESSAGE_REPLY_TEXT_LEFT = 200;
    private final static int MESSAGE_REPLY_TEXT_RIGHT = 201;
    private final static int MESSAGE_REPLY_IMAGE_LEFT = 203;
    private final static int MESSAGE_REPLY_IMAGE_RIGHT = 204;
    private final static int MESSAGE_REPLY_LIKE_LEFT = 210;
    private final static int MESSAGE_REPLY_LIKE_RIGHT = 220;
    private ItemOnClickListener itemOnClickListener;
    private OnLongClickItemListener onLongClickItemListener;
    private PositionTo positionTo;
    private OnClickScrollListener onClickScrollListener;

    public OnClickScrollListener getOnClickScrollListener() {
        return onClickScrollListener;
    }

    public void setOnClickScrollListener(OnClickScrollListener onClickScrollListener) {
        this.onClickScrollListener = onClickScrollListener;
    }

    public MessageAdapter(List<Message> messages, Context context, ItemOnClickListener itemOnClickListener, OnLongClickItemListener onLongClickItemListener) {
        this.messages = messages;
        this.context = context;
        this.itemOnClickListener = itemOnClickListener;
        this.onLongClickItemListener = onLongClickItemListener;
        positionTo = new PositionTo();
        this.sqLite = new SQLiteImage(context);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        refDb = FirebaseDatabase.getInstance().getReference();
        refStg = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_image_right, parent, false);

        if (viewType == MESSAGE_IMAGE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_image_left, parent, false);
        }
        if (viewType == MESSAGE_IMAGE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_image_right, parent, false);
        }
        if (viewType == MESSAGE_TEXT_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_text_left, parent, false);
        }
        if (viewType == MESSAGE_TEXT_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_text_right, parent, false);
        }

        if (viewType == MESSAGE_LIKE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_like_left, parent, false);
        }
        if (viewType == MESSAGE_LIKE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_like_right, parent, false);
        }
        if (viewType == MESSAGE_REPLY_IMAGE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_image_left, parent, false);
        }
        if (viewType == MESSAGE_REPLY_IMAGE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_image_right, parent, false);
        }
        if (viewType == MESSAGE_REPLY_LIKE_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_like_left, parent, false);
        }
        if (viewType == MESSAGE_REPLY_LIKE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_like_right, parent, false);
        }
        if (viewType == MESSAGE_REPLY_TEXT_LEFT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_text_left, parent, false);
        }
        if (viewType == MESSAGE_REPLY_TEXT_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_reply_text_right, parent, false);
        }
        return new MessageHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = messages.get(position);
        ArrayList<String> denied = message.getDenied();
        if (message.getType() == TypeMessage.MESSAGE_DELETE) {
            holder.itemView.setVisibility(View.GONE);
            ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
            iv_show_image.setVisibility(View.GONE);

        } else {
            if (message.getType() == TypeMessage.MESSAGE_IMAGE_HIDE || message.getType() == TypeMessage.MESSAGE_TEXT_HIDE) {
                if (check(denied)) {
                    holder.itemView.setVisibility(View.GONE);
                    if (getItemViewType(position) == MESSAGE_IMAGE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        civ.setVisibility(View.GONE);
                        iv_show_image.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_IMAGE_RIGHT) {
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        iv_show_image.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_TEXT_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        civ.setVisibility(View.GONE);
                        tv_show_text.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_TEXT_RIGHT) {
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        tv_show_text.setVisibility(View.GONE);
                    }

                    if (getItemViewType(position) == MESSAGE_LIKE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);
                        bt_like.setVisibility(View.GONE);
                        civ.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_LIKE_RIGHT) {
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);
                        bt_like.setVisibility(View.GONE);
                    }
                    /***
                     * reply text image like
                     */
                    if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        tv_show_message.setVisibility(View.GONE);
                        civ.setVisibility(View.GONE);
                        iv_show_image.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_RIGHT) {
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        tv_show_message.setVisibility(View.GONE);
                        iv_show_image.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_TEXT_LEFT) {

                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);

                        tv_show_message.setVisibility(View.GONE);
                        civ.setVisibility(View.GONE);
                        tv_show_text.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_TEXT_RIGHT) {

                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);

                        tv_show_message.setVisibility(View.GONE);
                        tv_show_text.setVisibility(View.GONE);
                    }

                    if (getItemViewType(position) == MESSAGE_REPLY_LIKE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);

                        tv_show_message.setVisibility(View.GONE);
                        bt_like.setVisibility(View.GONE);
                        civ.setVisibility(View.GONE);
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_LIKE_RIGHT) {
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);

                        tv_show_message.setVisibility(View.GONE);
                        bt_like.setVisibility(View.GONE);
                    }
                } else {
                    if (getItemViewType(position) == MESSAGE_IMAGE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }
                        if (sqLite.checkExist(message.getBody())) {
                            byte[] bytes = sqLite.getImage(message.getBody());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_show_image.setImageBitmap(bitmap);
                        } else {
                            refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    iv_show_image.setImageBitmap(bitmap);
                                    sqLite.Add(message.getBody(), task.getResult());
                                }
                            });
                        }
                        iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                        iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    if (getItemViewType(position) == MESSAGE_IMAGE_RIGHT) {
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);

                        if (sqLite.checkExist(message.getBody())) {
                            byte[] bytes = sqLite.getImage(message.getBody());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_show_image.setImageBitmap(bitmap);
                        } else {
                            refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    iv_show_image.setImageBitmap(bitmap);
                                    sqLite.Add(message.getBody(), task.getResult());
                                }
                            });
                        }

                        iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                        iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    if (getItemViewType(position) == MESSAGE_TEXT_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);

                        tv_show_text.setText(message.getBody());

                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }

                        tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    if (getItemViewType(position) == MESSAGE_TEXT_RIGHT) {

                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        tv_show_text.setText(message.getBody());

                        tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }

                    if (getItemViewType(position) == MESSAGE_LIKE_LEFT) {
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }

                        bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }

                    if (getItemViewType(position) == MESSAGE_LIKE_RIGHT) {
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                        bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }

                    if (getItemViewType(position) == MESSAGE_REPLY_TEXT_LEFT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));
                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);

                        tv_show_text.setText(message.getBody());

                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }

                        tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_TEXT_RIGHT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));
                        /***
                         * reply text
                         */
                        TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                        tv_show_text.setText(message.getBody());
                        tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    /////// complete text
                    if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_RIGHT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));


                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);

                        if (sqLite.checkExist(message.getBody())) {
                            byte[] bytes = sqLite.getImage(message.getBody());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_show_image.setImageBitmap(bitmap);
                        } else {
                            refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    iv_show_image.setImageBitmap(bitmap);
                                    sqLite.Add(message.getBody(), task.getResult());
                                }
                            });
                        }

                        iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                        iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_LEFT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }
                        if (sqLite.checkExist(message.getBody())) {
                            byte[] bytes = sqLite.getImage(message.getBody());
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_show_image.setImageBitmap(bitmap);
                        } else {
                            refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                    iv_show_image.setImageBitmap(bitmap);
                                    sqLite.Add(message.getBody(), task.getResult());
                                }
                            });
                        }
                        iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                        iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                    /***
                     * reply image
                     */
                    ////////// complete reply image
                    if (getItemViewType(position) == MESSAGE_REPLY_LIKE_LEFT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                        CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                        refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    if (sqLite.checkExist(snapshot.getValue().toString())) {
                                        byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        civ.setImageBitmap(bitmap);
                                    } else {
                                        refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                                civ.setImageBitmap(bitmap);
                                                sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                            civ.setVisibility(View.INVISIBLE);
                        }

                        bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));

                    }
                    if (getItemViewType(position) == MESSAGE_REPLY_LIKE_RIGHT) {
                        TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                        int index = Integer.parseInt(message.getForMessage());
                        int type = messages.get(index).getType();
                        if (type == TypeMessage.MESSAGE_DELETE) {
                            tv_show_message.setText("Đã xóa");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                            tv_show_message.setText("Trả lời một ảnh");
                        }
                        if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                            tv_show_message.setText("Trả lời một ảnh bị ẩn");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                            tv_show_message.setText("like");
                        }
                        if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                            tv_show_message.setText(messages.get(index).getBody());
                        }
                        if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                            tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                        }
                        tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                        Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                        bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                    }
                }
            } else {

                if (getItemViewType(position) == MESSAGE_IMAGE_LEFT) {
                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }
                    if (sqLite.checkExist(message.getBody())) {
                        byte[] bytes = sqLite.getImage(message.getBody());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        iv_show_image.setImageBitmap(bitmap);
                    } else {
                        refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                iv_show_image.setImageBitmap(bitmap);
                                sqLite.Add(message.getBody(), task.getResult());
                            }
                        });
                    }
                    iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                    iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                if (getItemViewType(position) == MESSAGE_IMAGE_RIGHT) {
                    ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);

                    if (sqLite.checkExist(message.getBody())) {
                        byte[] bytes = sqLite.getImage(message.getBody());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        iv_show_image.setImageBitmap(bitmap);
                    } else {
                        refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                iv_show_image.setImageBitmap(bitmap);
                                sqLite.Add(message.getBody(), task.getResult());
                            }
                        });
                    }

                    iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                    iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                if (getItemViewType(position) == MESSAGE_TEXT_LEFT) {
                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);

                    tv_show_text.setText(message.getBody());

                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }

                    tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                if (getItemViewType(position) == MESSAGE_TEXT_RIGHT) {

                    TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                    tv_show_text.setText(message.getBody());

                    tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }

                if (getItemViewType(position) == MESSAGE_LIKE_LEFT) {
                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }

                    bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }

                if (getItemViewType(position) == MESSAGE_LIKE_RIGHT) {
                    Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                    bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }

                if (getItemViewType(position) == MESSAGE_REPLY_TEXT_LEFT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));
                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);

                    tv_show_text.setText(message.getBody());

                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }

                    tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                if (getItemViewType(position) == MESSAGE_REPLY_TEXT_RIGHT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));
                    /***
                     * reply text
                     */
                    TextView tv_show_text = holder.itemView.findViewById(R.id.tv_show_text);
                    tv_show_text.setText(message.getBody());
                    tv_show_text.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                /////// complete text
                if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_RIGHT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));


                    ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);

                    if (sqLite.checkExist(message.getBody())) {
                        byte[] bytes = sqLite.getImage(message.getBody());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        iv_show_image.setImageBitmap(bitmap);
                    } else {
                        refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                iv_show_image.setImageBitmap(bitmap);
                                sqLite.Add(message.getBody(), task.getResult());
                            }
                        });
                    }

                    iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                    iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                if (getItemViewType(position) == MESSAGE_REPLY_IMAGE_LEFT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    ImageView iv_show_image = holder.itemView.findViewById(R.id.iv_show_image_message);
                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }
                    if (sqLite.checkExist(message.getBody())) {
                        byte[] bytes = sqLite.getImage(message.getBody());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        iv_show_image.setImageBitmap(bitmap);
                    } else {
                        refStg.child("messages/" + message.getBody() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                iv_show_image.setImageBitmap(bitmap);
                                sqLite.Add(message.getBody(), task.getResult());
                            }
                        });
                    }
                    iv_show_image.setOnClickListener(v -> itemOnClickListener.onClickItem(v, position));
                    iv_show_image.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
                /***
                 * reply image
                 */
                ////////// complete reply image
                if (getItemViewType(position) == MESSAGE_REPLY_LIKE_LEFT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                    CircleImageView civ = holder.itemView.findViewById(R.id.civ_avatar);
                    Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                    refDb.child(AVATAR).child(message.getFrom().hashCode() + "").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                if (sqLite.checkExist(snapshot.getValue().toString())) {
                                    byte[] bytes = sqLite.getImage(snapshot.getValue().toString());
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    civ.setImageBitmap(bitmap);
                                } else {
                                    refStg.child("avatar").child(snapshot.getValue().toString() + ".png").getBytes(Long.MAX_VALUE).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length);
                                            civ.setImageBitmap(bitmap);
                                            sqLite.Add(snapshot.getValue().toString(), task.getResult());
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    if (!positionTo.checkPosition(position, fUser.getEmail(), messages)) {
                        civ.setVisibility(View.INVISIBLE);
                    }
                    bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));

                }
                if (getItemViewType(position) == MESSAGE_REPLY_LIKE_RIGHT) {
                    TextView tv_show_message = holder.itemView.findViewById(R.id.tv_show_message_reply);
                    int index = Integer.parseInt(message.getForMessage());
                    int type = messages.get(index).getType();
                    if (type == TypeMessage.MESSAGE_DELETE) {
                        tv_show_message.setText("Đã xóa");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE || type == TypeMessage.MESSAGE_REPLY_IMAGE) {
                        tv_show_message.setText("Trả lời một ảnh");
                    }
                    if (type == TypeMessage.MESSAGE_IMAGE_HIDE || type == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                        tv_show_message.setText("Trả lời một ảnh bị ẩn");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT && messages.get(index).getBody().equals("---like")) {
                        tv_show_message.setText("like");
                    }
                    if (type == TypeMessage.MESSAGE_TEXT || type == TypeMessage.MESSAGE_REPLY_TEXT) {
                        tv_show_message.setText(messages.get(index).getBody());
                    }
                    if (type == TypeMessage.MESSAGE_TEXT_HIDE || type == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                        tv_show_message.setText("Trả lời một tin nhắn bị ẩn");
                    }
                    tv_show_message.setOnClickListener(v -> onClickScrollListener.onScrollToPosition(v, index));

                    Button bt_like = holder.itemView.findViewById(R.id.bt_like_message);

                    bt_like.setOnLongClickListener(v -> onLongClickItemListener.OnLongClick(v, position));
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }


    public static class MessageHolder extends RecyclerView.ViewHolder {

        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            this.setIsRecyclable(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getFrom().equals(fUser.getEmail())) {
            if ((messages.get(position).getType() == TypeMessage.MESSAGE_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_TEXT_HIDE) && (messages.get(position).getBody().trim().equals("---like"))) {
                return MESSAGE_LIKE_RIGHT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_TEXT_HIDE) {
                return MESSAGE_TEXT_RIGHT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_IMAGE || messages.get(position).getType() == TypeMessage.MESSAGE_IMAGE_HIDE) {
                return MESSAGE_IMAGE_RIGHT;
            }
            if ((messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) && (messages.get(position).getBody().trim().equals("---like"))) {
                return MESSAGE_REPLY_LIKE_RIGHT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                return MESSAGE_REPLY_TEXT_RIGHT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                return MESSAGE_REPLY_IMAGE_RIGHT;
            }
        } else {
            if ((messages.get(position).getType() == TypeMessage.MESSAGE_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_TEXT_HIDE) && (messages.get(position).getBody().trim().equals("---like"))) {
                return MESSAGE_LIKE_LEFT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_TEXT_HIDE) {
                return MESSAGE_TEXT_LEFT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_IMAGE || messages.get(position).getType() == TypeMessage.MESSAGE_IMAGE_HIDE) {
                return MESSAGE_IMAGE_LEFT;
            }
            if ((messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) && (messages.get(position).getBody().trim().equals("---like"))) {
                return MESSAGE_REPLY_LIKE_LEFT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_TEXT_HIDE) {
                return MESSAGE_REPLY_TEXT_LEFT;
            }
            if (messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE || messages.get(position).getType() == TypeMessage.MESSAGE_REPLY_IMAGE_HIDE) {
                return MESSAGE_REPLY_IMAGE_LEFT;
            }
        }
        return 0;
    }

    public boolean check(ArrayList<String> denied) {
        for (int i = 0; i < denied.size(); i++) {
            if (denied.get(i).equals(fUser.getEmail())) {
                return true;
            }
        }

        return false;
    }
}