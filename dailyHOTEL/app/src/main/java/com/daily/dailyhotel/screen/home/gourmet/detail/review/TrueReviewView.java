package com.daily.dailyhotel.screen.home.gourmet.detail.review;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.TrueReview;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityTrueReviewDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewHeaderDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewProgressbarDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

public class TrueReviewView extends BaseDialogView<TrueReviewView.OnEventListener, ActivityTrueReviewDataBinding> implements TrueReviewInterface
{
    private TrueReviewListAdapter mTrueReviewListAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();

        void onTopClick();

        void onScroll(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    public TrueReviewView(BaseActivity baseActivity, TrueReviewView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityTrueReviewDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                getEventListener().onScroll(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                getEventListener().onScrollStateChanged(recyclerView, newState);
            }
        });

        viewDataBinding.topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onTopClick();
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if(getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
        getViewDataBinding().toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onBackClick();
            }
        });
    }

    public void smoothScrollTop(final Animator.AnimatorListener animatorListener)
    {
        if(getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recyclerView.setEnabled(false);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getViewDataBinding().recyclerView.getLayoutManager();
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
                getViewDataBinding().recyclerView.scrollToPosition(value);
            }
        });

        long duration = linearLayoutManager.findFirstVisibleItemPosition() * 2000 / getViewDataBinding().recyclerView.getHeight();

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

                getViewDataBinding().recyclerView.setEnabled(true);

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

    public void setTopButtonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setReviewScores(List<ReviewScore> reviewScoreList)
    {
        if (getViewDataBinding() == null || reviewScoreList == null || reviewScoreList.size() == 0)
        {
            return;
        }

        if (mTrueReviewListAdapter == null)
        {
            mTrueReviewListAdapter = new TrueReviewListAdapter(getContext());
            getViewDataBinding().recyclerView.setAdapter(mTrueReviewListAdapter);
        }

        List<ObjectItem> objectItemList = new ArrayList<>();

        objectItemList.add(new ObjectItem(ObjectItem.TYPE_HEADER_VIEW, reviewScoreList));

        mTrueReviewListAdapter.setHeader(getString(R.string.message_detail_review_gourmet_explain), objectItemList);
        mTrueReviewListAdapter.notifyDataSetChanged();
    }

    public void addReviewList(List<TrueReview> trueReviewList, int totalCount)
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null || trueReviewList == null || trueReviewList.size() == 0)
        {
            return;
        }

        List<ObjectItem> placeReviewItemList = new ArrayList<>();

        for (TrueReview trueReview : trueReviewList)
        {
            placeReviewItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, trueReview));
        }

        mTrueReviewListAdapter.setTotalCount(totalCount);
        mTrueReviewListAdapter.addAll(placeReviewItemList);
        mTrueReviewListAdapter.notifyDataSetChanged();
    }

    public void addDailyFooter()
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null)
        {
            return;
        }

        ObjectItem objectItem = mTrueReviewListAdapter.getItem(mTrueReviewListAdapter.getItemCount() - 1);

        if (objectItem.mType == ObjectItem.TYPE_FOOTER_VIEW)
        {
            return;
        }

        List<ObjectItem> placeReviewItemList = new ArrayList<>();
        placeReviewItemList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));

        mTrueReviewListAdapter.addAll(placeReviewItemList);
        mTrueReviewListAdapter.notifyDataSetChanged();
    }

    public void addLoadingFooter()
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null)
        {
            return;
        }

        ObjectItem placeReviewItem = mTrueReviewListAdapter.getItem(mTrueReviewListAdapter.getItemCount() - 1);

        if (placeReviewItem != null && placeReviewItem.mType == ObjectItem.TYPE_LOADING_VIEW)
        {
            return;
        }

        List<ObjectItem> placeReviewItemList = new ArrayList<>();
        placeReviewItemList.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));

        mTrueReviewListAdapter.addAll(placeReviewItemList);
        mTrueReviewListAdapter.notifyDataSetChanged();
    }

    public void removeLoadingFooter()
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null)
        {
            return;
        }

        int position = mTrueReviewListAdapter.getItemCount() - 1;
        ObjectItem objectItem = mTrueReviewListAdapter.getItem(position);

        if (objectItem != null && objectItem.mType == ObjectItem.TYPE_LOADING_VIEW)
        {
            mTrueReviewListAdapter.removeItem(position);
            mTrueReviewListAdapter.notifyDataSetChanged();
        }
    }

    class TrueReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        boolean mShowProgressbarAnimation;
        private Context mContext;
        private LayoutInflater mInflater;
        private List<ObjectItem> mObjectItemList;
        private int mTotalCount;
        private String mHeaderTitle;

        private Handler mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                mShowProgressbarAnimation = true;

                startAnimation((ViewGroup) msg.obj);
            }
        };

        public TrueReviewListAdapter(Context context)
        {
            mContext = context;

            mObjectItemList = new ArrayList<>();
        }

        public ObjectItem getItem(int position)
        {
            if (position < 0 || mObjectItemList.size() <= position)
            {
                return null;
            }

            return mObjectItemList.get(position);
        }

        public void setHeader(String headerTitle, List<ObjectItem> objectItemList)
        {
            clear();

            mHeaderTitle = headerTitle;
            addAll(objectItemList);

            mShowProgressbarAnimation = false;
        }

        public void addAll(List<ObjectItem> objectItemList)
        {
            if (objectItemList == null || objectItemList.size() == 0)
            {
                return;
            }

            mObjectItemList.addAll(objectItemList);
        }

        public void removeItem(int position)
        {
            if (position < 0 || mObjectItemList.size() <= position)
            {
                return;
            }

            mObjectItemList.remove(position);
        }

        public void setTotalCount(int totalCount)
        {
            mTotalCount = totalCount;
        }

        public void clear()
        {
            mObjectItemList.clear();
        }

        @Override
        public int getItemViewType(int position)
        {
            return mObjectItemList.get(position).mType;
        }

        @Override
        public int getItemCount()
        {
            if (mObjectItemList == null)
            {
                return 0;
            }

            return mObjectItemList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            switch (viewType)
            {
                case ObjectItem.TYPE_HEADER_VIEW:
                {
                    LayoutTrueReviewHeaderDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_header_data, parent, false);

                    return new HeaderViewHolder(viewDataBinding);
                }

                case ObjectItem.TYPE_ENTRY:
                {
                    View view = mInflater.inflate(R.layout.list_row_place_review, parent, false);

                    return new ReviewViewHolder(view);
                }

                case ObjectItem.TYPE_FOOTER_VIEW:
                {
                    View view = mInflater.inflate(R.layout.list_row_product_footer, parent, false);

                    return new FooterViewHolder(view);
                }

                case ObjectItem.TYPE_LOADING_VIEW:
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
            ObjectItem objectItem = getItem(position);

            if (objectItem == null)
            {
                return;
            }

            switch (objectItem.mType)
            {
                case ObjectItem.TYPE_HEADER_VIEW:
                    onBindViewHolder((HeaderViewHolder) holder, position, objectItem);
                    break;

                case ObjectItem.TYPE_ENTRY:
                    onBindViewHolder((ReviewViewHolder) holder, position, objectItem);
                    break;

                case ObjectItem.TYPE_FOOTER_VIEW:
                case ObjectItem.TYPE_LOADING_VIEW:
                    break;
            }
        }

        private void onBindViewHolder(HeaderViewHolder holder, int position, ObjectItem placeViewItem)
        {
            mHandler.removeMessages(0);

            List<ReviewScore> reviewScoreList = placeViewItem.getItem();

            holder.dataBinding.termsView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onTermsClick();
                }
            });

            holder.dataBinding.trueReviewGuideTextView.setText(mHeaderTitle);
            holder.dataBinding.progressBarLayout.removeAllViews();

            if (holder.dataBinding.progressBarLayout.getChildCount() == 0)
            {
                for (ReviewScore reviewScore : reviewScoreList)
                {
                    LayoutTrueReviewProgressbarDataBinding progressbarDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_progressbar_data, holder.dataBinding.progressBarLayout, true);

                    progressbarDataBinding.titleTextView.setText(reviewScore.type);

                    int progress = (int) (10.0f * reviewScore.scoreAvg);

                    if (mShowProgressbarAnimation == true)
                    {
                        progressbarDataBinding.progressBar.setProgress(progress);
                    } else
                    {
                        progressbarDataBinding.progressBar.setProgress(0);
                    }

                    progressbarDataBinding.progressBar.setTag(progress);
                    progressbarDataBinding.valueTextView.setText(Float.toString(reviewScore.scoreAvg));
                }
            }

            if (mTotalCount == 0)
            {
                holder.dataBinding.reviewCountTextView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.reviewCountTextView.setVisibility(View.VISIBLE);
            }

            holder.dataBinding.reviewCountTextView.setText(mContext.getString(R.string.label_detail_review_count, mTotalCount));

            if (mShowProgressbarAnimation == false)
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

                    int lineStartIndex = reviewViewHolder.reviewTextView.getLayout().getLineStart(MAX_LINE - 1);
                    int lineEndIndex = reviewViewHolder.reviewTextView.getLayout().getLineEnd(MAX_LINE - 1);

                    String text = placeReview.comment.substring(lineStartIndex, lineEndIndex);

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

                        int lineStartIndex = reviewViewHolder.reviewReplayTextView.getLayout().getLineStart(MAX_LINE - 1);
                        int lineEndIndex = reviewViewHolder.reviewReplayTextView.getLayout().getLineEnd(MAX_LINE - 1);

                        String text = reviewReply.reply.substring(lineStartIndex, lineEndIndex);

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
            LayoutTrueReviewHeaderDataBinding dataBinding;

            public HeaderViewHolder(LayoutTrueReviewHeaderDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
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
