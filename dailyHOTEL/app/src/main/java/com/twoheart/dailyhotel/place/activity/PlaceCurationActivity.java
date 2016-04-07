package com.twoheart.dailyhotel.place.activity;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

import java.lang.ref.WeakReference;

public abstract class PlaceCurationActivity extends BaseActivity implements View.OnClickListener
{
    private static final int HANDLE_MESSAGE_RESULT = 1;
    private static final int HANDLE_MESSAGE_DELAYTIME = 750;

    private TextView mResultCountView;
    private View mConfirmView;

    private Handler mHandler;

    protected abstract void initContentLayout(ViewGroup contentLayout);

    protected abstract void onComplete();

    protected abstract void onCancel();

    protected abstract void onReset();

    protected abstract void updateResultMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mIsShowStatusBar = false;

        super.onCreate(savedInstanceState);
    }

    protected void initLayout()
    {
        setContentView(R.layout.activity_curation);

        mHandler = new UpdateHandler(this);

        mResultCountView = (TextView) findViewById(R.id.resultCountView);
        mConfirmView = findViewById(R.id.confirmView);
        setConfirmOnClickListener(this);

        final View contentScrollView = findViewById(R.id.contentScrollView);
        contentScrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
                int scrollViewHeight = Util.getLCDHeight(PlaceCurationActivity.this) - Util.dpToPx(PlaceCurationActivity.this, 55)//
                    - Util.dpToPx(PlaceCurationActivity.this, 50) - Util.dpToPx(PlaceCurationActivity.this, 47) - Util.dpToPx(PlaceCurationActivity.this, 36) - rect.top;

                if (contentScrollView.getHeight() > scrollViewHeight)
                {
                    ViewGroup.LayoutParams layoutParams = contentScrollView.getLayoutParams();
                    layoutParams.height = scrollViewHeight;
                    contentScrollView.setLayoutParams(layoutParams);
                }
            }
        });

        View exitView = findViewById(R.id.exitView);
        exitView.setOnClickListener(this);

        View resetCurationView = findViewById(R.id.resetCurationView);
        resetCurationView.setOnClickListener(this);

        View closeView = findViewById(R.id.closeView);
        closeView.setOnClickListener(this);

        ViewGroup contentLayout = (ViewGroup) findViewById(R.id.contentLayout);

        initContentLayout(contentLayout);
    }

    protected void setResultMessage(String text)
    {
        if (mResultCountView == null)
        {
            return;
        }

        mResultCountView.setText(text);
    }

    protected void setConfirmEnable(boolean enabled)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setEnabled(enabled);
    }

    protected void setConfirmOnClickListener(View.OnClickListener listener)
    {
        if (mConfirmView == null)
        {
            return;
        }

        mConfirmView.setOnClickListener(listener);
    }

    protected void requestUpdateResult()
    {
        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessage(HANDLE_MESSAGE_RESULT);
    }

    protected void requestUpdateResultDelayed()
    {
        setConfirmOnClickListener(null);

        mHandler.removeMessages(HANDLE_MESSAGE_RESULT);
        mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_RESULT, HANDLE_MESSAGE_DELAYTIME);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }

    @Override
    public void onBackPressed()
    {
        setResult(RESULT_CANCELED);

        super.onBackPressed();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                onComplete();
                break;

            case R.id.closeView:
            case R.id.exitView:
                onCancel();
                break;

            case R.id.resetCurationView:
                onReset();
                break;
        }
    }

    protected DailyTextView getGridLayoutItemView(String text, int resId, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(this);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getResources().getColorStateList(R.drawable.selector_curation_textcolor));
        dailyTextView.setText(text);
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            dailyTextView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 15));
        } else
        {
            dailyTextView.setPadding(0, Util.dpToPx(this, 10), 0, Util.dpToPx(this, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    private static class UpdateHandler extends Handler
    {
        private final WeakReference<PlaceCurationActivity> mWeakReference;

        public UpdateHandler(PlaceCurationActivity activity)
        {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PlaceCurationActivity placeCurationActivity = mWeakReference.get();

            if (placeCurationActivity == null)
            {
                return;
            }

            switch (msg.what)
            {
                case HANDLE_MESSAGE_RESULT:
                    placeCurationActivity.updateResultMessage();
                    break;
            }

        }
    }
}