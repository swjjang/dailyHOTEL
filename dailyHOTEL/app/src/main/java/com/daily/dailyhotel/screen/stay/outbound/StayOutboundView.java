package com.daily.dailyhotel.screen.stay.outbound;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Suggest;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityOutboundDataBinding;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayOutboundView extends BaseView<StayOutboundView.OnEventListener, ActivityOutboundDataBinding> implements StayOutboundViewInterface, View.OnClickListener
{
    private DailyToolbarLayout mDailyToolbarLayout;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onRequestSuggests(String keyword);

        void onSuggestClick(Suggest suggest);

        void onSuggestClick(String keyword);

        void onCalendarClick();
    }

    public StayOutboundView(BaseActivity baseActivity, StayOutboundView.OnEventListener listener)
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

        initToolbar(viewDataBinding);

        viewDataBinding.keywrodEditText.addTextChangedListener(new TextWatcher()
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
                    viewDataBinding.deleteKeywrodView.setVisibility(View.INVISIBLE);
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

                    viewDataBinding.deleteKeywrodView.setVisibility(View.VISIBLE);
                }

                getEventListener().onRequestSuggests(editable.toString());
            }
        });


        viewDataBinding.deleteKeywrodView.setOnClickListener(this);
        viewDataBinding.calendarTextView.setOnClickListener(this);
    }

    @Override
    public void setCalendarText(String calendarText)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().calendarTextView.setText(calendarText);
    }

    @Override
    public void setRecentlySuggests(List<Suggest> suggestList)
    {

    }

    @Override
    public void setRecentlySuggestsVisibility(boolean visibility)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visibility == true)
        {
            getViewDataBinding().recentSuggestsLayout.setVisibility(View.VISIBLE);
        } else
        {
            getViewDataBinding().recentSuggestsLayout.setVisibility(View.GONE);
        }
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
                if (suggest.id == null)
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
    public void setToolbarMenuEnable(boolean enable)
    {
        if (mDailyToolbarLayout == null)
        {
            return;
        }

        mDailyToolbarLayout.setToolbarMenuEnable(enable, enable);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.textView:
                Object object = v.getTag();

                if (object == null || object instanceof Suggest == false)
                {
                    return;
                }

                Suggest suggest = (Suggest) object;

                getEventListener().onSuggestClick(suggest);
                break;

            case R.id.calendarTextView:
                getEventListener().onCalendarClick();
                break;


            case R.id.menu1View:
                break;

            case R.id.deleteKeywrodView:
                resetKeyword();
                break;
        }
    }

    private void initToolbar(ActivityOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        mDailyToolbarLayout = new DailyToolbarLayout(getContext(), viewDataBinding.toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.label_search_stay_outbound)//
            , v -> getEventListener().finish());

        mDailyToolbarLayout.setToolbarMenu(getString(R.string.label_search), null);
        mDailyToolbarLayout.setToolbarMenuEnable(false, false);
        mDailyToolbarLayout.setToolbarMenuClickListener(this);
    }

    private void resetKeyword()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().keywrodEditText.setText(null);
    }
}
