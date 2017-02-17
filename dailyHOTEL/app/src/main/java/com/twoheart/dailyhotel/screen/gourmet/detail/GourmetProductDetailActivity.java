package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Area;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.Province;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetTicket;
import com.twoheart.dailyhotel.network.model.ImageInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.AddProfileSocialActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.EditProfilePhoneActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GourmetProductDetailActivity extends BaseActivity
{
    private GourmetProductDetailLayout mGourmetProductDetailLayout;
    private SaleTime mSaleTime;
    private GourmetDetail mGourmetDetail;
    private Province mProvince;
    private String mArea;
    private int mSelectedTicketIndex;

    public static Intent newInstance(Context context, SaleTime saleTime, GourmetDetail gourmetDetail, int selectedTicketIndex)
    {
        Intent intent = new Intent(context, GourmetProductDetailActivity.class);


        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        // Intent


        mGourmetProductDetailLayout = new GourmetProductDetailLayout(this, mOnEventListener);

    }

    private void initLayout()
    {
        setContentView(mGourmetProductDetailLayout.onCreateView(R.layout.activity_gourmet_product_detail));


    }


    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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
                    processBooking(mSaleTime, mGourmetDetail, mSelectedTicketIndex);
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
                processBooking(mSaleTime, mGourmetDetail, mSelectedTicketIndex);
            }
        }
    }

    private void processBooking(SaleTime saleTime, GourmetDetail gourmetDetail, int ticketIndex)
    {
        if (saleTime == null || gourmetDetail == null || ticketIndex < 0)
        {
            return;
        }

        GourmetTicket gourmetTicket = gourmetDetail.getProduct(ticketIndex);
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        if (gourmetTicket == null || gourmetDetailParams == null)
        {
            return;
        }

        String imageUrl = null;
        List<ImageInformation> imageInformationList = gourmetDetail.getImageList();

        if (imageInformationList != null && imageInformationList.size() > 0)
        {
            imageUrl = imageInformationList.get(0).getImageUrl();
        }

        boolean isBenefit = Util.isTextEmpty(gourmetDetailParams.benefit) == false;

        Intent intent = GourmetPaymentActivity.newInstance(GourmetProductDetailActivity.this, gourmetDetailParams.name, gourmetTicket//
            , saleTime, imageUrl, gourmetDetailParams.category, gourmetDetail.index, isBenefit //
            , mProvince, mArea, gourmetDetail.isShowOriginalPrice, gourmetDetail.entryPosition //
            , gourmetDetail.isDailyChoice, gourmetDetailParams.ratingValue);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    protected Map<String, String> recordAnalyticsBooking(SaleTime saleTime, GourmetDetail gourmetDetail, int ticketIndex)
    {
        if (saleTime == null || gourmetDetail == null || gourmetDetail.getGourmetDetailParmas() == null || ticketIndex <= 0)
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

            GourmetTicket gourmetTicket = gourmetDetail.getProduct(ticketIndex);

            if (gourmetTicket != null)
            {
                params.put(AnalyticsManager.KeyType.PRICE_OF_SELECTED_TICKET, Integer.toString(gourmetTicket.discountPrice));
            }

            params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));

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

        }

        @Override
        public void onReservationClick()
        {

        }

        @Override
        public void finish()
        {
            GourmetProductDetailActivity.this.finish();
        }
    };
}
