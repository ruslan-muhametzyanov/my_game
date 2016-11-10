package com.gpro.flowergotchi.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GameReciever extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent intent2 = new Intent(context, GameIntentService.class);
        context.startService(intent2);
    }
}
