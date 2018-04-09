package com.twoheart.dailyhotel.screen.gourmet.preview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

@Deprecated
public class GourmetPreviewActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_DATA_WISH = "wish";

    private static final int REQUEST_CODE_LOGIN_IN_BY_WISH = 10000;

    private static final int SKIP_CHECK_DISCOUNT_PRICE_VALUE = Integer.MIN_VALUE;

    protected GourmetPreviewLayout mPreviewLayout;
    protected GourmetPreviewNetworkController mNetworkController;
    CommonRemoteImpl mCommonRemoteImpl;

    GourmetBookingDay mPlaceBookingDay;
    GourmetDetail mPlaceDetail;
    PlaceReviewScores mPlaceReviewScores;

    private int mViewPrice;
    boolean mEnteredLogin;

    //    /**
    //     * 리스트에서 호출
    //     *
    //     * @param context
    //     * @param gourmetBookingDay
    //     * @param gourmet
    //     * @return
    //     */
    //    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, Gourmet gourmet)
    //    {
    //        Intent intent = new Intent(context, GourmetPreviewActivity.class);
    //
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, gourmet.isSoldOut);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
    //
    //        return intent;
    //    }
    //
    //    /**
    //     * 캠페인태그 리스트에서 호출
    //     *
    //     * @param context
    //     * @param visitDate
    //     * @param gourmet
    //     * @return
    //     */
    //    public static Intent newInstance(Context context, String visitDate, Gourmet gourmet)
    //    {
    //        Intent intent = new Intent(context, GourmetPreviewActivity.class);
    //
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, gourmet.index);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, gourmet.name);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, gourmet.category);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, gourmet.isSoldOut);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, gourmet.discountPrice);
    //
    //        return intent;
    //    }
    //
    //    /**
    //     * 홈에서 호출
    //     *
    //     * @param context
    //     * @param gourmetBookingDay
    //     * @param recentlyPlace
    //     * @return
    //     */
    //    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, RecentlyPlace recentlyPlace)
    //    {
    //        if (gourmetBookingDay == null || recentlyPlace == null)
    //        {
    //            return null;
    //        }
    //
    //        Intent intent = new Intent(context, GourmetPreviewActivity.class);
    //
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, recentlyPlace.index);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, recentlyPlace.title);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, recentlyPlace.details.category);
    //
    //        if (recentlyPlace.prices != null && recentlyPlace.prices.discountPrice > 0)
    //        {
    //            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, recentlyPlace.prices.discountPrice);
    //        } else
    //        {
    //            intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, SKIP_CHECK_DISCOUNT_PRICE_VALUE);
    //        }
    //
    //        return intent;
    //    }
    //
    //    /**
    //     * 추천 목록에서 호출
    //     *
    //     * @param context
    //     * @param gourmetBookingDay
    //     * @param recommendationGourmet
    //     * @return
    //     */
    //    public static Intent newInstance(Context context, GourmetBookingDay gourmetBookingDay, RecommendationGourmet recommendationGourmet)
    //    {
    //        Intent intent = new Intent(context, GourmetPreviewActivity.class);
    //
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, recommendationGourmet.index);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, recommendationGourmet.name);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CATEGORY, recommendationGourmet.category);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_IS_SOLDOUT, recommendationGourmet.isSoldOut);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, recommendationGourmet.discount);
    //
    //        return intent;
    //    }


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

        mPreviewLayout = new GourmetPreviewLayout(this, mOnEventListener);
        mNetworkController = new GourmetPreviewNetworkController(this, getNetworkTag(), mOnNetworkControllerListener);
        mCommonRemoteImpl = new CommonRemoteImpl(this);

        mEnteredLogin = DailyHotel.isLogin();

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE) == true)
        {
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

            try
            {
                String visitDate = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE);
                gourmetBookingDay.setVisitDay(visitDate);
            } catch (Exception e)
            {
                ExLog.e(e.getMessage());
            }

            mPlaceBookingDay = gourmetBookingDay;
        } else
        {
            mPlaceBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        }

        int placeIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
        mPlaceDetail = new GourmetDetail(placeIndex);

        mViewPrice = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DISCOUNTPRICE, 0);
        String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
        String category = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CATEGORY);

        initLayout(placeName, category);

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
        unLockUI();

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_LOGIN_IN_BY_WISH:
                if (resultCode == Activity.RESULT_OK)
                {
                    mOnEventListener.onWishClick();
                }
                break;
        }
    }

    private void initLayout(String placeName, String category)
    {
        //        setContentView(mPreviewLayout.onCreateView(R.layout.activity_place_preview));

        mPreviewLayout.setPlaceName(placeName);
        mPreviewLayout.setCategory(category, null);


        mPreviewLayout.showPopAnimation();
    }


    private void onRefresh(int placeIndex)
    {
        lockUI();

        try
        {
            mNetworkController.requestGourmetDetailInformation(placeIndex, mPlaceBookingDay.getVisitDay("yyyy-MM-dd"));
            mNetworkController.requestPlaceReviewScores(PlaceType.FNB, placeIndex);
        } catch (Exception e)
        {
            finish();
        }
    }

    void updatePreviewInformationLayout(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, int reviewCount)
    {
        unLockUI();

        if (gourmetBookingDay == null || gourmetDetail == null)
        {
            return;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParams();

        if (mPreviewLayout != null)
        {
            mPreviewLayout.setCategory(gourmetDetailParams.category, gourmetDetailParams.categorySub);

            List<GourmetProduct> stayProductList = gourmetDetail.getProductList();

            // SOLD OUT
            if (stayProductList == null || stayProductList.size() == 0)
            {
                mPreviewLayout.updateLayout(gourmetBookingDay, gourmetDetail, reviewCount, changedProductPrice(gourmetDetail, mViewPrice), true);
            } else
            {
                mPreviewLayout.updateLayout(gourmetBookingDay, gourmetDetail, reviewCount, changedProductPrice(gourmetDetail, mViewPrice), false);
            }
        }

        try
        {
            HashMap<String, String> params = new HashMap();
            params.put(AnalyticsManager.KeyType.PLACE_TYPE, AnalyticsManager.ValueType.GOURMET);
            params.put(AnalyticsManager.KeyType.CATEGORY, gourmetDetailParams.category);

            AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.PEEK_POP, null, params);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private boolean changedProductPrice(GourmetDetail gourmetDetail, int listViewPrice)
    {
        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<GourmetProduct> gourmetProductList = gourmetDetail.getProductList();

        if (gourmetProductList == null || gourmetProductList.size() == 0)
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
                for (GourmetProduct gourmetProduct : gourmetProductList)
                {
                    if (listViewPrice == gourmetProduct.discountPrice)
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

    GourmetPreviewLayout.OnEventListener mOnEventListener = new GourmetPreviewLayout.OnEventListener()
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
                if (mPlaceDetail.getGourmetDetailParams().myWish == true)
                {
                    mNetworkController.requestRemoveWishList(PlaceType.FNB, mPlaceDetail.index);

                    AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.PEEK_POP_DELETE_WISHLIST, null, null);
                } else
                {
                    mNetworkController.requestAddWishList(PlaceType.FNB, mPlaceDetail.index);

                    AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.PEEK_POP_ADD_WISHLIST, null, null);
                }
            } else
            {
                Intent intent = LoginActivity.newInstance(GourmetPreviewActivity.this);
                startActivityForResult(intent, REQUEST_CODE_LOGIN_IN_BY_WISH);
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

                String name = DailyUserPreference.getInstance(GourmetPreviewActivity.this).getName();

                if (mPlaceDetail == null)
                {
                    return;
                }

                GourmetDetailParams gourmetDetailParams = mPlaceDetail.getGourmetDetailParams();
                if (gourmetDetailParams == null)
                {
                    return;
                }

                String urlFormat = "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s&utm_source=share&utm_medium=gourmet_detail_kakaotalk";
                String longUrl = String.format(Locale.KOREA, urlFormat, mPlaceDetail.index //
                    , mPlaceBookingDay.getVisitDay("yyyy-MM-dd"));

                addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String shortUrl) throws Exception
                    {
                        KakaoLinkManager.newInstance(GourmetPreviewActivity.this).shareGourmet(name//
                            , gourmetDetailParams.name//
                            , gourmetDetailParams.address//
                            , mPlaceDetail.index//
                            , gourmetDetailParams.getImageList() == null || gourmetDetailParams.getImageList().size() == 0 ? null : gourmetDetailParams.getImageList().get(0).getImageUrl()//
                            , shortUrl //
                            , mPlaceBookingDay);

                        GourmetPreviewActivity.this.finish();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        KakaoLinkManager.newInstance(GourmetPreviewActivity.this).shareGourmet(name//
                            , gourmetDetailParams.name//
                            , gourmetDetailParams.address//
                            , mPlaceDetail.index//
                            , gourmetDetailParams.getImageList() == null || gourmetDetailParams.getImageList().size() == 0 ? null : gourmetDetailParams.getImageList().get(0).getImageUrl()//
                            , "https://mobile.dailyhotel.co.kr/gourmet/" + mPlaceDetail.index //
                            , mPlaceBookingDay);

                        GourmetPreviewActivity.this.finish();
                    }
                }));
            } catch (Exception e)
            {
                showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                    , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                    , new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Util.installPackage(GourmetPreviewActivity.this, "com.kakao.talk");
                        }
                    }, null, null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            GourmetPreviewActivity.this.finish();
                        }
                    }, true);
            }

            AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
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

            GourmetDetailParams gourmetDetailParams = mPlaceDetail.getGourmetDetailParams();

            if (gourmetDetailParams == null)
            {
                return;
            }

            Util.shareNaverMap(GourmetPreviewActivity.this, gourmetDetailParams.name//
                , Double.toString(gourmetDetailParams.latitude), Double.toString(gourmetDetailParams.longitude));

            GourmetPreviewActivity.this.finish();

            AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_NAVER_MAP, null, null);
        }

        @Override
        public void onPlaceDetailClick()
        {
            AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_RESERVATION, null, null);

            setResult(RESULT_OK);
            GourmetPreviewActivity.this.finish();
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

            AnalyticsManager.getInstance(GourmetPreviewActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.PEEK_POP_CLOSE, AnalyticsManager.Label.CLOSE, null);
        }

        @Override
        public void finish()
        {
            GourmetPreviewActivity.this.finish();
        }
    };


    private GourmetPreviewNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new GourmetPreviewNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onGourmetDetailInformation(GourmetDetailParams gourmetDetailParams)
        {
            if (mPlaceDetail == null)
            {
                return;
            }

            try
            {
                mPlaceDetail.setGourmetDetailParams(gourmetDetailParams);

                if (mPlaceReviewScores != null)
                {
                    updatePreviewInformationLayout(mPlaceBookingDay, mPlaceDetail, mPlaceReviewScores.reviewScoreTotalCount);
                }
            } catch (Exception e)
            {
                DailyToast.showToast(GourmetPreviewActivity.this, R.string.act_base_network_connect, DailyToast.LENGTH_LONG);
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

                GourmetDetailParams gourmetDetailParams = mPlaceDetail.getGourmetDetailParams();

                if (gourmetDetailParams != null)
                {
                    gourmetDetailParams.myWish = true;
                    gourmetDetailParams.wishCount++;

                    mPreviewLayout.updateWishInformation(mPlaceReviewScores.reviewScoreTotalCount, gourmetDetailParams.wishCount, gourmetDetailParams.myWish);

                    // 로그인전에는 해당 위시만 갱신하고 로그인 후에는 전체 리스트를 리플래쉬 해야한다.
                    if (mEnteredLogin == true)
                    {
                        Intent intent = new Intent();
                        intent.putExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH, true);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH, intent);
                    } else
                    {
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                }
            } else
            {
                DailyToast.showToast(GourmetPreviewActivity.this, message, DailyToast.LENGTH_SHORT);
            }
        }

        @Override
        public void onRemoveWishList(boolean isSuccess, String message)
        {
            if (isSuccess == true)
            {
                lockUI(false);
                mPreviewLayout.removeWish();

                GourmetDetailParams gourmetDetailParams = mPlaceDetail.getGourmetDetailParams();

                if (gourmetDetailParams != null)
                {
                    gourmetDetailParams.myWish = false;
                    gourmetDetailParams.wishCount--;

                    mPreviewLayout.updateWishInformation(mPlaceReviewScores.reviewScoreTotalCount, gourmetDetailParams.wishCount, gourmetDetailParams.myWish);

                    // 로그인전에는 해당 위시만 갱신하고 로그인 후에는 전체 리스트를 리플래쉬 해야한다.
                    if (mEnteredLogin == true)
                    {
                        Intent intent = new Intent();
                        intent.putExtra(GourmetPreviewActivity.INTENT_EXTRA_DATA_WISH, false);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH, intent);
                    } else
                    {
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                }
            } else
            {
                DailyToast.showToast(GourmetPreviewActivity.this, message, DailyToast.LENGTH_SHORT);
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

            if (mPlaceDetail.getGourmetDetailParams() != null)
            {
                updatePreviewInformationLayout(mPlaceBookingDay, mPlaceDetail, mPlaceReviewScores.reviewScoreTotalCount);
            }
        }


        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            GourmetPreviewActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            GourmetPreviewActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(final int msgCode, final String message)
        {
            GourmetPreviewActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            GourmetPreviewActivity.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(final Call call, final Response response)
        {
            GourmetPreviewActivity.this.onErrorResponse(call, response);
            finish();
        }
    };
}
