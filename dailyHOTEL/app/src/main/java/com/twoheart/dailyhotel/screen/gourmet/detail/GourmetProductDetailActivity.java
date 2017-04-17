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
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.ImageDetailListActivity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetProductDetailActivity extends BaseActivity
{
    private GourmetProductDetailLayout mGourmetProductDetailLayout;
    GourmetBookingDay mGourmetBookingDay;
    GourmetDetail mGourmetDetail;
    private Province mProvince;
    private String mArea;
    int mSelectedProductPosition;

    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail//
        , int productPosition, Province province, String area)
    {
        Intent intent = new Intent(context, GourmetProductDetailActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GOUREMT_DETAIL, gourmetDetail);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, productPosition);
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

        mGourmetProductDetailLayout = new GourmetProductDetailLayout(this, mOnEventListener);

        mGourmetBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mGourmetDetail = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_GOUREMT_DETAIL);
        mSelectedProductPosition = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PRODUCTINDEX, -1);
        mProvince = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PROVINCE);
        mArea = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_AREA);

        setContentView(mGourmetProductDetailLayout.onCreateView(R.layout.activity_gourmet_product_detail));

        mGourmetProductDetailLayout.setInformation(mGourmetDetail, mSelectedProductPosition);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.GOURMET_MENU_DETAIL, null);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        AnalyticsManager.getInstance(GourmetProductDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
            AnalyticsManager.Action.GOURMET_MENU_BACK_CLICK, AnalyticsManager.Label.MENU_DETAIL, null);

        super.onBackPressed();
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
                    mOnEventListener.onReservationClick();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_IMAGELIST:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                    processBooking(mGourmetBookingDay, mGourmetDetail, mSelectedProductPosition);
                }
            }
        } else
        {
            // 입력된 정보가 부족해.
            if (com.daily.base.util.TextUtils.isTextEmpty(user.getEmail(), user.getPhone(), user.getName()) == true)
            {
                moveToAddSocialUserInformation(user, birthday);
            } else if (Util.isValidatePhoneNumber(user.getPhone()) == false)
            {
                moveToUpdateUserPhoneNumber(user, EditProfilePhoneActivity.Type.WRONG_PHONENUMBER, user.getPhone());
            } else
            {
                processBooking(mGourmetBookingDay, mGourmetDetail, mSelectedProductPosition);
            }
        }
    }

    private void processBooking(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, int productIndex)
    {
        if (gourmetBookingDay == null || gourmetDetail == null || productIndex < 0)
        {
            return;
        }

        GourmetProduct gourmetProduct = gourmetDetail.getProduct(productIndex);
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetProduct == null || gourmetDetailParams == null)
        {
            return;
        }

        String imageUrl = null;
        List<ImageInformation> imageInformationList = gourmetDetail.getImageList();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageUrl();
        }

        boolean isBenefit = com.daily.base.util.TextUtils.isTextEmpty(gourmetDetailParams.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetProductDetailActivity.this, gourmetDetailParams.name, gourmetProduct//
            , gourmetBookingDay, imageUrl, gourmetDetailParams.category, gourmetDetail.index, isBenefit //
            , mProvince, mArea, gourmetDetail.isShowOriginalPrice, gourmetDetail.entryPosition //
            , gourmetDetail.isDailyChoice, gourmetDetailParams.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    protected Map<String, String> recordAnalyticsBooking(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, int productIndex)
    {
        if (gourmetBookingDay == null || gourmetDetail == null || gourmetDetail.getGourmetDetailParmas() == null || productIndex <= 0)
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

                params.put(AnalyticsManager.KeyType.AREA, com.daily.base.util.TextUtils.isTextEmpty(mArea) ? AnalyticsManager.ValueType.EMPTY : mArea);
            }

            GourmetProduct gourmetProduct = gourmetDetail.getProduct(productIndex);

            if (gourmetProduct != null)
            {
                params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(gourmetProduct.discountPrice));
            }

            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookingDay.getVisitDay("yyyyMMdd"));

            return params;
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private GourmetProductDetailLayout.OnEventListener mOnEventListener = new GourmetProductDetailLayout.OnEventListener()
    {
        @Override
        public void onImageClick(int position)
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            GourmetProduct gourmetProduct = mGourmetDetail.getProduct(mSelectedProductPosition);

            List<ProductImageInformation> productImageInformationList = mGourmetDetail.getProduct(mSelectedProductPosition).getImageList();
            if (productImageInformationList.size() == 0)
            {
                return;
            }

            lockUiComponent();

            // ProductImageInformation -> ImageInformation 로 수정
            List<ImageInformation> imageInformationList = new ArrayList<>();

            for (ProductImageInformation productImageInformation : productImageInformationList)
            {
                ImageInformation imageInformation = new ImageInformation();
                imageInformation.description = productImageInformation.imageDescription;
                imageInformation.setImageUrl(productImageInformation.imageUrl);

                imageInformationList.add(imageInformation);
            }

            Intent intent = ImageDetailListActivity.newInstance(GourmetProductDetailActivity.this, PlaceType.FNB, gourmetProduct.ticketName, imageInformationList, position);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_IMAGELIST);

            AnalyticsManager.getInstance(GourmetProductDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS,//
                AnalyticsManager.Action.GOURMET_IMAGE_CLICKED, gourmetProduct.ticketName, null);
        }

        @Override
        public void onReservationClick()
        {
            GourmetProduct gourmetProduct = mGourmetDetail.getProduct(mSelectedProductPosition);

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

            if (DailyHotel.isLogin() == false)
            {
                startLoginActivity(AnalyticsManager.Screen.DAILYGOURMET_DETAIL);
            } else
            {
                lockUI();
                DailyMobileAPI.getInstance(GourmetProductDetailActivity.this).requestUserProfile(mNetworkTag, mUserProfileCallback);
            }

            GourmetDetailParams gourmetDetailParams = mGourmetDetail.getGourmetDetailParmas();

            if (gourmetDetailParams != null)
            {
                String label = String.format(Locale.KOREA, "%s-%s", gourmetDetailParams.name, gourmetProduct.ticketName);
                AnalyticsManager.getInstance(GourmetProductDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
                    , AnalyticsManager.Action.BOOKING_CLICKED, label, recordAnalyticsBooking(mGourmetBookingDay, mGourmetDetail, mSelectedProductPosition));
            }

            if (gourmetProduct.getPrimaryImage() == null)
            {
                AnalyticsManager.getInstance(GourmetProductDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_BOOKING_CLICK_PHOTO, "n", null);
            } else
            {
                AnalyticsManager.getInstance(GourmetProductDetailActivity.this).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS, //
                    AnalyticsManager.Action.GOURMET_MENU_BOOKING_CLICK_PHOTO, "y", null);
            }
        }

        @Override
        public void finish()
        {
            GourmetProductDetailActivity.this.onBackPressed();
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
