package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.StaticLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceReviewItem;
import com.twoheart.dailyhotel.network.model.PlaceReview;
import com.twoheart.dailyhotel.network.model.PlaceReviewProgress;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceReviewLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private ReviewListAdapter mReviewListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();
    }

    public PlaceReviewLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);


    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view, mContext.getString(R.string.label_truereview));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    private void initToolbar(View view, String title)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        }, false);
    }

    public void setReviewList(List<PlaceReviewProgress> placeReviewProgressList, List<PlaceReview> placeReviewList)
    {
        if (placeReviewProgressList == null || placeReviewProgressList.size() == 0 || placeReviewList == null || placeReviewList.size() == 0)
        {
            return;
        }

        if (mReviewListAdapter == null)
        {
            mReviewListAdapter = new ReviewListAdapter(mContext);
            mRecyclerView.setAdapter(mReviewListAdapter);
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();

        placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_HEADER_VIEW, placeReviewProgressList));

        for (PlaceReview placeReview : placeReviewList)
        {
            placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_ENTRY, placeReview));
        }

        mReviewListAdapter.setAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void addReviewList(List<PlaceReview> placeReviewList)
    {
        if (mReviewListAdapter == null || placeReviewList == null || placeReviewList.size() == 0)
        {
            return;
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();

        for (PlaceReview placeReview : placeReviewList)
        {
            placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_ENTRY, placeReview));
        }

        mReviewListAdapter.addAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private boolean mShowtProgressbarAnimation;
        private Context mContext;
        private LayoutInflater mInflater;
        private List<PlaceReviewItem> mPlaceReviewItemList;

        public ReviewListAdapter(Context context)
        {
            mContext = context;

            mPlaceReviewItemList = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public PlaceReviewItem getItem(int position)
        {
            if (position < 0 || mPlaceReviewItemList.size() <= position)
            {
                return null;
            }

            return mPlaceReviewItemList.get(position);
        }

        public void setAll(List<PlaceReviewItem> placeReviewItemList)
        {
            clear();
            addAll(placeReviewItemList);

            mShowtProgressbarAnimation = false;
        }

        public void addAll(List<PlaceReviewItem> placeReviewItemList)
        {
            if (placeReviewItemList == null || placeReviewItemList.size() == 0)
            {
                return;
            }

            mPlaceReviewItemList.addAll(placeReviewItemList);
        }

        public void clear()
        {
            mPlaceReviewItemList.clear();
        }

        @Override
        public int getItemViewType(int position)
        {
            return mPlaceReviewItemList.get(position).mType;
        }

        @Override
        public int getItemCount()
        {
            if (mPlaceReviewItemList == null)
            {
                return 0;
            }

            return mPlaceReviewItemList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case PlaceReviewItem.TYPE_HEADER_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_place_review_header, parent, false);

                    return new HeaderViewHolder(view);
                }

                case PlaceReviewItem.TYPE_ENTRY:
                {
                    View view = mInflater.inflate(R.layout.list_row_place_review, parent, false);

                    return new ReviewViewHolder(view);
                }

                case PlaceReviewItem.TYPE_FOOTER_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_product_footer, parent, false);

                    return new FooterViewHolder(view);
                }
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            PlaceReviewItem placeReviewItem = getItem(position);

            if (placeReviewItem == null)
            {
                return;
            }

            switch (placeReviewItem.mType)
            {
                case PlaceReviewItem.TYPE_HEADER_VIEW:
                    onBindViewHolder((HeaderViewHolder) holder, position, placeReviewItem);
                    break;

                case PlaceReviewItem.TYPE_ENTRY:
                    onBindViewHolder((ReviewViewHolder) holder, position, placeReviewItem);
                    break;

                case PlaceReviewItem.TYPE_FOOTER_VIEW:
                    break;
            }
        }

        private void onBindViewHolder(final HeaderViewHolder headerViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            List<PlaceReviewProgress> placeReviewProgressList = placeViewItem.getItem();

            headerViewHolder.termsView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((OnEventListener) mOnEventListener).onTermsClick();
                }
            });

            for (PlaceReviewProgress placeReviewProgress : placeReviewProgressList)
            {
                View view = LayoutInflater.from(mContext).inflate(R.layout.view_progress_layout, headerViewHolder.progressBarLayout, false);

                TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                TextView valueTextView = (TextView) view.findViewById(R.id.valueTextView);

                titleTextView.setText(placeReviewProgress.name);

                if (mShowtProgressbarAnimation == true)
                {
                    progressBar.setProgress(placeReviewProgress.value);
                } else
                {
                    progressBar.setProgress(0);
                }

                progressBar.setTag(placeReviewProgress.value);
                valueTextView.setText(Float.toString(((float) placeReviewProgress.value) / 10.0f));

                headerViewHolder.progressBarLayout.addView(view);
            }

            headerViewHolder.reviewCountTextView.setText(mContext.getString(R.string.label_detail_review_count, getItemCount() - 1));

            if(mShowtProgressbarAnimation == false)
            {
                mShowtProgressbarAnimation = true;

                headerViewHolder.progressBarLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startAnimation(headerViewHolder.progressBarLayout);
                    }
                });
            }
        }

        private void onBindViewHolder(final ReviewViewHolder reviewViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            final int MAXLINE = 11;
            PlaceReview placeReview = placeViewItem.getItem();

            reviewViewHolder.emailTextView.setText(placeReview.email);

            try
            {
                reviewViewHolder.dateTextView.setText(DailyCalendar.convertDateFormatString(placeReview.date, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            reviewViewHolder.reviewTextView.setText(placeReview.message);
            reviewViewHolder.reviewTextView.setTag(placeReview.message);

            Paint paint = reviewViewHolder.reviewTextView.getPaint();

            int textViewWidth = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 30);
            int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY);
            int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            reviewViewHolder.reviewTextView.measure(widthMeasureSpec, heightMeasureSpec);

            int lineCount = reviewViewHolder.reviewTextView.getLineCount();

            if (lineCount > MAXLINE)
            {
                final String expandText = "...  더 읽어보기";

                StaticLayout layout = (StaticLayout) reviewViewHolder.reviewTextView.getLayout();
                int lineStartIndex = reviewViewHolder.reviewTextView.getLayout().getLineStart(MAXLINE - 1);
                int lineEndIndex = reviewViewHolder.reviewTextView.getLayout().getLineEnd(MAXLINE - 1);

                CharSequence text = reviewViewHolder.reviewTextView.getText().subSequence(lineStartIndex, lineEndIndex);

                int length = text.length();
                float moreReadWidth = paint.measureText(expandText);

                int count = 0, readCount = 0;

                for (int i = length - 1; i >= 0; i--)
                {
                    readCount = paint.breakText(text, i, length, false, moreReadWidth, null);

                    if (readCount != count)
                    {
                        count = readCount;
                    } else
                    {
                        break;
                    }
                }

                reviewViewHolder.reviewTextView.setText(reviewViewHolder.reviewTextView.getText().subSequence(0, lineEndIndex - count - 1) + "...");
                reviewViewHolder.moreReadTextView.setVisibility(View.VISIBLE);
                reviewViewHolder.moreReadTextView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        reviewViewHolder.reviewTextView.setText((String) reviewViewHolder.reviewTextView.getTag());
                        reviewViewHolder.moreReadTextView.setVisibility(View.GONE);
                        reviewViewHolder.moreReadTextView.setOnClickListener(null);
                    }
                });
            } else
            {
                reviewViewHolder.moreReadTextView.setVisibility(View.GONE);
                reviewViewHolder.moreReadTextView.setOnClickListener(null);
            }
        }

        private void startAnimation(ViewGroup viewGroup)
        {
            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                final ProgressBar progressBar = (ProgressBar) viewGroup.getChildAt(i).findViewById(R.id.progressBar);
                final int value = (int) progressBar.getTag();
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, value);
                valueAnimator.setDuration(300 * value / 50);
                valueAnimator.setStartDelay(200 + i * 100);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        if (animation.getAnimatedValue() == null)
                        {
                            return;
                        }

                        progressBar.setProgress((int) animation.getAnimatedValue());
                    }
                });

                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        progressBar.setProgress(value);
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();
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
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder
        {
            View termsView;
            LinearLayout progressBarLayout;
            TextView reviewCountTextView;

            public HeaderViewHolder(View view)
            {
                super(view);

                termsView = itemView.findViewById(R.id.termsView);
                progressBarLayout = (LinearLayout) itemView.findViewById(R.id.progressBarLayout);
                reviewCountTextView = (TextView) itemView.findViewById(R.id.reviewCountTextView);
            }
        }

        private class ReviewViewHolder extends RecyclerView.ViewHolder
        {
            TextView emailTextView;
            TextView dateTextView;
            TextView reviewTextView;
            TextView moreReadTextView;

            public ReviewViewHolder(View view)
            {
                super(view);

                emailTextView = (TextView) view.findViewById(R.id.emailTextView);
                dateTextView = (TextView) view.findViewById(R.id.dateTextView);
                reviewTextView = (TextView) view.findViewById(R.id.reviewTextView);
                moreReadTextView = (TextView) view.findViewById(R.id.moreReadTextView);
            }
        }

        private class FooterViewHolder extends RecyclerView.ViewHolder
        {
            public FooterViewHolder(View view)
            {
                super(view);
            }
        }
    }

}