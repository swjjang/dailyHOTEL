package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.Stamp;
import com.twoheart.dailyhotel.network.model.StampHistory;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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

        String stampDate1 = DailyRemoteConfigPreference.getInstance(this).getRemoteConfigStampDate2();
        String stampDate2 = DailyRemoteConfigPreference.getInstance(this).getRemoteConfigStampDate3();

        mStampHistoryLayout.setStampDate(stampDate1, stampDate2);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(StampHistoryActivity.this).recordScreen(this, AnalyticsManager.Screen.STAMP_HISTORY, null);

        if (DailyRemoteConfigPreference.getInstance(this).isRemoteConfigStampEnabled() == true)
        {
            lockUI();
            DailyMobileAPI.getInstance(this).requestUserStamps(mNetworkTag, true, mStampHistoryCallback);
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

    void onStampHistory(Stamp stamp)
    {
        unLockUI();

        if (stamp == null)
        {
            return;
        }

        mStampHistoryLayout.setHistoryList(stamp.list);
    }

    private StampHistoryLayout.OnEventListener mOnEventListener = new StampHistoryLayout.OnEventListener()
    {

        @Override
        public void onHomeClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            setResult(CODE_RESULT_ACTIVITY_GO_HOME);
            finish();
        }

        @Override
        public void onStampHistoryClick(StampHistory stampHistory)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String deepLink = "dailyhotel://dailyhotel.co.kr?vc=12&v=bd&ri=" + stampHistory.reservationIdx + "&pt=stay";

            Intent intent = new Intent(StampHistoryActivity.this, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(deepLink));

            startActivity(intent);
        }

        @Override
        public void finish()
        {
            StampHistoryActivity.this.finish();
        }
    };

    private retrofit2.Callback mStampHistoryCallback = new retrofit2.Callback<BaseDto<Stamp>>()
    {
        @Override
        public void onResponse(Call<BaseDto<Stamp>> call, Response<BaseDto<Stamp>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                BaseDto<Stamp> baseDto = response.body();

                if (baseDto.msgCode == 100)
                {
                    onStampHistory(baseDto.data);
                } else
                {
                    onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                }
            } else
            {
                onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<Stamp>> call, Throwable t)
        {
            onError(t);
        }
    };
}