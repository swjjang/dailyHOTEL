package com.daily.dailyhotel.screen.home.stay.outbound.detail;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundDetailImage;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayOutboundPaymentAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.amenities.AmenityListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.images.ImageListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.payment.StayOutboundPaymentActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.AppResearch;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

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
    public static final int STATUS_FINISH = 4;

    public enum PriceType
    {
        AVERAGE,
        TOTAL
    }

    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private int mStayIndex, mListPrice;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private CommonDateTime mCommonDateTime;
    private StayOutboundDetail mStayOutboundDetail;
    private People mPeople;
    private StayOutboundRoom mSelectedRoom;

    private int mStatus = STATUS_NONE;

    private boolean mIsUsedMultiTransition;
    private boolean mIsDeepLink;
    private boolean mCheckChangedPrice;
    private int mGradientType;

    private AppResearch mAppResearch;

    public interface StayOutboundDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(StayOutboundDetailAnalyticsParam analyticsParam);

        StayOutboundDetailAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity);

        void onScreenRoomList(Activity activity);

        StayOutboundPaymentAnalyticsParam getPaymentAnalyticsParam(String grade, boolean nrd, boolean showOriginalPrice);
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

        mAppResearch = new AppResearch(activity);
        setAnalytics(new StayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mProfileRemoteImpl = new ProfileRemoteImpl(activity);

        setPeople(People.DEFAULT_ADULTS, null);

        setStatus(STATUS_NONE);

        setRefresh(false);

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
            mGradientType = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            mIsDeepLink = false;

            mStayIndex = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

            if (mStayIndex == -1)
            {
                return false;
            }

            mStayName = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_NAME);
            mImageUrl = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);
            mListPrice = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_LIST_PRICE, StayOutboundDetailActivity.NONE_PRICE);

            String checkInDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECK_IN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECK_OUT);

            setStayBookDateTime(checkInDateTime, checkOutDateTime);

            int numberOfAdults = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHILD_LIST);

            setPeople(numberOfAdults, childAgeList);

            mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            getViewInterface().setSharedElementTransitionEnabled(true, mGradientType);
            getViewInterface().setInitializedTransLayout(mStayName, mImageUrl);
        } else
        {
            getViewInterface().setSharedElementTransitionEnabled(false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            getViewInterface().setInitializedImage(mImageUrl);
        }

        RecentlyPlaceUtil.addRecentlyItem(getActivity(), Constants.ServiceType.OB_STAY, mStayIndex, mStayName, null, mImageUrl, true);

        if (mIsUsedMultiTransition == true)
        {
            screenLock(false);

            Disposable disposable = Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())//
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> screenLock(true));

            addCompositeDisposable(disposable);

            addCompositeDisposable(Observable.zip(getViewInterface().getSharedElementTransition()//
                , mCommonRemoteImpl.getCommonDateTime(), mStayOutboundRemoteImpl.getStayOutboundDetail(mStayIndex, mStayBookDateTime, mPeople)//
                , new Function3<Boolean, CommonDateTime, StayOutboundDetail, StayOutboundDetail>()
                {
                    @Override
                    public StayOutboundDetail apply(@io.reactivex.annotations.NonNull Boolean aBoolean, @io.reactivex.annotations.NonNull CommonDateTime commonDateTime, @io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
                    {
                        setCommonDateTime(commonDateTime);
                        return stayOutboundDetail;
                    }
                }).subscribe(new Consumer<StayOutboundDetail>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
                {
                    onStayOutboundDetail(stayOutboundDetail);

                    if (disposable != null)
                    {
                        disposable.dispose();
                    }

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    if (disposable != null)
                    {
                        disposable.dispose();
                    }

                    onHandleError(throwable);
                }
            }));
        } else
        {
            onRefresh(true);
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        onHideRoomListClick(false);

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        mAppResearch.onResume("outbound_스테이", mStayIndex);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mAppResearch.onPause("outbound_스테이", mStayIndex);
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
    public synchronized boolean onBackPressed()
    {
        switch (mStatus)
        {
            case STATUS_BOOKING:
                onHideRoomListClick(true);
                return true;

            case STATUS_FINISH:
                break;

            default:
                setStatus(STATUS_FINISH);

                if (mIsUsedMultiTransition == true)
                {
                    lock();

                    getViewInterface().scrollTop();

                    Single.just(mIsUsedMultiTransition).delaySubscription(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                        {
                            getActivity().onBackPressed();
                        }
                    });

                    return true;
                }
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

                        setStayBookDateTime(checkInDateTime, checkOutDateTime);
                        setRefresh(true);
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

                        setPeople(numberOfAdults, childAgeList);
                        setRefresh(true);
                    }
                }
                break;
            }

            case StayOutboundDetailActivity.REQUEST_CODE_HAPPYTALK:
                break;

            case StayOutboundDetailActivity.REQUEST_CODE_CALL:
                break;

            case StayOutboundDetailActivity.REQUEST_CODE_PAYMENT:
                if (resultCode == BaseActivity.RESULT_CODE_REFRESH)
                {
                    setRefresh(true);
                }
                break;

            case StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE:
            case StayOutboundDetailActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    onActionButtonClick();
                } else
                {
                    onHideRoomListClick(false);
                }
                break;
        }
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

        mSelectedRoom = null;

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), mStayOutboundRemoteImpl.getStayOutboundDetail(mStayIndex, mStayBookDateTime, mPeople), new BiFunction<CommonDateTime, StayOutboundDetail, StayOutboundDetail>()
        {
            @Override
            public StayOutboundDetail apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime, @io.reactivex.annotations.NonNull StayOutboundDetail stayOutboundDetail) throws Exception
            {
                setCommonDateTime(commonDateTime);
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
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    protected void onHandleError(Throwable throwable)
    {
        unLockAll();

        // 에러가 나는 경우 리스트로 복귀
        if (throwable instanceof BaseException)
        {
            // 팝업 에러 보여주기
            BaseException baseException = (BaseException) throwable;

            getViewInterface().showSimpleDialog(null, baseException.getMessage()//
                , getString(R.string.frag_error_btn), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onRefresh(true);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        if (getActivity().isFinishing() == true)
                        {
                            return;
                        }

                        getActivity().onBackPressed();
                    }
                });
        } else if (throwable instanceof HttpException && ((HttpException) throwable).code() != BaseException.CODE_UNAUTHORIZED)
        {
            retrofit2.HttpException httpException = (HttpException) throwable;

            getViewInterface().showSimpleDialog(null, getString(R.string.act_base_network_connect)//
                , getString(R.string.frag_error_btn), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onRefresh(true);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        if (getActivity().isFinishing() == true)
                        {
                            return;
                        }

                        getActivity().onBackPressed();
                    }
                });

            DailyToast.showToast(getActivity(), getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);

            Crashlytics.log(httpException.response().raw().request().url().toString());
            Crashlytics.logException(throwable);
        } else
        {
            super.onHandleError(throwable);
        }
    }

    @Override
    public void onShareClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showShareDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });
    }

    @Override
    public void onShareKakaoClick()
    {
        if (mStayOutboundDetail == null || mStayBookDateTime == null)
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
    }

    @Override
    public void onShareSmsClick()
    {
        if (mStayOutboundDetail == null || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            String name = DailyUserPreference.getInstance(getActivity()).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            int nights = mStayBookDateTime.getNights();

            String message = getString(R.string.message_detail_stay_outbound_share_sms//
                , name, mStayOutboundDetail.name//
                , mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                , nights, nights + 1 //
                , mStayOutboundDetail.address);

            Util.sendSms(getActivity(), message);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onImageClick(int position)
    {
        if (mStayOutboundDetail == null || mStayOutboundDetail.getImageList() == null//
            || mStayOutboundDetail.getImageList().size() == 0 || lock() == true)
        {
            return;
        }

        startActivityForResult(ImageListActivity.newInstance(getActivity(), mStayOutboundDetail.name//
            , mStayOutboundDetail.getImageList(), position), StayOutboundDetailActivity.REQUEST_CODE_IMAGE_LIST);
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
        if (mStayBookDateTime == null || lock() == true)
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
            if (getActivity().isFinishing() == true || lock() == true)
            {
                return;
            }

            startActivityForResult(ZoomMapActivity.newInstance(getActivity()//
                , ZoomMapActivity.SourceType.HOTEL, mStayOutboundDetail.name, mStayOutboundDetail.address//
                , mStayOutboundDetail.latitude, mStayOutboundDetail.longitude, true), StayOutboundDetailActivity.REQUEST_CODE_MAP);
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
        if (mStayOutboundDetail == null || lock() == true)
        {
            return;
        }

        NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();

        startActivityForResult(NavigatorDialogActivity.newInstance(getActivity(), mStayOutboundDetail.name//
            , mStayOutboundDetail.latitude, mStayOutboundDetail.longitude, true, analyticsParam), StayOutboundDetailActivity.REQUEST_CODE_NAVIGATOR);
    }

    @Override
    public void onConciergeClick()
    {
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
                if (mSelectedRoom == null || lock() == true)
                {
                    return;
                }

                if (DailyHotel.isLogin() == false)
                {
                    DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);

                    startActivityForResult(LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND)//
                        , StayOutboundDetailActivity.REQUEST_CODE_LOGIN);
                } else
                {
                    addCompositeDisposable(mProfileRemoteImpl.getProfile().subscribe(new Consumer<User>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull User user) throws Exception
                        {
                            boolean isDailyUser = Constants.DAILY_USER.equalsIgnoreCase(user.userType);
                            StayOutboundPaymentAnalyticsParam analyticsParam = mAnalytics.getPaymentAnalyticsParam(getString(R.string.label_stay_outbound_detail_grade, (int) mStayOutboundDetail.rating)//
                                , mSelectedRoom.nonRefundable, mSelectedRoom.promotion);

                            if (isDailyUser == true)
                            {
                                // 인증이 되어있지 않던가 기존에 인증이 되었는데 인증이 해지되었다.
                                if (Util.isValidatePhoneNumber(user.phone) == false || (user.verified == true && user.phoneVerified == false))
                                {
                                    startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                                        , EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.phone)//
                                        , StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                                } else
                                {
                                    startActivityForResult(StayOutboundPaymentActivity.newInstance(getActivity(), mStayOutboundDetail.index//
                                        , mStayOutboundDetail.name, mImageUrl, mSelectedRoom.total//
                                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                        , mPeople.numberOfAdults, mPeople.getChildAgeList()//
                                        , mSelectedRoom.roomName, mSelectedRoom.rateCode, mSelectedRoom.rateKey//
                                        , mSelectedRoom.roomTypeCode, mSelectedRoom.roomBedTypeId, analyticsParam)//
                                        , StayOutboundDetailActivity.REQUEST_CODE_PAYMENT);
                                }
                            } else
                            {
                                // 입력된 정보가 부족해.
                                if (DailyTextUtils.isTextEmpty(user.email, user.phone, user.name) == true)
                                {
                                    Customer customer = new Customer();
                                    customer.setEmail(user.email);
                                    customer.setName(user.name);
                                    customer.setPhone(user.phone);
                                    customer.setUserIdx(Integer.toString(user.index));

                                    startActivityForResult(AddProfileSocialActivity.newInstance(getActivity()//
                                        , customer, user.birthday), StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                                } else if (Util.isValidatePhoneNumber(user.phone) == false)
                                {
                                    startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                                        , EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.phone)//
                                        , StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                                } else
                                {
                                    startActivityForResult(StayOutboundPaymentActivity.newInstance(getActivity(), mStayOutboundDetail.index//
                                        , mStayOutboundDetail.name, mImageUrl, mSelectedRoom.total//
                                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                                        , mPeople.numberOfAdults, mPeople.getChildAgeList()//
                                        , mSelectedRoom.roomName, mSelectedRoom.rateCode, mSelectedRoom.rateKey//
                                        , mSelectedRoom.roomTypeCode, mSelectedRoom.roomBedTypeId, analyticsParam)//
                                        , StayOutboundDetailActivity.REQUEST_CODE_PAYMENT);
                                }
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                        {
                            onHandleError(throwable);
                        }
                    }));
                }
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

                mAnalytics.onScreenRoomList(getActivity());
                break;

            default:
                break;
        }
    }

    @Override
    public void onAmenityMoreClick()
    {
        if (mStayOutboundDetail == null || mStayOutboundDetail.getAmenityList() == null//
            || mStayOutboundDetail.getAmenityList().size() == 0 || lock() == true)
        {
            return;
        }

        SparseArray<String> amenitySparseArray = mStayOutboundDetail.getAmenityList();
        int size = amenitySparseArray.size();
        ArrayList<String> amenityList = new ArrayList<>(size);

        for (int i = 0; i < size; i++)
        {
            String amenity = amenitySparseArray.get(amenitySparseArray.keyAt(i));

            if (DailyTextUtils.isTextEmpty(amenity) == false)
            {
                amenityList.add(amenitySparseArray.get(amenitySparseArray.keyAt(i)));
            }
        }

        startActivityForResult(AmenityListActivity.newInstance(getActivity(), amenityList), StayOutboundDetailActivity.REQUEST_CODE_AMENITY);
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

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_OUTBOUND_DETAIL//
                , mStayOutboundDetail.index, 0, mStayOutboundDetail.name), StayOutboundDetailActivity.REQUEST_CODE_HAPPYTALK);
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
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayOutboundDetailActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onRoomClick(StayOutboundRoom stayOutboundRoom)
    {
        mSelectedRoom = stayOutboundRoom;
    }

    private void setStatus(int status)
    {
        mStatus = status;

        getViewInterface().setBottomButtonLayout(status);
    }

    private void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
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

    private void onStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        if (stayOutboundDetail == null)
        {
            return;
        }

        mStayOutboundDetail = stayOutboundDetail;

        // 리스트에서 이미지가 큰사이즈가 없는 경우 상세에서도 해당 사이즈가 없기 때문에 고려해준다.
        try
        {
            StayOutboundDetailImage stayOutboundDetailImage = stayOutboundDetail.getImageList().get(0);
            ImageMap imageMap = stayOutboundDetailImage.getImageMap();

            if (mImageUrl.equalsIgnoreCase(imageMap.smallUrl) == true)
            {
                imageMap.bigUrl = null;
                imageMap.mediumUrl = null;
            }

            // 땡큐 페이지에서 이미지를 못읽는 경우가 생겨서 작은 이미지로 수정
            mImageUrl = imageMap.smallUrl;
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (mIsDeepLink == true)
        {
            getViewInterface().setToolbarTitle(stayOutboundDetail.name);
        }

        getViewInterface().setStayDetail(mStayBookDateTime, mPeople, stayOutboundDetail);

        // 리스트 가격 변동은 진입시 한번 만 한다.
        checkChangedPrice(mIsDeepLink, stayOutboundDetail, mListPrice, mCheckChangedPrice == false);
        mCheckChangedPrice = true;

        // 선택된 방이 없으면 처음 방으로 한다.
        if (mStayOutboundDetail.getRoomList() == null || mStayOutboundDetail.getRoomList().size() == 0)
        {
            setStatus(STATUS_SOLD_OUT);
        } else
        {
            if (mSelectedRoom == null)
            {
                onRoomClick(stayOutboundDetail.getRoomList().get(0));
            }

            setStatus(STATUS_ROOM_LIST);
        }

        mIsDeepLink = false;
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

    private void checkChangedPrice(boolean isDeepLink, StayOutboundDetail stayOutboundDetail, int listViewPrice, boolean compareListPrice)
    {
        if (stayOutboundDetail == null)
        {
            return;
        }

        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<StayOutboundRoom> roomList = stayOutboundDetail.getRoomList();

        if (roomList == null || roomList.size() == 0)
        {
            setResult(BaseActivity.RESULT_CODE_REFRESH);

            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_outbound_detail_sold_out)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {

                    }
                });
        } else
        {
            if (isDeepLink == false && compareListPrice == true)
            {
                boolean hasPrice = false;

                if (listViewPrice == StayOutboundDetailActivity.NONE_PRICE)
                {
                    hasPrice = true;
                } else
                {
                    for (StayOutboundRoom room : roomList)
                    {
                        if (listViewPrice == room.total)
                        {
                            hasPrice = true;
                            break;
                        }
                    }
                }

                if (hasPrice == false)
                {
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_outbound_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                onActionButtonClick();
                            }
                        });
                }
            }
        }
    }
}
