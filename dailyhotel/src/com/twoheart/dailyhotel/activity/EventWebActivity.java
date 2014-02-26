package com.twoheart.dailyhotel.activity;

import java.util.HashMap;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.R.layout;
import com.twoheart.dailyhotel.R.menu;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EventWebActivity extends Activity {

	private WebView webview;
	
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;
	
	// Jason | Google analytics
	@Override
	public void onStart() {
		super.onStart();
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Event View");
		
		mGaTracker.send(hitParameters);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_event_web);
		
		// Google analytics
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker("UA-43721645-1");
		
		// Jason
		webview = (WebView) findViewById(R.id.webviewForEvent);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSupportZoom(false);
		
		webview.loadUrl("http://event.dailyhotel.co.kr");

		/* If you print customized html, refer next.
		String customHtml = "<html><a href=https://play.google.com/store/apps/details?id=com.twoheart.dailyhotel>Apply event</a></html>";
		webview.loadData(customHtml, "text/html", "UTF-8");
		*/
		// URL parsing
		this.webview.setWebViewClient(new WebViewClient() {
		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		    	if (url.equals("event://"))
		    	{
		    		finish();
		    		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		    		
		    		Uri uri = Uri.parse("market://details?id=com.twoheart.dailyhotel");
		    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    		startActivity(intent);
		    	}
		    	else if (url.equals("event://tstore"))
		    	{
		    		finish();
		    		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		    		
		    		Uri uri = Uri.parse("http://tsto.re/0000412421");
		    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    		startActivity(intent);
		    	}
		    	else
		    	{
		    		view.loadUrl(url);
		    	}
		    	return true;
		    }
		});
		
		// For Javascript alert
		this.webview.setWebChromeClient(new WebChromeClient() {
		    @Override
		    public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
		    {
		        new AlertDialog.Builder(view.getContext())
		            .setTitle("¾Ë¸²")
		            .setMessage(message)
		            .setPositiveButton(android.R.string.ok,
		                    new AlertDialog.OnClickListener()
		                    {
		                        public void onClick(DialogInterface dialog, int which)
		                        {
		                            result.confirm();
		                        }
		                    })
		            .setCancelable(false)
		            .create()
		            .show();
		        return true;
		    };
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_web, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		super.onBackPressed();
	}
}
