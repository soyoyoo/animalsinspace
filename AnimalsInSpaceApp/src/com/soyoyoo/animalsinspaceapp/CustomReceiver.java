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
		
		// Pass the intent to AdWords SDK receivers.
		new InstallReceiver().onReceive(context, intent);
		// When you're done, pass the intent to the Google Analytics receiver.
		new CampaignTrackingReceiver().onReceive(context, intent);
	}
}
