package com.twoheart.dailyhotel.screen.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyPlaceDetailListView;
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
    private HomeEventImageViewPagerAdapter mEventViewPagerAdapter;
    private DailyPlaceDetailListView mListView;
    private HomeListAdapter mListAdapter;

    private int mEventImageHeight;
    private int mStatusBarHeight;

    private View mActionButtonLayout;

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
        initEventLayout(view);
        initRefreshLayout(view);
        initScrollLayout(view);
        setStatusBarHeight(mContext);
        initActionButtonLayout(view);

        //        initMessageLayout(view);
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

    private void initEventLayout(View view)
    {
        if (mContext == null)
        {
            return;
        }

        mEventViewPager = (DailyLoopViewPager) view.findViewById(R.id.loopViewPager);
        mEventCountTextView = (DailyTextView) view.findViewById(R.id.pagerCountTextView);

        ViewGroup.LayoutParams params = mEventViewPager.getLayoutParams();
        params.height = getEventImageHeight(mContext);
        mEventViewPager.setLayoutParams(params);

        setDefaultEventImage();
    }

    private void initScrollLayout(View view)
    {
        mListView = (DailyPlaceDetailListView) view.findViewById(R.id.placeListView);
        mListAdapter = new HomeListAdapter(mContext, getEventImageHeight(mContext), (OnEventListener) mOnEventListener, mEmptyViewOnTouchListener);
        mListView.setOnScrollListener(mOnScrollListener);
        mListView.setAdapter(mListAdapter);
        //        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    // 홈의 상단 고정 버튼 레이아웃
    private void initActionButtonLayout(View view)
    {
        mActionButtonLayout = view.findViewById(R.id.productLayout);
        mActionButtonLayout.setVisibility(View.GONE);

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

    private int getEventImageHeight(Context context)
    {
        if (mEventImageHeight == 0)
        {
            mEventImageHeight = Util.getListRowHeight(context);
        }

        return mEventImageHeight;
    }

    public void setTextMessageData(String title, String description)
    {
        if (mListAdapter != null)
        {
            mListAdapter.setTextMessageData(title, description);
            mListAdapter.setReviewMessageData(null);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public void setReviewData(Review review)
    {
        if (mListAdapter != null)
        {
            mListAdapter.setTextMessageData(null, null);
            mListAdapter.setReviewMessageData(review);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public void setActionButtonVisibility(int visibility)
    {
        mActionButtonLayout.setVisibility(visibility);
    }

    //    private void initMessageLayout(View view)
    //    {
    //        if (mContentLayout == null || mContext == null)
    //        {
    //            return;
    //        }
    //
    //        MessageType messageType = MessageType.NONE;
    //
    //        if (DailyHotel.isLogin() == true)
    //        {
    //            boolean isLoginAreaEnable = DailyPreference.getInstance(mContext).isRemoteConfigHomeMessageAreaLoginEnabled();
    //            if (isLoginAreaEnable == true)
    //            {
    //                messageType = MessageType.REVIEW;
    //            }
    //        } else
    //        {
    //            boolean isLogoutAreaEnable = DailyPreference.getInstance(mContext).isRemoteConfigHomeMessageAreaLogoutEnabled();
    //            if (isLogoutAreaEnable == true)
    //            {
    //                messageType = MessageType.TEXT;
    //            }
    //        }
    //
    //        if (MessageType.REVIEW == messageType)
    //        {
    //            // init review layout
    //            initMessageReviewLayout(view);
    //        } else if (MessageType.TEXT == messageType)
    //        {
    //            // init text layout
    //            initNSetMessageTextLayout(view);
    //        } else
    //        {
    //            // gone message layout
    //            if (mMessageLayout != null)
    //            {
    //                mMessageLayout.setVisibility(View.GONE);
    //            }
    //        }
    //    }

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
        if (mListView != null || mListView.getChildCount() == 0)
        {
            mListView.smoothScrollBy(0, 0);
            mListView.setSelection(0);
        }
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

    public View getProductLayout()
    {
        if (mListAdapter == null)
        {
            return null;
        }

        return mListAdapter.getProductLayout();
    }

    public void setStatusBarHeight(Context context)
    {
        if (context == null)
        {
            return;
        }

        Activity activity = (Activity) context;
        final Window window = activity.getWindow();

        mListView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);

                mStatusBarHeight = rect.top;
            }
        });
    }

    public void onResumeReviewAnimation()
    {
        if (mListAdapter != null)
        {
            mListAdapter.onResumeReviewAnimation();
        }
    }

    public void onPauseReviewAnimation()
    {
        if (mListAdapter != null)
        {
            mListAdapter.onPauseReviewAnimation();
        }
    }

    public void onDestroyReviewAnimation()
    {
        if (mListAdapter != null)
        {
            mListAdapter.onDestroyReviewAnimation();
        }
    }

    public void onResumeCarouselAnimation()
    {
        if (mListAdapter != null)
        {
            mListAdapter.onResumeCarouselAnimation();
        }
    }

    public void onPauseCarouselAnimation()
    {
        if (mListAdapter != null)
        {
            mListAdapter.onPauseCarouselAnimation();
        }
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener()
    {
        private Rect mProductLayoutRect = new Rect();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            // do nothing!
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (view.getAdapter() == null)
            {
                return;
            }

            if (firstVisibleItem > 1)
            {
                // ????? - 발생가능 ???? 발생하면 초기화 ???
                return;
            }

            if (mStatusBarHeight == 0)
            {
                return;
            }

            View productLayout = getProductLayout();
            if (productLayout == null)
            {
                return;
            }

            final int TOOLBAR_HEIGHT = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);

            productLayout.getGlobalVisibleRect(mProductLayoutRect);
            int globalTop = mProductLayoutRect.top;
            int globalRight = mProductLayoutRect.right;

            productLayout.getLocalVisibleRect(mProductLayoutRect);
            int localTop = mProductLayoutRect.top;

            if (globalTop == globalRight)
            {
                // do nothing!
            } else
            {
                ExLog.d(globalTop + " , " + localTop);
                if (globalTop <= (mStatusBarHeight + TOOLBAR_HEIGHT) || localTop > 0)
                {
                    // show
                    ExLog.d("Show");
                    setActionButtonVisibility(View.VISIBLE);
                } else
                {
                    // hide
                    ExLog.d("Hide");
                    setActionButtonVisibility(View.GONE);
                }
            }
        }
    };

    protected View.OnTouchListener mEmptyViewOnTouchListener = new View.OnTouchListener()
    {
        private int mMoveState;
        private float mPrevX, mPrevY;

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction() & MotionEventCompat.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                {
                    mPrevX = event.getX();
                    mPrevY = event.getY();

                    mMoveState = 0;
                    mListView.setScrollEnabled(false);

                    try
                    {
                        mEventViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                {
                    int touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

                    int x = (int) (mPrevX - event.getX());
                    int y = (int) (mPrevY - event.getY());

                    int distance = (int) Math.sqrt(x * x + y * y);

                    if (distance < touchSlop)
                    {
                        // TODO : 이벤트 아이템 클릭 이벤트 처리 추가 필요!
                        //                        ((PlaceDetailLayout.OnEventListener) mOnEventListener).onClickImage(mPlaceDetail);

                        mMoveState = 0;

                        try
                        {
                            mEventViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mEventViewPager.getScrollX(), mEventViewPager.getScrollY());
                            mEventViewPager.onTouchEvent(event);
                        }

                        mListView.setScrollEnabled(true);
                        break;
                    }
                }
                case MotionEvent.ACTION_CANCEL:
                {
                    mMoveState = 0;

                    try
                    {
                        mEventViewPager.onTouchEvent(event);
                    } catch (Exception e)
                    {
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        event.setLocation(mEventViewPager.getScrollX(), mEventViewPager.getScrollY());
                        mEventViewPager.onTouchEvent(event);
                    }

                    mListView.setScrollEnabled(true);
                    break;
                }

                case MotionEvent.ACTION_MOVE:
                {
                    float x = event.getX();
                    float y = event.getY();

                    if (mMoveState == 0)
                    {
                        if (Math.abs(x - mPrevX) == Math.abs(y - mPrevY))
                        {

                        } else if (Math.abs(x - mPrevX) > Math.abs(y - mPrevY))
                        {
                            // x 축으로 이동한 경우.
                            mMoveState = 100;

                            try
                            {
                                mEventViewPager.onTouchEvent(event);
                            } catch (Exception e)
                            {
                                event.setAction(MotionEvent.ACTION_CANCEL);
                                event.setLocation(mEventViewPager.getScrollX(), mEventViewPager.getScrollY());
                                mEventViewPager.onTouchEvent(event);
                            }
                        } else
                        {
                            // y축으로 이동한 경우.
                            mMoveState = 10;
                            mListView.setScrollEnabled(true);
                            return true;
                        }
                    } else if (mMoveState == 100)
                    {
                        try
                        {
                            mEventViewPager.onTouchEvent(event);
                        } catch (Exception e)
                        {
                            event.setAction(MotionEvent.ACTION_CANCEL);
                            event.setLocation(mEventViewPager.getScrollX(), mEventViewPager.getScrollY());
                            mEventViewPager.onTouchEvent(event);
                        }
                    }
                    break;
                }
            }

            return false;
        }
    };
}
