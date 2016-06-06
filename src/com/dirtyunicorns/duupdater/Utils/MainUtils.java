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

package com.dirtyunicorns.duupdater.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dirtyunicorns.duupdater.R;

/**
 * Created by mazwoz on 12/18/14.
 */
@SuppressLint("SimpleDateFormat")
public class MainUtils {



    private static String[] dirs;
    private static FileObject[] files;

    private static final String TAG_MASTER = "dev_info";
    private static String URL_PATH;
    private static String URL_SEPARATOR_DEVICE;
    private static String URL_SEPARATOR_FOLDER;
    private static String URL_API_FILE;

    private static ConnectivityManager connectivityManager;
    private static boolean connected = false;
    private static boolean DNSGood = false;

    public static String[] getDirs(Context ctx) {
        URL_PATH = ctx.getResources().getString(R.string.conf_server_url_main)+ctx.getResources().getString(R.string.conf_server_url_path);
        URL_SEPARATOR_DEVICE = ctx.getResources().getString(R.string.conf_server_url_separator_device);
        URL_API_FILE = ctx.getResources().getString(R.string.conf_server_url_api_file);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                String path = URL_PATH + URL_SEPARATOR_DEVICE + Build.UPDATER + URL_API_FILE;

                JSONObject json = jsonParser.getJSONFromUrl(path);
                JSONArray folders = null;
                try{
                    if (json != null) {
                        folders = json.getJSONArray(TAG_MASTER);
                        dirs = new String[folders.length()];
                        for (int i = 0; i < folders.length(); i++) {
                            JSONObject d = folders.getJSONObject(i);
                            String id = d.getString("filename");
                            dirs[i] = id;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        return dirs;
    }

    public static FileObject[] getFiles(Context ctx, final String dir) {
        URL_PATH = ctx.getResources().getString(R.string.conf_server_url_main)+ctx.getResources().getString(R.string.conf_server_url_path);
        URL_SEPARATOR_DEVICE = ctx.getResources().getString(R.string.conf_server_url_separator_device);
        URL_SEPARATOR_FOLDER = ctx.getResources().getString(R.string.conf_server_url_separator_folder);
        URL_API_FILE = ctx.getResources().getString(R.string.conf_server_url_api_file);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                JSONParser jsonParser = new JSONParser();

                String path = URL_PATH + URL_SEPARATOR_DEVICE + Build.UPDATER + URL_SEPARATOR_FOLDER + dir + URL_API_FILE;
                JSONObject json = jsonParser.getJSONFromUrl(path);
                JSONArray folders = null;
                try{
                    if (json != null) {
                        folders = json.getJSONArray(TAG_MASTER);
                        files = new FileObject[folders.length()];
                        for (int i = 0; i < folders.length(); i++) {
                            JSONObject d = folders.getJSONObject(i);
                            FileObject file_temp = new FileObject();
                            file_temp.filename = d.getString("filename").replace(".zip","");
                            file_temp.downloads = d.getString("downloads");
                            file_temp.direct = d.getBoolean("direct");
                            files[i] = file_temp;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();
        while (t.isAlive()) {
            SystemClock.sleep(200);
        }
        return files;
    }
    
    public static boolean isOnline(Context ctx) {
        try {
            connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public static Date StringtoDate(String strDate) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(strDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean CompareDates(Date buildDate, Date updateDate) {
        if (buildDate.before(updateDate)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean CheckDNS(final Context ctx) {
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
                            try {
                                URL_PATH = ctx.getResources().getString(R.string.conf_server_url_main);
				URL url = new URL(URL_PATH);
			    	InetAddress address = InetAddress.getByName(url.getHost());
			    	String temp = address.toString();
			    	String IP = temp.substring(temp.indexOf("/")+1,temp.length());
			    	if (IP != null) {
			    		DNSGood = true;
			    	} else {
			    		Dialogs.BadDNS(ctx);
			    	}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			
		});
		t.start();
		
		while (t.isAlive()) {
			try {
				SystemClock.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		return DNSGood;
    }
}