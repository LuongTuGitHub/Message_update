package application.tool.activity.message;

import android.annotation.SuppressLint;
import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MessagingService extends FirebaseMessagingService {
    private final static String CHANNEL_ID = "application.com.tool.message";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(remoteMessage.getData().get("from"))
                .setContentText("Message")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(remoteMessage.getData().get("body")))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.build();
    }
    public void notification(String body){

    }

}