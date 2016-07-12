package com.twoheart.dailyhotel.screen.main;

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

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.CloseOnBackPressed;
import com.twoheart.dailyhotel.screen.common.ExitActivity;
import com.twoheart.dailyhotel.screen.common.SatisfactionActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetCurationManager;
import com.twoheart.dailyhotel.screen.hotel.list.StayCurationManager;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AppboyManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends BaseActivity implements Constants
{
    public static final String BROADCAST_EVENT_UPDATE = " com.twoheart.dailyhotel.broadcastreceiver.EVENT_UPDATE";

    // Back 버튼을 두 번 눌러 핸들러 멤버 변수
    private CloseOnBackPressed mBackButtonHandler;
    private MainNetworkController mNetworkController;
    private MainFragmentManager mMainFragmentManager;
    private MenuBarLayout mMenuBarLayout;
    private Dialog mSettingNetworkDialog;
    private View mSplashLayout;

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

        // URL 만들때 사용
        //                com.twoheart.dailyhotel.network.request.DailyHotelRequest.makeUrlEncoder();

        mIsInitialization = true;
        mNetworkController = new MainNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        //        DailyPreference.getInstance(this).removeDeepLink();
        DailyPreference.getInstance(this).setSettingRegion(PlaceType.HOTEL, false);
        DailyPreference.getInstance(this).setSettingRegion(PlaceType.FNB, false);

        String version = DailyPreference.getInstance(this).getAppVersion();
        String currentVersion = Util.getAppVersion(this);
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
            AppboyManager.setPushEnabled(this, true);
        } else
        {
            AppboyManager.setPushEnabled(this, false);
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

        ViewGroup bottomMenuBarLayout = (ViewGroup) findViewById(R.id.bottomMenuBarLayout);
        mMenuBarLayout = new MenuBarLayout(this, bottomMenuBarLayout, onMenuBarSelectedListener);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);
        mMainFragmentManager = new MainFragmentManager(this, contentLayout, mMenuBarLayout, new MenuBarLayout.MenuBarLayoutOnPageChangeListener(mMenuBarLayout));
        mBackButtonHandler = new CloseOnBackPressed(this);

        StayCurationManager.getInstance().clear();
        GourmetCurationManager.getInstance().clear();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mIsInitialization == true)
        {
            if (VolleyHttpClient.isAvailableNetwork(this) == false)
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
                mNetworkController.requestGourmetIsExistRating();
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
                break;

            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH:
            {
                if (mMainFragmentManager == null || mMainFragmentManager.getCurrentFragment() == null)
                {
                    Util.restartApp(this);
                    return;
                }

                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT);
                } else
                {
                    mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                mMainFragmentManager.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
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

        if (lastIndex == MainFragmentManager.INDEX_HOTEL_FRAGMENT || lastIndex == MainFragmentManager.INDEX_GOURMET_FRAGMENT)
        {
            if (mBackButtonHandler.onBackPressed())
            {
                ExitActivity.exitApplication(this);

                super.onBackPressed();
            }
        } else
        {
            mMainFragmentManager.select(mMainFragmentManager.getLastMainIndexFragment());
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

            mMainFragmentManager.select(MainFragmentManager.INDEX_ERROR_FRAGMENT);
        }
    }

    public void onRuntimeError(String message)
    {
        if (mIsInitialization == false)
        {
            if (DEBUG == false && Util.isTextEmpty(message) == false)
            {
                Crashlytics.logException(new RuntimeException("message"));
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
                mSettingNetworkDialog.dismiss();

                if (VolleyHttpClient.isAvailableNetwork(MainActivity.this) == true)
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
                mSettingNetworkDialog.dismiss();
            }
        };

        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    mSettingNetworkDialog.dismiss();
                    finish();
                    return true;
                }
                return false;
            }
        };

        mSettingNetworkDialog = createSimpleDialog(getString(R.string.dialog_btn_text_waiting)//
            , getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi)//
            , getString(R.string.dialog_btn_text_retry)//
            , getString(R.string.dialog_btn_text_setting), positiveListener, negativeListener);
        mSettingNetworkDialog.setOnKeyListener(keyListener);

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
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.DAILY_HOTEL_CLICKED, AnalyticsManager.Label.HOTEL_SCREEN, null);
                    break;

                case 1:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.DAILY_GOURMET_CLICKED, AnalyticsManager.Label.GOURMET_SCREEN, null);
                    break;

                case 2:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.BOOKING_STATUS_CLICKED, AnalyticsManager.Label.BOOKINGSTATUS_SCREEN, null);
                    break;

                case 3:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT);

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
        public void updateNewEvent(boolean isNewEvent, boolean isNewCoupon)
        {
            if (isNewEvent == false && isNewCoupon == false)
            {
                mMenuBarLayout.setNewIconVisible(false);
            } else
            {
                mMenuBarLayout.setNewIconVisible(true);
            }

            DailyPreference.getInstance(MainActivity.this).setNewEvent(isNewEvent);
            DailyPreference.getInstance(MainActivity.this).setNewCoupon(isNewCoupon);

            LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(new Intent(BROADCAST_EVENT_UPDATE));
        }

        @Override
        public void onSatisfactionGourmet(String ticketName, int reservationIndex, long checkInTime)
        {
            Intent intent = SatisfactionActivity.newInstance(MainActivity.this, ticketName, reservationIndex, checkInTime);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET);
        }

        @Override
        public void onSatisfactionHotel(String hotelName, int reservationIndex, long checkInTime, long checkOutTime)
        {
            Intent intent = SatisfactionActivity.newInstance(MainActivity.this, hotelName, reservationIndex, checkInTime, checkOutTime);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mDelayTimeHandler.removeMessages(0);
            unLockUI();

            MainActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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
        public void onCheckServerResponse(String title, String message)
        {
            mDelayTimeHandler.removeMessages(0);
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

        @Override
        public void onAppVersionResponse(int maxVersion, int minVersion)
        {
            int currentVersion = Integer.parseInt(DailyHotel.VERSION.replace(".", ""));
            int skipMaxVersion = Integer.parseInt(DailyPreference.getInstance(MainActivity.this).getSkipVersion().replace(".", ""));

            ExLog.d("MIN / MAX / CUR / SKIP : " + minVersion + " / " + maxVersion + " / " + currentVersion + " / " + skipMaxVersion);

            if (minVersion > currentVersion)
            {
                mDelayTimeHandler.removeMessages(0);
                unLockUI();

                // Android
                if (Util.isOverAPI15() == false)
                {
                    showSimpleDialog(getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_do_not_support_dailyhotel), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            finish();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            finish();
                        }
                    });

                    return;
                }

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

                showSimpleDialog(getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_please_update_new_version), getString(R.string.dialog_btn_text_update), posListener, cancelListener);

            } else if ((maxVersion > currentVersion) && (skipMaxVersion != maxVersion))
            {
                mDelayTimeHandler.removeMessages(0);
                unLockUI();

                // Android
                if (Util.isOverAPI15() == false)
                {
                    showSimpleDialog(getString(R.string.dialog_title_notice), getString(R.string.dialog_msg_do_not_support_dailyhotel), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            finish();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            finish();
                        }
                    });

                    return;
                }

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
                        String maxVersion = DailyPreference.getInstance(MainActivity.this).getMaxVersion();
                        DailyPreference.getInstance(MainActivity.this).setSkipVersion(maxVersion);

                        mNetworkController.requestConfiguration();
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

                showSimpleDialog(getString(R.string.dialog_title_notice)//
                    , getString(R.string.dialog_msg_update_now)//
                    , getString(R.string.dialog_btn_text_update)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , posListener, negListener, cancelListener, null, false);
            } else
            {
                mNetworkController.requestConfiguration();
            }
        }

        @Override
        public void onConfigurationResponse()
        {
            lockUI(false);

            finishSplash();

            if (DailyDeepLink.getInstance().isValidateLink() == true)
            {
                if (DailyDeepLink.getInstance().isHotelView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                } else if (DailyDeepLink.getInstance().isGourmetView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT);
                } else if (DailyDeepLink.getInstance().isBookingView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT);
                } else if (DailyDeepLink.getInstance().isEventView() == true//
                    || DailyDeepLink.getInstance().isBonusView() == true//
                    || DailyDeepLink.getInstance().isEventDetailView() == true//
                    || DailyDeepLink.getInstance().isCouponView() == true //
                    || DailyDeepLink.getInstance().isInformationView() == true //
                    || DailyDeepLink.getInstance().isRecommendFriendView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT);
                } else if (DailyDeepLink.getInstance().isSingUpView() == true)
                {
                    if (DailyHotel.isLogin() == false)
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT);
                    } else
                    {
                        DailyDeepLink.getInstance().clear();
                        mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                    }
                } else
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                }
            } else
            {
                String lastMenu = DailyPreference.getInstance(MainActivity.this).getLastMenu();

                if (getString(R.string.label_dailygourmet).equalsIgnoreCase(lastMenu) == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT);
                } else if (getString(R.string.label_dailyhotel).equalsIgnoreCase(lastMenu) == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                } else
                {
                    if (mMainFragmentManager.getLastIndexFragment() == MainFragmentManager.INDEX_GOURMET_FRAGMENT)
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT);
                    } else
                    {
                        mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                    }
                }

                if (DailyHotel.isLogin() == true)
                {
                    // session alive
                    // 호텔 평가를 위한 사용자 정보 조회
                    mNetworkController.requestUserInformation();
                } else
                {
                    // GCM 등록
                    Util.requestGoogleCloudMessaging(MainActivity.this, new Util.OnGoogleCloudMessagingListener()
                    {
                        @Override
                        public void onResult(String registrationId)
                        {
                            if (Util.isTextEmpty(registrationId) == true)
                            {
                                return;
                            }

                            mNetworkController.registerNotificationId(registrationId, null);
                        }
                    });

                    // 헤택이 Off 되어있는 경우 On으로 수정
                    if (DailyPreference.getInstance(MainActivity.this).isUserBenefitAlarm() == false//
                        && DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarm() == false)
                    {
                        mNetworkController.requestNoticeAgreement();
                    }
                }
            }
        }

        @Override
        public void onNoticeAgreement(String message, boolean isFirstTimeBuyer)
        {
            if (DailyPreference.getInstance(MainActivity.this).isUserBenefitAlarm() == true)
            {
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
                    return;
                }
            } else
            {
                if (DailyPreference.getInstance(MainActivity.this).isShowBenefitAlarm() == true)
                {
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
            AppboyManager.setPushEnabled(MainActivity.this, mIsBenefitAlarm);

            if (mIsBenefitAlarm == true)
            {
                AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.FIRST_NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);

                showSimpleDialog(getString(R.string.label_setting_alarm), agreeMessage, getString(R.string.dialog_btn_text_confirm), null);
            } else
            {
                AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , AnalyticsManager.Action.FIRST_NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.OFF, null);

                showSimpleDialog(getString(R.string.label_setting_alarm), cancelMessage, getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onCommonDateTime(long currentDateTime, long openDateTime, long closeDateTime)
        {
            try
            {
                // 요청하면서 CS운영시간도 같이 받아온다.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                String text = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(openDateTime))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(closeDateTime))));

                DailyPreference.getInstance(MainActivity.this).setOperationTimeMessage(text);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            String viewedEventTime = DailyPreference.getInstance(MainActivity.this).getViewedEventTime();
            String viewedCouponTime = DailyPreference.getInstance(MainActivity.this).getViewedCouponTime();

            currentDateTime -= 3600 * 1000 * 9;

            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
            calendar.setTimeInMillis(currentDateTime);

            String lastestTime = Util.getISO8601String(calendar.getTime());

            DailyPreference.getInstance(MainActivity.this).setLastestEventTime(lastestTime);
            DailyPreference.getInstance(MainActivity.this).setLastestCouponTime(lastestTime);

            if (Util.isTextEmpty(viewedEventTime) == true)
            {
                viewedEventTime = Util.getISO8601String(new Date(0L));
            }

            if (Util.isTextEmpty(viewedCouponTime) == true)
            {
                viewedCouponTime = Util.getISO8601String(new Date(0L));
            }

            mNetworkController.requestEventNCouponNewCount(viewedEventTime, viewedCouponTime);
        }
    };
}
