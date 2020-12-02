package com.proseek.mdbsaudio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Myceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
          MainActivity mainActivit=new MainActivity();
       mainActivit.getConnectivityStringStatus((MainActivity) context);
//          if(status.isEmpty())
//          {
//              status="Please check your Internect connection";
//          }
//        Toast.makeText(context, "Connection:"+status, Toast.LENGTH_SHORT).show();
    }
}
