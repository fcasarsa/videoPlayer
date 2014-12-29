package it.solari.videoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

// this event is received at boot

public class BootEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Bundle extras = intent.getExtras();
        Log.d(this.getClass().getCanonicalName(), "Boot event");
        Intent dialogIntent = new Intent(context, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }
}
