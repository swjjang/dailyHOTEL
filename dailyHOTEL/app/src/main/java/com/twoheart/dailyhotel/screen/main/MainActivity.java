package com.twoheart.dailyhotel.screen.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;

import com.android.volley.VolleyError;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.ExitActivity;
import com.twoheart.dailyhotel.activity.SplashActivity;
import com.twoheart.dailyhotel.fragment.BookingListFragment;
import com.twoheart.dailyhotel.fragment.ErrorFragment;
import com.twoheart.dailyhotel.screen.gourmetlist.GourmetMainFragment;
import com.twoheart.dailyhotel.screen.hotellist.HotelMainFragment;
import com.twoheart.dailyhotel.fragment.SettingFragment;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.CloseOnBackPressed;

public class MainActivity extends BaseActivity implements Constants
{
    private static final int ERROR_FRAGMENT = -1;
    private static final int INDEX_HOTEL_FRAGMENT = 0;
    private static final int INDEX_GOURMET_FRAGMENT = 1;
    private static final int INDEX_BOOKING_FRAGMENT = 2;
    private static final int INDEX_INFORMATION_FRAGMENT = 3;

    // 마지막으로 머물렀던 Fragment의 index
    private int mIndexLastFragment; // Error Fragment에서 다시 돌아올 때 필요.

    private FragmentManager mFragmentManager;
    private ViewGroup mContentLayout;

    // Back 버튼을 두 번 눌러 핸들러 멤버 변수
    private CloseOnBackPressed mBackButtonHandler;

    private MainPresenter mMainPresenter;
    private OnResponsePresenterListener mOnResponsePresenterListener;

    public interface OnResponsePresenterListener
    {
        void setNewIconVisible(boolean visible);

        void onSatisfactionGourmet(String ticketName, int reservationIndex, long checkInTime);

        void onSatisfactionHotel(String hotelName, int reservationIndex, long checkInTime, long checkOutTime);

        void lockUI();

        void unLockUI();

        void onError();

        void onErrorResponse(VolleyError volleyError);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // URL 만들때 사용
        //        com.twoheart.dailyhotel.network.request.DailyHotelRequest.makeUrlEncoder();

        mMainPresenter = new MainPresenter(this, mOnResponsePresenterListener);
        VolleyHttpClient.cookieManagerCreate();
        DailyPreference.getInstance(this).removeDeepLink();

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

        Uri uri = getIntent().getData();
        String title = checkDeepLink(uri);

        initLayout(title);

