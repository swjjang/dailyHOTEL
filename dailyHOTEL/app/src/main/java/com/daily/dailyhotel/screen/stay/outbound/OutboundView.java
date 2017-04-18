package com.daily.dailyhotel.screen.stay.outbound;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityOutboundDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class OutboundView extends BaseView<OutboundView.OnEventListener, ActivityOutboundDataBinding> implements OutboundViewInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
        void onSearchKeyword(String keyword);

        void onReset();
    }

    public OutboundView(BaseActivity baseActivity, OutboundView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void initLayout(final ActivityOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.searchEditText.addTextChangedListener(new TextWatcher()
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
                int length = editable.length();

                if (length == 0)
                {
                    viewDataBinding.deleteView.setVisibility(View.INVISIBLE);
                    getEventListener().onReset();
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

                    viewDataBinding.deleteView.setVisibility(View.VISIBLE);
                    getEventListener().onSearchKeyword(editable.toString());
                }
            }
        });

    }

    @Override
    public void onReset()
    {


    }

    @Override
    public void showRecentlyKeyword()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recentSearchLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRecentlyKeyword()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().recentSearchLayout.setVisibility(View.GONE);
    }

    private void initToolbar(ActivityOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_search)//
            , v -> getEventListener().finish());
    }
}
