/*
 * 		Animals In Space App
 * 		by JeeWook Kim
 * 
 * 		Samples are offered on as-is basis, and designed only to provide you with certain examples of how such code samples could be utilized.
 *      By implementing any of Samples, you agree to solely assume all responsibility for any consequences that arise from such implementation.
 *
 */

package com.soyoyoo.animalsinspaceapp;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class VoteActivity extends Activity {
	private String animal;
	private String prod_id;
	private String value;
	private String screen_name;
	private String category;
	private String tid;
	private String store = "Soyoyoo Store";
	private String coupon ="mobile";
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent voteIntent = getIntent();
		animal = voteIntent.getStringExtra("animal");
		Log.v("VoteActivity", "animal:" + animal);
		initialiseFromXml(animal);

		setContentView(R.layout.activity_vote);
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText(screen_name);
		// configure WebView to demonstrate WebView tracking using AdWords SDK and GA SDK
		ConfigureWebView();
	}

	protected void onResume() {
		super.onResume();
		
		Locale locale = Locale.getDefault();
		DateFormat df = new SimpleDateFormat("yyyyMMdd", locale);
		DateFormat dfs = new SimpleDateFormat("yyyyMMddHHmmss", locale);
		Date dt = new Date(System.currentTimeMillis());
		String sdt = df.format(dt);
		tid = dfs.format(dt)+(new Random()).nextInt(9);
		Log.v("VoteActivity","tid="+tid);
		// set custom parameters for re-marketing segment gathering
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("action_type", "vote");
		params.put("product_category", category);
		params.put("value", value);
		params.put("purchase_date", sdt);
		// send re-marketing parameters to AdWords
		AdWordsRemarketingReporter.reportWithConversionId(
				getApplicationContext(), "969704640", params);
		// get a Google Analytics tracker
		Tracker t = ((AnimalsInSpaceApp) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		ScreenViewBuilder hitBuilder = new HitBuilders.ScreenViewBuilder();
		// set custom dimension
		hitBuilder.setCustomDimension(1, "land");
		// set custom metrics
		hitBuilder.setCustomMetric(1, Integer.parseInt(value));
		hitBuilder.setCustomMetric(3, 1);
		// Set screen name.
		t.setScreenName("Vote");
		// Enhanced Ecommerce tracking
		Product product = new Product().setId(prod_id).setName(animal)
				.setCategory(category)
				.setPrice(Integer.parseInt(value))
				.setQuantity(1);
		ProductAction productAction = new ProductAction(
				ProductAction.ACTION_PURCHASE).setTransactionId(tid)
				.setTransactionAffiliation(store)
				.setTransactionRevenue(Integer.parseInt(value))
				.setTransactionTax(Math.round(Integer.parseInt(value) / 10))
				.setTransactionShipping(2500)
				.setTransactionCouponCode(coupon);
		hitBuilder.addProduct(product).setProductAction(productAction);
		// Send a screen view.
		t.send(hitBuilder.build());

	}

	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.button1:
			// report a conversion to AdWords
			AdWordsConversionReporter.reportWithConversionId(
					this.getApplicationContext(), "969704640",
					"lNlxCKPUwFcQwImyzgM", value, true);
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			Log.v("VoteActivity", "v.getId() is: " + String.valueOf(v.getId()));
		}

	}

	protected void initialiseFromXml(String animal) {

		XmlResourceParser xrp = getResources().getXml(R.xml.feed);
		try {
			int eventType = xrp.getEventType();

			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					Log.v("SubActivity", "Start tag " + xrp.getName());
					String tagname = xrp.getName();
					if (tagname.equals("item")) {
						String animalname = xrp.getAttributeValue(null,
								"animal");
						if (animalname.equals(animal)) {
							prod_id = xrp.getAttributeValue(null, "prod_id");
							value = xrp.getAttributeValue(null, "value");
							category = xrp.getAttributeValue(null, "category");
							screen_name = xrp.getAttributeValue(null,
									"screen_name");
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

	

	private void ConfigureWebView() {
		webView = (WebView) findViewById(R.id.webview);
		// Enable Javascript
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// Enable Viewport
		webSettings.setUseWideViewPort(true);
		webView.addJavascriptInterface(new WebAppInterface(this), "animalsinapp");
		// load sample page where user action and load time are being tracked
		webView.loadUrl("http://1-dot-glossy-depot-510.appspot.com/vote/survey.html");
	}

	final class WebAppInterface {
	    Context mContext;
	    // Instantiate the interface and set the context
	    WebAppInterface(Context c) {
	        mContext = c;
	    }
	   
	    @JavascriptInterface
	    public void sendTrackingInfo(String sMessage){
	    	// Track conversion using AdWords Conversion Tracking SDK
	    	AdWordsConversionReporter.reportWithConversionId(
	    			mContext.getApplicationContext(), "969704640",
					"8GqWCM-Wpl8QwImyzgM", value, true);
	    	// Build and send Google Analytics event.
	    	Tracker t = ((AnimalsInSpaceApp) getApplication())
					.getTracker(TrackerName.APP_TRACKER);	
	    	t.send(new HitBuilders.EventBuilder()
	    	    .setCategory("interaction")
	    	    .setAction("survey")
	    	    .setLabel(sMessage)
	    	    .setCustomMetric(4, 1)
	    	    .build());
	    }
	    @JavascriptInterface
	    public void sendTimingInfo(long elapsedTime) {
	    	// Build and send Google Analytics User Timing tracking
	    	Tracker t = ((AnimalsInSpaceApp) getApplication())
					.getTracker(TrackerName.APP_TRACKER);	
	    	t.send(new HitBuilders.TimingBuilder("load time", "survey", elapsedTime)
	    	    .build());
	    }
	}
	
	

}
