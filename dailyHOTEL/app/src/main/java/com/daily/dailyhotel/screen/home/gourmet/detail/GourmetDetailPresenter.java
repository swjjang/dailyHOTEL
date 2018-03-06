package com.daily.dailyhotel.screen.home.gourmet.detail;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.DetailImageInformation;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetCart;
import com.daily.dailyhotel.entity.GourmetCartMenu;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.TrueVR;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.GourmetPaymentAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.ImageListAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.TrueReviewAnalyticsParam;
import com.daily.dailyhotel.repository.local.CartLocalImpl;
import com.daily.dailyhotel.repository.local.RecentlyLocalImpl;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.common.images.ImageListActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.menus.GourmetMenusActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.truereview.GourmetTrueReviewActivity;
import com.daily.dailyhotel.screen.home.gourmet.payment.GourmetPaymentActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.TrueVRActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.SelectGourmetCouponDialogActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.AppResearch;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetDetailPresenter extends BaseExceptionPresenter<GourmetDetailActivity, GourmetDetailViewInterface>//
    implements GourmetDetailView.OnEventListener
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_BOOKING = 1;
    public static final int STATUS_SELECT_MENU = 2;
    public static final int STATUS_SOLD_OUT = 3;
    public static final int STATUS_FINISH = 4;

    private static final int VALID_GOURMET_CART_NONE = -1;
    private static final int VALID_GOURMET_CART_DEFAULT = 0;
    private static final int INVALID_GOURMET_CART_VISIT_TIME = 1;
    private static final int INVALID_GOURMET_CART_QUANTITY = 2;
    private static final int INVALID_GOURMET_SOLD_OUT = 3;
    private static final int INVALID_GOURMET_CART_CHANGED_PRICE = 4; // 카트 가격이 변동 되는 경우
    private static final int INVALID_GOURMET_CHANGED_PRICE = 5; // 가격이 변동 되는 경우


    public static final String FULL_TIME = "FULL_TIME";

    static final int SHOWN_MENU_COUNT = 5;

    GourmetDetailAnalyticsInterface mAnalytics;

    private GourmetRemoteImpl mGourmetRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;
    private CalendarImpl mCalendarImpl;
    private RecentlyLocalImpl mRecentlyLocalImpl;
    CartLocalImpl mCartLocalImpl;

    int mGourmetIndex, mPriceFromList;
    private String mGourmetName, mCategory;
    private String mImageUrl;
    GourmetBookDateTime mGourmetBookDateTime;
    private CommonDateTime mCommonDateTime;
    GourmetDetail mGourmetDetail;
    ReviewScores mReviewScores;

    private int mStatus = STATUS_NONE;

    private boolean mIsUsedMultiTransition;
    boolean mIsDeepLink;
    boolean mCheckChangedPrice;
    private boolean mSoldOutFromList;
    private int mGradientType;
    private List<Integer> mSoldOutDateList;
    boolean mShowCalendar;
    boolean mShowTrueVR;
    List<TrueVR> mTrueVRList;

    // 멀티 구매
    private String mVisitTime;
    private List<String> mOperationTimeList;

    DailyDeepLink mDailyDeepLink;
    private AppResearch mAppResearch;

    public interface GourmetDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetDetailAnalyticsParam analyticsParam);

        GourmetPaymentAnalyticsParam getStayPaymentAnalyticsParam(GourmetDetail gourmetDetail, GourmetCart gourmetCart);

        void onScreen(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int priceFromList);

        void onEventShareKakaoClick(Activity activity, boolean login, String userType, boolean benefitAlarm//
            , int gourmetIndex, String gourmetName);

        void onEventCopyLinkClick(Activity activity);

        void onEventMoreShareClick(Activity activity);

        void onEventDownloadCoupon(Activity activity, String gourmetName);

        void onEventDownloadCouponByLogin(Activity activity, boolean login);

        void onEventShare(Activity activity);

        void onEventHasHiddenMenus(Activity activity);

        void onEventChangedPrice(Activity activity, boolean deepLink, String gourmetName, boolean soldOut);

        void onEventCalendarClick(Activity activity);

        void onEventOrderClick(Activity activity, GourmetBookDateTime gourmetBookDateTime//
            , int gourmetIndex, String gourmetName, String category, GourmetCart gourmetCart);

        void onEventScrollTopMenuClick(Activity activity, String gourmetName);

        void onEventMenuClick(Activity activity, int menuIndex, int position);

        void onEventTrueReviewClick(Activity activity);

        void onEventMoreMenuClick(Activity activity, boolean opened, int gourmetIndex);

        void onEventImageClick(Activity activity, String gourmetName);

        void onEventConciergeClick(Activity activity);

        void onEventMapClick(Activity activity, String gourmetName);

        void onEventClipAddressClick(Activity activity, String gourmetName);

        void onEventWishClick(Activity activity, GourmetBookDateTime gourmetBookDateTime, GourmetDetail gourmetDetail, int priceFromList, boolean myWish);

        void onEventCallClick(Activity activity);

        void onEventFaqClick(Activity activity);

        void onEventHappyTalkClick(Activity activity);

        void onEventShowTrueReview(Activity activity, int gourmetIndex);

        void onEventShowCoupon(Activity activity, int gourmetIndex);

        void onEventVisitTimeClick(Activity activity, String visitTime);

        void onEventToolbarBookingClick(Activity activity, int gourmetIndex);

        void onEventTrueAwards(Activity activity, int gourmetIndex);

        void onEventTrueAwardsClick(Activity activity, int gourmetIndex);
    }

    public GourmetDetailPresenter(@NonNull GourmetDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetDetailViewInterface createInstanceViewInterface()
    {
        return new GourmetDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetDetailActivity activity)
    {
        setContentView(R.layout.activity_gourmet_detail_data);

        mAppResearch = new AppResearch(activity);
        setAnalytics(new GourmetDetailAnalyticsImpl());

        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mProfileRemoteImpl = new ProfileRemoteImpl(activity);
        mCalendarImpl = new CalendarImpl(activity);
        mRecentlyLocalImpl = new RecentlyLocalImpl(activity);
        mCartLocalImpl = new CartLocalImpl(activity);

        setStatus(STATUS_NONE);

        setVisitTime(FULL_TIME, false);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetDetailAnalyticsInterface) analytics;
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
            mAnalytics.setAnalyticsParam(new GourmetDetailAnalyticsParam());

            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(intent.getStringExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;

                return false;
            }

            mIsUsedMultiTransition = false;
            mIsDeepLink = true;

            if (mDailyDeepLink.isExternalDeepLink() == true)
            {
                addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().subscribe(new Consumer<CommonDateTime>()
                {
                    @Override
                    public void accept(CommonDateTime commonDateTime) throws Exception
                    {
                        setCommonDateTime(commonDateTime);

                        DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                        try
                        {
                            mGourmetIndex = Integer.parseInt(externalDeepLink.getIndex());
                        } catch (Exception e)
                        {
                            Crashlytics.log(externalDeepLink.getDeepLink());
                            Crashlytics.logException(e);
                            finish();
                            return;
                        }

                        mShowCalendar = externalDeepLink.isShowCalendar();
                        mShowTrueVR = externalDeepLink.isShowVR();

                        setGourmetBookDateTime(externalDeepLink.getGourmetBookDateTime(commonDateTime, externalDeepLink).getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));

                        mDailyDeepLink.clear();
                        mDailyDeepLink = null;

                        setRefresh(true);
                        onRefresh(true);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        Crashlytics.log(mDailyDeepLink.getDeepLink());
                        Crashlytics.logException(throwable);

                        onHandleError(throwable);
                    }
                }));
            }
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);
            mShowCalendar = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SHOW_CALENDAR, false);
            mShowTrueVR = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SHOW_TRUE_VR, false);
            mGradientType = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            mIsDeepLink = false;

            mGourmetIndex = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_GOURMET_INDEX, -1);

            if (mGourmetIndex == -1)
            {
                return false;
            }

            mGourmetName = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_GOURMET_NAME);
            mImageUrl = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);
            mPriceFromList = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_LIST_PRICE, GourmetDetailActivity.NONE_PRICE);

            String visitDate = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_VISIT_DATE);

            setGourmetBookDateTime(visitDate);

            mCategory = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CATEGORY);

            // 이미 판매 완료인 경우에는 가격을 검사할 필요가 없다.
            mSoldOutFromList = mCheckChangedPrice = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SOLDOUT, false);
            mAnalytics.setAnalyticsParam(intent.getParcelableExtra(BaseActivity.INTENT_EXTRA_DATA_ANALYTICS));
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            getViewInterface().setSharedElementTransitionEnabled(true, mGradientType);
            getViewInterface().setInitializedTransLayout(mGourmetName, mImageUrl);
        } else
        {
            getViewInterface().setSharedElementTransitionEnabled(false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            getViewInterface().setInitializedImage(mImageUrl);
        }

        addCompositeDisposable(mRecentlyLocalImpl.addRecentlyItem( //
            Constants.ServiceType.GOURMET, mGourmetIndex, mGourmetName, null, mImageUrl, true) //
            .observeOn(Schedulers.io()).subscribe());

        if (mIsUsedMultiTransition == true)
        {
            setRefresh(false);
            screenLock(false);

            Disposable disposable = Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())//
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> screenLock(true));

            addCompositeDisposable(disposable);

            onRefresh(getViewInterface().getSharedElementTransition(mGradientType), disposable);
        } else
        {
            if (mIsDeepLink == false)
            {
                setRefresh(true);
            }
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

        if (isRefresh() == true)
        {
            onRefresh(true);
        } else
        {
            addCompositeDisposable(mCartLocalImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
            {
                @Override
                public void accept(GourmetCart gourmetCart) throws Exception
                {
                    setToolbarGourmetCart(gourmetCart);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    getViewInterface().setToolbarCartMenusVisible(false);
                }
            }));
        }

        mAppResearch.onResume("고메", mGourmetIndex);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mAppResearch.onPause("고메", mGourmetIndex);
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
            case STATUS_FINISH:
                break;

            default:
                setStatus(STATUS_FINISH);

                if (getResultCode() == BaseActivity.RESULT_CODE_REFRESH)
                {
                    finish();
                    return true;
                }

                if (mIsUsedMultiTransition == true)
                {
                    if (lock() == true)
                    {
                        return true;
                    }

                    getViewInterface().setTransitionVisible(true);
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

        Util.restartApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case GourmetDetailActivity.REQUEST_CODE_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(GourmetDetailCalendarActivity.NAME_INTENT_EXTRA_DATA_VISIT_DATE) == true)
                    {
                        String visitDateTime = data.getStringExtra(GourmetDetailCalendarActivity.NAME_INTENT_EXTRA_DATA_VISIT_DATE);

                        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
                        {
                            return;
                        }

                        setGourmetBookDateTime(visitDateTime);
                        setRefresh(true);
                    }
                }
                break;
            }

            case GourmetDetailActivity.REQUEST_CODE_HAPPYTALK:
                break;

            case GourmetDetailActivity.REQUEST_CODE_CALL:
                break;

            case GourmetDetailActivity.REQUEST_CODE_PAYMENT:
                switch (resultCode)
                {
                    case BaseActivity.RESULT_CODE_REFRESH:
                        setRefresh(true);
                        break;

                    case BaseActivity.RESULT_CODE_BACK:
                        setResult(BaseActivity.RESULT_CODE_REFRESH);
                        onBackClick();
                        break;
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_ORDER:
            case GourmetDetailActivity.REQUEST_CODE_PROFILE_UPDATE:
                if (resultCode == Activity.RESULT_OK)
                {
                    addCompositeDisposable(mCartLocalImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
                    {
                        @Override
                        public void accept(GourmetCart gourmetCart) throws Exception
                        {
                            onBookingCartMenu(gourmetCart);
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {

                        }
                    }));

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_DOWNLOAD_COUPON:
                break;

            case GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_WISH:
                if (resultCode == Activity.RESULT_OK)
                {
                    onWishClick();

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_COUPON:
                if (resultCode == Activity.RESULT_OK)
                {
                    onDownloadCouponClick();

                    setResult(BaseActivity.RESULT_CODE_REFRESH);
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_MENU:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        addCompositeDisposable(mCartLocalImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
                        {
                            @Override
                            public void accept(GourmetCart gourmetCart) throws Exception
                            {
                                onBookingCartMenu(gourmetCart);
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(Throwable throwable) throws Exception
                            {

                            }
                        }));
                        break;

                    case Activity.RESULT_CANCELED:
                        if (data != null && data.hasExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_VISIT_TIME) == true)
                        {
                            getViewInterface().performVisitTimeClick(data.getStringExtra(GourmetMenusActivity.INTENT_EXTRA_DATA_VISIT_TIME));
                        }
                        break;

                    default:
                        break;
                }
                break;
            }
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

        if (mGourmetBookDateTime == null)
        {
            Util.restartApp(getActivity());
            return;
        }

        onRefresh(new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                observer.onNext(true);
                observer.onComplete();
            }
        }, null);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    protected void setResult(int resultCode)
    {
        if (getResultCode() == BaseActivity.RESULT_CODE_REFRESH)
        {
            super.setResult(BaseActivity.RESULT_CODE_REFRESH);
        } else
        {
            super.setResult(resultCode);
        }
    }

    @Override
    protected void setResult(int resultCode, Intent resultData)
    {
        if (getResultCode() == BaseActivity.RESULT_CODE_REFRESH)
        {
            super.setResult(BaseActivity.RESULT_CODE_REFRESH);
        } else
        {
            super.setResult(resultCode, resultData);
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

        mAnalytics.onEventShare(getActivity());
    }

    @Override
    public void onWishClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);

            Intent intent = LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_WISH);
        } else
        {
            boolean wish = !mGourmetDetail.myWish;
            int wishCount = wish ? mGourmetDetail.wishCount + 1 : mGourmetDetail.wishCount - 1;

            notifyWishChanged(wishCount, wish);

            if (wish == true)
            {
                addCompositeDisposable(mGourmetRemoteImpl.addWish(mGourmetDetail.index)//
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull WishResult wishResult) throws Exception
                        {
                            Intent intent = new Intent();
                            intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, true);
                            setResult(BaseActivity.RESULT_CODE_REFRESH, intent);

                            if (wishResult.success == true)
                            {
                                mGourmetDetail.myWish = true;
                                mGourmetDetail.wishCount++;

                                notifyWishChanged();

                                Observable<Boolean> observable = getViewInterface().showWishView(mGourmetDetail.myWish);

                                if (observable != null)
                                {
                                    addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                    {
                                        @Override
                                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                                        {
                                            unLockAll();
                                        }
                                    }));
                                } else
                                {
                                    unLockAll();
                                }

                                mAnalytics.onEventWishClick(getActivity(), mGourmetBookDateTime, mGourmetDetail, mPriceFromList, true);
                            } else
                            {
                                notifyWishChanged(mGourmetDetail.wishCount, mGourmetDetail.myWish);

                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), wishResult.message//
                                    , getString(R.string.dialog_btn_text_confirm), null);

                                unLockAll();
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                        {
                            onHandleError(throwable);

                            notifyWishChanged(mGourmetDetail.wishCount, mGourmetDetail.myWish);
                        }
                    }));
            } else
            {
                addCompositeDisposable(mGourmetRemoteImpl.removeWish(mGourmetDetail.index)//
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull WishResult wishResult) throws Exception
                        {
                            Intent intent = new Intent();
                            intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_WISH, false);
                            setResult(BaseActivity.RESULT_CODE_REFRESH, intent);

                            if (wishResult.success == true)
                            {
                                mGourmetDetail.myWish = false;
                                mGourmetDetail.wishCount--;

                                notifyWishChanged();

                                Observable<Boolean> observable = getViewInterface().showWishView(mGourmetDetail.myWish);

                                if (observable != null)
                                {
                                    addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                                    {
                                        @Override
                                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                                        {
                                            unLockAll();
                                        }
                                    }));
                                } else
                                {
                                    unLockAll();
                                }

                                mAnalytics.onEventWishClick(getActivity(), mGourmetBookDateTime, mGourmetDetail, mPriceFromList, false);
                            } else
                            {
                                notifyWishChanged(mGourmetDetail.wishCount, mGourmetDetail.myWish);

                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), wishResult.message//
                                    , getString(R.string.dialog_btn_text_confirm), null);

                                unLockAll();
                            }
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                        {
                            onHandleError(throwable);

                            notifyWishChanged(mGourmetDetail.wishCount, mGourmetDetail.myWish);
                        }
                    }));
            }
        }
    }

    @Override
    public void onShareKakaoClick()
    {
        if (mGourmetDetail == null || mGourmetBookDateTime == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String name = DailyUserPreference.getInstance(getActivity()).getName();

            String urlFormat = "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s&utm_source=share&utm_medium=gourmet_detail_kakaotalk";
            String longUrl = String.format(Locale.KOREA, urlFormat, mGourmetDetail.index //
                , mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String shortUrl) throws Exception
                {
                    unLockAll();

                    KakaoLinkManager.newInstance(getActivity()).shareGourmet(name //
                        , mGourmetDetail.name //
                        , mGourmetDetail.address//
                        , mGourmetDetail.index //
                        , mGourmetDetail.getImageInformationList() == null || mGourmetDetail.getImageInformationList().size() == 0 ? null : mGourmetDetail.getImageInformationList().get(0).getImageMap().bigUrl //
                        , shortUrl, mGourmetBookDateTime);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();

                    KakaoLinkManager.newInstance(getActivity()).shareGourmet(name //
                        , mGourmetDetail.name //
                        , mGourmetDetail.address//
                        , mGourmetDetail.index //
                        , mGourmetDetail.getImageInformationList() == null || mGourmetDetail.getImageInformationList().size() == 0 ? null : mGourmetDetail.getImageInformationList().get(0).getImageMap().bigUrl //
                        , "https://mobile.dailyhotel.co.kr/gourmet/" + mGourmetDetail.index, mGourmetBookDateTime);
                }
            }));

            mAnalytics.onEventShareKakaoClick(getActivity(), DailyHotel.isLogin()//
                , DailyUserPreference.getInstance(getActivity()).getType()//
                , DailyUserPreference.getInstance(getActivity()).isBenefitAlarm(), mGourmetDetail.index, mGourmetDetail.name);
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

            unLockAll();
        }
    }

    @Override
    public void onCopyLinkClick()
    {
        if (mGourmetDetail == null || mGourmetBookDateTime == null)
        {
            return;
        }

        try
        {
            String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s"//
                , mGourmetDetail.index, mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    DailyTextUtils.clipText(getActivity(), shortUrl);

                    DailyToast.showToast(getActivity(), R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    DailyTextUtils.clipText(getActivity(), "https://mobile.dailyhotel.co.kr/gourmet/" + mGourmetDetail.index);

                    DailyToast.showToast(getActivity(), R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);
                }
            }));

            mAnalytics.onEventCopyLinkClick(getActivity());
        } catch (Exception e)
        {
            unLockAll();

            ExLog.d(e.toString());
        }
    }

    @Override
    public void onMoreShareClick()
    {
        if (mGourmetDetail == null || mGourmetBookDateTime == null)
        {
            return;
        }

        try
        {
            String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s&utm_source=share&utm_medium=gourmet_detail_moretab"//
                , mGourmetDetail.index, mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd"));

            String name = DailyUserPreference.getInstance(getActivity()).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            final String message = getString(R.string.message_detail_gourmet_share_sms//
                , name, mGourmetDetail.name//
                , mGourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)")//
                , mGourmetDetail.address);

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + shortUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + "https://mobile.dailyhotel.co.kr/gourmet/" + mGourmetDetail.index);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }));

            mAnalytics.onEventMoreShareClick(getActivity());
        } catch (Exception e)
        {
            unLockAll();

            ExLog.d(e.toString());
        }
    }

    @Override
    public void onImageClick(int position)
    {
        if (mGourmetDetail == null || mGourmetDetail.getImageInformationList() == null//
            || mGourmetDetail.getImageInformationList().size() == 0 || lock() == true)
        {
            return;
        }

        ImageListAnalyticsParam analyticsParam = new ImageListAnalyticsParam();
        analyticsParam.serviceType = Constants.ServiceType.GOURMET;

        startActivityForResult(ImageListActivity.newInstance(getActivity(), mGourmetDetail.name//
            , mGourmetDetail.getImageInformationList(), position, analyticsParam), GourmetDetailActivity.REQUEST_CODE_IMAGE_LIST);

        mAnalytics.onEventImageClick(getActivity(), mGourmetDetail.name);
    }

    @Override
    public void onCalendarClick()
    {
        if (mGourmetBookDateTime == null || lock() == true)
        {
            return;
        }

        try
        {
            startCalendar(mCommonDateTime, mGourmetBookDateTime, mGourmetIndex, mSoldOutDateList, true);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            unLockAll();
        }
    }

    @Override
    public void onMapClick()
    {
        if (Util.isInstallGooglePlayService(getActivity()) == true)
        {
            if (mGourmetDetail == null || lock() == true)
            {
                return;
            }

            startActivityForResult(ZoomMapActivity.newInstance(getActivity()//
                , ZoomMapActivity.SourceType.GOURMET, mGourmetDetail.name, mGourmetDetail.address//
                , mGourmetDetail.latitude, mGourmetDetail.longitude, false), GourmetDetailActivity.REQUEST_CODE_MAP);
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

        if (mGourmetDetail != null)
        {
            mAnalytics.onEventMapClick(getActivity(), mGourmetDetail.name);
        }
    }

    @Override
    public void onClipAddressClick(String address)
    {
        DailyTextUtils.clipText(getActivity(), address);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT);

        if (mGourmetDetail != null)
        {
            mAnalytics.onEventClipAddressClick(getActivity(), mGourmetDetail.name);
        }
    }

    @Override
    public void onNavigatorClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();
        analyticsParam.category = AnalyticsManager.Category.GOURMET_BOOKINGS;
        analyticsParam.action = AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED;

        startActivityForResult(NavigatorDialogActivity.newInstance(getActivity(), mGourmetDetail.name//
            , mGourmetDetail.latitude, mGourmetDetail.longitude, false, analyticsParam), GourmetDetailActivity.REQUEST_CODE_NAVIGATOR);
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

        mAnalytics.onEventConciergeClick(getActivity());
    }

    @Override
    public void onActionButtonClick()
    {
        switch (mStatus)
        {
            case STATUS_SELECT_MENU:
                getViewInterface().performFirstMenuClick();
                break;

            case STATUS_BOOKING:
                break;

            default:
                break;
        }
    }

    @Override
    public void onConciergeFaqClick()
    {
        startActivity(FAQActivity.newInstance(getActivity()));

        mAnalytics.onEventFaqClick(getActivity());
    }

    @Override
    public void onConciergeHappyTalkClick()
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_DETAIL//
                , mGourmetDetail.index, 0, mGourmetDetail.name), GourmetDetailActivity.REQUEST_CODE_HAPPYTALK);
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

        mAnalytics.onEventHappyTalkClick(getActivity());
    }

    @Override
    public void onConciergeCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetDetailActivity.REQUEST_CODE_CALL);

        mAnalytics.onEventCallClick(getActivity());
    }

    @Override
    public void onTrueReviewClick()
    {
        if (mGourmetDetail == null || mReviewScores == null || lock() == true)
        {
            return;
        }

        TrueReviewAnalyticsParam analyticsParam = new TrueReviewAnalyticsParam();
        analyticsParam.category = mGourmetDetail.category;

        startActivityForResult(GourmetTrueReviewActivity.newInstance(getActivity(), mGourmetDetail.index, mReviewScores, analyticsParam), GourmetDetailActivity.REQUEST_CODE_TRUE_VIEW);

        mAnalytics.onEventTrueReviewClick(getActivity());
    }

    @Override
    public void onTrueVRClick()
    {
        if (mGourmetDetail == null || mTrueVRList == null || mTrueVRList.size() == 0 || lock() == true)
        {
            return;
        }

        if (DailyPreference.getInstance(getActivity()).isTrueVRCheckDataGuide() == false)
        {
            getViewInterface().showTrueVRDialog(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked)
                {
                    DailyPreference.getInstance(getActivity()).setTrueVRCheckDataGuide(checked);
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startActivityForResult(TrueVRActivity.newInstance(getActivity(), mGourmetDetail.index, mTrueVRList//
                        , Constants.PlaceType.FNB, mGourmetDetail.category), GourmetDetailActivity.REQUEST_CODE_TRUE_VR);
                }
            }, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLockAll();
                }
            });
        } else
        {
            startActivityForResult(TrueVRActivity.newInstance(getActivity(), mGourmetDetail.index, mTrueVRList//
                , Constants.PlaceType.HOTEL, mGourmetDetail.category), GourmetDetailActivity.REQUEST_CODE_TRUE_VR);
        }
    }

    @Override
    public void onDownloadCouponClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        mAnalytics.onEventDownloadCoupon(getActivity(), mGourmetDetail.name);

        if (DailyHotel.isLogin() == false)
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_detail_please_login), //
                getString(R.string.dialog_btn_login_for_benefit), getString(R.string.dialog_btn_text_close), //
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
                        startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_COUPON);

                        mAnalytics.onEventDownloadCouponByLogin(getActivity(), true);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mAnalytics.onEventDownloadCouponByLogin(getActivity(), false);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        mAnalytics.onEventDownloadCouponByLogin(getActivity(), false);
                    }
                }, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        unLockAll();
                    }
                }, true);
        } else
        {
            Intent intent = SelectGourmetCouponDialogActivity.newInstance(getActivity(), mGourmetBookDateTime.getVisitDateTime("yyyy-MM-dd")//
                , mGourmetDetail.index, mGourmetDetail.name);
            startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_DOWNLOAD_COUPON);
        }
    }

    @Override
    public void onMoreMenuClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        Observable<Boolean> observable;

        if (getViewInterface().isOpenedMoreMenuList() == true)
        {
            observable = getViewInterface().closeMoreMenuList();

            mAnalytics.onEventMoreMenuClick(getActivity(), true, mGourmetDetail.index);
        } else
        {
            observable = getViewInterface().openMoreMenuList();

            mAnalytics.onEventMoreMenuClick(getActivity(), false, mGourmetDetail.index);
        }

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        } else
        {
            unLockAll();
        }
    }

    @Override
    public void onMenuClick(int index, int position)
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        List<DetailImageInformation> imageInformationList = mGourmetDetail.getImageInformationList();
        String imageUrl = null;

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageMap().bigUrl;
        }

        startActivityForResult(GourmetMenusActivity.newInstance(getActivity(), mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mGourmetDetail.index, mGourmetDetail.name, mGourmetDetail.getGourmetMenuList(), position, (ArrayList) mOperationTimeList, mVisitTime, mGourmetDetail.category, imageUrl)//
            , GourmetDetailActivity.REQUEST_CODE_MENU);

        try
        {
            mAnalytics.onEventMenuClick(getActivity(), mGourmetDetail.getGourmetMenuList().get(index).index, position);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onHideWishTooltipClick()
    {
        DailyPreference.getInstance(getActivity()).setWishTooltip(false);
        getViewInterface().hideWishTooltip();
    }

    @Override
    public void onVisitTimeClick(String visitTime)
    {
        if (lock() == true)
        {
            return;
        }

        setVisitTime(visitTime, true);

        notifyOperationTimeChanged();

        unLockAll();
    }

    @Override
    public void onBookingClick()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mCartLocalImpl.getGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
        {
            @Override
            public void accept(GourmetCart gourmetCart) throws Exception
            {
                unLockAll();

                onBookingCartMenu(gourmetCart);

                mAnalytics.onEventToolbarBookingClick(getActivity(), gourmetCart.gourmetIndex);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onTrueAwardsClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        getViewInterface().showTrueAwardsDialog(mGourmetDetail.awards, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                unLockAll();
            }
        });

        mAnalytics.onEventTrueAwardsClick(getActivity(), mGourmetDetail.index);
    }

    private void setStatus(int status)
    {
        mStatus = status;

        getViewInterface().setBottomButtonLayout(status);
    }

    void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    void setReviewScores(ReviewScores reviewScores)
    {
        mReviewScores = reviewScores;
    }

    void setSoldOutDateList(List<Integer> soldOutList)
    {
        mSoldOutDateList = soldOutList;
    }

    void setTrueVRList(List<TrueVR> trueVRList)
    {
        mTrueVRList = trueVRList;
    }

    void setGourmetDetail(GourmetDetail gourmetDetail)
    {
        mGourmetDetail = gourmetDetail;

        mAnalytics.onScreen(getActivity(), mGourmetBookDateTime, gourmetDetail, mPriceFromList);
    }

    void setVisitTime(String visitTime, boolean clicked)
    {
        mVisitTime = visitTime;

        if (clicked == true)
        {
            mAnalytics.onEventVisitTimeClick(getActivity(), visitTime);
        }
    }

    void setOperationTimes(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime, List<GourmetMenu> gourmetMenuList)
    {
        if (commonDateTime == null || gourmetBookDateTime == null)
        {
            return;
        }

        if (mOperationTimeList == null)
        {
            mOperationTimeList = new ArrayList<>();
        }

        mOperationTimeList.clear();

        if (gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return;
        }

        try
        {
            TreeSet<String> visitTimeSet = new TreeSet<>();

            for (GourmetMenu gourmetMenu : gourmetMenuList)
            {
                visitTimeSet.addAll(gourmetMenu.getOperationTimeList());
            }

            mOperationTimeList.addAll(visitTimeSet);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyGourmetDetailChanged()
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        // 리스트에서 이미지가 큰사이즈가 없는 경우 상세에서도 해당 사이즈가 없기 때문에 고려해준다.
        try
        {
            if (mGourmetDetail.getImageInformationList() != null && mGourmetDetail.getImageInformationList().size() > 0)
            {
                mImageUrl = mGourmetDetail.getImageInformationList().get(0).getImageMap().bigUrl;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        getViewInterface().setGourmetDetail(mGourmetBookDateTime, mGourmetDetail, mOperationTimeList//
            , mReviewScores != null ? mReviewScores.reviewScoreTotalCount : 0, SHOWN_MENU_COUNT);

        if (mGourmetDetail.getGourmetMenuList() == null || mGourmetDetail.getGourmetMenuList().size() == 0)
        {
            setStatus(STATUS_SOLD_OUT);
        } else
        {
            setStatus(STATUS_SELECT_MENU);

            if (mGourmetDetail.getGourmetMenuList().size() > SHOWN_MENU_COUNT)
            {
                mAnalytics.onEventHasHiddenMenus(getActivity());
            }
        }

        if (DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0)
        {
            if (mTrueVRList != null && mTrueVRList.size() > 0)
            {
                getViewInterface().setTrueVRVisible(true);
            } else
            {
                getViewInterface().setTrueVRVisible(false);
            }
        } else
        {
            getViewInterface().setTrueVRVisible(false);
        }

        if (mShowCalendar == true)
        {
            mShowCalendar = false;

            if (mGourmetDetail.getGourmetMenuList() != null && mGourmetDetail.getGourmetMenuList().size() > 0)
            {
                onCalendarClick();
            }
        } else if (mShowTrueVR == true)
        {
            mShowTrueVR = false;

            if (DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0)
            {
                onTrueVRClick();
            } else
            {
                getViewInterface().showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        mIsDeepLink = false;
    }

    /**
     * @param visitDateTime ISO-8601
     */
    void setGourmetBookDateTime(String visitDateTime)
    {
        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            mGourmetBookDateTime = new GourmetBookDateTime();
        }

        try
        {
            mGourmetBookDateTime.setVisitDateTime(visitDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setGourmetBookDateTime(String visitDateTime, int afterDay)
    {
        if (DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            mGourmetBookDateTime = new GourmetBookDateTime();
        }

        try
        {
            mGourmetBookDateTime.setVisitDateTime(visitDateTime, afterDay);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyWishChanged()
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        getViewInterface().setWishCount(mGourmetDetail.wishCount);
        getViewInterface().setWishSelected(mGourmetDetail.myWish);
    }

    void notifyWishChanged(int wishCount, boolean myWish)
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        getViewInterface().setWishCount(wishCount);
        getViewInterface().setWishSelected(myWish);
    }

    void notifyOperationTimeChanged()
    {
        List<GourmetMenu> menuList = mGourmetDetail.getGourmetMenuList();

        if (menuList == null)
        {
            getViewInterface().setMenus(null, SHOWN_MENU_COUNT);
            return;
        }

        if (FULL_TIME.equalsIgnoreCase(mVisitTime) == true)
        {
            for (GourmetMenu gourmetMenu : menuList)
            {
                gourmetMenu.visible = true;
                gourmetMenu.orderCount = 0;
            }
        } else
        {
            for (GourmetMenu gourmetMenu : menuList)
            {
                gourmetMenu.visible = false;
                gourmetMenu.orderCount = 0;

                List<String> operationTimeList = gourmetMenu.getOperationTimeList();

                if (operationTimeList == null)
                {
                    continue;
                }

                for (String operationTime : gourmetMenu.getOperationTimeList())
                {
                    if (mVisitTime.equalsIgnoreCase(operationTime) == true)
                    {
                        gourmetMenu.visible = true;
                        break;
                    }
                }
            }
        }

        getViewInterface().setMenus(menuList, SHOWN_MENU_COUNT);
    }

    void showWishTooltip()
    {
        getViewInterface().showWishTooltip();

        addCompositeDisposable(Observable.timer(3, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())//
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>()
            {
                @Override
                public void accept(Long aLong) throws Exception
                {
                    DailyPreference.getInstance(getActivity()).setWishTooltip(false);
                    getViewInterface().hideWishTooltip();
                }
            }));
    }

    private void startCalendar(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime//
        , int gourmetIndex, List<Integer> soldOutList, boolean animation) throws Exception
    {
        if (commonDateTime == null || gourmetBookDateTime == null)
        {
            return;
        }

        String callByScreen = equalsCallingActivity(EventWebActivity.class) ? AnalyticsManager.Label.EVENT : AnalyticsManager.ValueType.DETAIL;

        Intent intent = GourmetDetailCalendarActivity.newInstance(getActivity(), //
            commonDateTime, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), gourmetIndex//
            , GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , callByScreen, (ArrayList) soldOutList, true, animation);

        startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_CALENDAR);

        mAnalytics.onEventCalendarClick(getActivity());
    }

    //    private void checkChangedPrice(boolean isDeepLink, GourmetDetail gourmetDetail, int listViewPrice, boolean compareListPrice)
    //    {
    //        if (gourmetDetail == null)
    //        {
    //            return;
    //        }
    //
    //        // 판매 완료 혹은 가격이 변동되었는지 조사한다
    //        List<GourmetMenu> menuList = gourmetDetail.getGourmetMenuList();
    //
    //        if (menuList == null || menuList.size() == 0)
    //        {
    //            if (mSoldOutFromList == false)
    //            {
    //                Intent intent = new Intent();
    //                intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SOLD_OUT, true);
    //                setResult(BaseActivity.RESULT_CODE_REFRESH, intent);
    //            }
    //
    //            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
    //                , getString(R.string.label_changing_date)//
    //                , v -> onCalendarClick(), null, true);
    //
    //            mAnalytics.onEventChangedPrice(getActivity(), isDeepLink, gourmetDetail.name, true);
    //        } else
    //        {
    //            if (isDeepLink == false && compareListPrice == true)
    //            {
    //                boolean hasPrice = false;
    //
    //                if (listViewPrice == GourmetDetailActivity.NONE_PRICE)
    //                {
    //                    hasPrice = true;
    //                } else
    //                {
    //                    for (GourmetMenu menu : menuList)
    //                    {
    //                        if (listViewPrice == menu.discountPrice)
    //                        {
    //                            hasPrice = true;
    //                            break;
    //                        }
    //                    }
    //                }
    //
    //                if (hasPrice == false)
    //                {
    //                    if (mSoldOutFromList == false)
    //                    {
    //                        Intent intent = new Intent();
    //                        intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CHANGED_PRICE, true);
    //                        setResult(BaseActivity.RESULT_CODE_REFRESH, intent);
    //                    }
    //
    //                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_changed_price)//
    //                        , getString(R.string.dialog_btn_text_confirm), null);
    //
    //                    mAnalytics.onEventChangedPrice(getActivity(), isDeepLink, gourmetDetail.name, false);
    //                }
    //            }
    //        }
    //    }

    int checkChangedPrice(boolean isDeepLink, GourmetDetail gourmetDetail, int listViewPrice, boolean compareListPrice)
    {
        if (gourmetDetail == null)
        {
            return INVALID_GOURMET_SOLD_OUT;
        }

        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<GourmetMenu> menuList = gourmetDetail.getGourmetMenuList();

        if (menuList == null || menuList.size() == 0)
        {
            if (mSoldOutFromList == false)
            {
                Intent intent = new Intent();
                intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SOLD_OUT, true);
                setResult(BaseActivity.RESULT_CODE_REFRESH, intent);
            }

            mAnalytics.onEventChangedPrice(getActivity(), isDeepLink, gourmetDetail.name, true);

            return INVALID_GOURMET_SOLD_OUT;
        } else
        {
            if (isDeepLink == false && compareListPrice == true)
            {
                boolean hasPrice = false;

                if (listViewPrice == GourmetDetailActivity.NONE_PRICE)
                {
                    hasPrice = true;
                } else
                {
                    for (GourmetMenu menu : menuList)
                    {
                        if (listViewPrice == menu.discountPrice)
                        {
                            hasPrice = true;
                            break;
                        }
                    }
                }

                if (hasPrice == false)
                {
                    if (mSoldOutFromList == false)
                    {
                        Intent intent = new Intent();
                        intent.putExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CHANGED_PRICE, true);
                        setResult(BaseActivity.RESULT_CODE_REFRESH, intent);
                    }

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null);

                    mAnalytics.onEventChangedPrice(getActivity(), isDeepLink, gourmetDetail.name, false);

                    return INVALID_GOURMET_CHANGED_PRICE;
                }
            }
        }

        return VALID_GOURMET_CART_DEFAULT;
    }

    private void onRefresh(Observable<Boolean> observable, Disposable disposable)
    {
        if (observable == null)
        {
            return;
        }

        if (mGourmetBookDateTime == null)
        {
            Util.restartApp(getActivity());
            return;
        }

        addCompositeDisposable(Observable.zip(observable//
            , mGourmetRemoteImpl.getDetail(mGourmetIndex, mGourmetBookDateTime)//
            , mCalendarImpl.getGourmetUnavailableDates(mGourmetIndex, GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT, false)//
            , mGourmetRemoteImpl.getReviewScores(mGourmetIndex)//
            , mGourmetRemoteImpl.getTrueVR(mGourmetIndex)//
            , mCommonRemoteImpl.getCommonDateTime()//
            , mCartLocalImpl.getGourmetCart()//
            , new Function7<Boolean, GourmetDetail, List<Integer>, ReviewScores, List<TrueVR>, CommonDateTime, GourmetCart, GourmetCart>()
            {
                @Override
                public GourmetCart apply(@io.reactivex.annotations.NonNull Boolean aBoolean//
                    , @io.reactivex.annotations.NonNull GourmetDetail gourmetDetail//
                    , @io.reactivex.annotations.NonNull List<Integer> unavailableDates//
                    , @io.reactivex.annotations.NonNull ReviewScores reviewScores//
                    , @io.reactivex.annotations.NonNull List<TrueVR> trueVRList//
                    , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime//
                    , @io.reactivex.annotations.NonNull GourmetCart gourmetCart) throws Exception
                {
                    setCommonDateTime(commonDateTime);
                    setReviewScores(reviewScores);
                    setSoldOutDateList(unavailableDates);
                    setTrueVRList(trueVRList);
                    setGourmetDetail(gourmetDetail);
                    setOperationTimes(commonDateTime, mGourmetBookDateTime, gourmetDetail.getGourmetMenuList());
                    setVisitTime(FULL_TIME, false);

                    return gourmetCart;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetCart>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull GourmetCart gourmetCart) throws Exception
            {
                if (disposable != null)
                {
                    disposable.dispose();
                }

                int checkPriceResult = checkChangedPrice(mIsDeepLink, mGourmetDetail, mPriceFromList, mCheckChangedPrice == false);
                mCheckChangedPrice = false;

                int validCartResult = validGourmetCart(mGourmetIndex, gourmetCart, mGourmetDetail.getGourmetMenuList());

                switch (validCartResult)
                {
                    case INVALID_GOURMET_CART_VISIT_TIME:
                        showClearCartDialog(getString(R.string.message_gourmet_product_detail_after_visit_day));
                        break;

                    case INVALID_GOURMET_CART_QUANTITY:
                        showClearCartDialog(getString(R.string.message_gourmet_product_detail_insufficient_quantity));
                        break;

                    case INVALID_GOURMET_SOLD_OUT:
                        addCompositeDisposable(mCartLocalImpl.clearGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                        {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception
                            {
                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out_has_cart)//
                                    , getString(R.string.label_changing_date)//
                                    , v -> onCalendarClick(), null, true);
                            }
                        }));
                        break;

                    case INVALID_GOURMET_CART_CHANGED_PRICE:
                        addCompositeDisposable(mCartLocalImpl.setGourmetCart(gourmetCart).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                        {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception
                            {
                                getViewInterface().showSimpleDialog(null, getString(R.string.message_gourmet_detail_changed_price_has_cart), getString(R.string.dialog_btn_text_confirm), null);
                            }
                        }));
                        break;

                    default:
                        setToolbarGourmetCart(gourmetCart);

                        if (DailyPreference.getInstance(getActivity()).isWishTooltip() == true)
                        {
                            showWishTooltip();
                        }

                        switch (checkPriceResult)
                        {
                            case INVALID_GOURMET_SOLD_OUT:
                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out_has_cart)//
                                    , getString(R.string.label_changing_date)//
                                    , v -> onCalendarClick(), null, true);
                                break;

                            case INVALID_GOURMET_CHANGED_PRICE:
                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_changed_price)//
                                    , getString(R.string.dialog_btn_text_confirm), null);
                                break;
                        }
                        break;
                }

                notifyGourmetDetailChanged();
                notifyOperationTimeChanged();
                notifyWishChanged();

                unLockAll();

                if (mReviewScores != null && mReviewScores.reviewScoreTotalCount > 0)
                {
                    mAnalytics.onEventShowTrueReview(getActivity(), mGourmetIndex);
                }

                if (mGourmetDetail.couponPrice > 0)
                {
                    mAnalytics.onEventShowCoupon(getActivity(), mGourmetIndex);
                }

                if (mGourmetDetail.awards != null && DailyTextUtils.isTextEmpty(mGourmetDetail.awards.title) == false)
                {
                    mAnalytics.onEventTrueAwards(getActivity(), mGourmetDetail.index);
                }
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
    }

    void onBookingCartMenu(GourmetCart gourmetCart)
    {
        if (mGourmetDetail == null || gourmetCart == null || gourmetCart.getMenuCount() == 0 || lock() == true)
        {
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);

            Intent intent = LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_ORDER);
        } else
        {
            addCompositeDisposable(mProfileRemoteImpl.getProfile().subscribe(new Consumer<User>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull User user) throws Exception
                {
                    switch (Util.verifyUserInformation(user))
                    {
                        case Util.VERIFY_USER:
                            startPayment(mGourmetDetail, gourmetCart);
                            break;

                        case Util.VERIFY_DAILY_USER_NOT_VERIFY_PHONE:
                            startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                                , EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.phone)//
                                , GourmetDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                            break;

                        case Util.VERIFY_SOCIAL_USER_NOT_VERIFY:
                        case Util.VERIFY_SOCIAL_USER_NOT_VERIFY_EMAIL:
                            startActivityForResult(AddProfileSocialActivity.newInstance(getActivity()//
                                , new Customer(user), user.birthday), GourmetDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                            break;

                        case Util.VERIFY_SOCIAL_USER_NOT_VERIFY_PHONE:
                            startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity()//
                                , EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.phone)//
                                , GourmetDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
                            break;

                        default:
                            break;
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

        mAnalytics.onEventOrderClick(getActivity(), mGourmetBookDateTime, mGourmetDetail.index, mGourmetDetail.name//
            , mGourmetDetail.category, gourmetCart);
    }

    /**
     * @param gourmetCart
     * @return
     */
    int validGourmetCart(int gourmetIndex, GourmetCart gourmetCart, List<GourmetMenu> gourmetMenuList) throws Exception
    {
        if (gourmetCart == null || gourmetCart.getMenuCount() == 0)
        {
            return VALID_GOURMET_CART_NONE;
        }

        if (gourmetCart.gourmetIndex != gourmetIndex)
        {
            return VALID_GOURMET_CART_DEFAULT;
        }

        // 방문일이 다르면 비교하지 않는다.
        try
        {
            if (gourmetCart.equalsDay(mGourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)) != true)
            {
                return VALID_GOURMET_CART_DEFAULT;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return VALID_GOURMET_CART_DEFAULT;
        }

        if (mOperationTimeList == null || mOperationTimeList.size() == 0 || gourmetMenuList == null || gourmetMenuList.size() == 0)
        {
            return INVALID_GOURMET_SOLD_OUT;
        }

        // 방문 시간 체크, 첫번째 운영 시간 보다 작으면 안됨.
        if (DailyCalendar.compareDateTime(gourmetCart.visitTime, mOperationTimeList.get(0)) < 0)
        {
            return INVALID_GOURMET_CART_VISIT_TIME;
        } else
        {
            boolean changedPrice = false;

            for (GourmetCartMenu gourmetCartMenu : gourmetCart.getMenuList())
            {
                boolean hasMenu = false;
                boolean hasOrderQuantity = false;

                for (GourmetMenu gourmetMenu : gourmetMenuList)
                {
                    if (gourmetCartMenu.index == gourmetMenu.index)
                    {
                        hasMenu = true;

                        // 수량
                        if (gourmetCartMenu.count <= gourmetMenu.availableTicketNumbers)
                        {
                            hasOrderQuantity = true;
                        }

                        if (gourmetCartMenu.discountPrice != gourmetMenu.discountPrice)
                        {
                            changedPrice = true;

                            gourmetCartMenu.price = gourmetMenu.price;
                            gourmetCartMenu.discountPrice = gourmetMenu.discountPrice;
                        }
                    }
                }

                if (hasMenu == false || hasOrderQuantity == false)
                {
                    return INVALID_GOURMET_CART_QUANTITY;
                }
            }

            if (changedPrice == true)
            {
                return INVALID_GOURMET_CART_CHANGED_PRICE;
            }
        }

        return VALID_GOURMET_CART_DEFAULT;
    }

    void setToolbarGourmetCart(GourmetCart gourmetCart)
    {
        if (gourmetCart != null && gourmetCart.gourmetIndex == mGourmetIndex && gourmetCart.getTotalCount() > 0)
        {
            getViewInterface().setToolbarCartMenusVisible(true);
            getViewInterface().setToolbarCartMenusCount(gourmetCart.getTotalCount());
        } else
        {
            getViewInterface().setToolbarCartMenusVisible(false);
        }
    }

    void startPayment(GourmetDetail gourmetDetail, GourmetCart gourmetCart)
    {
        if (gourmetDetail == null || gourmetCart == null || gourmetCart.getMenuCount() == 0)
        {
            return;
        }

        startActivityForResult(GourmetPaymentActivity.newInstance(getActivity(), gourmetCart, mAnalytics.getStayPaymentAnalyticsParam(gourmetDetail, gourmetCart))//
            , GourmetDetailActivity.REQUEST_CODE_PAYMENT);
    }

    void showClearCartDialog(String message)
    {
        addCompositeDisposable(mCartLocalImpl.clearGourmetCart().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
            }
        }));

        getViewInterface().setToolbarCartMenusVisible(false);
    }
}
