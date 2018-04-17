package com.daily.dailyhotel.screen.common.payment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.BasePaymentWebActivity;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by android_sam on 2017. 3. 17..
 */

public class PaymentWebActivity extends BasePaymentWebActivity
{
    private static final String INTENT_EXTRA_DATA_JSON_STRING = "jsonString";
    private static final String INTENT_EXTRA_DATA_URL = "url";
    private static final String INTENT_EXTRA_DATA_CALL_SCREEN = "callScreen";

    private String mJSONString;
    private String mUrl;
    private String mCallScreen;

    public static Intent newInstance(Context context, String url, String jsonString, String callScreen)
    {
        Intent intent = new Intent(context, PaymentWebActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_JSON_STRING, jsonString);
        intent.putExtra(INTENT_EXTRA_DATA_URL, url);
        intent.putExtra(INTENT_EXTRA_DATA_CALL_SCREEN, callScreen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestPostPaymentWebView(mWebView, mUrl, mJSONString);
    }

    @Override
    public void onIntent(Intent intent)
    {
        mJSONString = intent.getStringExtra(INTENT_EXTRA_DATA_JSON_STRING);
        mUrl = intent.getStringExtra(INTENT_EXTRA_DATA_URL);
        mCallScreen = intent.getStringExtra(INTENT_EXTRA_DATA_CALL_SCREEN);
    }

    @Override
    protected String getScreenName()
    {
        return mCallScreen;
    }

    @Override
    public void onPaymentResult(String jsonString)
    {
        int msgCode;

        if (DEBUG)
        {
            ExLog.d("pinkred : " + jsonString);
        }

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

    protected void requestPostPaymentWebView(WebView webView, String url, String jsonString)
    {
        if (DailyTextUtils.isTextEmpty(url, jsonString) == true)
        {
            DailyToast.showToast(this, R.string.toast_msg_failed_to_get_payment_info, Toast.LENGTH_SHORT);
            finish();
            return;
        }

        WebViewPostAsyncTask webViewPostAsyncTask = new WebViewPostAsyncTask(webView, jsonString);
        webViewPostAsyncTask.execute(url);
    }

    protected class WebViewPostAsyncTask extends AsyncTask<String, Void, Response>
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
        protected Response doInBackground(String... params)
        {
            mUrl = params[0];

            try
            {
                if (DEBUG == true)
                {
                    ExLog.d("pinkred : " + mJSONString);
                }

                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()//
                    .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

                Request request = new Request.Builder()//
                    .url(mUrl)//
                    .addHeader("Os-Type", "android")//
                    .addHeader("App-Version", DailyHotel.VERSION)//
                    .addHeader("App-VersionCode", DailyHotel.VERSION_CODE)//
                    .addHeader("Authorization", DailyHotel.AUTHORIZATION)//
                    .addHeader("User-Agent", System.getProperty("http.agent"))//
                    .addHeader("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID)//
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mJSONString)).build();

                Response response = okHttpClient.newCall(request).execute();
                return response;
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response response)
        {
            if (response == null)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
                finish();
                return;
            }

            int code = response.code();

            // 세션이 만료된 경우
            if (code == 401)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION);
                finish();
                return;
            }

            // 가격이 변동된 경우
            if (code == 1190)
            {
                String message = response.message();
                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    message = getString(R.string.dialog_msg_hotel_payment_changed_price);
                }

                showSimpleDialog(getString(R.string.dialog_title_payment) //
                    , message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {

                        }
                    }, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            setResult(CODE_RESULT_ACTIVITY_PAYMENT_CHANGED_PRICE);
                            finish();
                        }
                    });
                return;
            }

            String data = null;

            try
            {
                data = response.body().string();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            if (DailyTextUtils.isTextEmpty(data) == true)
            {
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
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
                setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
                finish();
            }
        }
    }
}


