package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.exception.BaseException;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.layout.PlaceReservationDetailLayout;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyLocationFactory;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class PlaceReservationDetailActivity extends BaseActivity
{
    private boolean mDontReload;
    protected int mReservationIndex;
    protected String mImageUrl;
    protected boolean mIsDeepLink;
    protected PlaceBookingDetail mPlaceBookingDetail;
    protected TodayDateTime mTodayDateTime;

    protected PlaceReservationDetailLayout mPlaceReservationDetailLayout;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    protected abstract void requestPlaceReservationDetail(int reservationIndex);

    protected abstract void showCallDialog();

    protected abstract void showShareDialog();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mReservationIndex = bundle.getInt(NAME_INTENT_EXTRA_DATA_BOOKINGIDX);
            mImageUrl = bundle.getString(NAME_INTENT_EXTRA_DATA_URL);
            mIsDeepLink = bundle.getBoolean(NAME_INTENT_EXTRA_DATA_DEEPLINK, false);
        }

        if (mReservationIndex <= 0)
        {
            Util.restartApp(this);
            return;
        }

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.BOOKING_DETAIL, null);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (mReservationIndex <= 0)
        {
            Util.restartApp(this);
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            startLogin();
            return;
        }

        if (mDontReload == false)
        {
            lockUI();

            requestCommonDatetime();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mDontReload == false)
        {
            mDontReload = true;
        }

        if (mPlaceReservationDetailLayout != null && mPlaceReservationDetailLayout.getBlurVisibility() == true)
        {
            mPlaceReservationDetailLayout.setBlurVisibility(this, false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == RESULT_OK)
                {
                    lockUI();

                    requestCommonDatetime();
                } else
                {
                    finish();
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                if (mPlaceReservationDetailLayout != null)
                {
                    searchMyLocation(mPlaceReservationDetailLayout.getMyLocationView());
                }

                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == RESULT_OK)
                {
                    if (mPlaceReservationDetailLayout != null)
                    {
                        searchMyLocation(mPlaceReservationDetailLayout.getMyLocationView());
                    }
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_ZOOMMAP:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL:
            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
            {
                if (resultCode == RESULT_OK)
                {
                    mPlaceBookingDetail.reviewStatusType = PlaceBookingDetail.ReviewStatusType.COMPLETE;
                    mPlaceReservationDetailLayout.updateReviewButtonLayout(mPlaceBookingDetail.reviewStatusType);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_FAQ:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(CODE_RESULT_ACTIVITY_GO_HOME);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mPlaceReservationDetailLayout != null && mPlaceReservationDetailLayout.isExpandedMap() == true)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mPlaceReservationDetailLayout.collapseMap();
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        DailyLocationFactory.getInstance(this).stopLocationMeasure();
        DailyLocationFactory.getInstance(this).clear();
    }

    protected void requestCommonDatetime()
    {
        DailyMobileAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new retrofit2.Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        BaseDto<TodayDateTime> baseDto = response.body();

                        if (baseDto.msgCode == 100)
                        {
                            setCurrentDateTime(baseDto.data);

                            requestUserProfile();
                        } else
                        {
                            PlaceReservationDetailActivity.this.onErrorPopupMessage(baseDto.msgCode, baseDto.msg);
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                        PlaceReservationDetailActivity.this.onError(e);
                        finish();
                    }
                } else
                {
                    PlaceReservationDetailActivity.this.onErrorResponse(call, response);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
            {
                PlaceReservationDetailActivity.this.onError(t);
                finish();
            }
        });
    }

    protected void requestUserProfile()
    {
        DailyMobileAPI.getInstance(this).requestUserProfile(mNetworkTag, new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                            setUserName(jsonObject.getString("name"));

                            requestPlaceReservationDetail(mReservationIndex);
                        } else
                        {
                            String msg = responseJSONObject.getString("msg");
                            DailyToast.showToast(PlaceReservationDetailActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                        PlaceReservationDetailActivity.this.onError(e);
                        finish();
                    }
                } else
                {
                    PlaceReservationDetailActivity.this.onErrorResponse(call, response);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                PlaceReservationDetailActivity.this.onError(t);
                finish();
            }
        });
    }

    private void startLogin()
    {
        showSimpleDialog(null, getString(R.string.message_booking_detail_do_login), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LoginActivity.newInstance(PlaceReservationDetailActivity.this);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    void setCurrentDateTime(TodayDateTime todayDateTime)
    {
        mTodayDateTime = todayDateTime;
    }

    void setUserName(String userName)
    {
        if (mPlaceBookingDetail == null)
        {
            return;
        }

        mPlaceBookingDetail.userName = userName;
    }

    protected void searchMyLocation(ImageView myLocationView)
    {
        lockUI();

        if (DailyLocationFactory.getInstance(this).measuringLocation() == true)
        {
            return;
        }

        DailyLocationFactory.getInstance(this).checkLocationMeasure(this, new DailyLocationFactory.OnCheckLocationListener()
        {
            @Override
            public void onRequirePermission()
            {
                unLockUI();

                Intent intent = PermissionManagerActivity.newInstance(PlaceReservationDetailActivity.this, PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockUI();
            }

            @Override
            public void onProviderEnabled()
            {
                DailyLocationFactory.getInstance(PlaceReservationDetailActivity.this).startLocationMeasure(PlaceReservationDetailActivity.this, myLocationView, new DailyLocationFactory.OnLocationListener()
                {
                    @Override
                    public void onFailed()
                    {
                        unLockUI();
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

                        DailyLocationFactory.getInstance(PlaceReservationDetailActivity.this).stopLocationMeasure();

                        if (mPlaceReservationDetailLayout == null || location == null)
                        {
                            return;
                        }

                        mPlaceReservationDetailLayout.changeLocation(location);
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
                DailyLocationFactory.getInstance(PlaceReservationDetailActivity.this).stopLocationMeasure();

                showSimpleDialog(getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), getString(R.string.dialog_btn_text_dosetting), getString(R.string.dialog_btn_text_cancel), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
                    }
                }, null, true);
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 기존의 BaseActivity에 있는 정보 가져오기
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected void addCompositeDisposable(Disposable disposable)
    {
        if (disposable == null)
        {
            return;
        }

        mCompositeDisposable.add(disposable);
    }

    private void clearCompositeDisposable()
    {
        mCompositeDisposable.clear();
    }

    protected void onHandleError(Throwable throwable)
    {
        unLockUI();

        BaseActivity baseActivity = this;

        if (baseActivity == null || baseActivity.isFinishing() == true)
        {
            return;
        }

        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            baseActivity.showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.dialog_btn_text_confirm), null, null, null, null, dialogInterface -> this.onBackPressed(), true);
        } else if (throwable instanceof HttpException)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            if (httpException.code() == BaseException.CODE_UNAUTHORIZED)
            {
                addCompositeDisposable(new ConfigLocalImpl(this).clear().subscribe(object ->
                {
                    new FacebookRemoteImpl().logOut();
                    new KakaoRemoteImpl().logOut();

                    baseActivity.restartExpiredSession();
                }));
            } else
            {
                DailyToast.showToast(this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

                Crashlytics.log(httpException.response().raw().request().url().toString());
                Crashlytics.logException(throwable);
            }
        } else
        {
            DailyToast.showToast(this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }
    }
}
