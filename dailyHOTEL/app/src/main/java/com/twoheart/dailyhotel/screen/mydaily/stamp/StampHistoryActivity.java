package com.twoheart.dailyhotel.screen.mydaily.stamp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.StampHistory;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.ArrayList;
import java.util.List;

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

        String stampDate1 = DailyPreference.getInstance(this).getRemoteConfigStampDate1();
        String stampDate2 = DailyPreference.getInstance(this).getRemoteConfigStampDate2();
        String stampDate3 = DailyPreference.getInstance(this).getRemoteConfigStampDate3();

        mStampHistoryLayout.setStampDate(stampDate1, stampDate2, stampDate3);


//        List<StampHistory> stampHistoryList = new ArrayList<>();
//
//        stampHistoryList.add(new StampHistory("호텔 카푸치노", "2017-05-29T09:00:00+09:00", 5));
//        stampHistoryList.add(new StampHistory("그랜드 워커힐 서울 (구 쉐라톤 그랜드 워커힐) 업장명 두줄까지 노출", "2017-05-01T09:00:00+09:00", 4));
//        stampHistoryList.add(new StampHistory("더 플라자 호텔", "2017-04-26T09:00:00+09:00", 3));
//        stampHistoryList.add(new StampHistory("핸드픽트 호텔 서울", "2017-04-22T09:00:00+09:00", 2));
//        stampHistoryList.add(new StampHistory("서울신라호텔", "2017-04-17T09:00:00+09:00", 1));
//
//        mStampHistoryLayout.setHistoryList(stampHistoryList);
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
        public void onStampHistoryClick(StampHistory stampHistory)
        {

        }

        @Override
        public void finish()
        {
            StampHistoryActivity.this.finish();
        }
    };
}