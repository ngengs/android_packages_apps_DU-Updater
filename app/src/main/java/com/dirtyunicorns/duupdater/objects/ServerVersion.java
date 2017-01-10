package com.dirtyunicorns.duupdater.objects;

import java.util.Date;

/**
 * Created by mazwoz on 10/12/16.
 */

public class ServerVersion extends Update {

    private String link;

    public void setLink(String link) {
        this.link = link;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    public String getLink() {
        return link;
    }
}
