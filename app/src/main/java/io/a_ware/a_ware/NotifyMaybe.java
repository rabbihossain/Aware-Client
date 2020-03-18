package io.a_ware.a_ware;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;

public class NotifyMaybe extends Worker {

    public NotifyMaybe(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        String notifyContent = getInputData().getString("notifycontent");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "aware")
                .setSmallIcon(R.drawable.permissions_sheild)
                .setContentTitle("Aware Permission Manager")
                .setContentText(notifyContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Random random = new Random();
        int randomNumber = random.nextInt(10000 - 1000) + 99;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getApplicationContext());
        notificationManager.notify(randomNumber, builder.build());



        return Result.success();
    }
}
