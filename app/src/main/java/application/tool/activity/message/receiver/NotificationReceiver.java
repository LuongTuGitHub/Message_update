package application.tool.activity.message.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import application.tool.activity.message.R;
import application.tool.activity.message.activity.ConversationActivity;

import static application.tool.activity.message.App.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("DDD", "SERVICE");
        String from = intent.getStringExtra("from");
        String body = intent.getStringExtra("body");
        String key = intent.getStringExtra("key");
        Log.e("AAA", "AAA");
        Log.e("AAA", body + " " + from);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intentSend = new Intent(context, ConversationActivity.class);
        intentSend.putExtra("key", key);
        intentSend.putExtra("status", true);
        intentSend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentSend, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(from)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).setSound(defaultSound);
        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(0, builder.build());
    }
}