package com.dirtyunicorns.duupdater.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcel;
import android.os.SystemClock;

import com.dirtyunicorns.duupdater.objects.ServerVersion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mazwoz on 7/5/16.
 */
public class Utils extends Vars {

    protected static ConnectivityManager connectivityManager;
    protected static boolean connected = false;
    private static ArrayList<File> files;
    private static ArrayList<ServerVersion> serverVersions;

    public static String ConvertSpeed(double currentSpeed) {
        DecimalFormat df = new DecimalFormat("0.0");
        if (currentSpeed > 1024) {
            return df.format(currentSpeed / 1024) + "MB/s";
        } else if (currentSpeed < 0) {
            return "Stalled download, please wait";
        } else {
            df = new DecimalFormat("0");
            return df.format(currentSpeed) + "KB/s";
        }
    }

    public static ArrayList<File> getFiles(final String dir, final boolean isDeviceFiles) {
        files = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path;
                if (isDeviceFiles) {
                    device = Build.PRODUCT;
                    path = device + "/" + dir;
                } else {
                    path = "device=&folder=" + dir;
                }

                try {
                    URI uri;
                    if (isDeviceFiles) {
                        uri = new URI(link_scheme_hyperunicorns, null, link_host_hyperunicorns, link_port_hyperunicorns, link_path_hyperunicorns + path, null, null);
                    } else {
                        uri = new URI(link_scheme, null, link_host, link_port, link_path, path, null);
                    }
                    JSONObject json;
                    if (isDeviceFiles) json = jsonParser.getJSONFromUrlHttp(uri.toASCIIString());
                    else json = jsonParser.getJSONFromUrlHttps(uri.toASCIIString());
                    try {
                        if (json != null) {
                            JSONArray folders = json.getJSONArray(TAG_MASTER);
                            dirs = new String[folders.length()];
                            for (int i = 0; i < folders.length(); i++) {
                                JSONObject d = folders.getJSONObject(i);
                                File f = new File(Parcel.obtain());
                                f.SetFileName(d.getString("filename"));
                                f.SetFileSize(d.getString("filesize"));
                                f.SetFileLink(d.getString("downloads"));
                                f.SetFileMD5(d.getString("md5"));
                                if (isDeviceFiles) f.SetFileDirect(d.getBoolean("direct"));
                                else f.SetFileDirect(true);
                                files.add(f);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        if (!isDeviceFiles) Collections.reverse(files);
        return files;
    }

    public static ArrayList<ServerVersion> getServerVersions(final String dir) {
        if (dir == null) throw new AssertionError();
        serverVersions = new ArrayList<>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                JSONParser jsonParser = new JSONParser();
                String path;
                device = Build.PRODUCT;
                path = device + "/" + dir;

                try {
                    URI uri;
                    uri = new URI(link_scheme_hyperunicorns, null, link_host_hyperunicorns, link_port_hyperunicorns, link_path_hyperunicorns + path, null, null);

                    JSONObject json = jsonParser.getJSONFromUrlHttp(uri.toASCIIString());
                    try {
                        if (json != null) {
                            JSONArray folders = json.getJSONArray(TAG_MASTER);
                            dirs = new String[folders.length()];
                            for (int i = 0; i < folders.length(); i++) {

                                JSONObject d = folders.getJSONObject(i);
                                ServerVersion serverVersion = new ServerVersion();
                                String[] buildInfo = d.getString("filename").replace(".zip", "").split("_");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String dateUpload = d.getString("date");
                                try {
                                    serverVersion.setBuildDate(dateFormat.parse(dateUpload));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                serverVersion.setAndroidVersion(buildInfo[2]);
                                serverVersion.setBuildType("Hyperunicorns");
                                serverVersion.setLink(d.getString("downloads"));
                                serverVersions.add(serverVersion);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        return serverVersions;
    }

    public static boolean isOnline(Context ctx) {
        try {
            connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return connected;
    }
}
