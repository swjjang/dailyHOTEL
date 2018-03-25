package com.daily.dailyhotel.screen.home.stay.outbound.preview;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.wish.WishDialogActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPreviewPresenter extends BaseExceptionPresenter<StayOutboundPreviewActivity, StayOutboundPreviewInterface> implements StayOutboundPreviewView.OnEventListener
{
    private StayOutboundPreviewAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private int mStayIndex;
    private String mStayName;
    private StayBookDateTime mStayBookDateTime;
    private StayOutboundDetail mStayOutboundDetail;
    private List<StayOutboundRoom> mRoomList;
    private People mPeople;
    private int mPosition;

    public interface StayOutboundPreviewAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventWishClick(Activity activity, int stayIndex, boolean isWish);
    }

    public StayOutboundPreviewPresenter(@NonNull StayOutboundPreviewActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundPreviewInterface createInstanceViewInterface()
    {
        return new StayOutboundPreviewView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundPreviewActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_preview_data);

        setAnalytics(new StayOutboundPreviewAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundPreviewAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

        if (mStayIndex == -1)
        {
            return false;
        }

        mPosition = intent.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION, -1);

        mStayName = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_NAME);

        String checkInDateTime = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        int numberOfAdults = intent.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setStayName(mStayName);

        getViewInterface().showAnimation();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        if (lock() == true)
        {
            return true;
        }

        getViewInterface().hideAnimation();

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case StayOutboundPreviewActivity.REQUEST_CODE_WISH_DIALOG:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case BaseActivity.RESULT_CODE_ERROR:
                        if (data != null)
                        {
                            boolean isWish = data.getBooleanExtra(WishDialogActivity.INTENT_EXTRA_DATA_WISH, false);

                            if (mStayOutboundDetail.myWish == isWish)
                            {
                                getViewInterface().setWish(isWish, mStayOutboundDetail.wishCount);
                            } else
                            {
                                getViewInterface().setWish(isWish, mStayOutboundDetail.wishCount + (isWish ? 1 : -1));
                            }

                            Intent intent = new Intent();
                            intent.putExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_MY_WISH, isWish);
                            intent.putExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_POSITION, mPosition);
                            setResult(BaseActivity.RESULT_CODE_DATA_CHANGED, intent);

                            mAnalytics.onEventWishClick(getActivity(), mStayOutboundDetail.index, isWish);
                        } else
                        {
                            setResult(BaseActivity.RESULT_CODE_REFRESH);
                        }

                        getViewInterface().hideAnimation();
                        break;

                    case com.daily.base.BaseActivity.RESULT_CODE_REFRESH:
                        setResult(BaseActivity.RESULT_CODE_REFRESH);

                        getViewInterface().hideAnimation();
                        break;
                }
                break;

            default:
                unLockAll();
                break;
        }
    }

    private void setStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        mStayOutboundDetail = stayOutboundDetail;
    }

    private void setRoomList(List<StayOutboundRoom> roomList)
    {
        mRoomList = roomList;
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(Observable.zip(mStayOutboundRemoteImpl.getDetailInformation(mStayIndex, mStayBookDateTime, mPeople) //
            , mStayOutboundRemoteImpl.getDetailRoomList(mStayIndex, mStayBookDateTime, mPeople) //
            , new BiFunction<StayOutboundDetail, List<StayOutboundRoom>, StayOutboundDetail>()
        {
            @Override
            public StayOutboundDetail apply(StayOutboundDetail stayOutboundDetail, List<StayOutboundRoom> stayOutboundRoomList) throws Exception
            {
                setStayOutboundDetail(stayOutboundDetail);
                setRoomList(stayOutboundRoomList);

                return stayOutboundDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayOutboundDetail>()
        {
            @Override
            public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
            {
                notifyDataSetChanged();
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
                finish();
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onWishClick()
    {
        if (mStayOutboundDetail == null || lock() == true)
        {
            return;
        }

        boolean currentWish = mStayOutboundDetail.myWish;

        if (DailyHotel.isLogin() == true)
        {
            getViewInterface().setWish(!currentWish, mStayOutboundDetail.wishCount + (!currentWish ? 1 : -1));
        }

        startActivityForResult(WishDialogActivity.newInstance(getActivity(), Constants.ServiceType.OB_STAY//
            , mStayOutboundDetail.index, !currentWish, mPosition, AnalyticsManager.Screen.DAILYHOTEL_LIST), StayOutboundPreviewActivity.REQUEST_CODE_WISH_DIALOG);
    }

    @Override
    public void onKakaoClick()
    {
        if (mStayOutboundDetail == null || lock() == true)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String name = DailyUserPreference.getInstance(getActivity()).getName();

            String imageUrl;

            ImageMap imageMap = mStayOutboundDetail.getImageList().get(0).getImageMap();

            if (ScreenUtils.getScreenWidth(getActivity()) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
            {
                if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
                {
                    imageUrl = imageMap.smallUrl;
                } else
                {
                    imageUrl = imageMap.bigUrl;
                }
            } else
            {
                if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
                {
                    imageUrl = imageMap.smallUrl;
                } else
                {
                    imageUrl = imageMap.mediumUrl;
                }
            }

            KakaoLinkManager.newInstance(getActivity()).shareStayOutbound(name//
                , mStayOutboundDetail.name//
                , mStayOutboundDetail.address//
                , mStayOutboundDetail.index//
                , imageUrl//
                , mStayBookDateTime);

            getViewInterface().hideAnimation();
        } catch (Exception e)
        {
            unLockAll();

            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");

                        onBackClick();
                    }
                }, null);
        }
    }

    @Override
    public void onMapClick()
    {
        if (lock() == true)
        {
            return;
        }

        Util.shareGoogleMap(getActivity(), mStayOutboundDetail.name, Double.toString(mStayOutboundDetail.latitude), Double.toString(mStayOutboundDetail.longitude));

        getViewInterface().hideAnimation();
    }

    @Override
    public void onViewDetailClick()
    {
        if (lock() == true)
        {
            return;
        }

        setResult(Activity.RESULT_OK);
        getViewInterface().hideAnimation();
    }

    @Override
    public void onHideAnimation()
    {
        finish();
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyDataSetChanged()
    {
        if (mStayOutboundDetail == null || mStayBookDateTime == null)
        {
            return;
        }

        boolean provideRewardSticker = false;

        try
        {
            // 선택된 방이 없으면 처음 방으로 한다.
            if (mRoomList == null || mRoomList.size() == 0)
            {
                getViewInterface().setRoomInformation(true, 0, mStayBookDateTime.getNights(), 0, 0);
            } else
            {

                int minPrice = Integer.MAX_VALUE;
                int maxPrice = Integer.MIN_VALUE;

                for (StayOutboundRoom stayOutboundRoom : mRoomList)
                {
                    if (minPrice > stayOutboundRoom.nightly)
                    {
                        minPrice = stayOutboundRoom.nightly;
                    }

                    if (maxPrice < stayOutboundRoom.nightly)
                    {
                        maxPrice = stayOutboundRoom.nightly;
                    }

                    if (stayOutboundRoom.provideRewardSticker)
                    {
                        provideRewardSticker = true;
                    }
                }

                getViewInterface().setRoomInformation(false, mRoomList.size(), mStayBookDateTime.getNights(), minPrice, maxPrice);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        getViewInterface().setCategory(mStayOutboundDetail.rating, mStayOutboundDetail.activeReward && provideRewardSticker);
        getViewInterface().setImages(mStayOutboundDetail.getImageList());
        getViewInterface().setWish(mStayOutboundDetail.myWish, mStayOutboundDetail.wishCount);
    }
}
