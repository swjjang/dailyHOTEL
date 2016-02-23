package com.twoheart.dailyhotel.screen.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

public class SortFilterActivity extends BaseActivity
{
    private TYPE mType;

    public static int FLAG_HOTEL_FILTER_PERSON = 0x01000000;
    public static int FLAG_HOTEL_FILTER_BED_TYPE = 0x02000000;
    public static int FLAG_GOURMET_FILTER_CATEGORY = 0x04000000;

    public static Intent newInstance(Context context, TYPE type, Province province)
    {
        Intent intent = new Intent(context, SortFilterActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE, type.toString());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        // 호텔 인지 고메인지
        mType = TYPE.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACETYPE));
        Province selectedProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);

        if (mType == null)
        {
            Util.restartApp(this);
            return;
        }

        // 국내로 시작하는지 헤외로 시작하는지
        // 고메인 경우에는 해외 지역이 없기 때문에 기존과 동일하게?

        initLayout(mType);
    }

    private void initLayout(TYPE type)
    {
        setContentView(R.layout.activity_sort_filter);

        View view = findViewById(R.id.sortFilterLayout);

        switch (type)
        {
            case HOTEL:
                initHotel(view);
                break;

            case FNB:
                initGourmet(view);
                break;
        }
    }

    private void initHotel(View view)
    {

    }

    private void initGourmet(View view)
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }
}