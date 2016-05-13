/*
* Copyright (C) 2015 Dirty Unicorns
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.dirtyunicorns.duupdater.Services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.dirtyunicorns.duupdater.Utils.FileObject;
import com.dirtyunicorns.duupdater.Utils.MainUtils;
import java.util.Date;

import com.dirtyunicorns.duupdater.R;

/**
 * Created by mazwoz on 03.03.15.
 */
@SuppressLint({ "NewApi", "DefaultLocale" }) public class UpdateCheck extends IntentService{

    public UpdateCheck() {
        super("UpdateCheck");
    }

    @SuppressLint("DefaultLocale")
	@Override
    protected void onHandleIntent(Intent workIntent) {
        String buildType = BuildType().toLowerCase();
        Date[] potUpdates;
        FileObject[] foPotUpdates;
        int i = 0;

        if (!buildType.equals("unofficial")) {

            FileObject[] potentialUpdates = MainUtils.getFiles(buildType.substring(0,1).toUpperCase() + buildType.substring(1));

            if (potentialUpdates != null) {
                potUpdates = new Date[potentialUpdates.length];
                foPotUpdates = new FileObject[potentialUpdates.length];
                for (FileObject update : potentialUpdates) {
                    Date buildDate = MainUtils.StringtoDate(BuildDate());
                    Date updateDate = MainUtils.StringtoDate(GetDateFromUpdate(update.filename));

                    if (MainUtils.CompareDates(buildDate,updateDate)) {
                        potUpdates[i] = updateDate;
                        foPotUpdates[i] = update;
                        i++;
                    }
                }
                if (foPotUpdates.length > 0) {
	                Intent downloadIntent = new Intent(getApplication(), DownloadIntent.class);
	                downloadIntent.putExtra("fileObject", foPotUpdates[i-1]);
	                PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, downloadIntent, 0);
	                Notification mBuilder = new Notification.Builder(getApplication())
	                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
	                        .setContentTitle(getString(R.string.update_available_title))
	                        .setContentText(getString(R.string.update_download_prompt_title))
	                        .setAutoCancel(true)
	                        .addAction(android.R.drawable.stat_sys_download, getString(R.string.update_download_button_title), pendingIntent).build();
	                mBuilder.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
	                NotificationManager mNotificationManaager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	                mNotificationManaager.notify(0, mBuilder);
                }
            }

        }
    }

    private String GetDateFromUpdate(String fileName) {
        String[] splitUnders = fileName.split("_");
        String[] splitDash = splitUnders[3].split("-");
        return splitDash[0];
    }


    private String BuildType() {
        String buildType = Build.VERSION.CODENAME;
        return  buildType;
    }

    private String BuildDate() {
        String buildDate = Build.DISPLAY;
        buildDate = buildDate.split("\\.")[4];
        return buildDate;
    }
}
