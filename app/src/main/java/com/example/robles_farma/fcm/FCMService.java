package com.example.robles_farma.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.robles_farma.CargaActivity;
import com.example.robles_farma.MainActivity;
import com.example.robles_farma.R;
import com.example.robles_farma.sharedpreferences.FirebaseTokenManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

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
        FirebaseTokenManager.setFirebaseToken(getApplicationContext(), token, deviceName);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "From: " + message.getFrom());

        // 1. OBTENER LOS DATOS (Payload)
        // Están en message.getData(), NO en message.getNotification()
        Map<String, String> data = message.getData();

        // 2. OBTENER LA NOTIFICACIÓN VISUAL (Si existe)
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            Log.e("FCM MESSAGE title", title);
            Log.e("FCM MESSAGE body", body);
            Log.e("FCM MESSAGE data", data.toString());

            // Pasamos título, cuerpo Y LOS DATOS a la función
            sendNotification(title, body, data);
        }
        else if (data.size() > 0) {
            sendNotification("Nueva notificación", "Tienes un mensaje nuevo", data);
        }
    }

    private void sendNotification(String messageTitle, String messageBody, Map<String, String> data) {
        // CLAVE 1: Apuntar DIRECTO a MainActivity para evitar que el Splash (CargaActivity) se coma los datos.
        Intent intent = new Intent(this, MainActivity.class);

        // CLAVE 2: Limpiar flags para evitar duplicidad de actividades
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // CLAVE 3: Copiar los datos del Map de Firebase al Intent de Android
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
                Log.d(TAG, "Copiando al Intent -> Key: " + entry.getKey() + " Value: " + entry.getValue());
            }
        }

        // CLAVE 4: Usar un requestCode ÚNICO.
        // Si usas '0' siempre, Android reutiliza el intent anterior (que quizás no tenía datos).
        // Al usar currentTimeMillis, forzamos uno nuevo.
        int uniqueRequestCode = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                uniqueRequestCode, // <--- ÚNICO
                intent,
                // UPDATE_CURRENT: Si ya existe, actualiza sus extras. IMMUTABLE: Requisito Android 12+.
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = "fcm_default_channel";
        String channelName = "Notificaciones Generales";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.citas_salud_logo) // Asegúrate de que este icono existe
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(uniqueRequestCode, notificationBuilder.build());
    }
}