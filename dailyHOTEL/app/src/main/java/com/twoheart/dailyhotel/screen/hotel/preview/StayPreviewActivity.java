package com.twoheart.dailyhotel.screen.hotel.preview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayDetail;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.RecommendationStay;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StayPreviewActivity extends BaseActivity
{
    private static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    protected StayPreviewLayout mPreviewLayout;
    protected StayPreviewNetworkController mNetworkController;

    private StayBookingDay mPlaceBookingDay;
    private StayDetail mPlaceDetail;
    private PlaceReviewScores mPlaceReviewScores;

    private int mViewPrice;

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
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, stay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, stay.name);
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
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, homePlace.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, homePlace.title);

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
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, recommendationStay.index);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, recommendationStay.name);
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

        mPreviewLayout = new StayPreviewLayout(this, mOnEventListener);
        mNetworkController = new StayPreviewNetworkController(this, getNetworkTag(), mOnNetworkControllerListener);

        mPlaceBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

        int placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
        mPlaceDetail = new StayDetail(placeIndex, -1, "N", -1, false);

        mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, 0);
        String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);

        Stay.Grade grade;

        try
        {
            grade = Stay.Grade.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_GRADE));
        } catch (Exception e)
        {
            grade = null;
        }

        initLayout(placeName, grade);

        onRefresh(placeIndex);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.PEEK_POP_CLOSE, AnalyticsManager.Label.BACKKEY, null);

        mPreviewLayout.hidePopAnimation();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        Util.restartApp(this);
    }

    private void initLayout(String placeName, Stay.Grade grade)
    {
        setContentView(mPreviewLayout.onCreateView(R.layout.activity_place_preview));

        mPreviewLayout.setGrade(grade);
        mPreviewLayout.setPlaceName(placeName);
        mPreviewLayout.showPopAnimation();
    }


    private void onRefresh(int placeIndex)
    {
        lockUI();

        try
        {
            mNetworkController.requestStayDetailInformation(placeIndex, mPlaceBookingDay.getCheckInDay("yyyy-MM-dd"), mPlaceBookingDay.getNights());
            mNetworkController.requestPlaceReviewScores(PlaceType.HOTEL, placeIndex);
        } catch (Exception e)
        {
            finish();
        }
    }

    void updatePreviewInformationLayout(StayBookingDay stayBookingDay, StayDetail stayDetail, int reviewCount)
    {
        unLockUI();

        if (stayBookingDay == null || stayDetail == null)
        {
            return;
        }

        StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();

        if (mPreviewLayout != null)
        {
            List<StayProduct> stayProductList = stayDetail.getProductList();

            // SOLD OUT
            if (stayProductList == null || stayProductList.size() == 0)
            {
                mPreviewLayout.updateLayout(stayBookingDay, stayDetail, reviewCount, changedProductPrice(stayDetail, mViewPrice), true);
            } else
            {
                mPreviewLayout.updateLayout(stayBookingDay, stayDetail, reviewCount, changedProductPrice(stayDetail, mViewPrice), false);
            }

            try
            {
                HashMap<String, String> params = new HashMap();
                params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.STAY);
                params.put(AnalyticsManager.KeyType.CATEGORY, stayDetailParams.category);

                AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.PEEK_POP, null, params);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
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
            if (mPlaceBookingDay == null || mPlaceDetail == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            if (DailyHotel.isLogin() == true)
            {
                if (mPlaceDetail.getStayDetailParams().myWish == true)
                {
                    mNetworkController.requestRemoveWishList(PlaceType.HOTEL, mPlaceDetail.index);

                    AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.PEEK_POP_DELETE_WISHLIST, null, null);
                } else
                {
                    mNetworkController.requestAddWishList(PlaceType.HOTEL, mPlaceDetail.index);

                    AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.PEEK_POP_ADD_WISHLIST, null, null);
                }
            } else
            {
                Intent intent = LoginActivity.newInstance(StayPreviewActivity.this);
                startActivity(intent);
                StayPreviewActivity.this.finish();
            }
        }

        @Override
        public void onKakaoClick()
        {
            if (mPlaceBookingDay == null || mPlaceDetail == null || lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            try
            {
                // 카카오톡 패키지 설치 여부
                getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                String name = DailyUserPreference.getInstance(StayPreviewActivity.this).getName();

                if (DailyTextUtils.isTextEmpty(name) == true)
                {
                    name = getString(R.string.label_friend) + "가";
                } else
                {
                    name += "님이";
                }

                StayDetail stayDetail = mPlaceDetail;

                if (stayDetail == null)
                {
                    return;
                }

                StayDetailParams stayDetailParams = stayDetail.getStayDetailParams();
                if (stayDetailParams == null)
                {
                    return;
                }

                KakaoLinkManager.newInstance(StayPreviewActivity.this).shareStay(name//
                    , stayDetailParams.name//
                    , stayDetailParams.address//
                    , stayDetail.index//
                    , stayDetailParams.getImageList().get(0).getImageUrl()//
                    , mPlaceBookingDay);

                StayPreviewActivity.this.finish();
            } catch (Exception e)
            {
                showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                    , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                    , new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Util.installPackage(StayPreviewActivity.this, "com.kakao.talk");
                        }
                    }, null, null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            StayPreviewActivity.this.finish();
                        }
                    }, true);
            }

            AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_SHARE_KAKAO, null, null);
        }

        @Override
        public void onMapClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mPlaceDetail == null)
            {
                return;
            }

            StayDetailParams stayDetailParams = mPlaceDetail.getStayDetailParams();

            if (stayDetailParams == null)
            {
                return;
            }

            Util.shareNaverMap(StayPreviewActivity.this, mPlaceDetail.getStayDetailParams().name//
                , Double.toString(stayDetailParams.latitude), Double.toString(stayDetailParams.longitude));

            StayPreviewActivity.this.finish();

            AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_NAVER_MAP, null, null);
        }

        @Override
        public void onPlaceDetailClick()
        {
            AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_RESERVATION, null, null);

            setResult(RESULT_OK);
            StayPreviewActivity.this.finish();
        }

        @Override
        public void onHideAnimation()
        {
            finish();
        }

        @Override
        public void onCloseClick()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            mPreviewLayout.hidePopAnimation();

            AnalyticsManager.getInstance(StayPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_CLOSE, AnalyticsManager.Label.CLOSE, null);
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
            if (mPlaceDetail == null)
            {
                return;
            }

            try
            {
                mPlaceDetail.setStayDetailParams(stayDetailParams);

                if (mPlaceReviewScores != null)
                {
                    updatePreviewInformationLayout(mPlaceBookingDay, mPlaceDetail, mPlaceReviewScores.reviewScoreTotalCount);
                }
            } catch (Exception e)
            {
                DailyToast.showToast(StayPreviewActivity.this, R.string.act_base_network_connect, Toast.LENGTH_LONG);
                finish();
            }
        }

        @Override
        public void onAddWishList(boolean isSuccess, String message)
        {
            if (isSuccess == true)
            {
                lockUI(false);
                mPreviewLayout.addWish();

                StayDetailParams stayDetailParams = mPlaceDetail.getStayDetailParams();

                if (stayDetailParams != null)
                {
                    stayDetailParams.myWish = true;
                    stayDetailParams.wishCount++;

                    mPreviewLayout.updateWishInformation(mPlaceReviewScores.reviewScoreTotalCount, stayDetailParams.wishCount, stayDetailParams.myWish);
                }
            } else
            {
                DailyToast.showToast(StayPreviewActivity.this, message, DailyToast.LENGTH_SHORT);
            }
        }

        @Override
        public void onRemoveWishList(boolean isSuccess, String message)
        {
            if (isSuccess == true)
            {
                lockUI(false);
                mPreviewLayout.removeWish();

                StayDetailParams stayDetailParams = mPlaceDetail.getStayDetailParams();

                if (stayDetailParams != null)
                {
                    stayDetailParams.myWish = false;
                    stayDetailParams.wishCount--;

                    mPreviewLayout.updateWishInformation(mPlaceReviewScores.reviewScoreTotalCount, stayDetailParams.wishCount, stayDetailParams.myWish);
                }
            } else
            {
                DailyToast.showToast(StayPreviewActivity.this, message, DailyToast.LENGTH_SHORT);
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

            if (mPlaceDetail.getStayDetailParams() != null)
            {
                updatePreviewInformationLayout(mPlaceBookingDay, mPlaceDetail, mPlaceReviewScores.reviewScoreTotalCount);
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
            StayPreviewActivity.this.onErrorToastMessage(message);
            finish();
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
