package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

public abstract class CollectionBaseLayout extends BaseBlurLayout
{
    RecyclerView mRecyclerView;
    PlaceListAdapter mPlaceListAdapter;
    private TextView mCalendarTextView;
    TextView mTitleTextView, mSubTitleTextView;
    private SimpleDraweeView mSimpleDraweeView;
    int mTitleLayoutHeight;
    int mTitleLayoutTopPaddingHeight;
    int mScrollState;
    boolean mIsUsedMultiTransition;

    private Handler mHandler = new Handler();

    protected abstract PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener);

    protected abstract void setUsedMultiTransition(boolean isUsedMultiTransition);

    protected abstract void notifyWishChanged(int position, boolean wish);

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onPlaceClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onPlaceLongClick(int position, View view, PlaceViewItem placeViewItem, int count);

        void onWishClick(int position, PlaceViewItem placeViewItem);
    }

    public CollectionBaseLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        // 백이미지
        final View backImageView = view.findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        final View imageViewLayout = view.findViewById(R.id.imageViewLayout);

        // 16:9 높이
        int height16x9 = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext));

        // 이미지
        mSimpleDraweeView = imageViewLayout.findViewById(R.id.simpleDraweeView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height16x9);
        mSimpleDraweeView.setLayoutParams(layoutParams);

        final FrameLayout titleBoxLayout = view.findViewById(R.id.titleBoxLayout);
        final int DP_15 = ScreenUtils.dpToPx(mContext, 15);
        int titleBoxTopPadding = ScreenUtils.getRatioHeightType16x9(ScreenUtils.getScreenWidth(mContext)) + ScreenUtils.dpToPx(mContext, 81) - DP_15 - ScreenUtils.dpToPx(mContext, 118);
        titleBoxLayout.setPadding(DP_15, titleBoxTopPadding + ScreenUtils.dpToPx(mContext, 21), DP_15, DP_15);

        final View fakeBackImageView = titleBoxLayout.findViewById(R.id.fakeBackImageView);
        final View subTitleLayout = view.findViewById(R.id.subTitleLayout);

        mTitleTextView = titleBoxLayout.findViewById(R.id.titleTextView);
        mSubTitleTextView = subTitleLayout.findViewById(R.id.subTitleTextView);

        mCalendarTextView = subTitleLayout.findViewById(R.id.calendarTextView);
        mCalendarTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onCalendarClick();
            }
        });

        // 리스트
        mRecyclerView = view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new ScrollingLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        //        mRecyclerView.setShadowVisible(false);

        if (mPlaceListAdapter == null)
        {
            mPlaceListAdapter = getPlaceListAdapter(mOnItemClickListener);
        }

        if (DailyPreference.getInstance(mContext).getTrueVRSupport() > 0)
        {
            mPlaceListAdapter.setTrueVREnabled(true);
        }

        if (Util.supportPreview(mContext) == true)
        {
            mPlaceListAdapter.setOnLongClickListener(mOnItemLongClickListener);
        }

        mPlaceListAdapter.setOnWishClickListener(mOnWishClickListener);

        mRecyclerView.setAdapter(mPlaceListAdapter);

        titleBoxLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTitleLayoutTopPaddingHeight = titleBoxLayout.getPaddingTop();
                mTitleLayoutHeight = titleBoxLayout.getHeight() - titleBoxLayout.getPaddingTop() - titleBoxLayout.getPaddingBottom();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private final int dp15Height = ScreenUtils.dpToPx(mContext, 15);
            private final int dp3Height = ScreenUtils.dpToPx(mContext, 3);
            private final int dp20Height = ScreenUtils.dpToPx(mContext, 20);
            private final int dp21Height = ScreenUtils.dpToPx(mContext, 21);
            private final int dp42Height = ScreenUtils.dpToPx(mContext, 42);
            private final int dp30Height = ScreenUtils.dpToPx(mContext, 30);
            private final int dp44Height = ScreenUtils.dpToPx(mContext, 44);

            private int mDragDistance;
            private boolean mIsFirstCollapse;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int position = linearLayoutManager.findFirstVisibleItemPosition();

                switch (mScrollState)
                {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mDragDistance += dy;
                        break;
                }

                if (position == 0)
                {
                    mIsFirstCollapse = false;

                    View firstView = linearLayoutManager.findViewByPosition(position);
                    final float value = (float) firstView.getBottom() / firstView.getHeight();

                    imageViewLayout.setTranslationY(-imageViewLayout.getHeight() * (1.0f - value) / 2);

                    // 높이 조절 / 폭 조절
                    final int startAnimationHeight = mTitleLayoutHeight - dp44Height + dp15Height;

                    if (firstView.getBottom() >= startAnimationHeight)
                    {
                        float titleLayoutValue = (float) (firstView.getBottom() - startAnimationHeight) / (firstView.getHeight() - startAnimationHeight);

                        float titleLayoutTopPaddingValue = mTitleLayoutTopPaddingHeight - (firstView.getHeight() - startAnimationHeight) * (1.0f - titleLayoutValue);
                        titleBoxLayout.setPadding(dp15Height, (int) titleLayoutTopPaddingValue, dp15Height, dp15Height);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -mTitleLayoutTopPaddingHeight;
                        fakeBackImageLayoutParams.leftMargin = -dp15Height;

                        mTitleTextView.setTranslationX(0);
                        mTitleTextView.setTranslationY(0);

                        subTitleLayout.setPadding(0, dp42Height, 0, 0);
                        mSubTitleTextView.setAlpha(1.0f);

                        titleBoxLayout.setTranslationY(-dp21Height);
                    } else
                    {
                        final float titleLayoutValue = (float) firstView.getBottom() / startAnimationHeight;
                        final float titleLayoutTopPaddingValue = (mTitleLayoutTopPaddingHeight - (firstView.getHeight() - startAnimationHeight)) * titleLayoutValue;
                        final int titleLayoutPaddingValue = (int) (titleLayoutValue * dp15Height);

                        titleBoxLayout.setPadding(titleLayoutPaddingValue, (int) titleLayoutTopPaddingValue, titleLayoutPaddingValue, titleLayoutPaddingValue);
                        titleBoxLayout.setTranslationY(-dp21Height * titleLayoutValue);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -titleBoxLayout.getPaddingTop() - (int) titleBoxLayout.getTranslationY();
                        fakeBackImageLayoutParams.leftMargin = -titleLayoutPaddingValue;

                        subTitleLayout.setPadding(0, dp20Height + (int) (dp21Height * titleLayoutValue), 0, 0);

                        if (titleBoxLayout.getPaddingTop() < 6)
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        } else
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundResource(R.drawable.home_pick_bg);
                        }

                        int titleTranslationX = fakeBackImageView.getRight() - mTitleTextView.getLeft();
                        mTitleTextView.setTranslationX(titleTranslationX * (1.0f - titleLayoutValue));
                        mTitleTextView.setTranslationY(-dp3Height * (1.0f - titleLayoutValue));

                        float alphaValue = titleLayoutValue / 5;
                        if (alphaValue > 1.0f)
                        {
                            alphaValue = 1.0f;
                        } else if (alphaValue < 0.0f)
                        {
                            alphaValue = 0.0f;
                        }
                        mSubTitleTextView.setAlpha(alphaValue);
                    }
                } else
                {
                    setDefaultCollapseValue();
                }
            }

            private void setDefaultCollapseValue()
            {
                if (mIsFirstCollapse == true)
                {
                    return;
                }

                mIsFirstCollapse = true;

                titleBoxLayout.setPadding(0, 0, 0, 0);
                titleBoxLayout.setTranslationY(0);
                titleBoxLayout.getChildAt(0).setBackgroundColor(mContext.getResources().getColor(R.color.white));

                FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                fakeBackImageLayoutParams.topMargin = 0;
                fakeBackImageLayoutParams.leftMargin = 0;

                mTitleTextView.setTranslationX(fakeBackImageView.getRight() - mTitleTextView.getLeft());
                mTitleTextView.setTranslationY(-dp3Height);

                mSubTitleTextView.setAlpha(0.0f);
                subTitleLayout.setPadding(0, dp20Height, 0, 0);

                imageViewLayout.setTranslationY(-imageViewLayout.getHeight() / 2);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                mScrollState = newState;

                int position = linearLayoutManager.findFirstVisibleItemPosition();

                if (position == 0)
                {
                    switch (newState)
                    {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (mDragDistance > 0)
                            {
                                if (mDragDistance > dp30Height)
                                {
                                    recyclerView.smoothScrollToPosition(1);
                                } else
                                {
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            } else if (mDragDistance < 0)
                            {
                                if (Math.abs(mDragDistance) < dp30Height)
                                {
                                    recyclerView.smoothScrollToPosition(1);
                                } else
                                {
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }

                            mDragDistance = 0;
                            break;

                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            break;

                        case RecyclerView.SCROLL_STATE_SETTLING:
                            break;
                    }
                } else
                {
                    switch (newState)
                    {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            if (position == 1)
                            {
                                setDefaultCollapseValue();
                            }

                        case RecyclerView.SCROLL_STATE_DRAGGING:
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            mDragDistance = 0;
                            break;
                    }
                }
            }
        });
    }

    private String mTitleText;
    private String mSubTitleText;
    private String mImageUrl;

    protected void setTitleLayoutData(String title, String subTitle, String imageUrl)
    {
        mTitleText = title;
        mSubTitleText = subTitle;
        mImageUrl = imageUrl;
    }

    protected void notifyChangedTitleLayout() {
        mTitleTextView.setText(mTitleText);
        mSubTitleTextView.setText(mSubTitleText);

        mSimpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
        Util.requestImageResize(mContext, mSimpleDraweeView, mImageUrl);
    }

    protected void setCalendarText(String date)
    {
        if (mCalendarTextView == null)
        {
            return;
        }

        mCalendarTextView.setText(date);
    }

    public void setListScrollTop()
    {
        if (mRecyclerView == null || mRecyclerView.getChildCount() == 0)
        {
            return;
        }

        mRecyclerView.scrollToPosition(0);
    }

    public PlaceViewItem getItem(int position)
    {
        if (mRecyclerView == null || mPlaceListAdapter == null)
        {
            return null;
        }

        return mPlaceListAdapter.getItem(position);
    }

    protected void setData(ArrayList<PlaceViewItem> placeViewItems, PlaceBookingDay placeBookingDay, boolean rewardEnabled)
    {
        mPlaceListAdapter.setPlaceBookingDay(placeBookingDay);
        mPlaceListAdapter.setRewardEnabled(rewardEnabled);
        mPlaceListAdapter.setAll(placeViewItems);

        mRecyclerView.setAdapter(mPlaceListAdapter);
        //        postAndNotifyAdapter(mRecyclerView, mPlaceListAdapter);
    }

    void postAndNotifyAdapter(final RecyclerView recyclerView, final RecyclerView.Adapter adapter)
    {
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (recyclerView.isComputingLayout() == false)
                {
                    adapter.notifyDataSetChanged();
                } else
                {
                    postAndNotifyAdapter(recyclerView, adapter);
                }
            }
        }, 50);
    }

    private View.OnClickListener mOnItemClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceClick(position, view, placeViewItem, mPlaceListAdapter.getItemCount());
            }
        }
    };

    private View.OnLongClickListener mOnItemLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return false;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onPlaceLongClick(position, view, placeViewItem, mPlaceListAdapter.getItemCount());
            }

            return true;
        }
    };

    private View.OnClickListener mOnWishClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position < 0)
            {
                return;
            }

            PlaceViewItem placeViewItem = mPlaceListAdapter.getItem(position);

            if (placeViewItem.mType == PlaceViewItem.TYPE_ENTRY)
            {
                ((OnEventListener) mOnEventListener).onWishClick(position, placeViewItem);
            }
        }
    };

    private class ScrollingLinearLayoutManager extends LinearLayoutManager
    {
        public ScrollingLinearLayoutManager(Context context, int orientation, boolean reverseLayout)
        {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
        {
            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext())
            {
                @Override
                public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference)
                {
                    switch (snapPreference)
                    {
                        case SNAP_TO_START:
                            return boxStart - viewStart;
                        case SNAP_TO_END:
                            return boxEnd - viewEnd;
                        case SNAP_TO_ANY:
                            final int dtStart = boxStart - viewStart;
                            if (dtStart > 0)
                            {
                                return dtStart;
                            } else if (dtStart < 0)
                            {
                                return dtStart;
                            }

                            final int dtEnd = boxEnd - viewEnd;
                            if (dtEnd < 0)
                            {
                                return dtEnd;
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("snap preference should be one of the" + " constants defined in SmoothScroller, starting with SNAP_");
                    }
                    return 0;
                }
            };

            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }
}