package com.twoheart.dailyhotel.activity;

import static com.twoheart.dailyhotel.util.AppConstants.PAYMENT;
import static com.twoheart.dailyhotel.util.AppConstants.PAYMENT_DISCOUNT;
import static com.twoheart.dailyhotel.util.AppConstants.REST_URL;
import kr.co.kcp.android.payment.standard.KcpApplication;
import kr.co.kcp.util.PackageState;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.android.gcm.GCMRegistrar;
import com.twoheart.dailyhotel.R;
//import com.google.android.gcm.GCMRegistrar;

public class PaymentActivity extends Activity{
	
	public static final String   ACTIVITY_RESULT         = "ActivityResult";
	public static final int      PROGRESS_STAT_NOT_START = 1;
	public static final int      PROGRESS_STAT_IN        = 2;
	public static final int      PROGRESS_DONE           = 3;
	public static       String   CARD_CD                 = "";
	public static       String   QUOTA                   = "";
	public              WebView  webView;
	private final       Handler  handler                 = new Handler();
	public              int      m_nStat                 = PROGRESS_STAT_NOT_START;
	
	String url;
	String postData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		
		Intent intent = getIntent();
		String booking_idx = intent.getStringExtra("booking_idx");
		boolean isBonus = intent.getBooleanExtra("isBonus", false);
		boolean isFullBonus = intent.getBooleanExtra("isFullBonus", false);
		String bonus = intent.getStringExtra("credit");
		
		if(!isBonus) {
			url =  REST_URL + PAYMENT + booking_idx + "?unique=" + GCMRegistrar.getRegistrationId(this);
			Log.d("url", url);
		}
		else {
			if(isFullBonus) {
				url = REST_URL + PAYMENT_DISCOUNT + booking_idx + "?unique=" + GCMRegistrar.getRegistrationId(this);// + "/" + bonus;
				Log.d("url", url);
			} else {
				url = REST_URL + PAYMENT_DISCOUNT + booking_idx+ "/" + bonus + "?unique=" + GCMRegistrar.getRegistrationId(this);
				Log.d("url", url);
			}
		}
		
		postData = "AppUrl=dailyHOTEL://card_pay";
		
