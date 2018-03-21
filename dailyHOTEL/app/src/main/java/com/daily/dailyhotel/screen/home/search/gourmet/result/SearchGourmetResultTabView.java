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
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.screen.home.search.gourmet.result.campaign.SearchGourmetCampaignTagListFragment;
import com.daily.dailyhotel.screen.home.search.gourmet.result.search.SearchGourmetResultListFragment;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailySearchResultEmptyView;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchGourmetResultTabDataBinding;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SearchGourmetResultTabView extends BaseDialogView<SearchGourmetResultTabInterface.OnEventListener, ActivitySearchGourmetResultTabDataBinding> implements SearchGourmetResultTabInterface.ViewInterface
{
    private BaseFragmentPagerAdapter<BasePagerFragment> mFragmentPagerAdapter;
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
        initEmptyView(viewDataBinding);

        getViewDataBinding().viewPager.setOffscreenPageLimit(1);

        viewDataBinding.floatingActionView.setOnViewOptionClickListener(v -> getEventListener().onViewTypeClick());
        viewDataBinding.floatingActionView.setOnFilterOptionClickListener(v -> getEventListener().onFilterClick());
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

    private void initEmptyView(ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.emptyView.setImage(R.drawable.no_gourmet_ic);
        viewDataBinding.emptyView.setMessage(R.string.message_searchresult_gourmet_empty_subtitle);
        viewDataBinding.emptyView.setBottomLeftButton(R.drawable.vector_search_shortcut_01_stay, R.string.label_searchresult_search_stay);
        viewDataBinding.emptyView.setBottomRightButton(R.drawable.vector_search_shortcut_02_ob, R.string.label_searchresult_search_stayoutbound);

        viewDataBinding.emptyView.setOnEventListener(new DailySearchResultEmptyView.OnEventListener()
        {
            @Override
            public void onCampaignTagClick(CampaignTag campaignTag)
            {
                getEventListener().onCampaignTagClick(campaignTag);
            }

            @Override
            public void onBottomLeftButtonClick()
            {
                getEventListener().onStayClick();
            }

            @Override
            public void onBottomRightButtonClick()
            {
                getEventListener().onStayOutboundClick();
            }
        });
    }

    @Override
    public void setViewType(SearchGourmetResultTabPresenter.ViewType viewType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.LIST);
                break;

            case MAP:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.MAP);
                break;
        }
    }

    @Override
    public void setToolbarTitleImageResource(int resId)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleImageResource(resId);
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
    public void setToolbarRadiusSpinnerVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setRadiusSpinnerVisible(visible);
    }

    @Override
    public void setRadiusSpinnerSelection(float radius)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position = getRadiusPosition(radius);

        getViewDataBinding().toolbarView.setRadiusSpinnerSelection(position);
    }

    private int getRadiusPosition(float radius)
    {
        if (radius > 5.0f)
        {
            return 4; // 10km
        } else if (radius > 3.0f)
        {
            return 3; // 5km
        } else if (radius > 1.0f)
        {
            return 2; // 3km
        } else if (radius > 0.5f)
        {
            return 1; // 1km
        } else
        {
            return 0; // 0.5km
        }
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
    public void setOptionFilterSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setFilterOptionSelected(selected);
    }

    @Override
    public Observable<BasePagerFragment> setCampaignTagFragment()
    {
        clearViewPager();

        mFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchGourmetCampaignTagListFragment();
        basePagerFragment.setOnFragmentEventListener(new SearchGourmetCampaignTagListFragment.OnEventListener()
        {
            @Override
            public void setEmptyViewVisible(boolean visible)
            {
                getEventListener().setEmptyViewVisible(visible);
            }

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

        mFragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);

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
    public Observable<BasePagerFragment> setSearchResultFragment()
    {
        clearViewPager();

        mFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchGourmetResultListFragment();
        basePagerFragment.setOnFragmentEventListener(new SearchGourmetResultListFragment.OnEventListener()
        {
            @Override
            public void setEmptyViewVisible(boolean visible)
            {
                getEventListener().setEmptyViewVisible(visible);
            }

            @Override
            public void onResearchClick()
            {
                getEventListener().onResearchClick();
            }

            @Override
            public void onFilterClick()
            {
                getEventListener().onFilterClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onRadiusClick()
            {
                getViewDataBinding().toolbarView.showRadiusSpinnerPopup();
            }
        });

        mFragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);

        return basePagerFragment.getCompleteCreatedObservable().map(new Function()
        {
            @Override
            public BasePagerFragment apply(Object o) throws Exception
            {
                return basePagerFragment;
            }
        });
    }

    private void clearViewPager()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().viewPager.setAdapter(null);
        getViewDataBinding().viewPager.removeAllViews();

        if (mFragmentPagerAdapter != null)
        {
            mFragmentPagerAdapter.removeAll();
            mFragmentPagerAdapter = null;
        }
    }

    @Override
    public void setEmptyViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyViewCampaignTagVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setCampaignTagVisible(visible);
    }

    @Override
    public void setEmptyViewCampaignTag(String title, List<CampaignTag> campaignTagList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setCampaignTag(title, campaignTagList);
    }

    @Override
    public boolean onFragmentBackPressed()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return false;
        }

        return mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).onBackPressed();
    }

    @Override
    public void refreshCurrentFragment()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return;
        }

        mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).onRefresh();
    }

    @Override
    public void removeAllFragment()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        clearViewPager();
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
