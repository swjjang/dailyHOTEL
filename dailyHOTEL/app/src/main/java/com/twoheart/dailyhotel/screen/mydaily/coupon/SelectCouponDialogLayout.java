package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.app.Activity;
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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

/**
 * Created by android_sam on 2016. 5. 26..
 */
@Deprecated
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
    TextView mPositiveTextView, mNegativeTextView, mConfirmTextView;
    private RecyclerView mRecyclerView;
    SelectCouponAdapter mListAdapter;

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

        if (mContext instanceof Activity && ScreenUtils.isTabletDevice((Activity) mContext) == true)
        {
            ViewGroup.LayoutParams layoutParams = mDialogLayout.getLayoutParams();
            layoutParams.width = ScreenUtils.getScreenWidth(mContext) * 10 / 15;
        }

        mTitleTextView = view.findViewById(R.id.titleTextView);
        mOneButtonLayout = view.findViewById(R.id.oneButtonLayout);
        mTwoButtonLayout = view.findViewById(R.id.twoButtonLayout);
        mNegativeTextView = mTwoButtonLayout.findViewById(R.id.negativeTextView);
        mPositiveTextView = mTwoButtonLayout.findViewById(R.id.positiveTextView);
        mConfirmTextView = mOneButtonLayout.findViewById(R.id.confirmTextView);

        mPositiveTextView.setEnabled(false);

        mNegativeTextView.setOnClickListener(this);
        mPositiveTextView.setOnClickListener(this);
        mConfirmTextView.setOnClickListener(this);

        View punchMaskLayout = view.findViewById(R.id.punchMaskLayout);

        if (VersionUtils.isOverAPI16() == true)
        {
            punchMaskLayout.setBackground(new BackgroundDrawable(mContext, punchMaskLayout));
        } else
        {
            punchMaskLayout.setBackgroundDrawable(new BackgroundDrawable(mContext, punchMaskLayout));
        }

        setVisibility(false);
    }

    private void initListView(View view)
    {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
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

    public void setTitle(int resId)
    {
        if (mTitleTextView == null)
        {
            return;
        }

        mTitleTextView.setText(resId);
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
            mRecyclerView.setAdapter(mListAdapter);
        } else
        {
            mListAdapter.setAll(list);
            mListAdapter.setSelectedMode(isSelected);
            mListAdapter.notifyDataSetChanged();
        }
    }

    public Coupon getCoupon(String couponCode)
    {
        return mListAdapter.getCoupon(couponCode);
    }

    public int getCouponCount()
    {
        if (mListAdapter == null)
        {
            return 0;
        }

        return mListAdapter.getItemCount();
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
