package com.dirtyunicorns.duupdater.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by mazwoz on 7/5/16.
 */
public class File implements Parcelable, Serializable {
    private String FileName;
    private String FileSize;
    private String FileMD5;
    private String FildLink;
    private boolean FileDirect;

    protected File(Parcel in) {
        FileName = in.readString();
        FileSize = in.readString();
        FileMD5 = in.readString();
        FildLink = in.readString();
        FileDirect = in.readByte() != 0;
    }

    public static final Creator<File> CREATOR = new Creator<File>() {
        @Override
        public File createFromParcel(Parcel in) {
            return new File(in);
        }

        @Override
        public File[] newArray(int size) {
            return new File[size];
        }
    };

    public void SetFileName(String value) {
        FileName = value;
    }

    public void SetFileSize(String value) {
        FileSize = value;
    }

    public void SetFileLink(String value) {
        FildLink = value;
    }

    public void SetFileMD5(String value) {
        FileMD5 = value;
    }

    public void SetFileDirect(boolean value) {
        FileDirect = value;
    }

    public String GetFileName() {
        return FileName;
    }

    public String GetFileSize() {
        return FileSize;
    }

    public String GetFileMD5() {
        return FileMD5;
    }

    public String GetFileLink() {
        return FildLink;
    }

    public boolean IsFileDirect() {
        return FileDirect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(FileName);
        dest.writeString(FileSize);
        dest.writeString(FileMD5);
        dest.writeString(FildLink);
        dest.writeByte((byte) (FileDirect ? 1 : 0));
    }
}
