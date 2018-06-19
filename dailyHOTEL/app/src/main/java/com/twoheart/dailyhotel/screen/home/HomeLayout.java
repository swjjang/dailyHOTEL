package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
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
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.view.DailyRewardCardView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.daily.dailyhotel.view.carousel.DailyCarouselAnimationLayout;
import com.daily.dailyhotel.view.carousel.DailyCarouselLayout;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowHomeSeasonBannerDataBinding;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyHomeScrollView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseBlurLayout
{
    private static final int EVENT_VIEWPAGER_ANIMATION_DURATION = 5000;
    private static final int MESSAGE_ANIMATION_DURATION = 200;
    private static final int ERROR_ANIMATION_DURATION = 200;
    private static final int ACTION_BUTTON_DURATION = 100;

    private float mErrorLayoutMinTranslationY;
    private float mErrorLayoutMaxTranslationY;

    int mEventImageHeight;

    Handler mEventHandler;

    RecyclerView mEventRecyclerView;
    private DailyTextView mEventCountTextView;
    EventListAdapter mEventListAdapter;

    View mActionButtonLayout;
    private ValueAnimator mScrollButtonLayoutAnimator;
    private int mActionButtonLayoutVisibility = View.GONE;
    int mSkipScrollAnimationGapValue;

    SwipeRefreshLayout mSwipeRefreshLayout;
    DailyHomeScrollView mDailyHomeScrollView;
    View mErrorPopupLayout;
    private LinearLayout mHomeContentLayout;
    private View mEventAreaLayout;
    View mScrollButtonLayout;
    private HomeCategoryLayout mCategoryLayout;
    View mTextMessageLayout;
    View mTopButtonLayout, mProviderLayout;
    DailyCarouselAnimationLayout mRecentListLayout;
    DailyCarouselAnimationLayout mWishListLayout;
    HomeRecommendationLayout mHomeRecommendationLayout;
    private ListRowHomeSeasonBannerDataBinding mListRowHomeSeasonBannerDataBinding;

    private DailyRewardCardView mDailyRewardCardView;

    ObjectAnimator mErrorPopupAnimator;

    LinearLayout mProviderInfoView;

    BaseMenuNavigationFragment.OnScreenScrollChangeListener mOnScreenScrollChangeListener;


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

        void onWishListItemClick(View view);

        void onWishListItemLongClick(View view);

        void onRecentListItemClick(View view);

        void onRecentListItemLongClick(View view);

        void onTermsClick();

        void onPrivacyTermsClick();

        void onLocationTermsClick();

        void onProtectedYouthClick();

        void onCategoryItemClick(DailyCategoryType categoryType);

        void onRewardGuideClick();

        void onRewardLoginClick();

        void onRewardDetailClick();

        void onSeasonBannerClick(Event event);
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

        updateButtonDoText();
    }

    /**
     * G5, G6 등의 단말 자체에서 화면의 글자를 강제로 키웠을 경우 레이아웃이 넘치는 이슈 제거
     */
    private void updateButtonDoText()
    {
        if (mScrollButtonLayout == null)
        {
            return;
        }

        TextView stayDoTextView = mScrollButtonLayout.findViewById(R.id.stayDoTextView);
        TextView gourmetDoTextView = mScrollButtonLayout.findViewById(R.id.gourmetDoTextView);

        gourmetDoTextView.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (gourmetDoTextView.getLineCount() > 1)
                {
                    stayDoTextView.setText(R.string.label_home_stay_menu_description_low_resolution);
                    gourmetDoTextView.setText(R.string.label_home_gourmet_menu_description_low_resolution);

                    if (mActionButtonLayout == null)
                    {
                        return;
                    }

                    TextView actionStayDoTextView = mActionButtonLayout.findViewById(R.id.stayDoTextView);
                    TextView actionGourmetDoTextView = mActionButtonLayout.findViewById(R.id.gourmetDoTextView);
                    actionStayDoTextView.setText(R.string.label_home_stay_menu_description_low_resolution);
                    actionGourmetDoTextView.setText(R.string.label_home_gourmet_menu_description_low_resolution);
                }
            }
        });

    }

    private void initSwipeRefreshLayout(View view)
    {
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(() -> forceRefreshing(true));
    }

    private void initToolbarLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setBackImageResource(R.drawable.img_gnb_logo);

        dailyToolbarView.clearMenuItem();
        dailyToolbarView.addMenuItem(DailyToolbarView.MenuItem.SEARCH, null, new View.OnClickListener()
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

        mActionButtonLayout = view.findViewById(R.id.actionButtonLayout);
        mActionButtonLayout.setVisibility(View.GONE);

        View stayButton = mActionButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mActionButtonLayout.findViewById(R.id.gourmetButtonLayout);
        TextView stayDoTextView = mActionButtonLayout.findViewById(R.id.stayDoTextView);
        TextView gourmetDoTextView = mActionButtonLayout.findViewById(R.id.gourmetDoTextView);

        if (ScreenUtils.getScreenWidth(mContext) < 720)
        {
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
        mErrorPopupLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                forceRefreshing(false);
            }
        });

        DailyTextView errorTextView1 = mErrorPopupLayout.findViewById(R.id.errorTextView1);
        DailyTextView errorTextView2 = mErrorPopupLayout.findViewById(R.id.errorTextView2);
        View retryButtonView = mErrorPopupLayout.findViewById(R.id.retryTextView);
        View closeButtonView = mErrorPopupLayout.findViewById(R.id.closeImageView);

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
        mHomeContentLayout = view.findViewById(R.id.homeContentLayout);

        initEventLayout(mHomeContentLayout);
        initScrollButtonLayout(mHomeContentLayout);
        initCategoryLayout(mHomeContentLayout);
        initSeasonBannerLayout(mHomeContentLayout);
        initTextMessageLayout(mHomeContentLayout);
        initRecentListLayout(mHomeContentLayout);
        initRecommendationLayout(mHomeContentLayout);
        initWishListLayout(mHomeContentLayout);

        if (DailyRemoteConfigPreference.getInstance(mContext).isKeyRemoteConfigRewardStickerEnabled() == true)
        {
            initRewardLayout(mHomeContentLayout);
        }

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

        mEventRecyclerView = mEventAreaLayout.findViewById(R.id.loopRecyclerView);
        mEventCountTextView = mEventAreaLayout.findViewById(R.id.pagerCountTextView);

        ViewGroup.LayoutParams params = mEventRecyclerView.getLayoutParams();
        params.height = getEventImageHeight(mContext);
        mEventRecyclerView.setLayoutParams(params);

        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(mEventRecyclerView);

        EdgeEffectColor.setEdgeGlowColor(mEventRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mEventHandler = new EventHandler(mEventAreaLayout);
    }

    private void initScrollButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mScrollButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_product_layout, null);
        mScrollButtonLayout.setBackgroundResource(R.color.white);
        layout.addView(mScrollButtonLayout);

        View stayButton = mScrollButtonLayout.findViewById(R.id.stayButtonLayout);
        View gourmetButton = mScrollButtonLayout.findViewById(R.id.gourmetButtonLayout);

        stayButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onStayButtonClick());

        gourmetButton.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onGourmetButtonClick());

        TextView stayDoTextView = mScrollButtonLayout.findViewById(R.id.stayDoTextView);
        TextView gourmetDoTextView = mScrollButtonLayout.findViewById(R.id.gourmetDoTextView);

        if (ScreenUtils.getScreenWidth(mContext) < 720)
        {
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

    private void initSeasonBannerLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mListRowHomeSeasonBannerDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.list_row_home_season_banner_data, layout, true);
        mListRowHomeSeasonBannerDataBinding.getRoot().setVisibility(View.GONE);
        mListRowHomeSeasonBannerDataBinding.seasonBannerSimpleDraweeView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onSeasonBannerClick((Event) v.getTag()));
    }

    private void initTextMessageLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mTextMessageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_text_layout, null);
        layout.addView(mTextMessageLayout);

        View homeMessageLayout = mTextMessageLayout.findViewById(R.id.homeMessageLayout);
        View closeView = mTextMessageLayout.findViewById(R.id.closeImageView);

        homeMessageLayout.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onMessageTextAreaClick());

        closeView.setOnClickListener(v -> {
            startTextLayoutCloseAnimation();

            ((OnEventListener) mOnEventListener).onMessageTextAreaCloseClick();
        });

        hideMessageLayout();
    }

    private void initRewardLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mDailyRewardCardView = new DailyRewardCardView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final int DP_15 = ScreenUtils.dpToPx(mContext, 15);
        layoutParams.leftMargin = DP_15;
        layoutParams.rightMargin = DP_15;
        layoutParams.topMargin = DP_15;
        layoutParams.bottomMargin = DP_15;

        mDailyRewardCardView.setLayoutParams(layoutParams);

        layout.addView(mDailyRewardCardView);

        mDailyRewardCardView.setOnGuideClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onRewardGuideClick();
            }
        });

        setRewardCardVisible(false);
    }

    private void initWishListLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mWishListLayout = new DailyCarouselAnimationLayout(mContext);
        mWishListLayout.setUsePriceLayout(false);
        // 생성 에니매이션 제거로 인한 주석처리 V2.1.1 sunny 요청
        //        mWishListLayout.setUseAnimation(true);
        mWishListLayout.setBottomMarginView(0, Color.TRANSPARENT);
        layout.addView(mWishListLayout);

        mWishListLayout.setTitleText(R.string.label_wishlist);

        mWishListLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListViewAllClick();
            }

            @Override
            public void onItemClick(View view, android.support.v4.util.Pair[] pairs)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListItemClick(view);
            }

            @Override
            public void onItemLongClick(View view, android.support.v4.util.Pair[] pairs)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onWishListItemLongClick(view);
            }
        });
    }

    private void initRecentListLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mRecentListLayout = new DailyCarouselAnimationLayout(mContext);
        mRecentListLayout.setUsePriceLayout(false);
        // 생성 에니매이션 제거로 인한 주석처리 V2.1.1 sunny 요청
        //        mRecentListLayout.setUseAnimation(true);
        mRecentListLayout.setBottomMarginView(0, Color.TRANSPARENT);
        layout.addView(mRecentListLayout);

        mRecentListLayout.setTitleText(R.string.frag_recent_places);

        mRecentListLayout.setCarouselListener(new DailyCarouselLayout.OnCarouselListener()
        {
            @Override
            public void onViewAllClick()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListViewAllClick();
            }

            @Override
            public void onItemClick(View view, android.support.v4.util.Pair[] pairs)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListItemClick(view);
            }

            @Override
            public void onItemLongClick(View view, android.support.v4.util.Pair[] pairs)
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRecentListItemLongClick(view);
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
        // 생성 에니매이션 제거로 인한 주석처리 V2.1.1 sunny 요청
        //        mHomeRecommendationLayout.setUseAnimation(true);
        mHomeRecommendationLayout.setListener((view, recommendation, position) -> ((OnEventListener) mOnEventListener).onRecommendationClick(view, recommendation));

        layout.addView(mHomeRecommendationLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initProviderInfoLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mProviderLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_provider_information_layout, null);
        layout.addView(mProviderLayout);

        mProviderInfoView = mProviderLayout.findViewById(R.id.providerInfoLayout);

        LinearLayout policyLayout = mProviderLayout.findViewById(R.id.policyLayout);
        View verticalLine1 = mProviderLayout.findViewById(R.id.verticalLineTextView1);
        View verticalLine2 = mProviderLayout.findViewById(R.id.verticalLineTextView2);
        View verticalLine3 = mProviderLayout.findViewById(R.id.verticalLineTextView3);

        mProviderInfoView.setVisibility(View.GONE);

        if (DisplayMetrics.DENSITY_XHIGH > ScreenUtils.getResolution((Activity) mContext))
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

            TextView protectYouthTermsTextView = policyLayout.findViewById(R.id.protectYouthTermsTextView);
            protectYouthTermsTextView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (protectYouthTermsTextView.getLineCount() > 1)
                    {
                        policyLayout.setOrientation(LinearLayout.VERTICAL);
                        verticalLine1.setVisibility(View.GONE);
                        verticalLine2.setVisibility(View.GONE);
                        verticalLine3.setVisibility(View.GONE);
                    }
                }
            });
        }

        String phone = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyPhoneNumber();
        String privacyEmail = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyPrivacyEmail();
        String address = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyAddress();
        String ceoName = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyCEO();
        String registrationNo = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyBizRegNumber();
        String mailSalesOrderNo = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyItcRegNumber();
        String companyName = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigCompanyName();

        DailyTextView companyInfoTextView = mProviderLayout.findViewById(R.id.companyInfoTextView);
        DailyTextView companyAddressTextView = mProviderLayout.findViewById(R.id.companyAddressTextView);
        DailyTextView registrationNoTextView = mProviderLayout.findViewById(R.id.registrationNoTextView);
        DailyTextView mailSalesOrderNoTextView = mProviderLayout.findViewById(R.id.mailSalesOrderNoTextView);
        DailyTextView privacyEmailTextView = mProviderLayout.findViewById(R.id.privacyEmailTextView);

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

        final DailyTextView providerButtonView = mProviderLayout.findViewById(R.id.providerInfoButtonView);
        providerButtonView.setDrawableVectorTint(R.color.default_text_cababab);
        providerButtonView.setSelected(false);
        providerButtonView.setOnClickListener(v -> {
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

        View termsView = mProviderLayout.findViewById(R.id.termsTextView);
        View privacyView = mProviderLayout.findViewById(R.id.privacyTextView);
        View locationView = mProviderLayout.findViewById(R.id.locationTermsTextView);
        View protectYouthView = mProviderLayout.findViewById(R.id.protectYouthTermsTextView);

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
        mSkipScrollAnimationGapValue = ScreenUtils.dpToPx(mContext, 20);

        mDailyHomeScrollView = view.findViewById(R.id.nestedScrollView);
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
        String homeEventCurrentVersion = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigHomeEventCurrentVersion();
        String homeEventUrl = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigHomeEventUrl();
        String homeEventTitle = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigHomeEventTitle();
        int homeEventIndex = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigHomeEventIndex();

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

        if (mEventListAdapter == null)
        {
            mEventListAdapter = new EventListAdapter(mContext, new View.OnClickListener()
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
                            ((OnEventListener) mOnEventListener).onEventItemClick(homeEvent);
                        }
                    }
                }
            });
        }

        mEventListAdapter.setData(list);

        if (defaultEvent == null //
            || HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL.equalsIgnoreCase(defaultEvent.defaultImageUrl) == false)
        {
            setEventCountView(0, mEventListAdapter.getRealCount());
        }

        mEventRecyclerView.setAdapter(mEventListAdapter);

        int firstPosition = (mEventListAdapter.getItemCount() / 2);
        firstPosition = firstPosition + (mEventListAdapter.getRealCount() - (firstPosition % mEventListAdapter.getRealCount()));
        mEventRecyclerView.scrollToPosition(firstPosition);

        // PagerSnapHelper 내부에서 addOnScrollListener 를 사용하여 이벤트 처리를 함으로 SnapHelper의 ScrollListener를 수정하지 않으면
        // 스크롤 리스너가 여러번 add 될 수 있어 setOnScrollListener 사용
        mEventRecyclerView.setOnScrollListener(mOnScrollListener);

        moveNextEventPosition(mEventRecyclerView, firstPosition);
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

    public void setSeasonBannerVisible(boolean visible)
    {
        if (mListRowHomeSeasonBannerDataBinding == null)
        {
            return;
        }

        mListRowHomeSeasonBannerDataBinding.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setSeasonBanner(String imageUrl, Event event)
    {
        if (mListRowHomeSeasonBannerDataBinding == null)
        {
            return;
        }

        mListRowHomeSeasonBannerDataBinding.seasonBannerSimpleDraweeView.setTag(event);

        if (DailyTextUtils.isTextEmpty(imageUrl) == false)
        {
            mListRowHomeSeasonBannerDataBinding.seasonBannerSimpleDraweeView.setImageURI(imageUrl);
        }
    }

    public void clearNextEventPosition()
    {
        if (mEventHandler == null)
        {
            return;
        }

        mEventHandler.removeMessages(0);
        mEventHandler = null;
    }

    public void resumeNextEventPosition()
    {
        if (mEventRecyclerView == null || mEventAreaLayout == null)
        {
            clearNextEventPosition();
            return;
        }

        if (mEventHandler == null)
        {
            mEventHandler = new EventHandler(mEventAreaLayout);
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mEventRecyclerView.getLayoutManager();

        moveNextEventPosition(mEventRecyclerView, linearLayoutManager.findFirstVisibleItemPosition());
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

        DailyTextView titleView = mTextMessageLayout.findViewById(R.id.titleTextView);
        DailyTextView descriptionView = mTextMessageLayout.findViewById(R.id.descriptionTextView);

        titleView.setText(title);

        SpannableString spannableString = new SpannableString(description);
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionView.setText(spannableString);

        if (mTextMessageLayout.getVisibility() == View.VISIBLE)
        {
            return;
        }

        setMessageLayoutVisibility(View.VISIBLE);

        // 생성 에니매이션 제거로 인한 주석처리 V2.1.1 sunny 요청
        //        setMessageLayoutVisibility(View.INVISIBLE);
        //
        //        mTextMessageLayout.post(() -> startTextLayoutShowAnimation());
    }

    public void setRewardCardVisible(boolean visible)
    {
        if (mDailyRewardCardView == null)
        {
            return;
        }

        mDailyRewardCardView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setNonMemberRewardData(String titleText, String descriptionText, String optionText, int stickerCount)
    {
        if (mDailyRewardCardView == null)
        {
            return;
        }

        mDailyRewardCardView.setGuideVisible(true);
        mDailyRewardCardView.setRewardTitleText(titleText);
        mDailyRewardCardView.setDescriptionText(descriptionText);
        mDailyRewardCardView.setOptionText(optionText);
        mDailyRewardCardView.setCampaignFreeStickerCount(stickerCount);

        mDailyRewardCardView.setOnOptionClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onRewardLoginClick();
            }
        });

        mDailyRewardCardView.startCampaignStickerAnimation();
    }

    public void startRewardCampaignStickerAnimation()
    {
        if (mDailyRewardCardView == null || mDailyRewardCardView.getVisibility() == View.GONE)
        {
            return;
        }

        mDailyRewardCardView.startCampaignStickerAnimation();
    }

    public void stopRewardCampaignStickerAnimation()
    {
        if (mDailyRewardCardView == null)
        {
            return;
        }

        mDailyRewardCardView.stopCampaignStickerAnimation();
    }

    public void setMemberRewardData(String titleText, String descriptionText, String optionText, int stickerCount)
    {
        mDailyRewardCardView.setGuideVisible(true);
        mDailyRewardCardView.setRewardTitleText(titleText);
        mDailyRewardCardView.setDescriptionText(descriptionText);
        mDailyRewardCardView.setOptionText(optionText);
        mDailyRewardCardView.setStickerCount(stickerCount);

        mDailyRewardCardView.setOnOptionClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onRewardDetailClick();
            }
        });
    }

    public void setCategoryStayOutboundNewVisible(boolean visible)
    {
        if (mCategoryLayout == null)
        {
            return;
        }

        // 해외 호텔 new 표시
        mCategoryLayout.setStayOutBoundNewVisible(visible);
    }

    public void setWishListData(ArrayList<CarouselListItem> list, boolean isError)
    {
        mWishListLayout.setData(list, false);

        if (isError == true)
        {
            setErrorPopupLayout(true);
        }
    }

    public void setRecentListData(ArrayList<CarouselListItem> list, boolean isError)
    {
        mRecentListLayout.setData(list, false);

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

    void setErrorPopupLayout(final boolean isShow)
    {
        if (mErrorPopupLayout == null || mContext == null)
        {
            return;
        }

        if (mOnScreenScrollChangeListener != null)
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

                if (mErrorPopupAnimator != null)
                {
                    mErrorPopupAnimator.removeAllListeners();
                    mErrorPopupAnimator.removeAllUpdateListeners();
                    mErrorPopupAnimator = null;
                }

                mErrorPopupLayout.setTranslationY(end);

                // 마지막으로 한번더 하단 버튼을 노출해 준다.
                if (mOnScreenScrollChangeListener != null)
                {
                    mOnScreenScrollChangeListener.onScrollState(isShow);
                }
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

    boolean getErrorPopupVisible()
    {
        if (mErrorPopupLayout == null)
        {
            return false;
        }

        return mErrorPopupLayout.getVisibility() == View.VISIBLE;
    }

    @Deprecated
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
        valueAnimator.addUpdateListener(animation -> {
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
        closeValueAnimator.addUpdateListener(animation -> {
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
        clearScrollButtonAnimation();

        if (mActionButtonLayoutVisibility == visibility)
        {
            return;
        }

        mActionButtonLayoutVisibility = visibility;

        mActionButtonLayout.setVisibility(visibility);
        View scrollLayout = mScrollButtonLayout.findViewById(R.id.productButtonLayout);
        scrollLayout.setVisibility(View.VISIBLE == visibility ? View.GONE : View.VISIBLE);
    }

    public void clearScrollButtonAnimation()
    {
        if (mScrollButtonLayoutAnimator != null)
        {
            mScrollButtonLayoutAnimator.cancel();
            mScrollButtonLayoutAnimator.removeAllUpdateListeners();
            mScrollButtonLayoutAnimator.removeAllListeners();
            mScrollButtonLayoutAnimator = null;
        }
    }

    public void setScrollButtonLayoutAnimation(final int visibility)
    {

        if (mScrollButtonLayout == null)
        {
            return;
        }

        if (mActionButtonLayoutVisibility == visibility)
        {
            return;
        }

        clearScrollButtonAnimation();

        mActionButtonLayoutVisibility = visibility;

        final boolean isShow = visibility == View.VISIBLE;

        float start = isShow == true ? 1.0f : 0.0f;
        float end = isShow == true ? 0.0f : 1.0f;

        View scrollLayout = mScrollButtonLayout.findViewById(R.id.productButtonLayout);
        View productLayout = mActionButtonLayout.findViewById(R.id.buttonLayout);
        View stayButtonLayout = mActionButtonLayout.findViewById(R.id.stayButtonLayout);
        View stayButtonImageView = mActionButtonLayout.findViewById(R.id.stayButtonImageView);
        View gourmetButtonLayout = mActionButtonLayout.findViewById(R.id.gourmetButtonLayout);
        View gourmetButtonImageView = mActionButtonLayout.findViewById(R.id.gourmetButtonImageView);

        ViewGroup.MarginLayoutParams productLayoutParams = (ViewGroup.MarginLayoutParams) productLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams stayLayoutParams = (ViewGroup.MarginLayoutParams) stayButtonLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams stayImageLayoutParams = (ViewGroup.MarginLayoutParams) stayButtonImageView.getLayoutParams();
        ViewGroup.MarginLayoutParams gourmetLayoutParams = (ViewGroup.MarginLayoutParams) gourmetButtonLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams gourmetImageLayoutParams = (ViewGroup.MarginLayoutParams) gourmetButtonImageView.getLayoutParams();

        int maxLayoutHeight = ScreenUtils.dpToPx(mContext, 96d);
        int layoutVerticalPadding = ScreenUtils.dpToPx(mContext, 10d);
        int gapLayoutHeightValue = layoutVerticalPadding * 2;
        int minLayoutHeight = maxLayoutHeight - gapLayoutHeightValue;
        int horizontalInsideValue = ScreenUtils.dpToPx(mContext, 5d); // 각 버튼의 가로 안쪽 마진
        int horizontalOutsideValue = ScreenUtils.dpToPx(mContext, 15d); // 각 버튼의 가로 바깥쪽 마진
        int verticalOutsideValue = ScreenUtils.dpToPx(mContext, 5d); // 각 버튼의 세로 마진
        int showImageLeftMargin = ScreenUtils.dpToPx(mContext, 12d);
        int hideImageLeftMargin = ScreenUtils.dpToPx(mContext, 7d);
        int animationImageLeftMargin = horizontalOutsideValue + hideImageLeftMargin;

        mScrollButtonLayoutAnimator = ValueAnimator.ofFloat(start, end);
        mScrollButtonLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float ratio = (float) animation.getAnimatedValue();
                int heightValue = minLayoutHeight + (int) (gapLayoutHeightValue * ratio);
                int leftMarginValue = showImageLeftMargin + (int) ((animationImageLeftMargin - showImageLeftMargin) * ratio);

                productLayoutParams.height = heightValue;
                stayImageLayoutParams.leftMargin = leftMarginValue;

                productLayout.setLayoutParams(productLayoutParams);
                stayButtonImageView.setLayoutParams(stayImageLayoutParams);
            }
        });

        mScrollButtonLayoutAnimator.setDuration(ACTION_BUTTON_DURATION);
        mScrollButtonLayoutAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                scrollLayout.setVisibility(View.INVISIBLE);
                mActionButtonLayout.setVisibility(View.VISIBLE);

                stayButtonLayout.setBackgroundResource(0);
                gourmetButtonLayout.setBackgroundResource(0);

                productLayout.setPadding(0, 0, 0, 0);
                productLayoutParams.height = isShow == true ? maxLayoutHeight : minLayoutHeight;

                stayLayoutParams.topMargin = 0;
                stayLayoutParams.bottomMargin = 0;
                stayLayoutParams.leftMargin = 0;
                stayLayoutParams.rightMargin = 0;

                gourmetLayoutParams.topMargin = 0;
                gourmetLayoutParams.bottomMargin = 0;
                gourmetLayoutParams.leftMargin = 0;
                gourmetLayoutParams.rightMargin = 0;

                stayImageLayoutParams.leftMargin = animationImageLeftMargin;
                gourmetImageLayoutParams.leftMargin = showImageLeftMargin;

                productLayout.setLayoutParams(productLayoutParams);
                stayButtonLayout.setLayoutParams(stayLayoutParams);
                gourmetButtonLayout.setLayoutParams(gourmetLayoutParams);
                stayButtonImageView.setLayoutParams(stayImageLayoutParams);
                gourmetButtonImageView.setLayoutParams(gourmetImageLayoutParams);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                scrollLayout.setVisibility(isShow == true ? View.INVISIBLE : View.VISIBLE);
                mActionButtonLayout.setVisibility(isShow == true ? View.VISIBLE : View.GONE);

                stayButtonLayout.setBackgroundResource(isShow == true ? 0 : R.drawable.home_category_btn);
                gourmetButtonLayout.setBackgroundResource(isShow == true ? 0 : R.drawable.home_category_btn);

                int verticalPaddingValue = isShow == true ? 0 : layoutVerticalPadding;
                productLayout.setPadding(0, verticalPaddingValue, 0, verticalPaddingValue);
                productLayoutParams.height = isShow == true ? minLayoutHeight : maxLayoutHeight;

                stayLayoutParams.topMargin = isShow == true ? 0 : verticalOutsideValue;
                stayLayoutParams.bottomMargin = isShow == true ? 0 : verticalOutsideValue;
                stayLayoutParams.leftMargin = isShow == true ? 0 : horizontalOutsideValue;
                stayLayoutParams.rightMargin = isShow == true ? 0 : horizontalInsideValue;

                gourmetLayoutParams.topMargin = isShow == true ? 0 : verticalOutsideValue;
                gourmetLayoutParams.bottomMargin = isShow == true ? 0 : verticalOutsideValue;
                gourmetLayoutParams.leftMargin = isShow == true ? 0 : horizontalInsideValue;
                gourmetLayoutParams.rightMargin = isShow == true ? 0 : horizontalOutsideValue;

                stayImageLayoutParams.leftMargin = isShow == true ? showImageLeftMargin : hideImageLeftMargin;
                gourmetImageLayoutParams.leftMargin = isShow == true ? showImageLeftMargin : hideImageLeftMargin;

                productLayout.setLayoutParams(productLayoutParams);
                stayButtonLayout.setLayoutParams(stayLayoutParams);
                gourmetButtonLayout.setLayoutParams(gourmetLayoutParams);
                stayButtonImageView.setLayoutParams(stayImageLayoutParams);
                gourmetButtonImageView.setLayoutParams(gourmetImageLayoutParams);
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

        mScrollButtonLayoutAnimator.start();
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
            mDailyHomeScrollView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //                    mDailyHomeScrollView.fullScroll(View.FOCUS_UP);
                    mDailyHomeScrollView.smoothScrollTo(0, 0);
                }
            }, 50);
        }
    }

    public void setScrollBottom()
    {
        if (mDailyHomeScrollView != null && mDailyHomeScrollView.getChildCount() != 0)
        {
            mDailyHomeScrollView.postDelayed(() -> {
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

    void moveNextEventPosition(RecyclerView eventRecyclerView, int currentPosition)
    {
        if (mEventHandler == null)
        {
            return;
        }

        mEventHandler.removeMessages(0);

        Message message = new Message();
        message.what = 0;
        message.arg1 = currentPosition;
        message.obj = eventRecyclerView;
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

            RecyclerView eventRecyclerView = (RecyclerView) msg.obj;
            if (eventRecyclerView == null)
            {
                return;
            }

            eventRecyclerView.smoothScrollToPosition(msg.arg1 + 1);
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

            // globalVisibleRect 로 동작시 android os 4.X 에서 화면을 벗어날때 rect.top 이 증가하는 이슈로 상단 뷰 크기를 고정으로 알아와서 적용!
            if (scrollY >= mEventImageHeight)
            {
                // show animation
                setScrollButtonLayoutAnimation(View.VISIBLE);
            } else if (scrollY > mEventImageHeight - mSkipScrollAnimationGapValue && scrollY < mEventImageHeight)
            {
                // hide animation
                setScrollButtonLayoutAnimation(View.GONE);
            } else
            {
                // hide none animation
                setActionButtonVisibility(View.GONE);
            }

            if (scrollY > 0)
            {
                mSwipeRefreshLayout.setEnabled(false);
            } else
            {
                mSwipeRefreshLayout.setEnabled(true);
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
            if (child != null && mProviderLayout != null)
            {
                isCanScroll = v.getHeight() - mContext.getResources().getDimensionPixelSize(R.dimen.bottom_navigation_height_over21) < mProviderLayout.getBottom() - child.getY();
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

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING)
            {
                if (mEventHandler != null)
                {
                    mEventHandler.removeMessages(0);
                }
            }

            mSwipeRefreshLayout.setEnabled(RecyclerView.SCROLL_STATE_IDLE == newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            int totalCount = mEventListAdapter.getRealCount();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mEventRecyclerView.getLayoutManager();

            setEventCountView(linearLayoutManager.findFirstVisibleItemPosition() % totalCount, totalCount);
            moveNextEventPosition(recyclerView, linearLayoutManager.findFirstVisibleItemPosition());
        }
    };

    class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.SimpleDraweeViewHolder>
    {
        private static final String DEFAULT_EVENT_IMAGE_URL = "defaultImageUrl";

        private List<Event> mHomeEventList;
        View.OnClickListener mOnClickListener;

        public EventListAdapter(Context context, View.OnClickListener listener)
        {
            setData(null);

            mOnClickListener = listener;
        }

        public void setData(List<Event> list)
        {
            if (mHomeEventList == null)
            {
                mHomeEventList = new ArrayList<>();
            }

            mHomeEventList.clear();

            if (list != null && list.size() > 0)
            {
                mHomeEventList.addAll(list);
            }
        }

        @Override
        public SimpleDraweeViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            final int width = ScreenUtils.getScreenWidth(mContext);
            final int height = ScreenUtils.getRatioHeightType16x9(width);

            final SimpleDraweeView simpleDraweeView = new SimpleDraweeView(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
            simpleDraweeView.setLayoutParams(layoutParams);

            return new SimpleDraweeViewHolder(simpleDraweeView);
        }

        @Override
        public void onBindViewHolder(SimpleDraweeViewHolder holder, int position)
        {
            if (mHomeEventList == null || mHomeEventList.size() == 0 || position < 0)
            {
                holder.simpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                //            contentImageView.setTag(contentImageView.getId(), position);
                holder.simpleDraweeView.setTag(null);
                holder.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

                return;
            }

            if (position < getItemCount())
            {
                Event homeEvent = mHomeEventList.get(position % getRealCount());

                holder.simpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
                //            contentImageView.setTag(contentImageView.getId(), position);
                holder.simpleDraweeView.setTag(homeEvent);
                holder.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

                String url = ScreenUtils.getScreenWidth(mContext) < 1440 ? homeEvent.lowResolutionImageUrl : homeEvent.defaultImageUrl;
                if (DEFAULT_EVENT_IMAGE_URL.equalsIgnoreCase(url) == true)
                {
                    // RemoteConfig 실패등의 상황에서 기본 layerlist_placeholder 만 노출
                    holder.simpleDraweeView.setImageURI((String) null);
                } else
                {
                    Util.requestImageResize(mContext, holder.simpleDraweeView, url);
                }
            } else
            {
                Util.restartApp(mContext);
            }
        }

        @Override
        public int getItemCount()
        {
            if (getRealCount() <= 1)
            {
                return getRealCount();
            } else
            {
                return Integer.MAX_VALUE;
            }
        }

        public int getRealCount()
        {
            return mHomeEventList.size();
        }

        class SimpleDraweeViewHolder extends RecyclerView.ViewHolder
        {
            SimpleDraweeView simpleDraweeView;

            public SimpleDraweeViewHolder(View itemView)
            {
                super(itemView);

                simpleDraweeView = (SimpleDraweeView) itemView;
                itemView.setOnClickListener(mOnClickListener);
            }
        }
    }
}