package com.twoheart.dailyhotel.screen.main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.firebase.DailyRemoteConfig;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.screen.common.CloseOnBackPressed;
import com.twoheart.dailyhotel.screen.common.ExitActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements Constants
{
    public static final String BROADCAST_EVENT_UPDATE = " com.twoheart.dailyhotel.broadcastreceiver.EVENT_UPDATE";

    // Back 버튼을 두 번 눌러 핸들러 멤버 변수
    private CloseOnBackPressed mBackButtonHandler;
    private MainNetworkController mNetworkController;
    private MainFragmentManager mMainFragmentManager;
    private MenuBarLayout mMenuBarLayout;
    private Dialog mSettingNetworkDialog;
    private View mSplashLayout, mTooltipLayout;

    private boolean mIsInitialization;
    private boolean mIsBenefitAlarm;
    private Handler mDelayTimeHandler = new Handler()
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
                        lockUI();
                    }
                    break;

                case 1:
                    if (isVisibleLockUI() == true)
                    {
                        showLockUIProgress();
                    }
                    break;

                case 2:
                {
                    if (mSplashLayout.getVisibility() == View.VISIBLE)
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

        DailyPreference.getInstance(MainActivity.this).setSettingRegion(PlaceType.HOTEL, false);
        DailyPreference.getInstance(MainActivity.this).setSettingRegion(PlaceType.FNB, false);

        // 현재 앱버전을 Analytics로..
        String version = DailyPreference.getInstance(this).getAppVersion();
        String currentVersion = Util.getAppVersionCode(this);
        if (currentVersion.equalsIgnoreCase(version) == false)
        {
            DailyPreference.getInstance(this).setAppVersion(currentVersion);
            AnalyticsManager.getInstance(this).currentAppVersion(currentVersion);
        }

        initLayout();

        mNetworkController.requestCheckServer();

        // 3초안에 메인화면이 뜨지 않으면 프로그래스바가 나온다
        mDelayTimeHandler.sendEmptyMessageDelayed(0, 3000);

        // 로그인한 유저와 로그인하지 않은 유저의 판단값이 다르다.
        if (DailyPreference.getInstance(this).isUserBenefitAlarm() == true)
        {
            AnalyticsManager.getInstance(this).setPushEnabled(true, null);
        } else
        {
            AnalyticsManager.getInstance(this).setPushEnabled(false, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        mOnNetworkControllerListener.onConfigurationResponse();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        System.gc();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_main);

        mSplashLayout = findViewById(R.id.splashLayout);

        loadSplash(mSplashLayout);

        mTooltipLayout = findViewById(R.id.tooltipLayout);

        if (DailyPreference.getInstance(this).isViewWishListTooltip() == true)
        {
            hideAnimationTooltip();
        } else
        {
            DailyPreference.getInstance(this).setIsViewWishListTooltip(true);
            mTooltipLayout.setVisibility(View.VISIBLE);
            mTooltipLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    hideAnimationTooltip();
                }
            });
        }

        ViewGroup bottomMenuBarLayout = (ViewGroup) findViewById(R.id.bottomMenuBarLayout);
        mMenuBarLayout = new MenuBarLayout(this, bottomMenuBarLayout, onMenuBarSelectedListener);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);
        mMainFragmentManager = new MainFragmentManager(this, contentLayout, new PlaceMainFragment.OnMenuBarListener()
        {
            @Override
            public void onMenuBarTranslationY(float y)
            {
                mMenuBarLayout.setTranslationY(y);
            }

            @Override
            public void onMenuBarEnabled(boolean enabled)
            {
                mMenuBarLayout.setEnabled(enabled);
            }
        }, new MenuBarLayout.MenuBarLayoutOnPageChangeListener(mMenuBarLayout));

        mBackButtonHandler = new CloseOnBackPressed(this);
    }

    private void loadSplash(View splashLayout)
    {
        String splashVersion = DailyPreference.getInstance(this).getRemoteConfigIntroImageVersion();

        ImageView imageView = (ImageView) splashLayout.findViewById(R.id.splashImageView);

        if (Util.isTextEmpty(splashVersion) == true || Constants.DAILY_INTRO_DEFAULT_VERSION.equalsIgnoreCase(splashVersion) == true)
        {
            imageView.setImageResource(R.drawable.img_splash_logo);
        } else if (Constants.DAILY_INTRO_CURRENT_VERSION.equalsIgnoreCase(splashVersion) == true)
        {
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.splash);
        } else
        {
            String fileName = Util.makeIntroImageFileName(splashVersion);
            File file = new File(getCacheDir(), fileName);

            if (file.exists() == false)
            {
                DailyPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
                imageView.setImageResource(R.drawable.img_splash_logo);
            } else
            {
                try
                {
                    imageView.setPadding(0, 0, 0, 0);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageURI(Uri.fromFile(file));
                } catch (Exception e)
                {
                    DailyPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
                    imageView.setPadding(0, 0, 0, Util.dpToPx(this, 26));
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.img_splash_logo);
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
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
                mNetworkController.requestNoticeAgreement();
                break;

            case CODE_REQUEST_ACTIVITY_REGIONLIST:
            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
            case CODE_REQUEST_ACTIVITY_COLLECTION:
            {
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT, false);
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
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT, false);
                        break;

                    case CODE_RESULT_ACTIVITY_STAY_LIST:
                        mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, false);
                        break;

                    case CODE_RESULT_ACTIVITY_GOURMET_LIST:
                        mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, false);
                        break;

                    default:
                        mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                        break;
                }
                break;
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

        if (lastIndex == MainFragmentManager.INDEX_HOTEL_FRAGMENT || lastIndex == MainFragmentManager.INDEX_GOURMET_FRAGMENT)
        {
            if (mBackButtonHandler.onBackPressed())
            {
                ExitActivity.exitApplication(this);

                super.onBackPressed();
            }
        } else
        {
            mMainFragmentManager.select(mMainFragmentManager.getLastMainIndexFragment(), false);
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

        super.onDestroy();
    }

    @Override
    public void onError()
    {
        if (mIsInitialization == false)
        {
            super.onError();

            mMainFragmentManager.select(MainFragmentManager.INDEX_ERROR_FRAGMENT, false);
        }
    }

    private void hideAnimationTooltip()
    {
        if (mTooltipLayout.getTag() != null)
        {
            return;
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mTooltipLayout, "alpha", 1.0f, 0.0f);

        mTooltipLayout.setTag(objectAnimator);

        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(300);
        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                mTooltipLayout.setTag(null);
                mTooltipLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });

        objectAnimator.start();
    }

    private void checkAppVersion(final String currentVersion, final String forceVersion)
    {
        if (Util.isTextEmpty(currentVersion, forceVersion) == true)
        {
            mOnNetworkControllerListener.onConfigurationResponse();
            return;
        }

        int appVersion = Integer.parseInt(Util.getAppVersionCode(MainActivity.this).replace(".", ""));
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

            showSimpleDialog(getString(R.string.label_alarm_update), getString(R.string.dialog_msg_please_update_new_version), getString(R.string.dialog_btn_text_update), posListener, cancelListener);

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

            showSimpleDialog(getString(R.string.label_alarm_update)//
                , getString(R.string.dialog_msg_update_now)//
                , getString(R.string.dialog_btn_text_update)//
                , getString(R.string.dialog_btn_text_cancel)//
                , posListener, negListener, cancelListener, null, false);
        } else
        {
            mOnNetworkControllerListener.onConfigurationResponse();
        }
    }

    public void onRuntimeError(String message)
    {
        if (mIsInitialization == false)
        {
            if (DEBUG == false && Util.isTextEmpty(message) == false)
            {
                Crashlytics.logException(new RuntimeException(message));
            }

            onError();
        }
    }

    private void showDisabledNetworkPopup()
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
            mSettingNetworkDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void finishSplash()
    {
        mDelayTimeHandler.sendEmptyMessageDelayed(2, 2000);
        mDelayTimeHandler.removeMessages(0);
        mDelayTimeHandler.sendEmptyMessageDelayed(1, 3000);
        mIsInitialization = false;
        mNetworkController.requestCommonDatetime();
    }

    private MenuBarLayout.OnMenuBarSelectedListener onMenuBarSelectedListener = new MenuBarLayout.OnMenuBarSelectedListener()
    {
        @Override
        public void onMenuSelected(int index)
        {
            if (mMainFragmentManager.getLastIndexFragment() == index || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            switch (index)
            {
                case 0:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, false);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.DAILY_HOTEL_CLICKED, AnalyticsManager.Label.HOTEL_SCREEN, null);
                    break;

                case 1:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, false);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.DAILY_GOURMET_CLICKED, AnalyticsManager.Label.GOURMET_SCREEN, null);
                    break;

                case 2:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT, false);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.BOOKING_STATUS_CLICKED, AnalyticsManager.Label.BOOKINGSTATUS_SCREEN, null);
                    break;

                case 3:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT, false);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.MENU_CLICKED, AnalyticsManager.Label.MENU_SCREEN, null);
                    break;
            }
        }

        @Override
        public void onMenuUnselected(int index)
        {
        }

        @Override
        public void onMenuReselected(int intdex)
        {

        }
    };

    private MainNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new MainNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void updateNewEvent(boolean isNewEvent, boolean isNewCoupon, boolean isNewNotices)
        {
            if (isNewEvent == false && isNewCoupon == false && isNewNotices == false && Util.hasNoticeNewList(MainActivity.this) == false)
            {
                mMenuBarLayout.setNewIconVisible(false);
            } else
            {
                mMenuBarLayout.setNewIconVisible(true);
            }

            DailyPreference.getInstance(MainActivity.this).setNewEvent(isNewEvent);
            DailyPreference.getInstance(MainActivity.this).setNewCoupon(isNewCoupon);
            DailyPreference.getInstance(MainActivity.this).setNewNotice(isNewNotices);

            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(BROADCAST_EVENT_UPDATE));
        }

        @Override
        public void onReviewGourmet(Review review)
        {
            Intent intent = ReviewActivity.newInstance(MainActivity.this, review);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET);
        }

        @Override
        public void onReviewHotel(Review review)
        {
            Intent intent = ReviewActivity.newInstance(MainActivity.this, review);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL);
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
        public void onErrorResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onErrorResponse(call, response);
        }

        @Override
        public void onCheckServerResponse(String title, String message)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            if (Util.isTextEmpty(title, message) == true)
            {
                DailyRemoteConfig.getInstance(MainActivity.this).requestRemoteConfig(new DailyRemoteConfig.OnCompleteListener()
                {
                    @Override
                    public void onComplete(String currentVersion, String forceVersion)
                    {
                        if (Util.isTextEmpty(currentVersion, forceVersion) == true)
                        {
                            mNetworkController.requestVersion();
                        } else
                        {
                            checkAppVersion(currentVersion, forceVersion);
                        }
                    }
                });
            } else
            {
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

            finishSplash();

            PlaceType placeType = null;

            if (DailyDeepLink.getInstance().isValidateLink() == true)
            {
                if (DailyDeepLink.getInstance().isCollectionView() == true)
                {
                    String deepLinkPlaceType = DailyDeepLink.getInstance().getPlaceType();

                    if (Util.isTextEmpty(deepLinkPlaceType) == false)
                    {
                        switch (deepLinkPlaceType)
                        {
                            case "stay":
                                mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, true);
                                placeType = PlaceType.HOTEL;
                                break;

                            case "gourmet":
                                mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, true);
                                placeType = PlaceType.FNB;
                                break;
                        }
                    }
                } else if (DailyDeepLink.getInstance().isHotelView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, true);
                    placeType = PlaceType.HOTEL;
                } else if (DailyDeepLink.getInstance().isGourmetView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, true);
                    placeType = PlaceType.FNB;
                } else if (DailyDeepLink.getInstance().isBookingView() == true //
                    || DailyDeepLink.getInstance().isBookingDetailView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT, true);
                } else if (DailyDeepLink.getInstance().isEventView() == true//
                    || DailyDeepLink.getInstance().isBonusView() == true//
                    || DailyDeepLink.getInstance().isEventDetailView() == true//
                    || DailyDeepLink.getInstance().isCouponView() == true //
                    || DailyDeepLink.getInstance().isInformationView() == true //
                    || DailyDeepLink.getInstance().isRecommendFriendView() == true //
                    || DailyDeepLink.getInstance().isRegisterCouponView() == true //
                    || DailyDeepLink.getInstance().isNoticeDetailView() == true//
                    || DailyDeepLink.getInstance().isRecentlyWatchHotelView() == true//
                    || DailyDeepLink.getInstance().isRecentlyWatchGourmetView() == true//
                    || DailyDeepLink.getInstance().isFAQView() == true//
                    || DailyDeepLink.getInstance().isTermsNPolicyView() == true//
                    || DailyDeepLink.getInstance().isProfileView() == true//
                    || DailyDeepLink.getInstance().isProfileBirthdayView() == true//
                    || DailyDeepLink.getInstance().isWishlistHotelView() == true//
                    || DailyDeepLink.getInstance().isWishlistGourmetView() == true//
                    )
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT, true);
                } else if (DailyDeepLink.getInstance().isSingUpView() == true)
                {
                    if (DailyHotel.isLogin() == false)
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT, true);
                    } else
                    {
                        DailyDeepLink.getInstance().clear();
                        mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, true);
                        placeType = PlaceType.HOTEL;
                    }
                } else
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, true);
                    placeType = PlaceType.HOTEL;
                }

                if (placeType != null)
                {
                    String realProvinceName = DailyPreference.getInstance(MainActivity.this).getSelectedRegionTypeProvince(placeType);
                    String preferenceVersion = DailyPreference.getInstance(getApplicationContext()).getFirstAppVersion();
                    String currentAppVersion = Util.getAppVersionCode(MainActivity.this);

                    if (Util.isTextEmpty(realProvinceName) == true)
                    {
                        if (Util.isTextEmpty(preferenceVersion) == true //
                            || (Util.isTextEmpty(currentAppVersion) == false && currentAppVersion.equalsIgnoreCase(preferenceVersion) == true))
                        {
                            realProvinceName = "서울";
                        }
                    }

                    boolean isOverSeas = DailyPreference.getInstance(MainActivity.this).isSelectedOverseaRegion(placeType);
                    String overSeas = isOverSeas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;

                    AnalyticsManager.getInstance(MainActivity.this).onRegionChanged(overSeas, realProvinceName);
                }

                AnalyticsManager.getInstance(MainActivity.this).startApplication();
            } else
            {
                String lastMenu = DailyPreference.getInstance(MainActivity.this).getLastMenu();

                if (getString(R.string.label_dailygourmet).equalsIgnoreCase(lastMenu) == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, false);
                    placeType = PlaceType.FNB;
                } else if (getString(R.string.label_dailyhotel).equalsIgnoreCase(lastMenu) == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, false);
                    placeType = PlaceType.HOTEL;
                } else
                {
                    if (mMainFragmentManager.getLastIndexFragment() == MainFragmentManager.INDEX_GOURMET_FRAGMENT)
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT, false);
                        placeType = PlaceType.FNB;
                    } else
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT, false);
                        placeType = PlaceType.HOTEL;
                    }
                }

                if (placeType != null)
                {
                    String realProvinceName = DailyPreference.getInstance(MainActivity.this).getSelectedRegionTypeProvince(placeType);
                    String preferenceVersion = DailyPreference.getInstance(getApplicationContext()).getFirstAppVersion();
                    String currentAppVersion = Util.getAppVersionCode(MainActivity.this);

                    if (Util.isTextEmpty(realProvinceName) == true)
                    {
                        if (Util.isTextEmpty(preferenceVersion) == true //
                            || (Util.isTextEmpty(currentAppVersion) == false && currentAppVersion.equalsIgnoreCase(preferenceVersion) == true))
                        {
                            realProvinceName = "서울";
                        }
                    }

                    boolean isOverSeas = DailyPreference.getInstance(MainActivity.this).isSelectedOverseaRegion(placeType);
                    String overSeas = isOverSeas ? AnalyticsManager.KeyType.OVERSEAS : AnalyticsManager.KeyType.DOMESTIC;

                    AnalyticsManager.getInstance(MainActivity.this).onRegionChanged(overSeas, realProvinceName);
                }

                if (DailyHotel.isLogin() == true)
                {
                    // session alive
                    // 호텔 평가를 위한 사용자 정보 조회
                    // 해당 경우는 Analytics 의 startApplication을 해당 메소드의 onResponse에서 처리
                    mNetworkController.requestUserInformation();
                } else
                {
                    // 헤택이 Off 되어있는 경우 On으로 수정
                    boolean isUserBenefitAlarm = DailyPreference.getInstance(MainActivity.this).isUserBenefitAlarm();
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

                // 10초 후에 터치가 없으면 자동으로 사라짐.(기획서상 10초이지만 실제 보이기까지 여분의 시간을 넣음)
                mDelayTimeHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mTooltipLayout.getVisibility() != View.GONE)
                        {
                            hideAnimationTooltip();
                        }
                    }
                }, 10000);
            }
        }

        @Override
        public void onNoticeAgreement(String message, boolean isFirstTimeBuyer)
        {
            if (DailyPreference.getInstance(MainActivity.this).isUserBenefitAlarm() == true)
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
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mIsBenefitAlarm = false;
                        mNetworkController.requestNoticeAgreementResult(false);
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        mIsBenefitAlarm = false;
                        mNetworkController.requestNoticeAgreementResult(false);
                    }
                }, null, true);
        }

        @Override
        public void onNoticeAgreementResult(final String agreeMessage, final String cancelMessage)
        {
            DailyPreference.getInstance(MainActivity.this).setShowBenefitAlarm(true);
            DailyPreference.getInstance(MainActivity.this).setUserBenefitAlarm(mIsBenefitAlarm);
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
        public void onCommonDateTime(long currentDateTime, long openDateTime, long closeDateTime)
        {
            try
            {
                // 요청하면서 CS운영시간도 같이 받아온다.
                String startHour = DailyCalendar.format(openDateTime, "H", TimeZone.getTimeZone("GMT"));
                String endtHour = DailyCalendar.format(closeDateTime, "H", TimeZone.getTimeZone("GMT"));

                DailyPreference.getInstance(MainActivity.this).setOperationTime(String.format("%s,%s", startHour, endtHour));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            String viewedEventTime = DailyPreference.getInstance(MainActivity.this).getViewedEventTime();
            String viewedCouponTime = DailyPreference.getInstance(MainActivity.this).getViewedCouponTime();
            String viewedNoticeTime = DailyPreference.getInstance(MainActivity.this).getViewedNoticeTime();

            currentDateTime -= 3600 * 1000 * 9;

            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
            calendar.setTimeInMillis(currentDateTime);

            //            String lastestTime = Util.getISO8601String(calendar.getTime());
            String lastestTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            DailyPreference.getInstance(MainActivity.this).setLastestEventTime(lastestTime);
            DailyPreference.getInstance(MainActivity.this).setLastestCouponTime(lastestTime);
            DailyPreference.getInstance(MainActivity.this).setLastestNoticeTime(lastestTime);

            if (Util.isTextEmpty(viewedEventTime) == true)
            {
                viewedEventTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            if (Util.isTextEmpty(viewedCouponTime) == true)
            {
                viewedCouponTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            if (Util.isTextEmpty(viewedNoticeTime) == true)
            {
                viewedNoticeTime = DailyCalendar.format(new Date(0L), DailyCalendar.ISO_8601_FORMAT);
            }

            mNetworkController.requestEventNCouponNNoticeNewCount(viewedEventTime, viewedCouponTime, viewedNoticeTime);
        }
    };
}
