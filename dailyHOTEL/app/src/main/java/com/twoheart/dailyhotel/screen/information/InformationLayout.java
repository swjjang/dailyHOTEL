package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyScrollView;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.Locale;

/**
 * Created by sam on 2016. 5. 18..
 */
public class InformationLayout extends BaseLayout implements View.OnClickListener
{
    private View mNewEventIconView, mNewNoticeIconView;

    private DailyScrollView mScrollView;
    BaseMenuNavigationFragment.OnScreenScrollChangeListener mOnScreenScrollChangeListener;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startGuide();

        void startLifeStyle();

        void startSNS();

        void startEvent();

        void startNotice();

        void startFAQ();

        void startContactUs();

        void startTermsNPolicy();

        void onDailyRewardClick();

        void onDailyTrueAwardsClick();

        void onDailyPriceLabClick();
    }

    public InformationLayout(Context context, OnEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(mContext, view);

        mScrollView = view.findViewById(R.id.informationScrollView);
        EdgeEffectColor.setEdgeGlowColor(mScrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mScrollView.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
            {
                if (mOnScreenScrollChangeListener != null)
                {
                    mOnScreenScrollChangeListener.onScrollChange(scrollView, l, t, oldl, oldt);
                }
            }
        });

        View aboutLayout = view.findViewById(R.id.aboutLayout);
        View lifeStyleLayout = view.findViewById(R.id.lifeStyleLayout);
        View dailyRewardLayout = view.findViewById(R.id.dailyRewardLayout);
        View dailyTrueAwardsLayout = view.findViewById(R.id.dailyTrueAwardsLayout);
        View snsLayout = view.findViewById(R.id.snsLayout);
        View eventLayout = view.findViewById(R.id.eventLayout);
        View noticeLayout = view.findViewById(R.id.noticeLayout);
        View faqLayout = view.findViewById(R.id.faqLayout);
        View contactUsLayout = view.findViewById(R.id.contactUsLayout);
        View termsNpolicyLayout = view.findViewById(R.id.termsNpolicyLayout);

        aboutLayout.setOnClickListener(this);
        lifeStyleLayout.setOnClickListener(this);
        dailyRewardLayout.setOnClickListener(this);
        dailyTrueAwardsLayout.setOnClickListener(this);
        snsLayout.setOnClickListener(this);
        eventLayout.setOnClickListener(this);
        noticeLayout.setOnClickListener(this);
        faqLayout.setOnClickListener(this);
        contactUsLayout.setOnClickListener(this);
        termsNpolicyLayout.setOnClickListener(this);

        dailyRewardLayout.setVisibility(DailyRemoteConfigPreference.getInstance(mContext).isKeyRemoteConfigRewardStickerEnabled() ? View.VISIBLE : View.GONE);

        mNewEventIconView = eventLayout.findViewById(R.id.eventNewIconView);
        mNewNoticeIconView = noticeLayout.findViewById(R.id.noticeNewIconView);

        TextView versionTextView = view.findViewById(R.id.versionTextView);

        String dotVersion = String.format(Locale.KOREA, "%s.%d.%d.%s", DailyHotel.VERSION_CODE.substring(0, 1) //
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(1, 3))//
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(3, 5)) //
            , DailyHotel.VERSION_CODE.substring(5, 7));

        versionTextView.setText(dotVersion);

        View debugLayout = view.findViewById(R.id.debugLayout);

        if (Constants.DEBUG == true)
        {
            debugLayout.setVisibility(View.VISIBLE);

            TextView productServerTextView = debugLayout.findViewById(R.id.productServerTextView);
            TextView stagingServerTextView = debugLayout.findViewById(R.id.stagingServerTextView);
            TextView devServerTextView = debugLayout.findViewById(R.id.devServerTextView);
            TextView alphaServerTextView = debugLayout.findViewById(R.id.alphaServerTextView);
            TextView canaryServerTextView = debugLayout.findViewById(R.id.canaryServerTextView);

            productServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fprod-mobileapi.dailyhotel.kr%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fprod-silo.dailyhotel.me%2F"));
                    mContext.startActivity(intent);
                }
            });

            stagingServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fstage-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fstage-silo.dailyhotel.me%2F"));
                    mContext.startActivity(intent);
                }
            });

            devServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fdev-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fdev-silo.dailyhotel.me%2F"));
                    mContext.startActivity(intent);
                }
            });

            alphaServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fdev-alpha-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fdev-silo.dailyhotel.me%2F"));
                    mContext.startActivity(intent);
                }
            });

            canaryServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fbeta-mobileapi.dailyhotel.me%2Fgoodnight%2F&baseOutBoundUrl=https%3A%2F%2Fbeta-silo.dailyhotel.me%2F"));
                    mContext.startActivity(intent);
                }
            });

            TextView debugTextView = debugLayout.findViewById(R.id.debugTextView);

            WebView webView = new WebView(mContext);
            String webViewVersion = webView.getSettings().getUserAgentString();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            String dpiText = "\nldpi 120dpi\n" +//
                "mdpi 160dpi\n" +//
                "hdpi 240dpi\n" +//
                "xhdpi 320dpi\n" +//
                "xxhdpi 480dpi\n" +//
                "xxxhdpi 640dpi";

            debugTextView.setText(DailyPreference.getInstance(mContext).getBaseUrl() + "\n" + DailyPreference.getInstance(mContext).getBaseOutBoundUrl() + "\n"//
                + webViewVersion + "\n\nDPI : " + displayMetrics.densityDpi//
                + String.format(Locale.KOREA, "(%d, %d)", ScreenUtils.getScreenWidth(mContext), ScreenUtils.getScreenHeight(mContext))//
                + dpiText);
        } else
        {
            debugLayout.setVisibility(View.GONE);
        }
    }

    private void initToolbar(Context context, View view)
    {
        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_setting_frag);
        dailyToolbarView.setBackVisible(false);
    }

    public void updateNewIconView(boolean hasNewEvent, boolean hasNewNotice)
    {
        if (mNewEventIconView == null)
        {
            return;
        }

        if (hasNewEvent == true)
        {
            mNewEventIconView.setVisibility(View.VISIBLE);
        } else
        {
            mNewEventIconView.setVisibility(View.GONE);
        }

        if (hasNewNotice == true)
        {
            mNewNoticeIconView.setVisibility(View.VISIBLE);
        } else
        {
            mNewNoticeIconView.setVisibility(View.GONE);
        }
    }

    public void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;

        if (mScrollView != null)
        {
            mScrollView.setOnScrollChangedListener(new DailyScrollView.OnScrollChangedListener()
            {
                @Override
                public void onScrollChanged(ScrollView scrollView, int l, int t, int oldl, int oldt)
                {
                    if (mOnScreenScrollChangeListener != null)
                    {
                        mOnScreenScrollChangeListener.onScrollChange(scrollView, l, t, oldl, oldt);
                    }
                }
            });
        }
    }

    public void scrollTop()
    {
        if (mScrollView != null)
        {
            mScrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.aboutLayout:
                ((OnEventListener) mOnEventListener).startGuide();
                break;

            case R.id.lifeStyleLayout:
                ((OnEventListener) mOnEventListener).startLifeStyle();
                break;

            case R.id.dailyRewardLayout:
                ((OnEventListener) mOnEventListener).onDailyRewardClick();
                break;

            case R.id.dailyTrueAwardsLayout:
                ((OnEventListener) mOnEventListener).onDailyTrueAwardsClick();
                break;

            case R.id.snsLayout:
                ((OnEventListener) mOnEventListener).startSNS();
                break;

            case R.id.dailyPriceLabLayout:
                ((OnEventListener) mOnEventListener).onDailyPriceLabClick();
                break;

            case R.id.eventLayout:
                ((OnEventListener) mOnEventListener).startEvent();
                break;

            case R.id.noticeLayout:
                ((OnEventListener) mOnEventListener).startNotice();
                break;

            case R.id.faqLayout:
                ((OnEventListener) mOnEventListener).startFAQ();
                break;

            case R.id.contactUsLayout:
                ((OnEventListener) mOnEventListener).startContactUs();
                break;

            case R.id.termsNpolicyLayout:
                ((OnEventListener) mOnEventListener).startTermsNPolicy();
                break;

            default:
                break;
        }
    }
}
