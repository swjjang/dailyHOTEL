package com.daily.dailyhotel.screen.home.stay.outbound.detail.coupon;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.Coupon;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySelectCouponDialogDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;

public class SelectStayOutboundCouponDialogView extends BaseDialogView<SelectStayOutboundCouponDialogInterface.OnEventListener, ActivitySelectCouponDialogDataBinding> implements SelectStayOutboundCouponDialogInterface.ViewInterface
{
    SelectStayOutboundCouponDialogAdapter mSelectStayOutboundCouponDialogAdapter;

    public SelectStayOutboundCouponDialogView(BaseActivity baseActivity, SelectStayOutboundCouponDialogInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void setContentView(final ActivitySelectCouponDialogDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        if (isTabletDevice() == true)
        {
            ViewGroup.LayoutParams layoutParams = viewDataBinding.dialogLayout.getLayoutParams();
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 10 / 15;
        }

        if (VersionUtils.isOverAPI16() == true)
        {
            viewDataBinding.punchMaskLayout.setBackground(new BackgroundDrawable(getContext(), viewDataBinding.punchMaskLayout));
        } else
        {
            viewDataBinding.punchMaskLayout.setBackgroundDrawable(new BackgroundDrawable(getContext(), viewDataBinding.punchMaskLayout));
        }

        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recyclerView, getColor(R.color.default_over_scroll_edge));

        setVisible(false);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

    }

    @Override
    public void setVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dialogLayout.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setCouponListDialog(String title, List<Coupon> couponList, View.OnClickListener confirmListener)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().titleTextView.setText(title);

        if (mSelectStayOutboundCouponDialogAdapter == null)
        {
            mSelectStayOutboundCouponDialogAdapter = new SelectStayOutboundCouponDialogAdapter(getContext(), new SelectStayOutboundCouponDialogAdapter.OnCouponItemListener()
            {
                @Override
                public void onDownloadClick(int position)
                {
                    getEventListener().onDownloadCouponClick(mSelectStayOutboundCouponDialogAdapter.getItem(position));
                }
            });

            getViewDataBinding().recyclerView.setAdapter(mSelectStayOutboundCouponDialogAdapter);
        }

        mSelectStayOutboundCouponDialogAdapter.setAll(couponList);
        mSelectStayOutboundCouponDialogAdapter.notifyDataSetChanged();

        getViewDataBinding().oneButtonLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().twoButtonLayout.setVisibility(View.GONE);

        getViewDataBinding().confirmTextView.setOnClickListener(confirmListener);
    }

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
