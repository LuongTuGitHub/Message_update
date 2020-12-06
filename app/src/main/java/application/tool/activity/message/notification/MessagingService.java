package application.tool.activity.message.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import application.tool.activity.message.ContentActivity;
import application.tool.activity.message.R;
import application.tool.activity.message.StartAppActivity;

import static application.tool.activity.message.App.CHANNEL_ID;


public class MessagingService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        Intent intent = new Intent(this,NotificationReceiver.class);
        intent.putExtra("from",from);
        intent.putExtra("body",body);
        sendBroadcast(intent);
    }
    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
    }

    private void updateToken(String refreshToken) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        assert user != null;
        reference.child(Objects.requireNonNull(user.getEmail()).hashCode() + "").setValue(token);
    }


}