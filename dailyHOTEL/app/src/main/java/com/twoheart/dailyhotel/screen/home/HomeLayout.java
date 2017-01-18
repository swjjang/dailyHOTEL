package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HomeRecommed;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.io.File;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseLayout
{
    private DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout mAppbarLayout;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mContentLayout;
    private HomeEventImageViewPagerAdapter mEventViewPagerAdapter;
    private View mMessageLayout;
    private HomeRecommedLayout mHomeRecommedLayout;
    private HomeCarouselLayout mWishListLayout;
    private HomeCarouselLayout mRecentListLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onMessageTextAreaClick();

        void onMessageCloseClick();

        void onSearchImageClick();

        void onStayButtonClick();

        void onGourmetButtonClick();

        void onRefreshAll(boolean isShowProgress);
    }

    private enum MessageType
    {
        NONE,
        TEXT,
        REVIEW
    }


    public HomeLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        mAppbarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);

        initRefreshLayout(view);
        initScrollLayout(view);
        initContentLayout(view);

        initEventLayout(view);
        initProductLayout(view);
        initMessageLayout(view);
        initWishListLayout(view);
        initRecentListLayout(view);
        initRecommendLayout(view);

        initTopButtonLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        View searchImageView = view.findViewById(R.id.searchImageView);
        searchImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onSearchImageClick();
            }
        });
    }

    private void initRefreshLayout(View view)
    {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

        // 리프레시 기능 미 구현으로 인한 false 처리
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.dh_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((HomeLayout.OnEventListener) mOnEventListener).onRefreshAll(false);
            }
        });


    }

    private void initScrollLayout(View view)
    {
        mNestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        mNestedScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void initContentLayout(View view)
    {
        mContentLayout = (LinearLayout) view.findViewById(R.id.homeContentLayout);
    }

    private void initEventLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mEventViewPager = (DailyLoopViewPager) view.findViewById(R.id.loopViewPager);
        mEventCountTextView = (DailyTextView) view.findViewById(R.id.pagerCountTextView);

        int height = Util.getListRowHeight(mContext);

        ViewGroup.LayoutParams params = mEventViewPager.getLayoutParams();
        params.height = height;
        mEventViewPager.setLayoutParams(params);

        setDefaultEventImage();
    }

    private void initProductLayout(View view)
    {
        if (mContentLayout == null)
        {
            return;
        }

        View stayButtonLayout = view.findViewById(R.id.stayButtonLayout);
        View gourmetButtonLayout = view.findViewById(R.id.gourmetButtonLayout);

        stayButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onStayButtonClick();
            }
        });

        gourmetButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onGourmetButtonClick();
            }
        });
    }

    private void initMessageLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        MessageType messageType = MessageType.NONE;

        if (DailyHotel.isLogin() == true)
        {
            boolean isLoginAreaEnable = DailyPreference.getInstance(mContext).isRemoteConfigHomeMessageAreaLoginEnabled();
            if (isLoginAreaEnable == true)
            {
                messageType = MessageType.REVIEW;
            }
        } else
        {
            boolean isLogoutAreaEnable = DailyPreference.getInstance(mContext).isRemoteConfigHomeMessageAreaLogoutEnabled();
            if (isLogoutAreaEnable == true)
            {
                messageType = MessageType.TEXT;
            }
        }

        if (MessageType.REVIEW == messageType)
        {
            // init review layout
            initMessageReviewLayout(view);
        } else if (MessageType.TEXT == messageType)
        {
            // init text layout
            initMessageTextLayout(view);
        } else
        {
            // gone message layout
            if (mMessageLayout != null)
            {
                mMessageLayout.setVisibility(View.GONE);
            }
        }
    }

    private void initMessageReviewLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mMessageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_review_layout, null);

        // TODO : review layout 생성 및 request 요청하는 부분 생성 필요!
        mContentLayout.addView(mMessageLayout);
    }

    private void initMessageTextLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mMessageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_text_layout, null);

        View homeMessageLayout = mMessageLayout.findViewById(R.id.homeMessageLayout);
        View closeView = mMessageLayout.findViewById(R.id.closeImageView);
        DailyTextView titleView = (DailyTextView) mMessageLayout.findViewById(R.id.titleTextView);
        DailyTextView descriptionView = (DailyTextView) mMessageLayout.findViewById(R.id.descriptionTextView);

        homeMessageLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onMessageTextAreaClick();
            }
        });

        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMessageLayoutCloseAnimation();
            }
        });

        String title = DailyPreference.getInstance(mContext).getRemoteConfigHomeMessageAreaLogoutTitle();
        titleView.setText(title);

        String description = DailyPreference.getInstance(mContext).getRemoteConfigHomeMessageAreaLogoutCallToAction();

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

        mContentLayout.addView(mMessageLayout);
    }

    private void initWishListLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mWishListLayout = new HomeCarouselLayout(mContext);

        mContentLayout.addView(mWishListLayout);
    }

    private void initRecentListLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        mRecentListLayout = new HomeCarouselLayout(mContext);

        mContentLayout.addView(mRecentListLayout);
    }

    private void initRecommendLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        HomeRecommedLayout recommendLayout = new HomeRecommedLayout(mContext);
        recommendLayout.setListener(new HomeRecommedLayout.HomeRecommendListener()
        {
            @Override
            public void onRecommedClick(HomeRecommed recommed, int position)
            {
                // TODO : 추천 상세로 이동!!!
            }
        });

        mContentLayout.addView(recommendLayout);
    }

    private void initTopButtonLayout(View view)
    {
        if (mContentLayout == null || mContext == null)
        {
            return;
        }

        final View topButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_top_button_layout, null);

        topButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mAppbarLayout.setExpanded(true);
            }
        });
        mContentLayout.addView(topButtonLayout);
    }

    private void startMessageLayoutCloseAnimation()
    {
        if (mMessageLayout == null)
        {
            return;
        }

        if (mMessageLayout.getVisibility() == View.GONE)
        {
            return;
        }

        ValueAnimator closeValueAnimator = ValueAnimator.ofInt(mMessageLayout.getHeight(), 0);
        closeValueAnimator.setDuration(100);
        closeValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        closeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mMessageLayout.getLayoutParams();
                params.height = value;
                mMessageLayout.setLayoutParams(params);
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
                mMessageLayout.setVisibility(View.GONE);
                mMessageLayout.clearAnimation();
                mContentLayout.removeView(mMessageLayout);
                mMessageLayout = null;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mMessageLayout.setVisibility(View.GONE);
                mMessageLayout.clearAnimation();
                mContentLayout.removeView(mMessageLayout);
                mMessageLayout = null;
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        closeValueAnimator.start();
    }

    public void setRefreshing(boolean isRefreshing)
    {
        if (mSwipeRefreshLayout == null)
        {
            return;
        }

        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public void setEventCountView(int pageIndex, int totalCount)
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

    public void setDefaultEventImage()
    {
        String url = getDefaultImage();
        setEventCountView(1, 1);

        if (mEventViewPagerAdapter == null)
        {
            mEventViewPagerAdapter = new HomeEventImageViewPagerAdapter(mContext);
        }

        ArrayList<ImageInformation> arrayList = new ArrayList<>();
        arrayList.add(new ImageInformation(url, null));

        mEventViewPagerAdapter.setData(arrayList);
        mEventViewPager.setAdapter(mEventViewPagerAdapter);
    }

    public void setRecommedList(ArrayList<HomeRecommed> list)
    {
        mHomeRecommedLayout.setData(list, true);
    }

    // TODO : R.drawable.banner 의 경우 임시 테스트로 들어간 이미지로 1월 30일 이후에 growth 에서 전달받은 이미지로 적용해야 함
    private String getDefaultImage()
    {
        String homeEventCurrentVersion = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventCurrentVersion();

        if (Util.isTextEmpty(homeEventCurrentVersion) == true  //
            || Constants.DAILY_HOME_EVENT_CURRENT_VERSION.equalsIgnoreCase(homeEventCurrentVersion) == true)
        {
            return HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL;
        } else
        {
            String fileName = Util.makeIntroImageFileName(homeEventCurrentVersion);
            File file = new File(getCacheDir(), fileName);

            if (file.exists() == false)
            {
                DailyPreference.getInstance(mContext).setRemoteConfigIntroImageVersion(Constants.DAILY_HOME_EVENT_CURRENT_VERSION);
                return HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL;
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
                    DailyPreference.getInstance(mContext).setRemoteConfigIntroImageVersion(Constants.DAILY_HOME_EVENT_CURRENT_VERSION);
                    return HomeEventImageViewPagerAdapter.DEFAULT_EVENT_IMAGE_URL;
                } else
                {
                    return urlString;
                }
            }
        }
    }

}
