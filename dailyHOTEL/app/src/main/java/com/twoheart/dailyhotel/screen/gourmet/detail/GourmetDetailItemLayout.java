package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyImageView;
import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ListRowDetailProductDataBinding;
import com.twoheart.dailyhotel.model.DetailInformation;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.PlaceReviewScores;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;
import com.twoheart.dailyhotel.place.layout.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 6. 5..
 */

public class GourmetDetailItemLayout extends LinearLayout
{
    private static final int GRID_COLUMN_COUNT = 5;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private GourmetDetailLayout.OnEventListener mEventListener;
    private View.OnTouchListener mEmptyViewOnTouchListener;

    private GourmetDetail mGourmetDetail;
    private GourmetBookingDay mGourmetBookingDay;
    private PlaceReviewScores mPlaceReviewScores;
    protected View mGourmetTitleLayout;
    private LinearLayout mMoreLayout;

    private int mDpi;
    private int mFirstProductIndex;
    private int mLastProductIndex;

    public GourmetDetailItemLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public GourmetDetailItemLayout(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public GourmetDetailItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GourmetDetailItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    public void initLayout()
    {
        setOrientation(LinearLayout.VERTICAL);

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnEventListener(GourmetDetailLayout.OnEventListener onEventListener)
    {
        mEventListener = onEventListener;
    }

    public void setEmptyViewOnTouchListener(View.OnTouchListener emptyViewOnTouchListener)
    {
        mEmptyViewOnTouchListener = emptyViewOnTouchListener;
    }

    public void setDpi(int dpi)
    {
        mDpi = dpi;
    }

    public void setData(GourmetBookingDay gourmetBookingDay, GourmetDetail gourmetDetail, PlaceReviewScores placeReviewScores)
    {
        mGourmetBookingDay = gourmetBookingDay;
        mGourmetDetail = gourmetDetail;
        mPlaceReviewScores = placeReviewScores;

        setView();
    }

    public int getFirstProductIndex()
    {
        return mFirstProductIndex;
    }

    public int getLastProductIndex()
    {
        return mLastProductIndex;
    }

    private void setView()
    {
        removeAllViews();

        // 빈화면
        View emptyView = getEmptyView(mLayoutInflater, mEmptyViewOnTouchListener);
        if (emptyView != null)
        {
            addView(emptyView);
        }

        // 레스토랑 등급과 이름.
        View titleView = getTitleView(mLayoutInflater, mGourmetDetail, mEventListener);
        if (titleView != null)
        {
            addView(titleView);
        }

        // D Benefit
        View benefitView = getBenefitView(mLayoutInflater, mGourmetDetail);
        if (benefitView != null)
        {
            addView(benefitView);
        } else
        {
            // 베네핏이 없으면 정보화면의 상단 라인으로 대체한다.
            View view = new View(mContext);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 1));
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.color.default_line_cf0f0f0);
            addView(view);
        }

        mFirstProductIndex = getChildCount();

        // 상품 정보
        setProductListLayout(mLayoutInflater, this, mGourmetDetail.getProductList());

        mLastProductIndex = getChildCount() - 1;

        // 주소 및 맵
        View addressView = getAddressView(mLayoutInflater, mGourmetDetail, mEventListener);
        if (addressView != null)
        {
            addView(addressView);
        }

        // 편의 시설
        View amenitiesView = getAmenitiesView(mLayoutInflater, mGourmetDetail);
        if (amenitiesView != null)
        {
            addView(amenitiesView);
        }

        // 정보
        View informationView = getInformationView(mLayoutInflater, mGourmetDetail);
        if (informationView != null)
        {
            addView(informationView);
        }

