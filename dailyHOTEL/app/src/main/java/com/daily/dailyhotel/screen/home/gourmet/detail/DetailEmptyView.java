package com.daily.dailyhotel.screen.home.gourmet.detail;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class DetailEmptyView extends View implements View.OnTouchListener
{
    private final int MOVE_STATE_NONE = 0;
    private final int MOVE_STATE_SCROLL = 10;
    private final int MOVE_STATE_VIEWPAGER = 100;

    private final float MOVE_CALIBRATE_VALUE = 1.25f;

    private int mMoveState;
    private float mPrevX, mPrevY;

    private OnEventListener mOnEventListener;

    public interface OnEventListener
    {
        void onStopMove(MotionEvent event);

        void onHorizontalMove(MotionEvent event);

        void onVerticalMove(MotionEvent event);

        void onCancelMove(MotionEvent event);

        void onImageClick();
    }

    public DetailEmptyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DetailEmptyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DetailEmptyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    public DetailEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        setClickable(true);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction() & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mPrevX = event.getX();
                mPrevY = event.getY();

                mMoveState = MOVE_STATE_NONE;

                if (mOnEventListener != null)
                {
                    mOnEventListener.onStopMove(event);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

                int x = (int) (mPrevX - event.getX());
                int y = (int) (mPrevY - event.getY());

                int distance = (int) Math.sqrt(x * x + y * y);

                if (distance < touchSlop)
                {
                    mMoveState = MOVE_STATE_NONE;

                    if (mOnEventListener != null)
                    {
                        mOnEventListener.onImageClick();
                        mOnEventListener.onCancelMove(event);
                    }
                    break;
                }
            }

            case MotionEvent.ACTION_CANCEL:
            {
                mMoveState = MOVE_STATE_NONE;

                if (mOnEventListener != null)
                {
                    mOnEventListener.onCancelMove(event);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                float x = event.getX();
                float y = event.getY();

                switch (mMoveState)
                {
                    case MOVE_STATE_NONE:
                    {
                        if (Math.abs(x - mPrevX) == Math.abs(y - mPrevY))
                        {

                        } else if (Math.abs(x - mPrevX) * MOVE_CALIBRATE_VALUE > Math.abs(y - mPrevY))
                        {
                            // x 축으로 이동한 경우.
                            mMoveState = MOVE_STATE_VIEWPAGER;

                            if (mOnEventListener != null)
                            {
                                mOnEventListener.onHorizontalMove(event);
                            }
                        } else
                        {
                            // y축으로 이동한 경우.
                            mMoveState = MOVE_STATE_SCROLL;

                            if (mOnEventListener != null)
                            {
                                mOnEventListener.onVerticalMove(event);
                            }
                            return true;
                        }
                        break;
                    }

                    case MOVE_STATE_VIEWPAGER:
                    {
                        if (mOnEventListener != null)
                        {
                            mOnEventListener.onHorizontalMove(event);
                        }
                    }
                    break;
                }
                break;
            }
        }

        return false;
    }

    public void setOnEventListener(DetailEmptyView.OnEventListener listener)
    {
        mOnEventListener = listener;
    }
}
