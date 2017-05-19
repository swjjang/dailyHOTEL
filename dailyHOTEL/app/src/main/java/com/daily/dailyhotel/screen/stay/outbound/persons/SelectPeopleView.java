package com.daily.dailyhotel.screen.stay.outbound.persons;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.People;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPersonsDataBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectPeopleView extends BaseView<SelectPeopleView.OnEventListener, ActivityStayOutboundPersonsDataBinding> //
    implements SelectPeopleViewInterface, View.OnClickListener
{
    private ChildAgeArrayAdapter[] mChildAgeArrayAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onAdultPlusClick();

        void onAdultMinusClick();

        void onChildPlusClick();

        void onChildMinusClick();
    }

    public SelectPeopleView(BaseActivity baseActivity, SelectPeopleView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPersonsDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (ScreenUtils.isTabletDevice((Activity) getContext()) == true)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 10 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(getContext()) * 13 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        viewDataBinding.getRoot().setLayoutParams(layoutParams);

        viewDataBinding.adultCountMinus.setOnClickListener(this);
        viewDataBinding.adultCountPlus.setOnClickListener(this);
        viewDataBinding.childCountMinus.setOnClickListener(this);
        viewDataBinding.childCountPlus.setOnClickListener(this);
        viewDataBinding.child1AgeLayout.setOnClickListener(this);
        viewDataBinding.child2AgeLayout.setOnClickListener(this);
        viewDataBinding.child3AgeLayout.setOnClickListener(this);

        viewDataBinding.child1AgeSpinner.setHintLayout(R.layout.spinner_row_age_hint);
        viewDataBinding.child2AgeSpinner.setHintLayout(R.layout.spinner_row_age_hint);
        viewDataBinding.child3AgeSpinner.setHintLayout(R.layout.spinner_row_age_hint);

        mChildAgeArrayAdapter = new ChildAgeArrayAdapter[3];

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getString(R.string.label_select_category));

        mChildAgeArrayAdapter[0] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_happytalk, arrayList);
        mChildAgeArrayAdapter[0].clear();
        mChildAgeArrayAdapter[0].addAll(new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[0].setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
        viewDataBinding.child1AgeSpinner.setAdapter(mChildAgeArrayAdapter[0]);

        mChildAgeArrayAdapter[1] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_happytalk, arrayList);
        mChildAgeArrayAdapter[1].clear();
        mChildAgeArrayAdapter[1].addAll(new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[1].setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
        viewDataBinding.child2AgeSpinner.setAdapter(mChildAgeArrayAdapter[1]);

        mChildAgeArrayAdapter[2] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_happytalk, arrayList);
        mChildAgeArrayAdapter[2].clear();
        mChildAgeArrayAdapter[2].addAll(new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[2].setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
        viewDataBinding.child3AgeSpinner.setAdapter(mChildAgeArrayAdapter[2]);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setPeople(People people)
    {
        if (getViewDataBinding() == null || people == null)
        {
            return;
        }

        setAdultCount(people.numberOfAdults);
        setChildAgeList(people.getChildAgeList());
    }

    @Override
    public void setAdultCount(int numberOfAdults)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().adultCountTextView.setText(getString(R.string.label_person_number_of_person, numberOfAdults));
    }

    @Override
    public void setChildAgeList(ArrayList<Integer> childAgeList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (childAgeList == null)
        {
            getViewDataBinding().childCountTextView.setText(getString(R.string.label_person_number_of_person, 0));

            getViewDataBinding().divisionLineView.setVisibility(View.GONE);
            getViewDataBinding().child1Layout.setVisibility(View.GONE);
            getViewDataBinding().child2Layout.setVisibility(View.GONE);
            getViewDataBinding().child3Layout.setVisibility(View.GONE);
        } else
        {
            int childSize = childAgeList.size();

            getViewDataBinding().childCountTextView.setText(getString(R.string.label_person_number_of_person, childSize));

            switch (childSize)
            {
                case 3:
                    getViewDataBinding().child3Layout.setVisibility(View.VISIBLE);

                    if (childAgeList.get(2) != -1)
                    {
                        mChildAgeArrayAdapter[2].setSelectedPosition(childAgeList.get(2));
                    }

                case 2:
                    getViewDataBinding().child2Layout.setVisibility(View.VISIBLE);

                    if (childAgeList.get(1) != -1)
                    {
                        mChildAgeArrayAdapter[1].setSelectedPosition(childAgeList.get(1));
                    }

                case 1:
                    getViewDataBinding().child1Layout.setVisibility(View.VISIBLE);

                    if (childAgeList.get(0) != -1)
                    {
                        mChildAgeArrayAdapter[0].setSelectedPosition(childAgeList.get(0));
                    }
                    break;

                default:
                    setChildAgeList(null);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.adultCountMinus:
                getEventListener().onAdultMinusClick();
                break;

            case R.id.adultCountPlus:
                getEventListener().onAdultPlusClick();
                break;

            case R.id.childCountMinus:
                getEventListener().onChildMinusClick();
                break;

            case R.id.childCountPlus:
                getEventListener().onChildPlusClick();
                break;
        }
    }

    private class ChildAgeArrayAdapter extends ArrayAdapter<String>
    {
        private int mSelectedPosition;

        public ChildAgeArrayAdapter(Context context, int resourceId, ArrayList<String> arrayList)
        {
            super(context, resourceId, arrayList);

            mSelectedPosition = -1;
        }

        private void setSelectedPosition(int position)
        {
            mSelectedPosition = position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);

            String age = getItem(position);

            TextView textView = (TextView) view;
            textView.setText(age);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            String age = getItem(position);

            TextView textView = (TextView) view;
            textView.setText(age);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setSelected(mSelectedPosition == position);

            if (mSelectedPosition == position)
            {
                textView.setTextColor(getColor(R.color.default_text_cb70038));
            } else
            {
                textView.setTextColor(getColor(R.color.default_text_c323232));
            }

            return view;
        }
    }
}
