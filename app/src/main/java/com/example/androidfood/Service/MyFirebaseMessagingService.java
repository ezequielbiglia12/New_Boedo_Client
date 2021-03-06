package com.example.androidfood.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.androidfood.Common.Common;
import com.example.androidfood.Helper.NotificationHelper;
import com.example.androidfood.MainActivity;
import com.example.androidfood.Model.Token;
import com.example.androidfood.OrderStatus;
import com.example.androidfood.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            sendNotificationAPI29(remoteMessage);
        else
            sendNotification(remoteMessage);
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String tokenRefreshed=FirebaseInstanceId.getInstance().getToken();
        if (Common.currentuser != null)
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token token = new Token(tokenRefreshed,false);
        tokens.child(Common.currentuser.getPhone()).setValue(token);

    }

    private void sendNotificationAPI29(RemoteMessage remoteMessage){//
        RemoteMessage.Notification  notification = remoteMessage.getNotification();
        String title=notification.getTitle();
        String content=notification.getBody();

        PendingIntent pendingIntent;


        Intent intent = new Intent(this, OrderStatus.class);
        intent.putExtra(Common.PHONE_TEXT,Common.currentuser.getPhone());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder =helper.getNewBoedoChannelNotification(title,content,pendingIntent,defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(),builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String Notification_channel_id= "com.example.newboedoserver.Service.MyFirebaseMessagingService";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            NotificationChannel channel = new NotificationChannel(Notification_channel_id,
                    "Notification", NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("newboedo");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        Intent intent = new Intent(this, OrderStatus.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,Notification_channel_id);
        builder.setSmallIcon(R.drawable.ic_newboedonotification32323)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setShowWhen(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

        //notificationManager.notify(new Random().nextInt(),builder.build());
    }


}
