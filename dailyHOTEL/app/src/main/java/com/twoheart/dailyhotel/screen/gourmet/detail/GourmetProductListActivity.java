package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetProductListActivity extends BaseActivity
{
    private GourmetProductListLayout mGourmetProductListLayout;
    SaleTime mSaleTime;
    GourmetDetail mGourmetDetail;
    Province mProvince;
    String mArea;
    int mSelectedProductPosition;

    public static Intent newInstance(Context context, SaleTime saleTime, GourmetDetail gourmetDetail, int productIndex, Province province, String area)
    {
        Intent intent = new Intent(context, GourmetProductListActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GOUREMT_DETAIL, gourmetDetail);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, productIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_AREA, area);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mGourmetProductListLayout = new GourmetProductListLayout(this, mOnEventListener);

        mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mGourmetDetail = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_GOUREMT_DETAIL);
        int productIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, -1);
        mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);

        setContentView(mGourmetProductListLayout.onCreateView(R.layout.activity_gourmet_product_list));

        mGourmetProductListLayout.setProductInformationLayout(this, mGourmetDetail.getProductList());

        settingABTest(mGourmetDetail);

        // 메뉴 상세화면 진입
        if (productIndex > 0)
        {
            List<GourmetProduct> gourmetProductList = mGourmetDetail.getProductList();
            int size = gourmetProductList.size();
            for (int i = 0; i < size; i++)
            {
                if (gourmetProductList.get(i).index == productIndex)
                {
                    Intent intentProductDetail = GourmetProductDetailActivity.newInstance(GourmetProductListActivity.this//
                        , mSaleTime, mGourmetDetail, i, mProvince, mArea);
                    startActivityForResult(intentProductDetail, CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_DETAIL);
                    break;
                }
            }
        }
    }

    private void settingABTest(GourmetDetail gourmetDetail)
    {
        String data = DailyPreference.getInstance(this).getRemoteConfigABTestGourmetProductList();

        if (Util.isTextEmpty(data) == true)
        {
            return;
        }

        try
        {
            JSONObject jsonObject = new JSONObject(data);
            String abTest = jsonObject.getString("value");

            if (Util.isTextEmpty(abTest) == true)
            {
                return;
            }

            boolean isLeft;

            switch (abTest)
            {
                case "b":
                    isLeft = true;
                    break;

                case "a":
                default:
                    isLeft = false;
                    break;
            }

            mGourmetProductListLayout.setThumbnailLocation(isLeft);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_BOOKING:
            case CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_DETAIL:
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
                        finish();
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
                    mOnEventListener.onReservationClick(mSelectedProductPosition);
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish()
    {
        GourmetDetailParams gourmetDetailParams = mGourmetDetail.getGourmetDetailParmas();

        if (gourmetDetailParams != null)
        {
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                , AnalyticsManager.Action.TICKET_TYPE_CANCEL_CLICKED, gourmetDetailParams.name, null);
        }

        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
            AnalyticsManager.Action.GOURMET_MENU_BACK_CLICK, AnalyticsManager.Label.MENU_LIST, null);

        super.onBackPressed();
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

    void onUserProfile(Customer user, String birthday, boolean isDailyUser, boolean isVerified, boolean isPhoneVerified)
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
                    processBooking(mSaleTime, mGourmetDetail, mSelectedProductPosition);
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
                processBooking(mSaleTime, mGourmetDetail, mSelectedProductPosition);
            }
        }
    }

    private void processBooking(SaleTime saleTime, GourmetDetail gourmetDetail, int productIndex)
    {
        if (saleTime == null || gourmetDetail == null || productIndex < 0)
        {
            unLockUI();
            return;
        }

        GourmetProduct gourmetProduct = gourmetDetail.getProduct(productIndex);
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetProduct == null || gourmetDetailParams == null)
        {
            unLockUI();
            return;
        }

        String imageUrl = null;
        List<ImageInformation> imageInformationList = gourmetDetail.getImageList();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageUrl();
        }

        boolean isBenefit = Util.isTextEmpty(gourmetDetailParams.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetProductListActivity.this, gourmetDetailParams.name, gourmetProduct//
            , saleTime, imageUrl, gourmetDetailParams.category, gourmetDetail.index, isBenefit //
            , mProvince, mArea, gourmetDetail.isShowOriginalPrice, gourmetDetail.entryPosition //
            , gourmetDetail.isDailyChoice, gourmetDetailParams.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    protected Map<String, String> recordAnalyticsBooking(SaleTime saleTime, GourmetDetail gourmetDetail, int productIndex)
    {
        if (saleTime == null || gourmetDetail == null || gourmetDetail.getGourmetDetailParmas() == null || productIndex <= 0)
        {
            return null;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetDetailParams == null)
        {
            return null;
        }

        try
        {
            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.NAME, gourmetDetailParams.name);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

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

            GourmetProduct gourmetProduct = gourmetDetail.getProduct(productIndex);

            if (gourmetProduct != null)
            {
                params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(gourmetProduct.discountPrice));
            }

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
            GourmetProductListActivity.this.onBackPressed();
        }

        @Override
        public void onProductDetailClick(int position, int where)
        {
            GourmetProduct gourmetProduct = mGourmetDetail.getProduct(position);

            if (gourmetProduct == null)
            {
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = GourmetProductDetailActivity.newInstance(GourmetProductListActivity.this//
                , mSaleTime, mGourmetDetail, position, mProvince, mArea);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_PRODUCT_DETAIL);

            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(AnalyticsManager.KeyType.VALUE, Integer.toString(where));

            AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                AnalyticsManager.Action.GOURMET_MENU_DETAIL_CLICK, Integer.toString(gourmetProduct.index), paramsMap);

            if (gourmetProduct.getPrimaryImage() == null)
            {
                AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_DETAIL_CLICK_PHOTO, "n", null);
            } else
            {
                AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_DETAIL_CLICK_PHOTO, "y", null);
            }
        }

        @Override
        public void onReservationClick(int position)
        {
            GourmetProduct gourmetProduct = mGourmetDetail.getProduct(position);

            if (gourmetProduct == null)
            {
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mSelectedProductPosition = position;

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            } else
            {
                lockUI();
                DailyMobileAPI.getInstance(GourmetProductListActivity.this).requestUserProfile(mNetworkTag, mUserProfileCallback);
            }

            GourmetDetailParams gourmetDetailParams = mGourmetDetail.getGourmetDetailParmas();

            if (gourmetDetailParams != null)
            {
                String label = String.format("%s-%s", gourmetDetailParams.name, gourmetProduct.ticketName);
                AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                    , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mSaleTime, mGourmetDetail, position));
            }

            if (gourmetProduct.getPrimaryImage() == null)
            {
                AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_BOOKING_CLICK_PHOTO, "n", null);
            } else
            {
                AnalyticsManager.getInstance(GourmetProductListActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_BOOKING_CLICK_PHOTO, "y", null);
            }
        }
    };

    retrofit2.Callback mUserProfileCallback = new retrofit2.Callback<JSONObject>()
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
