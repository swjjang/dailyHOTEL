package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

/**
 * Created by android_sam on 2016. 9. 19..
 */
public class RegisterCouponLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener, View.OnTouchListener
{
    private View mTitleView;
    private View mCompleteView;
    private EditText mCouponEditText;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRegisterCoupon(String couponCode);
    }

    public RegisterCouponLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);

        mTitleView = view.findViewById(R.id.couponTitleView);
        mCompleteView = view.findViewById(R.id.registerCouponCompleteView);
        mCouponEditText = (EditText) view.findViewById(R.id.couponEditText);
        mCouponEditText.setOnTouchListener(this);
        mCouponEditText.setOnFocusChangeListener(this);

        mCouponEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        mCompleteView.performClick();
                        return true;

                    default:
                        return false;
                }
            }
        });

        mCompleteView.setOnClickListener(this);
    }

    private void initToolbar(View view)
    {
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_register_coupon), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registerCouponCompleteView:
                String text = mCouponEditText == null ? "" : mCouponEditText.getText().toString();
                ((OnEventListener) mOnEventListener).onRegisterCoupon(text);

                hideKeyboard(v);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.couponEditText:
                setFocusLabelView(mTitleView, mCouponEditText, hasFocus);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (v instanceof EditText == false)
        {
            return false;
        }

        EditText editText = (EditText) v;

        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            Drawable[] drawables = editText.getCompoundDrawables();

            if (drawables == null || drawables[DRAWABLE_RIGHT] == null)
            {
                return false;
            }

            int withDrawable = drawables[DRAWABLE_RIGHT].getBounds().width() + editText.getCompoundDrawablePadding();

            if (event.getRawX() >= (editText.getRight() - withDrawable))
            {
                editText.setText(null);
            }
        }

        return false;
    }

    private void showKeyboard(View view)
    {
        if (view == null)
        {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void hideKeyboard(View view)
    {
        if (view == null)
        {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setFocusLabelView(View labelView, EditText editText, boolean hasFocus)
    {
        if (hasFocus == true)
        {
            labelView.setActivated(false);
            labelView.setSelected(true);

            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_ic_01_delete, 0);
        } else
        {
            if (editText.length() > 0)
            {
                labelView.setActivated(true);
            }

            labelView.setSelected(false);

            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }
}
