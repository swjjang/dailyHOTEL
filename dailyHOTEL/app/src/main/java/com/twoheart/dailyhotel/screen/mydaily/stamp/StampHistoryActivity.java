package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;

import retrofit2.Call;
import retrofit2.Response;

public class StampHistoryActivity extends BaseActivity
{
    StampHistoryLayout mStampHistoryLayout;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StampHistoryActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mStampHistoryLayout = new StampHistoryLayout(this, mOnEventListener);

        setContentView(mStampHistoryLayout.onCreateView(R.layout.activity_stamp_history));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //        AnalyticsManager.getInstance(StampActivity.this).recordScreen(this, AnalyticsManager.Screen.STAMP, null);

        if (DailyPreference.getInstance(this).getRemoteConfigStampEnabled() == true)
        {
            //                lockUI();
            //                mNetworkController.requestStamp();
        } else
        {
            showFinishDialog();
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    finish();
                }
                break;
            }

            default:
                break;
        }
    }

    private void showFinishDialog()
    {
        showSimpleDialog(null, getString(R.string.message_stamp_finish_stamp), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                StampHistoryActivity.this.onBackPressed();
            }
        });
    }

    private StampHistoryLayout.OnEventListener mOnEventListener = new StampHistoryLayout.OnEventListener()
    {

        @Override
        public void onHomeClick()
        {
            setResult(CODE_RESULT_ACTIVITY_GO_HOME);
            finish();
        }

        @Override
        public void finish()
        {
            StampHistoryActivity.this.finish();
        }
    };

    private StampNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StampNetworkController.OnNetworkControllerListener()
    {

        @Override
        public void onBenefitAgreement(boolean isAgree, String updateDate)
        {

        }

        @Override
        public void onError(Throwable e)
        {
            StampHistoryActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StampHistoryActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StampHistoryActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StampHistoryActivity.this.onErrorResponse(call, response);
            finish();
        }
    };
}