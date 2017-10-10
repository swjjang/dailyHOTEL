package com.daily.dailyhotel.screen.home.stay.outbound.preview;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

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
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

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
    private People mPeople;

    public interface StayOutboundPreviewAnalyticsInterface extends BaseAnalyticsInterface
    {
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

        mStayName = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_STAY_NAME);

        String checkInDateTime = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHECK_IN);
        String checkOutDateTime = intent.getStringExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHECK_OUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        int numberOfAdults = intent.getIntExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundPreviewActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        return true;
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
        unLockAll();
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

        addCompositeDisposable(mStayOutboundRemoteImpl.getDetail(mStayIndex, mStayBookDateTime, mPeople).subscribe(new Consumer<StayOutboundDetail>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
            {
                onStayOutboundDetail(stayOutboundDetail);

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
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

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

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
        } catch (Exception e)
        {
            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");
                    }
                }, null);
        }

        onBackClick();
    }

    @Override
    public void onMapClick()
    {
        if (lock() == true)
        {
            return;
        }

        Util.shareGoogleMap(getActivity(), mStayOutboundDetail.name, Double.toString(mStayOutboundDetail.latitude), Double.toString(mStayOutboundDetail.longitude));

        onBackClick();
    }

    @Override
    public void onViewDetailClick()
    {
        setResult(Activity.RESULT_OK);
        onBackClick();
    }

    @Override
    public void onHideAnimation()
    {
        finish();
    }

    @Override
    public void onCloseClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().hideAnimation();
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

    void onStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        if (stayOutboundDetail == null || mStayBookDateTime == null)
        {
            return;
        }

        mStayOutboundDetail = stayOutboundDetail;

        getViewInterface().setStayRating(stayOutboundDetail.rating);
        getViewInterface().setImages(stayOutboundDetail.getImageList());

        try
        {
            // 선택된 방이 없으면 처음 방으로 한다.
            if (stayOutboundDetail.getRoomList() == null || stayOutboundDetail.getRoomList().size() == 0)
            {
                getViewInterface().setRoomInformation(true, 0, mStayBookDateTime.getNights(), 0, 0);
            } else
            {

                int minPrice = Integer.MAX_VALUE;
                int maxPrice = Integer.MIN_VALUE;

                for (StayOutboundRoom stayOutboundRoom : stayOutboundDetail.getRoomList())
                {
                    if (minPrice > stayOutboundRoom.nightly)
                    {
                        minPrice = stayOutboundRoom.nightly;
                    }

                    if (maxPrice < stayOutboundRoom.nightly)
                    {
                        maxPrice = stayOutboundRoom.nightly;
                    }
                }

                getViewInterface().setRoomInformation(false, stayOutboundDetail.getRoomList().size(), mStayBookDateTime.getNights(), minPrice, maxPrice);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
