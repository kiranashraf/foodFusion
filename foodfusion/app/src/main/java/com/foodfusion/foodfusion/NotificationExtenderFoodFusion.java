package com.foodfusion.foodfusion;

import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import java.math.BigInteger;

/**
 * Created by Rameez on 3/20/2018.
 */

public class NotificationExtenderFoodFusion  extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        // Read Properties from result
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Yellow on Android 5.0+ devices.
                return builder.setColor(new BigInteger("fff6b132",16).intValue()).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ff_icon_large_notification));

//                return builder.setColor(0xff000000+Integer.parseInt("#F7B032",16));
//                return builder.setColor(getResources().getColor(R.color.appColor));
//                return builder.setColor(Color.parseColor("#FF717d13"));
                //return builder.setColor(0xfff6b132);
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);

        // Return true to stop the notification from displaying
        return true;
    }
}