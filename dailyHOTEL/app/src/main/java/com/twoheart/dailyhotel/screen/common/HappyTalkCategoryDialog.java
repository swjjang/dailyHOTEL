package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.HappyTalkCategory;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailySpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappyTalkCategoryDialog extends BaseActivity
{
    private LinkedHashMap<String, String> mMainCategoryMap;
    private LinkedHashMap<String, List<Pair<String, String>>> mSubCategoryMap;

    private DailySpinner mMainCategorySpinner;
    private DailySpinner mSubCategorySpinner;

    private CategoryArrayAdapter mMainCategoryArrayAdapter;
    private CategoryArrayAdapter mSubCategoryArrayAdapter;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, HappyTalkCategoryDialog.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null)
        {
        } else
        {
            finish();
        }

        initLayout();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_happytalk_category_dialog);

        View rootLayout = findViewById(R.id.rootLayout);
        ViewGroup.LayoutParams layoutParams = rootLayout.getLayoutParams();

        if (Util.isTabletDevice(this) == false)
        {
            layoutParams.width = Util.getLCDWidth(this) * 13 / 15;
        } else
        {
            layoutParams.width = Util.getLCDWidth(this) * 10 / 15;
        }

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        rootLayout.setLayoutParams(layoutParams);

        String happyTalkCategory = DailyPreference.getInstance(this).getHappyTalkCategory();

        // 해피톡 상담유형을 받은적이 없는 경우
        if (Util.isTextEmpty(happyTalkCategory) == true)
        {
            lockUI();

            DailyMobileAPI.getInstance(this).requestHappyTalkCategory(mNetworkTag, new Callback<JSONObject>()
            {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
                {
                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        try
                        {
                            JSONObject jsonObjectData = response.body();

                            if ("success".equalsIgnoreCase(jsonObjectData.getString("code")) == true)
                            {
                                JSONObject jsonObjectResults = jsonObjectData.getJSONObject("results");
                                JSONArray jsonArray = jsonObjectResults.getJSONArray("assign");

                                String happyTalkCategory = jsonArray.toString();
                                DailyPreference.getInstance(HappyTalkCategoryDialog.this).setHappyTalkCategory(happyTalkCategory);

                                parseCategory(happyTalkCategory);

                                initCategoryLayout();
                            }

                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                        unLockUI();
                    } else
                    {

                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {

                }
            });
        } else
        {
            parseCategory(happyTalkCategory);

            initCategoryLayout();
        }
    }

    private void initCategoryLayout()
    {
        mMainCategorySpinner = (DailySpinner) findViewById(R.id.mainCategorySpinner);
        mSubCategorySpinner = (DailySpinner) findViewById(R.id.subCategorySpinner);

        mMainCategorySpinner.setLayout(R.layout.list_row_coupon_spinner);
        mSubCategorySpinner.setLayout(R.layout.list_row_coupon_spinner);

        mMainCategoryArrayAdapter = new CategoryArrayAdapter(this, R.layout.list_row_coupon_spinner, new ArrayList<>(mMainCategoryMap.values()));
        mMainCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mMainCategorySpinner.setAdapter(mMainCategoryArrayAdapter);
        mMainCategorySpinner.setLayout(R.layout.list_row_coupon_spinner);

        ArrayList<String> subCategoryList = new ArrayList<>();
        subCategoryList.add(0, getString(R.string.label_select_sub_category));
        mSubCategoryArrayAdapter = new CategoryArrayAdapter(HappyTalkCategoryDialog.this, R.layout.list_row_coupon_spinner, subCategoryList);
        mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
        mSubCategorySpinner.setAdapter(mSubCategoryArrayAdapter);
        mSubCategorySpinner.setEnabled(false);

        mMainCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (mMainCategoryArrayAdapter.getSelectedPosition() == position)
                {
                    return;
                }

                mMainCategoryArrayAdapter.setSelectedPosition(position);

                ArrayList<String> setArrayList = new ArrayList<>(mMainCategoryMap.keySet());
                List<Pair<String, String>> pairList = mSubCategoryMap.get(setArrayList.get(position));

                mSubCategoryArrayAdapter = new CategoryArrayAdapter(HappyTalkCategoryDialog.this, R.layout.list_row_coupon_spinner, new ArrayList(pairList));
                mSubCategoryArrayAdapter.setDropDownViewResource(R.layout.list_row_coupon_sort_dropdown_item);
                mSubCategorySpinner.setAdapter(mSubCategoryArrayAdapter);
                mSubCategorySpinner.setEnabled(true);
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

                mSubCategoryArrayAdapter.setSelectedPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
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
                mMainCategoryMap.put(happyTalkCategory.id, happyTalkCategory.name);

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

    private class CategoryArrayAdapter extends ArrayAdapter
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

        private int getSelectedPosition()
        {
            return mSelectedPosition;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getView(position, convertView, parent);

            Object item = getItem(position);

            if (item instanceof Pair)
            {
                Pair<String, String> pair = (Pair<String, String>) item;

                TextView textView = (TextView) view;
                textView.setText(pair.second);
            }

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            TextView textView = (TextView) view;
            Object item = getItem(position);

            if (item instanceof String)
            {
                textView.setText((String) item);
            } else if (item instanceof Pair)
            {
                Pair<String, String> pair = (Pair<String, String>) item;
                textView.setText(pair.second);
            }

            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setSelected(mSelectedPosition == position);

            if (mSelectedPosition == position)
            {
                textView.setTextColor(getResources().getColor(R.color.default_text_cb70038));
            } else
            {
                textView.setTextColor(getResources().getColor(R.color.default_text_c323232));
            }

            return view;
        }
    }
}