package com.twoheart.dailyhotel.screen.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyImageView;
import com.daily.dailyhotel.entity.Configurations;
import com.daily.dailyhotel.repository.local.ConfigLocalImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.FacebookRemoteImpl;
import com.daily.dailyhotel.repository.remote.KakaoRemoteImpl;
import com.daily.dailyhotel.screen.home.stay.inbound.list.StayTabActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.facebook.AccessToken;
import com.facebook.FacebookException;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.firebase.DailyRemoteConfig;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.screen.common.ChangeServerUrlDialog;
import com.twoheart.dailyhotel.screen.common.CloseOnBackPressed;
import com.twoheart.dailyhotel.screen.common.ExitActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.home.HomeFragment;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements Constants, BaseMenuNavigationFragment.OnScreenScrollChangeListener//
    , BaseMenuNavigationFragment.OnMenuChangeListener
{
    public static final String BROADCAST_EVENT_UPDATE = " com.twoheart.dailyhotel.broadcastreceiver.EVENT_UPDATE";

    private static final int HOLIDAY_DAYS = 365; // 휴일 날짜를 몇일까지 가져올지

    // Back 버튼을 두 번 눌러 핸들러 멤버 변수
    private CloseOnBackPressed mBackButtonHandler;
    MainNetworkController mNetworkController;
    MainFragmentManager mMainFragmentManager;
    MenuBarLayout mMenuBarLayout;
    private Dialog mSettingNetworkDialog;
    Dialog mAppPermissionsGuideDialog;
    View mSplashLayout;
    DailyDeepLink mDailyDeepLink;

    boolean mIsInitialization;
    boolean mIsBenefitAlarm;
    private int mDistance;
    private boolean mMenuBarAnimationDisabled;

    Handler mDelayTimeHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (isFinishing() == true)
            {
                return;
            }

            switch (msg.what)
            {
                case 0:
                    if (mIsInitialization == true)
                    {
                        lockUIImmediately();
                    }
                    break;

                case 1:
                    break;

                case 2:
                {
                    if (mSplashLayout != null && mSplashLayout.getVisibility() == View.VISIBLE)
                    {
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
                        animation.setDuration(400);
                        animation.setAnimationListener(new Animation.AnimationListener()
                        {
                            @Override
                            public void onAnimationStart(Animation animation)
                            {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation)
                            {
                                mSplashLayout.setVisibility(View.GONE);
                                mSplashLayout.setAnimation(null);

                                ViewGroup viewGroup = (ViewGroup) mSplashLayout.getParent();
                                viewGroup.removeView(mSplashLayout);
                                mSplashLayout = null;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation)
                            {

                            }
                        });

                        mSplashLayout.startAnimation(animation);
                    }
                    break;
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mIsInitialization = true;
        mNetworkController = new MainNetworkController(MainActivity.this, mNetworkTag, mOnNetworkControllerListener);

        // 현재 앱버전을 Analytics로..
        String version = DailyPreference.getInstance(this).getAppVersion();
        String currentVersion = VersionUtils.getAppVersionCode(this);
        if (currentVersion.equalsIgnoreCase(version) == false)
        {
            DailyPreference.getInstance(this).setAppVersion(currentVersion);
            AnalyticsManager.getInstance(this).currentAppVersion(currentVersion);
        }

        Intent intent = getIntent();

        initDeepLink(intent);

        initLayout();

        if (Util.checkGooglePlayServiceStatus(this) == false)
        {
            return;
        }

        if (DailyPreference.getInstance(this).isShowAppPermissionsGuide() == true)
        {
            showAppPermissionsGuideDialog(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DailyPreference.getInstance(MainActivity.this).setShowAppPermissionsGuide(false);

                    if (mAppPermissionsGuideDialog != null && mAppPermissionsGuideDialog.isShowing() == true)
                    {
                        mAppPermissionsGuideDialog.dismiss();
                    }

                    mNetworkController.requestCheckServer();

                    // 3초안에 메인화면이 뜨지 않으면 프로그래스바가 나온다
                    mDelayTimeHandler.sendEmptyMessageDelayed(0, 3000);
                }
            }, null);
        } else
        {
            mNetworkController.requestCheckServer();

            // 3초안에 메인화면이 뜨지 않으면 프로그래스바가 나온다
            mDelayTimeHandler.sendEmptyMessageDelayed(0, 3000);
        }

        // 로그인한 유저와 로그인하지 않은 유저의 판단값이 다르다.
        if (DailyUserPreference.getInstance(this).isBenefitAlarm() == true)
        {
            AnalyticsManager.getInstance(this).setPushEnabled(true, null);
        } else
        {
            AnalyticsManager.getInstance(this).setPushEnabled(false, null);
        }

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.DEVICE_INFO, "android", Build.MODEL, null);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initDeepLink(intent);

        processFinishSplash();
    }

    private void initDeepLink(Intent intent)
    {
        if (intent == null || intent.getData() == null)
        {
            return;
        }

        try
        {
            mDailyDeepLink = DailyDeepLink.getNewInstance(intent.getData());
        } catch (Exception e)
        {
            mDailyDeepLink = null;
        }
    }

    void processFinishSplash()
    {
        finishSplash();

        if (mDailyDeepLink != null)
        {
            parseDeepLink(mDailyDeepLink);

            clearDeepLink();
        } else
        {
            mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);

            if (DailyHotel.isLogin() == true)
            {
                // session alive
                // 호텔 평가를 위한 사용자 정보 조회
                // 해당 경우는 Analytics 의 startApplication을 해당 메소드의 onResponse에서 처리
                mNetworkController.requestUserInformation();
            } else
            {
                // 헤택이 Off 되어있는 경우 On으로 수정
                boolean isUserBenefitAlarm = DailyUserPreference.getInstance(MainActivity.this).isBenefitAlarm();
                if (isUserBenefitAlarm == false//
                    && DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarm() == false)
                {
                    mNetworkController.requestNoticeAgreement();
                    AnalyticsManager.getInstance(MainActivity.this).setPushEnabled(isUserBenefitAlarm, null);
                } else
                {
                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                    AnalyticsManager.getInstance(MainActivity.this).setPushEnabled(isUserBenefitAlarm, null);
                }

                AnalyticsManager.getInstance(MainActivity.this).startApplication();
            }
        }
    }

    void parseDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return;
        }

        if (dailyDeepLink.isInternalDeepLink() == true)
        {
            DailyInternalDeepLink internalDeepLink = (DailyInternalDeepLink) dailyDeepLink;
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, internalDeepLink.getDeepLink());

            if (internalDeepLink.isHomeView() == true)
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, true, null);
            } else if (internalDeepLink.isBookingDetailView() == true)
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_BOOKING_FRAGMENT, true, bundle);
            } else
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, true, null);
            }
        } else
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;
            Bundle bundle = new Bundle();
            bundle.putString(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK, externalDeepLink.getDeepLink());

            if (externalDeepLink.isHomeEventDetailView() == true//
                || externalDeepLink.isHomeRecommendationPlaceListView() == true//
                || externalDeepLink.isHotelListView() == true//
                || externalDeepLink.isHotelDetailView() == true//
                || externalDeepLink.isStaySearchResultView() == true//
                || externalDeepLink.isGourmetListView() == true//
                || externalDeepLink.isGourmetDetailView() == true//
                || externalDeepLink.isGourmetSearchResultView() == true//
                || externalDeepLink.isRecentlyWatchHotelView() == true//
                || externalDeepLink.isRecentlyWatchGourmetView() == true//
                || externalDeepLink.isWishListHotelView() == true//
                || externalDeepLink.isWishListGourmetView() == true//
                || externalDeepLink.isShortcutView() == true//
                || externalDeepLink.isStayOutboundSearchResultView() == true//
                || externalDeepLink.isCampaignTagListView() == true //
                || externalDeepLink.isPlaceDetailView() == true //
                || externalDeepLink.isSearchHomeView() == true//
                )
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, true, bundle);

            } else if (externalDeepLink.isBookingView() == true //
                || externalDeepLink.isBookingDetailView() == true)
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_BOOKING_FRAGMENT, true, bundle);
            } else if (externalDeepLink.isMyDailyView() == true //
                || externalDeepLink.isBonusView() == true//
                || externalDeepLink.isCouponView() == true //
                || externalDeepLink.isRegisterCouponView() == true //
                || externalDeepLink.isProfileView() == true//
                || externalDeepLink.isProfileBirthdayView() == true//
                || externalDeepLink.isRewardView() == true//
                || externalDeepLink.isLoginView() == true//
                )
            {
                if (externalDeepLink.isRewardView() == true)
                {
                    // 리워드 이벤트가 종료되면 리워드 화면에서 에러 처리
                    mMainFragmentManager.select(false, MainFragmentManager.INDEX_MYDAILY_FRAGMENT, true, bundle);
                } else
                {
                    mMainFragmentManager.select(false, MainFragmentManager.INDEX_MYDAILY_FRAGMENT, true, bundle);
                }
            } else if (externalDeepLink.isSingUpView() == true)
            {
                if (DailyHotel.isLogin() == false)
                {
                    mMainFragmentManager.select(false, MainFragmentManager.INDEX_MYDAILY_FRAGMENT, true, bundle);
                } else
                {
                    mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, true, bundle);
                }
            } else if (externalDeepLink.isEventView() == true//
                || externalDeepLink.isEventDetailView() == true//
                || externalDeepLink.isInformationView() == true //
                || externalDeepLink.isNoticeDetailView() == true//
                || externalDeepLink.isFAQView() == true//
                || externalDeepLink.isTermsNPolicyView() == true//
                )
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_INFORMATION_FRAGMENT, true, bundle);
            } else
            {
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, true, null);
            }

            AnalyticsManager.getInstance(MainActivity.this).startApplication();
        }
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        System.gc();
    }

    void clearDeepLink()
    {
        if (mDailyDeepLink == null)
        {
            return;
        }

        mDailyDeepLink.clear();
        mDailyDeepLink = null;
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_main);

        mSplashLayout = findViewById(R.id.splashLayout);
        mSplashLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // 앱이 데브 서버가 죽어서 안될때.ㅜㅜ
                if (Constants.DEBUG == true)
                {
                    startActivity(ChangeServerUrlDialog.newInstance(MainActivity.this));
                }
            }
        });

        loadSplash(mSplashLayout);

        ViewGroup bottomNavigationLayout = findViewById(R.id.bottomNavigationLayout);
        mMenuBarLayout = new MenuBarLayout(this, bottomNavigationLayout, onMenuBarSelectedListener);

        ViewGroup contentLayout = findViewById(R.id.contentLayout);
        mMainFragmentManager = new MainFragmentManager(this, contentLayout, new MenuBarLayout.MenuBarLayoutOnPageChangeListener(mMenuBarLayout));

        mBackButtonHandler = new CloseOnBackPressed(this);
    }

    private void loadSplash(View splashLayout)
    {
        if (splashLayout == null)
        {
            return;
        }

        String splashUpdateTime = DailyRemoteConfigPreference.getInstance(this).getRemoteConfigIntroImageVersion();

        DailyImageView imageView = splashLayout.findViewById(R.id.splashImageView);

        if (DailyTextUtils.isTextEmpty(splashUpdateTime) == true || Constants.DAILY_INTRO_DEFAULT_VERSION.compareTo(splashUpdateTime) > 0)
        {
            // 앱의 스플래쉬 버전이 비었거나 기본 버전일때
            imageView.setVectorImageResource(R.drawable.img_splash_logo);
        } else if (Constants.DAILY_INTRO_CURRENT_VERSION.compareTo(splashUpdateTime) >= 0)
        {
            // 앱의 스플래쉬 버전이 현재 버전과 같거나 작을때
            imageView.setPadding(0, 0, 0, 0);

            if (ScreenUtils.isTabletDevice(this) == true)
            {

                imageView.setBackgroundColor(getResources().getColor(R.color.white));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else
            {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            imageView.setImageResource(R.drawable.splash);
        } else
        {
            // 앱의 스플래쉬 버전이 현재 버전보다 높을때
            String fileName = Util.makeIntroImageFileName(splashUpdateTime);
            File file = new File(getCacheDir(), fileName);

            if (file.exists() == false)
            {
                DailyRemoteConfigPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
                imageView.setVectorImageResource(R.drawable.img_splash_logo);
            } else
            {
                try
                {
                    imageView.setPadding(0, 0, 0, 0);

                    if (ScreenUtils.isTabletDevice(this) == true)
                    {
                        imageView.setBackgroundColor(getResources().getColor(R.color.white));
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    } else
                    {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }

                    imageView.setImageURI(Uri.fromFile(file));
                } catch (Exception | OutOfMemoryError e)
                {
                    DailyRemoteConfigPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
                    imageView.setPadding(0, 0, 0, ScreenUtils.dpToPx(this, 26));
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setVectorImageResource(R.drawable.img_splash_logo);
                }
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mIsInitialization == true)
        {
            if (Util.isAvailableNetwork(this) == false)
            {
                mDelayTimeHandler.removeMessages(0);

                showDisabledNetworkPopup();
            }
        } else
        {
            mNetworkController.requestCommonDatetime();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);

        releaseUiComponent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL:
                mNetworkController.requestReviewGourmet();

                if (mMainFragmentManager != null)
                {
                    Fragment fragment = mMainFragmentManager.getCurrentFragment();

                    if (fragment instanceof HomeFragment)
                    {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
                mNetworkController.requestReviewStayOutbound();

                if (mMainFragmentManager != null)
                {
                    Fragment fragment = mMainFragmentManager.getCurrentFragment();

                    if (fragment instanceof HomeFragment)
                    {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_STAYOUTBOUND:
                mNetworkController.requestNoticeAgreement();

                if (mMainFragmentManager != null)
                {
                    Fragment fragment = mMainFragmentManager.getCurrentFragment();

                    if (fragment instanceof HomeFragment)
                    {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;

            // 해당 go home 목록이 MainActivity 목록과 동일해야함.
            case CODE_REQUEST_ACTIVITY_STAY:
            case CODE_REQUEST_ACTIVITY_GOURMET:
            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
            case CODE_REQUEST_ACTIVITY_COLLECTION:
            case CODE_REQUEST_ACTIVITY_STAY_OB_DETAIL:
            {
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        mMainFragmentManager.select(false, MainFragmentManager.INDEX_BOOKING_FRAGMENT, false, null);
                        break;

                    case CODE_RESULT_ACTIVITY_GO_HOME:
                        Fragment fragment = mMainFragmentManager.getCurrentFragment();

                        if (fragment instanceof HomeFragment)
                        {
                            fragment.onActivityResult(requestCode, resultCode, data);
                        } else
                        {
                            mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);
                        }
                        break;

                    default:
                        mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            {
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                if (data == null && (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY))
                {
                    mMainFragmentManager.select(false, MainFragmentManager.INDEX_BOOKING_FRAGMENT, false, null);
                } else
                {
                    mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE:
            {
                unLockUI();

                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        mMainFragmentManager.select(false, MainFragmentManager.INDEX_BOOKING_FRAGMENT, false, null);
                        break;

                    case CODE_RESULT_ACTIVITY_STAY_LIST:
                        mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);

                        startActivityForResult(StayTabActivity.newInstance(this, DailyCategoryType.STAY_ALL, false), Constants.CODE_REQUEST_ACTIVITY_STAY);
                        break;

                    case CODE_RESULT_ACTIVITY_GOURMET_LIST:
                        mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);

                        startActivityForResult(GourmetMainActivity.newInstance(this, null), Constants.CODE_REQUEST_ACTIVITY_GOURMET);
                        break;

                    // 해당 go home 목록이 HomeFragment 목록과 동일해야함.
                    case Constants.CODE_RESULT_ACTIVITY_GO_HOME:
                        if (mMainFragmentManager != null)
                        {
                            Fragment fragment = mMainFragmentManager.getCurrentFragment();

                            if (fragment instanceof HomeFragment)
                            {
                                fragment.onActivityResult(requestCode, resultCode, data);
                            } else
                            {
                                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);
                            }
                        }
                        break;

                    default:
                        mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                        break;
                }
                break;
            }

            // 해당 go home 목록이 HomeFragment 목록과 동일해야함.
            case Constants.CODE_REQUEST_ACTIVITY_GUIDE:
            case Constants.CODE_REQUEST_ACTIVITY_ABOUT:
            case Constants.CODE_REQUEST_ACTIVITY_SNS:
            case Constants.CODE_REQUEST_ACTIVITY_LIFESTYLE:
            case Constants.CODE_REQUEST_ACTIVITY_EVENT_LIST:
            case Constants.CODE_REQUEST_ACTIVITY_NOTICE_LIST:
            case Constants.CODE_REQUEST_ACTIVITY_FAQ:
            case Constants.CODE_REQUEST_ACTIVITY_CONTACTUS:
            case Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY:
            case Constants.CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_COUPONLIST:
            case Constants.CODE_REQUEST_ACTIVITY_BONUS:
            {
                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_GO_HOME:
                        if (mMainFragmentManager != null)
                        {
                            Fragment fragment = mMainFragmentManager.getCurrentFragment();

                            if (fragment instanceof HomeFragment)
                            {
                                fragment.onActivityResult(requestCode, resultCode, data);
                            } else
                            {
                                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);
                            }
                        }
                        break;

                    default:
                        if (mMainFragmentManager != null)
                        {
                            Fragment fragment = mMainFragmentManager.getCurrentFragment();

                            if (fragment != null)
                            {
                                fragment.onActivityResult(requestCode, resultCode, data);
                            }
                        }
                        break;
                }
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            return true;
        } else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed()
    {
        int lastIndex = mMainFragmentManager.getLastIndexFragment();

        if (lastIndex == MainFragmentManager.INDEX_HOME_FRAGMENT)
        {
            addCompositeDisposable(mBackButtonHandler.onBackPressed().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    if (aBoolean == true)
                    {
                        ExitActivity.exitApplication(MainActivity.this);

                        MainActivity.super.onBackPressed();
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {

                }
            }));
        } else
        {
            unLockUI();

            mMainFragmentManager.select(false, mMainFragmentManager.getLastMainIndexFragment(), false, null);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mSettingNetworkDialog != null)
        {
            if (mSettingNetworkDialog.isShowing() == true)
            {
                mSettingNetworkDialog.dismiss();
            }

            mSettingNetworkDialog = null;
        }

        if (mAppPermissionsGuideDialog != null)
        {
            if (mAppPermissionsGuideDialog.isShowing() == true)
            {
                mAppPermissionsGuideDialog.dismiss();
            }

            mAppPermissionsGuideDialog = null;
        }

        super.onDestroy();
    }

    @Override
    public void onError()
    {
        if (mIsInitialization == false)
        {
            super.onError();

            mMainFragmentManager.select(false, MainFragmentManager.INDEX_ERROR_FRAGMENT, false, null);
        }
    }

    void checkAppVersion(final String currentVersion, final String forceVersion)
    {
        if (DailyTextUtils.isTextEmpty(currentVersion, forceVersion) == true)
        {
            mOnNetworkControllerListener.onConfigurationResponse();
            return;
        }

        int appVersion = Integer.parseInt(VersionUtils.getAppVersionCode(MainActivity.this).replace(".", ""));
        int skipMaxVersion = Integer.parseInt(DailyPreference.getInstance(MainActivity.this).getSkipVersion().replace(".", ""));
        int forceVersionNumber = Integer.parseInt(forceVersion.replace(".", ""));
        int currentVersionNumber = Integer.parseInt(currentVersion.replace(".", ""));

        boolean isForceUpdate = forceVersionNumber > appVersion;
        boolean isUpdate = currentVersionNumber > appVersion;

        if (isForceUpdate == true)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                    marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

                    if (marketLaunch.resolveActivity(getPackageManager()) == null)
                    {
                        marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
                    }

                    startActivity(marketLaunch);
                    finish();
                }
            };

            DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            };

            String title;
            String message;

            try
            {
                String forceString = DailyRemoteConfigPreference.getInstance(MainActivity.this).getRemoteConfigUpdateForce();

                if (DailyTextUtils.isTextEmpty(forceString) == true)
                {
                    throw new NullPointerException();
                }

                JSONObject jsonObject = new JSONObject(forceString);

                title = jsonObject.getString("title");
                message = jsonObject.getString("message");
            } catch (Exception e)
            {
                title = getString(R.string.label_force_update);
                message = getString(R.string.dialog_msg_please_update_new_version);
            }

            showSimpleDialog(title, message, getString(R.string.dialog_btn_text_update), posListener, cancelListener);

        } else if (isUpdate == true && skipMaxVersion != currentVersionNumber)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                    marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

                    if (marketLaunch.resolveActivity(getPackageManager()) == null)
                    {
                        marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
                    }

                    startActivity(marketLaunch);
                    finish();
                }
            };

            final DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    DailyPreference.getInstance(MainActivity.this).setSkipVersion(currentVersion);

                    mOnNetworkControllerListener.onConfigurationResponse();
                }
            };

            View.OnClickListener negListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    cancelListener.onCancel(null);
                }
            };

            String title;
            String message;

            try
            {
                String optionalString = DailyRemoteConfigPreference.getInstance(MainActivity.this).getRemoteConfigUpdateOptional();

                if (DailyTextUtils.isTextEmpty(optionalString) == true)
                {
                    throw new NullPointerException();
                }

                JSONObject jsonObject = new JSONObject(optionalString);

                title = jsonObject.getString("title");
                message = jsonObject.getString("message");
            } catch (Exception e)
            {
                title = getString(R.string.label_option_update);
                message = getString(R.string.dialog_msg_update_now);
            }

            showSimpleDialog(title, message//
                , getString(R.string.dialog_btn_text_update)//
                , getString(R.string.dialog_btn_text_update_next)//
                , posListener, negListener, cancelListener, null, false);
        } else
        {
            mOnNetworkControllerListener.onConfigurationResponse();
        }
    }

    void showDisabledNetworkPopup()
    {
        if (isFinishing() == true)
        {
            return;
        }

        mDelayTimeHandler.removeMessages(0);
        unLockUI();

        if (mSettingNetworkDialog != null)
        {
            if (mSettingNetworkDialog.isShowing() == true)
            {
                mSettingNetworkDialog.dismiss();
            }

            mSettingNetworkDialog = null;
        }

        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Util.isAvailableNetwork(MainActivity.this) == true)
                {
                    lockUI();
                    mNetworkController.requestCheckServer();
                } else
                {
                    mDelayTimeHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showDisabledNetworkPopup();
                        }
                    }, 100);
                }
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        };

        mSettingNetworkDialog = createSimpleDialog(getString(R.string.dialog_btn_text_waiting)//
            , getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi)//
            , getString(R.string.dialog_btn_text_retry)//
            , getString(R.string.dialog_btn_text_setting), positiveListener, negativeListener);

        mSettingNetworkDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        });

        try
        {
            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mSettingNetworkDialog);

            mSettingNetworkDialog.show();

            mSettingNetworkDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void showAppPermissionsGuideDialog(View.OnClickListener onClickListener, DialogInterface.OnCancelListener onCancelListener)
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_app_permissions_guide, null, false);

        mAppPermissionsGuideDialog = new Dialog(this);
        mAppPermissionsGuideDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAppPermissionsGuideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mAppPermissionsGuideDialog.setCanceledOnTouchOutside(false);

        mAppPermissionsGuideDialog.setCancelable(false);
        mAppPermissionsGuideDialog.setOnCancelListener(onCancelListener);

        View confirmTextView = dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(onClickListener);

        try
        {
            mAppPermissionsGuideDialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mAppPermissionsGuideDialog);

            mAppPermissionsGuideDialog.show();

            mAppPermissionsGuideDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void finishSplash()
    {
        mDelayTimeHandler.sendEmptyMessageDelayed(2, 2000);
        mDelayTimeHandler.removeMessages(0);
        mIsInitialization = false;
        mNetworkController.requestCommonDatetime();
    }

    @Override
    public void onScrollChange(ViewGroup scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
    {
        if (mMenuBarLayout == null || mMenuBarAnimationDisabled == true)
        {
            return;
        }

        final int MAX_MOVE_DISTANCE = ScreenUtils.dpToPx(this, 20);
        final int MIN_MOVE_DISTANCE = 2;

        if (Math.abs(mDistance) > MAX_MOVE_DISTANCE)
        {
            if (mDistance > 0)
            {
                mMenuBarLayout.hideMenuBarAnimation();
                mDistance = 0;
            } else if (mDistance < 0)
            {
                mMenuBarLayout.showMenuBarAnimation(false);
                mDistance = 0;
            }
        } else
        {
            mDistance += (scrollY - oldScrollY);

            if (Math.abs(scrollY - oldScrollY) <= MIN_MOVE_DISTANCE)
            {
                mDistance = 0;
            }
        }

        // 하단에 도챡했는지..
        if (scrollView.getChildCount() > 0)
        {
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (view != null && view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) == 0)
            {
                mMenuBarLayout.showMenuBarAnimation(true);
                mDistance = 0;
            }
        }
    }

    @Override
    public void onScrollState(boolean disabled)
    {
        mMenuBarAnimationDisabled = disabled;

        if (disabled == true)
        {
            showMenuBar();
        }
    }

    /**
     * @param changeMenu
     * @param changeScreen ActivityResult 결과로 사용했는데 따로 정의할지 애매함..
     */
    @Override
    public void onMenu(int changeMenu, int changeScreen)
    {
        switch (changeMenu)
        {
            case MainFragmentManager.INDEX_HOME_FRAGMENT:
            case MainFragmentManager.INDEX_BOOKING_FRAGMENT:
            case MainFragmentManager.INDEX_MYDAILY_FRAGMENT:
            case MainFragmentManager.INDEX_INFORMATION_FRAGMENT:
            case MainFragmentManager.INDEX_ERROR_FRAGMENT:
                mMainFragmentManager.select(false, changeMenu, false, null);
                break;

            default:
                mMainFragmentManager.select(false, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);
                break;
        }

        switch (changeScreen)
        {
            case Constants.CODE_RESULT_ACTIVITY_STAY_LIST:
                startActivityForResult(StayTabActivity.newInstance(this, DailyCategoryType.STAY_ALL, false), Constants.CODE_REQUEST_ACTIVITY_STAY);
                break;

            case Constants.CODE_RESULT_ACTIVITY_GOURMET_LIST:
                startActivityForResult(GourmetMainActivity.newInstance(this, null), Constants.CODE_REQUEST_ACTIVITY_GOURMET);
                break;
        }
    }

    public void showMenuBar()
    {
        if (mMenuBarLayout != null)
        {
            mMenuBarLayout.showMenuBar();
        }

        mDistance = 0;
    }

    void analyticsRankABTest()
    {
        try
        {
            String rankTestName = DailyRemoteConfigPreference.getInstance(this).getKeyRemoteConfigStayRankTestName();

            if (DailyTextUtils.isTextEmpty(rankTestName) == false)
            {
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.EXPERIMENT//
                    , AnalyticsManager.Action.RANKING, rankTestName, null);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private MenuBarLayout.OnMenuBarSelectedListener onMenuBarSelectedListener = new MenuBarLayout.OnMenuBarSelectedListener()
    {
        @Override
        public void onMenuSelected(boolean isCallMenuBar, int index, int previousIndex)
        {
            if (mMainFragmentManager.getLastIndexFragment() == index || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            // 메뉴가 변경될때마다 이벤트 여부를 체크한다.
            if (mIsInitialization == false)
            {
                mNetworkController.requestCommonDatetime();
            }

            switch (index)
            {
                // 홈
                case 0:
                    mMainFragmentManager.select(isCallMenuBar, MainFragmentManager.INDEX_HOME_FRAGMENT, false, null);

                    if (DailyHotel.isLogin() == true && DailyPreference.getInstance(MainActivity.this).isRequestReview() == false)
                    {
                        DailyPreference.getInstance(MainActivity.this).setIsRequestReview(true);
                        mNetworkController.requestReviewStay();
                    }

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.HOME_CLICK, getIndexName(previousIndex), null);
                    break;

                // 예약내역
                case 1:
                    mMainFragmentManager.select(isCallMenuBar, MainFragmentManager.INDEX_BOOKING_FRAGMENT, false, null);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.BOOKINGSTATUS_CLICK, getIndexName(previousIndex), null);
                    break;

                // 마이데일리
                case 2:
                    mMainFragmentManager.select(isCallMenuBar, MainFragmentManager.INDEX_MYDAILY_FRAGMENT, false, null);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.MYDAILY_CLICK, getIndexName(previousIndex), null);
                    break;

                // 더보기
                case 3:
                    mMainFragmentManager.select(isCallMenuBar, MainFragmentManager.INDEX_INFORMATION_FRAGMENT, false, null);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.MENU_CLICK, getIndexName(previousIndex), null);
                    break;
            }
        }

        @Override
        public void onMenuUnselected(boolean isCallMenuBar, int index)
        {
        }

        @Override
        public void onMenuReselected(boolean isCallMenuBar, int index)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            switch (index)
            {
                // 홈
                case 0:
                    if (mMainFragmentManager != null)
                    {
                        Fragment fragment = mMainFragmentManager.getCurrentFragment();

                        if (fragment instanceof HomeFragment && fragment.isResumed())
                        {
                            ((HomeFragment) fragment).forceRefreshing();
                        }
                    }
                    break;

                case 1:
                case 2:
                case 3:
                    if (mMainFragmentManager != null)
                    {
                        Fragment fragment = mMainFragmentManager.getCurrentFragment();

                        if (fragment instanceof BaseMenuNavigationFragment && fragment.isResumed())
                        {
                            ((BaseMenuNavigationFragment) fragment).scrollTop();
                        }
                    }
                    break;
            }
        }

        private String getIndexName(int index)
        {
            switch (index)
            {
                case 0:
                    return AnalyticsManager.Label.HOME;

                case 1:
                    return AnalyticsManager.Label.BOOKINGSTATUS;

                case 2:
                    return AnalyticsManager.Label.MYDAILY;

                case 3:
                    return AnalyticsManager.Label.MENU;

                default:
                    return AnalyticsManager.Label.HOME;
            }
        }
    };

    MainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new MainNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void updateNewEvent(boolean isNewEvent, boolean isNewCoupon, boolean isNewNotices)
        {
            if (isNewCoupon == true)
            {
                mMenuBarLayout.setMyDailyNewIconVisible(true);
            } else
            {
                mMenuBarLayout.setMyDailyNewIconVisible(false);
            }

            if (isNewEvent == false && isNewNotices == false && Util.hasNoticeNewList(MainActivity.this) == false)
            {
                mMenuBarLayout.setInformationNewIconVisible(false);
            } else
            {
                mMenuBarLayout.setInformationNewIconVisible(true);
            }

            DailyPreference.getInstance(MainActivity.this).setNewEvent(isNewEvent);
            DailyPreference.getInstance(MainActivity.this).setNewCoupon(isNewCoupon);
            DailyPreference.getInstance(MainActivity.this).setNewNotice(isNewNotices);

            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(BROADCAST_EVENT_UPDATE));
        }

        @Override
        public void onReviewGourmet(Review review)
        {
            Intent intent = ReviewActivity.newInstance(MainActivity.this, review, PlaceBookingDetail.ReviewStatusType.ADDABLE);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET);
        }

        @Override
        public void onReviewStay(Review review)
        {
            Intent intent = ReviewActivity.newInstance(MainActivity.this, review, PlaceBookingDetail.ReviewStatusType.ADDABLE);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL);
        }

        @Override
        public void onReviewStayOutbound(Review review)
        {
            Intent intent = ReviewActivity.newInstance(MainActivity.this, review, PlaceBookingDetail.ReviewStatusType.ADDABLE);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_STAYOUTBOUND);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onError(call, e, false);
        }

        @Override
        public void onError(Throwable e)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int magCode, String message)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onErrorPopupMessage(magCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onErrorResponse(call, response);
        }

        @Override
        public void onCheckServerResponse(String title, String message)
        {
            mDelayTimeHandler.removeMessages(0);

            // 메시지가 있을때 무조건 팝업을 발생한다.
            if (DailyTextUtils.isTextEmpty(title, message) == true)
            {
                addCompositeDisposable(new CommonRemoteImpl().getConfigurations().observeOn(Schedulers.newThread()).subscribe(new Consumer<Configurations>()
                {
                    @Override
                    public void accept(Configurations configurations) throws Exception
                    {
                        DailyRemoteConfigPreference.getInstance(MainActivity.this).setKeyRemoteConfigRewardStickerEnabled(configurations.activeReward);
                        new DailyRemoteConfig(MainActivity.this).requestRemoteConfig(new DailyRemoteConfig.OnCompleteListener()
                        {
                            @Override
                            public void onComplete(String currentVersion, String forceVersion)
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (DailyTextUtils.isTextEmpty(currentVersion, forceVersion) == true)
                                        {
                                            mNetworkController.requestVersion();
                                        } else
                                        {
                                            checkAppVersion(currentVersion, forceVersion);
                                        }

                                        analyticsRankABTest();
                                    }
                                });
                            }
                        });
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                onHandleError(throwable);
                            }
                        });
                    }
                }));
            } else
            {
                unLockUI();

                showSimpleDialog(title, message, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                }, null, false);
            }
        }

        @Override
        public void onAppVersionResponse(String currentVersion, String forceVersion)
        {
            checkAppVersion(currentVersion, forceVersion);
        }

        @Override
        public void onConfigurationResponse()
        {
            lockUI(false);

            Observable<Boolean> checkUserLoginObservable;

            final String userType;

            if (DailyHotel.isLogin() == true)
            {
                userType = DailyUserPreference.getInstance(MainActivity.this).getType();

                if (DailyTextUtils.isTextEmpty(userType) == false)
                {
                    switch (userType)
                    {
                        case Constants.KAKAO_USER:
                            checkUserLoginObservable = checkSessionKakaoUser();
                            break;

                        case Constants.FACEBOOK_USER:
                            checkUserLoginObservable = checkSessionFacebookUser();
                            break;

                        default:
                            checkUserLoginObservable = Observable.just(true);
                            break;
                    }
                } else
                {
                    checkUserLoginObservable = Observable.just(true);
                }
            } else
            {
                userType = null;

                checkUserLoginObservable = Observable.just(true);
            }

            addCompositeDisposable(checkUserLoginObservable.subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, Observable<Boolean>>()
            {
                @Override
                public Observable<Boolean> apply(Boolean sessionOpened) throws Exception
                {
                    if (sessionOpened == true)
                    {
                        return Observable.just(false);
                    } else
                    {
                        return new ConfigLocalImpl().clear(MainActivity.this);
                    }
                }
            }).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean clearUser) throws Exception
                {
                    if (clearUser == true)
                    {
                        new FacebookRemoteImpl().logOut();
                        new KakaoRemoteImpl().logOut();

                        if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
                        {
                            unLockUI();

                            showSimpleDialog(null//
                                , getString(R.string.message_home_closed_sns_session)//
                                , getString(R.string.dialog_btn_text_confirm)//
                                , null, new DialogInterface.OnDismissListener()
                                {
                                    @Override
                                    public void onDismiss(DialogInterface dialog)
                                    {
                                        mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=24&v=login"));

                                        processFinishSplash();
                                    }
                                });
                        } else if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
                        {
                            mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=24&v=login"));

                            processFinishSplash();
                        } else
                        {
                            processFinishSplash();
                        }
                    } else
                    {
                        processFinishSplash();
                    }
                }
            }));
        }

        @Override
        public void onNoticeAgreement(String message, boolean isFirstTimeBuyer)
        {
            if (DailyUserPreference.getInstance(MainActivity.this).isBenefitAlarm() == true)
            {
                AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                return;
            }

            final boolean isLogined = DailyHotel.isLogin();

            if (isLogined == true)
            {
                if (isFirstTimeBuyer == true && DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarmFirstBuyer() == false)
                {
                    DailyPreference.getInstance(MainActivity.this).setShowBenefitAlarmFirstBuyer(true);
                } else if (DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarm() == false)
                {

                } else
                {
                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                    return;
                }
            } else
            {
                if (DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarm() == true)
                {
                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                    return;
                }
            }

            // 혜택
            showSimpleDialogType01(getString(R.string.label_setting_alarm), message, getString(R.string.label_now_setting_alarm), getString(R.string.label_after_setting_alarm)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mIsBenefitAlarm = true;
                        mNetworkController.requestNoticeAgreementResult(true);

                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NOTIFICATION, "button_clicks", "now", null);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mIsBenefitAlarm = false;
                        mNetworkController.requestNoticeAgreementResult(false);

                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NOTIFICATION, "button_clicks", "later", null);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        mIsBenefitAlarm = false;
                        mNetworkController.requestNoticeAgreementResult(false);

                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NOTIFICATION, "button_clicks", "later", null);
                    }
                }, null, true);


            AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NOTIFICATION, "impression", "home", null);
        }

        @Override
        public void onNoticeAgreementResult(final String agreeMessage, final String cancelMessage)
        {
            DailyPreference.getInstance(MainActivity.this).setShowBenefitAlarm(true);
            DailyUserPreference.getInstance(MainActivity.this).setBenefitAlarm(mIsBenefitAlarm);
            AnalyticsManager.getInstance(MainActivity.this).setPushEnabled(mIsBenefitAlarm, AnalyticsManager.ValueType.LAUNCH);

            if (mIsBenefitAlarm == true)
            {
                AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.FIRST_NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);

                showSimpleDialog(getString(R.string.label_setting_alarm), agreeMessage, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                    }
                });
            } else
            {
                AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.FIRST_NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.OFF, null);

                showSimpleDialog(getString(R.string.label_setting_alarm), cancelMessage, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Screen.APP_LAUNCHED, null, null, null);
                    }
                });
            }
        }

        @Override
        public void onCommonDateTime(TodayDateTime todayDateTime)
        {
            try
            {
                // 요청하면서 CS운영시간도 같이 받아온다.
                String startHour = DailyCalendar.convertDateFormatString(todayDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "H");
                String endtHour = DailyCalendar.convertDateFormatString(todayDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "H");

                DailyPreference.getInstance(MainActivity.this).setOperationTime(String.format(Locale.KOREA, "%s,%s", startHour, endtHour));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            String viewedEventTime = DailyPreference.getInstance(MainActivity.this).getViewedEventTime();
            String viewedCouponTime = DailyPreference.getInstance(MainActivity.this).getViewedCouponTime();
            String viewedNoticeTime = DailyPreference.getInstance(MainActivity.this).getViewedNoticeTime();

            DailyPreference.getInstance(MainActivity.this).setLatestEventTime(todayDateTime.currentDateTime);
            DailyPreference.getInstance(MainActivity.this).setLatestCouponTime(todayDateTime.currentDateTime);
            DailyPreference.getInstance(MainActivity.this).setLatestNoticeTime(todayDateTime.currentDateTime);

            if (DailyTextUtils.isTextEmpty(viewedEventTime) == true)
            {
                viewedEventTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            if (DailyTextUtils.isTextEmpty(viewedCouponTime) == true)
            {
                viewedCouponTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            if (DailyTextUtils.isTextEmpty(viewedNoticeTime) == true)
            {
                viewedNoticeTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            try
            {
                Calendar startDayCalendar = DailyCalendar.getInstance();
                DailyCalendar.setCalendarDateString(startDayCalendar, todayDateTime.dailyDateTime, -1);
                String startDay = DailyCalendar.format(startDayCalendar.getTime(), "yyyy-MM-dd");

                // 같은날짜에는 중복으로 요청하지 않는다.
                if (startDay.equalsIgnoreCase(DailyPreference.getInstance(MainActivity.this).getCheckCalendarHolidays()) == false)
                {
                    // 90일을 미리 얻어온다.
                    Calendar endDayCalendar = DailyCalendar.getInstance();
                    DailyCalendar.setCalendarDateString(endDayCalendar, todayDateTime.dailyDateTime, HOLIDAY_DAYS);
                    String endDay = DailyCalendar.format(endDayCalendar.getTime(), "yyyy-MM-dd");

                    mNetworkController.requestHoliday(startDay, endDay);

                    // 하루에 한번 휴일을 얻을때 해피톡 카테고리도 같이 얻는다.
                    mNetworkController.requestHappyTalkCategory();
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

            mNetworkController.requestEventNCouponNNoticeNewCount(viewedEventTime, viewedCouponTime, viewedNoticeTime);
        }

        @Override
        public void onUserProfileBenefit(boolean isExceedBonus)
        {
            DailyUserPreference.getInstance(MainActivity.this).setExceedBonus(isExceedBonus);
            AnalyticsManager.getInstance(MainActivity.this).setExceedBonus(isExceedBonus);

            mNetworkController.requestReviewStay();

            DailyPreference.getInstance(MainActivity.this).setIsRequestReview(true);
        }

        @Override
        public void onHolidays(String startDay, String holidays)
        {
            DailyPreference.getInstance(MainActivity.this).setCheckCalendarHolidays(startDay);

            if (DailyTextUtils.isTextEmpty(holidays) == true)
            {
                holidays = "";
            }

            DailyPreference.getInstance(MainActivity.this).setCalendarHolidays(holidays);
        }

        @Override
        public void onHappyTalkCategory(String categorys)
        {
            if (DailyTextUtils.isTextEmpty(categorys) == true)
            {
                return;
            }

            DailyPreference.getInstance(MainActivity.this).setHappyTalkCategory(categorys);
        }

        private Observable checkSessionKakaoUser()
        {
            return new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    UserManagement.getInstance().requestMe(new MeResponseCallback()
                    {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult)
                        {
                            observer.onNext(false);
                            observer.onComplete();
                        }

                        @Override
                        public void onNotSignedUp()
                        {
                            observer.onNext(false);
                            observer.onComplete();
                        }

                        @Override
                        public void onSuccess(UserProfile result)
                        {
                            observer.onNext(true);
                            observer.onComplete();
                        }
                    });
                }
            };
        }

        private Observable checkSessionFacebookUser()
        {
            return new Observable<Boolean>()
            {
                @Override
                protected void subscribeActual(Observer<? super Boolean> observer)
                {
                    AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback()
                    {
                        @Override
                        public void OnTokenRefreshed(AccessToken accessToken)
                        {
                            observer.onNext(true);
                            observer.onComplete();
                        }

                        @Override
                        public void OnTokenRefreshFailed(FacebookException exception)
                        {
                            observer.onNext(false);
                            observer.onComplete();
                        }
                    });
                }
            };
        }
    };
}
