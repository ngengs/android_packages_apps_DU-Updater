package com.dirtyunicorns.duupdater.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dirtyunicorns.duupdater.R;
import com.dirtyunicorns.duupdater.objects.CurrentVersion;
import com.dirtyunicorns.duupdater.objects.ServerVersion;
import com.dirtyunicorns.duupdater.utils.Utils;

import java.util.ArrayList;

import static com.dirtyunicorns.duupdater.utils.Utils.getServerVersions;

/**
 * Created by mazwoz on 10/12/16.
 */

public class CheckService extends Service {

    private static final String STARTTEXT = "STARTDOWNLOAD";
    private ArrayList<ServerVersion> serverVersions;
    private NotificationManager mNotifyManager;
    private CurrentVersion currentVersion;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (intent.getAction() != null && intent.getAction().equals(STARTTEXT)) {
            mNotifyManager.cancel(00);
            return START_NOT_STICKY;
        } else {
            currentVersion = new CurrentVersion();
            currentVersion.GetInfo();
            if (Utils.isOnline(this)) {
                serverVersions = getServerVersions(getBuildString());
                ParseBuilds();
            }
            return START_NOT_STICKY;
        }
    }

    private String getBuildString() {
        if (currentVersion.isHyperunicorns()) {
            return "Hyperunicorns";
        } else {
            return null;
        }
    }

    private void ParseBuilds() {
        for (ServerVersion serverVersion : serverVersions) {
            if (currentVersion.getBuildDate().before(serverVersion.getBuildDate())) {
                UpdateNotification(serverVersion.getLink());
                break;
            }
        }
    }

    private void UpdateNotification(String link) {
        Notification.Builder mBuilder = new Notification.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
        mBuilder.setContentTitle(getString(R.string.dialog_title));
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW);
        downloadIntent.setData(Uri.parse(link));
        PendingIntent startDownload = PendingIntent.getActivity(this, 42, downloadIntent, PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(startDownload);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.setAutoCancel(true);

        mBuilder.setContentText(getString(R.string.build_date_text));
        mNotifyManager.notify(00, mBuilder.build());
    }
}

