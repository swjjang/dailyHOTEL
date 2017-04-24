package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.deprecated.DeviceResolutionUtil;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyHomeScrollView;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseBlurLayout
{
    private static final int EVENT_VIEWPAGER_ANIMATION_DURATION = 5000;
    private static final int MESSAGE_ANIMATION_DURATION = 200;
    private static final int ERROR_ANIMATION_DURATION = 200;

    private float mErrorLayoutMinTranslationY;
    private float mErrorLayoutMaxTranslationY;

    int mEventImageHeight;

    Handler mEventHandler;

    DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    HomeEventImageViewPagerAdapter mEventViewPagerAdapter;

    private View mActionButtonLayout;
    SwipeRefreshLayout mSwipeRefreshLayout;
    DailyHomeScrollView mDailyHomeScrollView;
    View mErrorPopupLayout;
    private LinearLayout mHomeContentLayout;
    private View mEventAreaLayout;
    View mScrollButtonLayout;
    private HomeCategoryLayout mCategoryLayout;
    View mTextMessageLayout;
    View mTopButtonLayout;
    HomeCarouselLayout mRecentListLayout;
    HomeCarouselLayout mWishListLayout;
    HomeRecommendationLayout mHomeRecommendationLayout;

    ObjectAnimator mErrorPopupAnimator;

    LinearLayout mProviderInfoView;

    private BaseMenuNavigationFragment.OnScreenScrollChangeListener mOnScreenScrollChangeListener;


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

        void onWishListItemClick(View view, int position);

        void onWishListItemLongClick(View view, int position);

        void onRecentListItemClick(View view, int position);

        void onRecentListItemLongClick(View view, int position);

        void onTermsClick();

        void onPrivacyTermsClick();

        void onLocationTermsClick();

        void onProtectedYouthClick();

        void onCategoryItemClick(DailyCategoryType categoryType);
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

        initToolbarLayout(view);
        initSwipeRefreshLayout(view);
        initNestedScrollLayout(view);
        initActionButtonLayout(view);
        initErrorPopupLayout(view);
        initHomeContentLayout(view);
    }

    private void initSwipeRefreshLayout(View view)
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(() -> forceRefreshing(true));
    }

    private void initToolbarLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        View searchView = view.findViewById(R.id.searchImageView);
        searchView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onSearchImageClick());
    }

    // 홈의 상단 고정 버튼 레이아웃
    private void initActionButtonLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        mActionButtonLayout = view.findViewById(R.id.actionButtonLayout);
        mActionButtonLayout.setVisibility(View.GONE);

        View stayButton = mActionButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mActionButtonLayout.findViewById(R.id.gourmetButtonLayout);

        if (ScreenUtils.getScreenWidth(mContext) < 720)
        {
            TextView stayDoTextView = (TextView) mScrollButtonLayout.findViewById(R.id.stayDoTextView);
            TextView gourmetDoTextView = (TextView) mScrollButtonLayout.findViewById(R.id.gourmetDoTextView);

            stayDoTextView.setText(R.string.label_home_stay_menu_description_low_resolution);
            gourmetDoTextView.setText(R.string.label_home_gourmet_menu_description_low_resolution);
        }

        stayButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onStayButtonClick());

        gourmetButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onGourmetButtonClick());
    }

    private void initErrorPopupLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        mErrorPopupLayout = view.findViewById(R.id.errorView);
        mErrorPopupLayout.setVisibility(View.GONE);
        DailyTextView errorTextView1 = (DailyTextView) mErrorPopupLayout.findViewById(R.id.errorTextView1);
        DailyTextView errorTextView2 = (DailyTextView) mErrorPopupLayout.findViewById(R.id.errorTextView2);
        View retryButtonView = mErrorPopupLayout.findViewById(R.id.retryTextView);
        View closeButtonView = mErrorPopupLayout.findViewById(R.id.closeImageView);

        retryButtonView.setOnClickListener(v -> forceRefreshing(false));

        closeButtonView.setOnClickListener(v -> setErrorPopupLayout(false));

        String errorText = mContext.getResources().getString(R.string.label_home_server_error_text);

        int index;

        if (ScreenUtils.getScreenWidth(mContext) < 720)
        {
            String findText = mContext.getResources().getString(R.string.label_home_server_error_low_display_split_text);
            index = errorText.lastIndexOf(findText) + findText.length();
        } else
        {
            index = errorText.lastIndexOf(". ") + 1;
        }

        errorTextView1.setText(errorText.substring(0, index).trim());
        errorTextView2.setText(errorText.substring(index + 1).trim());


        mErrorLayoutMinTranslationY = 0;
        mErrorLayoutMaxTranslationY = ScreenUtils.dpToPx(mContext, 93d) + 1;
        mErrorPopupLayout.setTranslationY(mErrorLayoutMaxTranslationY);
    }

    private void initHomeContentLayout(View view)
    {
        mHomeContentLayout = (LinearLayout) view.findViewById(R.id.homeContentLayout);

        initEventLayout(mHomeContentLayout);
        initScrollButtonLayout(mHomeContentLayout);
        initCategoryLayout(mHomeContentLayout);
        initTextMessageLayout(mHomeContentLayout);
        initRecentListLayout(mHomeContentLayout);
        initWishListLayout(mHomeContentLayout);
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
        //        mEventViewPager.setSlideTime(4);

        mEventHandler = new EventHandler(mEventAreaLayout);
    }

    private void initScrollButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mScrollButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_product_layout, null);

        layout.addView(mScrollButtonLayout);

        View stayButton = mScrollButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mScrollButtonLayout.findViewById(R.id.gourmetButtonLayout);

        stayButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onStayButtonClick());

        gourmetButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onGourmetButtonClick());

        if (ScreenUtils.getScreenWidth(mContext) < 720)
        {
            TextView stayDoTextView = (TextView) mScrollButtonLayout.findViewById(R.id.stayDoTextView);
            TextView gourmetDoTextView = (TextView) mScrollButtonLayout.findViewById(R.id.gourmetDoTextView);

            stayDoTextView.setText(R.string.label_home_stay_menu_description_low_resolution);
            gourmetDoTextView.setText(R.string.label_home_gourmet_menu_description_low_resolution);
        }

        View scrollButtonBottomLineView = mScrollButtonLayout.findViewById(R.id.bottomLine);
        scrollButtonBottomLineView.setVisibility(View.VISIBLE);
    }

    private void initCategoryLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mCategoryLayout = new HomeCategoryLayout(mContext);
        layout.addView(mCategoryLayout);

        mCategoryLayout.setOnItemClickListener(dailyCategoryType -> ((OnEventListener) mOnEventListener).onCategoryItemClick(dailyCategoryType));
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

        View homeMessageLayout = mTextMessageLayout.findViewById(R.id.homeMessageLayout);
        View closeView = mTextMessageLayout.findViewById(R.id.closeImageView);

        homeMessageLayout.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onMessageTextAreaClick());

        closeView.setOnClickListener(v ->
        {
            startTextLayoutCloseAnimation();

            ((OnEventListener) mOnEventListener).onMessageTextAreaCloseClick();
        });

        hideMessageLayout();
    }

    private void initWishListLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mWishListLayout = new HomeCarouselLayout(mContext);
        layout.addView(mWishListLayout);

        mWishListLayout.setTitleText(R.string.label_wishlist);

        mWishListLayout.setCarouselListener(new HomeCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListViewAllClick();
            }

            @Override
            public void onItemClick(View view, int position)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListItemClick(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListItemLongClick(view, position);
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

            @Override
            public void onItemClick(View view, int position)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListItemClick(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListItemLongClick(view, position);
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
        mHomeRecommendationLayout.setListener((view, recommendation, position) -> ((OnEventListener) mOnEventListener).onRecommendationClick(view, recommendation));

        layout.addView(mHomeRecommendationLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        providerButtonView.setOnClickListener(v ->
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
                providerButtonView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.navibar_m_ic_v_gray, 0);
            }
        });

        View termsView = providerLayout.findViewById(R.id.termsTextView);
        View privacyView = providerLayout.findViewById(R.id.privacyTextView);
        View locationView = providerLayout.findViewById(R.id.locationTermsTextView);
        View protectYouthView = providerLayout.findViewById(R.id.protectYouthTermsTextView);

        termsView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onTermsClick());

        privacyView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onPrivacyTermsClick());

        locationView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onLocationTermsClick());

        protectYouthView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onProtectedYouthClick());
    }

    private void initTopButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mTopButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_top_button_layout, null);
        layout.addView(mTopButtonLayout);
        mTopButtonLayout.setVisibility(View.GONE);

        View topButton = mTopButtonLayout.findViewById(R.id.topButtonView);
        topButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onTopButtonClick());
    }

    private void initNestedScrollLayout(View view)
    {
        mDailyHomeScrollView = (DailyHomeScrollView) view.findViewById(R.id.nestedScrollView);
        mDailyHomeScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mDailyHomeScrollView.setOnScrollChangedListener(mScrollChangedListener);
        mDailyHomeScrollView.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    // Event area
    private int getEventImageHeight(Context context)
    {
        if (mEventImageHeight == 0)
        {
            mEventImageHeight = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext));
        }

        return mEventImageHeight;
    }

    private Event getDefaultEvent()
    {
        String homeEventCurrentVersion = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventCurrentVersion();
        String homeEventUrl = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventUrl();
        String homeEventTitle = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventTitle();
        int homeEventIndex = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventIndex();

        if (DailyTextUtils.isTextEmpty(homeEventCurrentVersion) == true)
        {
            return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null, -1);
        } else
        {
            String fileName = Util.makeImageFileName(homeEventCurrentVersion);
            File file = new File(mContext.getCacheDir(), fileName);

            if (file.exists() == false)
            {
                return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                    HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null, -1);
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

                if (DailyTextUtils.isTextEmpty(urlString) == true)
                {
                    return new Event(HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL,//
                        HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL, null, null, -1);
                } else
                {
                    return new Event(urlString, urlString, homeEventTitle, homeEventUrl, homeEventIndex);
                }
            }
        }
    }

    public void setEventList(ArrayList<Event> list)
    {
        Event defaultEvent = null;

        if (list == null || list.size() == 0)
        {
            defaultEvent = getDefaultEvent();

            list = new ArrayList<>();
            list.add(defaultEvent);
        }

        if (mEventViewPagerAdapter == null)
        {
            mEventViewPagerAdapter = new HomeEventImageViewPagerAdapter(mContext, v ->
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
                        ((OnEventListener) mOnEventListener).onEventItemClick(homeEvent);
                    }
                }
            });
        }

        mEventViewPagerAdapter.setData(list);

        int defaultIndex = 0;

        if (defaultEvent == null //
            || HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL.equalsIgnoreCase(defaultEvent.defaultImageUrl) == false)
        {
            setEventCountView(defaultIndex, mEventViewPagerAdapter.getCount());
        }

        mEventViewPager.setOnPageChangeListener(null);
        mEventViewPager.setAdapter(mEventViewPagerAdapter);
        mEventViewPager.setCurrentItem(defaultIndex);
        mEventViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                int totalCount = mEventViewPagerAdapter.getCount();
                setEventCountView(position, totalCount);

                moveNextEventPosition(mEventViewPager, position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == DailyLoopViewPager.SCROLL_STATE_DRAGGING)
                {
                    if (mEventHandler != null)
                    {
                        mEventHandler.removeMessages(0);
                    }
                }
            }
        });

        moveNextEventPosition(mEventViewPager, defaultIndex);
    }

    void setEventCountView(int pageIndex, int totalCount)
    {
        if (mEventCountTextView == null)
        {
            return;
        }

        pageIndex++;

        if (pageIndex < 1)
        {
            pageIndex = 1;
        } else if (pageIndex > totalCount)
        {
            pageIndex = totalCount;
        }

        if (totalCount == 0)
        {
            mEventCountTextView.setVisibility(View.GONE);
        } else
        {
            mEventCountTextView.setVisibility(View.VISIBLE);

            String countString = mContext.getResources().getString(R.string.format_home_event_count, pageIndex, totalCount);
            int slashIndex = countString.indexOf("/");

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
        setMessageLayoutVisibility(View.GONE);
    }

    public void setMessageLayoutVisibility(int visibility)
    {
        mTextMessageLayout.setVisibility(visibility);
    }

    public void setTextMessageData(String title, String description)
    {
        if (mTextMessageLayout == null)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(title) == true && DailyTextUtils.isTextEmpty(description) == true)
        {
            if (mTextMessageLayout.getVisibility() != View.GONE)
            {
                startTextLayoutCloseAnimation();
            }
            return;
        }

        DailyTextView titleView = (DailyTextView) mTextMessageLayout.findViewById(R.id.titleTextView);
        DailyTextView descriptionView = (DailyTextView) mTextMessageLayout.findViewById(R.id.descriptionTextView);

        titleView.setText(title);

        SpannableString spannableString = new SpannableString(description);
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionView.setText(spannableString);

        if (mTextMessageLayout.getVisibility() == View.VISIBLE)
        {
            return;
        }

        setMessageLayoutVisibility(View.INVISIBLE);

        mTextMessageLayout.post(() -> startTextLayoutShowAnimation());
    }

    public void setWishListData(ArrayList<HomePlace> list, boolean isError)
    {
        mWishListLayout.setData(list);

        if (isError == true)
        {
            setErrorPopupLayout(true);
        }
    }

    public void setRecentListData(ArrayList<HomePlace> list, boolean isError)
    {
        mRecentListLayout.setData(list);

        if (isError == true)
        {
            setErrorPopupLayout(true);
        }
    }

    public void setRecommendationData(ArrayList<Recommendation> list, boolean isError)
    {
        mHomeRecommendationLayout.setData(list);

        if (isError == true)
        {
            setErrorPopupLayout(true);
        }
    }

    public void setCategoryEnabled(boolean isEnabled)
    {
        mCategoryLayout.setCategoryEnabled(isEnabled);

        View scrollButtonBottomLineView = mScrollButtonLayout.findViewById(R.id.bottomLine);
        scrollButtonBottomLineView.setVisibility(isEnabled == true ? View.GONE : View.VISIBLE);
    }

    public boolean hasWishListData()
    {
        return mWishListLayout.hasData();
    }

    public boolean hasRecentListData()
    {
        return mRecentListLayout.hasData();
    }

    public HomePlace getWishItem(int position)
    {
        if (mWishListLayout == null)
        {
            return null;
        }

        return mWishListLayout.getItem(position);
    }

    public HomePlace getRecentItem(int position)
    {
        if (mRecentListLayout == null)
        {
            return null;
        }

        return mRecentListLayout.getItem(position);
    }

    void setErrorPopupLayout(final boolean isShow)
    {
        if (mErrorPopupLayout == null || mContext == null)
        {
            return;
        }

        if (mScrollChangedListener != null)
        {
            mOnScreenScrollChangeListener.onScrollState(isShow);
        }

        if (mErrorPopupAnimator != null && mErrorPopupAnimator.isRunning() == true)
        {
            mErrorPopupAnimator.cancel();
        }

        float oldTranslationY = mErrorPopupLayout.getTranslationY();

        if (isShow == true && oldTranslationY <= mErrorLayoutMinTranslationY)
        {
            return;
        } else if (isShow == false && oldTranslationY >= mErrorLayoutMaxTranslationY)
        {
            return;
        }

        final float start = isShow == true ? mErrorLayoutMaxTranslationY : mErrorLayoutMinTranslationY;
        final float end = isShow == true ? mErrorLayoutMinTranslationY : mErrorLayoutMaxTranslationY;

        mErrorPopupAnimator = ObjectAnimator.ofFloat(mErrorPopupLayout, "translationY", start, end);
        mErrorPopupAnimator.setDuration(ERROR_ANIMATION_DURATION);
        mErrorPopupAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                if (isShow == true)
                {
                    mErrorPopupLayout.setVisibility(View.VISIBLE);
                }

                mErrorPopupLayout.setTranslationY(start);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (isShow == false)
                {
                    mErrorPopupLayout.setVisibility(View.GONE);
                }

                mErrorPopupAnimator.removeAllListeners();
                mErrorPopupAnimator.removeAllUpdateListeners();
                mErrorPopupAnimator = null;

                mErrorPopupLayout.setTranslationY(end);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mErrorPopupAnimator.start();
    }

    void startTextLayoutShowAnimation()
    {
        if (mTextMessageLayout == null)
        {
            return;
        }

        final View view = mTextMessageLayout;

        if (view.getVisibility() == View.VISIBLE)
        {
            return;
        }

        final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, view.getHeight());
        valueAnimator.setDuration(MESSAGE_ANIMATION_DURATION);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = value;
            view.setLayoutParams(params);
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
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.removeAllListeners();

                view.setVisibility(View.VISIBLE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        valueAnimator.start();
    }

    void startTextLayoutCloseAnimation()
    {
        if (mTextMessageLayout == null)
        {
            return;
        }

        final View view = mTextMessageLayout;

        if (view.getVisibility() == View.GONE)
        {
            return;
        }

        final ValueAnimator closeValueAnimator = ValueAnimator.ofInt(view.getHeight(), 0);
        closeValueAnimator.setDuration(MESSAGE_ANIMATION_DURATION);
        closeValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        closeValueAnimator.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = value;
            view.setLayoutParams(params);
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
                closeValueAnimator.removeAllUpdateListeners();
                closeValueAnimator.removeAllListeners();

                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
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

    public void forceRefreshing(boolean isSwipeRefresh)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        if (isSwipeRefresh == false)
        {
            if (isRefreshing() == true)
            {
                return;
            } else
            {
                setScrollTop();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        }

        setErrorPopupLayout(false);

        ((HomeLayout.OnEventListener) mOnEventListener).onRefreshAll(false);
    }

    public void setScrollTop()
    {
        if (mDailyHomeScrollView != null && mDailyHomeScrollView.getChildCount() != 0)
        {
            mDailyHomeScrollView.postDelayed(() -> mDailyHomeScrollView.fullScroll(View.FOCUS_UP), 50);
        }
    }

    public void setScrollBottom()
    {
        if (mDailyHomeScrollView != null && mDailyHomeScrollView.getChildCount() != 0)
        {
            mDailyHomeScrollView.postDelayed(() ->
            {
                // 간헐적으로 2번 해줘야 동작하는 경우로 인하여 2번 처리
                mDailyHomeScrollView.fullScroll(View.FOCUS_DOWN);
                mDailyHomeScrollView.scrollBy(0, 10000);
            }, 50);
        }
    }

    public void removeOnLayoutChangeListener()
    {
        if (mDailyHomeScrollView == null)
        {
            return;
        }

        mDailyHomeScrollView.removeOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    Drawable getRotateDrawable(Drawable drawable, final float degrees)
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

    void moveNextEventPosition(DailyLoopViewPager eventViewPager, int currentPosition)
    {
        if (mEventHandler == null)
        {
            return;
        }

        mEventHandler.removeMessages(0);

        Message message = new Message();
        message.what = 0;
        message.arg1 = currentPosition;
        message.obj = eventViewPager;
        mEventHandler.sendMessageDelayed(message, EVENT_VIEWPAGER_ANIMATION_DURATION);
    }

    private static class EventHandler extends Handler
    {
        private final WeakReference<View> mWeakReference;

        public EventHandler(View view)
        {
            mWeakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (mWeakReference.get() == null)
            {
                return;
            }

            DailyLoopViewPager eventViewPager = (DailyLoopViewPager) msg.obj;
            if (eventViewPager == null)
            {
                return;
            }

            eventViewPager.setCurrentItem(msg.arg1 + 1);
        }
    }

    public void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;
    }

    private DailyHomeScrollView.OnScrollChangedListener mScrollChangedListener = new DailyHomeScrollView.OnScrollChangedListener()
    {
        @Override
        public void onScrollChanged(ScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
        {
            if (mOnScreenScrollChangeListener != null)
            {
                mOnScreenScrollChangeListener.onScrollChange(scrollView, scrollX, scrollY, oldScrollX, oldScrollY);
            }

            if (mEventImageHeight == 0 || mScrollButtonLayout == null)
            {
                return;
            }

            int startScrollY = mEventImageHeight / 20 * 15;
            int endScrollY = mEventImageHeight / 20 * 19;

            int minValue = ScreenUtils.dpToPx(mContext, 5d);
            int maxValue = ScreenUtils.dpToPx(mContext, 15d);
            int buttonLayoutAlpha = 255;

            int maxLayoutTopMargin = ScreenUtils.dpToPx(mContext, 10d);
            int minLayoutTopMargin = 0;
//            int minLayoutTopMargin = ScreenUtils.dpToPx(mContext, 0d);

            View stayButtonLayout = mScrollButtonLayout.findViewById(R.id.stayButtonLayout);
            View gourmetButtonLayout = mScrollButtonLayout.findViewById(R.id.gourmetButtonLayout);
            View productButtonLayout = mScrollButtonLayout.findViewById(R.id.productButtonLayout);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) stayButtonLayout.getLayoutParams();

            ViewGroup.MarginLayoutParams productButtonLayoutParams = (ViewGroup.MarginLayoutParams) productButtonLayout.getLayoutParams();

            if (scrollY <= startScrollY)
            {
                layoutParams.leftMargin = maxValue;
                layoutParams.rightMargin = minValue;
                buttonLayoutAlpha = 255;

                productButtonLayoutParams.topMargin = maxLayoutTopMargin;
            } else if (endScrollY < scrollY)
            {
                layoutParams.leftMargin = minValue;
                layoutParams.rightMargin = maxValue;
                buttonLayoutAlpha = 0;

                productButtonLayoutParams.topMargin = minLayoutTopMargin;
            } else
            {
                double ratio = ((double) (scrollY - startScrollY) / (double) (endScrollY - startScrollY));
                int gap = (int) ((maxValue - minValue) * ratio);

                layoutParams.leftMargin = maxValue - gap;
                layoutParams.rightMargin = minValue + gap;

                buttonLayoutAlpha = 255 - (int) (255 * ratio);

                productButtonLayoutParams.topMargin = maxLayoutTopMargin - (int) ((maxLayoutTopMargin - minLayoutTopMargin) * ratio);
            }

            stayButtonLayout.setLayoutParams(layoutParams);
            stayButtonLayout.getBackground().setAlpha(buttonLayoutAlpha);
            gourmetButtonLayout.getBackground().setAlpha(buttonLayoutAlpha);

            productButtonLayout.setLayoutParams(productButtonLayoutParams);

            // globalVisibleRect 로 동작시 android os 4.X 에서 화면을 벗어날때 rect.top 이 증가하는 이슈로 상단 뷰 크기를 고정으로 알아와서 적용!
            if (scrollY > mEventImageHeight)
//            if (scrollY > mEventImageHeight + ScreenUtils.dpToPx(mContext, 9d))
            {
                // show
                setActionButtonVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setEnabled(true);
            } else
            {
                // hide
                setActionButtonVisibility(View.GONE);
                mSwipeRefreshLayout.setEnabled(false);
            }
        }
    };

    private View.OnLayoutChangeListener mOnLayoutChangeListener = new View.OnLayoutChangeListener()
    {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
        {
            if (mTopButtonLayout == null)
            {
                return;
            }

            boolean isCanScroll = false;

            View child = ((DailyHomeScrollView) v).getChildAt(0);
            if (child != null)
            {
                int childHeight = child.getHeight();
                int topButtonHeight = mTopButtonLayout.getHeight();

                isCanScroll = v.getHeight() < childHeight + v.getPaddingTop() + v.getPaddingBottom() - topButtonHeight;
            }

            if (isCanScroll == true)
            {
                mTopButtonLayout.setVisibility(View.VISIBLE);
            } else
            {
                mTopButtonLayout.setVisibility(View.GONE);
            }
        }
    };

}
