package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogLayout extends BaseLayout implements View.OnClickListener
{

    public interface OnEventListener extends OnBaseEventListener
    {
        void setResult(Coupon coupon);

        void onCouponDownloadClick(int position);
    }

    private View mDialogLayout;
    private DailyTextView mMessageTextView;
    private View mOneButtonLayout;
    private View mTwoButtonLayout;
    private View mConfirmView;
    private View mNegativeView;
    private View mPositiveView;


    private RecyclerView mRecyclerView;
    private SelectCouponAdapter mListAdapter;

    private Window mWindow;


    public SelectCouponDialogLayout(Context context, Window window, OnBaseEventListener listener)
    {
        super(context, listener);

        mContext = context;
        mWindow = window;
    }


    @Override
    protected void initLayout(View view)
    {
        updateWindowHeight();
        initListView(view);

        mDialogLayout = view.findViewById(R.id.dialogLayout);

        mMessageTextView = (DailyTextView) view.findViewById(R.id.messageTextView);
        mOneButtonLayout = view.findViewById(R.id.oneButtonLayout);
        mTwoButtonLayout = view.findViewById(R.id.twoButtonLayout);
        mNegativeView = view.findViewById(R.id.negativeTextView);
        mPositiveView = view.findViewById(R.id.positiveTextView);
        mConfirmView = view.findViewById(R.id.confirmTextView);

        mPositiveView.setEnabled(false);

        mNegativeView.setOnClickListener(this);
        mPositiveView.setOnClickListener(this);
        mConfirmView.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        updateDialogLayout(false);
        updateLayout(null);
    }


    private void updateWindowHeight()
    {
        int height = WindowManager.LayoutParams.WRAP_CONTENT;
        //
        //        if (listSize > 0)
        //        {
        //            height = Util.getLCDHeight(mContext) - Util.dpToPx(mContext, 96);
        //        } else
        //        {
        //            height = WindowManager.LayoutParams.WRAP_CONTENT;
        //        }

        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.width = Util.getLCDWidth(mContext) - Util.dpToPx(mContext, 24);
        params.height = height;

        mWindow.setAttributes(params);
    }

    private void initListView(View view)
    {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.over_scroll_edge));

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void updateLayout(List<Coupon> list)
    {
        boolean isEmpty = isEmpty(list);

        updateMessageView(isEmpty);
        updateButtonLayout(isEmpty);
    }

    private boolean isEmpty(List<Coupon> list)
    {
        return (list != null && list.size() != 0) ? false : true;
    }

    private void updateDialogLayout(boolean isShow)
    {
        mDialogLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void updateButtonLayout(boolean isEmpty)
    {
        if (isEmpty == false)
        {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mOneButtonLayout.setVisibility(View.GONE);
        } else
        {
            mTwoButtonLayout.setVisibility(View.GONE);
            mOneButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateMessageView(boolean isEmpty)
    {
        int messageResId;
        if (isEmpty == false)
        {
            messageResId = R.string.message_select_coupon_selected;
        } else
        {
            messageResId = R.string.message_select_coupon_empty;
        }
        mMessageTextView.setText(messageResId);
    }


    public void setData(List<Coupon> list)
    {

        if (isEmpty(list) == false)
        {
            mListAdapter = new SelectCouponAdapter(mContext, list, mCouponItemListener);
        } else
        {
            mListAdapter = new SelectCouponAdapter(mContext, new ArrayList<Coupon>(), mCouponItemListener);
        }

        updateDialogLayout(true);
        updateLayout(list);
        mRecyclerView.setAdapter(mListAdapter);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.negativeTextView:
                mOnEventListener.finish();
                break;
            case R.id.positiveTextView:
                int selectPosition = mListAdapter.getSelectPosition();
                Coupon coupon = mListAdapter.getItem(selectPosition);

                ((OnEventListener) mOnEventListener).setResult(coupon);
                break;
            case R.id.confirmTextView:
                mOnEventListener.finish();
                break;
        }
    }

    private SelectCouponAdapter.OnCouponItemListener mCouponItemListener = new SelectCouponAdapter.OnCouponItemListener()
    {
        @Override
        public void onDownloadClick(int position)
        {
            ((OnEventListener) mOnEventListener).onCouponDownloadClick(position);
        }

        @Override
        public void updatePositiveButton()
        {
            if (mListAdapter.getSelectPosition() != -1)
            {
                mPositiveView.setEnabled(true);
            } else
            {
                mPositiveView.setEnabled(false);
            }
        }
    };
}
