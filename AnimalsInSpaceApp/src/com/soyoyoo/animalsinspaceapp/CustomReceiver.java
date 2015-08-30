/**
 * 		Animals-In-Space Demo App
 * 		by JeeWook Kim
 * 		THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND
 */
package com.soyoyoo.animalsinspaceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.ads.conversiontracking.InstallReceiver;
import com.soyoyoo.animalsinspaceapp.AnimalsInSpaceApp.TrackerName;

public class CustomReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String uri = intent.toUri(0);
		Log.w("CustomReceiver", "URI is: " + uri);
		
		String referrerString = intent.getStringExtra("referrer");
		Log.w("CustomReceiver", "Referrer is: " + referrerString);
		
		Tracker t = ((AnimalsInSpaceApp) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);
		ScreenViewBuilder hitBuilder = new HitBuilders.ScreenViewBuilder();
		
		if (referrerString!=null) hitBuilder.setCustomDimension(3, referrerString);
		if (uri!=null) hitBuilder.setCustomDimension(2, uri);
		t.setScreenName("DeepLinkActivity");
		t.send(hitBuilder.build());
		
		// Pass the intent to AW SDK receivers.
		new InstallReceiver().onReceive(context, intent);
		// When you're done, pass the intent to the Google Analytics receiver.
		new CampaignTrackingReceiver().onReceive(context, intent);
	}
}
