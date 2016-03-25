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
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

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
    protected void onStart()
    {
        super.onStart();

        requestSearchResultList();
    }

    @Override
    public void onBackPressed()
    {
        finish(RESULT_CANCELED);
    }

    protected void finish(int resultCode)
    {
        //        if (mResultListLayout.getVisibility() == View.VISIBLE)
        //        {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, getKeyword());
        setResult(resultCode, intent);
        //        } else
        //        {
        //            setResult(resultCode);
        //        }

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
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish(resultCode);
                }
                break;
            }
        }
    }

    protected void showCallDialog(String message)
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
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString())));
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

        if (Util.isTextEmpty(message) == true)
        {
            message = getString(R.string.dialog_msg_call);
        }

        showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }
}
