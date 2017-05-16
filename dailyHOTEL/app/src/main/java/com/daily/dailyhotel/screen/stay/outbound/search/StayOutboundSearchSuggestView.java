package com.daily.dailyhotel.screen.stay.outbound.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundSearchSuggestDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundSearchSuggestView extends BaseView<StayOutboundSearchSuggestView.OnEventListener, ActivityStayOutboundSearchSuggestDataBinding> implements StayOutboundSearchSuggestViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRequestSuggests(String keyword);

        void onSuggestClick(Suggest suggest);
    }

    public StayOutboundSearchSuggestView(BaseActivity baseActivity, StayOutboundSearchSuggestView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundSearchSuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.keywordEditText.addTextChangedListener(mTextWatcher);
        viewDataBinding.deleteKeywrodView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarTitle(title);
    }

    @Override
    public void setSuggestsVisibility(boolean visibility)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visibility == true)
        {
            getViewDataBinding().suggestsScrollLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().suggestsScrollLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSuggests(List<Suggest> suggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().suggestsContentsLayout.removeAllViews();

        if (suggestList != null && suggestList.size() > 0)
        {
            for (Suggest suggest : suggestList)
            {
                DailyTextView dailyTextView = new DailyTextView(getContext());
                dailyTextView.setId(R.id.textView);
                dailyTextView.setTextColor(getColor(R.color.default_text_c323232));
                dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                // 해당 구분 내용인 경우
                if (suggest.id == 0)
                {
                    dailyTextView.setText(suggest.name);
                } else
                {
                    dailyTextView.setText(suggest.display);
                    dailyTextView.setTag(suggest);
                    dailyTextView.setOnClickListener(this);
                }

                getViewDataBinding().suggestsContentsLayout.addView(dailyTextView);
            }
        }
    }

    @Override
    public void setSuggest(String suggest)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.removeTextChangedListener(mTextWatcher);
        getViewDataBinding().keywordEditText.setText(suggest);
        getViewDataBinding().keywordEditText.setSelection(getViewDataBinding().keywordEditText.length());
        getViewDataBinding().keywordEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // 자동 완성 목록에서 특정 텍스트를 클릭하는 경우
            case R.id.textView:
                Object object = v.getTag();

                if (object == null || object instanceof Suggest == false)
                {
                    return;
                }

                Suggest suggest = (Suggest) object;

                getEventListener().onSuggestClick(suggest);
                break;

            case R.id.deleteKeywrodView:
                setSuggest(null);
                break;
        }
    }

    @Override
    public void showKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywordEditText.setFocusable(true);
        getViewDataBinding().keywordEditText.setFocusableInTouchMode(true);
        getViewDataBinding().keywordEditText.requestFocus();
        getViewDataBinding().keywordEditText.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(getViewDataBinding().keywordEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 500);
    }

    @Override
    public void hideKeyboard()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getViewDataBinding().keywordEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void initToolbar(ActivityStayOutboundSearchSuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar.findViewById(R.id.toolbar));
        mDailyToolbarLayout.initToolbar(getString(R.string.label_search_stay_outbound)//
            , v -> getEventListener().onBackClick());
    }

    private TextWatcher mTextWatcher = new TextWatcher()
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
        public void afterTextChanged(Editable editable)
        {
            if (getViewDataBinding() == null)
            {
                return;
            }

            int length = editable.length();

            if (length == 0)
            {
                getViewDataBinding().deleteKeywrodView.setVisibility(View.INVISIBLE);
            } else
            {
                if (length == 1 && editable.charAt(0) == ' ')
                {
                    editable.delete(0, 1);
                    return;
                }

                if (length > 1 && editable.charAt(length - 1) == ' ')
                {
                    if (editable.charAt(length - 2) == ' ')
                    {
                        editable.delete(length - 1, length);
                    }
                    return;
                }

                getViewDataBinding().deleteKeywrodView.setVisibility(View.VISIBLE);
            }

            getEventListener().onRequestSuggests(editable.toString());
        }
    };
}
