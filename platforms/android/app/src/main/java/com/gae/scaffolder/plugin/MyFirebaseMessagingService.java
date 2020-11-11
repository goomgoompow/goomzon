package com.gae.scaffolder.plugin;

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
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pentaon.vzon.R;
import com.pentaon.vzon.utils.SystemUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

  private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

  @Override
  public void onNewToken(String s) {
    super.onNewToken(s);
  }

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    // TODO(developer): Handle FCM messages here.
    // If the application is in the foreground handle both data and notification messages here.
    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
    Log.d(TAG, "==> MyFirebaseMessagingService onMessageReceived");

    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
      Log.d(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
    }

    Map<String, Object> data = new HashMap<String, Object>();
    boolean isTapped = FCMPluginActivity.IS_TAPPED;
    Log.d(TAG, "onMessageReceived: isTapped = " + isTapped);
    data.put("wasTapped", isTapped);
    for (String key : remoteMessage.getData().keySet()) {
      Object value = remoteMessage.getData().get(key);
      Log.d(TAG, "\tKey: " + key + " Value: " + value);
      data.put(key, value);
    }
    Log.d(TAG, "\tNotification Data: " + data.toString());
    boolean isInBackground = SystemUtil.isAppInBackground(this);
    Log.d(TAG, "onMessageReceived: isInBackground = " + isInBackground);
    //------------------------------------------------
    // FCMPluginActivity의 onCrteate()에서 sendPushPayload() 메서드 호출하므로 중복이서서 제거
    //------------------------------------------------
//        if(!isInBackground) FCMPlugin.sendPushPayload( data );

    String title = data.get("title") == null ? "@Null" : data.get("title").toString();
    String message = data.get("body") == null ? "@Null" : data.get("body").toString();

    sendNotification(title, message, data);
  }
  // [END receive_message]

  /**
   * Create and show a simple notification containing the received FCM message.
   *
   * @param messageBody FCM message body received.
   */
  private void sendNotification(String title, String messageBody, Map<String, Object> data) {
    Intent intent = new Intent(this, FCMPluginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    for (String key : data.keySet()) {
      intent.putExtra(key, data.get(key).toString());
    }
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        PendingIntent.FLAG_ONE_SHOT);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
        .setPriority(NotificationCompat.PRIORITY_MAX);

    int notificationIcon = (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) ? R.mipmap.icon_outlined
        : getApplicationInfo().icon;

    notificationBuilder.setSmallIcon(notificationIcon)
        .setLights(Color.RED, 700, 3300)
        .setDefaults(
            Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
        .setContentTitle(title)
        .setContentText(messageBody)
        .setAutoCancel(true)
//        .setSound(defaultSoundUri)
//        .setLights(0x8288c2, 700, 3300)
        .setContentIntent(pendingIntent)
        .setOnlyAlertOnce(false)
    ;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "Hello";// The user-visible name of the channel.
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel mChannel = new NotificationChannel("CHANNEL_ID", name, importance);
      mChannel.setShowBadge(true);
      mChannel.enableLights(true);
      mChannel.setLightColor(0x8288c2);

      Log.d(TAG, "sendNotification: mChannel.shouldShowLight() "+mChannel.shouldShowLights());

      notificationManager.createNotificationChannel(mChannel);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX)
          .setCategory(Notification.CATEGORY_MESSAGE)
          .setVisibility(Notification.VISIBILITY_PUBLIC);
    }
    int currentMills = (int) System.currentTimeMillis();
    notificationManager.notify(currentMills /* ID of notification */, notificationBuilder.build());
  }
}
