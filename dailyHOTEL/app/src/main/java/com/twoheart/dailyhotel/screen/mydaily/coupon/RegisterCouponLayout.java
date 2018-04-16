package com.twoheart.dailyhotel.screen.mydaily.coupon;

import android.content.Context;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.daily.base.widget.DailyEditText;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.StringFilter;

/**
 * Created by android_sam on 2016. 9. 19..
 */
@Deprecated
public class RegisterCouponLayout extends BaseLayout implements View.OnClickListener, View.OnFocusChangeListener
{
    private View mTitleView;
    View mCompleteView;
    private DailyEditText mCouponEditText;

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
        mCouponEditText = view.findViewById(R.id.couponEditText);
        mCouponEditText.setDeleteButtonVisible(null);
        mCouponEditText.setOnFocusChangeListener(this);

        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowRegisterCouponFilters = new InputFilter[2];
        allowRegisterCouponFilters[0] = stringFilter.allowRegisterCouponFilter;
        allowRegisterCouponFilters[1] = new InputFilter.LengthFilter(20);

        mCouponEditText.setFilters(allowRegisterCouponFilters);

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
        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_register_coupon);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
        } else
        {
            if (editText.length() > 0)
            {
                labelView.setActivated(true);
            }

            labelView.setSelected(false);
        }
    }

    public String getInputText()
    {
        return mCouponEditText != null ? mCouponEditText.getText().toString() : "";
    }
}
