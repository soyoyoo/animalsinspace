/*
Copyright 2015 Google Inc. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.soyoyoo.animalsinspaceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.ads.conversiontracking.InstallReceiver;
import com.soyoyoo.animalsinspaceapp.AnimalsInSpaceApp.TrackerName;

public class CustomReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String uri = intent.toUri(0);
		Log.w("CustomReceiver", "URI is: " + uri);
		// get referrer string from an installation intent
		String referrerString = intent.getStringExtra("referrer");
		Log.w("CustomReceiver", "Referrer is: " + referrerString);
		// get an instance of Google Analytics tracker
		Tracker t = ((AnimalsInSpaceApp) context.getApplicationContext())
				.getTracker(TrackerName.APP_TRACKER);
		// create an EventBuilder to send an event to GA
		EventBuilder hitBuilder = new HitBuilders.EventBuilder("interaction", "installation");
		// set custom dimensions
		if (referrerString!=null) hitBuilder.setCustomDimension(3, referrerString);
		if (uri!=null) {
			hitBuilder.setCustomDimension(2, uri);
			hitBuilder.setLabel(uri);  // set label for event tracking
		}
		t.send(hitBuilder.build()); // send an event to GA
		
		// Pass the intent to AdWords Conversion Tracking SDK receiver
		new InstallReceiver().onReceive(context, intent);
		// Pass the intent to the Google Analytics campaign tracking receiver
		new CampaignTrackingReceiver().onReceive(context, intent);
	}
}
