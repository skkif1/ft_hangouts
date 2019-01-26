package com.omotyliu.ft_hangouts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;


public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent)
    {


        if (intent.getAction().equals(SMS_RECEIVED))
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                if (ChatActivity.getInstance() != null)
                {
                    ChatActivity.getInstance().refreshChat();
                }

//                abortBroadcast();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    CharSequence name = "asdasd";
                    String description = "asdknf;saindf;ubsda;o";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel("1", name, importance);
                    channel.setDescription(description);
                    NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                            .setSmallIcon(R.drawable.done_24dp)
                            .setContentTitle("My notification")
                            .setContentText("Much longer text that cannot fit one line...")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText("Much longer text that cannot fit one line..."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    mBuilder.build();
                }
            }
        }
    }

}
