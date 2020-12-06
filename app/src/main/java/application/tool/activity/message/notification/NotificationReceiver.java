package application.tool.activity.message.notification;

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

import application.tool.activity.message.ContentActivity;
import application.tool.activity.message.R;

import static application.tool.activity.message.App.CHANNEL_ID;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("DDD", "SERVICE");
        String from = intent.getStringExtra("from");
        String body = intent.getStringExtra("body");
        Log.e("AAA", "AAA");
        Log.e("AAA", body + " " + from);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intentSend = new Intent(context, ContentActivity.class);
        intentSend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentSend, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(from)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat compat = NotificationManagerCompat.from(context);
        compat.notify(0, builder.build());
        MediaPlayer player = MediaPlayer.create(context,defaultSound);
        player.start();
    }
}
