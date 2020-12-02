package com.proseek.mdbsaudio;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public  static String getConnectivityStringStatus(Context context){
        String status=null;
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activnetwrk=connectivityManager.getActiveNetworkInfo();
        if(activnetwrk!=null)
        {
            if(activnetwrk.getType()==ConnectivityManager.TYPE_WIFI)
            {
                status="Wifi is enabled";
                return status;
            }
            else if(activnetwrk.getType()==ConnectivityManager.TYPE_MOBILE)
            {
                status="Mobile data is enabled";
                return status;
            }

        }
        else
        {
            status="no internet connection";
            return status;
        }
        return  status;
    }



}

