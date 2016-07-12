package com.example.usuario.alarmaincendiofinal;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        sendNotification(message);
    }
    private void sendNotification(String message) {
        Intent intent = new Intent(this, LlamadaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Intent intent = new Intent(Intent.ACTION_CALL);
        //intent.setData(Uri.parse("tel:"));
        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        //Setup notification
        //Sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Build notification
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alarma Incendio")
                .setTicker("Alarma Incendio")
                .setContentText(message)  //cambiar por mensaje que envie la placa, o setear uno por defecto
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000})
                .addAction(R.drawable.llamadaicono,"Llam. Num. Emergencia",pendingIntent)
                .setSound(sound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}

