package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyPreference;

import retrofit2.Call;
import retrofit2.Response;

public class StampActivity extends BaseActivity
{
    StampLayout mStampLayout;
    private StampNetworkController mNetworkController;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, StampActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mStampLayout = new StampLayout(this, mOnEventListener);
        mNetworkController = new StampNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mStampLayout.onCreateView(R.layout.activity_stamp));

        String stampDate1 = DailyPreference.getInstance(this).getRemoteConfigStampDate1();
        String stampDate2 = DailyPreference.getInstance(this).getRemoteConfigStampDate2();
        String stampDate3 = DailyPreference.getInstance(this).getRemoteConfigStampDate3();

        mStampLayout.setStampDate(stampDate1, stampDate2, stampDate3);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //        AnalyticsManager.getInstance(StampActivity.this).recordScreen(this, AnalyticsManager.Screen.STAMP, null);

        if (DailyPreference.getInstance(this).getRemoteConfigStampEnabled() == true)
        {
            if (DailyHotel.isLogin() == false)
            {
                //                showLoginDialog();
            } else
            {
                //                lockUI();
                //                mNetworkController.requestStamp();
            }
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

    private void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    private void showLoginDialog()
    {
        // 로그인 필요
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI();
                startLogin();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        };

        String title = getString(R.string.dialog_notice2);
        String message = getString(R.string.message_stamp_you_can_check_the_stamp_after_login);
        String positive = getString(R.string.dialog_btn_text_yes);
        String negative = getString(R.string.dialog_btn_text_no);

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        }, null, true);
    }

    private void showFinishDialog()
    {
        showSimpleDialog(null, getString(R.string.message_stamp_finish_stamp), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                StampActivity.this.onBackPressed();
            }
        });
    }

    private StampLayout.OnEventListener mOnEventListener = new StampLayout.OnEventListener()
    {
        @Override
        public void onStampHistoryClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(StampHistoryActivity.newInstance(StampActivity.this), CODE_REQUEST_ACTIVITY_STAMP_HISTORY);
        }

        @Override
        public void onStampTermsClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(StampTermsActivity.newInstance(StampActivity.this), CODE_REQUEST_ACTIVITY_STAMP_TERMS);
        }

        @Override
        public void finish()
        {
            StampActivity.this.finish();
        }
    };

    private StampNetworkController.OnNetworkControllerListener mNetworkControllerListener = new StampNetworkController.OnNetworkControllerListener()
    {

        @Override
        public void onError(Throwable e)
        {
            StampActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StampActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StampActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StampActivity.this.onErrorResponse(call, response);
            finish();
        }
    };
}