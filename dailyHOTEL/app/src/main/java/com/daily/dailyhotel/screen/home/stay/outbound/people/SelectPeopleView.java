package com.daily.dailyhotel.screen.home.stay.outbound.people;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.People;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayOutboundPeopleDataBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectPeopleView extends BaseDialogView<SelectPeopleView.OnEventListener, ActivityStayOutboundPeopleDataBinding> //
    implements SelectPeopleViewInterface, View.OnClickListener
{
    private ChildAgeArrayAdapter[] mChildAgeArrayAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onAdultPlusClick();

        void onAdultMinusClick();

        void onChildPlusClick();

        void onChildMinusClick();

        void onSelectedChild1AgeClick(int agePosition);

        void onSelectedChild2AgeClick(int agePosition);

        void onSelectedChild3AgeClick(int agePosition);

        void onCancelClick();

        void onConfirmClick();
    }

    public SelectPeopleView(BaseActivity baseActivity, SelectPeopleView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayOutboundPeopleDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isTabletDevice() == true)
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

        mChildAgeArrayAdapter = new ChildAgeArrayAdapter[3];

        mChildAgeArrayAdapter[0] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_people, new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[0].setDropDownViewResource(R.layout.spinner_dropdown_row_people);
        viewDataBinding.child1AgeSpinner.setAdapter(mChildAgeArrayAdapter[0]);

        mChildAgeArrayAdapter[1] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_people, new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[1].setDropDownViewResource(R.layout.spinner_dropdown_row_people);
        viewDataBinding.child2AgeSpinner.setAdapter(mChildAgeArrayAdapter[1]);

        mChildAgeArrayAdapter[2] = new ChildAgeArrayAdapter(getContext(), R.layout.spinner_row_people, new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.child_age_array))));
        mChildAgeArrayAdapter[2].setDropDownViewResource(R.layout.spinner_dropdown_row_people);
        viewDataBinding.child3AgeSpinner.setAdapter(mChildAgeArrayAdapter[2]);

        viewDataBinding.child1AgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getEventListener().onSelectedChild1AgeClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        viewDataBinding.child2AgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getEventListener().onSelectedChild2AgeClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        viewDataBinding.child3AgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getEventListener().onSelectedChild3AgeClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        viewDataBinding.negativeTextView.setOnClickListener(this);
        viewDataBinding.positiveTextView.setOnClickListener(this);
    }

    @Override
    public void setToolbarTitle(String title)
    {
    }

    @Override
    public void setPeople(People people, int adultMin, int adultMax, int childMin, int childMax)
    {
        if (getViewDataBinding() == null || people == null)
        {
            return;
        }

        setAdultCount(people.numberOfAdults, adultMin, adultMax);
        setChildAgeList(people.getChildAgeList(), childMin, childMax);
    }

    @Override
    public void setAdultCount(int numberOfAdults, int min, int max)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (numberOfAdults == min)
        {
            getViewDataBinding().adultCountMinus.setEnabled(false);
            getViewDataBinding().adultCountPlus.setEnabled(true);
        } else if (numberOfAdults == max)
        {
            getViewDataBinding().adultCountMinus.setEnabled(true);
            getViewDataBinding().adultCountPlus.setEnabled(false);
        } else
        {
            getViewDataBinding().adultCountMinus.setEnabled(true);
            getViewDataBinding().adultCountPlus.setEnabled(true);
        }

        getViewDataBinding().adultCountTextView.setText(getString(R.string.label_people_number_of_person, numberOfAdults));
    }

    @Override
    public void setChildAgeList(ArrayList<Integer> childAgeList, int min, int max)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (childAgeList == null || childAgeList.size() == 0)
        {
            getViewDataBinding().childCountMinus.setEnabled(false);
            getViewDataBinding().childCountPlus.setEnabled(true);
        } else if (childAgeList.size() == max)
        {
            getViewDataBinding().childCountMinus.setEnabled(true);
            getViewDataBinding().childCountPlus.setEnabled(false);
        } else
        {
            getViewDataBinding().childCountMinus.setEnabled(true);
            getViewDataBinding().childCountPlus.setEnabled(true);
        }

        if (childAgeList == null)
        {
            getViewDataBinding().childCountTextView.setText(getString(R.string.label_people_number_of_person, 0));

            getViewDataBinding().divisionLineView.setVisibility(View.GONE);
            getViewDataBinding().child1Layout.setVisibility(View.GONE);
            getViewDataBinding().child2Layout.setVisibility(View.GONE);
            getViewDataBinding().child3Layout.setVisibility(View.GONE);
        } else
        {
            int childSize = childAgeList.size();

            getViewDataBinding().childCountTextView.setText(getString(R.string.label_people_number_of_person, childSize));

            switch (childSize)
            {
                case 3:
                    getViewDataBinding().divisionLineView.setVisibility(View.VISIBLE);

                    getViewDataBinding().child3Layout.setVisibility(View.VISIBLE);
                    getViewDataBinding().child2Layout.setVisibility(View.VISIBLE);
                    getViewDataBinding().child1Layout.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    getViewDataBinding().divisionLineView.setVisibility(View.VISIBLE);

                    getViewDataBinding().child3Layout.setVisibility(View.GONE);
                    getViewDataBinding().child2Layout.setVisibility(View.VISIBLE);
                    getViewDataBinding().child1Layout.setVisibility(View.VISIBLE);
                    break;

                case 1:
                    getViewDataBinding().divisionLineView.setVisibility(View.VISIBLE);

                    getViewDataBinding().child3Layout.setVisibility(View.GONE);
                    getViewDataBinding().child2Layout.setVisibility(View.GONE);
                    getViewDataBinding().child1Layout.setVisibility(View.VISIBLE);
                    break;

                default:
                    setChildAgeList(null, -1, -1);
                    return;
            }

            int position;
            for (int i = 0; i < childSize; i++)
            {
                position = childAgeList.get(i);
                mChildAgeArrayAdapter[i].setSelectedPosition(position);

                switch (i)
                {
                    case 0:
                        getViewDataBinding().child1AgeSpinner.setSelection(position);
                        break;

                    case 1:
                        getViewDataBinding().child2AgeSpinner.setSelection(position);
                        break;

                    case 2:
                        getViewDataBinding().child3AgeSpinner.setSelection(position);
                        break;
                }
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

            case R.id.negativeTextView:
                getEventListener().onCancelClick();
                break;

            case R.id.positiveTextView:
                getEventListener().onConfirmClick();
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

        void setSelectedPosition(int position)
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
            textView.setSelected(mSelectedPosition == position);

            if (mSelectedPosition == position)
            {
                textView.setTextColor(getColor(R.color.default_text_ceb2135));
            } else
            {
                textView.setTextColor(getColor(R.color.default_text_c323232));
            }

            return view;
        }
    }
}
