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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.soyoyoo.animalsinspaceapp.R;
import com.google.ads.conversiontracking.AdWordsAutomatedUsageReporter;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.ads.conversiontracking.AdWordsRemarketingReporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.soyoyoo.animalsinspaceapp.AnimalsInSpaceApp.TrackerName;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.AppIndexApi.AppIndexingLink;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

public class MainActivity extends Activity {
	// App Indexing URLs
	// For detail info for App Indexing: https://developers.google.com/app-indexing/android/appActivity
	static final Uri APP_URI = Uri
			.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/");
	static final Uri WEB_URL = Uri
			.parse("http://http://glossy-depot-510.appspot.com/");
	static final Uri EMPTY_WEB_URL = Uri.parse("");
	private GoogleApiClient mClient;
	static protected String aid;
	static protected SparseArray<String> paths;

	public void onClick(View v) {

		Intent intent = null;
		Log.v("MainActivity", "v.getId() is: " + String.valueOf(v.getId()));
		String path = paths.get(v.getId());
		Log.v("MainActivity", "path = "+path);
		// report conversion to AdWords
		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"tSR_CIrIulcQwImyzgM", "5.00", true);

		intent = new Intent(this, SubActivity.class);
		if (path != null)
			intent.putExtra("path", path);
		startActivity(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		paths = new SparseArray<String>();
		paths.put(R.id.imageView1, "/cat");
		paths.put(R.id.imageView2, "/dog");
		paths.put(R.id.imageView3, "/hamster");
		paths.put(R.id.imageView4, "/koala");
		paths.put(R.id.button1, "/cat");
		paths.put(R.id.button2, "/dog");
		paths.put(R.id.button3, "/hamster");
		paths.put(R.id.button4, "/koala");
		mClient = new GoogleApiClient.Builder(this).addApi(
				AppIndex.APP_INDEX_API).build();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onResume() {
		super.onResume();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					aid = getIdThread();  // get advertising id
					if (aid !=null)
						Log.v("ADID", aid);
					else
						Log.v("ADID", "NA");
					finished(aid);
				} catch (Exception e) {
					e.printStackTrace();
					finished(null);
				}

			}
		});

		thread.start();

		// Enable automated usage reporting.
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(
				this.getApplicationContext(), "969704640");
		// Conversion: Animals In Space App open (unique)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"Nm1ZCIro7V0QwImyzgM", "0.00", false); // set the repeat parameter to false

		// Conversion: Animals In Space app open (repeat)
		// Google Android in-app conversion tracking snippet
		// Add this code to the event you'd like to track in your app

		AdWordsConversionReporter.reportWithConversionId(
				this.getApplicationContext(), "969704640",
				"tA7fCMXY6V0QwImyzgM", "0.00", true);

		Locale locale = Locale.getDefault();
		DateFormat df = new SimpleDateFormat("yyyyMMdd", locale);
		String sdt = df.format(new Date(System.currentTimeMillis()));
		// set custom parameters for re-marketing segment gathering 
		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("action_type", "page_view");
		params2.put("page", "home");
		params2.put("value", "1");
		params2.put("open_date", sdt);
		// send re-marketing parameters to AdWords
		AdWordsRemarketingReporter.reportWithConversionId(
				getApplicationContext(), "969704640", params2);

	}

	protected void finished(String aid) {
		// get a tracker for Google Analytics
		Tracker t = ((AnimalsInSpaceApp) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		// create a ScreenViewBuilder to send a screen view to Google Analytics
		ScreenViewBuilder hitBuilder = new HitBuilders.ScreenViewBuilder();
		// set custom dimensions
		if (aid !=null)
			hitBuilder.setCustomDimension(4, aid);
		else 
			hitBuilder.setCustomDimension(4, "NA");
		hitBuilder.setCustomDimension(1, "page_view");

		if (aid !=null)
			Log.v("ADID", aid);
		else
			Log.v("ADID", "NA");
		// set a custom metric
		hitBuilder.setCustomMetric(1, 1);

		// Set a screen name
		t.setScreenName("Home");

		// Send a screen view to Google Analytics
		t.send(hitBuilder.build());
	}

	protected void onStart() {
		super.onStart();
		// Connect your client
		mClient.connect();

		// Define the outlinks on the page for both app and web for deep-linking
		Uri appUri1 = Uri
				.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/cat/");
		Uri webUrl1 = Uri
				.parse("http://http://glossy-depot-510.appspot.com/cat/");
		Uri appUri2 = Uri
				.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/dog/");
		Uri webUrl2 = Uri
				.parse("http://http://glossy-depot-510.appspot.com/dog/");
		Uri appUri3 = Uri
				.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/hamster/");
		Uri webUrl3 = Uri
				.parse("http://http://glossy-depot-510.appspot.com/hamster/");
		Uri appUri4 = Uri
				.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/koala/");
		Uri webUrl4 = Uri
				.parse("http://http://glossy-depot-510.appspot.com/koala/");

		// Create App Indexing link objects
		AppIndexingLink item1 = new AppIndexingLink(appUri1, webUrl1,
				this.findViewById(R.id.button1));
		AppIndexingLink item2 = new AppIndexingLink(appUri2, webUrl2,
				this.findViewById(R.id.button2));
		AppIndexingLink item3 = new AppIndexingLink(appUri3, webUrl3,
				this.findViewById(R.id.button3));
		AppIndexingLink item4 = new AppIndexingLink(appUri4, webUrl4,
				this.findViewById(R.id.button4));

		// Create a list out of App Indexing link objects
		List<AppIndexingLink> outlinks = new ArrayList<AppIndexingLink>();
		outlinks.add(item1);
		outlinks.add(item2);
		outlinks.add(item3);
		outlinks.add(item4);

		final String TITLE = "Animals In Space Home";

		// Call the App Indexing API view method
		AppIndex.AppIndexApi.view(mClient, this, APP_URI, TITLE, WEB_URL,
				outlinks);
	}

	public void onStop() {
		super.onStop();
		// Call viewEnd() and disconnect the client
		AppIndex.AppIndexApi.viewEnd(mClient, this, APP_URI);
		mClient.disconnect();
	}

	public String getIdThread() {

		Info adInfo = null;
		try {
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this
					.getApplicationContext());

		} catch (IOException e) {
			e.printStackTrace();

		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GooglePlayServicesRepairableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!adInfo.isLimitAdTrackingEnabled())
			return adInfo.getId(); // return the advertising id if the user opted in
		else
			return null;

	}
	public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_menu_help).setTitle("Exit")
                .setMessage("Do you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    } 
}
