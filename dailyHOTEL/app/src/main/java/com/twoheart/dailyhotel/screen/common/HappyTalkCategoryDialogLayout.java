package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HappyTalkCategory;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyHintSpinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HappyTalkCategoryDialogLayout extends BaseLayout implements View.OnClickListener
{
    private LinkedHashMap<String, Pair<String, String>> mMainCategoryMap;
    private LinkedHashMap<String, List<Pair<String, String>>> mSubCategoryMap;

    private DailyHintSpinner mMainCategorySpinner;
    private DailyHintSpinner mSubCategorySpinner;

    private CategoryArrayAdapter mMainCategoryArrayAdapter;
    private CategoryArrayAdapter mSubCategoryArrayAdapter;

    private String mSelectedMainCategoryId, mSelectedSubCategoryId;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHappyTalk(String mainId, String subId);
    }

    public HappyTalkCategoryDialogLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (Util.isTabletDevice(mContext) == false)
        {
            layoutParams.width = Util.getLCDWidth(mContext) * 13 / 15;
        } else
        {
            layoutParams.width = Util.getLCDWidth(mContext) * 10 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(layoutParams);

        setVisibility(View.INVISIBLE);

    }

    public void setCategory(String category)
    {
        parseCategory(category);

        setVisibility(View.VISIBLE);
        initCategoryLayout(mRootView);
    }

    private void initCategoryLayout(View view)
    {
        mMainCategorySpinner = (DailyHintSpinner) view.findViewById(R.id.mainCategorySpinner);
        mSubCategorySpinner = (DailyHintSpinner) view.findViewById(R.id.subCategorySpinner);

        View negativeTextView = view.findViewById(R.id.negativeTextView);
        final View positiveTextView = view.findViewById(R.id.positiveTextView);
        positiveTextView.setEnabled(false);

        negativeTextView.setOnClickListener(this);
        positiveTextView.setOnClickListener(this);

        mMainCategorySpinner.setHintLayout(R.layout.list_row_coupon_spinner);
        mSubCategorySpinner.setHintLayout(R.layout.list_row_coupon_spinner);

        mMainCategoryArrayAdapter = new CategoryArrayAdapter(mContext, R.layout.list_row_coupon_spinner, new ArrayList<>(mMainCategoryMap.values()));
        mMainCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mMainCategorySpinner.setAdapter(mMainCategoryArrayAdapter);
        mMainCategorySpinner.setHintLayout(R.layout.list_row_coupon_spinner);

        ArrayList<String> subCategoryList = new ArrayList<>();
        subCategoryList.add(0, mContext.getString(R.string.label_select_sub_category));
        mSubCategoryArrayAdapter = new CategoryArrayAdapter(mContext, R.layout.list_row_coupon_spinner, subCategoryList);
        mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mSubCategorySpinner.setAdapter(mSubCategoryArrayAdapter);
        mSubCategorySpinner.setEnabled(false);

        mMainCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position < 0)
                {
                    return;
                }

                mMainCategoryArrayAdapter.setSelectedPosition(position);

                Pair<String, String> pair = mMainCategoryArrayAdapter.getItem(position);

                mSelectedMainCategoryId = pair.first;
                List<Pair<String, String>> pairList = mSubCategoryMap.get(mSelectedMainCategoryId);

                mSubCategoryArrayAdapter.clear();
                mSubCategoryArrayAdapter.addAll(new ArrayList(pairList));
                mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
                mSubCategorySpinner.setAdapter(mSubCategoryArrayAdapter);
                mSubCategorySpinner.setEnabled(true);

                positiveTextView.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        mSubCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (position < 0)
                {
                    return;
                }

                mSubCategoryArrayAdapter.setSelectedPosition(position);

                Pair<String, String> pair = mSubCategoryArrayAdapter.getItem(position);

                mSelectedSubCategoryId = pair.first;

                positiveTextView.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.negativeTextView:
                mOnEventListener.finish();
                break;

            case R.id.positiveTextView:
                ((OnEventListener) mOnEventListener).onHappyTalk(mSelectedMainCategoryId, mSelectedSubCategoryId);
                break;
        }
    }

    private void parseCategory(String categoryData)
    {
        try
        {
            List<HappyTalkCategory> happyTalkCategoryList = LoganSquare.parseList(categoryData, HappyTalkCategory.class);

            int size = happyTalkCategoryList.size();

            // 대분류
            if (mMainCategoryMap == null)
            {
                mMainCategoryMap = new LinkedHashMap<>();
            }

            // 중분류
            if (mSubCategoryMap == null)
            {
                mSubCategoryMap = new LinkedHashMap<>();
            }

            mMainCategoryMap.clear();
            mSubCategoryMap.clear();

            for (HappyTalkCategory happyTalkCategory : happyTalkCategoryList)
            {
                mMainCategoryMap.put(happyTalkCategory.id, new Pair(happyTalkCategory.id, happyTalkCategory.name));

                List<Pair<String, String>> subCategoryList = mSubCategoryMap.get(happyTalkCategory.id);

                if (subCategoryList == null)
                {
                    subCategoryList = new ArrayList<>();
                    mSubCategoryMap.put(happyTalkCategory.id, subCategoryList);
                }

                subCategoryList.add(new Pair(happyTalkCategory.id2, happyTalkCategory.name2));
            }

        } catch (Exception e)
        {
            ExLog.d(e.toString());
            // 에러가 나면 특정 유형으로 상담이 되도로 하는 것이 필요할것 같음.
        }
    }

    private class CategoryArrayAdapter extends ArrayAdapter<Pair<String, String>>
    {
        private int mSelectedPosition;

        public CategoryArrayAdapter(Context context, int resourceId, ArrayList arrayList)
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

            Pair<String, String> pair = getItem(position);

            TextView textView = (TextView) view;
            textView.setText(pair.second);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            Pair<String, String> pair = getItem(position);

            TextView textView = (TextView) view;
            textView.setText(pair.second);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setSelected(mSelectedPosition == position);

            if (mSelectedPosition == position)
            {
                textView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));
            } else
            {
                textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            }

            return view;
        }
    }
}