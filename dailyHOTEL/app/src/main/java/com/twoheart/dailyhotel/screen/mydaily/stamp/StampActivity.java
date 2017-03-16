package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
        mStampLayout.setLogin(DailyHotel.isLogin());

        boolean isBenefitAlarm = DailyPreference.getInstance(StampActivity.this).isUserBenefitAlarm();

        mStampLayout.setPushLayout(isBenefitAlarm == false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        //        AnalyticsManager.getInstance(StampActivity.this).recordScreen(this, AnalyticsManager.Screen.STAMP, null);

        if (DailyPreference.getInstance(this).getRemoteConfigStampEnabled() != true)
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
                if (resultCode == Activity.RESULT_OK)
                {
                    mStampLayout.setLogin(DailyHotel.isLogin());
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAMP_TERMS:
            case CODE_REQUEST_ACTIVITY_STAMP_HISTORY:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;

            default:
                break;
        }
    }

    private void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    private void showFinishDialog()
    {
        if (DailyPreference.getInstance(this).isRemoteConfigStampStayEndEventPopupEnabled() == false)
        {
            return;
        }

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
        public void onLoginClick()
        {
            Intent intent = LoginActivity.newInstance(StampActivity.this);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
        }

        @Override
        public void onStampEventDetailClick()
        {

        }

        @Override
        public void onSettingPushClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            mNetworkController.requestPushBenefit(true);
        }

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
        public void onBenefitAgreement(boolean isAgree, String updateDate)
        {
            DailyPreference.getInstance(StampActivity.this).setUserBenefitAlarm(isAgree);
            AnalyticsManager.getInstance(StampActivity.this).setPushEnabled(isAgree, AnalyticsManager.ValueType.OTHER);

            if (isAgree == true)
            {
                // 혜택 알림 설정이 off --> on 일때
                String title = getResources().getString(R.string.label_setting_alarm);
                String message = getResources().getString(R.string.message_benefit_alarm_on_confirm_format, updateDate);
                String positive = getResources().getString(R.string.dialog_btn_text_confirm);

                showSimpleDialog(title, message, positive, null, null, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        mStampLayout.setPushLayout(false);
                    }
                }, true);

                AnalyticsManager.getInstance(StampActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                    AnalyticsManager.Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);
            }

            unLockUI();
        }

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