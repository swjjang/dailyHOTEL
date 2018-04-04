package com.daily.dailyhotel.screen.home.gourmet.filter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityGourmetFilterDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class GourmetFilterView extends BaseDialogView<GourmetFilterInterface.OnEventListener, ActivityGourmetFilterDataBinding> implements GourmetFilterInterface.ViewInterface, View.OnClickListener
{
    public GourmetFilterView(BaseActivity baseActivity, GourmetFilterInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        EdgeEffectColor.setEdgeGlowColor(getViewDataBinding().nestedScrollView, getColor(R.color.default_over_scroll_edge));

        initSortLayout(viewDataBinding);
        initCategoryLayout(viewDataBinding);
        iniTimeLayout(viewDataBinding);
        initAmenitiesLayout(viewDataBinding);

        getViewDataBinding().resetTextView.setOnClickListener(v -> getEventListener().onResetClick());
        getViewDataBinding().confirmTextView.setOnClickListener(v -> getEventListener().onConfirmClick());
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

    @Override
    public void defaultSortLayoutGone()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().sortInclude.regionRadioButton.setVisibility(View.GONE);
        getViewDataBinding().sortInclude.emptyRadioButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setCategory(LinkedHashMap<String, GourmetFilter.Category> categoryMap)
    {
        if (getViewDataBinding() == null || categoryMap == null)
        {
            return;
        }

        getViewDataBinding().foodGridLayout.removeAllViews();

        List<GourmetFilter.Category> categoryList = new ArrayList<>(categoryMap.values());

        for (GourmetFilter.Category category : categoryList)
        {
            DailyTextView categoryView = getGridLayoutItemView(getContext(), category);

            if (categoryView == null)
            {
                continue;
            }

            categoryView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    getEventListener().onCheckedChangedCategories(category);
                }
            });

            getViewDataBinding().foodGridLayout.addView(categoryView);
        }

        final int GOURMET_CATEGORY_COLUMN = 5;
        int categoryCount = categoryList.size();

        // 음식 종류가 COLUMN 개수보다 작으면 위치가 맞지 않는 경우가 발생해서 추가 개수를 넣어준다.
        if (categoryCount < GOURMET_CATEGORY_COLUMN)
        {
            int addViewCount = GOURMET_CATEGORY_COLUMN - categoryCount;

            for (int i = 0; i < addViewCount; i++)
            {
                DailyTextView categoryView = getGridLayoutItemView(null, null);

                if (categoryView == null)
                {
                    continue;
                }

                getViewDataBinding().foodGridLayout.addView(categoryView);
            }
        }
    }

    protected DailyTextView getGridLayoutItemView(Context context, GourmetFilter.Category category)
    {
        if (context == null)
        {
            return null;
        }

        DailyTextView dailyTextView = new DailyTextView(context);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(getColorStateList(R.color.selector_curation_textcolor));

        if (category != null)
        {
            dailyTextView.setTag(category);
            dailyTextView.setText(category.name);
            dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, getCategoryResourceId(category.code), 0, 0);
        }

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = category == null ? 1 : ScreenUtils.dpToPx(context, 74d);
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 12), 0, 0);
        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    private int getCategoryResourceId(int code)
    {
        final int[] resourceIndex = new int[]{0//
            , R.drawable.f_ic_gourmet_02_food_01//
            , R.drawable.f_ic_gourmet_02_food_02//
            , R.drawable.f_ic_gourmet_02_food_03//
            , R.drawable.f_ic_gourmet_02_food_04//
            , R.drawable.f_ic_gourmet_02_food_05//
            , R.drawable.f_ic_gourmet_02_food_06//
            , R.drawable.f_ic_gourmet_02_food_07//
            , R.drawable.f_ic_gourmet_02_food_08};

        if (code < 1 || code >= resourceIndex.length)
        {
            code = 0;
        }

        return resourceIndex[code];
    }

    @Override
    public void setSortCheck(GourmetFilter.SortType sortType)
    {
        if (getViewDataBinding() == null || sortType == null)
        {
            return;
        }

        switch (sortType)
        {
            case DEFAULT:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.regionRadioButton);
                break;

            case DISTANCE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.distanceRadioButton);
                break;

            case LOW_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.lowPriceRadioButton);
                break;

            case HIGH_PRICE:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.highPriceRadioButton);
                break;

            case SATISFACTION:
                getViewDataBinding().sortInclude.sortRadioGroup.check(R.id.satisfactionRadioButton);
                break;
        }
    }

    @Override
    public void setSortLayoutEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().sortInclude.sortRadioGroup.setEnabled(enabled);

        int childCount = getViewDataBinding().sortInclude.sortRadioGroup.getChildCount();

        for (int i = 0; i < childCount; i++)
        {
            getViewDataBinding().sortInclude.sortRadioGroup.getChildAt(i).setEnabled(enabled);
        }
    }

    @Override
    public void setCategoriesCheck(GourmetFilter.Category category)
    {
        if (getViewDataBinding() == null || category == null)
        {
            return;
        }

        int count = getViewDataBinding().foodGridLayout.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View view = getViewDataBinding().foodGridLayout.getChildAt(i);
            Object tag = view.getTag();

            if (tag != null && tag instanceof GourmetFilter.Category)
            {
                if (category.code == ((GourmetFilter.Category) tag).code)
                {
                    view.setSelected(view.isSelected() == false);
                    break;
                }
            }
        }
    }

    @Override
    public void setCategoriesCheck(HashMap<String, Integer> flagCategoryFilterMap)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int count = getViewDataBinding().foodGridLayout.getChildCount();

        for (int i = 0; i < count; i++)
        {
            View view = getViewDataBinding().foodGridLayout.getChildAt(i);
            Object tag = view.getTag();

            if (tag != null && tag instanceof GourmetFilter.Category)
            {
                view.setSelected(flagCategoryFilterMap != null && flagCategoryFilterMap.containsKey(((GourmetFilter.Category) tag).name) == true);
            } else
            {
                view.setSelected(false);
            }
        }
    }

    @Override
    public void setTimesCheck(int flagBedTypeFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().timeInclude.time0611View.setSelected((flagBedTypeFilters & GourmetFilter.FLAG_TIME_06_11) == GourmetFilter.FLAG_TIME_06_11);
        getViewDataBinding().timeInclude.time1115View.setSelected((flagBedTypeFilters & GourmetFilter.FLAG_TIME_11_15) == GourmetFilter.FLAG_TIME_11_15);
        getViewDataBinding().timeInclude.time1517View.setSelected((flagBedTypeFilters & GourmetFilter.FLAG_TIME_15_17) == GourmetFilter.FLAG_TIME_15_17);
        getViewDataBinding().timeInclude.time1721View.setSelected((flagBedTypeFilters & GourmetFilter.FLAG_TIME_17_21) == GourmetFilter.FLAG_TIME_17_21);
        getViewDataBinding().timeInclude.time2106View.setSelected((flagBedTypeFilters & GourmetFilter.FLAG_TIME_21_06) == GourmetFilter.FLAG_TIME_21_06);
    }

    @Override
    public void setAmenitiesCheck(int flagAmenitiesFilters)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().amenityInclude.parkingCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PARKING) == GourmetFilter.FLAG_AMENITIES_PARKING);
        getViewDataBinding().amenityInclude.valetCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_VALET) == GourmetFilter.FLAG_AMENITIES_VALET);
        getViewDataBinding().amenityInclude.babySeatCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_BABYSEAT) == GourmetFilter.FLAG_AMENITIES_BABYSEAT);
        getViewDataBinding().amenityInclude.privateRoomCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_PRIVATEROOM) == GourmetFilter.FLAG_AMENITIES_PRIVATEROOM);
        getViewDataBinding().amenityInclude.groupBookingCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_GROUPBOOKING) == GourmetFilter.FLAG_AMENITIES_GROUPBOOKING);
        getViewDataBinding().amenityInclude.corkageCheckView.setSelected((flagAmenitiesFilters & GourmetFilter.FLAG_AMENITIES_CORKAGE) == GourmetFilter.FLAG_AMENITIES_CORKAGE);
    }

    @Override
    public void setConfirmText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmTextView.setText(text);
    }

    @Override
    public void setConfirmEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().confirmTextView.setEnabled(enabled);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            // Time //
            case R.id.time0611View:
                getEventListener().onCheckedChangedTimes(GourmetFilter.FLAG_TIME_06_11);
                break;
            case R.id.time1115View:
                getEventListener().onCheckedChangedTimes(GourmetFilter.FLAG_TIME_11_15);
                break;
            case R.id.time1517View:
                getEventListener().onCheckedChangedTimes(GourmetFilter.FLAG_TIME_15_17);
                break;
            case R.id.time1721View:
                getEventListener().onCheckedChangedTimes(GourmetFilter.FLAG_TIME_17_21);
                break;
            case R.id.time2106View:
                getEventListener().onCheckedChangedTimes(GourmetFilter.FLAG_TIME_21_06);
                break;

            // Amenity
            case R.id.parkingTextView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_PARKING);
                break;
            case R.id.valetCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_VALET);
                break;
            case R.id.babySeatCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_BABYSEAT);
                break;
            case R.id.privateRoomCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_PRIVATEROOM);
                break;
            case R.id.groupBookingCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_GROUPBOOKING);
                break;
            case R.id.corkageCheckView:
                getEventListener().onCheckedChangedAmenities(GourmetFilter.FLAG_AMENITIES_CORKAGE);
                break;
        }
    }

    private void initToolbar(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initSortLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.sortInclude.sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
            {
                RadioButton radioButton = radioGroup.findViewById(checkedId);

                if (radioButton == null)
                {
                    return;
                }

                boolean isChecked = radioButton.isChecked();

                if (isChecked == false)
                {
                    return;
                }

                switch (checkedId)
                {
                    case R.id.regionRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.DEFAULT);
                        break;

                    case R.id.distanceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.DISTANCE);
                        break;

                    case R.id.lowPriceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.LOW_PRICE);
                        break;

                    case R.id.highPriceRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.HIGH_PRICE);
                        break;

                    case R.id.satisfactionRadioButton:
                        getEventListener().onCheckedChangedSort(GourmetFilter.SortType.SATISFACTION);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void initCategoryLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

    }

    private void iniTimeLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.timeInclude.timeRangeLayout.getChildCount();

        View view;

        for (int i = 0; i < count; i++)
        {
            view = viewDataBinding.timeInclude.timeRangeLayout.getChildAt(i);

            if (view instanceof DailyTextView)
            {
                view.setOnClickListener(this);
            } else
            {
                view.setEnabled(false);
            }
        }
    }

    private void initAmenitiesLayout(ActivityGourmetFilterDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        int count = viewDataBinding.amenityInclude.amenityGridLayout.getChildCount();

        View view;

        for (int i = 0; i < count; i++)
        {
            view = viewDataBinding.amenityInclude.amenityGridLayout.getChildAt(i);

            if (view instanceof DailyTextView)
            {
                view.setOnClickListener(this);
            } else
            {
                view.setEnabled(false);
            }
        }
    }
}
