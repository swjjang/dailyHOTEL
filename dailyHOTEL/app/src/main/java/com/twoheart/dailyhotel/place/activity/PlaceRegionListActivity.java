package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.widget.DailyToast;

public abstract class PlaceRegionListActivity extends BaseActivity
{
    protected abstract void initPrepare();

    protected abstract void initIntent(Intent intent);

    protected abstract void initTabLayout(TabLayout tabLayout);

    protected abstract void initToolbar(View toolbar);

    protected abstract void initViewPager(TabLayout tabLayout);

    protected abstract void showSearch();

    protected abstract void requestRegionList();

    protected abstract void updateTermsOfLocationLayout();

    public enum Region
    {
        DOMESTIC,
        GLOBAL
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.hold);

        super.onCreate(savedInstanceState);

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

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                updateTermsOfLocationLayout();

                searchMyLocation();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                updateTermsOfLocationLayout();

                if (resultCode == Activity.RESULT_OK)
                {
                    searchMyLocation();
                }
                break;
            }
        }
    }

    protected void searchMyLocation()
    {
        lockUI();

        DailyLocationFactory.getInstance(PlaceRegionListActivity.this).startLocationMeasure(this, null, new DailyLocationFactory.LocationListenerEx()
        {
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                Intent intent = PermissionManagerActivity.newInstance(PlaceRegionListActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockUI();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProviderDisabled(String provider)
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                DailyLocationFactory.getInstance(PlaceRegionListActivity.this).stopLocationMeasure();

                PlaceRegionListActivity.this.showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                    , getString(R.string.dialog_msg_used_gps)//
                    , getString(R.string.dialog_btn_text_dosetting)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        }
                    }, null, false);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                DailyLocationFactory.getInstance(PlaceRegionListActivity.this).stopLocationMeasure();

                if (location == null)
                {
                    DailyToast.showToast(PlaceRegionListActivity.this, R.string.message_failed_mylocation, Toast.LENGTH_SHORT);
                } else
                {
                    // Location
                    Intent intent = new Intent();
                    intent.putExtra(NAME_INTENT_EXTRA_DATA_LOCATION, location);
                    setResult(RESULT_ARROUND_SEARCH_LIST, intent);
                    finish();
                }
            }
        });
    }
}