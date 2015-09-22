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

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.google.android.gms.analytics.Tracker;
import com.soyoyoo.animalsinspaceapp.AnimalsInSpaceApp.TrackerName;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class DeepLinkActivity extends Activity {
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();

		String uri = intent.toUri(0);
		Log.v("DeepLinkActivity", "URI is: " + uri);

		String referrerString = intent.getStringExtra("referrer");
		Log.v("DeepLinkActivity", "referrer from Extra is: " + referrerString);

		Uri intentData = intent.getData();
		Log.v("DeepLinkActivity", "intentData is: " + intentData.toString());
		String path = intentData.getPath().toString();
		Log.v("DeepLinkActivity", "Intent path: " + path);
		String campaignData = intentData.getQueryParameter("utm_campaign");
		Log.v("DeepLinkActivity", "campaignData is: " + campaignData);
		String referrerValue = intentData.getQueryParameter("referrer");
		Log.v("DeepLinkActivity", "referrer from URI param is: "
				+ referrerValue);
		Tracker t = ((AnimalsInSpaceApp) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		EventBuilder hitBuilder = new HitBuilders.EventBuilder("interaction", "deeplink");
		if (uri != null)
			hitBuilder.setCustomDimension(2, uri);
			hitBuilder.setLabel(path);
		if (referrerValue != null) {
			hitBuilder.setCustomDimension(3, referrerValue);
			AdWordsConversionReporter.registerReferrer(
					this.getApplicationContext(), this.getIntent().getData());

		}
		// Animals In Space Deeplink (unique)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"zGTJCP6m410QwImyzgM", "0.00", false);
		// Animals In Space Deeplink (repeat)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"SMnSCOva6V0QwImyzgM", "0.00", true);
		
		if (campaignData != null) {
			t.send(hitBuilder.setCampaignParamsFromUrl(intentData.toString())
					.build());
		} else {
			t.send(hitBuilder.build());
		}

		intent = new Intent(this, SubActivity.class);
		intent.putExtra("path", path);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

}
