package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeLayout extends BaseLayout
{
    private DailyLoopViewPager mEventViewPager;
    private DailyTextView mEventCountTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mContentLayout;
    private HomeEventImageViewPagerAdapter mEventViewPagerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchImageClick();

        void onStayButtonClick();

        void onGourmetButtonClick();

        void onRefreshAll(boolean isShowProgress);
    }


    public HomeLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);
        initRefreshLayout(view);
        initScrollLayout(view);
        initContentLayout(view);

        initEventLayout(view);
        initProductLayout(view);
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


//        String defaultImageUrl = DailyPreference.getInstance(mContext).getRemoteConfigIntroImageNewUrl()
        setDefaultEventImage("");

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

    public void setDefaultEventImage(String url)
    {
        if (Util.isTextEmpty(url) == true)
        {
            setEventCountView(0, 0);
            return;
        }

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

//    private void loadDefaultEventImage(View eventLayout)
//    {
//        String splashVersion = DailyPreference.getInstance(mContext).getRemoteConfigHomeEventDefaultVersion();
//
//        DailyImageView imageView = (DailyImageView) eventLayout.findViewById(R.id.splashImageView);
//
//        if (Util.isTextEmpty(splashVersion) == true || Constants.DAILY_INTRO_DEFAULT_VERSION.equalsIgnoreCase(splashVersion) == true)
//        {
//            imageView.setVectorImageResource(R.drawable.img_splash_logo);
//        } else if (Constants.DAILY_INTRO_CURRENT_VERSION.equalsIgnoreCase(splashVersion) == true)
//        {
//            imageView.setPadding(0, 0, 0, 0);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(R.drawable.splash);
//        } else
//        {
//            String fileName = Util.makeIntroImageFileName(splashVersion);
//            File file = new File(getCacheDir(), fileName);
//
//            if (file.exists() == false)
//            {
//                DailyPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
//                imageView.setVectorImageResource(R.drawable.img_splash_logo);
//            } else
//            {
//                try
//                {
//                    imageView.setPadding(0, 0, 0, 0);
//                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                    imageView.setImageURI(Uri.fromFile(file));
//                } catch (Exception e)
//                {
//                    DailyPreference.getInstance(this).setRemoteConfigIntroImageVersion(Constants.DAILY_INTRO_DEFAULT_VERSION);
//                    imageView.setPadding(0, 0, 0, Util.dpToPx(this, 26));
//                    imageView.setScaleType(ImageView.ScaleType.CENTER);
//                    imageView.setVectorImageResource(R.drawable.img_splash_logo);
//                }
//            }
//        }
//    }
}
