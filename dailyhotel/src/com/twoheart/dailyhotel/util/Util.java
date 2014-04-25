package com.twoheart.dailyhotel.util;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class Util implements Constants {
	
	public static int dpToPx(Context context, double dp) {
		float scale = context.getResources().getDisplayMetrics().density; 
		return (int) (dp * scale + 0.5f);
	}
	
	public static String storeReleaseAddress() {
		if (IS_GOOGLE_RELEASE) {
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else {
			return URL_STORE_T_DAILYHOTEL;
		}
	}
	
	public static String storeReleaseAddress(String newUrl) {
		if (IS_GOOGLE_RELEASE) {
			return URL_STORE_GOOGLE_DAILYHOTEL;
		} else {
			return newUrl;
		}
	}
	
	 public static void CopyStream(InputStream is, OutputStream os)
	    {
	        final int buffer_size=1024;
	        try
	        {
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){}
	    }
	
}
