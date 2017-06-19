package com.daily.dailyhotel.screen.stay.outbound.detail;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.daily.base.BaseSubView;
import com.daily.base.OnBaseSubEventListener;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.databinding.LayoutStayOutboundDetail01DataBinding;

public class StayOutboundDetailEmptyView extends BaseSubView<StayOutboundDetailEmptyView.OnEventListener, LayoutStayOutboundDetail01DataBinding>//
    implements View.OnTouchListener
{
    private final int MOVE_STATE_NONE = 0;
    private final int MOVE_STATE_SCROLL = 10;
    private final int MOVE_STATE_VIEWPAGER = 100;

    private final float MOVE_CALIBRATE_VALUE = 1.25f;

    private int mMoveState;
    private float mPrevX, mPrevY;

    public interface OnEventListener extends OnBaseSubEventListener
    {
        void onStopMove(MotionEvent event);

        void onHorizontalMove(MotionEvent event);

        void onVerticalMove(MotionEvent event);

        void onCancelMove(MotionEvent event);

        void onImageClick();
    }

    public StayOutboundDetailEmptyView(Context context, StayOutboundDetailEmptyView.OnEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void setContentView(final LayoutStayOutboundDetail01DataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.imageEmptyView.getLayoutParams().height = ScreenUtils.getDetailScreenImageLayoutHeight(getContext());
        viewDataBinding.imageEmptyView.setClickable(true);
        viewDataBinding.imageEmptyView.setOnTouchListener(this);
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

                getEventListener().onStopMove(event);
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
                    getEventListener().onImageClick();

                    mMoveState = MOVE_STATE_NONE;

                    getEventListener().onCancelMove(event);
                    break;
                }
            }

            case MotionEvent.ACTION_CANCEL:
            {
                mMoveState = MOVE_STATE_NONE;

                getEventListener().onCancelMove(event);
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

                            getEventListener().onHorizontalMove(event);
                        } else
                        {
                            // y축으로 이동한 경우.
                            mMoveState = MOVE_STATE_SCROLL;

                            getEventListener().onVerticalMove(event);
                            return true;
                        }
                        break;
                    }

                    case MOVE_STATE_VIEWPAGER:
                    {
                        getEventListener().onHorizontalMove(event);
                    }
                    break;
                }
                break;
            }
        }

        return false;
    }
}
