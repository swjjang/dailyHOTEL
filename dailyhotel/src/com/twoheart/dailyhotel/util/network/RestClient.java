package com.twoheart.dailyhotel.util.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class RestClient {

	private static final String TAG = "RestClient";
	
	private static final int TIME_OUT = 10 * 1000;  // Connection TIME OUT 10√ 
	
	public static RestClient instance;
	
    private ArrayList <NameValuePair> params;
    private ArrayList <NameValuePair> headers;

    private String url;
    private int responseCode;
    private String message;
    private String response;
    private Cookie cookie;
    private CookieManager cookieManager;
    
    private HttpClient client;
    
    public String getResponse() {
        return response;
    }
    
    public Cookie getCookie() {
    	return  cookie;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public static RestClient getInstance() {
		if(instance == null) 
			instance = new RestClient();
		return instance;
	}
    
    
    public RestClient() {
    	HttpParams params = new BasicHttpParams();
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
        client = new DefaultHttpClient(cm, params);
    }

    public void setUrl(String url) {
		this.url = url;
    	params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }
    
    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }
    
    public void execute(String method) {
    	if ( method.equalsIgnoreCase("GET") ) {
            //add parameters
            String combinedParams = "";
            if(!params.isEmpty()){
                combinedParams += "?";
                for(NameValuePair p : params) {
                    String paramString = "";
					try {
						paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), HTTP.UTF_8);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
                    if(combinedParams.length() > 1) {
                        combinedParams  +=  "&" + paramString;
                    }
                    else {
                        combinedParams += paramString;
                    }
                }
            }

            HttpGet request = new HttpGet(url + combinedParams);

            //add headers
            for(NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }

            //	set timeout
            request.getParams().setParameter("http.protocol.expect-continue", false);
            request.getParams().setParameter("http.connection.timeout", TIME_OUT);
            request.getParams().setParameter("http.socket.timeout", TIME_OUT);
            
            executeRequest(request, url);
            
    	} else if ( method.equalsIgnoreCase("POST") ) {
            
    		try {
    		
    			HttpPost request = new HttpPost(url);
	            
	            //add headers
	            for(NameValuePair h : headers) {
	                request.addHeader(h.getName(), h.getValue());
	            }
	            
	            
	            //set timeout
	            request.getParams().setParameter("http.protocol.expect-continue", false);
	            request.getParams().setParameter("http.connection.timeout", TIME_OUT);
	            request.getParams().setParameter("http.socket.timeout", TIME_OUT);
	            
	            if(!params.isEmpty()){
	                try {
						request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						executeRequest(request, url);
					} catch (UnsupportedEncodingException e) {
						Log.d(TAG, e.toString());
						response =  "NETWORK_ERROR";
					}
	            } else {
	            	executeRequest(request, url);
	            }
	        
			} catch (Exception e) {
	    		Log.d(TAG, e.toString());
	    	}
    	}
    }
    
    private void executeRequest(HttpUriRequest request, String url) {
        HttpResponse httpResponse;
        
        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();
            
            if (entity != null) {

                InputStream instream = entity.getContent();
                response = convertStreamToString(instream);
                
                // Closing the input stream will trigger connection release
                instream.close();
            }
            
            List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
            
            for(int i=0; i<cookies.size(); i++) {
            	Cookie tempCookie = cookies.get(i);
            	if(tempCookie.getName().equals("JSESSIONID")) {
            		cookie = tempCookie;
//            		String cookieString = cookie.getName() + "=" + cookie.getValue();
//			        Log.e("cookieString", cookieString);
//			        cookieManager.setCookie("http://dailyhotel.kr/goodnight", cookieString);
//			        CookieSyncManager.getInstance().sync();
//					Thread.sleep(500);
            		break;
            	}
            }
            
        } catch (Exception e) {
        	Log.d(TAG, e.toString());
        	response = "NETWORK_ERROR";
		}
        
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
