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
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.parcel.TrueVRParcel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrueVRActivity extends WebViewActivity implements View.OnClickListener
{
    List<TrueVR> mTrueVRList;
    private TextView mProductNameTextView;
    private TextView mCurrentPageTextView, mTotalPageTextView;
    private View mPageLayout;
    private View mPrevView, mNextView;
    View mWebViewLayout;

    int mPlaceIndex;
    int mCurrentPage;
    PlaceType mPlaceType;

    public static Intent newInstance(Context context, int placeIndex, List<TrueVR> trueVRList, PlaceType placeType, String category)
    {
        Intent intent = new Intent(context, TrueVRActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);

        ArrayList<TrueVRParcel> trueVRParcelList = new ArrayList<>(trueVRList.size());

        for (TrueVR trueVR : trueVRList)
        {
            trueVRParcelList.add(new TrueVRParcel(trueVR));
        }

        intent.putParcelableArrayListExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVR_LIST, trueVRParcelList);
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
        List<TrueVRParcel> trueVRParcelList = intent.getParcelableArrayListExtra(Constants.NAME_INTENT_EXTRA_DATA_TRUEVR_LIST);

        if (trueVRParcelList == null || trueVRParcelList.size() == 0)
        {
            finish();
            return;
        }

        mTrueVRList = new ArrayList<>(trueVRParcelList.size());

        for (TrueVRParcel trueVRParcel : trueVRParcelList)
        {
            mTrueVRList.add(trueVRParcel.getTrueVR());
        }

        mPlaceType = PlaceType.valueOf(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE));
        String category = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CATEGORY);

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

        if (mTrueVRList != null && mTrueVRList.size() == 1)
        {
            mPageLayout.setVisibility(View.GONE);
        } else
        {
            mPageLayout.setVisibility(View.VISIBLE);
        }

        mProductNameTextView = findViewById(R.id.productNameTextView);
        mCurrentPageTextView = findViewById(R.id.currentPageTextView);
        mTotalPageTextView = findViewById(R.id.totalPageTextView);

        mNextView = findViewById(R.id.nextView);
        mPrevView = findViewById(R.id.prevView);

        mNextView.setOnClickListener(this);
        mPrevView.setOnClickListener(this);
    }

    private void setTrueViewPage(int page)
    {
        if (mWebView == null || page >= mTrueVRList.size() || page < 0)
        {
            return;
        }

        int totalPage = mTrueVRList.size();

        TrueVR trueVR = mTrueVRList.get(page);

        if (trueVR == null)
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

        mWebView.loadUrl(trueVR.url);
        mWebView.setBackgroundColor(getResources().getColor(R.color.black));

        if (DailyTextUtils.isTextEmpty(trueVR.name) == true)
        {
            mProductNameTextView.setVisibility(View.INVISIBLE);
        } else
        {
            mProductNameTextView.setVisibility(View.VISIBLE);
            mProductNameTextView.setText(trueVR.name);
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
                                    TrueVR trueVR = mTrueVRList.get(mCurrentPage);
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
                                    TrueVR trueVR = mTrueVRList.get(mCurrentPage);
                                    Crashlytics.logException(new Exception("Failed load True VR : " + mPlaceType.name() + ", " + mPlaceIndex + ", " + trueVR.name + ", " + trueVR.url));

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
