package application.tool.activity.message.notification;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

import application.tool.activity.message.receiver.NotificationReceiver;

import static application.tool.activity.message.module.Firebase.TOKEN;


public class MessagingService extends FirebaseMessagingService {
    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String key = remoteMessage.getData().get("user");
        if (fUser != null) {
            if (from != null) {
                if (!from.equals(fUser.getEmail())) {
                    Intent intent = new Intent(this, NotificationReceiver.class);
                    intent.putExtra("key", key);
                    intent.putExtra("from", from);
                    intent.putExtra("body", body);
                    sendBroadcast(intent);
                }
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        updateToken(s);
    }

    private void updateToken(String refreshToken) {
        if (fUser != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(TOKEN);
            Token token = new Token(refreshToken);
            reference.child(Objects.requireNonNull(fUser.getEmail()).hashCode() + "").setValue(token);
        }
    }


}