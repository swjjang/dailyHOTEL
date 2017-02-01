package com.twoheart.dailyhotel.screen.home.collection;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.place.adapter.PlaceListAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;

public abstract class CollectionBaseLayout extends BaseLayout
{
    PinnedSectionRecyclerView mRecyclerView;
    PlaceListAdapter mPlaceListAdapter;
    private TextView mCalendarTextView;
    private TextView mTitleTextView, mSubTitleTextView;
    private SimpleDraweeView mSimpleDraweeView;
    private int mTitleLayoutHeight;
    private int mScrollState;

    protected abstract PlaceListAdapter getPlaceListAdapter(View.OnClickListener listener);

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCalendarClick();

        void onPlaceClick(View view, PlaceViewItem placeViewItem, int count);
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
        int height16x9 = Util.getRatioHeightType16x9(Util.getLCDWidth(mContext));

        // 이미지
        mSimpleDraweeView = (SimpleDraweeView) imageViewLayout.findViewById(R.id.simpleDraweeView);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height16x9);
        mSimpleDraweeView.setLayoutParams(layoutParams);

        final FrameLayout titleBoxLayout = (FrameLayout) view.findViewById(R.id.titleBoxLayout);
        titleBoxLayout.setPadding(Util.dpToPx(mContext, 15), Util.dpToPx(mContext, 171), Util.dpToPx(mContext, 15), 0);

        final View fakeBackImageView = titleBoxLayout.findViewById(R.id.fakeBackImageView);
        final View subTitleLayout = view.findViewById(R.id.subTitleLayout);

        mTitleTextView = (TextView) titleBoxLayout.findViewById(R.id.titleTextView);
        mSubTitleTextView = (TextView) subTitleLayout.findViewById(R.id.subTitleTextView);

        mCalendarTextView = (TextView) subTitleLayout.findViewById(R.id.calendarTextView);
        mCalendarTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onCalendarClick();
            }
        });

        // 리스트
        mRecyclerView = (PinnedSectionRecyclerView) view.findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new ScrollingLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setShadowVisible(false);

        if (mPlaceListAdapter == null)
        {
            mPlaceListAdapter = getPlaceListAdapter(mOnItemClickListener);
        }

        mRecyclerView.setAdapter(mPlaceListAdapter);

        titleBoxLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mTitleLayoutHeight = titleBoxLayout.getHeight() - titleBoxLayout.getPaddingTop() - titleBoxLayout.getPaddingBottom();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private final int dp15Height = Util.dpToPx(mContext, 15);
            private final int dp3Height = Util.dpToPx(mContext, 3);
            private final int dp20Height = Util.dpToPx(mContext, 20);
            private final int dp21Height = Util.dpToPx(mContext, 21);
            private final int dp41Height = Util.dpToPx(mContext, 41);
            private final int dp30Height = Util.dpToPx(mContext, 30);
            private final int dp44Height = Util.dpToPx(mContext, 44);
            private final int dp171Height = Util.dpToPx(mContext, 171);

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

                        float titleLayoutTopPaddingValue = dp171Height - (firstView.getHeight() - startAnimationHeight) * (1.0f - titleLayoutValue);
                        titleBoxLayout.setPadding(dp15Height, (int) titleLayoutTopPaddingValue, dp15Height, dp15Height);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -dp171Height;
                        fakeBackImageLayoutParams.leftMargin = -dp15Height;

                        mTitleTextView.setTranslationX(0);
                        mTitleTextView.setTranslationY(0);

                        subTitleLayout.setPadding(0, dp41Height, 0, 0);
                        mSubTitleTextView.setAlpha(1.0f);

                        titleBoxLayout.setTranslationY(-dp21Height);
                    } else
                    {
                        final float titleLayoutValue = (float) firstView.getBottom() / startAnimationHeight;
                        final float titleLayoutTopPaddingValue = (dp171Height - (firstView.getHeight() - startAnimationHeight)) * titleLayoutValue;
                        final int titleLayoutPaddingValue = (int) (titleLayoutValue * dp15Height);

                        titleBoxLayout.setPadding(titleLayoutPaddingValue, (int) titleLayoutTopPaddingValue, titleLayoutPaddingValue, 0);
                        titleBoxLayout.setTranslationY(-dp21Height * titleLayoutValue);

                        FrameLayout.LayoutParams fakeBackImageLayoutParams = (FrameLayout.LayoutParams) fakeBackImageView.getLayoutParams();
                        fakeBackImageLayoutParams.topMargin = -titleBoxLayout.getPaddingTop() - (int) titleBoxLayout.getTranslationY();
                        fakeBackImageLayoutParams.leftMargin = -titleLayoutPaddingValue;

                        subTitleLayout.setPadding(0, dp20Height + (int) (dp21Height * titleLayoutValue), 0, 0);

                        if (titleBoxLayout.getPaddingTop() < 5)
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundColor(mContext.getResources().getColor(R.color.white));
                        } else
                        {
                            titleBoxLayout.getChildAt(0).setBackgroundResource(R.drawable.home_pick_bg);
                        }

                        int titleTranslationX = fakeBackImageView.getRight() - mTitleTextView.getLeft();
                        mTitleTextView.setTranslationX(titleTranslationX * (1.0f - titleLayoutValue));
                        mTitleTextView.setTranslationY(-dp3Height * (1.0f - titleLayoutValue));

                        float alphaValue = titleLayoutValue / 4;
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

    protected void setTitleLayout(String title, String subTitle, String imageUrl)
    {
        mTitleTextView.setText(title);
        mSubTitleTextView.setText(subTitle);
        Util.requestImageResize(mContext, mSimpleDraweeView, imageUrl);
    }

    protected void setCalendarText(String date)
    {
        if (mCalendarTextView == null)
        {
            return;
        }

        mCalendarTextView.setText(date);
    }

    protected void setData(ArrayList<PlaceViewItem> placeViewItems)
    {
        mPlaceListAdapter.setAll(placeViewItems);
        mPlaceListAdapter.notifyDataSetChanged();
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
                ((OnEventListener) mOnEventListener).onPlaceClick(view, placeViewItem, mPlaceListAdapter.getItemCount());
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