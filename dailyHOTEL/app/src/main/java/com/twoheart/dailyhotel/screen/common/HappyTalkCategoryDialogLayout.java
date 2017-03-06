package com.twoheart.dailyhotel.screen.common;

import android.app.Activity;
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

    private String mSelectedPlaceType, mSelectedMainCategoryId;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHappyTalk(String placeType, String mainId);
    }

    public HappyTalkCategoryDialogLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (mContext instanceof Activity && Util.isTabletDevice((Activity) mContext) == true)
        {
            layoutParams.width = Util.getLCDWidth(mContext) * 10 / 15;
        } else
        {
            layoutParams.width = Util.getLCDWidth(mContext) * 13 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(layoutParams);

        setVisibility(View.INVISIBLE);

    }

    public void setCategory(HappyTalkCategoryDialog.CallScreen callScreen, String category)
    {
        parseCategory(category);

        setVisibility(View.VISIBLE);
        initCategoryLayout(mRootView, callScreen);
    }

    private void initCategoryLayout(View view, HappyTalkCategoryDialog.CallScreen callScreen)
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

        mMainCategorySpinner.setPromptId(R.string.label_select_main_category);

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

                mSelectedPlaceType = pair.first;
                List<Pair<String, String>> pairList = mSubCategoryMap.get(mSelectedPlaceType);

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

                mSelectedMainCategoryId = pair.first;

                positiveTextView.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });

        switch (callScreen)
        {
            case SCREEN_STAY_DETAIL:
            case SCREEN_STAY_BOOKING:
            case SCREEN_STAY_PAMENT_WAIT:
                mMainCategorySpinner.setSelection(0);
                break;

            case SCREEN_GOURMET_DETAIL:
            case SCREEN_GOURMET_BOOKING:
            case SCREEN_GOURMET_PAMENT_WAIT:
                mMainCategorySpinner.setSelection(1);
                break;

            case SCREEN_FAQ:
            case SCREEN_CONTACT_US:
            case SCREEN_STAY_REFUND:
            default:

                break;
        }
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
                ((OnEventListener) mOnEventListener).onHappyTalk(mSelectedPlaceType, mSelectedMainCategoryId);
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

            final String STAY_PREFIX = "호텔";
            final String GOURMET_PREFIX = "고메";

            mMainCategoryMap.put(STAY_PREFIX, new Pair(STAY_PREFIX, STAY_PREFIX));
            mMainCategoryMap.put(GOURMET_PREFIX, new Pair(GOURMET_PREFIX, GOURMET_PREFIX));

            List<Pair<String, String>> subStayCategoryList = new ArrayList<>();
            mSubCategoryMap.put(STAY_PREFIX, subStayCategoryList);

            List<Pair<String, String>> subGourmetCategoryList = new ArrayList<>();
            mSubCategoryMap.put(GOURMET_PREFIX, subGourmetCategoryList);


            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

            for (HappyTalkCategory happyTalkCategory : happyTalkCategoryList)
            {
                String value = linkedHashMap.get(happyTalkCategory.id);

                if (Util.isTextEmpty(value) == true)
                {
                    linkedHashMap.put(happyTalkCategory.id, happyTalkCategory.name);

                    if (happyTalkCategory.name.startsWith(STAY_PREFIX) == true)
                    {
                        subStayCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name.substring(STAY_PREFIX.length())));
                    } else if (happyTalkCategory.name.startsWith(GOURMET_PREFIX) == true)
                    {
                        subGourmetCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name.substring(GOURMET_PREFIX.length())));
                    } else
                    {
                        subStayCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name));
                        subGourmetCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name));
                    }
                }
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