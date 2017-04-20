package com.twoheart.dailyhotel.screen.hotel.preview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StayPreviewActivity extends BaseActivity
{
    protected static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    protected StayPreviewLayout mStayPreviewLayout;
    protected StayPreviewNetworkController mNetworkController;

    private StayBookingDay mPlaceBookingDay;
    private PlaceDetail mPlaceDetail;
    private PlaceReviewScores mPlaceReviewScores;

    private boolean mCheckPrice;
    private int mViewPrice;
    private int mPlaceIndex;

    /**
     * 리스트에서 호출, 검색 결과에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param stay
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, Stay stay)
    {
        Intent intent = new Intent(context, StayPreviewActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, stay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, stay.discountPrice);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, stay.getGrade().name());

        return intent;
    }

    /**
     * 홈에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param homePlace
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, HomePlace homePlace)
    {
        if (stayBookingDay == null || homePlace == null)
        {
            return null;
        }

        Intent intent = new Intent(context, StayPreviewActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, homePlace.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, homePlace.title);

        if (homePlace.prices != null && homePlace.prices.discountPrice > 0)
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, homePlace.prices.discountPrice);
        } else
        {
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, SKIP_CHECK_DISCOUNT_PRICE_VALUE);
        }

        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, homePlace.details.stayGrade.name());

        return intent;
    }

    /**
     * 추천 목록에서 호출
     *
     * @param context
     * @param stayBookingDay
     * @param recommendationStay
     * @return
     */
    public static Intent newInstance(Context context, StayBookingDay stayBookingDay, RecommendationStay recommendationStay)
    {
        Intent intent = new Intent(context, StayPreviewActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, recommendationStay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, recommendationStay.name);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, recommendationStay.discount);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_GRADE, recommendationStay.grade);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mStayPreviewLayout = new StayPreviewLayout(this, mOnEventListener);
        mNetworkController = new StayPreviewNetworkController(this, getNetworkTag(), mOnNetworkControllerListener);

        mPlaceBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

        mPlaceIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        mPlaceDetail = new StayDetail(mPlaceIndex, -1, "N", -1, false);

        mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, 0);
        String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
        Stay.Grade grade = Stay.Grade.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_GRADE));
        boolean isFromMap = intent.hasExtra(NAME_INTENT_EXTRA_DATA_FROM_MAP) == true;

        initLayout(placeName, grade, isFromMap);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        lockUI();

        try
        {
            mNetworkController.requestStayDetailInformation(mPlaceIndex, mPlaceBookingDay.getCheckInDay("yyyy-MM-dd"), mPlaceBookingDay.getNights());
            mNetworkController.requestPlaceReviewScores(PlaceType.HOTEL, mPlaceIndex);
        } catch (Exception e)
        {
            finish();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        finish();
    }

    private void initLayout(String placeName, Stay.Grade grade, boolean isFromMap)
    {
        setContentView(mStayPreviewLayout.onCreateView(R.layout.activity_stay_preview));

        mStayPreviewLayout.setGrade(grade);
        mStayPreviewLayout.setPlaceName(placeName);
    }

    void updatePreviewInformationLayout(StayBookingDay stayBookingDay, StayDetail stayDetail, int reviewCount)
    {
        if (stayBookingDay == null || stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (mStayPreviewLayout != null)
        {
            mStayPreviewLayout.updateLayout(stayDetail, reviewCount, changedProductPrice(stayDetail, mViewPrice));
        }
    }

    private boolean changedProductPrice(StayDetail stayDetail, int listViewPrice)
    {
        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<StayProduct> stayProductList = stayDetail.getProductList();
        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (stayProductList == null || stayProductList.size() == 0)
        {
            return true;
        } else
        {
            boolean hasPrice = false;

            if (listViewPrice == SKIP_CHECK_DISCOUNT_PRICE_VALUE)
            {
                // 홈 가격 정보 제거로 인한 처리 추가
                hasPrice = true;
            } else
            {
                for (StayProduct stayProduct : stayProductList)
                {
                    if (listViewPrice == stayProduct.averageDiscount)
                    {
                        hasPrice = true;
                        break;
                    }
                }
            }

           return hasPrice == false;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    StayPreviewLayout.OnEventListener mOnEventListener = new StayPreviewLayout.OnEventListener()
    {
        @Override
        public void onWishClick()
        {

        }

        @Override
        public void onKakaoClick()
        {

        }

        @Override
        public void onMapClick()
        {

        }

        @Override
        public void finish()
        {
            StayPreviewActivity.this.finish();
        }
    };


    private StayPreviewNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayPreviewNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onStayDetailInformation(StayDetailParams stayDetailParams)
        {
            try
            {
                StayDetail stayDetail = (StayDetail) mPlaceDetail;

                stayDetail.setStayDetailParams(stayDetailParams);

                if(mPlaceReviewScores != null)
                {
                    updatePreviewInformationLayout(mPlaceBookingDay, stayDetail, mPlaceReviewScores.reviewScoreTotalCount);
                }
            } catch (Exception e)
            {
                DailyToast.showToast(StayPreviewActivity.this, R.string.act_base_network_connect, Toast.LENGTH_LONG);
                finish();
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onAddWishList(boolean isSuccess, String message)
        {
            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (isSuccess == true)
            {
                stayDetailParams.myWish = true;
                int wishCount = ++stayDetailParams.wishCount;
                mStayPreviewLayout.setWishCount(wishCount);
                mStayPreviewLayout.addWish();
            } else
            {
                mStayPreviewLayout.setWishCount(stayDetailParams.wishCount);
                mStayPreviewLayout.removeWish();

                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    message = "";
                }

                releaseUiComponent();

                showSimpleDialog(getResources().getString(R.string.dialog_notice2), message//
                    , getResources().getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onRemoveWishList(boolean isSuccess, String message)
        {
            StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;
            StayDetail stayDetail = (StayDetail) mPlaceDetail;
            StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

            if (isSuccess == true)
            {
                stayDetailParams.myWish = false;
                int wishCount = --stayDetailParams.wishCount;
                mStayPreviewLayout.setWishCount(wishCount);
                mStayPreviewLayout.addWish();
            } else
            {
                mStayPreviewLayout.setWishCount(stayDetailParams.wishCount);
                mStayPreviewLayout.removeWish();

                if (DailyTextUtils.isTextEmpty(message) == true)
                {
                    message = "";
                }

                releaseUiComponent();

                showSimpleDialog(getResources().getString(R.string.dialog_notice2), message//
                    , getResources().getString(R.string.dialog_btn_text_confirm), null);
            }
        }

        @Override
        public void onPlaceReviewScores(PlaceReviewScores placeReviewScores)
        {
            if (placeReviewScores == null)
            {
                return;
            }

            mPlaceReviewScores = placeReviewScores;

            StayDetail stayDetail = (StayDetail) mPlaceDetail;

            if(stayDetail.getStayDetailParams() != null)
            {
                updatePreviewInformationLayout(mPlaceBookingDay, stayDetail, mPlaceReviewScores.reviewScoreTotalCount);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayPreviewActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayPreviewActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(final int msgCode, final String message)
        {
            // 판매 마감시
            if (msgCode == 5)
            {
                StayPreviewActivity.this.onErrorPopupMessage(msgCode, message, null);
            } else
            {
                StayPreviewActivity.this.onErrorPopupMessage(msgCode, message);
            }
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayPreviewActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(final Call call, final Response response)
        {
            StayPreviewActivity.this.onErrorResponse(call, response);
            finish();
        }
    };
}