		loadResource();
	}
	
	
	public void loadResource(){
		webView = (WebView) findViewById(R.id.webview);
		
		webView.getSettings().setSavePassword(false);
		webView.getSettings().setJavaScriptEnabled( true );
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically( true );
		webView.addJavascriptInterface(new KCPPayBridge(), "KCPPayApp");
        // 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
		webView.addJavascriptInterface(new KCPPayCardInfoBridge(), "KCPPayCardInfo");
		webView.addJavascriptInterface(new JavaScriptExtention(), "android");

		webView.setWebChromeClient( new WebChromeClient() );
		webView.setWebViewClient  ( new mWebViewClient()  );
//		webView.getSettings().setPluginState(PluginState.ON);
		
		
		webView.postUrl( url, EncodingUtils.getBytes(postData, "BASE64") );
	}
	
	 private boolean url_scheme_intent( String url ) {
        Log.d( KcpApplication.m_strLogTag, 
                "[PayDemoActivity] called__test - url=[" + url + "]" );
        
        Uri       uri = Uri.parse(url);
        Log.d("urllllllllll", uri.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        
        try{
            startActivity(intent);
        }catch(ActivityNotFoundException e) {
        	return true;
        }
        
        return true;
    }
	 
	 private class mWebViewClient extends WebViewClient { 
			@Override 
			public boolean shouldOverrideUrlLoading( WebView view, String url )
			{
				Log.d( KcpApplication.m_strLogTag, 
					   "[PayDemoActivity] called__shouldOverrideUrlLoading - url=[" + url + "]" );

				if (url != null && !url.equals("about:blank"))
	            {
				    String url_scheme_nm = url.substring(0, 10);
				    
	                if( url_scheme_nm.contains("http://") || url_scheme_nm.contains("https://") )
	                {
	                    if (url.contains("http://market.android.com")            ||
	                        url.contains("http://m.ahnlab.com/kr/site/download") ||
	                        url.endsWith(".apk")                                   )
	                    {
	                        return url_scheme_intent( url );
	                    }
	                    else
	                    {
	                        view.loadUrl( url );
	                        return false;
	                    }
	                }
	                
	                else if(url_scheme_nm.contains("mailto:"))
	                {
	                    return false;
	                }
	                else if(url_scheme_nm.contains("tel:"))
	                {
	                    return false;
	                }
	                else 
	                {
	                    return url_scheme_intent( url );
	                }
	            }

				return true;
			}
			
			// error 처리
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				webView.loadUrl("about:blank"); 
				Intent	intent = new Intent();
				intent.putExtra( ACTIVITY_RESULT, "NEWORK_ERROR" );
				setResult(RESULT_OK, intent );
				finish();
			}
	}
		
		// 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
		private class KCPPayCardInfoBridge
	    {
	        public void getCardInfo( final String card_cd, final String quota )
	        {
	            handler.post( new Runnable() {
	                public void run()
	                {
	                    Log.d( KcpApplication.m_strLogTag, "[PayDemoActivity] KCPPayCardInfoBridge=[" + card_cd + ", " + quota + "]" );
	                    
	                    CARD_CD = card_cd;
	                    QUOTA   = quota;
	                    
	                    PackageState ps = new PackageState( PaymentActivity.this );
	                    
	                    if(!ps.getPackageDownloadInstallState( "com.skt.at" ))
	                    {
	                        alertToNext();
	                    }
	                }
	            });
	        }
	        
	        private void alertToNext()
	        {
	            AlertDialog.Builder  dlgBuilder = new AlertDialog.Builder( PaymentActivity.this );
	            AlertDialog          alertDlg;
	          
	            dlgBuilder.setMessage( "HANA SK 모듈이 설이 되어있지 않습니다.\n설치 하시겠습니까?" );
	            dlgBuilder.setCancelable( false );
	            dlgBuilder.setPositiveButton( "예",
	                                          new DialogInterface.OnClickListener()
	                                              {
	                                                  @Override
	                                                  public void onClick(DialogInterface dialog, int which)
	                                                  {
	                                                      dialog.dismiss();
	                                                      
	                                                      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( "http://cert.hanaskcard.com/Ansim/HanaSKPay.apk" ) );

	                                                      m_nStat = PROGRESS_STAT_IN;

	                                                      startActivity( intent );
	                                                  }
	                                              }
	                                          );
	            dlgBuilder.setNegativeButton( "아니오",
	                                          new DialogInterface.OnClickListener()
	                                              {
	                                                  @Override
	                                                  public void onClick(DialogInterface dialog, int which)
	                                                  {
	                                                      // TODO Auto-generated method stub
	                                                      dialog.dismiss();
	                                                  }
	                                              }
	                                          );

	            alertDlg = dlgBuilder.create();
	            alertDlg.show();
	        }
	    }
		
	    private class KCPPayBridge
	    {
	        public void launchMISP( final String arg )
	        {
	            handler.post( new Runnable() {
	                public void run()
	                {
	                    boolean isp_app = true;
	                    String  strUrl;
	                    String  argUrl;

	                    PackageState ps = new PackageState( PaymentActivity.this );
	                    
	                    argUrl = arg;
	                    
	                    if(!arg.equals("Install"))
	                    {
	                        if(!ps.getPackageDownloadInstallState( "kvp.jjy.MispAndroid" ))
	                        {
	                            argUrl = "Install";
	                        }
	                    }
	                    
	                    strUrl = ( argUrl.equals( "Install" ) == true )
	                                ? "market://details?id=kvp.jjy.MispAndroid320" //"http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
	                                : argUrl;

	                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse( strUrl ) );

	                    m_nStat = PROGRESS_STAT_IN;
	                    Log.d("m_nStat", Integer.toString(m_nStat));
	                    startActivity( intent );
	                }
	            });
	        }
	    }

	    @Override
	    protected void onRestart()
	    {
	        super.onResume();

	        Log.d( KcpApplication.m_strLogTag,
	               "[PayDemoActivity] called__onResume + INPROGRESS=[" + m_nStat + "]" );

	        KcpApplication myApp = (KcpApplication)getApplication();
	        
	        // 하나 SK 모듈로 결제 이후 해당 카드 정보를 가지고 오기위해 사용
	        if(myApp.m_uriResult != null)
	        {
	    		if( myApp.m_uriResult.getQueryParameter("realPan") != null &&
	    		    myApp.m_uriResult.getQueryParameter("cavv")    != null &&
	    		    myApp.m_uriResult.getQueryParameter("xid")     != null &&
	    		    myApp.m_uriResult.getQueryParameter("eci")     != null
	    		)
	            {
	    		    Log.d( KcpApplication.m_strLogTag,
	    	               "[PayDemoActivity] HANA SK Result = javascript:hanaSK('"     + myApp.m_uriResult.getQueryParameter("realPan") +
	                                                                             "', '" + myApp.m_uriResult.getQueryParameter("cavv")    +
	                                                                             "', '" + myApp.m_uriResult.getQueryParameter("xid")     +
	                                                                             "', '" + myApp.m_uriResult.getQueryParameter("eci")     +
	                                                                             "', '" + CARD_CD                                        +
	                                                                             "', '" + QUOTA                                          + "');" );
	    		    
	                // 하나 SK 모듈로 인증 이후 승인을 하기위해 결제 함수를 호출 (주문자 페이지)
	                webView.loadUrl( "javascript:hanaSK('"     + myApp.m_uriResult.getQueryParameter("realPan")  +
	                                                     "', '" + myApp.m_uriResult.getQueryParameter("cavv")     +
	                                                     "', '" + myApp.m_uriResult.getQueryParameter("xid")      +
	                                                     "', '" + myApp.m_uriResult.getQueryParameter("eci")      +
	                                                     "', '" + CARD_CD                                         +
	                                                     "', '" + QUOTA                                           + "');" );
	            }
	    		
	    		if( (myApp.m_uriResult.getQueryParameter("res_cd") == null? "":
	    		     myApp.m_uriResult.getQueryParameter("res_cd")             ).equals("999"))
	            {
	                Log.d( KcpApplication.m_strLogTag,
	                       "[PayDemoActivity] HANA SK Result = cancel" );
	                
	                m_nStat = 9;
	            }
	    		
	    		if( (myApp.m_uriResult.getQueryParameter("isp_res_cd") == null? "":
	                myApp.m_uriResult.getQueryParameter("isp_res_cd")             ).equals("0000"))
	            {
	                Log.d( KcpApplication.m_strLogTag,
	                      "[PayDemoActivity] ISP Result = 0000" );
	               
	                webView.loadUrl( "http://pggw.kcp.co.kr/lds/smart_phone_linux_jsp/sample/card/samrt_res.jsp?result=OK&a=" + myApp.m_uriResult.getQueryParameter("a") );
//	                webView.loadUrl( "https://pggw.kcp.co.kr/app.do?ActionResult=app&approval_key=" + strApprovalKey );
	            }
	            else
	            {
	                Log.d( KcpApplication.m_strLogTag,
	                      "[PayDemoActivity] ISP Result = cancel" );
	            }
	        }
			
			if ( m_nStat == PROGRESS_STAT_IN )
			{
				checkFrom();
			}
			
			myApp.m_uriResult = null;
	    }

	    private void checkFrom()
	    {
	    	try
	    	{
	            KcpApplication myApp = (KcpApplication)getApplication();
	            
	            
	            if ( myApp.m_uriResult != null )
	            {
	            	m_nStat = PROGRESS_DONE;
	            	String	strResultInfo = myApp.m_uriResult.getQueryParameter( "approval_key" );
	            	
	            	if ( strResultInfo == null || strResultInfo.length() <= 4 )  finishActivity( "ISP 결제 오류" );
	            	
	            	String  strResCD = strResultInfo.substring( strResultInfo.length() - 4 );

	        		Log.d( KcpApplication.m_strLogTag,
	        			   "[PayDemoActivity] result=[" + strResultInfo + "]+" + "res_cd=[" + strResCD + "]" );

	            	if ( strResCD.equals( "0000" ) == true )
	            	{
	            		
	            		String	strApprovalKey = "";
	            		
	            		strApprovalKey = strResultInfo.substring( 0, strResultInfo.length() - 4  );
	            		
	            		Log.d( KcpApplication.m_strLogTag,
	             			   "[PayDemoActivity] approval_key=[" + strApprovalKey + "]" );
	            		
	            		webView.loadUrl( "https://pggw.kcp.co.kr/app.do?ActionResult=app&approval_key=" + strApprovalKey);

	            	}
	            	else if ( strResCD.equals( "3001" ) == true )
	            	{
	            		finishActivity( "ISP 결제 사용자 취소" );
	            	}
	            	else
	            	{
	            		finishActivity( "ISP 결제 기타 오류" );
	            	}
	            }
	    	}
	    	catch ( Exception e )
	    	{
	    	}
	    	finally
	    	{
	    	}
	    }

	    @Override
	    protected Dialog onCreateDialog( int id )
	    {
			Log.d( KcpApplication.m_strLogTag,
	  			   "[PayDemoActivity] called__onCreateDialog - id=[" + id + "]" );

	    	super.onCreateDialog( id );

	    	AlertDialog.Builder	 dlgBuilder = new AlertDialog.Builder( this );
	    	AlertDialog			 alertDlg;
	    	
	    	dlgBuilder.setTitle( "취소" );
	    	dlgBuilder.setMessage( "결제가 진행중입니다.\n취소하시겠습니까?" );
	    	dlgBuilder.setCancelable( false );
	    	dlgBuilder.setPositiveButton( "예",
	    			                      new DialogInterface.OnClickListener()
	    										{
													@Override
													public void onClick(DialogInterface dialog, int which)
													{
														// TODO Auto-generated method stub
														dialog.dismiss();
														
														finishActivity( "사용자 취소" );
													}
	    										}
	    								 );
	    	dlgBuilder.setNegativeButton( "아니오",
	                					  new DialogInterface.OnClickListener()
												{
													@Override
													public void onClick(DialogInterface dialog, int which)
													{
														// TODO Auto-generated method stub
														dialog.dismiss();
													}
												}
					 					 );

	    	alertDlg = dlgBuilder.create();

	    	return  alertDlg;
	    }
	 
	    public void finishActivity( String p_strFinishMsg ){
	    	Intent	intent = new Intent();
	    	
	    	if ( p_strFinishMsg != null )
	    	{
	    		intent.putExtra( ACTIVITY_RESULT, p_strFinishMsg );
	    		setResult( RESULT_OK, intent );
	    	}
	    	else
	    	{
	    		setResult( RESULT_CANCELED );
	    	}
	    	
	    	finish();
	    }
	    
	    private class JavaScriptExtention {
			
			JavaScriptExtention() {}
			
		    public void feed(final String msg) {
		    	Intent	intent = new Intent();
		    	
		    	if(msg.equals("SUCCESS")) {
		    		intent.putExtra( ACTIVITY_RESULT, "SUCCESS" );
		    		setResult( RESULT_OK, intent );
		    	} else if(msg.equals("INVALID_SESSION")) {
		    		 intent.putExtra( ACTIVITY_RESULT, "INVALID_SESSION" );
		    		 setResult( RESULT_OK, intent );
		    	} else if(msg.equals("SOLD_OUT")) {
		    		intent.putExtra( ACTIVITY_RESULT, "SOLD_OUT" );
		    		setResult( RESULT_OK, intent );
		    	} else if(msg.equals("PAYMENT_COMPLETE")) {
		    		intent.putExtra( ACTIVITY_RESULT, "PAYMENT_COMPLETE" );
		    		setResult( RESULT_OK, intent );
		    	} else if(msg.equals("INVALID_DATE")) {
		    		intent.putExtra(ACTIVITY_RESULT, "INVALID_DATE");
		    		setResult( RESULT_OK, intent );
		    	}
		    	finish();
		    }
		}
	    
	    @Override
	    public void onBackPressed() {
	    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentActivity.this);
			alertDialog.setTitle("결제알림").setMessage("결제를 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인",
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    	finish();
			    }
			}).setNegativeButton("취소",
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			    return;
			    }
			});
			AlertDialog alert = alertDialog.create();
			alert.show();
	    }
}
