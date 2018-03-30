package com.daily.dailyhotel.view;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DailyRecyclerStickyItemDecoration extends RecyclerView.ItemDecoration
{
    private StickyHeaderInterface mListener;
    private int mStickyHeaderHeight;
    private boolean mStickyEnabled;

    public DailyRecyclerStickyItemDecoration(@NonNull RecyclerView recyclerView, @NonNull StickyHeaderInterface listener)
    {
        mListener = listener;

        setStickyEnabled(true);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener()
        {
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent)
            {
                if (motionEvent.getY() <= mStickyHeaderHeight)
                {
                    // Handle the clicks on the header here ...
                    return true;
                }
                return false;
            }

            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent)
            {

            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
            {

            }
        });
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state)
    {
        super.onDrawOver(canvas, parent, state);

        if (mStickyEnabled == false)
        {
            return;
        }

        View topChild = parent.getChildAt(0);
        if (topChild == null)
        {
            return;
        }

        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION)
        {
            return;
        }

        View currentHeader = getHeaderViewForItem(topChildPosition, parent);
        fixLayoutSize(parent, currentHeader);

        int contactPoint = currentHeader.getBottom();
        View childInContact = getChildInContact(parent, contactPoint);

        if (childInContact == null)
        {
            return;
        }

        if (mListener.isStickyHeader(parent.getChildAdapterPosition(childInContact)))
        {
            moveHeader(canvas, currentHeader, childInContact);
            return;
        }

        drawHeader(canvas, currentHeader);
    }

    public void setStickyEnabled(boolean enabled)
    {
        mStickyEnabled = enabled;
    }

    private View getHeaderViewForItem(int position, RecyclerView parent)
    {
        int headerPosition = mListener.getStickyHeaderPositionForItem(position);
        int layoutResId = mListener.getStickyHeaderLayout(headerPosition);
        View header = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        mListener.onBindStickyHeaderView(header, headerPosition);

        return header;
    }


    private void drawHeader(Canvas canvas, View header)
    {
        canvas.save();
        canvas.translate(0, 0);
        header.draw(canvas);
        canvas.restore();
    }

    private void moveHeader(Canvas canvas, View currentHeader, View nextHeader)
    {
        canvas.save();
        canvas.translate(0, nextHeader.getTop() - currentHeader.getHeight());
        currentHeader.draw(canvas);
        canvas.restore();
    }

    private View getChildInContact(RecyclerView parent, int contactPoint)
    {
        View childInContact = null;
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            View child = parent.getChildAt(i);
            if (child.getBottom() > contactPoint)
            {
                if (child.getTop() <= contactPoint)
                {
                    // This child overlaps the contactPoint
                    childInContact = child;
                    break;
                }
            }
        }
        return childInContact;
    }

    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private void fixLayoutSize(ViewGroup parent, View view)
    {
        // Specs for parent (RecyclerView)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

        // Specs for children (headers)
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

        view.measure(childWidthSpec, childHeightSpec);

        view.layout(0, 0, view.getMeasuredWidth(), mStickyHeaderHeight = view.getMeasuredHeight());
    }

    public interface StickyHeaderInterface
    {
        int getStickyHeaderPositionForItem(int position);

        int getStickyHeaderLayout(int position);

        void onBindStickyHeaderView(View header, int position);

        boolean isStickyHeader(int position);
    }
}