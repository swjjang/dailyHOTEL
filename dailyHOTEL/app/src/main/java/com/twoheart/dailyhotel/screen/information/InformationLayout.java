package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

/**
 * Created by sam on 2016. 5. 18..
 */
public class InformationLayout extends BaseLayout implements View.OnClickListener
{
    private View mNewEventIconView, mNewNoticeIconView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void startAbout();

        void startEvent();

        void startNotice();

        void startFAQ();

        void startContactUs();

        void startTermsNPolicy();

        void startFacebook();

        void startInstagram();

        void startNaverBlog();

        void startYouTube();
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

        initSnsLayout(view);

        TextView versionTextView = (TextView) view.findViewById(R.id.versionTextView);

        String dotVersion = String.format("%s.%d.%d.%s", DailyHotel.VERSION_CODE.substring(0, 1) //
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(1, 3))//
            , Integer.parseInt(DailyHotel.VERSION_CODE.substring(3, 5)) //
            , DailyHotel.VERSION_CODE.substring(5, 7));

        versionTextView.setText(dotVersion);

        if (Constants.DEBUG == true)
        {
            TextView debugTextView = (TextView) view.findViewById(R.id.debugTextView);
            debugTextView.setVisibility(View.VISIBLE);

            WebView webView = new WebView(mContext);
            String webViewVersion = webView.getSettings().getUserAgentString();

            debugTextView.setText(DailyPreference.getInstance(mContext).getBaseUrl() + "\n" + webViewVersion);
        }
    }

    private void initToolbar(Context context, View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(context, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getResources().getString(R.string.actionbar_title_setting_frag), null, false);
    }

    private void initSnsLayout(View view)
    {
        View facebookView = view.findViewById(R.id.facebookLinkView);
        View instagramView = view.findViewById(R.id.instagramLinkView);
        View naverView = view.findViewById(R.id.naverLinkView);
        View youtubeLinkView = view.findViewById(R.id.youtubeLinkView);

        facebookView.setOnClickListener(this);
        instagramView.setOnClickListener(this);
        naverView.setOnClickListener(this);
        youtubeLinkView.setOnClickListener(this);
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
                ((OnEventListener) mOnEventListener).startAbout();
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

            case R.id.facebookLinkView:
                ((OnEventListener) mOnEventListener).startFacebook();
                break;

            case R.id.instagramLinkView:
                ((OnEventListener) mOnEventListener).startInstagram();
                break;

            case R.id.naverLinkView:
                ((OnEventListener) mOnEventListener).startNaverBlog();
                break;

            case R.id.youtubeLinkView:
                ((OnEventListener) mOnEventListener).startYouTube();
                break;

            default:
                break;
        }
    }
}
