package com.daily.dailyhotel.screen.home.stay.inbound.payment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.place.activity.PlacePaymentWebActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by android_sam on 2017. 3. 17..
 */

public class StayPaymentWebActivity extends PlacePaymentWebActivity
{
    private String URL_WEBAPI_PAYMENT = Constants.UNENCRYPTED_URL ? "api/v4/booking/hotel/{type}"//
        : "NTYkNjIkMzIkNTIkMjIkOTEkMTQkNTYkNDYkNzUkNDUkMTckNzEkNzgkMzMkMzQk$NTYyRTI2QTA0MDLFDUMkE2RjQk0OUVGNURWE3PMkUxMUExNDTYD5QTM4NTVKGLQkY5FMzI4RIPDAxRDROSFOTM0RTk3MURBNTUxQwQ==$";

    private static final String INTENT_EXTRA_DATA_PLACE_INDEX = "placeIndex";
    private static final String INTENT_EXTRA_DATA_PAY_TYPE = "payType";
    private static final String INTENT_EXTRA_DATA_JSON_STRING = "jsonString";

    private int mPlaceIndex;
    private String mPayType;
    private String mJSONString;

    public static Intent newInstance(Context context, int placeIndex, String payType, String jsonString)
    {
        Intent intent = new Intent(context, StayPaymentWebActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_PLACE_INDEX, placeIndex);
        intent.putExtra(INTENT_EXTRA_DATA_PAY_TYPE, payType);
        intent.putExtra(INTENT_EXTRA_DATA_JSON_STRING, jsonString);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestPostPaymentWebView(mWebView, mPlaceIndex, mPayType, mJSONString);
    }

    @Override
    public void initIntentData(Intent intent)
    {
        super.initIntentData(intent);

        mPlaceIndex = intent.getIntExtra(INTENT_EXTRA_DATA_PLACE_INDEX, -1);
        mPayType = intent.getStringExtra(INTENT_EXTRA_DATA_PAY_TYPE);
        mJSONString = intent.getStringExtra(INTENT_EXTRA_DATA_JSON_STRING);
    }

    @Override
    protected boolean hasProductList()
    {
        return true;
    }

    @Override
    protected int getProductIndex()
    {
        return mPlaceIndex;
    }

    @Override
    protected String getScreenName()
    {
        return AnalyticsManager.Screen.DAILYHOTEL_PAYMENT_PROCESS;
    }

    @Override
    public void onPaymentResult(String jsonString)
    {
        int msgCode;

        try
        {
            JSONObject jsonObject = new JSONObject(jsonString);
            msgCode = jsonObject.getInt("msgCode");
        } catch (Exception e)
        {
            msgCode = -1;

            ExLog.e(e.toString());
        }

        Intent intent = new Intent();
        if (msgCode < 0)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT, jsonString);
            setResult(RESULT_CANCELED, intent);
        } else
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT, jsonString);
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    protected void requestPostPaymentWebView(WebView webView, int placeIndex, String payType, String jsonString)
    {
        if (DailyTextUtils.isTextEmpty(jsonString) == true)
        {
            return;
        }

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("{type}", payType);

        try
        {
            String url;

            if (Constants.DEBUG == true)
            {
                url = DailyPreference.getInstance(this).getBaseOutBoundUrl()//
                    + Crypto.getUrlDecoderEx(URL_WEBAPI_PAYMENT, urlParams);
            } else
            {
                url = Crypto.getUrlDecoderEx(Setting.getOutboundServerUrl())//
                    + Crypto.getUrlDecoderEx(URL_WEBAPI_PAYMENT, urlParams);
            }

            WebViewPostAsyncTask webViewPostAsyncTask = new WebViewPostAsyncTask(webView, jsonString);
            webViewPostAsyncTask.execute(url);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            DailyToast.showToast(StayPaymentWebActivity.this, R.string.toast_msg_failed_to_get_payment_info, Toast.LENGTH_SHORT);
            finish();
            return;
        }
    }

    @Override
    public void onFeed(String msg)
    {
        // do nothing
    }

    @Override
    public void onPaymentFeed(String result)
    {
        // do nothing
    }

    protected class WebViewPostAsyncTask extends AsyncTask<String, Void, String>
    {
        private WebView mWebView;
        private String mJSONString;
        private String mUrl;

        public WebViewPostAsyncTask(WebView webView, String jsonString)
        {
            mWebView = webView;
            mJSONString = jsonString;
        }

        @Override
        protected String doInBackground(String... params)
        {
            mUrl = params[0];

            if (DEBUG == true)
            {
                ExLog.d("pinkred : " + mJSONString);
            }

            try
            {
                if (Constants.DEBUG == true)
                {
                    ExLog.d("pinkred : " + mJSONString);
                }

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()//
                    .url(mUrl)//
                    .addHeader("Os-Type", "android")//
                    .addHeader("App-Version", DailyHotel.VERSION)//
                    .addHeader("App-VersionCode", DailyHotel.VERSION_CODE)//
                    .addHeader("Authorization", DailyHotel.AUTHORIZATION)//
                    .addHeader("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID)//
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mJSONString)).build();

                Response response = okHttpClient.newCall(request).execute();

                // 세션이 만료된 경우
                if (response.code() == 401)
                {
                    return "401";
                }

                return response.body().string();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data)
        {
            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION, mPlacePaymentInformation);

            if (DailyTextUtils.isTextEmpty(data) == true)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL, intent);
                finish();
                return;
            } else if ("401".equalsIgnoreCase(data) == true)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION, intent);
                finish();
                return;
            }

            if (data.contains("payment_kcp") || data.contains("approval_key"))
            {
                mPgType = PgType.KCP;
            } else if (data.contains("inipay") || data.contains("inicis"))
            {
                mPgType = PgType.INICIS;
            } else
            {
                mPgType = PgType.ETC;
            }

            try
            {
                mWebView.loadDataWithBaseURL(mUrl, data, "text/html", "utf-8", null);
            } catch (Exception e)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL, intent);
                finish();
            }
        }
    }
}


