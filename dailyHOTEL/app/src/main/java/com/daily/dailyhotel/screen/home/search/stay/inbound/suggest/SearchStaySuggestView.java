package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.databinding.ActivitySearchStaySuggestDataBinding;

public class SearchStaySuggestView extends BaseDialogView<SearchStaySuggestView.OnEventListener, ActivitySearchStaySuggestDataBinding> implements SearchStaySuggestInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchSuggest(String keyword);

        void onSuggestClick(Suggest suggest);

        void onRecentlySuggestClick(Suggest suggest);

        void onPopularSuggestClick(Suggest suggest);

        void onDeleteAllRecentlySuggest();
    }

    public SearchStaySuggestView(BaseActivity baseActivity, SearchStaySuggestView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchStaySuggestDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.keywordEditText.setBackgroundDrawable(null);
        viewDataBinding.keywordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        viewDataBinding.keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        if (DailyTextUtils.isTextEmpty(v.getText().toString()) == false)
                        {
                            getEventListener().onSearchSuggest(v.getText().toString());
                        }
                        return true;

                    default:
                        return false;
                }
            }
        });

        viewDataBinding.backImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getEventListener().onBackClick();
            }
        });

        viewDataBinding.keywordEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void setToolbarTitle(String title)
    {
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
                getViewDataBinding().deleteTextView.setVisibility(View.INVISIBLE);
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

                getViewDataBinding().deleteTextView.setVisibility(View.VISIBLE);
            }

            getEventListener().onSearchSuggest(editable.toString());
        }
    };
}