        // 문의 하기
        View conciergeView = getConciergeView(mLayoutInflater, mEventListener);
        if (conciergeView != null)
        {
            addView(conciergeView);
        }
    }

    public View getTitleLayout()
    {
        return mGourmetTitleLayout;
    }

    /**
     * 빈화면
     *
     * @param layoutInflater
     * @param onTouchListener
     * @return
     */
    private View getEmptyView(LayoutInflater layoutInflater, View.OnTouchListener onTouchListener)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail01, this, false);

        View emptyView = view.findViewById(R.id.imageEmptyHeight);
        emptyView.getLayoutParams().height = PlaceDetailLayout.getImageLayoutHeight(mContext);

        emptyView.setClickable(true);
        emptyView.setOnTouchListener(onTouchListener);

        return view;
    }

    /**
     * 등급 및 이름
     *
     * @param layoutInflater
     * @param gourmetDetail
     * @param onEventListener
     * @return
     */
    private View getTitleView(LayoutInflater layoutInflater, final GourmetDetail gourmetDetail, GourmetDetailLayout.OnEventListener onEventListener)
    {
        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        View view = layoutInflater.inflate(R.layout.list_row_gourmet_detail02, this, false);

        mGourmetTitleLayout = view.findViewById(R.id.gourmetTitleLayout);

        // 등급
        TextView gradeTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetGradeTextView);

        if (DailyTextUtils.isTextEmpty(gourmetDetailParams.category) == true)
        {
            gradeTextView.setVisibility(View.GONE);
        } else
        {
            gradeTextView.setVisibility(View.VISIBLE);
            gradeTextView.setText(gourmetDetailParams.category);
        }

        // 소분류 등급
        TextView subGradeTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetSubGradeTextView);

        if (DailyTextUtils.isTextEmpty(gourmetDetailParams.categorySub) == true)
        {
            subGradeTextView.setVisibility(View.GONE);
        } else
        {
            subGradeTextView.setVisibility(View.VISIBLE);
            subGradeTextView.setText(gourmetDetailParams.categorySub);
        }

        // 호텔명
        TextView placeNameTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.gourmetNameTextView);
        placeNameTextView.setText(gourmetDetailParams.name);

        // 만족도
        TextView satisfactionView = (TextView) mGourmetTitleLayout.findViewById(R.id.satisfactionView);

        if (gourmetDetailParams.ratingShow == false)
        {
            satisfactionView.setVisibility(View.GONE);
        } else
        {
            satisfactionView.setVisibility(View.VISIBLE);
            DecimalFormat decimalFormat = new DecimalFormat("###,##0");
            satisfactionView.setText(mContext.getString(R.string.label_gourmet_detail_satisfaction, //
                gourmetDetailParams.ratingValue, decimalFormat.format(gourmetDetailParams.ratingPersons)));
        }

        // 리뷰
        TextView trueReviewTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.trueReviewTextView);

        if (mPlaceReviewScores == null)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            setTrueReviewCount(mPlaceReviewScores.reviewScoreTotalCount);
        }

        // 할인 쿠폰
        View couponLayout = view.findViewById(R.id.couponLayout);

        if (gourmetDetail.hasCoupon == true)
        {
            couponLayout.setVisibility(View.VISIBLE);

            View downloadCouponLayout = couponLayout.findViewById(R.id.downloadCouponLayout);

            downloadCouponLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (onEventListener == null)
                    {
                        return;
                    }

                    onEventListener.onDownloadCouponClick();
                }
            });
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        // 날짜
        TextView dayTextView = (TextView) view.findViewById(R.id.dayTextView);

        dayTextView.setText(mGourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)"));

        View dateInformationLayout = view.findViewById(R.id.dateInformationLayout);
        dateInformationLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.onCalendarClick();
            }
        });

        return view;
    }

    public void setTrueReviewCount(int count)
    {
        if (mGourmetTitleLayout == null)
        {
            return;
        }

        TextView trueReviewTextView = (TextView) mGourmetTitleLayout.findViewById(R.id.trueReviewTextView);

        if (count == 0)
        {
            trueReviewTextView.setVisibility(View.GONE);
        } else
        {
            trueReviewTextView.setVisibility(View.VISIBLE);
            trueReviewTextView.setText(mContext.getString(R.string.label_detail_view_review_go, count));
            trueReviewTextView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mEventListener == null)
                    {
                        return;
                    }

                    mEventListener.onReviewClick();
                }
            });
        }
    }

    /**
     * 주소 및 맵
     *
     * @param layoutInflater
     * @param gourmetDetail
     * @param onEventListener
     * @return
     */
    private View getAddressView(LayoutInflater layoutInflater, GourmetDetail gourmetDetail, GourmetDetailLayout.OnEventListener onEventListener)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail03, this, false);

        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();

        // 주소지
        final TextView hotelAddressTextView = (TextView) view.findViewById(R.id.detailAddressTextView);

        final String address = gourmetDetailParams.address;
        hotelAddressTextView.setText(address);

        View clipAddress = view.findViewById(R.id.copyAddressView);
        clipAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.clipAddress(address);
            }
        });

        //길찾기
        View navigatorView = view.findViewById(R.id.navigatorView);
        navigatorView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener == null)
                {
                    return;
                }

                onEventListener.showNavigatorDialog();
            }
        });

        ImageView mapImageView = (ImageView) view.findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener != null)
                {
                    onEventListener.showMap();
                }
            }
        });

        return view;
    }

    /**
     * 편의시설
     *
     * @param layoutInflater
     * @return
     */
    private View getAmenitiesView(LayoutInflater layoutInflater, GourmetDetail gourmetDetail)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail_pictogram, this, false);
        if (view == null || gourmetDetail == null)
        {
            return view;
        }

        List<GourmetDetail.Pictogram> pictogramList = gourmetDetail.getPictogramList();
        if (pictogramList == null || pictogramList.size() == 0)
        {
            return null;
        }

        android.support.v7.widget.GridLayout gridLayout = (android.support.v7.widget.GridLayout) view.findViewById(R.id.amenitiesGridLayout);
        gridLayout.removeAllViews();

        boolean isSingleLine = pictogramList == null || pictogramList.size() <= GRID_COLUMN_COUNT;

        for (GourmetDetail.Pictogram pictogram : pictogramList)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, pictogram, isSingleLine));
        }

        int columnCount = pictogramList.size() % GRID_COLUMN_COUNT;

        if (columnCount != 0)
        {
            int addEmptyViewCount = GRID_COLUMN_COUNT - columnCount;
            for (int i = 0; i < addEmptyViewCount; i++)
            {
                gridLayout.addView(getGridLayoutItemView(mContext, GourmetDetail.Pictogram.none, isSingleLine));
            }
        }
        return view;
    }

    protected DailyTextView getGridLayoutItemView(Context context, GourmetDetail.Pictogram pictogram, boolean isSingleLine)
    {
        DailyTextView dailyTextView = new DailyTextView(mContext);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(mContext.getResources().getColorStateList(R.color.default_text_c323232));
        dailyTextView.setText(pictogram.getName(context));
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, pictogram.getImageResId(), 0, 0);
        dailyTextView.setDrawableVectorTint(R.color.default_background_c454545);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        if (isSingleLine == true)
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 15));
        } else
        {
            dailyTextView.setPadding(0, ScreenUtils.dpToPx(context, 10), 0, ScreenUtils.dpToPx(context, 2));
        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    /**
     * 고메 Benefit
     *
     * @param layoutInflater
     * @param gourmetDetail
     * @return
     */
    private View getBenefitView(LayoutInflater layoutInflater, GourmetDetail gourmetDetail)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail_benefit, this, false);
        if (view == null || gourmetDetail == null)
        {
            return view;
        }

        GourmetDetailParams gourmetDetailParams = gourmetDetail.getGourmetDetailParmas();
        final String benefit = gourmetDetailParams.benefit;
        if (DailyTextUtils.isTextEmpty(benefit) == true)
        {
            // benefit 이 없으면 상단 라인으로 대체 하기때문에 비어있으면 리턴
            return null;
        }

        final TextView benefitTitleTextView = (TextView) view.findViewById(R.id.benefitTitleTextView);
        final LinearLayout benefitMessagesLayout = (LinearLayout) view.findViewById(R.id.benefitMessagesLayout);

        benefitTitleTextView.setText(benefit);

        List<String> mBenefitInformation = gourmetDetail.getBenefitList();

        if (mBenefitInformation != null)
        {
            benefitMessagesLayout.removeAllViews();

            for (String information : mBenefitInformation)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail_benefit_text, benefitMessagesLayout, false);
                TextView textView = (TextView) childGroup.findViewById(R.id.textView);
                textView.setText(information);

                benefitMessagesLayout.addView(childGroup);
            }
        }

        return view;
    }

    private void setProductListLayout(LayoutInflater layoutInflater, ViewGroup parent, List<GourmetProduct> gourmetProductList)
    {
        if (layoutInflater == null || gourmetProductList == null || gourmetProductList.size() == 0)
        {
            return;
        }

        final int DEFAULT_SHOW_PRODUCT_COUNT = 3;
        int size = gourmetProductList.size();

        if (size > DEFAULT_SHOW_PRODUCT_COUNT)
        {
            if (mMoreLayout != null)
            {
                mMoreLayout.removeAllViews();
                mMoreLayout = null;
            }

            mMoreLayout = new LinearLayout(mContext);
            mMoreLayout.setOrientation(LinearLayout.VERTICAL);

            int firstPosition = parent.getChildCount();

            for (int i = 0; i < size; i++)
            {
                if (i < DEFAULT_SHOW_PRODUCT_COUNT)
                {
                    setProductLayout(layoutInflater, parent, gourmetProductList.get(i));
                } else if (i == DEFAULT_SHOW_PRODUCT_COUNT)
                {
                    DailyTextView dailyTextView = new DailyTextView(mContext);
                    dailyTextView.setText(String.format(Locale.KOREA, "%d개 상품 모두 보기 ", size));
                    dailyTextView.setGravity(Gravity.CENTER);
                    dailyTextView.setBackgroundResource(R.color.white);
                    dailyTextView.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Integer height = (Integer) mMoreLayout.getTag();

                            if (height == null)
                            {
                                return;
                            }

                            if (mMoreLayout.getHeight() == 0)
                            {
                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, height);
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                                {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                                    {
                                        if (valueAnimator == null)
                                        {
                                            return;
                                        }

                                        int val = (int) valueAnimator.getAnimatedValue();
                                        ViewGroup.LayoutParams layoutParams = mMoreLayout.getLayoutParams();
                                        layoutParams.height = val;
                                        mMoreLayout.requestLayout();
                                    }
                                });
                                valueAnimator.setDuration(200);
                                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                                valueAnimator.addListener(new Animator.AnimatorListener()
                                {
                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        valueAnimator.removeAllUpdateListeners();
                                        valueAnimator.removeAllListeners();

                                        dailyTextView.setText("접기");
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation)
                                    {

                                    }
                                });

                                valueAnimator.start();
                            } else
                            {
                                ScrollView scrollView = (ScrollView) GourmetDetailItemLayout.this.getParent();
                                scrollView.smoothScrollTo(0, (int) getChildAt(firstPosition).getY() - mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height));

                                ValueAnimator valueAnimator = ValueAnimator.ofInt(height, 0);
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                                {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                                    {
                                        if (valueAnimator == null)
                                        {
                                            return;
                                        }

                                        int val = (int) valueAnimator.getAnimatedValue();
                                        ViewGroup.LayoutParams layoutParams = mMoreLayout.getLayoutParams();
                                        layoutParams.height = val;
                                        mMoreLayout.requestLayout();
                                    }
                                });
                                valueAnimator.setDuration(200);
                                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                                valueAnimator.addListener(new Animator.AnimatorListener()
                                {
                                    @Override
                                    public void onAnimationStart(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        valueAnimator.removeAllUpdateListeners();
                                        valueAnimator.removeAllListeners();

                                        dailyTextView.setText(String.format(Locale.KOREA, "%d개 상품 모두 보기 ", size));
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation)
                                    {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation)
                                    {

                                    }
                                });

                                valueAnimator.start();
                            }
                        }
                    });

                    parent.addView(mMoreLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    parent.addView(dailyTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(mContext, 45)));

                    setProductLayout(layoutInflater, mMoreLayout, gourmetProductList.get(i));
                } else
                {
                    setProductLayout(layoutInflater, mMoreLayout, gourmetProductList.get(i));
                }
            }

            mMoreLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    mMoreLayout.setTag(mMoreLayout.getHeight());

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMoreLayout.getLayoutParams();
                    layoutParams.height = 0;
                    mMoreLayout.requestLayout();
                }
            });
        } else
        {
            for (GourmetProduct gourmetProduct : gourmetProductList)
            {
                setProductLayout(layoutInflater, parent, gourmetProduct);
            }
        }
    }

    private void setProductLayout(LayoutInflater layoutInflater, ViewGroup parent, GourmetProduct gourmetProduct)
    {
        if (layoutInflater == null || parent == null || gourmetProduct == null)
        {
            return;
        }

        ListRowDetailProductDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_row_detail_product_data, parent, true);

        viewDataBinding.contentsLayout.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mEventListener != null)
                {
                    mEventListener.onProductClick(gourmetProduct);
                }
            }
        });

        boolean hasThumbnail = true;

        ProductImageInformation productImageInformation = gourmetProduct.getPrimaryImage();
        if (productImageInformation == null)
        {
            hasThumbnail = false;
        } else
        {
            String url;
            if (mDpi <= 240)
            {
                url = "android_gourmet_product_hdpi";
            } else if (mDpi <= 480)
            {
                url = "android_gourmet_product_xhdpi";
            } else
            {
                url = "android_gourmet_product_xxxhdpi";
            }

            viewDataBinding.simpleDraweeView.setImageURI(Uri.parse(productImageInformation.imageUrl + "?impolicy=" + url));
            viewDataBinding.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder_s);
        }

        viewDataBinding.productNameTextView.setText(gourmetProduct.ticketName);

        if (hasThumbnail == false)
        {
            viewDataBinding.simpleDraweeView.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.simpleDraweeView.setVisibility(View.VISIBLE);
        }

        viewDataBinding.contentsList.removeAllViews();

        if (DailyTextUtils.isTextEmpty(gourmetProduct.menuBenefit) == true && DailyTextUtils.isTextEmpty(gourmetProduct.needToKnow) == true//
            && DailyTextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == true)
        {
            viewDataBinding.contentsList.setVisibility(View.GONE);
        } else
        {
            viewDataBinding.contentsList.setVisibility(View.VISIBLE);

            // 베네핏
            if (DailyTextUtils.isTextEmpty(gourmetProduct.menuBenefit) == false)
            {
                addProductSubInformation(layoutInflater, viewDataBinding.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, false);
            }

            // 이용 시간
            if (DailyTextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == false)
            {
                String timeFormat = mContext.getString(R.string.label_office_hours) + " " + String.format(Locale.KOREA, "%s ~ %s", gourmetProduct.openTime, gourmetProduct.closeTime);

                //                if (DailyTextUtils.isTextEmpty(gourmetProduct.lastOrderTime) == false)
                //                {
                //                    timeFormat += " " + mContext.getString(R.string.label_gourmet_product_lastorder, gourmetProduct.lastOrderTime);
                //                }

                addProductSubInformation(layoutInflater, viewDataBinding.contentsList, timeFormat, R.drawable.ic_detail_item_03_time, true);
            }

            // 확인 사항
            //            if (DailyTextUtils.isTextEmpty(gourmetProduct.needToKnow) == false)
            //            {
            //                addProductSubInformation(layoutInflater, viewDataBinding.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, true);
            //            }
        }

        String price = DailyTextUtils.getPriceFormat(mContext, gourmetProduct.price, false);
        String discountPrice = DailyTextUtils.getPriceFormat(mContext, gourmetProduct.discountPrice, false);

        if (gourmetProduct.price <= 0 || gourmetProduct.price <= gourmetProduct.discountPrice)
        {
            viewDataBinding.priceTextView.setVisibility(View.GONE);
            viewDataBinding.priceTextView.setText(null);
        } else
        {
            viewDataBinding.priceTextView.setVisibility(View.VISIBLE);
            viewDataBinding.priceTextView.setText(price);
            viewDataBinding.priceTextView.setPaintFlags(viewDataBinding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewDataBinding.discountPriceTextView.setText(discountPrice);
    }

    private void addProductSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, int iconResId, boolean hasTopMargin)
    {
        if (layoutInflater == null || viewGroup == null || DailyTextUtils.isTextEmpty(contentText) == true)
        {
            return;
        }

        View textLayout = layoutInflater.inflate(R.layout.list_row_detail_product_text, viewGroup, false);
        viewGroup.addView(textLayout);

        if (hasTopMargin == true)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 6);
            textLayout.setLayoutParams(layoutParams);
        }

        DailyImageView iconImageView = (DailyImageView) textLayout.findViewById(R.id.iconImageView);
        iconImageView.setVectorImageResource(iconResId);

        TextView textView = (TextView) textLayout.findViewById(R.id.textView);
        textView.setText(contentText);
    }


    /**
     * 정보
     *
     * @return
     */
    private View getInformationView(LayoutInflater layoutInflater, GourmetDetail gourmetDetail)
    {

        if (layoutInflater == null || gourmetDetail == null)
        {
            return null;
        }

        ViewGroup viewGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail04, this, false);
        if (viewGroup == null)
        {
            return null;
        }

        List<DetailInformation> detailInformationList = gourmetDetail.getDetailList();

        if (detailInformationList != null)
        {
            viewGroup.removeAllViews();

            for (DetailInformation detailInformation : detailInformationList)
            {
                ViewGroup childGroup = (ViewGroup) layoutInflater.inflate(R.layout.list_row_detail05, viewGroup, false);

                makeInformationLayout(layoutInflater, childGroup, detailInformation);

                viewGroup.addView(childGroup);
            }
        }

        return viewGroup;
    }

    private void makeInformationLayout(LayoutInflater layoutInflater, ViewGroup viewGroup, DetailInformation information)
    {
        if (layoutInflater == null || viewGroup == null || information == null)
        {
            return;
        }

        LinearLayout contentsLayout = (LinearLayout) viewGroup.findViewById(R.id.contentsList);
        contentsLayout.removeAllViews();

        TextView titleTextView = (TextView) viewGroup.findViewById(R.id.titleTextView);
        titleTextView.setText(information.title);

        List<String> contentsList = information.getContentsList();

        if (contentsList != null)
        {
            int size = contentsList.size();

            for (int i = 0; i < size; i++)
            {
                String contentText = contentsList.get(i);

                if (DailyTextUtils.isTextEmpty(contentText) == true)
                {
                    continue;
                }

                View textLayout = layoutInflater.inflate(R.layout.list_row_detail_text, null, false);
                TextView textView = (TextView) textLayout.findViewById(R.id.textView);
                textView.setText(contentText);

                contentsLayout.addView(textLayout);
            }
        }
    }

    /**
     * 문의 상담
     *
     * @param layoutInflater
     * @param onEventListener
     * @return
     */
    private View getConciergeView(LayoutInflater layoutInflater, GourmetDetailLayout.OnEventListener onEventListener)
    {
        View view = layoutInflater.inflate(R.layout.list_row_detail07, this, false);
        if (view == null)
        {
            return null;
        }

        view.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        TextView conciergeTimeTextView = (TextView) view.findViewById(R.id.conciergeTimeTextView);

        String[] hour = DailyPreference.getInstance(mContext).getOperationTime().split("\\,");

        String startHour = hour[0];
        String endHour = hour[1];

        String[] lunchTimes = DailyRemoteConfigPreference.getInstance(mContext).getRemoteConfigOperationLunchTime().split("\\,");
        String startLunchTime = lunchTimes[0];
        String endLunchTime = lunchTimes[1];

        conciergeTimeTextView.setText(mContext.getString(R.string.message_consult02, startHour, endHour, startLunchTime, endLunchTime));

        View conciergeLayout = view.findViewById(R.id.conciergeLayout);
        conciergeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onEventListener != null)
                {
                    onEventListener.onConciergeClick();
                }
            }
        });

        return view;
    }
}
