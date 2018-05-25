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

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.widget.DailyHintSpinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HappyTalkCategoryDialogLayout extends BaseLayout implements View.OnClickListener
{
    private LinkedHashMap<String, Pair<String, String>> mMainCategoryMap;
    LinkedHashMap<String, List<Pair<String, String>>> mSubCategoryMap;

    private DailyHintSpinner mMainCategorySpinner;
    DailyHintSpinner mSubCategorySpinner;

    CategoryArrayAdapter mMainCategoryArrayAdapter;
    CategoryArrayAdapter mSubCategoryArrayAdapter;

    String mSelectedPlaceType, mSelectedMainCategoryId;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onHappyTalk(String placeType, String mainId);

        void onCancel();
    }

    public HappyTalkCategoryDialogLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (mContext instanceof Activity && ScreenUtils.isTabletDevice((Activity) mContext) == true)
        {
            layoutParams.width = ScreenUtils.getScreenWidth(mContext) * 10 / 15;
        } else
        {
            layoutParams.width = ScreenUtils.getScreenWidth(mContext) * 13 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(layoutParams);
    }

    public void setCategory(HappyTalkCategoryDialog.CallScreen callScreen, LinkedHashMap<String, Pair<String, String>> mainCategoryMap, LinkedHashMap<String, List<Pair<String, String>>> subCategoryMap)
    {
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

        mMainCategoryMap.putAll(mainCategoryMap);
        mSubCategoryMap.putAll(subCategoryMap);

        initCategoryLayout(mRootView, callScreen);
    }

    private void initCategoryLayout(View view, HappyTalkCategoryDialog.CallScreen callScreen)
    {
        mMainCategorySpinner = view.findViewById(R.id.mainCategorySpinner);
        mSubCategorySpinner = view.findViewById(R.id.subCategorySpinner);

        View negativeTextView = view.findViewById(R.id.negativeTextView);
        final View positiveTextView = view.findViewById(R.id.positiveTextView);
        positiveTextView.setEnabled(false);

        negativeTextView.setOnClickListener(this);
        positiveTextView.setOnClickListener(this);

        mMainCategorySpinner.setHintLayout(R.layout.spinner_row_happytalk_hint);
        mSubCategorySpinner.setHintLayout(R.layout.spinner_row_happytalk_hint);

        mMainCategoryArrayAdapter = new CategoryArrayAdapter(mContext, R.layout.spinner_row_happytalk, new ArrayList<>(mMainCategoryMap.values()));
        mMainCategoryArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
        mMainCategorySpinner.setAdapter(mMainCategoryArrayAdapter);

        ArrayList<Pair<String, String>> subCategoryList = new ArrayList<>();
        subCategoryList.add(new Pair(mContext.getString(R.string.label_select_category), mContext.getString(R.string.label_select_category)));
        mSubCategoryArrayAdapter = new CategoryArrayAdapter(mContext, R.layout.spinner_row_happytalk, subCategoryList);
        mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
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
                mSubCategoryArrayAdapter.setSelectedPosition(-1);
                mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_row_happytalk);
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
            case SCREEN_STAY_PAYMENT_WAIT:
            case SCREEN_STAY_OUTBOUND_BOOKING:
            case SCREEN_STAY_OUTBOUND_DETAIL:
                mMainCategorySpinner.setSelection(0);
                break;

            case SCREEN_GOURMET_DETAIL:
            case SCREEN_GOURMET_BOOKING:
            case SCREEN_GOURMET_PAYMENT_WAIT:
                mMainCategorySpinner.setSelection(1);
                break;

            case SCREEN_STAY_REFUND:
            case SCREEN_STAY_OUTBOUND_REFUND:
                mMainCategorySpinner.setSelection(0);
                break;

            case SCREEN_FAQ:
            case SCREEN_CONTACT_US:
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
                ((OnEventListener) mOnEventListener).onCancel();
                break;

            case R.id.positiveTextView:
                ((OnEventListener) mOnEventListener).onHappyTalk(mSelectedPlaceType, mSelectedMainCategoryId);
                break;
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

        void setSelectedPosition(int position)
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
                textView.setTextColor(mContext.getResources().getColor(R.color.default_text_ceb2135));
            } else
            {
                textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
            }

            return view;
        }
    }
}