        // 스플래시 화면을 띄운다
        startActivityForResult(new Intent(this, SplashActivity.class), CODE_REQUEST_ACTIVITY_SPLASH);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initLayout(String title)
    {
        setContentView(R.layout.activity_main);

        //순서 중요
        // 1
//        Toolbar toolbar;
//
//        if (Util.isTextEmpty(title) == false)
//        {
//            toolbar = setActionBar(title, false);
//        } else
//        {
//            if (getString(R.string.label_dailygourmet).equalsIgnoreCase(DailyPreference.getInstance(this).getLastMenu()) == true)
//            {
//                toolbar = setActionBar(getString(R.string.actionbar_title_gourmet_list_frag), false);
//            } else
//            {
//                toolbar = setActionBar(getString(R.string.actionbar_title_hotel_list_frag), false);
//            }
//        }

        mContentLayout = (ViewGroup) findViewById(R.id.contentLayout);

        mFragmentManager = getSupportFragmentManager();
        mBackButtonHandler = new CloseOnBackPressed(this);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        DailyPreference.getInstance(this).removeDeepLink();

        Uri intentData = intent.getData();
        checkDeepLink(intentData);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mMainPresenter.requestEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_REQUEST_ACTIVITY_SPLASH)
        {
            switch (resultCode)
            {
                // 스플래시 화면이 정상적으로 종료되었을 경우
                case RESULT_OK:
                    break;

                default: // 스플래시가 비정상적으로 종료되었을 경우
                    super.finish(); // 어플리케이션(메인 화면)을 종료해버린다
                    return; // 메서드를 빠져나간다 - 호텔 평가를 수행하지 않음.
            }

            String deepLink = DailyPreference.getInstance(MainActivity.this).getDeepLink();

            if (Util.isTextEmpty(deepLink) == false)
            {
                if (deepLink.contains("hotelIndex") == true)
                {
                    selectMenu(INDEX_HOTEL_FRAGMENT);
                } else if (deepLink.contains("fnbIndex"))
                {
                    selectMenu(INDEX_GOURMET_FRAGMENT);
                } else
                {
                    String value = Util.getValueForLinkUrl(deepLink, "view");

                    if ("hotel".equalsIgnoreCase(value) == true)
                    {
                        selectMenu(INDEX_HOTEL_FRAGMENT);
                    } else if ("gourmet".equalsIgnoreCase(value) == true)
                    {
                        selectMenu(INDEX_GOURMET_FRAGMENT);
                    } else if ("bookings".equalsIgnoreCase(value) == true)
                    {
                        selectMenu(INDEX_BOOKING_FRAGMENT);
                    } else
                    {
                        selectMenu(INDEX_HOTEL_FRAGMENT);
                    }
                }
            } else
            {
                if (getString(R.string.label_dailygourmet).equalsIgnoreCase(DailyPreference.getInstance(this).getLastMenu()) == true)
                {
                    selectMenu(INDEX_GOURMET_FRAGMENT);
                } else if (getString(R.string.label_dailyhotel).equalsIgnoreCase(DailyPreference.getInstance(this).getLastMenu()) == true)
                {
                    selectMenu(INDEX_HOTEL_FRAGMENT);
                } else
                {
                    if (mIndexLastFragment == INDEX_GOURMET_FRAGMENT)
                    {
                        selectMenu(INDEX_GOURMET_FRAGMENT);
                    } else
                    {
                        selectMenu(INDEX_HOTEL_FRAGMENT);
                    }
                }

                mMainPresenter.requestUserAlive();
            }
        } else if (requestCode == CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL)
        {
            mMainPresenter.requestGourmetIsExistRating();
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
                    Fragment fragment = mFragmentManager.findFragmentByTag(String.valueOf(mIndexLastFragment));

                    if (fragment != null)
                    {
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    }
                }
                break;
        }
    }

    private String checkDeepLink(Uri uri)
    {
        if (uri == null)
        {
            return null;
        }

        final String KAKAOLINK = "kakaolink";
        final String DAILYHOTEL = "dailyhotel";

        String link = uri.toString();

        if (link.indexOf(KAKAOLINK) >= 0 || link.indexOf(DAILYHOTEL) >= 0)
        {
            String params = writeDeepLinkPreference(link);

            if (link.contains("hotelIndex") == true)
            {
                return getString(R.string.actionbar_title_hotel_list_frag);
            } else if (link.contains("fnbIndex") == true)
            {
                return getString(R.string.actionbar_title_gourmet_list_frag);
            } else
            {
                String value = Util.getValueForLinkUrl(params, "view");

                if ("hotel".equalsIgnoreCase(value) == true)
                {
                    return getString(R.string.actionbar_title_hotel_list_frag);
                } else if ("gourmet".equalsIgnoreCase(value) == true)
                {
                    return getString(R.string.actionbar_title_gourmet_list_frag);
                } else if ("bookings".equalsIgnoreCase(value) == true)
                {
                    return getString(R.string.actionbar_title_booking_list_frag);
                } else if ("bonus".equalsIgnoreCase(value) == true)
                {
                    return getString(R.string.actionbar_title_credit_frag);
                } else if ("event".equalsIgnoreCase(value) == true)
                {
                    return getString(R.string.actionbar_title_event_list_frag);
                }
            }
        }

        return null;
    }

    private String writeDeepLinkPreference(String link)
    {
        if (Util.isTextEmpty(link) == true)
        {
            return null;
        }

        int startIndex = link.indexOf('?') + 1;

        if (startIndex <= 0)
        {
            return null;
        }

        String param = link.substring(startIndex);
        DailyPreference.getInstance(this).setDeepLink(param);

        return param;
    }

    /**
     * 네비게이션 드로워 메뉴에서 선택할 수 있는 Fragment를 반환하는 메서드이다.
     *
     * @param index Fragment 리스트에 해당하는 index를 받는다.
     * @return 요청한 index에 해당하는 Fragment를 반환한다. => 기능 변경, 누를때마다 리프레시
     */
    public Fragment getFragment(int index)
    {
        switch (index)
        {
            case INDEX_HOTEL_FRAGMENT:
                return new HotelMainFragment();
            case INDEX_GOURMET_FRAGMENT:
                return new GourmetMainFragment();
            case INDEX_BOOKING_FRAGMENT:
                return new BookingListFragment();
            case INDEX_INFORMATION_FRAGMENT:
                return new SettingFragment();
        }

        return null;
    }

    /**
     * Fragment 컨테이너에서 해당 Fragment로 변경하여 표시한다.
     *
     * @param fragment Fragment 리스트에 보관된 Fragement들을 받는 것이 좋다.
     */
    public void replaceFragment(Fragment fragment, String tag)
    {
        try
        {
            clearFragmentBackStack();

            mFragmentManager.beginTransaction().replace(mContentLayout.getId(), fragment, tag).commitAllowingStateLoss();
        } catch (IllegalStateException e)
        {
            // 에러가 나는 경우 앱을 재부팅 시킨다.
            Util.restartApp(MainActivity.this);
        }

        // 액션바 위치를 다시 잡아준다.
    }

    /**
     * Fragment 컨테이너의 표시되는 Fragment를 변경할 때 Fragment 컨테이너에 적재된 Fragment들을 정리한다.
     */
    private void clearFragmentBackStack()
    {
        for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i)
        {
            mFragmentManager.popBackStackImmediate();
        }
    }

    @Deprecated
    public void removeFragment(Fragment fragment)
    {
        mFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
    }

    public void selectMenu(int index)
    {
        switch (index)
        {
            case R.drawable.selector_drawermenu_todayshotel:
                mIndexLastFragment = INDEX_HOTEL_FRAGMENT;

                DailyPreference.getInstance(this).setLastMenu(getString(R.string.label_dailyhotel));
                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_hotel_list_frag), (long) index);
                break;

            case R.drawable.selector_drawermenu_gourmet:
                mIndexLastFragment = INDEX_GOURMET_FRAGMENT;

                DailyPreference.getInstance(this).setLastMenu(getString(R.string.label_dailygourmet));
                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_gourmet_list_frag), (long) index);
                break;

            case R.drawable.selector_drawermenu_reservation:
                mIndexLastFragment = INDEX_BOOKING_FRAGMENT;

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_booking_list_frag), (long) index);
                break;

            case R.drawable.selector_drawermenu_setting:
                mIndexLastFragment = INDEX_INFORMATION_FRAGMENT;

                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_setting_frag), (long) index);
                break;
        }

        replaceFragment(getFragment(mIndexLastFragment), String.valueOf(mIndexLastFragment));
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
        if (mIndexLastFragment == INDEX_HOTEL_FRAGMENT || mIndexLastFragment == INDEX_GOURMET_FRAGMENT)
        {
            if (mBackButtonHandler.onBackPressed())
            {
                ExitActivity.exitApplication(this);

                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        VolleyHttpClient.destroyCookie();

        super.onDestroy();
    }

    @Override
    public void onError()
    {
        super.onError();

        // Error Fragment를 표시한다. -> stackoverflow가 발생하는 경우가 있음. 에러 원인 파악해야 함.
        replaceFragment(new ErrorFragment(), String.valueOf(ERROR_FRAGMENT));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        com.google.android.gms.appindexing.Action viewAction = com.google.android.gms.appindexing.Action.newAction(com.google.android.gms.appindexing.Action.TYPE_VIEW, // TODO: choose an action type.
            "데일리호텔", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://dailyhotel.co.kr"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.twoheart.dailyhotel/dailyhotel/dailyhotel.co.kr?view=hotel"));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop()
    {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        com.google.android.gms.appindexing.Action viewAction = com.google.android.gms.appindexing.Action.newAction(com.google.android.gms.appindexing.Action.TYPE_VIEW, // TODO: choose an action type.
            "데일리호텔", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://dailyhotel.co.kr"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://com.twoheart.dailyhotel/dailyhotel/dailyhotel.co.kr?view=hotel"));
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public int getIndexLastFragment()
    {
        return mIndexLastFragment;
    }
}
