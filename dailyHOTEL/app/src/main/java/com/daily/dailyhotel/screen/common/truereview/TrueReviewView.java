package com.daily.dailyhotel.screen.common.truereview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.ReviewScore;
import com.daily.dailyhotel.entity.TrueReview;
import com.daily.dailyhotel.entity.TrueReviewReply;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityTrueReviewDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewFooterDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewHeaderDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewLoadingDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewProgressbarDataBinding;
import com.twoheart.dailyhotel.databinding.LayoutTrueReviewReviewDataBinding;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class TrueReviewView extends BaseDialogView<TrueReviewView.OnEventListener, ActivityTrueReviewDataBinding>//
    implements TrueReviewInterface
{
    private TrueReviewListAdapter mTrueReviewListAdapter;
    boolean mShowProgressbarAnimation;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onTermsClick();

        void onTopClick();

        void onNextPage();
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
            private int mScrollDistance;
            private int mPrevScrollDistance;

            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (linearLayoutManager == null)
                {
                    return;
                }

                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int itemCount = linearLayoutManager.getItemCount();

                if (lastVisibleItemPosition >= (itemCount - 1) * 2 / 3)
                {
                    getEventListener().onNextPage();
                }

                if (linearLayoutManager.findFirstVisibleItemPosition() == 0 //
                    && recyclerView.getChildAt(0).getTop() == 0)
                {
                    setTopButtonVisible(false);
                    return;
                }

                mScrollDistance += dy;

                if (lastVisibleItemPosition == itemCount - 1)
                {
                    setTopButtonVisible(true);
                    return;
                }

                final int visibleDistance = recyclerView.getHeight() / 6;
                int moveDistance = mScrollDistance - mPrevScrollDistance;

                if (moveDistance > 0 && moveDistance > visibleDistance)
                {
                    setTopButtonVisible(false);
                    mPrevScrollDistance = mScrollDistance;
                } else if (moveDistance < 0 && -moveDistance > visibleDistance)
                {
                    setTopButtonVisible(true);
                    mPrevScrollDistance = mScrollDistance;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (recyclerView.isComputingLayout() == true)
                {
                    return;
                }

                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;

                    case RecyclerView.SCROLL_STATE_IDLE:
                        mScrollDistance = 0;
                        mPrevScrollDistance = 0;
                        break;

                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
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
        if (getViewDataBinding() == null)
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

    @Override
    public Observable<Boolean> smoothScrollTop()
    {
        if (getViewDataBinding() == null)
        {
            return null;
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

        return new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();

                        getViewDataBinding().recyclerView.setEnabled(true);

                        observer.onNext(true);
                        observer.onComplete();
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
        };
    }

    @Override
    public void setTopButtonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().topButtonView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setTrueReviewProductVisible(boolean visible)
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null)
        {
            return;
        }

        mTrueReviewListAdapter.setTrueReviewProductVisible(visible);
    }

    @Override
    public void setReviewScores(String title, List<ReviewScore> reviewScoreList)
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

        mTrueReviewListAdapter.setHeader(title, objectItemList);
        mTrueReviewListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showReviewScoresAnimation()
    {
        if (getViewDataBinding() == null || mTrueReviewListAdapter == null || mShowProgressbarAnimation == true)
        {
            return;
        }

        mShowProgressbarAnimation = true;
        mTrueReviewListAdapter.startHeaderAnimation();
    }

    @Override
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

    @Override
    public void addLastFooter()
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

    @Override
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

    @Override
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
        private Context mContext;
        private List<ObjectItem> mObjectItemList;
        private int mTotalCount;
        private String mHeaderTitle;
        private ViewGroup mProgressBarLayout;
        private boolean mProductVisible;

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

        public void setTrueReviewProductVisible(boolean visible)
        {
            mProductVisible = visible;
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
                    LayoutTrueReviewReviewDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_review_data, parent, false);

                    return new TrueReviewViewHolder(viewDataBinding);
                }

                case ObjectItem.TYPE_FOOTER_VIEW:
                {
                    LayoutTrueReviewFooterDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_footer_data, parent, false);

                    return new ViewHolder(viewDataBinding);
                }

                case ObjectItem.TYPE_LOADING_VIEW:
                {
                    LayoutTrueReviewLoadingDataBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_loading_data, parent, false);
                    viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.probressbar_default), PorterDuff.Mode.SRC_IN);

                    return new ViewHolder(viewDataBinding);
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
                    onBindViewHolder((TrueReviewViewHolder) holder, position, objectItem);
                    break;

                case ObjectItem.TYPE_FOOTER_VIEW:
                case ObjectItem.TYPE_LOADING_VIEW:
                    break;
            }
        }

        private void onBindViewHolder(HeaderViewHolder holder, int position, ObjectItem objectItem)
        {
            List<ReviewScore> reviewScoreList = objectItem.getItem();

            holder.dataBinding.termsView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onTermsClick();
                }
            });

            holder.dataBinding.trueReviewGuideTextView.setText(mHeaderTitle);

            if (holder.dataBinding.progressBarLayout.getChildCount() > 0)
            {
                holder.dataBinding.progressBarLayout.removeAllViews();
            }

            mProgressBarLayout = holder.dataBinding.progressBarLayout;

            if (holder.dataBinding.progressBarLayout.getChildCount() == 0)
            {
                for (ReviewScore reviewScore : reviewScoreList)
                {
                    LayoutTrueReviewProgressbarDataBinding progressbarDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_true_review_progressbar_data, holder.dataBinding.progressBarLayout, true);

                    progressbarDataBinding.titleTextView.setText(reviewScore.type);

                    int progress = (int) (10.0f * reviewScore.scoreAverage);

                    if (mShowProgressbarAnimation == true)
                    {
                        progressbarDataBinding.progressBar.setProgress(progress);
                        progressbarDataBinding.valueTextView.setText(Float.toString(reviewScore.scoreAverage));
                    } else
                    {
                        progressbarDataBinding.progressBar.setProgress(0);
                        progressbarDataBinding.valueTextView.setText(Float.toString(0.0f));
                    }

                    progressbarDataBinding.progressBar.setTag(progress);
                }
            }

            if (mTotalCount == 0)
            {
                holder.dataBinding.progressBarUnderLineView.setVisibility(View.GONE);
                holder.dataBinding.reviewCountTextView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.progressBarUnderLineView.setVisibility(View.VISIBLE);
                holder.dataBinding.reviewCountTextView.setVisibility(View.VISIBLE);
            }

            String totalCountText = DailyTextUtils.formatIntegerToString(mTotalCount);
            holder.dataBinding.reviewCountTextView.setText(mContext.getString(R.string.label_detail_review_count, totalCountText));
        }

        private void onBindViewHolder(final TrueReviewViewHolder holder, int position, ObjectItem objectItem)
        {
            final int MAX_LINE = 10;
            final TrueReview trueReview = objectItem.getItem();

            if (mProductVisible && DailyTextUtils.isTextEmpty(trueReview.productName) == false)
            {
                holder.dataBinding.productNameTextView.setVisibility(View.VISIBLE);
                holder.dataBinding.productNameTextView.setText(trueReview.productName);
            } else
            {
                holder.dataBinding.productNameTextView.setVisibility(View.GONE);
            }

            holder.dataBinding.ratingTextView.setText(Float.toString(trueReview.averageScore));

            if (DailyTextUtils.isTextEmpty(trueReview.email) == true)
            {
                trueReview.email = mContext.getString(R.string.label_customer);
            }

            try
            {
                final String SEPARATOR = "ㅣ";

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(trueReview.email + SEPARATOR//
                    + DailyCalendar.convertDateFormatString(trueReview.createdAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_line_ce7e7e7)), //
                    trueReview.email.length(), trueReview.email.length() + SEPARATOR.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.dataBinding.customerTextView.setText(spannableStringBuilder);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            // 첫번째 리뷰는 상단 패팅이 20dp이다.
            if (position == 1)
            {
                holder.dataBinding.getRoot().setPadding(ScreenUtils.dpToPx(mContext, 15), ScreenUtils.dpToPx(mContext, 20), ScreenUtils.dpToPx(mContext, 15), 0);
            } else
            {
                holder.dataBinding.getRoot().setPadding(ScreenUtils.dpToPx(mContext, 15), ScreenUtils.dpToPx(mContext, 24), ScreenUtils.dpToPx(mContext, 15), 0);
            }

            holder.dataBinding.reviewTextView.setText(trueReview.comment);
            holder.dataBinding.reviewTextView.setTag(trueReview.comment);

            Paint paint = holder.dataBinding.reviewTextView.getPaint();

            if (trueReview.more == true)
            {
                holder.dataBinding.reviewTextView.setText((String) holder.dataBinding.reviewTextView.getTag());
                holder.dataBinding.moreReadTextView.setVisibility(View.GONE);
                holder.dataBinding.moreReadTextView.setOnClickListener(null);
            } else
            {
                int textViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 30);
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                holder.dataBinding.reviewTextView.measure(widthMeasureSpec, heightMeasureSpec);

                int lineCount = holder.dataBinding.reviewTextView.getLineCount();

                if (lineCount > MAX_LINE)
                {
                    holder.dataBinding.reviewTextView.setText(trueReview.comment.substring(0, holder.dataBinding.reviewTextView.getLayout().getLineEnd(MAX_LINE - 1)));
                    holder.dataBinding.reviewTextView.measure(widthMeasureSpec, heightMeasureSpec);

                    final String expandText = "…  더 읽어보기";

                    int lineStartIndex = holder.dataBinding.reviewTextView.getLayout().getLineStart(MAX_LINE - 1);
                    int lineEndIndex = holder.dataBinding.reviewTextView.getLayout().getLineEnd(MAX_LINE - 1);

                    String text = trueReview.comment.substring(lineStartIndex, lineEndIndex);

                    float moreReadWidth = paint.measureText(expandText);

                    int count = paint.breakText(text, true, textViewWidth - moreReadWidth, null);

                    holder.dataBinding.reviewTextView.setText(trueReview.comment.substring(0, lineStartIndex + count) + "…");
                    holder.dataBinding.moreReadTextView.setVisibility(View.VISIBLE);
                    holder.dataBinding.moreReadTextView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            holder.dataBinding.reviewTextView.setText((String) holder.dataBinding.reviewTextView.getTag());
                            holder.dataBinding.moreReadTextView.setVisibility(View.GONE);
                            holder.dataBinding.moreReadTextView.setOnClickListener(null);

                            trueReview.more = true;
                        }
                    });
                } else
                {
                    holder.dataBinding.moreReadTextView.setVisibility(View.GONE);
                    holder.dataBinding.moreReadTextView.setOnClickListener(null);
                }
            }

            // 댓글이 있는 경우
            TrueReviewReply replay = trueReview.getReply();

            if (replay == null)
            {
                if (position == mTotalCount)
                {
                    holder.dataBinding.underLineView.setVisibility(View.INVISIBLE);
                } else
                {
                    holder.dataBinding.underLineView.setVisibility(View.VISIBLE);
                }

                holder.dataBinding.replayLayout.setVisibility(View.GONE);
                holder.dataBinding.replierTextView.setVisibility(View.GONE);
                holder.dataBinding.replierUnderLineView.setVisibility(View.GONE);
            } else
            {
                holder.dataBinding.underLineView.setVisibility(View.GONE);
                holder.dataBinding.replayLayout.setVisibility(View.VISIBLE);
                holder.dataBinding.replierTextView.setVisibility(View.VISIBLE);

                if (position == mTotalCount)
                {
                    holder.dataBinding.replierUnderLineView.setVisibility(View.INVISIBLE);
                } else
                {
                    holder.dataBinding.replierUnderLineView.setVisibility(View.VISIBLE);
                }

                try
                {
                    final String SEPARATOR = "ㅣ";

                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(replay.replier + SEPARATOR//
                        + DailyCalendar.convertDateFormatString(replay.repliedAt, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                    spannableStringBuilder.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.default_line_ce7e7e7)), //
                        replay.replier.length(), replay.replier.length() + SEPARATOR.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.dataBinding.replierTextView.setText(spannableStringBuilder);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                holder.dataBinding.reviewReplayTextView.setText(replay.comment);
                holder.dataBinding.reviewReplayTextView.setTag(replay.comment);

                if (replay.more == true)
                {
                    holder.dataBinding.reviewReplayTextView.setText((String) holder.dataBinding.reviewReplayTextView.getTag());
                    holder.dataBinding.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                    holder.dataBinding.reviewReplayMoreReadTextView.setOnClickListener(null);
                } else
                {
                    int textViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 52);
                    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(textViewWidth, View.MeasureSpec.EXACTLY);
                    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    holder.dataBinding.reviewReplayTextView.measure(widthMeasureSpec, heightMeasureSpec);

                    int lineCount = holder.dataBinding.reviewReplayTextView.getLineCount();

                    if (lineCount > MAX_LINE)
                    {
                        holder.dataBinding.reviewReplayTextView.setText(replay.comment.substring(0, holder.dataBinding.reviewReplayTextView.getLayout().getLineEnd(MAX_LINE - 1)));
                        holder.dataBinding.reviewReplayTextView.measure(widthMeasureSpec, heightMeasureSpec);

                        final String expandText = "...  더 읽어보기";

                        int lineStartIndex = holder.dataBinding.reviewReplayTextView.getLayout().getLineStart(MAX_LINE - 1);
                        int lineEndIndex = holder.dataBinding.reviewReplayTextView.getLayout().getLineEnd(MAX_LINE - 1);

                        String text = replay.comment.substring(lineStartIndex, lineEndIndex);

                        float moreReadWidth = paint.measureText(expandText);

                        int count = paint.breakText(text, true, textViewWidth - moreReadWidth, null);

                        holder.dataBinding.reviewReplayTextView.setText(replay.comment.substring(0, lineStartIndex + count) + "...");
                        holder.dataBinding.reviewReplayMoreReadTextView.setVisibility(View.VISIBLE);
                        holder.dataBinding.reviewReplayMoreReadTextView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                holder.dataBinding.reviewReplayTextView.setText((String) holder.dataBinding.reviewReplayTextView.getTag());
                                holder.dataBinding.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                                holder.dataBinding.reviewReplayMoreReadTextView.setOnClickListener(null);

                                replay.more = true;
                            }
                        });
                    } else
                    {
                        holder.dataBinding.reviewReplayMoreReadTextView.setVisibility(View.GONE);
                        holder.dataBinding.reviewReplayMoreReadTextView.setOnClickListener(null);
                    }
                }
            }
        }

        void startHeaderAnimation()
        {
            if (mProgressBarLayout == null)
            {
                return;
            }

            int childCount = mProgressBarLayout.getChildCount();

            for (int i = 0; i < childCount; i++)
            {
                LayoutTrueReviewProgressbarDataBinding progressbarDataBinding = DataBindingUtil.getBinding(mProgressBarLayout.getChildAt(i));

                final int value = (int) progressbarDataBinding.progressBar.getTag();
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

                        int value = (int) animation.getAnimatedValue();

                        progressbarDataBinding.progressBar.setProgress(value);
                        progressbarDataBinding.valueTextView.setText(Float.toString((float) value / 10.0f));
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
                        progressbarDataBinding.progressBar.setProgress(value);
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

        private class TrueReviewViewHolder extends RecyclerView.ViewHolder
        {
            LayoutTrueReviewReviewDataBinding dataBinding;

            public TrueReviewViewHolder(LayoutTrueReviewReviewDataBinding dataBinding)
            {
                super(dataBinding.getRoot());

                this.dataBinding = dataBinding;
            }
        }

        private class ViewHolder extends RecyclerView.ViewHolder
        {
            public ViewHolder(ViewDataBinding dataBinding)
            {
                super(dataBinding.getRoot());
            }
        }
    }
}
