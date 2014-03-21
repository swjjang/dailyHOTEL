package com.twoheart.dailyhotel.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;


public class EventWebActivity extends Activity implements Constants {

	private static final String URL_WEBAPI_EVENT = "http://event.dailyhotel.co.kr";
	private WebView webview;
	
	@JavascriptInterface
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    }
		
		setContentView(R.layout.activity_event_web);
		webview = (WebView) findViewById(R.id.webviewForEvent);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSupportZoom(false);
		webview.loadUrl(URL_WEBAPI_EVENT);

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
		    		
		    		Uri uri = Uri.parse(URL_STORE_GOOGLE_DAILYHOTEL);
		    		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		    		startActivity(intent);
		    	}
		    	else if (url.equals("event://tstore"))
		    	{
		    		finish();
		    		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		    		
		    		Uri uri = Uri.parse(URL_STORE_T_DAILYHOTEL);
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
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
	}

}
