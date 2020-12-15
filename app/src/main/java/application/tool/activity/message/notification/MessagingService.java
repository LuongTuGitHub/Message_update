package application.tool.activity.message.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ConversationActivity;
import application.tool.activity.message.module.Notification;
import application.tool.activity.message.module.SQLiteImage;

import static application.tool.activity.message.App.CHANNEL_ID;
import static application.tool.activity.message.module.Firebase.AVATAR;
import static application.tool.activity.message.module.Firebase.TOKEN;


public class MessagingService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        DatabaseReference refDb = FirebaseDatabase.getInstance().getReference();
        StorageReference refStg = FirebaseStorage.getInstance().getReference();
        SQLiteImage image = new SQLiteImage(getApplicationContext());
        String type = remoteMessage.getData().get("type");
        MediaPlayer player=MediaPlayer.create(this,R.raw.destroy);
        player.start();
        if(type!=null){
            if(type.equals(Notification.MESSAGE)){
                String key = remoteMessage.getData().get("key");
                String from =remoteMessage.getData().get("from");
                String body = remoteMessage.getData().get("body");
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("key",key);
                intent.putExtra("status",true);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
                android.app.Notification.Builder builder = new android.app.Notification.Builder(getApplicationContext(),CHANNEL_ID);
                builder.setSmallIcon(R.drawable.icon)
                .setContentTitle(from)
                .setContentText(body)
                .setContentIntent(pendingIntent);
                assert from != null;
                refDb.child(AVATAR).child(from.hashCode()+"").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue()!=null){
                                    if(image.checkExist(snapshot.getValue().toString())){
                                        byte[] bytes = image.getImage(snapshot.getValue().toString());
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        builder.setLargeIcon(bitmap);
                                    }else {
                                        refStg.getBytes(Long.MAX_VALUE)
                                                .addOnCompleteListener(task -> {
                                                    if(task.isSuccessful()){
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0,task.getResult().length);
                                                        builder.setLargeIcon(bitmap);
                                                        image.Add(snapshot.getValue().toString(),task.getResult());
                                                    }
                                                });
                                    }
                                }else {
                                    ImageView view = new ImageView(getApplicationContext());
                                    view.setImageResource(R.drawable.icon);
                                    BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    builder.setLargeIcon(bitmap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.notify(0,builder.build());
            }
        }

    }
    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
    }

    private void updateToken(String refreshToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(TOKEN);
        Token token = new Token(refreshToken);
        assert user != null;
        reference.child(Objects.requireNonNull(user.getEmail()).hashCode() + "").setValue(token);
    }

}