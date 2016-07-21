package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
public class SelectCouponDialogLayout extends BaseLayout implements View.OnClickListener
{

    public interface OnEventListener extends OnBaseEventListener
    {
        void setResult(Coupon coupon);

        void onCouponDownloadClick(Coupon coupon);
    }

    private View mDialogLayout;
    private DailyTextView mMessageTextView;
    private View mListLayout;
    private View mOneButtonLayout;
    private View mTwoButtonLayout;
    private View mPositiveView;
    private ListView mListView;
    private SelectCouponAdapter mListAdapter;

    public SelectCouponDialogLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);

        mContext = context;
    }

    @Override
    protected void initLayout(View view)
    {
        initListView(view);

        mDialogLayout = view.findViewById(R.id.dialogLayout);

        mMessageTextView = (DailyTextView) view.findViewById(R.id.messageTextView);
        mListLayout = view.findViewById(R.id.listLayout);
        mOneButtonLayout = view.findViewById(R.id.oneButtonLayout);
        mTwoButtonLayout = view.findViewById(R.id.twoButtonLayout);
        View negativeView = view.findViewById(R.id.negativeTextView);
        mPositiveView = view.findViewById(R.id.positiveTextView);
        View confirmView = view.findViewById(R.id.confirmTextView);

        mPositiveView.setEnabled(false);

        negativeView.setOnClickListener(this);
        mPositiveView.setOnClickListener(this);
        confirmView.setOnClickListener(this);

        updateDialogLayout(false);
        updateLayout(null);
    }

    private void initListView(View view)
    {
        mListView = (ListView) view.findViewById(R.id.list);
        EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.over_scroll_edge));
    }

    private void updateLayout(List<Coupon> list)
    {
        boolean isEmpty = isEmpty(list) == true;

        updateMessageView(isEmpty);
        updateButtonLayout(isEmpty);

        mListLayout.setVisibility((isEmpty == true) ? View.GONE : View.VISIBLE);
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
        updateDialogLayout(true);
        updateLayout(list);

        if (mListAdapter == null)
        {
            mListAdapter = new SelectCouponAdapter(mContext, list, mCouponItemListener);
            mListView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public Coupon getCoupon(String userCouponCode)
    {
        return mListAdapter.getCoupon(userCouponCode);
    }

    public int getCouponCount()
    {
        if (mListAdapter == null)
        {
            return 0;
        }

        return mListAdapter.getCount();
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
            Coupon coupon = mListAdapter.getItem(position);
            ((OnEventListener) mOnEventListener).onCouponDownloadClick(coupon);
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
