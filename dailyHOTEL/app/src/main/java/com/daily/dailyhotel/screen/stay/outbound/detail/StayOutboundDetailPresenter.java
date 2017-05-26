package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.stay.outbound.people.SelectPeopleActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailPresenter extends BaseExceptionPresenter<StayOutboundDetailActivity, StayOutboundDetailViewInterface> implements StayOutboundDetailView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_ROOM_LIST = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    public enum PriceType
    {
        AVERAGE,
        TOTAL
    }

    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private int mStayIndex;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private CommonDateTime mCommonDateTime;
    private StayOutboundDetail mStayOutboundDetail;
    private People mPeople;

    private int mStatus = STATUS_NONE;
    private PriceType mPriceType = PriceType.AVERAGE;

    private boolean mIsUsedMultiTransition;
    private boolean mIsDeepLink;
    private boolean mCheckChangedPrice;

    public interface StayOutboundDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundDetailPresenter(@NonNull StayOutboundDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundDetailViewInterface createInstanceViewInterface()
    {
        return new StayOutboundDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_detail_data);

        setAnalytics(new StayStayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        setPeople(People.DEFAULT_ADULTS, null);

        setStatus(STATUS_NONE);

        mPriceType = PriceType.AVERAGE;

        Observable<Boolean> observable = getViewInterface().hideRoomList(false);

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe());
        }
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundDetailAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            mIsUsedMultiTransition = false;
            mIsDeepLink = true;
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);
            mIsDeepLink = false;

            mStayIndex = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

            if (mStayIndex == -1)
            {
                return false;
            }

            mStayName = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_NAME);
            mImageUrl = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_URL);

            String checkInDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKOUT);

            setStayBookDateTime(checkInDateTime, checkOutDateTime);

            mPeople.numberOfAdults = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mPeople.setChildAgeList(intent.getIntegerArrayListExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHILD_LIST));
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            //            initTransLayout(placeName, imageUrl, grade, isFromMap);
        } else
        {
            getViewInterface().setInitializedImage(mImageUrl);
        }

        //        setLockUICancelable(true);
        getViewInterface().setToolbarTitle(mStayName);

        //        mOnEventListener.hideActionBar(false);

        if (mIsUsedMultiTransition == true)
        {
            addCompositeDisposable(Observable.zip(getViewInterface().getSharedElementTransition()//
                , mCommonRemoteImpl.getCommonDateTime(), mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPeople)//
                , new Function3<Boolean, CommonDateTime, StayOutboundDetail, StayOutboundDetail>()
                {
                    @Override
                    public StayOutboundDetail apply(@io.reactivex.annotations.NonNull Boolean aBoolean, @io.reactivex.annotations.NonNull CommonDateTime commonDateTime, @io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
                    {
                        onCommonDateTime(commonDateTime);
                        return stayOutboundDetail;
                    }
                }).subscribe(new Consumer<StayOutboundDetail>()
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
                }
            }));

            //            addCompositeDisposable(Observable.zip(getViewInterface().getSharedElementTransition(), mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPeople), new BiFunction<Boolean, StayOutboundDetail, StayOutboundDetail>()
            //            {
            //                @Override
            //                public StayOutboundDetail apply(Boolean aBoolean, StayOutboundDetail stayOutboundDetail) throws Exception
            //                {
            //                    return stayOutboundDetail;
            //                }
            //            }).subscribe(new Consumer<StayOutboundDetail>()
            //            {
            //                @Override
            //                public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
            //                {
            //                    if (stayOutboundDetail == null)
            //                    {
            //                        return;
            //                    }
            //
            //                    onStayOutboundDetail(stayOutboundDetail);
            //                }
            //            }, new Consumer<Throwable>()
            //            {
            //                @Override
            //                public void accept(Throwable throwable) throws Exception
            //                {
            //
            //                    onHandleError(throwable);
            //                }
            //            }));
        } else
        {
            onRefresh(true);
        }
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
    public void onFinish()
    {
        super.onFinish();

        if (mIsUsedMultiTransition == false)
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onBackPressed()
    {
        switch (mStatus)
        {
            case STATUS_BOOKING:
                onHideRoomListClick(true);
                return true;
        }

        return super.onBackPressed();
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

        switch (requestCode)
        {
            case StayOutboundDetailActivity.REQUEST_CODE_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                        && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                        {
                            return;
                        }

                        onCalendarDateTime(checkInDateTime, checkOutDateTime);
                        onRefresh(true);
                    }
                }
                break;
            }

            case StayOutboundDetailActivity.REQUEST_CODE_PEOPLE:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS) == true && data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST) == true)
                    {
                        int numberOfAdults = data.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> childAgeList = data.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        onPeople(numberOfAdults, childAgeList);
                        onRefresh(true);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPeople), new BiFunction<CommonDateTime, StayOutboundDetail, StayOutboundDetail>()
        {
            @Override
            public StayOutboundDetail apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime, @io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
            {
                onCommonDateTime(commonDateTime);
                return stayOutboundDetail;
            }
        }).subscribe(new Consumer<StayOutboundDetail>()
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
            }
        }));

        //            addCompositeDisposable(mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPeople).subscribe(new Consumer<StayOutboundDetail>()
        //            {
        //                @Override
        //                public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
        //                {
        //                    onStayOutboundDetail(stayOutboundDetail);
        //
        //                    unLockAll();
        //                }
        //            }, new Consumer<Throwable>()
        //            {
        //                @Override
        //                public void accept(Throwable throwable) throws Exception
        //                {
        //
        //
        //                }
        //            }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onShareClick()
    {

    }

    @Override
    public void onImageClick(int position)
    {

    }

    @Override
    public void onImageSelected(int position)
    {
        if (mStayOutboundDetail == null)
        {
            return;
        }

        getViewInterface().setDetailImageCaption(mStayOutboundDetail.getImageList().get(position).caption);
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance();
            startCalendar.setTime(DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT));
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);

            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);

            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.STAY, true, 0, true);

            startActivityForResult(intent, StayOutboundDetailActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onPeopleClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent;

        if (mPeople == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), mPeople.numberOfAdults, mPeople.getChildAgeList());
        }

        startActivityForResult(intent, StayOutboundDetailActivity.REQUEST_CODE_PEOPLE);
    }

    @Override
    public void onMapClick()
    {
        if (Util.isInstallGooglePlayService(getActivity()) == true)
        {
            if (lock() == true || getActivity().isFinishing() == true)
            {
                return;
            }

            startActivity(ZoomMapActivity.newInstance(getActivity()//
                , ZoomMapActivity.SourceType.HOTEL, mStayOutboundDetail.name, mStayOutboundDetail.address//
                , mStayOutboundDetail.latitude, mStayOutboundDetail.longitude, true));
        } else
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_title_googleplayservice)//
                , getString(R.string.dialog_msg_install_update_googleplayservice)//
                , getString(R.string.dialog_btn_text_install), getString(R.string.dialog_btn_text_cancel), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        try
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            intent.setPackage("com.android.vending");
                            startActivity(intent);
                        } catch (ActivityNotFoundException e)
                        {
                            try
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                intent.setPackage("com.android.vending");
                                startActivity(intent);
                            } catch (ActivityNotFoundException f)
                            {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                startActivity(intent);
                            }
                        }
                    }
                }, null, true);

        }
    }

    @Override
    public void onClipAddressClick(String address)
    {
        DailyTextUtils.clipText(getActivity(), address);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT);
    }

    @Override
    public void onNavigatorClick()
    {
        if (lock() == true || getActivity().isFinishing() == true)
        {
            return;
        }

        getViewInterface().showNavigatorDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });
    }

    @Override
    public void onConciergeClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showConciergeDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });
    }

    @Override
    public void onHideRoomListClick(boolean animation)
    {
        Observable<Boolean> observable = getViewInterface().hideRoomList(animation);

        if (observable != null)
        {
            screenLock(false);

            addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    unLockAll();

                    setStatus(STATUS_ROOM_LIST);
                }
            }));
        }
    }

    @Override
    public void onActionButtonClick()
    {
        switch (mStatus)
        {
            case STATUS_BOOKING:
                break;

            case STATUS_ROOM_LIST:
                screenLock(false);

                Observable<Boolean> observable = getViewInterface().showRoomList(true);

                if (observable != null)
                {
                    addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                        {
                            unLockAll();

                            setStatus(STATUS_BOOKING);
                        }
                    }));
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onAmenityMoreClick()
    {

    }

    @Override
    public void onPriceTypeClick(PriceType priceType)
    {
        getViewInterface().setPriceType(priceType);
    }

    @Override
    public void onConciergeFaqClick()
    {
        startActivity(FAQActivity.newInstance(getActivity()));
    }

    @Override
    public void onConciergeHappyTalkClick()
    {
        if (mStayOutboundDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivity(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_DETAIL//
                , mStayOutboundDetail.index, 0, mStayOutboundDetail.name));
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
    }

    @Override
    public void onConciergeCallClick()
    {
        // 복잡.
    }

    @Override
    public void onShareMapClick()
    {
        Util.shareGoogleMap(getActivity(), mStayOutboundDetail.name, Double.toString(mStayOutboundDetail.latitude), Double.toString(mStayOutboundDetail.longitude));
    }

    private void onStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        if (stayOutboundDetail == null)
        {
            return;
        }

        mStayOutboundDetail = stayOutboundDetail;

        if (mIsDeepLink == true)
        {
            // 딥링크로 진입한 경우에는 카테고리 코드를 알수가 없다. - 2017.04.28 알 수 없음으로 안보내기로 함 아이폰도 안보내고 있음.
            //            if (DailyTextUtils.isTextEmpty(stayDetailParams.category) == true)
            //            {
            //                stayDetailParams.category = stayDetailParams.getGrade().name();
            //            }

            getViewInterface().setToolbarTitle(stayOutboundDetail.name);
        }

        getViewInterface().setStayDetail(mStayBookDateTime, mPeople, stayOutboundDetail);

        if (mCheckChangedPrice == false)
        {
            mCheckChangedPrice = true;
            checkChangedPrice(mIsDeepLink, stayOutboundDetail, null);
        }


        setStatus(STATUS_ROOM_LIST);

        //        mProductDetailIndex = 0;
        mIsDeepLink = false;
    }

    private void setStatus(int status)
    {
        mStatus = status;

        getViewInterface().setBottomButtonLayout(status);
    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void onCalendarDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        setStayBookDateTime(checkInDateTime, checkOutDateTime);
        onStayBookDateTime(mStayBookDateTime);
    }

    private void onPeople(People people)
    {
        if (mPeople == null)
        {
            return;
        }

        getViewInterface().setPeopleText(mPeople.toShortString(getActivity()));
    }

    private void onPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        setPeople(numberOfAdults, childAgeList);

        onPeople(mPeople);
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

    private void onStayBookDateTime(@NonNull StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            String dateFormat = String.format(Locale.KOREA, "%s - %s, %s", stayBookDateTime.getCheckInDateTime("M.d(EEE)"), stayBookDateTime.getCheckOutDateTime("M.d(EEE)"), getString(R.string.label_nights, stayBookDateTime.getNights()));

            getViewInterface().setCalendarText(dateFormat);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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

    private void checkChangedPrice(boolean isDeepLink, StayOutboundDetail stayOutboundDetail, String listViewPrice)
    {
        if (stayOutboundDetail == null || DailyTextUtils.isTextEmpty(listViewPrice) == true)
        {
            return;
        }

        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<StayOutboundRoom> roomList = stayOutboundDetail.getRoomList();

        if (roomList == null || roomList.size() == 0)
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        //                        setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                });
        } else
        {
            if (isDeepLink == false)
            {
                boolean hasPrice = false;

                for (StayOutboundRoom room : roomList)
                {
                    if (listViewPrice == room.nightly)
                    {
                        hasPrice = true;
                        break;
                    }
                }

                if (hasPrice == false)
                {
                    //                    setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                Observable<Boolean> observable = getViewInterface().showRoomList(false);

                                if (observable != null)
                                {
                                    screenLock(false);

                                    addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
                                    {
                                        @Override
                                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                                        {
                                            unLockAll();
                                        }
                                    }));
                                }
                            }
                        });
                }
            }
        }
    }
}
