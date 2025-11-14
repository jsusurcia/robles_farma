package com.example.robles_farma.fcm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.robles_farma.sharedpreferences.FirebaseTokenManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService{

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e("FCM TOKEN", token);

        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;

        String deviceName = manufacturer + " " + model;

        if (deviceName.length() > 0) {
            deviceName = deviceName.substring(0, 1).toUpperCase() + deviceName.substring(1);
        }

        Log.e("DEVICE NAME", deviceName);

        // almacenar el token
        FirebaseTokenManager.setFirebaseToken(getApplicationContext(), token, deviceName);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.e("FCM MESSAGE title", message.getNotification().getTitle());
        Log.e("FCM MESSAGE body", message.getNotification().getBody());
    }
}
