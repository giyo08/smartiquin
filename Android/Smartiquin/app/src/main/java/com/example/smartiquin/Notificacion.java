package com.example.smartiquin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import static android.app.Notification.CATEGORY_ALARM;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Notificacion {

    public Notificacion(){

    }

    public void generarNuevaNotificacion(String titulo, String mensaje, Context context){

        NotificationManager manager =(NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent, 0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icono);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(CATEGORY_ALARM)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.icono)
                .setOngoing(false)
                .setColor(144)
                .setVibrate(new long[] {100, 250, 100, 500});

        manager.notify(getRandom(), builder.build());

    }

    private int getRandom(){
        Random rand = new Random();
        return rand.nextInt(1000 - 0 + 1);
    }
}
