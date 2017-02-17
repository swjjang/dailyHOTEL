package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetProductListActivity extends BaseActivity
{
    private TicketInformation mSelectedTicketInformation;
    private GourmetProductListLayout mGourmetProductListLayout;
    private SaleTime mSaleTime;
    private GourmetDetail mGourmetDetail;
    private Province mProvince;
    private String mArea;

    public static Intent newInstance(Context context, SaleTime saleTime, GourmetDetail gourmetDetail, int ticketIndex, Province province, String area)
    {
        Intent intent = new Intent(context, GourmetProductDetailActivity.class);


        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mGourmetProductListLayout = new GourmetProductListLayout(this, mOnEventListener);

        setContentView(mGourmetProductListLayout.onCreateView(R.layout.activity_gourmet_product_list));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_BOOKING:
            {
                setResult(resultCode);

                switch (resultCode)
                {
                    case RESULT_OK:
                    case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                        finish();
                        break;

                    case CODE_RESULT_ACTIVITY_REFRESH:
                    case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                        break;

                    default:
                        break;
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
            {
                if (resultCode == RESULT_OK)
                {
                    mOnEventListener.onReservationClick(mSelectedTicketInformation);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_IMAGELIST:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.TICKET_TYPE_CANCEL_CLICKED, mGourmetDetail.name, null);

        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void moveToUpdateUserPhoneNumber(Customer user, EditProfilePhoneActivity.Type type, String phoneNumber)
    {
        Intent intent = EditProfilePhoneActivity.newInstance(this, user.getUserIdx(), type, phoneNumber);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    private void moveToAddSocialUserInformation(Customer user, String birthday)
    {
        Intent intent = AddProfileSocialActivity.newInstance(this, user, birthday);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
    }

    private void onUserProfile(Customer user, String birthday, boolean isDailyUser, boolean isVerified, boolean isPhoneVerified)
    {
        if (isDailyUser == true)
        {
            if (Util.isValidatePhoneNumber(user.getPhone()) == false)
            {
                moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.getPhone());
            } else
            {
                // 기존에 인증이 되었는데 인증이 해지되었다.
                if (isVerified == true && isPhoneVerified == false)
                {
                    moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.NEED_VERIFICATION_PHONENUMBER, user.getPhone());
                } else
                {
                    processBooking(mSaleTime, mGourmetDetail, mSelectedTicketInformation);
                }
            }
        } else
        {
            // 입력된 정보가 부족해.
            if (Util.isTextEmpty(user.getEmail(), user.getPhone(), user.getName()) == true)
            {
                moveToAddSocialUserInformation(user, birthday);
            } else if (Util.isValidatePhoneNumber(user.getPhone()) == false)
            {
                moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.getPhone());
            } else
            {
                processBooking(mSaleTime, mGourmetDetail, mSelectedTicketInformation);
            }
        }
    }

    private void processBooking(SaleTime saleTime, GourmetDetail gourmetDetail, TicketInformation ticketInformation)
    {
        if (saleTime == null || gourmetDetail == null || ticketInformation == null)
        {
            return;
        }

        String imageUrl = null;
        ArrayList<ImageInformation> mImageInformationList = gourmetDetail.getImageInformationList();

        if (mImageInformationList != null && mImageInformationList.size() > 0)
        {
            imageUrl = mImageInformationList.get(0).url;
        }

        boolean isBenefit = Util.isTextEmpty(gourmetDetail.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetProductListActivity.this, ticketInformation//
            , saleTime, imageUrl, gourmetDetail.category, gourmetDetail.index, isBenefit //
            , mProvince, mArea, gourmetDetail.isShowOriginalPrice, gourmetDetail.entryPosition //
            , gourmetDetail.isDailyChoice, gourmetDetail.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    protected Map<String, String> recordAnalyticsBooking(SaleTime saleTime, GourmetDetail gourmetDetail, TicketInformation ticketInformation)
    {
        if (saleTime == null || gourmetDetail == null || ticketInformation == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetail.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetail.category);

            if (mProvince == null)
            {
                params.put(AnalyticsManager.KeyType.PROVINCE, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.EMPTY);
                params.put(AnalyticsManager.KeyType.AREA, AnalyticsManager.ValueType.EMPTY);
            } else
            {
                if (mProvince instanceof Area)
                {
                    Area area = (Area) mProvince;
                    params.put(AnalyticsManager.KeyType.PROVINCE, area.getProvince().name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, area.name);
                } else
                {
                    params.put(AnalyticsManager.KeyType.PROVINCE, mProvince.name);
                    params.put(AnalyticsManager.KeyType.DISTRICT, AnalyticsManager.ValueType.ALL_LOCALE_KR);
                }

                params.put(AnalyticsManager.KeyType.AREA, Util.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(ticketInformation.discountPrice));
            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private GourmetProductListLayout.OnEventListener mOnEventListener = new GourmetProductListLayout.OnEventListener()
    {
        @Override
        public void finish()
        {

        }

        @Override
        public void onProductDetailClick(TicketInformation ticketInformation)
        {

        }

        @Override
        public void onReservationClick(TicketInformation ticketInformation)
        {
            if (ticketInformation == null)
            {
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedTicketInformation = ticketInformation;

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            } else
            {
                lockUI();
                DailyMobileAPI.getInstance(GourmetProductListActivity.this).requestUserProfile(mNetworkTag, mUserProfileCallback);
            }

            String label = String.format("%s-%s", mGourmetDetail.name, mSelectedTicketInformation.name);
            AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mSaleTime, mGourmetDetail, mSelectedTicketInformation));
        }
    };

    private retrofit2.Callback mUserProfileCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                        Customer user = new Customer();
                        user.setEmail(jsonObject.getString("email"));
                        user.setName(jsonObject.getString("name"));
                        user.setPhone(jsonObject.getString("phone"));
                        user.setUserIdx(jsonObject.getString("userIdx"));

                        String birthday = null;

                        if (jsonObject.has("birthday") == true && jsonObject.isNull("birthday") == false)
                        {
                            birthday = jsonObject.getString("birthday");
                        }

                        String userType = jsonObject.getString("userType");
                        boolean isDailyUser = Constants.DAILY_USER.equalsIgnoreCase(userType);

                        boolean isVerified = jsonObject.getBoolean("verified");
                        boolean isPhoneVerified = jsonObject.getBoolean("phoneVerified");

                        onUserProfile(user, birthday, isDailyUser, isVerified, isPhoneVerified);
                    } else
                    {
                        String msg = responseJSONObject.getString("msg");
                        onErrorToastMessage(msg);
                    }
                } catch (Exception e)
                {
                    onError(e);
                }
            } else
            {
                onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            onError(t);
        }
    };
}
