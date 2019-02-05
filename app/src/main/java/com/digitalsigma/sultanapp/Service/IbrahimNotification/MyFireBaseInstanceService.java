package com.digitalsigma.sultanapp.Service.IbrahimNotification;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.digitalsigma.sultanapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d("FCMService", s);
        //save your token in sharedpref  (the token is String s )
        FirebaseMessaging.getInstance().subscribeToTopic(s);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        sharedpref(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            //get note title
            String title = remoteMessage.getData().get("title");
            //get note body
            String body = remoteMessage.getData().get("body");

            //display the note
            showNotification(title, body);

        }
    }

    private void showNotification(String title, String body) {
        new CustomNotification(getApplicationContext()).startNewNotification(title, body, R.drawable.ic_notifications);
    }

    private void sharedpref(String token){
        SharedPreferences.Editor editor = getSharedPreferences("ibrahimNote", MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.putInt("deliver", 0);
        editor.apply();
    }
}