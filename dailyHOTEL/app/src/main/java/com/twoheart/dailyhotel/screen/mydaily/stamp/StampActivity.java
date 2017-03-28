package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.Stamp;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import retrofit2.Call;
import retrofit2.Response;

public class StampActivity extends BaseActivity
{
    StampLayout mStampLayout;
    private StampNetworkController mNetworkController;
    private CallScreen mCallScreen;

    public enum CallScreen
    {
        MYDAILY,
        THANKYOU,
        EVENT,
    }

    public static Intent newInstance(Context context, CallScreen callScreen)
    {
        Intent intent = new Intent(context, StampActivity.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN, callScreen);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        mCallScreen = (CallScreen) intent.getSerializableExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN);

        mStampLayout = new StampLayout(this, mOnEventListener);
        mNetworkController = new StampNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mStampLayout.onCreateView(R.layout.activity_stamp));

        String stampDate1 = DailyPreference.getInstance(this).getRemoteConfigStampDate1();

        mStampLayout.setStampDate(stampDate1);

        if (DailyHotel.isLogin() == true)
        {
            mStampLayout.setLogin(true);
            mStampLayout.setStampHistoryEnabled(true);
        } else
        {
            mStampLayout.setLogin(false);
            mStampLayout.setStampHistoryEnabled(false);
        }

        boolean isBenefitAlarm = DailyUserPreference.getInstance(StampActivity.this).isBenefitAlarm();

        mStampLayout.setPushLayout(isBenefitAlarm == false);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(StampActivity.this).recordScreen(this, AnalyticsManager.Screen.STAMP_DETAIL, null);

        if (DailyPreference.getInstance(this).isRemoteConfigStampEnabled() == true)
        {
            if (DailyHotel.isLogin() == true)
            {
                lockUI();
                mNetworkController.requestUserStamps(false);
            } else
            {
                // 로그인 하지 않은 경우 멘트가 다름
                mStampLayout.setNights(-1);
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
            case Constants.CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    mStampLayout.setLogin(DailyHotel.isLogin());
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_EVENT:
            case Constants.CODE_REQUEST_ACTIVITY_STAMP_TERMS:
            case Constants.CODE_REQUEST_ACTIVITY_STAMP_HISTORY:
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
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startLogin();
        }

        @Override
        public void onStampEventDetailClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mCallScreen == CallScreen.EVENT)
            {
                finish();
            } else
            {
                startActivityForResult(EventWebActivity.newInstance(StampActivity.this, EventWebActivity.SourceType.STAMP//
                    , "http://m.dailyhotel.co.kr/banner/dailystamp_home", getString(R.string.label_stamp_event_title)), Constants.CODE_RESULT_ACTIVITY_EVENT);
            }

            AnalyticsManager.getInstance(StampActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.STAMP_DETAIL_CLICK, AnalyticsManager.Label.STAMP_DETAIL, null);
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

            AnalyticsManager.getInstance(StampActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.STAMP_HISTORY_CLICK, AnalyticsManager.ValueType.EMPTY, null);
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
            DailyUserPreference.getInstance(StampActivity.this).setBenefitAlarm(isAgree);
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
        public void onUserStamps(Stamp stamp)
        {
            unLockUI();

            if (stamp == null)
            {
                return;
            }

            mStampLayout.setNights(stamp.count);

            if (stamp.count > 0)
            {
                mStampLayout.setStampHistoryEnabled(true);
            } else
            {
                mStampLayout.setStampHistoryEnabled(false);
            }
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