package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.SectionIndexer;

public class PinnedSectionRecyclerView extends RecyclerView
{
    // fields used for handling touch events
    private final Rect mTouchRect = new Rect();
    private final PointF mTouchPoint = new PointF();

    //-- class fields
    /**
     * Delegating listener, can be null.
     */
    OnScrollListener mDelegateOnScrollListener;
    /**
     * Shadow for being recycled, can be null.
     */
    PinnedSection mRecycleSection;
    /**
     * shadow instance with a pinned view, can be null.
     */
    PinnedSection mPinnedSection;
    /**
     * Pinned view Y-translation. We use it to stick pinned view to the next
     * section.
     */
    int mTranslateY;
    private int mTouchSlop;
    private View mTouchTarget;
    private MotionEvent mDownEvent;
    // fields used for drawing shadow under a pinned section
    private GradientDrawable mShadowDrawable;
    private int mSectionsDistanceY;
    /**
     * Scroll listener which does the magic
     */
    private final OnScrollListener mOnScrollListener = new OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState)
        {
            if (mDelegateOnScrollListener != null)
            { // delegate
                mDelegateOnScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy)
        {
            if (mDelegateOnScrollListener != null)
            { // delegate
                mDelegateOnScrollListener.onScrolled(recyclerView, dx, dy);
            }

            // get expected adapter or fail fast
            Adapter adapter = getAdapter();
            if (adapter == null)
            {
                return; // nothing to do
            }

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            int visibleItemCount = lastVisibleItem - firstVisibleItem + 1;

            if (firstVisibleItem < 0 || firstVisibleItem >= adapter.getItemCount())
            {
                return;
            }

            final boolean isFirstVisibleItemSection = isItemViewTypePinned(adapter, adapter.getItemViewType(firstVisibleItem));

            if (isFirstVisibleItemSection)
            {
                View sectionView = getChildAt(0);
                if (sectionView.getTop() == getPaddingTop())
                { // view sticks to the top, no need for pinned shadow
                    destroyPinnedShadow();
                } else
                { // section doesn't stick to the top, make sure we have a pinned shadow
                    ensureShadowForPosition(firstVisibleItem, firstVisibleItem, visibleItemCount);
                }

            } else
            { // section is not at the first visible position
                int sectionPosition = findCurrentSectionPosition(firstVisibleItem);

                if (sectionPosition > -1)
                { // we have section position
                    ensureShadowForPosition(sectionPosition, firstVisibleItem, visibleItemCount);
                } else
                { // there is no section for the first visible item, destroy shadow
                    destroyPinnedShadow();
                }
            }
        }
    };
    /**
     * Default change observer.
     */
    private final AdapterDataObserver mDataSetObserver = new AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            recreatePinnedShadow();
        }
    };
    private int mShadowHeight;

    public PinnedSectionRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }

    public PinnedSectionRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView();
    }

    //-- constructors

    public boolean isItemViewTypePinned(Adapter adapter, int viewType)
    {
        return ((PinnedSectionListAdapter) adapter).isItemViewTypePinned(viewType);
    }

    private void initView()
    {
        setOnScrollListener(mOnScrollListener);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initShadow(true);
    }

    public void setShadowVisible(boolean visible)
    {
        initShadow(visible);
        if (mPinnedSection != null)
        {
            View v = mPinnedSection.viewHolder.itemView;
            invalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom() + mShadowHeight);
        }
    }

    //-- public API methods

    public void initShadow(boolean visible)
    {
        if (visible)
        {
            if (mShadowDrawable == null)
            {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.parseColor("#ffa0a0a0"), Color.parseColor("#50a0a0a0"), Color.parseColor("#00a0a0a0")});
                mShadowHeight = (int) (8 * getResources().getDisplayMetrics().density);
            }
        } else
        {
            if (mShadowDrawable != null)
            {
                mShadowDrawable = null;
                mShadowHeight = 0;
            }
        }
    }

    //-- pinned section drawing methods

    /**
     * Create shadow wrapper with a pinned view for a view at given position
     */
    void createPinnedShadow(int position)
    {
        // try to recycle shadow
        PinnedSection pinnedShadow = mRecycleSection;
        mRecycleSection = null;

        // create new shadow, if needed
        if (pinnedShadow == null)
        {
            pinnedShadow = new PinnedSection();
        }
        // request new view using recycled view, if such
        ViewHolder pinnedViewHolder = getAdapter().createViewHolder(null, 1);
        getAdapter().onBindViewHolder(pinnedViewHolder, position);

        // read layout parameters
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) pinnedViewHolder.itemView.getLayoutParams();
        if (layoutParams == null)
        {
            layoutParams = (RecyclerView.LayoutParams) generateDefaultLayoutParams();
            pinnedViewHolder.itemView.setLayoutParams(layoutParams);
        }

        int heightMode = MeasureSpec.getMode(layoutParams.height);
        int heightSize = MeasureSpec.getSize(layoutParams.height);

        if (heightMode == MeasureSpec.UNSPECIFIED)
        {
            heightMode = MeasureSpec.EXACTLY;
        }

        int maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (heightSize > maxHeight)
        {
            heightSize = maxHeight;
        }

        // measure & layout
        int ws = MeasureSpec.makeMeasureSpec(getWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        pinnedViewHolder.itemView.measure(ws, hs);
        pinnedViewHolder.itemView.layout(0, 0, pinnedViewHolder.itemView.getMeasuredWidth(), pinnedViewHolder.itemView.getMeasuredHeight());
        mTranslateY = 0;

        // initialize pinned shadow
        pinnedShadow.viewHolder = pinnedViewHolder;
        pinnedShadow.position = position;
        pinnedShadow.id = getAdapter().getItemId(position);

        // store pinned shadow
        mPinnedSection = pinnedShadow;
    }

    /**
     * Destroy shadow wrapper for currently pinned view
     */
    void destroyPinnedShadow()
    {
        if (mPinnedSection != null)
        {
            // keep shadow for being recycled later
            mRecycleSection = mPinnedSection;
            mPinnedSection = null;
        }
    }

    /**
     * Makes sure we have an actual pinned shadow for given position.
     */
    void ensureShadowForPosition(int sectionPosition, int firstVisibleItem, int visibleItemCount)
    {
        if (visibleItemCount < 2)
        { // no need for creating shadow at all, we have a single visible item
            destroyPinnedShadow();
            return;
        }

        if (mPinnedSection != null && mPinnedSection.position != sectionPosition)
        { // invalidate shadow, if required
            destroyPinnedShadow();
        }

        if (mPinnedSection == null)
        { // create shadow, if empty
            createPinnedShadow(sectionPosition);
        }

        // align shadow according to next section position, if needed
        int nextPosition = sectionPosition + 1;

        if (nextPosition < getAdapter().getItemCount())
        {
            int nextSectionPosition = findFirstVisibleSectionPosition(nextPosition, visibleItemCount - (nextPosition - firstVisibleItem));

            if (nextSectionPosition > -1)
            {
                View nextSectionView = getChildAt(nextSectionPosition - firstVisibleItem);

                final int bottom = mPinnedSection.viewHolder.itemView.getBottom() + getPaddingTop();
                mSectionsDistanceY = nextSectionView.getTop() - bottom;
                if (mSectionsDistanceY < 0)
                {
                    // next section overlaps pinned shadow, move it up
                    mTranslateY = mSectionsDistanceY;
                } else
                {
                    // next section does not overlap with pinned, stick to top
                    mTranslateY = 0;
                }
            } else
            {
                // no other sections are visible, stick to top
                mTranslateY = 0;
                mSectionsDistanceY = Integer.MAX_VALUE;
            }
        }
    }

    int findFirstVisibleSectionPosition(int firstVisibleItem, int visibleItemCount)
    {
        Adapter adapter = getAdapter();

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int adapterDataCount = adapter.getItemCount();
        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

        if (lastVisibleItem >= adapterDataCount)
        {
            return -1; // dataset has changed, no candidate
        }

        if (firstVisibleItem + visibleItemCount >= adapterDataCount)
        {
            //added to prevent index Outofbound (in case)
            visibleItemCount = adapterDataCount - firstVisibleItem;
        }

        for (int childIndex = 0; childIndex < visibleItemCount; childIndex++)
        {
            int position = firstVisibleItem + childIndex;
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType))
            {
                return position;
            }
        }
        return -1;
    }

    int findCurrentSectionPosition(int fromPosition)
    {
        Adapter adapter = getAdapter();

        if (fromPosition >= adapter.getItemCount())
        {
            return -1; // dataset has changed, no candidate
        }

        if (adapter instanceof SectionIndexer)
        {
            // try fast way by asking section indexer
            SectionIndexer indexer = (SectionIndexer) adapter;
            int sectionPosition = indexer.getSectionForPosition(fromPosition);
            int itemPosition = indexer.getPositionForSection(sectionPosition);
            int typeView = adapter.getItemViewType(itemPosition);
            if (isItemViewTypePinned(adapter, typeView))
            {
                return itemPosition;
            } // else, no luck
        }

        // try slow way by looking through to the next section item above
        for (int position = fromPosition; position >= 0; position--)
        {
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType))
            {
                return position;
            }
        }
        return -1; // no candidate found
    }

    void recreatePinnedShadow()
    {
        destroyPinnedShadow();
        Adapter adapter = getAdapter();
        if (adapter != null && adapter.getItemCount() > 0)
        {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();

            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
            if (sectionPosition == -1)
            {
                return; // no views to pin, exit
            }
            ensureShadowForPosition(sectionPosition, firstVisibleItem, lastVisibleItem - firstVisibleItem);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener)
    {
        if (listener == mOnScrollListener)
        {
            super.setOnScrollListener(listener);
        } else
        {
            mDelegateOnScrollListener = listener;
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        super.onRestoreInstanceState(state);
        post(new Runnable()
        {
            @Override
            public void run()
            { // restore pinned view after configuration change
                recreatePinnedShadow();
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter)
    {
        // assert adapter in debug mode
        if (adapter != null)
        {
        }

        // unregister observer at old adapter and register on new one
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null)
        {
            oldAdapter.unregisterAdapterDataObserver(mDataSetObserver);
        }
        if (adapter != null)
        {
            adapter.registerAdapterDataObserver(mDataSetObserver);
        }

        // destroy pinned shadow, if new adapter is not same as old one
        if (oldAdapter != adapter)
        {
            destroyPinnedShadow();
        }

        super.setAdapter(adapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        try
        {
            super.onLayout(changed, l, t, r, b);

            if (mPinnedSection != null)
            {
                int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
                int shadowWidth = mPinnedSection.viewHolder.itemView.getWidth();
                if (parentWidth != shadowWidth)
                {
                    recreatePinnedShadow();
                }
            }
        } catch (Exception e)
        {
            //            String tag = (String) getTag();
            //            throw new NullPointerException(tag + e.toString());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);

        if (mPinnedSection != null)
        {
            // prepare variables
            int pLeft = getPaddingLeft();
            int pTop = getPaddingTop();
            View view = mPinnedSection.viewHolder.itemView;

            // draw child
            canvas.save();

            int clipHeight = view.getHeight() + (mShadowDrawable == null ? 0 : Math.min(mShadowHeight, mSectionsDistanceY));
            canvas.clipRect(pLeft, pTop, pLeft + view.getWidth(), pTop + clipHeight);
            canvas.translate(pLeft, pTop + mTranslateY);
            drawChild(canvas, mPinnedSection.viewHolder.itemView, getDrawingTime());

            if (mShadowDrawable != null && mSectionsDistanceY > 0)
            {
                mShadowDrawable.setBounds(mPinnedSection.viewHolder.itemView.getLeft(), mPinnedSection.viewHolder.itemView.getBottom(), mPinnedSection.viewHolder.itemView.getRight(), mPinnedSection.viewHolder.itemView.getBottom() + mShadowHeight);
                mShadowDrawable.draw(canvas);
            }

            canvas.restore();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN && mTouchTarget == null && mPinnedSection != null && isPinnedViewTouched(mPinnedSection.viewHolder.itemView, x, y))
        { // create touch target

            // user touched pinned view
            mTouchTarget = mPinnedSection.viewHolder.itemView;
            mTouchPoint.x = x;
            mTouchPoint.y = y;

            // copy down event for eventually be used later
            mDownEvent = MotionEvent.obtain(ev);
        }

        if (mTouchTarget != null)
        {
            if (isPinnedViewTouched(mTouchTarget, x, y))
            { // forward event to pinned view
                mTouchTarget.dispatchTouchEvent(ev);
            }

            switch (action)
            {
                case MotionEvent.ACTION_UP:
                    super.dispatchTouchEvent(ev);
                    clearTouchTarget();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(y - mTouchPoint.y) > mTouchSlop)
                    {
                        // cancel sequence on touch target
                        MotionEvent event = MotionEvent.obtain(ev);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        mTouchTarget.dispatchTouchEvent(event);
                        event.recycle();

                        // provide correct sequence to super class for further handling
                        super.dispatchTouchEvent(mDownEvent);
                        super.dispatchTouchEvent(ev);
                        clearTouchTarget();
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    clearTouchTarget();
                    break;
            }

            return true;
        }

        // call super if this was not our pinned view
        return super.dispatchTouchEvent(ev);
    }

    //-- touch handling methods

    private boolean isPinnedViewTouched(View view, float x, float y)
    {
        view.getHitRect(mTouchRect);

        // by taping top or bottom padding, the list performs on click on a border item.
        // we don't add top padding here to keep behavior consistent.
        mTouchRect.top += mTranslateY;

        mTouchRect.bottom += mTranslateY + getPaddingTop();
        mTouchRect.left += getPaddingLeft();
        mTouchRect.right -= getPaddingRight();
        return mTouchRect.contains((int) x, (int) y);
    }

    private void clearTouchTarget()
    {
        mTouchTarget = null;
        if (mDownEvent != null)
        {
            mDownEvent.recycle();
            mDownEvent = null;
        }
    }

    /**
     * List adapter to be implemented for being used with PinnedSectionListView
     * adapter.
     */
    public interface PinnedSectionListAdapter
    {
        /**
         * This method shall return 'true' if views of given type has to be
         * pinned.
         */
        boolean isItemViewTypePinned(int viewType);
    }

    /**
     * Wrapper class for pinned section view and its position in the list.
     */
    public class PinnedSection
    {
        public ViewHolder viewHolder;
        public int position;
        public long id;
    }
}
