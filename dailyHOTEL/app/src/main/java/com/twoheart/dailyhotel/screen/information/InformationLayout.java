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

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.Locale;

/**
 * Created by sam on 2016. 5. 18..
 */
public class InformationLayout extends BaseLayout implements View.OnClickListener
{
    private View mNewEventIconView, mNewNoticeIconView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startGuide();

        void startEvent();

        void startNotice();

        void startFAQ();

        void startContactUs();

        void startTermsNPolicy();
    }

    public InformationLayout(Context context, OnEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(mContext, view);

        ScrollView scrollView = (ScrollView) view.findViewById(R.id.informationScrollView);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        View aboutLayout = view.findViewById(R.id.aboutLayout);
        View eventLayout = view.findViewById(R.id.eventLayout);
        View noticeLayout = view.findViewById(R.id.noticeLayout);

        View faqLayout = view.findViewById(R.id.faqLayout);
        View contactUsLayout = view.findViewById(R.id.contactUsLayout);
        View termsNpolicyLayout = view.findViewById(R.id.termsNpolicyLayout);

        aboutLayout.setOnClickListener(this);
        eventLayout.setOnClickListener(this);
        noticeLayout.setOnClickListener(this);

        faqLayout.setOnClickListener(this);
        contactUsLayout.setOnClickListener(this);
        termsNpolicyLayout.setOnClickListener(this);

        mNewEventIconView = eventLayout.findViewById(R.id.eventNewIconView);
        mNewNoticeIconView = noticeLayout.findViewById(R.id.noticeNewIconView);

        TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);

        String dotVersion = String.format(Locale.KOREA, "%s.%d.%d.%s", DailyHotel.VERSION_CODE.substring(0, 1) //
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(1, 3))//
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(3, 5)) //
            , DailyHotel.VERSION_CODE.substring(5, 7));

        versionTextView.setText(dotVersion);

        View debugLayout = view.findViewById(R.id.debugLayout);

        if (Constants.DEBUG == true)
        {
            debugLayout.setVisibility(View.VISIBLE);

            TextView productServerTextView = (TextView) debugLayout.findViewById(R.id.productServerTextView);
            TextView devServerTextView = (TextView) debugLayout.findViewById(R.id.devServerTextView);

            productServerTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=https%3A%2F%2Fprod-mobileapi.dailyhotel.kr%2Fgoodnight%2F"));
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
                    intent.setData(Uri.parse("dailyhotel://dailyhotel.co.kr?vc=11&baseUrl=http%3A%2F%2Fdev-mobileapi.dailyhotel.me%2Fgoodnight%2F"));
                    mContext.startActivity(intent);
                }
            });

            TextView debugTextView = (TextView) debugLayout.findViewById(R.id.debugTextView);

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

            debugTextView.setText(DailyPreference.getInstance(mContext).getBaseUrl() + "\n" + webViewVersion + "\n\nDPI : " + displayMetrics.densityDpi + dpiText);
        } else
        {
            debugLayout.setVisibility(View.GONE);
        }
    }

    private void initToolbar(Context context, View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(context, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getResources().getString(R.string.actionbar_title_setting_frag), null, false);
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.aboutLayout:
                ((OnEventListener) mOnEventListener).startGuide();
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
