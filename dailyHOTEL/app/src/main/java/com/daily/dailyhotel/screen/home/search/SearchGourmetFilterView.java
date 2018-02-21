package com.daily.dailyhotel.screen.home.search;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ViewSearchGourmetFilterDataBinding;

public class SearchGourmetFilterView extends ConstraintLayout implements View.OnClickListener
{
    private ViewSearchGourmetFilterDataBinding mViewDataBinding;

    private OnGourmetFilterListener mFilterListener;

    interface OnGourmetFilterListener
    {
        void onSuggestClick();

        void onCalendarClick();

        void onSearchClick();
    }

    public SearchGourmetFilterView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public SearchGourmetFilterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public SearchGourmetFilterView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_search_gourmet_filter_data, this, true);

        mViewDataBinding.suggestBackgroundView.setOnClickListener(this);
        mViewDataBinding.calendarBackgroundView.setOnClickListener(this);
        mViewDataBinding.searchTextView.setOnClickListener(this);
    }

    public void setOnFilterListener(OnGourmetFilterListener listener)
    {
        mFilterListener = listener;
    }

    public void setSuggestText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.suggestTextView.setText(text);
    }

    public void setCalendarText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.calendarTextView.setText(text);
    }

    public void setSearchEnabled(boolean enabled)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.searchTextView.setEnabled(enabled);
    }

    @Override
    public void onClick(View v)
    {
        if (mFilterListener == null)
        {
            return;
        }

        switch (v.getId())
        {
            case R.id.suggestBackgroundView:
                mFilterListener.onSuggestClick();
                break;

            case R.id.calendarBackgroundView:
                mFilterListener.onCalendarClick();
                break;

            case R.id.searchTextView:
                mFilterListener.onSearchClick();
                break;
        }
    }
}
