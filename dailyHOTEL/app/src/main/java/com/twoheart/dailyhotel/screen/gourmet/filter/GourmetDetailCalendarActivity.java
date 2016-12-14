package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetDetailCalendarActivity extends GourmetCalendarActivity
{
    private int mPlaceIndex;
    private SaleTime mSaleTime;

    public static Intent newInstance(Context context, SaleTime saleTime, SaleTime startSaleTime, SaleTime endSaleTime, int placeIndex, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, GourmetDetailCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_START_SALETIME, startSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_SALETIME, endSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mPlaceIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
    }

    @Override
    protected void onConfirm(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            setSaleTicketResult(-1);
            return;
        }

        if (mPlaceIndex == -1)
        {
            setSaleTicketResult(-1);
            return;
        }

        mSaleTime = saleTime;

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        DailyMobileAPI.getInstance(this).requestGourmetDetailInformation(mNetworkTag, mPlaceIndex, saleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), mGourmetDetailInformationCallback);
    }

    private void setSaleTicketResult(int count)
    {
        if (count < 1)
        {
            showEmptyDialog();
        } else
        {
            if (mSaleTime == null)
            {
                showEmptyDialog();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String date = mSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(mSaleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

            //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분");
            //            String phoneDate = simpleDateFormat.format(new Date());

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED//
                , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : //
                    AnalyticsManager.ValueType.NONE) + "-" + date + "-" + DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분"), params);

            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);

            setResult(RESULT_OK, intent);
            hideAnimation();
        }
    }

    private void showEmptyDialog()
    {
        unLockUI();

        String title = getResources().getString(R.string.dialog_notice2);
        String message = getResources().getString(R.string.gourmet_detail_calender_dialog_message);
        String confirm = getResources().getString(R.string.dialog_btn_text_confirm);

        showSimpleDialog(title, message, confirm, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mGourmetDetailInformationCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                int saleTicketCount = 0;

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    JSONObject dataJSONObject = null;

                    if (responseJSONObject.has("data") == true && responseJSONObject.isNull("data") == false)
                    {
                        dataJSONObject = responseJSONObject.getJSONObject("data");
                    }

                    if (msgCode != 100 || dataJSONObject == null)
                    {
                        saleTicketCount = 0;
                    } else
                    {
                        JSONArray ticketInfoJSONArray = dataJSONObject.getJSONArray("tickets");
                        if (ticketInfoJSONArray == null)
                        {
                            saleTicketCount = 0;
                        } else
                        {
                            saleTicketCount = ticketInfoJSONArray.length();
                        }
                    }
                } catch (Exception e)
                {
                    saleTicketCount = 0;
                } finally
                {
                    unLockUI();
                    GourmetDetailCalendarActivity.this.setSaleTicketResult(saleTicketCount);
                }
            } else
            {
                GourmetDetailCalendarActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            GourmetDetailCalendarActivity.this.onError(t);
        }
    };
}
