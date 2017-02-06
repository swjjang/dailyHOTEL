package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.deprecated.DeviceResolutionUtil;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseLayout
{
    private static final int MESSAGE_ANIMATION_DURATION = 200;
    private static final double BUTTON_LAYOUT_MIN_HEIGHT = 76d;
    private static final double BUTTON_LAYOUT_MAX_HEIGHT = 82d;

    private int mEventImageHeight;
    private int mButtonGapHeight;
    private int mScrollButtonMaxHeight;
    private int mScrollButtonMinHeight;

    private DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    private HomeEventImageViewPagerAdapter mEventViewPagerAdapter;

    private View mActionButtonLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mHomeContentLayout;
    private View mEventAreaLayout;
    private View mScrollButtonLayout;
    private View mTextMessageLayout;
    private HomeCarouselLayout mRecentListLayout;
    private HomeCarouselLayout mWishListLayout;
    private HomeRecommendationLayout mHomeRecommendationLayout;

    private LinearLayout mProviderInfoView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onMessageTextAreaClick();

        void onMessageTextAreaCloseClick();

        void onSearchImageClick();

        void onStayButtonClick();

        void onGourmetButtonClick();

        void onRefreshAll(boolean isShowProgress);

        void onTopButtonClick();

        void onEventItemClick(Event event);

        void onRecommendationClick(View view, Recommendation recommendation);

        void onWishListViewAllClick();

        void onRecentListViewAllClick();

        void onTermsClick();

        void onPrivacyTermsClick();

        void onLocationTermsClick();

        void onProtectedYouthClick();
    }

    public enum MessageType
    {
        NONE,
        TEXT
    }

    public HomeLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        mButtonGapHeight = Util.dpToPx(mContext, BUTTON_LAYOUT_MAX_HEIGHT - BUTTON_LAYOUT_MIN_HEIGHT);
        mScrollButtonMinHeight = Util.dpToPx(mContext, BUTTON_LAYOUT_MIN_HEIGHT);
        mScrollButtonMaxHeight = Util.dpToPx(mContext, BUTTON_LAYOUT_MAX_HEIGHT);

        initToolbarLayout(view);
        initSwipeRefreshLayout(view);
        initNestedScrollLayout(view);
        initActionButtonLayout(view);
        initHomeContentLayout(view);
    }

    private void initSwipeRefreshLayout(View view)
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                mWishListLayout.clearAll();
                mRecentListLayout.clearAll();
                mHomeRecommendationLayout.clearAll();

                ((HomeLayout.OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });
    }

    private void initToolbarLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        View searchView = view.findViewById(R.id.searchImageView);
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onSearchImageClick();
            }
        });
    }

    // 홈의 상단 고정 버튼 레이아웃
    private void initActionButtonLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        mActionButtonLayout = view.findViewById(R.id.productLayout);
        mActionButtonLayout.setVisibility(View.GONE);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mActionButtonLayout.getLayoutParams();
        params.height = Util.dpToPx(mContext, BUTTON_LAYOUT_MIN_HEIGHT);

        View stayButton = mActionButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mActionButtonLayout.findViewById(R.id.gourmetButtonLayout);

        stayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onStayButtonClick();
            }
        });

        gourmetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onGourmetButtonClick();
            }
        });
    }

    private void initHomeContentLayout(View view)
    {
        mHomeContentLayout = (LinearLayout) view.findViewById(R.id.homeContentLayout);

        initEventLayout(mHomeContentLayout);
        initScrollButtonLayout(mHomeContentLayout);
        initTextMessageLayout(mHomeContentLayout);
        initWishListLayout(mHomeContentLayout);
        initRecentListLayout(mHomeContentLayout);
        initRecommendationLayout(mHomeContentLayout);
        initProviderInfoLayout(mHomeContentLayout);
        initTopButtonLayout(mHomeContentLayout);
    }

    private void initEventLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mEventAreaLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_event_layout, null);
        layout.addView(mEventAreaLayout);

        mEventViewPager = (DailyLoopViewPager) mEventAreaLayout.findViewById(R.id.loopViewPager);
        mEventCountTextView = (DailyTextView) mEventAreaLayout.findViewById(R.id.pagerCountTextView);

        ViewGroup.LayoutParams params = mEventViewPager.getLayoutParams();
        params.height = getEventImageHeight(mContext);
        mEventViewPager.setLayoutParams(params);
    }

    private void initScrollButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mScrollButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_product_layout, null);
        layout.addView(mScrollButtonLayout);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(//
            ViewGroup.LayoutParams.MATCH_PARENT, Util.dpToPx(mContext, BUTTON_LAYOUT_MAX_HEIGHT));
        mScrollButtonLayout.setLayoutParams(params);

        View stayButton = mScrollButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mScrollButtonLayout.findViewById(R.id.gourmetButtonLayout);

        stayButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onStayButtonClick();
            }
        });

        gourmetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onGourmetButtonClick();
            }
        });
    }

    private void initTextMessageLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        View messageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_text_layout, null);
        layout.addView(messageLayout);

        mTextMessageLayout = messageLayout.findViewById(R.id.homeMessageLayout);
        mTextMessageLayout.setVisibility(View.GONE);
    }

    private void initWishListLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mWishListLayout = new HomeCarouselLayout(mContext);
        //        mWishListLayout.setVisibility(View.GONE);
        layout.addView(mWishListLayout);

        mWishListLayout.setTitleText(R.string.label_wishlist);

        mWishListLayout.setCarouselListener(new HomeCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListViewAllClick();
            }
        });
    }

    private void initRecentListLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mRecentListLayout = new HomeCarouselLayout(mContext);
        //        mRecentListLayout.setVisibility(View.GONE);
        layout.addView(mRecentListLayout);

        mRecentListLayout.setTitleText(R.string.frag_recent_places);

        mRecentListLayout.setCarouselListener(new HomeCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListViewAllClick();
            }
        });
    }

    private void initRecommendationLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mHomeRecommendationLayout = new HomeRecommendationLayout(mContext);
        mHomeRecommendationLayout.setListener(new HomeRecommendationLayout.HomeRecommendationListener()
        {
            @Override
            public void onRecommendationClick(View view, Recommendation recommendation, int position)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecommendationClick(view, recommendation);
            }
        });

        layout.addView(mHomeRecommendationLayout);

        setRecommendationData(null);
    }

    private void initProviderInfoLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        View providerLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_provider_information_layout, null);
        layout.addView(providerLayout);

        mProviderInfoView = (LinearLayout) providerLayout.findViewById(R.id.providerInfoLayout);

        LinearLayout policyLayout = (LinearLayout) providerLayout.findViewById(R.id.policyLayout);
        View verticalLine1 = providerLayout.findViewById(R.id.vericalLineTextView1);
        View verticalLine2 = providerLayout.findViewById(R.id.vericalLineTextView2);
        View verticalLine3 = providerLayout.findViewById(R.id.vericalLineTextView3);

        mProviderInfoView.setVisibility(View.GONE);

        if (DeviceResolutionUtil.RESOLUTION_XHDPI > DeviceResolutionUtil.getResolutionType((Activity) mContext))
        {
            policyLayout.setOrientation(LinearLayout.VERTICAL);
            verticalLine1.setVisibility(View.GONE);
            verticalLine2.setVisibility(View.GONE);
            verticalLine3.setVisibility(View.GONE);
        } else
        {
            policyLayout.setOrientation(LinearLayout.HORIZONTAL);
            verticalLine1.setVisibility(View.VISIBLE);
            verticalLine2.setVisibility(View.VISIBLE);
            verticalLine3.setVisibility(View.VISIBLE);
        }

        String phone = DailyPreference.getInstance(mContext).getRemoteConfigCompanyPhoneNumber();
        String privacyEmail = DailyPreference.getInstance(mContext).getRemoteConfigCompanyPrivacyEmail();
        String address = DailyPreference.getInstance(mContext).getRemoteConfigCompanyAddress();
        String ceoName = DailyPreference.getInstance(mContext).getRemoteConfigCompanyCEO();
        String registrationNo = DailyPreference.getInstance(mContext).getRemoteConfigCompanyBizRegNumber();
        String mailSalesOrderNo = DailyPreference.getInstance(mContext).getRemoteConfigCompanyItcRegNumber();
        String companyName = DailyPreference.getInstance(mContext).getRemoteConfigCompanyName();

        DailyTextView companyInfoTextView = (DailyTextView) providerLayout.findViewById(R.id.companyInfoTextView);
        DailyTextView companyAddressTextView = (DailyTextView) providerLayout.findViewById(R.id.companyAddressTextView);
        DailyTextView registrationNoTextView = (DailyTextView) providerLayout.findViewById(R.id.registrationNoTextView);
        DailyTextView mailSalesOrderNoTextView = (DailyTextView) providerLayout.findViewById(R.id.mailSalesOrderNoTextView);
        DailyTextView privacyEmailTextView = (DailyTextView) providerLayout.findViewById(R.id.privacyEmailTextView);

        String companyText = mContext.getString(R.string.label_home_business_license01, companyName, ceoName, phone);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(companyText);

        int start = companyText.indexOf("ㅣ");
        int length = companyText.length();

        while (start != -1 && start < length)
        {
            stringBuilder.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getDemiLightTypeface()),//
                start, start + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            start = companyText.indexOf("ㅣ", start + 1);
        }

        companyInfoTextView.setText(stringBuilder);

        companyAddressTextView.setText(address);
        registrationNoTextView.setText(mContext.getString(R.string.label_home_business_license02, registrationNo));
        mailSalesOrderNoTextView.setText(mContext.getString(R.string.label_home_business_license03, mailSalesOrderNo));
        privacyEmailTextView.setText(mContext.getString(R.string.label_home_business_license04, privacyEmail));

        final DailyTextView providerButtonView = (DailyTextView) providerLayout.findViewById(R.id.providerInfoButtonView);
        providerButtonView.setDrawableVectorTint(R.color.default_text_cababab);
        providerButtonView.setSelected(false);
        providerButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean isSelected = !providerButtonView.isSelected();
                providerButtonView.setSelected(isSelected);

                if (isSelected == true)
                {
                    mProviderInfoView.setVisibility(View.VISIBLE);
                    Drawable[] drawables = providerButtonView.getCompoundDrawables();

                    providerButtonView.setCompoundDrawablesWithIntrinsicBounds(null, null, getRotateDrawable(drawables[2], 180f), null);
                    setScrollBottom();
                } else
                {
                    mProviderInfoView.setVisibility(View.GONE);
                    providerButtonView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_ic_v, 0);
                }
            }
        });

        View termsView = providerLayout.findViewById(R.id.termsTextView);
        View privacyView = providerLayout.findViewById(R.id.privacyTextView);
        View locationView = providerLayout.findViewById(R.id.locationTermsTextView);
        View protectYouthView = providerLayout.findViewById(R.id.protectYouthTermsTextView);

        termsView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onTermsClick();
            }
        });

        privacyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onPrivacyTermsClick();
            }
        });

        locationView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onLocationTermsClick();
            }
        });

        protectYouthView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onProtectedYouthClick();
            }
        });
    }

    private void initTopButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        View topButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_top_button_layout, null);
        layout.addView(topButtonLayout);

        View topButton = topButtonLayout.findViewById(R.id.topButtonView);
        topButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onTopButtonClick();
            }
        });
    }

    private void initNestedScrollLayout(View view)
    {
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        mNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mNestedScrollView.setOnScrollChangeListener(mOnScrollChangeListener);
    }

    // Event area
    private int getEventImageHeight(Context context)
    {
        if (mEventImageHeight == 0)
        {
            mEventImageHeight = Util.getListRowHeight(context);
        }

        return mEventImageHeight;
    }

    private Event getDefaultEvent()
    {
        String homeEventCurrentVersion = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventCurrentVersion();
        String homeEventUrl = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventUrl();
        String homeEventTitle = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventTitle();

        if (Util.isTextEmpty(homeEventCurrentVersion) == true)
        {
            return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null);
        } else
        {
            String fileName = Util.makeImageFileName(homeEventCurrentVersion);
            File file = new File(mContext.getCacheDir(), fileName);

            if (file.exists() == false)
            {
                return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                    HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null);
            } else
            {
                String urlString = null;

                try
                {
                    Uri uri = Uri.fromFile(file);
                    urlString = uri.toString();
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                if (Util.isTextEmpty(urlString) == true)
                {
                    return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                        HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null);
                } else
                {
                    return new Event(urlString, urlString, homeEventTitle, homeEventUrl);
                }
            }
        }
    }

    public void setEventList(ArrayList<Event> list)
    {
        if (list == null || list.size() == 0)
        {
            list = new ArrayList<>();
            list.add(getDefaultEvent());
        }

        if (mEventViewPagerAdapter == null)
        {
            mEventViewPagerAdapter = new HomeEventImageViewPagerAdapter(mContext, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Object tag = v.getTag();
                    if (tag == null)
                    {
                        ExLog.d("Tag is null");
                        return;
                    }

                    if (tag instanceof Event)
                    {
                        Event homeEvent = (Event) tag;
                        String defaultImageUrl = homeEvent.defaultImageUrl;

                        if (HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL.equalsIgnoreCase(defaultImageUrl) == true)
                        {
                            // 기본 이미지 클릭 동작 없음
                            ExLog.d("default Event Click");
                        } else
                        {
                            ((HomeLayout.OnEventListener) mOnEventListener).onEventItemClick(homeEvent);
                        }
                    }
                }
            });
        }

        mEventViewPagerAdapter.setData(list);

        setEventCountView(1, mEventViewPagerAdapter.getCount());

        mEventViewPager.removeAllViews();
        mEventViewPager.clearOnPageChangeListeners();

        mEventViewPager.setAdapter(mEventViewPagerAdapter);
        mEventViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                int totalCount = mEventViewPagerAdapter.getCount();

                if (position < 1)
                {
                    position = totalCount;
                } else if (position > totalCount)
                {
                    position = 1;
                }

                setEventCountView(position, totalCount);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    private void setEventCountView(int pageIndex, int totalCount)
    {
        if (mEventCountTextView == null)
        {
            return;
        }

        if (totalCount == 0)
        {
            mEventCountTextView.setVisibility(View.GONE);
        } else
        {
            mEventCountTextView.setVisibility(View.VISIBLE);

            String countString = mContext.getResources().getString(R.string.format_home_event_count, pageIndex, totalCount);
            int slashIndex = countString.indexOf("/");
            int textSize = countString.length();
            if (slashIndex < textSize)
            {
                textSize++;
            }

            if (slashIndex == -1)
            {
                mEventCountTextView.setText(countString);
            } else
            {
                SpannableString spannableString = new SpannableString(countString);
                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.white)), //
                    0, slashIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                mEventCountTextView.setText(spannableString);
            }
        }
    }

    public void hideMessageLayout()
    {
        mTextMessageLayout.setVisibility(View.GONE);
    }

    public void setTextMessageData(String title, String description)
    {
        if (mTextMessageLayout == null)
        {
            return;
        }

        mTextMessageLayout.setVisibility(View.GONE);
        mTextMessageLayout.clearAnimation();

        if (Util.isTextEmpty(title) == true && Util.isTextEmpty(description) == true)
        {
            return;
        }

        View homeMessageLayout = mTextMessageLayout.findViewById(R.id.homeMessageLayout);
        View closeView = mTextMessageLayout.findViewById(R.id.closeImageView);
        DailyTextView titleView = (DailyTextView) mTextMessageLayout.findViewById(R.id.titleTextView);
        DailyTextView descriptionView = (DailyTextView) mTextMessageLayout.findViewById(R.id.descriptionTextView);

        homeMessageLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onMessageTextAreaClick();
            }
        });

        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMessageLayoutCloseAnimation(mTextMessageLayout);

                ((HomeLayout.OnEventListener) mOnEventListener).onMessageTextAreaCloseClick();
            }
        });

        titleView.setText(title);

        if (Util.isTextEmpty(description) == true)
        {
            descriptionView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else
        {
            // 메세지에 '>'가 포함 되었을 경우 제거하고 trim!
            if (description.endsWith(">") == true)
            {
                int lastIndex = description.lastIndexOf(">");
                if (lastIndex != -1)
                {
                    description = description.substring(0, lastIndex);
                    description = description.trim();
                }
            }

            descriptionView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.payment_ic_right, 0);
            descriptionView.setCompoundDrawablePadding(Util.dpToPx(mContext, 3d));
        }

        descriptionView.setText(description);

        mTextMessageLayout.setVisibility(View.INVISIBLE);

        mTextMessageLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                startMessageLayoutShowAnimation(mTextMessageLayout);
            }
        });
    }

    public void setWishListData(ArrayList<? extends Place> list)
    {
        mWishListLayout.setData(list);
    }

    public void setRecentListData(ArrayList<? extends Place> list)
    {
        mRecentListLayout.setData(list);
    }

    public void setRecommendationData(ArrayList<Recommendation> list)
    {
        mHomeRecommendationLayout.setData(list);
    }

    private void startMessageLayoutShowAnimation(final View view)
    {
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.VISIBLE)
        {
            return;
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, view.getHeight());
        valueAnimator.setDuration(MESSAGE_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = value;
                view.setLayoutParams(params);
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                view.setVisibility(View.VISIBLE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                view.setVisibility(View.VISIBLE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        valueAnimator.start();
    }

    private void startMessageLayoutCloseAnimation(final View view)
    {
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.GONE)
        {
            return;
        }

        ValueAnimator closeValueAnimator = ValueAnimator.ofInt(view.getHeight(), 0);
        closeValueAnimator.setDuration(MESSAGE_ANIMATION_DURATION);
        closeValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        closeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = value;
                view.setLayoutParams(params);
            }
        });

        closeValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                view.setVisibility(View.GONE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                view.setVisibility(View.GONE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        closeValueAnimator.start();
    }


    public void setActionButtonVisibility(int visibility)
    {
        mActionButtonLayout.setVisibility(visibility);
    }

    public void setRefreshing(boolean isRefreshing)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public boolean isRefreshing()
    {
        if (mSwipeRefreshLayout == null)
        {
            return false;
        }

        return mSwipeRefreshLayout.isRefreshing();
    }

    public void setScrollTop()
    {
        if (mNestedScrollView != null && mNestedScrollView.getChildCount() != 0)
        {
            mNestedScrollView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mNestedScrollView.fullScroll(View.FOCUS_UP);
                }
            }, 50);
        }
    }

    public void setScrollBottom()
    {
        if (mNestedScrollView != null && mNestedScrollView.getChildCount() != 0)
        {
            mNestedScrollView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    // 간헐적으로 2번 해줘야 동작하는 경우로 인하여 2번 처리
                    mNestedScrollView.fullScroll(View.FOCUS_DOWN);
                    mNestedScrollView.scrollBy(0, 10000);
                }
            }, 50);
        }
    }

    private Drawable getRotateDrawable(Drawable drawable, final float degrees)
    {
        final Drawable[] drawables = {drawable};
        return new LayerDrawable(drawables)
        {
            @Override
            public void draw(final Canvas canvas)
            {
                canvas.save();
                canvas.rotate(degrees, drawables[0].getBounds().width() / 2, drawables[0].getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    public void onResumeCarouselAnimation()
    {
        if (mWishListLayout != null)
        {
            mWishListLayout.startShimmer();
        }

        if (mRecentListLayout != null)
        {
            mRecentListLayout.startShimmer();
        }
    }

    public void onPauseCarouselAnimation()
    {
        if (mWishListLayout != null)
        {
            mWishListLayout.stopShimmer();
        }

        if (mRecentListLayout != null)
        {
            mRecentListLayout.stopShimmer();
        }
    }

    private NestedScrollView.OnScrollChangeListener mOnScrollChangeListener = new NestedScrollView.OnScrollChangeListener()
    {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
        {
            if (mEventImageHeight == 0)
            {
                return;
            }

            if (mScrollButtonLayout == null)
            {
                return;
            }

            int startScrollY = mEventImageHeight / 5;
            int endScrollY = mEventImageHeight / 5 * 4;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mScrollButtonLayout.getLayoutParams();
            if (scrollY <= startScrollY)
            {
                params.height = mScrollButtonMaxHeight;
            } else if (endScrollY < scrollY)
            {
                params.height = mScrollButtonMinHeight;
            } else
            {
                double ratio = ((double) (scrollY - startScrollY) / (double) (endScrollY - startScrollY));
                int gapHeight = (int) (mButtonGapHeight * ratio);
                int newHeight = mScrollButtonMaxHeight - gapHeight;

                params.height = newHeight;
            }

            mScrollButtonLayout.setLayoutParams(params);

            // globalVisibleRect 로 동작시 android os 4.X 에서 화면을 벗어날때 rect.top 이 증가하는 이슈로 상단 뷰 크기를 고정으로 알아와서 적용!
            if (scrollY >= mEventImageHeight)
            {
                // show
                setActionButtonVisibility(View.VISIBLE);
            } else
            {
                // hide
                setActionButtonVisibility(View.GONE);
            }

            if (scrollY < mEventImageHeight)
            {
                mSwipeRefreshLayout.setEnabled(true);
            } else
            {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    };

}
