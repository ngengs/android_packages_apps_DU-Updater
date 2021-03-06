package com.dirtyunicorns.duupdater.objects;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by mazwoz on 10/12/16.
 */

public class CurrentVersion extends Update implements Parcelable{

    public void GetInfo() {
        String[] buildInfo = GetProp().split("_");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmm");
        try {
            buildDate = dateFormat.parse(buildInfo[3].split("\\.")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        androidVersion = buildInfo[2];
        buildType = "Hyperunicorns";
        isHyperunicorns = true;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public CurrentVersion createFromParcel(Parcel in) {
            return new CurrentVersion();
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}