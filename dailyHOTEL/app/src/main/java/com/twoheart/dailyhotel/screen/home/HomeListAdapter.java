package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HomeRecommed;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.ReviewItem;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;
import com.twoheart.dailyhotel.widget.DailyEmoticonImageView;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by android_sam on 2017. 1. 24..
 */

public class HomeListAdapter extends BaseAdapter
{
    private static final int NUM_OF_VIEW = 8;
    private static final int MESSAGE_ANIMATION_DURATION = 200;

    private Context mContext;
    private int mEmptyViewHeight;
    private View[] mHomeItemView;

    private View mProductLayout;

    private HomeLayout.OnEventListener mOnEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    // text messageData
    private String mTextMessageTitle;
    private String mTextMessageDescription;

    // review messageData
    private Review mReview;
    private DailyEmoticonImageView[] mDailyEmoticonImageView;

    public HomeListAdapter(Context context, int emptyViewHeight, HomeLayout.OnEventListener eventListener, View.OnTouchListener emptyViewOnTouchListener)
    {
        mContext = context;
        mEmptyViewHeight = emptyViewHeight;
        mHomeItemView = new View[NUM_OF_VIEW];

        mOnEventListener = eventListener;
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;

        // initialize text message data
        mTextMessageTitle = null;
        mTextMessageDescription = null;
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCount()
    {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LinearLayout linearLayout;

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
        {
            linearLayout = new LinearLayout(mContext);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        } else
        {
            linearLayout = (LinearLayout) convertView;
        }

        linearLayout.removeAllViews();

        // 이벤트 뷰페이저 영역 크기 만큼의 빈뷰!

        if (mHomeItemView[0] == null)
        {
            mHomeItemView[0] = layoutInflater.inflate(R.layout.list_row_home_empty_layout, parent, false);
        }

        getEmptyLayout(mHomeItemView[0]);
        linearLayout.addView(mHomeItemView[0]);

        // 스테이, 고메 이동 버튼 레이아웃!
        if (mHomeItemView[1] == null)
        {
            mHomeItemView[1] = layoutInflater.inflate(R.layout.list_row_home_product_layout, parent, false);
        }

        getProductLayout(mHomeItemView[1]);
        linearLayout.addView(mHomeItemView[1]);

        // 메세지 영역(기본 메세지) 레이아웃
        if (mHomeItemView[2] == null)
        {
            mHomeItemView[2] = layoutInflater.inflate(R.layout.list_row_home_message_type_text_layout, parent, false);
        }

        getTextMessageLayout(mHomeItemView[2]);
        linearLayout.addView(mHomeItemView[2]);

        // 메세지 영역(리뷰) 레이아웃
        if (mHomeItemView[3] == null)
        {
            mHomeItemView[3] = layoutInflater.inflate(R.layout.list_row_home_message_type_review_layout, parent, false);
        }

        getMessageReviewLayout(mHomeItemView[3]);
        linearLayout.addView(mHomeItemView[3]);

        // wishList layout
        if (mHomeItemView[4] == null)
        {
            mHomeItemView[4] = new HomeCarouselLayout(mContext);
        }

        getWishListLayout(mHomeItemView[4]);
        linearLayout.addView(mHomeItemView[4]);

        // recentList layout
        if (mHomeItemView[5] == null)
        {
            mHomeItemView[5] = new HomeCarouselLayout(mContext);
        }

        getRecentListLayout(mHomeItemView[5]);
        linearLayout.addView(mHomeItemView[5]);

        // recommend layout
        if (mHomeItemView[6] == null)
        {
            mHomeItemView[6] = new HomeRecommendLayout(mContext);
        }

        getRecommendListLayout(mHomeItemView[6]);
        linearLayout.addView(mHomeItemView[6]);

        // top button layout
        if (mHomeItemView[7] == null)
        {
            mHomeItemView[7] = layoutInflater.inflate(R.layout.list_row_home_top_button_layout, parent, false);
        }

        getTopButtonLayout(mHomeItemView[7]);
        linearLayout.addView(mHomeItemView[7]);

        return linearLayout;
    }

    public void setTextMessageData(String title, String description)
    {
        mTextMessageTitle = title;
        mTextMessageDescription = description;
    }

    public void setTextMessageLayoutVisibility(int visibility)
    {
        if (mHomeItemView[2] != null)
        {
            mHomeItemView[2].setVisibility(visibility);
        }
    }

    public void setReviewMessageData(Review review)
    {
        mReview = review;
    }

    public void setReviewMessageLayoutVisibility(int visibility)
    {
        if (mHomeItemView[3] != null)
        {
            mHomeItemView[3].setVisibility(visibility);
        }
    }

    public View getProductLayout() {
        return mProductLayout;
    }

    private View getEmptyLayout(View view)
    {
        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = mEmptyViewHeight;

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(mEmptyViewOnTouchListener);

        return view;
    }

    private void getProductLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        mProductLayout = view.findViewById(R.id.productLayout);

        View stayButtonLayout = view.findViewById(R.id.stayButtonLayout);
        View gourmetButtonLayout = view.findViewById(R.id.gourmetButtonLayout);

        stayButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.onStayButtonClick();
            }
        });

        gourmetButtonLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.onGourmetButtonClick();
            }
        });
    }

    private void getTextMessageLayout(final View view)
    {
        if (view == null)
        {
            return;
        }

        view.setVisibility(View.GONE);
        view.clearAnimation();

        if (Util.isTextEmpty(mTextMessageTitle) == true && Util.isTextEmpty(mTextMessageDescription) == true)
        {
            return;
        }

        View homeMessageLayout = view.findViewById(R.id.homeMessageLayout);
        View closeView = view.findViewById(R.id.closeImageView);
        DailyTextView titleView = (DailyTextView) view.findViewById(R.id.titleTextView);
        DailyTextView descriptionView = (DailyTextView) view.findViewById(R.id.descriptionTextView);

        homeMessageLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.onMessageTextAreaClick();
            }
        });

        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMessageLayoutCloseAnimation(view);

                mOnEventListener.onMessageCloseClick();
            }
        });

        titleView.setText(mTextMessageTitle);

        String description = mTextMessageDescription;

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

        view.setVisibility(View.INVISIBLE);

        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                startMessageLayoutShowAnimation(view);
            }
        });
    }

    private void getMessageReviewLayout(final View view)
    {
        if (view == null)
        {
            return;
        }

        view.setVisibility(View.GONE);
        view.clearAnimation();

        if (mReview == null)
        {
            return;
        }

        View closeView = view.findViewById(R.id.closeImageView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startMessageLayoutCloseAnimation(view);
            }
        });

        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView periodTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        View goodEmoticonView = view.findViewById(R.id.goodEmoticonView);
        View badEmoticonView = view.findViewById(R.id.badEmoticonView);

        final ReviewItem reviewItem = mReview.getReviewItem();
        if (reviewItem == null)
        {
            view.setVisibility(View.GONE);
            view.clearAnimation();

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
        mDailyEmoticonImageView[0] = (DailyEmoticonImageView) view.findViewById(R.id.badEmoticonImageView);
        mDailyEmoticonImageView[1] = (DailyEmoticonImageView) view.findViewById(R.id.goodEmoticonImageView);

        mDailyEmoticonImageView[0].setJSONData("Review_Animation.aep.comp-737-A_not_satisfied.kf.json");
        mDailyEmoticonImageView[1].setJSONData("Review_Animation.aep.comp-573-B_satfisfied.kf.json");

        mDailyEmoticonImageView[0].startAnimation();
        mDailyEmoticonImageView[1].startAnimation();

        // 딤이미지
        final View badEmoticonDimView = view.findViewById(R.id.badEmoticonDimView);
        final View goodEmoticonDimView = view.findViewById(R.id.goodEmoticonDimView);

        // 텍스트
        final TextView badEmoticonTextView = (TextView) view.findViewById(R.id.badEmoticonTextView);
        final TextView goodEmoticonTextView = (TextView) view.findViewById(R.id.goodEmoticonTextView);

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

        view.setVisibility(View.INVISIBLE);

        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                startMessageLayoutShowAnimation(view);
            }
        });
    }

    private void getWishListLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        final HomeCarouselLayout wishListLayout = (HomeCarouselLayout) view;

        wishListLayout.postDelayed(new Runnable()
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
                wishListLayout.setData(placeList);
                // 임시 테스트 데이터 끝!
            }
        }, 5000);
    }

    public void getRecentListLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        final HomeCarouselLayout recentListLayout = (HomeCarouselLayout) view;

        recentListLayout.postDelayed(new Runnable()
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
                recentListLayout.setData(placeList);
                // 임시 테스트 데이터 끝!
            }
        }, 5000);
    }

    private void getRecommendListLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        final HomeRecommendLayout recommendLayout = (HomeRecommendLayout) view;

        recommendLayout.setListener(new HomeRecommendLayout.HomeRecommendListener()
        {
            @Override
            public void onRecommedClick(HomeRecommed recommed, int position)
            {
                // TODO : 추천 상세로 이동!!!
            }
        });

        // Test Data
        recommendLayout.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 임시 테스트 데이터
                ArrayList<HomeRecommed> recommendList = new ArrayList<>();
                Random random = new Random();
                int size = random.nextInt(8);
                for (int i = 0; i < size; i++)
                {
                    HomeRecommed homeRecommed = new HomeRecommed();

                    homeRecommed.title = "Recommend " + i;
                    homeRecommed.description = " Recommend description " + i;
                    homeRecommed.count = Math.abs(random.nextInt(11));

                    if (i % 3 == 0)
                    {
                        homeRecommed.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/01.jpg";
                    } else if (i % 3 == 1)
                    {
                        homeRecommed.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/02.jpg";
                    } else
                    {
                        homeRecommed.imageUrl = "https://img.dailyhotel.me/resources/images/dh_23351/03.jpg";
                    }
                    recommendList.add(homeRecommed);
                }
                recommendLayout.setData(recommendList, true);
                // 임시 테스트 데이터 끝!
            }
        }, 5000);
    }

    private void getTopButtonLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.onTopButtonClick();
            }
        });
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

                if (view.getId() == mHomeItemView[3].getId())
                {
                    onDestroyReviewAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                view.setVisibility(View.GONE);
                view.clearAnimation();
                if (view.getId() == mHomeItemView[3].getId())
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
        HomeCarouselLayout wishListLayout = (HomeCarouselLayout) mHomeItemView[4];
        HomeCarouselLayout recentListLayout = (HomeCarouselLayout) mHomeItemView[5];

        if (wishListLayout != null)
        {
            wishListLayout.startShimmer();
        }

        if (recentListLayout != null)
        {
            recentListLayout.startShimmer();
        }
    }

    public void onPauseCarouselAnimation()
    {
        HomeCarouselLayout wishListLayout = (HomeCarouselLayout) mHomeItemView[4];
        HomeCarouselLayout recentListLayout = (HomeCarouselLayout) mHomeItemView[5];

        if (wishListLayout != null)
        {
            wishListLayout.stopShimmer();
        }

        if (recentListLayout != null)
        {
            recentListLayout.stopShimmer();
        }
    }

}
