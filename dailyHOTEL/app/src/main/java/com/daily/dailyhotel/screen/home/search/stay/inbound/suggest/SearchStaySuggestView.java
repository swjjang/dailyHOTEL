package com.daily.dailyhotel.screen.home.search.stay.inbound.suggest;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchStaySuggestDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class SearchStaySuggestView extends BaseDialogView<SearchStaySuggestView.OnEventListener, ActivitySearchStaySuggestDataBinding> //
    implements SearchStaySuggestInterface, View.OnClickListener
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

        viewDataBinding.deleteTextView.setVisibility(View.INVISIBLE);
        viewDataBinding.deleteTextView.setOnClickListener(this);

        viewDataBinding.suggestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.suggestsRecyclerView, getColor(R.color.default_over_scroll_edge));

        viewDataBinding.suggestsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private int mDistance;
            private boolean mIsHide;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mDistance = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mIsHide == true)
                {
                    mDistance = 0;
                    return;
                }

                int defaultValue = ScreenUtils.dpToPx(getContext(), 41);

                mDistance += dy;

                if (mDistance > defaultValue == true)
                {
                    mDistance = 0;
                    mIsHide = true;

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);

                    Observable.just(false).delaySubscription(1, TimeUnit.SECONDS).subscribe(isHide -> mIsHide = isHide);
                }
            }
        });

        viewDataBinding.recentlySuggestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        EdgeEffectColor.setEdgeGlowColor(viewDataBinding.recentlySuggestRecyclerView, getColor(R.color.default_over_scroll_edge));
        viewDataBinding.recentlySuggestRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            private int mDistance;
            private boolean mIsHide;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState != RecyclerView.SCROLL_STATE_DRAGGING)
                {
                    mDistance = 0;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if (mIsHide == true)
                {
                    mDistance = 0;
                    return;
                }

                int defaultValue = ScreenUtils.dpToPx(getContext(), 41);

                mDistance += dy;

                if (mDistance > defaultValue == true)
                {
                    mDistance = 0;
                    mIsHide = true;

                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);

                    Observable.just(false).delaySubscription(1, TimeUnit.SECONDS).subscribe(isHide -> mIsHide = isHide);
                }
            }
        });

        viewDataBinding.deleteRecentlySuggestLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onDeleteAllRecentlySuggest();
            }
        });

        viewDataBinding.progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.default_probressbar), PorterDuff.Mode.SRC_IN);
        setProgressBarVisible(false);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setSuggestsVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == true)
        {
            getViewDataBinding().suggestsRecyclerView.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().suggestsRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSuggests(List<Suggest> suggestList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }


    }

    @Override
    public void setSuggest(String suggest)
    {

    }

    @Override
    public void showKeyboard()
    {

    }

    @Override
    public void hideKeyboard()
    {

    }

    @Override
    public void setEmptySuggestsVisible(boolean visible)
    {

    }

    @Override
    public void setProgressBarVisible(boolean visible)
    {

    }

    @Override
    public void setRecentlySuggests(List<Suggest> suggestList)
    {

    }

    @Override
    public void setPopularAreaSuggests(List<Suggest> suggestList)
    {

    }

    @Override
    public void setKeywordEditText(String text)
    {

    }

    @Override
    public void onClick(View view)
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
