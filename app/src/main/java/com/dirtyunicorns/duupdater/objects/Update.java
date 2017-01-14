package com.dirtyunicorns.duupdater.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by mazwoz on 10/12/16.
 */

public class Update {

    protected String androidVersion;
    protected String buildType;
    protected Date buildDate;
    protected boolean isHyperunicorns;

    protected String GetProp() {
        String retProp = null;

        try {
            Process p = new ProcessBuilder("/system/bin/getprop", "ro.du.version").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                retProp = line;
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retProp;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public boolean isHyperunicorns() {
        return isHyperunicorns;
    }
}
