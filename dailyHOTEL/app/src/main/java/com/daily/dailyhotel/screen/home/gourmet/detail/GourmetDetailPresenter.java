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

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.GourmetMenu;
import com.daily.dailyhotel.entity.ReviewScores;
import com.daily.dailyhotel.entity.WishResult;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.screen.common.call.CallDialogActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.util.RecentlyPlaceUtil;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.util.AppResearch;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Function6;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetDetailPresenter extends BaseExceptionPresenter<GourmetDetailActivity, GourmetDetailViewInterface>//
    implements GourmetDetailView.OnEventListener
{
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    public static final int STATUS_NONE = 0;
    public static final int STATUS_BOOKING = 1;
    public static final int STATUS_SELECT_MENU = 2;
    public static final int STATUS_SOLD_OUT = 3;
    public static final int STATUS_FINISH = 4;

    private GourmetDetailAnalyticsInterface mAnalytics;

    private GourmetRemoteImpl mGourmetRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;
    private CalendarImpl mCalendarImpl;

    private int mGourmetIndex, mListPrice;
    private String mGourmetName, mCategory;
    private String mImageUrl;
    private GourmetBookDateTime mGourmetBookDateTime;
    private CommonDateTime mCommonDateTime;
    private GourmetDetail mGourmetDetail;
    private ReviewScores mReviewScores;

    private int mStatus = STATUS_NONE;

    private boolean mIsUsedMultiTransition;
    private boolean mIsDeepLink;
    private boolean mCheckChangedPrice;
    private int mGradientType;
    private List<Integer> mSoldOutDateList;

    private DailyDeepLink mDailyDeepLink;
    private AppResearch mAppResearch;

    public interface GourmetDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
        void setAnalyticsParam(GourmetDetailAnalyticsParam analyticsParam);

        GourmetDetailAnalyticsParam getAnalyticsParam();

        void onScreen(Activity activity);
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

        setStatus(STATUS_NONE);

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
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);
            mGradientType = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CALL_GRADIENT_TYPE, GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            mIsDeepLink = false;

            mGourmetIndex = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_GOURMET_INDEX, -1);

            if (mGourmetIndex == -1)
            {
                return false;
            }

            mGourmetName = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_GOURMET_NAME);
            mImageUrl = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);
            mListPrice = intent.getIntExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_LIST_PRICE, GourmetDetailActivity.NONE_PRICE);

            String visitDate = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_VISIT_DATE);

            setGourmetBookDateTime(visitDate);

            mCategory = intent.getStringExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_CATEGORY);

            // 이미 판매 완료인 경우에는 가격을 검사할 필요가 없다.
            mCheckChangedPrice = intent.getBooleanExtra(GourmetDetailActivity.INTENT_EXTRA_DATA_SOLDOUT, false);
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
            getViewInterface().setInitializedTransLayout(mGourmetName, mImageUrl);
        } else
        {
            getViewInterface().setSharedElementTransitionEnabled(false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE);
            getViewInterface().setInitializedImage(mImageUrl);
        }

        RecentlyPlaceUtil.addRecentlyItem(getActivity(), Constants.ServiceType.GOURMET, mGourmetIndex, mGourmetName, null, mImageUrl, true);

        if (mIsUsedMultiTransition == true)
        {
            setRefresh(false);
            screenLock(false);

            Disposable disposable = Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.newThread())//
                .observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> screenLock(true));

            addCompositeDisposable(disposable);

            addCompositeDisposable(Observable.zip(getViewInterface().getSharedElementTransition()//
                , mGourmetRemoteImpl.getGourmetDetail(mGourmetIndex, mGourmetBookDateTime)//
                , mCalendarImpl.getGourmetUnavailableDates(mGourmetIndex, GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT, false)//
                , mGourmetRemoteImpl.getGourmetReviewScores(mGourmetIndex)//
                , mGourmetRemoteImpl.getGourmetHasCoupon(mGourmetIndex, mGourmetBookDateTime)//
                , mCommonRemoteImpl.getCommonDateTime()//
                , new Function6<Boolean, GourmetDetail, List<Integer>, ReviewScores, Boolean, CommonDateTime, GourmetDetail>()
                {
                    @Override
                    public GourmetDetail apply(@io.reactivex.annotations.NonNull Boolean aBoolean//
                        , @io.reactivex.annotations.NonNull GourmetDetail gourmetDetail//
                        , @io.reactivex.annotations.NonNull List<Integer> unavailableDates//
                        , @io.reactivex.annotations.NonNull ReviewScores reviewScores//
                        , @io.reactivex.annotations.NonNull Boolean hasCoupon//
                        , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                    {
                        setCommonDateTime(commonDateTime);
                        setReviewScores(reviewScores);
                        setSoldOutDateList(unavailableDates);

                        gourmetDetail.hasCoupon = hasCoupon;

                        setGourmetDetail(gourmetDetail);

                        return gourmetDetail;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetDetail>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull GourmetDetail gourmetDetail) throws Exception
                {
                    notifyGourmetDetailChanged();
                    notifyWishChanged();

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
                    onHandleError(throwable);
                }
            }));
        } else
        {
            setRefresh(true);
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

        if (isRefresh() == true)
        {
            onRefresh(true);
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
                if (resultCode == BaseActivity.RESULT_CODE_REFRESH)
                {
                    setRefresh(true);
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_PROFILE_UPDATE:
            case GourmetDetailActivity.REQUEST_CODE_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    onActionButtonClick();
                } else
                {
                }
                break;

            case GourmetDetailActivity.REQUEST_CODE_DOWNLOAD_COUPON:
                break;

            case GourmetDetailActivity.REQUEST_CODE_LOGIN_IN_BY_WISH:
                if (resultCode == Activity.RESULT_OK)
                {
                    onWishClick();
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

        addCompositeDisposable(Observable.zip(mGourmetRemoteImpl.getGourmetDetail(mGourmetIndex, mGourmetBookDateTime)//
            , mCalendarImpl.getGourmetUnavailableDates(mGourmetIndex, GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT, false)//
            , mGourmetRemoteImpl.getGourmetReviewScores(mGourmetIndex)//
            , mGourmetRemoteImpl.getGourmetHasCoupon(mGourmetIndex, mGourmetBookDateTime)//
            , mCommonRemoteImpl.getCommonDateTime()//
            , new Function5<GourmetDetail, List<Integer>, ReviewScores, Boolean, CommonDateTime, GourmetDetail>()
            {
                @Override
                public GourmetDetail apply(@io.reactivex.annotations.NonNull GourmetDetail gourmetDetail//
                    , @io.reactivex.annotations.NonNull List<Integer> unavailableDates//
                    , @io.reactivex.annotations.NonNull ReviewScores reviewScores//
                    , @io.reactivex.annotations.NonNull Boolean hasCoupon//
                    , @io.reactivex.annotations.NonNull CommonDateTime commonDateTime) throws Exception
                {
                    setCommonDateTime(commonDateTime);
                    setReviewScores(reviewScores);
                    setSoldOutDateList(unavailableDates);

                    gourmetDetail.hasCoupon = hasCoupon;

                    setGourmetDetail(gourmetDetail);

                    return gourmetDetail;
                }
            }).subscribe(new Consumer<GourmetDetail>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull GourmetDetail gourmetDetail) throws Exception
            {
                notifyGourmetDetailChanged();
                notifyWishChanged();

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
                addCompositeDisposable(mGourmetRemoteImpl.addGourmetWish(mGourmetDetail.index)//
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull WishResult wishResult) throws Exception
                    {
                        if (equalsCallingActivity(WishListTabActivity.class) == true)
                        {
                            setResult(BaseActivity.RESULT_CODE_REFRESH);
                        }

                        if (wishResult.success == true)
                        {
                            mGourmetDetail.myWish = true;
                            mGourmetDetail.wishCount++;

                            notifyWishChanged();

                            Observable<Boolean> observable = getViewInterface().showWishView(mGourmetDetail.myWish);

                            if (observable != null)
                            {
                                addCompositeDisposable(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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
                addCompositeDisposable(mGourmetRemoteImpl.removeGourmetWish(mGourmetDetail.index)//
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<WishResult>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull WishResult wishResult) throws Exception
                    {
                        if (equalsCallingActivity(WishListTabActivity.class) == true)
                        {
                            setResult(BaseActivity.RESULT_CODE_REFRESH);
                        }

                        if (wishResult.success == true)
                        {
                            mGourmetDetail.myWish = false;
                            mGourmetDetail.wishCount--;

                            notifyWishChanged();

                            Observable<Boolean> observable = getViewInterface().showWishView(mGourmetDetail.myWish);

                            if (observable != null)
                            {
                                addCompositeDisposable(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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
        if (mGourmetDetail == null || mGourmetBookDateTime == null || lock() == true)
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

            KakaoLinkManager.newInstance(getActivity()).shareGourmet(name, mGourmetDetail.name, mGourmetDetail.address//
                , mGourmetDetail.index //
                , mGourmetDetail.getImageInformationList().get(0).url //
                , mGourmetBookDateTime);

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

        unLockAll();
    }

    @Override
    public void onShareSmsClick()
    {
        if (mGourmetDetail == null || mGourmetBookDateTime == null || lock() == true)
        {
            return;
        }

        try
        {
            String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s"//
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
                , mGourmetBookDateTime.getVisitDateTime("yyyy.MM.dd (EEE)")//
                , mGourmetDetail.address);

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    Util.sendSms(getActivity(), message + shortUrl);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    Util.sendSms(getActivity(), message + "https://mobile.dailyhotel.co.kr/gourmet/" + mGourmetDetail.index);
                }
            }));
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

        //        startActivityForResult(ImageListActivity.newInstance(getActivity(), mStayOutboundDetail.name//
        //            , mStayOutboundDetail.getImageList(), position), StayOutboundDetailActivity.REQUEST_CODE_IMAGE_LIST);
    }

    @Override
    public void onImageSelected(int position)
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        getViewInterface().setDetailImageCaption(mGourmetDetail.getImageInformationList().get(position).caption);
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
            if (getActivity().isFinishing() == true || lock() == true)
            {
                return;
            }

            startActivityForResult(ZoomMapActivity.newInstance(getActivity()//
                , ZoomMapActivity.SourceType.HOTEL, mGourmetDetail.name, mGourmetDetail.address//
                , mGourmetDetail.latitude, mGourmetDetail.longitude, true), GourmetDetailActivity.REQUEST_CODE_MAP);
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
        if (getActivity().isFinishing() == true || lock() == true)
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
    public void onActionButtonClick()
    {
        //        switch (mStatus)
        //        {
        //            case STATUS_BOOKING:
        //                if (mSelectedRoom == null || lock() == true)
        //                {
        //                    return;
        //                }
        //
        //                if (DailyHotel.isLogin() == false)
        //                {
        //                    DailyToast.showToast(getActivity(), R.string.toast_msg_please_login, DailyToast.LENGTH_LONG);
        //
        //                    startActivityForResult(LoginActivity.newInstance(getActivity(), AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND)//
        //                        , StayOutboundDetailActivity.REQUEST_CODE_LOGIN);
        //                } else
        //                {
        //                    addCompositeDisposable(mProfileRemoteImpl.getProfile().subscribe(new Consumer<User>()
        //                    {
        //                        @Override
        //                        public void accept(@io.reactivex.annotations.NonNull User user) throws Exception
        //                        {
        //                            boolean isDailyUser = Constants.DAILY_USER.equalsIgnoreCase(user.userType);
        //                            StayOutboundPaymentAnalyticsParam analyticsParam = mAnalytics.getPaymentAnalyticsParam(getString(R.string.label_stay_outbound_detail_grade, (int) mStayOutboundDetail.rating)//
        //                                , mSelectedRoom.nonRefundable, mSelectedRoom.promotion);
        //
        //                            if (isDailyUser == true)
        //                            {
        //                                // 인증이 되어있지 않던가 기존에 인증이 되었는데 인증이 해지되었다.
        //                                if (Util.isValidatePhoneNumber(user.phone) == false || (user.verified == true && user.phoneVerified == false))
        //                                {
        //                                    startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity(), Integer.toString(user.index)//
        //                                        , EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.phone)//
        //                                        , StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
        //                                } else
        //                                {
        //                                    startActivityForResult(StayOutboundPaymentActivity.newInstance(getActivity(), mStayOutboundDetail.index//
        //                                        , mStayOutboundDetail.name, mImageUrl, mSelectedRoom.total//
        //                                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                                        , mPeople.numberOfAdults, mPeople.getChildAgeList()//
        //                                        , mSelectedRoom.roomName, mSelectedRoom.rateCode, mSelectedRoom.rateKey//
        //                                        , mSelectedRoom.roomTypeCode, mSelectedRoom.roomBedTypeId, analyticsParam)//
        //                                        , StayOutboundDetailActivity.REQUEST_CODE_PAYMENT);
        //                                }
        //                            } else
        //                            {
        //                                // 입력된 정보가 부족해.
        //                                if (DailyTextUtils.isTextEmpty(user.email, user.phone, user.name) == true)
        //                                {
        //                                    Customer customer = new Customer();
        //                                    customer.setEmail(user.email);
        //                                    customer.setName(user.name);
        //                                    customer.setPhone(user.phone);
        //                                    customer.setUserIdx(Integer.toString(user.index));
        //
        //                                    startActivityForResult(AddProfileSocialActivity.newInstance(getActivity()//
        //                                        , customer, user.birthday), StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
        //                                } else if (Util.isValidatePhoneNumber(user.phone) == false)
        //                                {
        //                                    startActivityForResult(EditProfilePhoneActivity.newInstance(getActivity(), Integer.toString(user.index)//
        //                                        , EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.phone)//
        //                                        , StayOutboundDetailActivity.REQUEST_CODE_PROFILE_UPDATE);
        //                                } else
        //                                {
        //                                    startActivityForResult(StayOutboundPaymentActivity.newInstance(getActivity(), mStayOutboundDetail.index//
        //                                        , mStayOutboundDetail.name, mImageUrl, mSelectedRoom.total//
        //                                        , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                                        , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
        //                                        , mPeople.numberOfAdults, mPeople.getChildAgeList()//
        //                                        , mSelectedRoom.roomName, mSelectedRoom.rateCode, mSelectedRoom.rateKey//
        //                                        , mSelectedRoom.roomTypeCode, mSelectedRoom.roomBedTypeId, analyticsParam)//
        //                                        , StayOutboundDetailActivity.REQUEST_CODE_PAYMENT);
        //                                }
        //                            }
        //                        }
        //                    }, new Consumer<Throwable>()
        //                    {
        //                        @Override
        //                        public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
        //                        {
        //                            onHandleError(throwable);
        //                        }
        //                    }));
        //                }
        //                break;
        //
        //            default:
        //                break;
        //        }
    }

    @Override
    public void onConciergeFaqClick()
    {
        startActivity(FAQActivity.newInstance(getActivity()));
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
    }

    @Override
    public void onConciergeCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetDetailActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onShareMapClick()
    {
        if (mGourmetDetail == null || lock() == true)
        {
            return;
        }

        Util.showShareMapDialog(getActivity(), mGourmetDetail.name//
            , mGourmetDetail.latitude, mGourmetDetail.longitude, false//
            , AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.GOURMET_DETAIL_NAVIGATION_APP_CLICKED//
            , null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLockAll();
                }
            });
    }

    @Override
    public void onReviewClick()
    {

    }

    @Override
    public void onDownloadCouponClick()
    {

    }

    @Override
    public void onMoreMenuClick()
    {

    }

    @Override
    public void onMenuClick(int index)
    {

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

    private void setReviewScores(ReviewScores reviewScores)
    {
        mReviewScores = reviewScores;
    }

    private void setSoldOutDateList(List<Integer> soldOutList)
    {
        mSoldOutDateList = soldOutList;
    }

    private void setGourmetDetail(GourmetDetail gourmetDetail)
    {
        mGourmetDetail = gourmetDetail;
    }

    private void notifyGourmetDetailChanged()
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
                mImageUrl = mGourmetDetail.getImageInformationList().get(0).url;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (mIsDeepLink == true)
        {
            getViewInterface().setToolbarTitle(mGourmetDetail.name);
        }

        getViewInterface().setGourmetDetail(mGourmetBookDateTime, mGourmetDetail, mReviewScores != null ? mReviewScores.reviewScoreTotalCount : 0);

        // 리스트 가격 변동은 진입시 한번 만 한다.
        checkChangedPrice(mIsDeepLink, mGourmetDetail, mListPrice, mCheckChangedPrice == false);
        mCheckChangedPrice = true;

        // 선택된 방이 없으면 처음 방으로 한다.
        if (mGourmetDetail.getGourmetMenuList() == null || mGourmetDetail.getGourmetMenuList().size() == 0)
        {
            setStatus(STATUS_SOLD_OUT);
        } else
        {
            setStatus(STATUS_SELECT_MENU);
        }

        mIsDeepLink = false;
    }

    /**
     * @param visitDateTime ISO-8601
     */
    private void setGourmetBookDateTime(String visitDateTime)
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

    private void notifyGourmetBookDateTimeChanged()
    {
        if (mGourmetBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(mGourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void notifyWishChanged()
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        getViewInterface().setWishCount(mGourmetDetail.wishCount);
        getViewInterface().setWishSelected(mGourmetDetail.myWish);
    }

    private void notifyWishChanged(int wishCount, boolean myWish)
    {
        if (mGourmetDetail == null)
        {
            return;
        }

        getViewInterface().setWishCount(wishCount);
        getViewInterface().setWishSelected(myWish);
    }

    private void startCalendar(CommonDateTime commonDateTime, GourmetBookDateTime gourmetBookDateTime//
        , int gourmetIndex, List<Integer> soldOutList, boolean animation) throws Exception
    {
        if (commonDateTime == null || gourmetBookDateTime == null)
        {
            return;
        }

        String callByScreen = mIsDeepLink ? AnalyticsManager.Label.EVENT : AnalyticsManager.ValueType.DETAIL;

        Intent intent = GourmetDetailCalendarActivity.newInstance(getActivity(), //
            commonDateTime, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), gourmetIndex//
            , GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , callByScreen, (ArrayList) soldOutList, true, animation);

        startActivityForResult(intent, GourmetDetailActivity.REQUEST_CODE_CALENDAR);
    }

    private void checkChangedPrice(boolean isDeepLink, GourmetDetail gourmetDetail, int listViewPrice, boolean compareListPrice)
    {
        if (gourmetDetail == null)
        {
            return;
        }

        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<GourmetMenu> menuList = gourmetDetail.getGourmetMenuList();

        if (menuList == null || menuList.size() == 0)
        {
            setResult(BaseActivity.RESULT_CODE_REFRESH);

            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
                , getString(R.string.label_changing_date)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onCalendarClick();
                    }
                }, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                    }
                }, true);
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
                    setResult(BaseActivity.RESULT_CODE_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_gourmet_detail_sold_out)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                            }
                        });
                }
            }
        }
    }
}
