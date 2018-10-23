/*
 * 		Animals In Space App
 * 		by JeeWook Kim
 * 
 * 		Samples are offered on as-is basis, and designed only to provide you with certain examples of how such code samples could be utilized.
 *      By implementing any of Samples, you agree to solely assume all responsibility for any consequences that arise from such implementation.
 *
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
		// get the referrer from the intent
		String referrerString = intent.getStringExtra("referrer");
		Log.v("DeepLinkActivity", "referrer from Extra is: " + referrerString);

		Uri intentData = intent.getData();
		Log.v("DeepLinkActivity", "intentData is: " + intentData.toString());
		// get the path from the intent
		String path = intentData.getPath().toString();
		Log.v("DeepLinkActivity", "Intent path: " + path);
		// get the utm parameter from query string
		String campaignData = intentData.getQueryParameter("utm_campaign");
		Log.v("DeepLinkActivity", "campaignData is: " + campaignData);
		String referrerValue = intentData.getQueryParameter("referrer");
		Log.v("DeepLinkActivity", "referrer from URI param is: "
				+ referrerValue);
		// get Google Analytics tracker instance
		Tracker t = ((AnimalsInSpaceApp) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		// create an event builder to send an event to Google Analytics
		EventBuilder hitBuilder = new HitBuilders.EventBuilder("interaction", "deeplink");
		if (uri != null)
			hitBuilder.setCustomDimension(2, uri);  // set custom dimension
			hitBuilder.setLabel(path); // set label for event tracking
		if (referrerValue != null) {
			hitBuilder.setCustomDimension(3, referrerValue);
			// register the installation referrer with AdWords Conversion Tracking SDK
			AdWordsConversionReporter.registerReferrer(
					this.getApplicationContext(), this.getIntent().getData());

		}
		// Conversion : Animals In Space Deeplink (unique)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"zGTJCP6m410QwImyzgM", "0.00", false);
		// Conversion: Animals In Space Deeplink (repeat)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"SMnSCOva6V0QwImyzgM", "0.00", true);
		
		if (campaignData != null) {
			t.send(hitBuilder.setCampaignParamsFromUrl(intentData.toString())
					.build()); // send an event with campaign tracking data to Google Analytics
		} else {
			t.send(hitBuilder.build()); // send an event to Google Analytics
		}

		intent = new Intent(this, SubActivity.class);
		intent.putExtra("path", path);
		startActivity(intent); // start SubActivity
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

}
