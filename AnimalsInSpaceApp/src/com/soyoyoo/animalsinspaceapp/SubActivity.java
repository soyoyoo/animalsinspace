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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.soyoyoo.animalsinspaceapp.R;
import com.soyoyoo.animalsinspaceapp.AnimalsInSpaceApp.TrackerName;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.ads.conversiontracking.AdWordsRemarketingReporter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.AppIndexApi.AppIndexingLink;
import com.google.android.gms.common.api.GoogleApiClient;

public class SubActivity extends Activity {
	private Uri APP_URI = Uri
			.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/cat/");
	private Uri WEB_URL = Uri
			.parse("http://http://glossy-depot-510.appspot.com/cat/");
	static final Uri EMPTY_WEB_URL = Uri.parse("");
	private GoogleApiClient mClient;
	private String path;
	private String animal;
	private String screen_name;
	private String prod_id;
	private String value;
	private int layout_id;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String uri = intent.toUri(0);
		Log.v("SubActivity", "onCreate() URI is: " + uri);
		path = (String) intent.getExtras().get("path");
		initialiseFromXml(path);
		mClient = new GoogleApiClient.Builder(this).addApi(
				AppIndex.APP_INDEX_API).build();
		setContentView(layout_id);
	}

	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String uri = intent.toUri(0);
		Log.v("SubActivity", "onResume() URI is: " + uri);
		path = (String) intent.getExtras().get("path");
		initialiseFromXml(path);
		
		Locale locale = Locale.getDefault();
		DateFormat df = new SimpleDateFormat("yyyyMMdd", locale);
		String sdt = df.format(new Date(System.currentTimeMillis()));
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("action_type", "land");
		params.put("product_category", animal);
		params.put("value", value);
		params.put("product_id", prod_id);
		params.put("view_date", sdt);
		AdWordsRemarketingReporter.reportWithConversionId(
				getApplicationContext(), "969704640", params);
		Tracker t = ((AnimalsInSpaceApp) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		ScreenViewBuilder hitBuilder = new HitBuilders.ScreenViewBuilder();
		hitBuilder.setCustomDimension(1, "land");
		hitBuilder.setCustomMetric(1, Integer.parseInt(value));
		hitBuilder.setCustomMetric(2, 1);
		// Set screen name.
		t.setScreenName(screen_name);
		Product product = new Product().setId(prod_id).setName(animal)
				.setCategory("Space Animals").setBrand("Soyoyoo")
				.setVariant("black").setPrice(Integer.parseInt(value)).setQuantity(1);
		ProductAction productAction = new ProductAction(
				ProductAction.ACTION_CHECKOUT).setCheckoutStep(1)
				.setCheckoutOptions("Credit Card");
		hitBuilder.addProduct(product).setProductAction(productAction);
		// Send a screen view.
		t.send(hitBuilder.build());

	}

	public void onClick(View v) {

		Intent intent = null;
		int vid = v.getId();
		Log.v("SubActivity", "v.getId() is: " + vid);
		Log.v("SubActivity", "R.id.button1: " + R.id.button1);
		
		switch (vid) {
		case R.id.button1:
		case R.id.imageView1:

			AdWordsConversionReporter.reportWithConversionId(
					this.getApplicationContext(), "969704640",
					"483qCPOJvFcQwImyzgM", value, true);

			intent = new Intent(this, VoteActivity.class);
			intent.putExtra("animal", animal);
			startActivity(intent);
			finish();
			break;

		default:
			Log.v("SubActivity", "View id=" + v.getId());
		}

	}

	protected void onStart() {
		super.onStart();
		// Connect your client
		mClient.connect();

		// Define the outlinks on the page for both app and web
		Uri appUri1 = Uri
				.parse("android-app://com.soyoyoo.animalsinspaceapp/dad/animals/vote/");
		Uri webUrl1 = Uri.parse("http://glossy-depot-510.appspot.com/vote/");

		// Create App Indexing Link objects
		AppIndexingLink item1 = new AppIndexingLink(appUri1, webUrl1,
				this.findViewById(R.id.button1));
		AppIndexingLink item2 = new AppIndexingLink(appUri1, webUrl1,
				this.findViewById(R.id.imageView1));

		// Create a list out of these objects
		List<AppIndexingLink> outlinks = new ArrayList<AppIndexingLink>();
		outlinks.add(item1);
		outlinks.add(item2);
		// Define a title for your current page, shown in autocompletion UI
		final String TITLE = "Animals In Space " + screen_name;

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

	protected void initialiseFromXml(String path) {

		XmlResourceParser xrp = getResources().getXml(R.xml.feed);

		try {
			int eventType = xrp.getEventType();

			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					Log.v("SubActivity", "Start tag " + xrp.getName());
					String tagname = xrp.getName();
					if (tagname.equals("item")) {
						String pathname = xrp.getAttributeValue(null, "path");
						if (pathname.equals(path)) {
			
							animal = xrp.getAttributeValue(null, "animal");
							APP_URI = Uri.parse(xrp.getAttributeValue(null,
									"app_uri"));
							WEB_URL = Uri.parse(xrp.getAttributeValue(null,
									"web_uri"));
							screen_name = xrp.getAttributeValue(null,
									"screen_name");
							prod_id = xrp.getAttributeValue(null, "prod_id");
							value = xrp.getAttributeValue(null, "value");
							String lid = xrp.getAttributeValue(null, "layout");
							Log.v("SubActivity", "layout = " + lid);
							layout_id = getResources().getIdentifier(lid,
									"layout", getPackageName());
							return;
						}

					}

				}
				eventType = xrp.next();
			}
		} catch (XmlPullParserException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
