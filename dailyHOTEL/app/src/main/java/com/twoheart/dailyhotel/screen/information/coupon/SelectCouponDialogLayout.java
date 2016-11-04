package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;

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
    private TextView mTitleTextView;
    private View mOneButtonLayout;
    private View mTwoButtonLayout;
    private TextView mPositiveTextView, mNegativeTextView, mConfirmTextView;
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

        mTitleTextView = (TextView) view.findViewById(R.id.titleTextView);
        mOneButtonLayout = view.findViewById(R.id.oneButtonLayout);
        mTwoButtonLayout = view.findViewById(R.id.twoButtonLayout);
        mNegativeTextView = (TextView) mTwoButtonLayout.findViewById(R.id.negativeTextView);
        mPositiveTextView = (TextView) mTwoButtonLayout.findViewById(R.id.positiveTextView);
        mConfirmTextView = (TextView) mOneButtonLayout.findViewById(R.id.confirmTextView);

        mPositiveTextView.setEnabled(false);

        mNegativeTextView.setOnClickListener(this);
        mPositiveTextView.setOnClickListener(this);
        mConfirmTextView.setOnClickListener(this);

        View puchMaskLayout = view.findViewById(R.id.puchMaskLayout);

        if (Util.isOverAPI16() == true)
        {
            puchMaskLayout.setBackground(new BackgroundDrawable(mContext, puchMaskLayout));
        } else
        {
            puchMaskLayout.setBackgroundDrawable(new BackgroundDrawable(mContext, puchMaskLayout));
        }


        setVisibility(false);
    }

    private void initListView(View view)
    {
        mListView = (ListView) view.findViewById(R.id.listView);
        EdgeEffectColor.setEdgeGlowColor(mListView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setVisibility(boolean visibility)
    {
        mDialogLayout.setVisibility(visibility == true ? View.VISIBLE : View.GONE);
    }

    private boolean isEmpty(List<Coupon> list)
    {
        return (list == null || list.size() == 0);
    }

    public void setOneButtonLayout(boolean visibility, int resId)
    {
        mConfirmTextView.setText(resId);

        if (visibility == true)
        {
            mTwoButtonLayout.setVisibility(View.GONE);
            mOneButtonLayout.setVisibility(View.VISIBLE);
        } else
        {
            mOneButtonLayout.setVisibility(View.GONE);
        }
    }

    public void setTwoButtonLayout(boolean visibility, int positiveResId, int negativeResId)
    {
        mPositiveTextView.setText(positiveResId);
        mNegativeTextView.setText(negativeResId);

        if (visibility == true)
        {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mOneButtonLayout.setVisibility(View.GONE);
        } else
        {
            mTwoButtonLayout.setVisibility(View.GONE);
        }
    }

    public void setTitle(int resid)
    {
        if (mTitleTextView == null)
        {
            return;
        }

        mTitleTextView.setText(resid);
    }

    public void setData(List<Coupon> list, boolean isSelected)
    {
        if (isEmpty(list) == true)
        {
            return;
        }

        if (mListAdapter == null)
        {
            mListAdapter = new SelectCouponAdapter(mContext, list, mCouponItemListener);
            mListAdapter.setSelectedMode(isSelected);
            mListView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setData(list);
            mListAdapter.setSelectedMode(isSelected);
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
                mPositiveTextView.setEnabled(true);
            } else
            {
                mPositiveTextView.setEnabled(false);
            }
        }
    };

    private class BackgroundDrawable extends Drawable
    {
        private View mView;
        private Paint mPaint;
        private Paint mOverPaint;
        private Bitmap mCircleBitmap;

        public BackgroundDrawable(Context context, View view)
        {
            mView = view;
            mCircleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle);

            mPaint = new Paint();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            mOverPaint = new Paint();
            mOverPaint.setAlpha(0x99);
        }

        @Override
        public void draw(Canvas canvas)
        {
            float cY = 0;
            float cX = mView.getX() - mCircleBitmap.getWidth() / 2;

            canvas.drawBitmap(mCircleBitmap, cX, cY, mPaint);
            canvas.drawBitmap(mCircleBitmap, cX + mView.getWidth(), cY, mPaint);

            canvas.drawBitmap(mCircleBitmap, cX, cY, mOverPaint);
            canvas.drawBitmap(mCircleBitmap, cX + mView.getWidth(), cY, mOverPaint);
        }

        @Override
        public void setAlpha(int alpha)
        {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter)
        {

        }

        @Override
        public int getOpacity()
        {
            return PixelFormat.TRANSPARENT;
        }
    }
}
