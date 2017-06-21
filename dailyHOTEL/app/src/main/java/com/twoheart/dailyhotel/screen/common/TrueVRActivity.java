package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyWebView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.TrueVRParams;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrueVRActivity extends WebViewActivity implements View.OnClickListener
{
    private List<TrueVRParams> mTrueVRParamsList;
    private TextView mProductNameTextView;
    private TextView mCurrentPageTextView, mTotalPageTextView;
    private View mPageLayout;
    private View mPrevView, mNextView;
    private View mWebViewLayout;

    private int mPlaceIndex;
    private int mCurrentPage;
    private PlaceType mPlaceType;

    public static Intent newInstance(Context context, int placeIndex, ArrayList<TrueVRParams> list, PlaceType placeType, String category)
    {
        Intent intent = new Intent(context, TrueVRActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVR_LIST, list);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CATEGORY, category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trueview);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        mPlaceIndex = intent.getIntExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
        mTrueVRParamsList = intent.getParcelableArrayListExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVR_LIST);
        mPlaceType = PlaceType.valueOf(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE));
        String category = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CATEGORY);

        if (mTrueVRParamsList == null || mTrueVRParamsList.size() == 0)
        {
            finish();
            return;
        }

        initWebView();
        initToolbar();
        initLayout((DailyWebView) mWebView);

        setTrueViewPage(mCurrentPage);

        try
        {
            HashMap<String, String> params = new HashMap();

            if (mPlaceType == PlaceType.HOTEL)
            {
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
            } else
            {
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            }

            params.put(AnalyticsManager.KeyType.CATEGORY, category);

            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.TRUE_VR, null, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void initToolbar()
    {
        View backView = findViewById(R.id.backView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(final DailyWebView dailyWebView)
    {
        if (dailyWebView == null)
        {
            return;
        }

        dailyWebView.addJavascriptInterface(new CupixEventListener(), "cupixEventListener");

        mWebViewLayout = findViewById(R.id.webViewLayout);
        mPageLayout = findViewById(R.id.pageLayout);

        if (mTrueVRParamsList != null && mTrueVRParamsList.size() == 1)
        {
            mPageLayout.setVisibility(View.GONE);
        } else
        {
            mPageLayout.setVisibility(View.VISIBLE);
        }

        mProductNameTextView = (TextView) findViewById(R.id.productNameTextView);
        mCurrentPageTextView = (TextView) findViewById(R.id.currentPageTextView);
        mTotalPageTextView = (TextView) findViewById(R.id.totalPageTextView);

        mNextView = findViewById(R.id.nextView);
        mPrevView = findViewById(R.id.prevView);

        mNextView.setOnClickListener(this);
        mPrevView.setOnClickListener(this);
    }

    private void setTrueViewPage(int page)
    {
        if (mWebView == null || page >= mTrueVRParamsList.size() || page < 0)
        {
            return;
        }

        int totalPage = mTrueVRParamsList.size();

        TrueVRParams trueVRParams = mTrueVRParamsList.get(page);

        if (trueVRParams == null)
        {
            return;
        }

        mCurrentPage = page;

        if (mPageLayout.getVisibility() == View.VISIBLE)
        {
            mCurrentPageTextView.setText(Integer.toString(mCurrentPage + 1));
            mTotalPageTextView.setText("/" + totalPage);

            if (page == 0)
            {
                mPrevView.setEnabled(false);
                mNextView.setEnabled(true);
            } else if (page == totalPage - 1)
            {
                mPrevView.setEnabled(true);
                mNextView.setEnabled(false);
            } else
            {
                mPrevView.setEnabled(true);
                mNextView.setEnabled(true);
            }
        }

        mWebView.loadUrl(trueVRParams.url);
        mWebView.setBackgroundColor(getResources().getColor(R.color.black));

        if (DailyTextUtils.isTextEmpty(trueVRParams.name) == true)
        {
            mProductNameTextView.setVisibility(View.INVISIBLE);
        } else
        {
            mProductNameTextView.setVisibility(View.VISIBLE);
            mProductNameTextView.setText(trueVRParams.name);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.prevView:
                setTrueViewPage(mCurrentPage - 1);
                break;

            case R.id.nextView:
                setTrueViewPage(mCurrentPage + 1);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    class CupixEventListener
    {
        @JavascriptInterface
        public void update(String eventType, String eventName, String arg)
        {
            //            1. Browser spec.을 체크하여 지원하지 않는 경우 넘어옵니다.
            //            Event Type: "ERROR"
            //            Event Name: "UNSUPPORTED_BROWSER"
            //            Argument: reason
            //
            //            2. House가 unpublished이거나 존재하지 않는등의 이유로 로드를 할수 없는 경우 넘어옵니다.
            //            Event Type: "ERROR"
            //            Event Name: "FAILED_TO_LOAD_PLAYER"
            //            Argument: errorMessage
            //
            //            3. View mode가 변경 되었을 때 넘어옵니다. View mode는 "view-3D", "view-walk", "view-stereo", "view-reset" 4가지 이며 stereo가 VR 모드입니다.
            //            Event Type: "INFO"
            //            Event Name: "VIEW_MODE_CHANGED"
            //            Argument: viewMode

            if (mWebViewLayout == null)
            {
                return;
            }

            mWebViewLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    switch (eventType)
                    {
                        case "ERROR":
                            switch (eventName)
                            {
                                case "UNSUPPORTED_BROWSER":
                                {
                                    TrueVRParams trueVRParams = mTrueVRParamsList.get(mCurrentPage);
                                    Crashlytics.logException(new Exception("Unsupported browser : " + Build.MODEL + ", " + getWebViewVersion()));

                                    showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null//
                                        , new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                TrueVRActivity.this.finish();
                                            }
                                        });
                                    break;
                                }

                                case "FAILED_TO_LOAD_PLAYER":
                                {
                                    TrueVRParams trueVRParams = mTrueVRParamsList.get(mCurrentPage);
                                    Crashlytics.logException(new Exception("Failed load True VR : " + mPlaceType.name() + ", " + mPlaceIndex + ", " + trueVRParams.name + ", " + trueVRParams.url));

                                    showSimpleDialog(null, getString(R.string.message_truevr_failed_load_truevr), getString(R.string.dialog_btn_text_confirm), null//
                                        , new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                TrueVRActivity.this.finish();
                                            }
                                        });
                                    break;
                                }
                            }
                            break;

                        case "INFO":
                            switch (eventName)
                            {
                                case "VIEW_MODE_CHANGED":
                                    switch (arg)
                                    {
                                        case "view-stereo":
                                            if (mWebViewLayout != null)
                                            {
                                                mWebViewLayout.setVisibility(View.INVISIBLE);
                                            }
                                            break;

                                        case "view-reset":
                                        case "view-3D":
                                        case "view-walk":
                                        default:
                                            if (mWebViewLayout != null)
                                            {
                                                mWebViewLayout.setVisibility(View.VISIBLE);
                                            }
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                }
            });
        }
    }
}
