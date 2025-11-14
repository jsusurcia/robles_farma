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

import com.example.robles_farma.MainActivity;
import com.example.robles_farma.R;
import com.example.robles_farma.sharedpreferences.FirebaseTokenManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

    /**
     * AQUÍ OCURRE LA MAGIA.
     * Este metodo se llama cuando se recibe un mensaje MIENTRAS LA APP ESTÁ EN PRIMER PLANO.
     * Si la app está en segundo plano o cerrada, FCM (en Android)
     * automáticamente maneja la notificación si envías un "notification payload".
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Imprimimos en consola (como ya hacías)
        Log.d(TAG, "From: " + message.getFrom());

        // El backend parece estar enviando un "notification payload".
        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            Log.e("FCM MESSAGE title", title);
            Log.e("FCM MESSAGE body", body);

            // ¡Llamamos a nuestra nueva función para MOSTRAR la notificación!
            sendNotification(title, body);
        }

        // Si también envías un "data payload" (datos adicionales)
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());
            // Aquí podrías procesar datos extra si los tuvieras
        }
    }

    /**
     * NUEVA FUNCIÓN: Construye y muestra la notificación visual en el dispositivo.
     */
    private void sendNotification(String messageTitle, String messageBody) {
        // 1. Define qué pasa cuando el usuario TOCA la notificación
        // (En este caso, abrir la MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // 2. Define un ID para el canal de notificación (requerido desde Android 8.0+)
        String channelId = "fcm_default_channel";
        String channelName = "Notificaciones Generales";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // 3. Construye la notificación
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        // ¡IMPORTANTE! Debes tener un icono en res/drawable
                        // Usa R.mipmap.ic_launcher si no tienes uno para probar
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true) // La notificación se cierra al tocarla
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        // 4. Obtiene el servicio de Notificaciones
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 5. Crea el Canal de Notificación (solo para Android 8.0 / API 26 y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // 6. Muestra la notificación
        // Usamos un ID único (basado en la hora) para que se muestren múltiples notificaciones
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}