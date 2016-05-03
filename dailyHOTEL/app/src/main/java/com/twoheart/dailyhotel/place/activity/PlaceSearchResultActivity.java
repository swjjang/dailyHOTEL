package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

public abstract class PlaceSearchResultActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";

    protected PlaceSearchResultLayout mPlaceSearchResultLayout;

    protected abstract void initIntent(Intent intent);

    protected abstract void requestSearchResultList();

    protected abstract Keyword getKeyword();

    protected abstract PlaceSearchResultLayout getLayout();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPlaceSearchResultLayout = getLayout();

        if (mPlaceSearchResultLayout == null)
        {
            finish();
            return;
        }

        setContentView(mPlaceSearchResultLayout.onCreateView(R.layout.activity_search_result));

        initIntent(getIntent());

        initContents();
    }

    protected void initContents()
    {

    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        requestSearchResultList();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onBackPressed()
    {
        finish(RESULT_CANCELED);
    }

    protected void finish(int resultCode)
    {
        if (mPlaceSearchResultLayout != null && mPlaceSearchResultLayout.isEmtpyLayout() == false)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, getKeyword());
            setResult(resultCode, intent);
        } else
        {
            setResult(resultCode);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish(resultCode);
                }
                break;
            }
        }
    }

    protected void showCallDialog()
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(PlaceSearchResultActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(PlaceSearchResultActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(PlaceSearchResultActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        String operatingTimeMessage = DailyPreference.getInstance(this).getOperationTimeMessage(this);

        showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }
}
