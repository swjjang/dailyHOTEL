package com.twoheart.dailyhotel.screen.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.common.ExitActivity;
import com.twoheart.dailyhotel.screen.common.SatisfactionActivity;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.CloseOnBackPressed;

public class MainActivity extends BaseActivity implements Constants
{
    // Back 버튼을 두 번 눌러 핸들러 멤버 변수
    private CloseOnBackPressed mBackButtonHandler;

    private MainPresenter mMainPresenter;
    private MainFragmentManager mMainFragmentManager;
    private MenuBarLayout mMenuBarLayout;
    private Dialog mSettingNetworkDialog;
    private View mSplashLayout;

    private boolean mIsInitialization;
    private Handler mDelayTimeHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
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
            }
        }
    };

    public interface OnResponsePresenterListener
    {
        void updateNewEvent();

        void onSatisfactionGourmet(String ticketName, int reservationIndex, long checkInTime);

        void onSatisfactionHotel(String hotelName, int reservationIndex, long checkInTime, long checkOutTime);

        void onCheckServerResponse(String title, String message);

        void onAppVersionResponse(int maxVersion, int minVersion);

        void onConfigurationResponse();

        void onError();

        void onErrorResponse(VolleyError volleyError);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // URL 만들때 사용
        //        com.twoheart.dailyhotel.network.request.DailyHotelRequest.makeUrlEncoder();

        mIsInitialization = true;
        mMainPresenter = new MainPresenter(this, mOnResponsePresenterListener);

        VolleyHttpClient.cookieManagerCreate();
        //        DailyPreference.getInstance(this).removeDeepLink();
        DailyPreference.getInstance(this).setSettingRegion(PlaceType.HOTEL, false);
        DailyPreference.getInstance(this).setSettingRegion(PlaceType.FNB, false);

        // 이전의 비정상 종료에 의한 만료된 쿠키들이 있을 수 있으므로, SplashActivity에서 자동 로그인을
        // 처리하기 이전에 미리 이미 저장되어 있는 쿠키들을 정리한다.
        // android.content.pm.PackageManager$NameNotFoundException: com.google.android.webview
        try
        {
            if (CookieManager.getInstance().getCookie(VolleyHttpClient.URL_DAILYHOTEL_SERVER) != null)
            {
                VolleyHttpClient.destroyCookie();
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        initLayout();

        mDelayTimeHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mMainPresenter.requestCheckServer();

                // 3초안에 메인화면이 뜨지 않으면 프로그래스바가 나온다
                mDelayTimeHandler.sendEmptyMessageDelayed(0, 3000);
            }
        }, 500);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        mOnResponsePresenterListener.onConfigurationResponse();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_main);

        mSplashLayout = findViewById(R.id.splashLayout);

        ViewGroup bottomMenuBarLayout = (ViewGroup) findViewById(R.id.bottomMenuBarLayout);
        mMenuBarLayout = new MenuBarLayout(this, bottomMenuBarLayout, onMenuBarSelectedListener);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);
        mMainFragmentManager = new MainFragmentManager(this, contentLayout, new MenuBarLayout.MenuBarLayoutOnPageChangeListener(mMenuBarLayout));
        mBackButtonHandler = new CloseOnBackPressed(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mIsInitialization == true)
        {
            if (VolleyHttpClient.isAvailableNetwork() == false)
            {
                showDisabledNetworkPopup();
            }
        } else
        {
            if (mIsInitialization == false)
            {
                mMainPresenter.requestEvent();
            }
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
                mMainPresenter.requestGourmetIsExistRating();
                break;

            case CODE_REQUEST_ACTIVITY_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);
                }
                break;

            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
                if (resultCode == Activity.RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.REQUEST_CODE_PERMISSIONS_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Fragment fragment = mMainFragmentManager.getCurrentFragment();

                    if (fragment != null)
                    {
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
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

        VolleyHttpClient.destroyCookie();

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

    private void showDisabledNetworkPopup()
    {
        if (isFinishing() == true)
        {
            return;
        }

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

                if (VolleyHttpClient.isAvailableNetwork() == true)
                {
                    lockUI();
                    mMainPresenter.requestCheckServer();
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
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
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

        mDelayTimeHandler.removeMessages(0);
        mDelayTimeHandler.sendEmptyMessageDelayed(1, 3000);
        mIsInitialization = false;
        mMainPresenter.requestEvent();
    }

    private MenuBarLayout.OnMenuBarSelectedListener onMenuBarSelectedListener = new MenuBarLayout.OnMenuBarSelectedListener()
    {
        @Override
        public void onMenuSelected(int index)
        {
            String name = mMenuBarLayout.getName(mMainFragmentManager.getLastIndexFragment());

            switch (index)
            {
                case 0:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_HOTEL_FRAGMENT);

                    if (Util.isTextEmpty(name) == false)
                    {
                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.DAILY_HOTEL_CLICKED, name, null);
                    }
                    break;

                case 1:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_GOURMET_FRAGMENT);

                    String gourmetName = getString(R.string.menu_item_title_fnb);

                    // 같은 이름으로 올경우에는 처음 시작한 경우이다.
                    if (Util.isTextEmpty(name) == false && name.equalsIgnoreCase(gourmetName) == false)
                    {
                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.DAILY_GOURMET_CLICKED, name, null);
                    }
                    break;

                case 2:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_BOOKING_FRAGMENT);

                    if (Util.isTextEmpty(name) == false)
                    {
                        AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                            , AnalyticsManager.Action.BOOKING_STATUS_CLICKED, name, null);
                    }
                    break;

                case 3:
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT);

                    AnalyticsManager.getInstance(MainActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.MENU_CLICKED, name, null);
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

    private OnResponsePresenterListener mOnResponsePresenterListener = new OnResponsePresenterListener()
    {
        @Override
        public void updateNewEvent()
        {
            if (DailyPreference.getInstance(MainActivity.this).hasNewEvent() == true)
            {
                mMenuBarLayout.setNewIconVisible(true);
            } else
            {
                mMenuBarLayout.setNewIconVisible(false);
            }
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
        public void onError()
        {
            mDelayTimeHandler.removeMessages(0);

            MainActivity.this.onError();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            mDelayTimeHandler.removeMessages(0);

            MainActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onCheckServerResponse(String title, String message)
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

        @Override
        public void onAppVersionResponse(int maxVersion, int minVersion)
        {
            int currentVersion = Integer.parseInt(DailyHotel.VERSION.replace(".", ""));
            int skipMaxVersion = Integer.parseInt(DailyPreference.getInstance(MainActivity.this).getSkipVersion().replace(".", ""));

            ExLog.d("MIN / MAX / CUR / SKIP : " + minVersion + " / " + maxVersion + " / " + currentVersion + " / " + skipMaxVersion);

            if (minVersion > currentVersion)
            {
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

                        mMainPresenter.requestConfiguration();
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
                mMainPresenter.requestConfiguration();
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
                    || DailyDeepLink.getInstance().isBonusView() == true)
                {
                    mMainFragmentManager.select(MainFragmentManager.INDEX_INFORMATION_FRAGMENT);
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

                mMainPresenter.requestUserAlive();
            }
        }
    };
}
