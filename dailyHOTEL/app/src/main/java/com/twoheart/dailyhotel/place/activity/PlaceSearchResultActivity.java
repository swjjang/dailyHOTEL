package com.twoheart.dailyhotel.place.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

public abstract class PlaceSearchResultActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_DATA_KEYWORD = "keyword";

    protected ViewType mViewType = ViewType.LIST;

    protected PlaceSearchResultLayout mPlaceSearchResultLayout;

    protected abstract PlaceSearchResultLayout getPlaceSearchResultLayout(Context context);

    protected abstract void onCalendarActivityResult(int requestCode, int resultCode, Intent data);

    protected abstract void onCurationActivityResult(int requestCode, int resultCode, Intent data);

    protected abstract void onLocationFailed();

    protected abstract void onLocationProviderDisabled();

    protected abstract void onLocationChanged(Location location);

    protected abstract void initIntent(Intent intent);

    protected abstract void initLayout();

    protected abstract Keyword getKeyword();

    protected abstract PlaceCuration getPlaceCuration();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mPlaceSearchResultLayout = getPlaceSearchResultLayout(this);

        initIntent(getIntent());

        setContentView(mPlaceSearchResultLayout.onCreateView(R.layout.activity_search_result));

        initLayout();
    }

    @Override
    public void onBackPressed()
    {
        finish(RESULT_CANCELED);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void finish(int resultCode)
    {
        if (mPlaceSearchResultLayout != null && mPlaceSearchResultLayout.isEmtpyLayout() == false)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_DATA_KEYWORD, getKeyword());
            setResult(resultCode, intent);
        } else
        {
            setResult(resultCode);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                onCalendarActivityResult(requestCode, resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAYCURATION:
            {
                onCurationActivityResult(requestCode, resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_GOURMETCURATION:
            {
                onCurationActivityResult(requestCode, resultCode, data);
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                if (mViewType == ViewType.MAP)
                {
                    PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
                    placeListFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (mViewType == ViewType.MAP)
                {
                    PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
                    placeListFragment.onActivityResult(requestCode, resultCode, data);
                } else
                {
                    if (resultCode == Activity.RESULT_OK)
                    {
                        searchMyLocation();
                    } else
                    {
                        onLocationFailed();
                    }
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            {
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    finish(resultCode);
                }
                break;
            }
        }
    }

    protected void refreshCurrentFragment(boolean isClearList)
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (isClearList == true)
        {
            for (PlaceListFragment placeListFragment : mPlaceSearchResultLayout.getPlaceListFragment())
            {
                // 메인의 클리어 리스트의 경우 타화면에 영향을 줌으로 전체 리스트 데이터를 클리어함
                placeListFragment.clearList();
                // 해당 리스트의 viewType이 gone일 수 있음, 해당 경우 메인의 viewType을 따름
                placeListFragment.setViewType(mViewType);
            }
        }

        PlaceListFragment currentListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
        if (currentListFragment != null)
        {
            currentListFragment.setPlaceCuration(getPlaceCuration());
            currentListFragment.refreshList(true);
        }
    }

    protected void searchMyLocation()
    {
        if (isFinishing() || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        DailyLocationFactory.getInstance(this).startLocationMeasure(this, null, new DailyLocationFactory.LocationListenerEx()
        {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                if (Util.isOverAPI23() == true)
                {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true)
                    {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:com.twoheart.dailyhotel"));
                        startActivityForResult(intent, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                    } else
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION);
                    }
                }
            }

            @Override
            public void onFailed()
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                onLocationFailed();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                unLockUI();

            }

            @Override
            public void onProviderEnabled(String provider)
            {
                unLockUI();
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
                DailyLocationFactory.getInstance(PlaceSearchResultActivity.this).stopLocationMeasure();

                showSimpleDialog(getString(R.string.dialog_title_used_gps)//
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
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onLocationProviderDisabled();
                        }
                    }, false);
            }

            @Override
            public void onLocationChanged(Location location)
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                DailyLocationFactory.getInstance(PlaceSearchResultActivity.this).stopLocationMeasure();

                PlaceSearchResultActivity.this.onLocationChanged(location);
            }
        });
    }

    protected void setScrollListTop()
    {
        if (isFinishing() == true)
        {
            return;
        }

        PlaceListFragment placeListFragment = mPlaceSearchResultLayout.getCurrentPlaceListFragment();
        if (placeListFragment != null)
        {
            placeListFragment.setScrollListTop();
        }
    }

    protected void showCallDialog()
    {
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(PlaceSearchResultActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(PlaceSearchResultActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(PlaceSearchResultActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        String operatingTimeMessage = DailyPreference.getInstance(this).getOperationTimeMessage(this);

        showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage,//
            getString(R.string.dialog_btn_call), getString(R.string.dialog_btn_text_cancel), positiveListener, null, null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    releaseUiComponent();
                }
            }, true);
    }

    protected void recordEventSearchResultByLocation(String address, boolean isEmpty)
    {
        String action = (isEmpty == true) ? AnalyticsManager.Action.AROUND_SEARCH_NOT_FOUND : AnalyticsManager.Action.AROUND_SEARCH_CLICKED;

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
            , action, address, null);
    }

    protected void recordEventSearchResultByRecentKeyword(Keyword keyword, boolean isEmpty)
    {
        String action = (isEmpty == true) ? AnalyticsManager.Action.RECENT_KEYWORD_NOT_FOUND : AnalyticsManager.Action.RECENT_KEYWORD;

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
            , action, keyword.name, null);
    }

    protected void recordEventSearchResultByKeyword(Keyword keyword, boolean isEmpty)
    {
        String action = (isEmpty == true) ? AnalyticsManager.Action.KEYWORD_NOT_FOUND : AnalyticsManager.Action.KEYWORD;

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(AnalyticsManager.Category.SEARCH//
            , action, keyword.name, null);
    }

    protected void recordEventSearchResultByAutoSearch(Keyword keyword, String inputText, boolean isEmpty)
    {
        String category = (isEmpty == true) ? AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND : AnalyticsManager.Category.AUTO_SEARCH;

        AnalyticsManager.getInstance(PlaceSearchResultActivity.this).recordEvent(category//
            , keyword.name, inputText, null);
    }
}
