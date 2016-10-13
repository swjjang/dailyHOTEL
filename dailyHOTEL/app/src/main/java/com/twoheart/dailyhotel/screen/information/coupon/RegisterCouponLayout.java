package com.twoheart.dailyhotel.screen.information.coupon;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

/**
 * Created by android_sam on 2016. 9. 19..
 */
public class RegisterCouponLayout extends BaseLayout implements View.OnClickListener
{
    private View mTitleView;
    private View mDeleteView;
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

        mDeleteView = view.findViewById(R.id.deleteView);
        mTitleView = view.findViewById(R.id.couponTitleView);
        mCompleteView = view.findViewById(R.id.registerCouponCompleteView);
        mCouponEditText = (EditText) view.findViewById(R.id.couponEditText);

        mCouponEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() > 0)
                {
                    mDeleteView.setVisibility(View.VISIBLE);
                } else
                {
                    mDeleteView.setVisibility(View.GONE);
                }
            }
        });

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

        int length = mCouponEditText.getText().length();
        mDeleteView.setVisibility(length > 0 ? View.VISIBLE : View.GONE);
        mDeleteView.setOnClickListener(this);
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

            case R.id.deleteView:
                if (mCouponEditText != null)
                {
                    mCouponEditText.setText("");
                }
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
}
