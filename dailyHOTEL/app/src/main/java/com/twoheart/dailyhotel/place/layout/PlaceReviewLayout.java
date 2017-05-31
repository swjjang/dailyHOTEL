package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceReviewItem;
import com.twoheart.dailyhotel.network.model.PlaceReview;
import com.twoheart.dailyhotel.network.model.PlaceReviewScore;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class PlaceReviewLayout extends BaseLayout
{
    private RecyclerView mRecyclerView;
    private ReviewListAdapter mReviewListAdapter;
    private View mTopButton;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();

        void onTopClick();

        void onScroll(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
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

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                ((OnEventListener) mOnEventListener).onScroll(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                ((OnEventListener) mOnEventListener).onScrollStateChanged(recyclerView, newState);
            }
        });

        mTopButton = view.findViewById(R.id.topButtonView);
        mTopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onTopClick();
            }
        });
    }

    public void smoothScrollTop(final Animator.AnimatorListener animatorListener)
    {
        mRecyclerView.setEnabled(false);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(linearLayoutManager.findFirstVisibleItemPosition(), 0);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                if (animation == null)
                {
                    return;
                }

                int value = (int) animation.getAnimatedValue();
                mRecyclerView.scrollToPosition(value);
            }
        });

        long duration = linearLayoutManager.findFirstVisibleItemPosition() * 2000 / mRecyclerView.getHeight();

        if (duration > 200)
        {
            duration = 200;
        }

        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                if (animatorListener != null)
                {
                    animatorListener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.removeAllListeners();

                mRecyclerView.setEnabled(true);

                if (animatorListener != null)
                {
                    animatorListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                if (animatorListener != null)
                {
                    animatorListener.onAnimationCancel(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
                if (animatorListener != null)
                {
                    animatorListener.onAnimationRepeat(animation);
                }
            }
        });

        valueAnimator.start();
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

    public void setTopButtonVisible(boolean visible)
    {
        if (mTopButton == null)
        {
            return;
        }

        if (visible == true)
        {
            mTopButton.setVisibility(View.VISIBLE);
        } else
        {
            mTopButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setReviewScores(Constants.PlaceType placeType, List<PlaceReviewScore> placeReviewScoreList)
    {
        if (placeReviewScoreList == null || placeReviewScoreList.size() == 0)
        {
            return;
        }

        if (mReviewListAdapter == null)
        {
            mReviewListAdapter = new ReviewListAdapter(mContext);
            mRecyclerView.setAdapter(mReviewListAdapter);
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();

        placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_HEADER_VIEW, placeReviewScoreList));

        mReviewListAdapter.setHeader(placeType, placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void addReviewList(List<PlaceReview> placeReviewList, int totalCount)
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

        mReviewListAdapter.setTotalCount(totalCount);
        mReviewListAdapter.addAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void addDailyFooter()
    {
        if (mReviewListAdapter == null)
        {
            return;
        }

        PlaceReviewItem placeReviewItem = mReviewListAdapter.getItem(mReviewListAdapter.getItemCount() - 1);

        if (placeReviewItem.mType == PlaceReviewItem.TYPE_FOOTER_VIEW)
        {
            return;
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();
        placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_FOOTER_VIEW, null));

        mReviewListAdapter.addAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void addLoadingFooter()
    {
        if (mReviewListAdapter == null)
        {
            return;
        }

        PlaceReviewItem placeReviewItem = mReviewListAdapter.getItem(mReviewListAdapter.getItemCount() - 1);

        if (placeReviewItem != null && placeReviewItem.mType == PlaceReviewItem.TYPE_LOADING_VIEW)
        {
            return;
        }

        List<PlaceReviewItem> placeReviewItemList = new ArrayList<>();
        placeReviewItemList.add(new PlaceReviewItem(PlaceReviewItem.TYPE_LOADING_VIEW, null));

        mReviewListAdapter.addAll(placeReviewItemList);
        mReviewListAdapter.notifyDataSetChanged();
    }

    public void removeLoadingFooter()
    {
        if (mReviewListAdapter == null)
        {
            return;
        }

        int position = mReviewListAdapter.getItemCount() - 1;
        PlaceReviewItem placeReviewItem = mReviewListAdapter.getItem(position);

        if (placeReviewItem != null && placeReviewItem.mType == PlaceReviewItem.TYPE_LOADING_VIEW)
        {
            mReviewListAdapter.removeItem(position);
            mReviewListAdapter.notifyDataSetChanged();
        }
    }

    class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        boolean mShowtProgressbarAnimation;
        private Context mContext;
        private LayoutInflater mInflater;
        private List<PlaceReviewItem> mPlaceReviewItemList;
        private int mTotalCount;
        private LinearLayout mProgressBarLayout;
        private Constants.PlaceType mPlaceType;

        private Handler mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                mShowtProgressbarAnimation = true;

                startAnimation((ViewGroup) msg.obj);
            }
        };

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

        public void setHeader(Constants.PlaceType placeType, List<PlaceReviewItem> placeReviewItemList)
        {
            clear();

            mPlaceType = placeType;
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

        public void removeItem(int position)
        {
            if (position < 0 || mPlaceReviewItemList.size() <= position)
            {
                return;
            }

            mPlaceReviewItemList.remove(position);
        }

        public void setTotalCount(int totalCount)
        {
            mTotalCount = totalCount;
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

                case PlaceReviewItem.TYPE_LOADING_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_loading, parent, false);

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
                case PlaceReviewItem.TYPE_LOADING_VIEW:
                    break;
            }
        }

        private void onBindViewHolder(final HeaderViewHolder headerViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            mHandler.removeMessages(0);

            List<PlaceReviewScore> placeReviewScoreList = placeViewItem.getItem();

            headerViewHolder.termsView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ((OnEventListener) mOnEventListener).onTermsClick();
                }
            });

            switch (mPlaceType)
            {
                case HOTEL:
                    headerViewHolder.trueReviewGuideTextView.setText(R.string.message_detail_review_stay_explain);
                    break;

                case FNB:
                    headerViewHolder.trueReviewGuideTextView.setText(R.string.message_detail_review_gourmet_explain);
                    break;
            }

            headerViewHolder.progressBarLayout.removeAllViews();

            if (mProgressBarLayout == null)
            {
                mProgressBarLayout = new LinearLayout(mContext);
                mProgressBarLayout.setOrientation(LinearLayout.VERTICAL);

                for (PlaceReviewScore placeReviewScore : placeReviewScoreList)
                {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.view_progress_layout, mProgressBarLayout, false);

                    TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
                    ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                    TextView valueTextView = (TextView) view.findViewById(R.id.valueTextView);

                    titleTextView.setText(placeReviewScore.type);

                    int progress = (int) (10.0f * placeReviewScore.scoreAvg);

                    if (mShowtProgressbarAnimation == true)
                    {
                        progressBar.setProgress(progress);
                    } else
                    {
                        progressBar.setProgress(0);
                    }

                    progressBar.setTag(progress);
                    valueTextView.setText(Float.toString(placeReviewScore.scoreAvg));

                    mProgressBarLayout.addView(view);
                }
            }

            headerViewHolder.progressBarLayout.addView(mProgressBarLayout);

            if (mTotalCount == 0)
            {
                headerViewHolder.reviewCountTextView.setVisibility(View.GONE);
            } else
            {
                headerViewHolder.reviewCountTextView.setVisibility(View.VISIBLE);
            }

            headerViewHolder.reviewCountTextView.setText(mContext.getString(R.string.label_detail_review_count, mTotalCount));

            if (mShowtProgressbarAnimation == false)
            {
                Message message = new Message();
                message.arg1 = 0;
                message.obj = mProgressBarLayout;
                mHandler.sendMessageDelayed(message, 300);
            }
        }

        private void onBindViewHolder(final ReviewViewHolder reviewViewHolder, int position, PlaceReviewItem placeViewItem)
        {
            final int MAX_LINE = 10;
            final PlaceReview placeReview = placeViewItem.getItem();

            reviewViewHolder.ratingTextView.setText(Float.toString(placeReview.avgScore));

            if (DailyTextUtils.isTextEmpty(placeReview.email) == true)
            {
                placeReview.email = mContext.getString(R.string.label_customer);
            }

            try
            {
                final String SEPARATOR = "ㅣ";

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(placeReview.email + SEPARATOR//
                    + DailyCalendar.convertDateFormatString(placeReview.createdAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_line_ce7e7e7)), //
                    placeReview.email.length(), placeReview.email.length() + SEPARATOR.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                reviewViewHolder.customerTextView.setText(spannableStringBuilder);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            // 첫번째 리뷰는 상단 패팅이 20dp이다.
            if (position == 1)
            {
                reviewViewHolder.itemView.setPadding(ScreenUtils.dpToPx(mContext, 15), ScreenUtils.dpToPx(mContext, 20), ScreenUtils.dpToPx(mContext, 15), 0);
            } else
            {
                reviewViewHolder.itemView.setPadding(ScreenUtils.dpToPx(mContext, 15), ScreenUtils.dpToPx(mContext, 24), ScreenUtils.dpToPx(mContext, 15), 0);
            }

            reviewViewHolder.reviewTextView.setText(placeReview.comment);
            reviewViewHolder.reviewTextView.setTag(placeReview.comment);

            Paint paint = reviewViewHolder.reviewTextView.getPaint();

            if (placeReview.isMore == true)
            {
                reviewViewHolder.reviewTextView.setText((String) reviewViewHolder.reviewTextView.getTag());
                reviewViewHolder.moreReadTextView.setVisibility(View.GONE);
                reviewViewHolder.moreReadTextView.setOnClickListener(null);
            } else
            {
                int textViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 30);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                reviewViewHolder.reviewTextView.measure(widthMeasureSpec, heightMeasureSpec);

                int lineCount = reviewViewHolder.reviewTextView.getLineCount();

                if (lineCount > MAX_LINE)
                {
                    reviewViewHolder.reviewTextView.setText(placeReview.comment.substring(0, reviewViewHolder.reviewTextView.getLayout().getLineEnd(MAX_LINE - 1)));
                    reviewViewHolder.reviewTextView.measure(widthMeasureSpec, heightMeasureSpec);

                    final String expandText = "…  더 읽어보기";

                    StaticLayout layout = (StaticLayout) reviewViewHolder.reviewTextView.getLayout();
                    int lineStartIndex = reviewViewHolder.reviewTextView.getLayout().getLineStart(MAX_LINE - 1);
                    int lineEndIndex = reviewViewHolder.reviewTextView.getLayout().getLineEnd(MAX_LINE - 1);

                    String text = placeReview.comment.substring(lineStartIndex, lineEndIndex);

                    int length = text.length();
                    float moreReadWidth = paint.measureText(expandText);

                    int count = paint.breakText(text, true, textViewWidth - moreReadWidth, null);

                    reviewViewHolder.reviewTextView.setText(placeReview.comment.substring(0, lineStartIndex + count) + "…");
                    reviewViewHolder.moreReadTextView.setVisibility(View.VISIBLE);
                    reviewViewHolder.moreReadTextView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            reviewViewHolder.reviewTextView.setText((String) reviewViewHolder.reviewTextView.getTag());
                            reviewViewHolder.moreReadTextView.setVisibility(View.GONE);
                            reviewViewHolder.moreReadTextView.setOnClickListener(null);

                            placeReview.isMore = true;
                        }
                    });
                } else
                {
                    reviewViewHolder.moreReadTextView.setVisibility(View.GONE);
                    reviewViewHolder.moreReadTextView.setOnClickListener(null);
                }
            }

            // 댓글이 있는 경우
            PlaceReview.ReviewReply reviewReply = placeReview.getReviewReply();

            if (reviewReply == null)
            {
                if (position == mTotalCount)
                {
                    reviewViewHolder.underLineView.setVisibility(View.INVISIBLE);
                } else
                {
                    reviewViewHolder.underLineView.setVisibility(View.VISIBLE);
                }

                reviewViewHolder.replayLayout.setVisibility(View.GONE);
                reviewViewHolder.replierTextView.setVisibility(View.GONE);
                reviewViewHolder.replierUnderLineView.setVisibility(View.GONE);
            } else
            {
                reviewViewHolder.underLineView.setVisibility(View.GONE);
                reviewViewHolder.replayLayout.setVisibility(View.VISIBLE);
                reviewViewHolder.replierTextView.setVisibility(View.VISIBLE);

                if (position == mTotalCount)
                {
                    reviewViewHolder.replierUnderLineView.setVisibility(View.INVISIBLE);
                } else
                {
                    reviewViewHolder.replierUnderLineView.setVisibility(View.VISIBLE);
                }

                try
                {
                    final String SEPARATOR = "ㅣ";

                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(reviewReply.replier + SEPARATOR//
                        + DailyCalendar.convertDateFormatString(reviewReply.repliedAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                    spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_line_ce7e7e7)), //
                        reviewReply.replier.length(), reviewReply.replier.length() + SEPARATOR.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    reviewViewHolder.replierTextView.setText(spannableStringBuilder);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                reviewViewHolder.reviewReplayTextView.setText(reviewReply.reply);
                reviewViewHolder.reviewReplayTextView.setTag(reviewReply.reply);

                Paint replyPaint = reviewViewHolder.reviewReplayTextView.getPaint();

                if (reviewReply.isMore == true)
                {
                    reviewViewHolder.reviewReplayTextView.setText((String) reviewViewHolder.reviewReplayTextView.getTag());
                    reviewViewHolder.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                    reviewViewHolder.reviewReplayMoreReadTextView.setOnClickListener(null);
                } else
                {
                    int textViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 52);
                    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    reviewViewHolder.reviewReplayTextView.measure(widthMeasureSpec, heightMeasureSpec);

                    int lineCount = reviewViewHolder.reviewReplayTextView.getLineCount();

                    if (lineCount > MAX_LINE)
                    {
                        reviewViewHolder.reviewReplayTextView.setText(reviewReply.reply.substring(0, reviewViewHolder.reviewReplayTextView.getLayout().getLineEnd(MAX_LINE - 1)));
                        reviewViewHolder.reviewReplayTextView.measure(widthMeasureSpec, heightMeasureSpec);

                        final String expandText = "...  더 읽어보기";

                        StaticLayout layout = (StaticLayout) reviewViewHolder.reviewReplayTextView.getLayout();
                        int lineStartIndex = reviewViewHolder.reviewReplayTextView.getLayout().getLineStart(MAX_LINE - 1);
                        int lineEndIndex = reviewViewHolder.reviewReplayTextView.getLayout().getLineEnd(MAX_LINE - 1);

                        String text = reviewReply.reply.substring(lineStartIndex, lineEndIndex);

                        int length = text.length();
                        float moreReadWidth = paint.measureText(expandText);

                        int count = paint.breakText(text, true, textViewWidth - moreReadWidth, null);

                        reviewViewHolder.reviewReplayTextView.setText(reviewReply.reply.substring(0, lineStartIndex + count) + "...");
                        reviewViewHolder.reviewReplayMoreReadTextView.setVisibility(View.VISIBLE);
                        reviewViewHolder.reviewReplayMoreReadTextView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                reviewViewHolder.reviewReplayTextView.setText((String) reviewViewHolder.reviewReplayTextView.getTag());
                                reviewViewHolder.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                                reviewViewHolder.reviewReplayMoreReadTextView.setOnClickListener(null);

                                reviewReply.isMore = true;
                            }
                        });
                    } else
                    {
                        reviewViewHolder.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                        reviewViewHolder.reviewReplayMoreReadTextView.setOnClickListener(null);
                    }
                }
            }
        }

        void startAnimation(ViewGroup viewGroup)
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
            TextView trueReviewGuideTextView;

            public HeaderViewHolder(View view)
            {
                super(view);

                termsView = itemView.findViewById(R.id.termsView);
                progressBarLayout = (LinearLayout) itemView.findViewById(R.id.progressBarLayout);
                reviewCountTextView = (TextView) itemView.findViewById(R.id.reviewCountTextView);
                trueReviewGuideTextView = (TextView) itemView.findViewById(R.id.trueReviewGuideTextView);
            }
        }

        private class ReviewViewHolder extends RecyclerView.ViewHolder
        {
            TextView ratingTextView;
            TextView customerTextView;
            TextView reviewTextView;
            TextView moreReadTextView;
            View underLineView;

            View replayLayout;
            TextView reviewReplayTextView;
            TextView reviewReplayMoreReadTextView;
            TextView replierTextView;
            View replierUnderLineView;

            public ReviewViewHolder(View view)
            {
                super(view);

                customerTextView = (TextView) view.findViewById(R.id.customerTextView);
                ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);
                reviewTextView = (TextView) view.findViewById(R.id.reviewTextView);
                moreReadTextView = (TextView) view.findViewById(R.id.moreReadTextView);
                underLineView = view.findViewById(R.id.underLineView);

                replayLayout = view.findViewById(R.id.replayLayout);
                reviewReplayTextView = (TextView) view.findViewById(R.id.reviewReplayTextView);
                reviewReplayMoreReadTextView = (TextView) view.findViewById(R.id.reviewReplayMoreReadTextView);
                replierTextView = (TextView) view.findViewById(R.id.replierTextView);
                replierUnderLineView = view.findViewById(R.id.replierUnderLineView);
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