package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.BonusTermActivity;

import java.util.List;

public class BonusActivity extends BaseActivity
{
    private static final int REQUEST_ACTIVITY_INVITEFRIENDS = 10000;

    private String mRecommendCode;
    private String mName;

    private BonusLayout mBonusLayout;
    private BonusNetworkController mNetworkController;

    private boolean mDontReloadAtOnResume;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, BonusActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mBonusLayout = new BonusLayout(this, mOnEventListener);
        mNetworkController = new BonusNetworkController(this, mNetworkTag, mNetworkControllerListener);

        setContentView(mBonusLayout.onCreateView(R.layout.activity_bonus));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mDontReloadAtOnResume == true)
        {
            unLockUI();
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();
            mNetworkController.requestBonus();
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        mDontReloadAtOnResume = true;
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void onBonusGuide()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(BonusActivity.this, BonusTermActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
            mBonusLayout.setBottomLayoutVisible(isExceedBonus);
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
            mBonusLayout.setBonus(bonus);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            BonusActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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
        }
    };
}