package com.twoheart.dailyhotel.screen.mydaily.bonus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.BonusTermActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class BonusActivity extends BaseActivity
{
    private static final int REQUEST_ACTIVITY_INVITEFRIENDS = 10000;
    private static final int REQUEST_ACTIVITY_TERMS = 10001;

    private String mRecommendCode;
    private String mName;

    private BonusLayout mBonusLayout;
    private BonusNetworkController mNetworkController;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, BonusActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mBonusLayout = new BonusLayout(this, mOnEventListener);
        mNetworkController = new BonusNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mBonusLayout.onCreateView(R.layout.activity_bonus));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(BonusActivity.this).recordScreen(this, AnalyticsManager.Screen.BONUS, null);

        if (DailyHotel.isLogin() == false)
        {
            showLoginDialog();
        } else
        {
            lockUI();
            mNetworkController.requestProfileBenefit();
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

        String title = this.getResources().getString(R.string.dialog_notice2);
        String message = this.getResources().getString(R.string.message_you_can_check_the_bonus_after_login);
        String positive = this.getResources().getString(R.string.dialog_btn_text_yes);
        String negative = this.getResources().getString(R.string.dialog_btn_text_no);

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        }, null, true);
    }

    private BonusLayout.OnEventListener mOnEventListener = new BonusLayout.OnEventListener()
    {
        @Override
        public void onInviteFriends()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = InviteFriendsActivity.newInstance(BonusActivity.this, mRecommendCode, mName);
            startActivityForResult(intent, REQUEST_ACTIVITY_INVITEFRIENDS);

            AnalyticsManager.getInstance(BonusActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, //
                AnalyticsManager.Action.INVITE_FRIEND_CLICKED, AnalyticsManager.Label.CREDIT_MANAGEMENT, null);
        }

        @Override
        public void onBonusGuide()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(BonusActivity.this, BonusTermActivity.class);
            startActivityForResult(intent, REQUEST_ACTIVITY_TERMS);
        }

        @Override
        public void finish()
        {
            BonusActivity.this.finish();
        }
    };

    private BonusNetworkController.OnNetworkControllerListener mNetworkControllerListener = new BonusNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserInformation(String recommendCode, String name, boolean isExceedBonus)
        {
            mName = name;
            mRecommendCode = recommendCode;
            mBonusLayout.setBottomLayoutVisible(isExceedBonus == false);
        }

        @Override
        public void onBonusHistoryList(List<Bonus> list)
        {
            mBonusLayout.setData(list);

            unLockUI();
        }

        @Override
        public void onBonus(int bonus)
        {
            if (bonus < 0)
            {
                bonus = 0;
            }

            mBonusLayout.setBonus(bonus);
        }

        @Override
        public void onError(Throwable e)
        {
            BonusActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            BonusActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            BonusActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            BonusActivity.this.onErrorResponse(call, response);
            finish();
        }
    };
}