package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HomeRecommend;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.ReviewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseLayout
{
    private static final int MESSAGE_ANIMATION_DURATION = 200;
    private static final double BUTTON_LAYOUT_MIN_HEIGHT = 76d;
    private static final double BUTTON_LAYOUT_MAX_HEIGHT = 82d;

    private DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    private HomeEventImageViewPagerAdapter mEventViewPagerAdapter;

    private int mEventImageHeight;
    private int mButtonGapHeight;
    private int mScrollButtonMaxHeight;
    private int mScrollButtonMinHeight;

    private View mActionButtonLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mHomeContentLayout;
    private View mEventAreaLayout;
    private View mScrollButtonLayout;
    private View mTextMessageLayout;
    private View mReviewMessageLayout;
    private HomeCarouselLayout mRecentListLayout;
    private HomeCarouselLayout mWishListLayout;
    private HomeRecommendLayout mHomeRecommendLayout;

    private DailyEmoticonImageView[] mDailyEmoticonImageView;


    public interface OnEventListener extends OnBaseEventListener
    {
        void onMessageTextAreaClick();

        void onMessageCloseClick();

        void onSearchImageClick();

        void onStayButtonClick();

        void onGourmetButtonClick();

        void onRefreshAll(boolean isShowProgress);

        void onTopButtonClick();
    }

    public enum MessageType
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
        mHomeContentLayout = (LinearLayout) view.findViewById(R.id.homContentLayout);
        //        mHomeContentLayout.removeAllViews();

        initEventLayout(mHomeContentLayout);
        initScrollButtonLayout(mHomeContentLayout);
        initMessageLayout(mHomeContentLayout);
        initWishListLayout(mHomeContentLayout);
        initRecentListLayout(mHomeContentLayout);
        initRecommendLayout(mHomeContentLayout);
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

        setEventImageList(null);
    }

    private void initScrollButtonLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mScrollButtonLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_product_layout, null);
        layout.addView(mScrollButtonLayout);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Util.dpToPx(mContext, BUTTON_LAYOUT_MAX_HEIGHT));
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

    private void initMessageLayout(LinearLayout layout)
    {
        if (layout == null)
        {
            return;
        }

        initTextMessageLayout(layout);
        initReviewMessageLayout(layout);
    }

    private void initTextMessageLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mTextMessageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_text_layout, null);
        mTextMessageLayout.setVisibility(View.GONE);
        layout.addView(mTextMessageLayout);
    }

    private void initReviewMessageLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mReviewMessageLayout = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_message_type_review_layout, null);
        mReviewMessageLayout.setVisibility(View.GONE);
        layout.addView(mReviewMessageLayout);
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

        // Test Data
        mRecentListLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 임시 테스트 데이터
                ArrayList<Stay> placeList = new ArrayList<>();
                Random random = new Random();
                int size = random.nextInt(14);
                for (int i = 0; i < size; i++)
                {
                    Stay stay = new Stay();

                    stay.price = Math.abs(random.nextInt(100000));
                    stay.name = "Stay " + i;
                    stay.discountPrice = Math.abs(stay.price - random.nextInt(10000));
                    stay.districtName = "서울";
                    stay.isSoldOut = i % 5 == 0;

                    if (i % 3 == 0)
                    {
                        stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/01.jpg";
                    } else if (i % 3 == 1)
                    {
                        stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/02.jpg";
                    } else
                    {
                        stay.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/03.jpg";
                    }
                    placeList.add(stay);

                    stay.setGrade(Stay.Grade.special2);
                }
                mRecentListLayout.setData(placeList);
                // 임시 테스트 데이터 끝!
            }
        }, 5000);
    }

    private void initRecommendLayout(LinearLayout layout)
    {
        if (layout == null || mContext == null)
        {
            return;
        }

        mHomeRecommendLayout = new HomeRecommendLayout(mContext);
        mHomeRecommendLayout.setListener(new HomeRecommendLayout.HomeRecommendListener()
        {
            @Override
            public void onRecommedClick(HomeRecommend recommed, int position)
            {
                // TODO : 추천 상세로 이동!!!
            }
        });

        layout.addView(mHomeRecommendLayout);

        // Test Data
        mHomeRecommendLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 임시 테스트 데이터
                ArrayList<HomeRecommend> recommendList = new ArrayList<>();
                Random random = new Random();
                int size = random.nextInt(8);
                for (int i = 0; i < size; i++)
                {
                    HomeRecommend homeRecommend = new HomeRecommend();

                    homeRecommend.title = "Recommend " + i;
                    homeRecommend.description = " Recommend description " + i;
                    homeRecommend.count = Math.abs(random.nextInt(11));

                    if (i % 3 == 0)
                    {
                        homeRecommend.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/01.jpg";
                    } else if (i % 3 == 1)
                    {
                        homeRecommend.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/02.jpg";
                    } else
                    {
                        homeRecommend.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/03.jpg";
                    }
                    recommendList.add(homeRecommend);
                }
                mHomeRecommendLayout.setData(recommendList, true);
                // 임시 테스트 데이터 끝!
            }
        }, 5000);
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

    public void setEventImageList(ArrayList<ImageInformation> list)
    {
        if (list == null || list.size() == 0)
        {
            String url = getDefaultImage();

            list = new ArrayList<>();
            list.add(new ImageInformation(url, null));
        }

        if (mEventViewPagerAdapter == null)
        {
            mEventViewPagerAdapter = new HomeEventImageViewPagerAdapter(mContext);
        }

        mEventViewPagerAdapter.setData(list);

        setEventCountView(1, mEventViewPagerAdapter.getCount());

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
                int displayPosition = position + 1;
                int totalCount = mEventViewPagerAdapter.getCount();
                setEventCountView(displayPosition, totalCount);
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
        mReviewMessageLayout.setVisibility(View.GONE);
    }

    public void setTextMessageData(String title, String description)
    {
        if (mTextMessageLayout == null)
        {
            return;
        }

        if (mReviewMessageLayout != null)
        {
            mReviewMessageLayout.setVisibility(View.GONE);
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

                ((HomeLayout.OnEventListener) mOnEventListener).onMessageCloseClick();
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

    public void setReviewData(Review review)
    {
        if (mReviewMessageLayout == null)
        {
            return;
        }

        if (mTextMessageLayout != null)
        {
            mTextMessageLayout.setVisibility(View.GONE);
        }

        mReviewMessageLayout.setVisibility(View.GONE);
        mReviewMessageLayout.clearAnimation();

        if (review == null)
        {
            return;
        }

        View closeView = mReviewMessageLayout.findViewById(R.id.closeImageView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMessageLayoutCloseAnimation(mReviewMessageLayout);
            }
        });

        TextView titleTextView = (TextView) mReviewMessageLayout.findViewById(R.id.titleTextView);
        TextView periodTextView = (TextView) mReviewMessageLayout.findViewById(R.id.descriptionTextView);
        View goodEmoticonView = mReviewMessageLayout.findViewById(R.id.goodEmoticonView);
        View badEmoticonView = mReviewMessageLayout.findViewById(R.id.badEmoticonView);

        final ReviewItem reviewItem = review.getReviewItem();
        if (reviewItem == null)
        {
            mReviewMessageLayout.setVisibility(View.GONE);
            mReviewMessageLayout.clearAnimation();

            throw new NullPointerException("reviewItem == null");
        }

        // 타이틀
        String title = mContext.getResources().getString(R.string.message_review_title, reviewItem.itemName);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
        spannableStringBuilder.setSpan(new CustomFontTypefaceSpan(FontManager.getInstance(mContext).getRegularTypeface()),//
            title.lastIndexOf('\'') + 1, title.length(),//
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        titleTextView.setText(spannableStringBuilder);

        try
        {
            // 시간
            switch (reviewItem.placeType)
            {
                case HOTEL:
                {
                    String periodDate = String.format("%s - %s"//
                        , DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)")//
                        , DailyCalendar.convertDateFormatString(reviewItem.useEndDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"));
                    periodTextView.setText(mContext.getResources().getString(R.string.message_review_date, periodDate));
                    break;
                }

                case FNB:
                {
                    String periodDate = DailyCalendar.convertDateFormatString(reviewItem.useStartDate, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");

                    periodTextView.setText(mContext.getResources().getString(R.string.message_review_date, periodDate));
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        mDailyEmoticonImageView = null;
        mDailyEmoticonImageView = new DailyEmoticonImageView[2];

        // 이미지
        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) mReviewMessageLayout.findViewById(R.id.badEmoticonImageView);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) mReviewMessageLayout.findViewById(R.id.goodEmoticonImageView);

        mDailyEmoticonImageView[0].setJSONData("Review_Animation.aep.comp-737-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[1].setJSONData("Review_Animation.aep.comp-573-B_satfisfied.kf.json");

        mDailyEmoticonImageView[0].startAnimation();
        mDailyEmoticonImageView[1].startAnimation();

        // 딤이미지
        final View badEmoticonDimView = mReviewMessageLayout.findViewById(R.id.badEmoticonDimView);
        final View goodEmoticonDimView = mReviewMessageLayout.findViewById(R.id.goodEmoticonDimView);

        // 텍스트
        final TextView badEmoticonTextView = (TextView) mReviewMessageLayout.findViewById(R.id.badEmoticonTextView);
        final TextView goodEmoticonTextView = (TextView) mReviewMessageLayout.findViewById(R.id.goodEmoticonTextView);

        goodEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO : 클릭 이벤트 적용 필요!
            }
        });

        badEmoticonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO : 클릭 이벤트 적용 필요!
            }
        });

        mReviewMessageLayout.setVisibility(View.INVISIBLE);

        mReviewMessageLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                startMessageLayoutShowAnimation(mReviewMessageLayout);
            }
        });
    }

    public void setWishListData(ArrayList<? extends Place> list)
    {
        mWishListLayout.setData(list);
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

                if (view.getId() == mReviewMessageLayout.getId())
                {
                    onDestroyReviewAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                view.setVisibility(View.GONE);
                view.clearAnimation();

                if (view.getId() == mReviewMessageLayout.getId())
                {
                    onDestroyReviewAnimation();
                }
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

    public void setScrollTop()
    {
        if (mNestedScrollView != null && mNestedScrollView.getChildCount() != 0)
        {
            mNestedScrollView.fullScroll(View.FOCUS_UP);
        }
    }

    public void onResumeReviewAnimation()
    {
        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.startAnimation();
            }
        }
    }

    public void onPauseReviewAnimation()
    {
        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.stopAnimation();
            }
        }
    }

    public void onDestroyReviewAnimation()
    {
        if (mDailyEmoticonImageView != null)
        {
            for (DailyEmoticonImageView dailyEmoticonImageView : mDailyEmoticonImageView)
            {
                dailyEmoticonImageView.stopAnimation();
            }
        }

        mDailyEmoticonImageView = null;
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
        }
    };

}
