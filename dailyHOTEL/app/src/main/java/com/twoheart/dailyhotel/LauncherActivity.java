package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.screen.main.MainActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.Arrays;

public class LauncherActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (Util.isOverAPI21() == true && Util.isOverAPI23() == false)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.statusbar_background));
        }

        DailyPreference.getInstance(this).setBackgroundAppTime(0);

        Intent intent = getIntent();

        if (intent == null)
        {
            return;
        }

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        ShortcutInfo shortcut1 = new ShortcutInfo.Builder(this, getResources().getString(R.string.label_shortcut_search_stay))
            .setShortLabel(getResources().getString(R.string.label_shortcut_search_stay))
            .setLongLabel(getResources().getString(R.string.label_shortcut_search_stay))
            .setIcon(Icon.createWithResource(this, R.drawable.tapbar_ic_01_hotel))
            .setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("dailyhotel://dailyhotel.co.kr?vc=8&v=hs")))
            .build();

        ShortcutInfo shortcut2 = new ShortcutInfo.Builder(this, getResources().getString(R.string.label_shortcut_search_gourmet))
            .setShortLabel(getResources().getString(R.string.label_shortcut_search_gourmet))
            .setLongLabel(getResources().getString(R.string.label_shortcut_search_gourmet))
            .setIcon(Icon.createWithResource(this, R.drawable.tapbar_ic_02_gourmet))
            .setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("dailyhotel://dailyhotel.co.kr?vc=8&v=gs")))
            .build();

        ShortcutInfo shortcut3 = new ShortcutInfo.Builder(this, getResources().getString(R.string.label_shortcut_event))
            .setShortLabel(getResources().getString(R.string.label_shortcut_event))
            .setLongLabel(getResources().getString(R.string.label_shortcut_event))
            .setIcon(Icon.createWithResource(this, R.drawable.tapbar_ic_04_more))
            .setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("dailyhotel://dailyhotel.co.kr?vc=8&v=el")))
            .build();

        ShortcutInfo shortcut4 = new ShortcutInfo.Builder(this, getResources().getString(R.string.label_shortcut_booking_list))
            .setShortLabel(getResources().getString(R.string.label_shortcut_booking_list))
            .setLongLabel(getResources().getString(R.string.label_shortcut_booking_list))
            .setIcon(Icon.createWithResource(this, R.drawable.tapbar_ic_03_reservation))
            .setIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("dailyhotel://dailyhotel.co.kr?vc=8&v=bl")))
            .build();

        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut1, shortcut2, shortcut3, shortcut4));


        Util.initializeMemory();

        DailyDeepLink.getInstance().clear();

        // 리뷰 초기화
        DailyPreference.getInstance(this).setIsRequestReview(false);

        // 선택 날짜를 초기화 한다.
        DailyPreference.getInstance(this).setStayLastViewDate(null);
        DailyPreference.getInstance(this).setGourmetLastViewDate(null);

        // 스테이 카테고리 정보를 초기화한다.
        DailyPreference.getInstance(this).setStayCategory(null, null);

        Intent newIntent = new Intent(getApplicationContext(), MainActivity.class);

        Uri uri = getIntent().getData();

        if (uri != null)
        {
            AnalyticsManager.getInstance(this).startDeepLink(uri);

            DailyDeepLink.getInstance().setDeepLink(uri);

            if (DailyDeepLink.getInstance().isValidateLink() == true)
            {
                if (Constants.DEBUG == true)
                {
                    String baseURL = DailyDeepLink.getInstance().getBaseUrl();

                    if (Util.isTextEmpty(baseURL) == false)
                    {
                        logOut();
                        DailyPreference.getInstance(this).setBaseUrl(baseURL);
                        DailyDeepLink.getInstance().clear();
                        Util.restartExitApp(this);
                        return;
                    }
                }

                AnalyticsManager.getInstance(this).recordDeepLink(DailyDeepLink.getInstance());

                newIntent.setData(uri);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }

        startActivity(newIntent);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.hold);
        finish();
    }

    private void logOut()
    {
        DailyPreference.getInstance(this).clear();

        try
        {
            LoginManager.getInstance().logOut();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        try
        {
            UserManagement.requestLogout(null);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        DailyToast.showToast(this, R.string.toast_msg_logouted, Toast.LENGTH_SHORT);

        // Adjust에서 로그아웃시 기존 정보를 보냄으로 이벤트 발생후 삭제 필요.
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE);
        AnalyticsManager.getInstance(this).setUserInformation(AnalyticsManager.ValueType.EMPTY, AnalyticsManager.ValueType.EMPTY);
    }
}
