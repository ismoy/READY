package cl.tofcompany.ready.Notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import cl.tofcompany.ready.Controllers.HomeActivity;
import cl.tofcompany.ready.R;

public class WorkManagerNotify extends Worker {

    public WorkManagerNotify(@NonNull Context context , @NonNull WorkerParameters workerParams) {
        super(context , workerParams);
    }

    public static void GuardarNotify(long duracion , Data data , String tag) {
        OneTimeWorkRequest notify = new OneTimeWorkRequest.Builder(WorkManagerNotify.class)
                .setInitialDelay(duracion , TimeUnit.MILLISECONDS).addTag(tag)
                .setInputData(data).build();
        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(notify);
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("New Message");
        String titleDetail = getInputData().getString("titleDetail");
        int id = (int) getInputData().getLong("idnotify" , 0);

        Ready(title , titleDetail);

        return Result.success();
    }

    private void Ready(String t , String d) {
        String id = "message";
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext() , id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id , "new" , NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("Notificacion FCM");
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        Intent intent = new Intent(getApplicationContext() , HomeActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext() , 0 , intent , PendingIntent.FLAG_ONE_SHOT);

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setTicker("Nueva notifications")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(d)
                .setSmallIcon(R.drawable.grupo)
                .setContentIntent(pendingIntent)
                .setContentInfo("new");

        Random random = new Random();
        int idNotify = random.nextInt(200);
        assert nm != null;
        nm.notify(idNotify , builder.build());
    }
}
