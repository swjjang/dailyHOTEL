package com.twoheart.dailyhotel.place.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.place.networkcontroller.PlaceMainNetworkController;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.Util;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public abstract class PlaceMainActivity extends BaseActivity
{
    protected boolean mDontReloadAtOnResume, mIsDeepLink;
    protected ViewType mViewType = ViewType.LIST;
    protected TodayDateTime mTodayDateTime;

    protected PlaceViewItem mPlaceViewItemByLongPress;
    protected int mListCountByLongPress;
    protected View mViewByLongPress;

    protected PlaceMainLayout mPlaceMainLayout;
    protected PlaceMainNetworkController mPlaceMainNetworkController;

    DailyLocationFactory mDailyLocationFactory;

    protected abstract PlaceMainLayout getPlaceMainLayout(Context context);

    protected abstract PlaceMainNetworkController getPlaceMainNetworkController(Context context);

    protected abstract void onRegionActivityResult(int resultCode, Intent data);

    protected abstract void onCalendarActivityResult(int resultCode, Intent data);

    protected abstract void onFilterActivityResult(int resultCode, Intent data);

    protected abstract void onLocationFailed();

    protected abstract void onLocationProviderDisabled();

    protected abstract void onLocationChanged(Location location);

    protected abstract PlaceCuration getPlaceCuration();

    protected abstract void changeViewType();

    protected abstract void onPlaceDetailClickByLongPress(View view, PlaceViewItem placeViewItem, int listCount);

    protected abstract void onRegionClick();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mPlaceMainLayout = getPlaceMainLayout(this);
        mPlaceMainNetworkController = getPlaceMainNetworkController(this);

        setContentView(mPlaceMainLayout.onCreateView(R.layout.activity_place_main));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isFinishing() == true)
        {
            return;
        }

        if (mDontReloadAtOnResume == true)
        {
            mDontReloadAtOnResume = false;
        } else
        {
            lockUI();

            mPlaceMainNetworkController.requestDateTime();
        }

        if (Util.supportPreview(this) == true)
        {
            if (mPlaceMainLayout.getBlurVisibility() == true)
            {
                mPlaceMainLayout.setBlurVisibility(this, false);
            } else
            {
                // View 타입이 리스트일때만
                if (mViewType == ViewType.LIST)
                {
                    int count = DailyPreference.getInstance(this).getCountPreviewGuide() + 1;

                    if (count == 2)
                    {
                        showPreviewGuide();
                    } else if (count > 2)
                    {
                        return;
                    }

                    DailyPreference.getInstance(this).setCountPreviewGuide(count);
                }
            }
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mDontReloadAtOnResume = true;

        //        if (mPlaceMainLayout.getBlurVisibility() == false)
        //        {
        //            new Handler().postDelayed(new Runnable()
        //            {
        //                @Override
        //                public void run()
        //                {
        //                    mPlaceMainLayout.showAppBarLayout(false);
        //                }
        //            }, 200);
        //        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    public void onBackPressed()
    {
        // 맵인 경우에 하단에 정보를 보고 있으면 백키를 누를 경우 정보를 사라지게 해준다.(편의성 제공)
        if (mViewType == ViewType.MAP)
        {
            try
            {
                if (mPlaceMainLayout.getCurrentPlaceListFragment().getPlaceListLayout().getListMapFragment().isShowPlaceInformation() == true)
                {
                    mPlaceMainLayout.getCurrentPlaceListFragment().getPlaceListLayout().getListMapFragment().clickMap();
                } else
                {
                    changeViewType();
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());

                changeViewType();
            }

            return;
        }

        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (isFinishing() == true)
        {
            return;
        }

        unLockUI();

        switch (requestCode)
        {
            // 지역을 선택한 후에 되돌아 온경우.
            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            {
                if (mIsDeepLink == false)
                {
                    mDontReloadAtOnResume = true;
                } else
                {
                    mDontReloadAtOnResume = false;
                    mIsDeepLink = false;
                }

                onRegionActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_CALENDAR:
            {
                mDontReloadAtOnResume = true;

                onCalendarActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_STAYCURATION:
            {
                mDontReloadAtOnResume = true;

                onFilterActivityResult(resultCode, data);
                break;
            }

            case CODE_REQUEST_ACTIVITY_GOURMETCURATION:
            {
                mDontReloadAtOnResume = true;

                onFilterActivityResult(resultCode, data);
                break;
            }

            case CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                mDontReloadAtOnResume = true;

                if (mViewType == ViewType.MAP)
                {
                    onActivityCurrentFragmentResult(requestCode, resultCode, data);
                } else
                {
                    searchMyLocation();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                mDontReloadAtOnResume = true;

                if (mViewType == ViewType.MAP)
                {
                    onActivityCurrentFragmentResult(requestCode, resultCode, data);
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

            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case CODE_REQUEST_ACTIVITY_COLLECTION:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        setResult(resultCode);
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_GO_HOME:
                        setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_GO_REGION_LIST:
                        onRegionClick();
                        break;

                    default:
                        if (mIsDeepLink == false)
                        {
                            switch (resultCode)
                            {
                                case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:

                                    if (data == null)
                                    {
                                        mDontReloadAtOnResume = false;
                                    } else
                                    {
                                        if (data.hasExtra(StayDetailActivity.INTENT_EXTRA_DATA_WISH) == true//
                                            || data.hasExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH) == true)
                                        {
                                            mDontReloadAtOnResume = true;

                                            onActivityCurrentFragmentResult(requestCode, resultCode, data);
                                        } else
                                        {
                                            mDontReloadAtOnResume = false;
                                        }
                                    }
                                    break;

                                case CODE_RESULT_ACTIVITY_REFRESH:
                                case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                                    mDontReloadAtOnResume = false;
                                    break;

                                default:
                                    mDontReloadAtOnResume = true;
                                    break;
                            }
                        } else
                        {
                            mIsDeepLink = false;

                            mDontReloadAtOnResume = false;
                        }
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PREVIEW:
                mDontReloadAtOnResume = true;

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        Observable.create(new ObservableOnSubscribe<Object>()
                        {
                            @Override
                            public void subscribe(ObservableEmitter<Object> e) throws Exception
                            {
                                onPlaceDetailClickByLongPress(mViewByLongPress, mPlaceViewItemByLongPress, mListCountByLongPress);
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                        if (data == null)
                        {
                            mDontReloadAtOnResume = false;
                        } else
                        {
                            onActivityCurrentFragmentResult(requestCode, resultCode, data);
                        }
                        break;
                }
                break;

            case Constants.CODE_REQUEST_ACTIVITY_WISH_DIALOG:
                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_REFRESH)
                {
                    mDontReloadAtOnResume = false;
                } else
                {
                    mDontReloadAtOnResume = true;

                    onActivityCurrentFragmentResult(requestCode, resultCode, data);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    protected void refreshCurrentFragment(boolean isClearList)
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (mPlaceMainLayout.getPlaceListFragment() == null)
        {
            Util.restartApp(this);
            return;
        }

        if (isClearList == true)
        {
            for (PlaceListFragment placeListFragment : mPlaceMainLayout.getPlaceListFragment())
            {
                // 메인의 클리어 리스트의 경우 타화면에 영향을 줌으로 전체 리스트 데이터를 클리어함
                placeListFragment.clearList();
                // 해당 리스트의 viewType이 gone일 수 있음, 해당 경우 메인의 viewType을 따름
                placeListFragment.setViewType(mViewType);
            }
        }

        PlaceListFragment currentListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();
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

        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationFactory(this);
        }

        if (mDailyLocationFactory.measuringLocation() == true)
        {
            return;
        }

        mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
        {
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                Intent intent = PermissionManagerActivity.newInstance(PlaceMainActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
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
            public void onProviderEnabled()
            {
                mDailyLocationFactory.startLocationMeasure(null, new DailyLocationFactory.OnLocationListener()
                {
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
                    public void onAlreadyRun()
                    {

                    }

                    @Override
                    public void onLocationChanged(Location location)
                    {
                        unLockUI();

                        if (isFinishing() == true)
                        {
                            return;
                        }

                        mDailyLocationFactory.stopLocationMeasure();

                        PlaceMainActivity.this.onLocationChanged(location);
                    }

                    @Override
                    public void onCheckSetting(ResolvableApiException exception)
                    {
                        unLockUI();

                        try
                        {
                            exception.startResolutionForResult(PlaceMainActivity.this, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                        } catch (Exception e)
                        {

                        }
                    }
                });
            }

            @Override
            public void onProviderDisabled()
            {
                unLockUI();

                if (isFinishing() == true)
                {
                    return;
                }

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                mDailyLocationFactory.stopLocationMeasure();

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
        });
    }

    protected void setScrollListTop()
    {
        if (isFinishing() == true)
        {
            return;
        }

        PlaceListFragment placeListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();
        if (placeListFragment != null)
        {
            placeListFragment.setScrollListTop();
        }
    }

    private void onActivityCurrentFragmentResult(int requestCode, int resultCode, Intent data)
    {
        PlaceListFragment currentListFragment = mPlaceMainLayout.getCurrentPlaceListFragment();

        if (currentListFragment != null)
        {
            currentListFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showPreviewGuide()
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_preview_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        View confirmTextView = dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }
}
