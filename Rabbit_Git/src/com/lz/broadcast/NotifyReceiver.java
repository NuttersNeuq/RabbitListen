package com.lz.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotifyReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		//开启NotifyService
		context.startService(new Intent("nut.service.NotifyService"));
	}

}
