package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public abstract class PlaceRegionListActivity extends BaseActivity
{
    protected abstract void initPrepare();

    protected abstract void initIntent(Intent intent);

    protected abstract void initTabLayout(TabLayout tabLayout);

    protected abstract void initToolbar(View toolbar);

    protected abstract void initViewPager(TabLayout tabLayout);

    protected abstract void showSearch();

    protected abstract void requestRegionList();

    public interface OnUserActionListener
    {
        void onRegionClick(Province province);
    }

    public enum Region
    {
        DOMESTIC,
        GLOBAL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        initPrepare();

        setContentView(R.layout.activity_region_list);

        initIntent(getIntent());

        // 지역로딩시에 백버튼 누르면 종료되도록 수정
        setLockUICancelable(true);
        initLayout();
    }

    protected void initLayout()
    {
        initToolbar();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        initTabLayout(tabLayout);
        initViewPager(tabLayout);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        initToolbar(toolbar);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_SEARCH:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }
        }
    }
}