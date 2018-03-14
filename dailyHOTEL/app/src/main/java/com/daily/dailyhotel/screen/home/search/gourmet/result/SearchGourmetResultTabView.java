package com.daily.dailyhotel.screen.home.search.gourmet.result;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.screen.home.search.gourmet.result.campaign.SearchGourmetCampaignTagListFragment;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchGourmetResultTabDataBinding;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SearchGourmetResultTabView extends BaseDialogView<SearchGourmetResultTabInterface.OnEventListener, ActivitySearchGourmetResultTabDataBinding> implements SearchGourmetResultTabInterface.ViewInterface
{
    private RadiusArrayAdapter mRadiusArrayAdapter;

    public SearchGourmetResultTabView(BaseActivity baseActivity, SearchGourmetResultTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        CharSequence[] strings = getContext().getResources().getTextArray(R.array.search_result_distance_array);
        mRadiusArrayAdapter = new RadiusArrayAdapter(getContext(), R.layout.list_row_search_result_spinner, strings);
        mRadiusArrayAdapter.setDropDownViewResource(R.layout.list_row_search_result_sort_dropdown_item);

        viewDataBinding.toolbarView.setRadiusSpinnerAdapter(mRadiusArrayAdapter);

        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                getEventListener().onResearchClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                RadiusArrayAdapter radiusArrayAdapter = (RadiusArrayAdapter) getViewDataBinding().toolbarView.getRadiusSpinnerAdapter();
                radiusArrayAdapter.setSelection(position);

                getEventListener().onChangedRadius(getSpinnerRadiusValue(position));
            }

            private float getSpinnerRadiusValue(int position)
            {
                if (getViewDataBinding() == null)
                {
                    return 0.0f;
                }

                switch (position)
                {
                    case 0:
                        return 0.5f;

                    case 1:
                        return 1.0f;

                    case 2:
                        return 3.0f;

                    case 3:
                        return 5.0f;

                    case 4:
                    default:
                        return SearchGourmetResultTabPresenter.DEFAULT_RADIUS;
                }
            }
        });
    }

    @Override
    public void setViewType(SearchGourmetResultTabPresenter.ViewType viewType)
    {

    }

    @Override
    public void setToolbarDateText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setSubTitleText(text);
    }

    @Override
    public void setFloatingActionViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public Observable<BasePagerFragment> setCampaignTagFragment()
    {
        getViewDataBinding().viewPager.setOffscreenPageLimit(1);
        getViewDataBinding().viewPager.setAdapter(null);
        getViewDataBinding().viewPager.removeAllViews();

        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchGourmetCampaignTagListFragment();
        basePagerFragment.setOnFragmentEventListener(new SearchGourmetCampaignTagListFragment.OnEventListener()
        {
            @Override
            public void onResearchClick()
            {
                getEventListener().onResearchClick();
            }

            @Override
            public void onFinishAndRefresh()
            {
                getEventListener().onFinishAndRefresh();
            }
        });

        fragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(fragmentPagerAdapter);

        return basePagerFragment.getCompleteCreatedObservable().map(new Function()
        {
            @Override
            public BasePagerFragment apply(Object o) throws Exception
            {
                return basePagerFragment;
            }
        });
    }

    @Override
    public Observable<Boolean> setSearchResultFragment()
    {
        return null;
    }

    private class RadiusArrayAdapter extends ArrayAdapter<CharSequence>
    {
        private int mSelectedPosition;

        public RadiusArrayAdapter(Context context, int resourceId, CharSequence[] list)
        {
            super(context, resourceId, list);
        }

        public void setSelection(int position)
        {
            mSelectedPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            if (view != null)
            {
                TextView textView = (TextView) view;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                textView.setSelected(mSelectedPosition == position);

                if (mSelectedPosition == position)
                {
                    textView.setTextColor(getColor(R.color.default_text_cb70038));
                } else
                {
                    textView.setTextColor(getColor(R.color.default_text_c323232));
                }
            }

            return view;
        }
    }
}
