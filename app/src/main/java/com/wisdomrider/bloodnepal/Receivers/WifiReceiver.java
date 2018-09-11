package com.wisdomrider.bloodnepal.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wisdomrider.bloodnepal.Services.Background;

/*
CREated by avi(Wisdomrider)
on 9/10/2018
*/
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Background.isConnected != 1) {
            context.startService(new Intent(context, Background.class));
        }
    }
}
