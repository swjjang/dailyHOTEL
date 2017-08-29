package com.twoheart.dailyhotel.screen.home;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.daily.dailyhotel.entity.ImageMap;
import com.daily.dailyhotel.entity.StayOutbound;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Prices;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 19..
 */

public class HomeCarouselAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    Context mContext;
    protected LayoutInflater mInflater;
    private ArrayList<CarouselListItem> mList;
    ItemClickListener mItemClickListener;
    private boolean mIsUsePriceLayout;
    protected PaintDrawable mPaintDrawable;

    public interface ItemClickListener
    {
        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    public HomeCarouselAdapter(Context context, ArrayList<CarouselListItem> list, ItemClickListener listener)
    {
        mContext = context;
        mList = list;
        mItemClickListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.list_row_home_carousel_item_layout, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
//        CarouselListItem carouselListItem = getItem(position);
//        if (carouselListItem == null)
//        {
//            return;
//        }
//
//        onBindViewHolder((PlaceViewHolder) holder, carouselListItem, position);

        CarouselListItem item = getItem(position);
        if (item == null)
        {
            return;
        }

        setLayoutMargin((PlaceViewHolder) holder, position);

        holder.itemView.setTag(item);

        switch (item.mType)
        {
            case CarouselListItem.TYPE_HOME_PLACE:
            {
                onBindViewHolderByHomePlace((PlaceViewHolder) holder, item);
                break;
            }

            case CarouselListItem.TYPE_IN_STAY:
            {
                onBindViewHolderByStay((PlaceViewHolder) holder, item);
                break;
            }

            case CarouselListItem.TYPE_OB_STAY:
            {
                onBindViewHolderByStayOutbound((PlaceViewHolder) holder, item);
                break;
            }

            case CarouselListItem.TYPE_GOURMET:
            {
                onBindViewHolderByGourmet((PlaceViewHolder) holder, item);
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByHomePlace(PlaceViewHolder holder, CarouselListItem item)
    {
        final HomePlace place = item.getItem();

        holder.contentImageView.setTag(holder.contentImageView.getId(), item);
        Util.requestImageResize(mContext, holder.contentImageView, place.imageUrl);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.soldoutView.setVisibility(View.GONE);
        //        }

        holder.contentTextView.setText(place.title);

        Prices prices = place.prices;

        if (prices == null || prices.discountPrice == 0 || mIsUsePriceLayout == false)
        {
            holder.priceLayout.setVisibility(mIsUsePriceLayout == false ? View.GONE : View.INVISIBLE);
            holder.contentOriginPriceView.setText("");
            holder.contentDiscountPriceView.setText("");
            holder.contentPersonView.setText("");
        } else
        {
            holder.priceLayout.setVisibility(View.VISIBLE);

            String strPrice = DailyTextUtils.getPriceFormat(mContext, prices.normalPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, prices.discountPrice, false);

            holder.contentDiscountPriceView.setText(strDiscount);

            if (prices.normalPrice <= 0 || prices.normalPrice <= prices.discountPrice)
            {
                holder.contentOriginPriceView.setText("");
            } else
            {
                holder.contentOriginPriceView.setText(strPrice);
                holder.contentOriginPriceView.setPaintFlags(holder.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.contentProvinceView.setText(place.regionName);

        if (place.placeType == Constants.PlaceType.HOTEL)
        {
            holder.contentGradeView.setText(place.details.stayGrade.getName(mContext));
            holder.contentDotImageView.setVisibility(View.VISIBLE);

            holder.contentPersonView.setText("");
            holder.contentPersonView.setVisibility(View.GONE);
        } else if (place.placeType == Constants.PlaceType.FNB)
        {
            // grade
            if (DailyTextUtils.isTextEmpty(place.details.category) == true)
            {
                holder.contentGradeView.setVisibility(View.GONE);
                holder.contentDotImageView.setVisibility(View.GONE);
                holder.contentGradeView.setText("");
            } else
            {
                holder.contentGradeView.setVisibility(View.VISIBLE);
                holder.contentDotImageView.setVisibility(View.VISIBLE);
                holder.contentGradeView.setText(place.details.category);
            }

            if (prices != null && place.details.persons > 1)
            {
                holder.contentPersonView.setText(//
                    mContext.getString(R.string.label_home_person_format, place.details.persons));
                holder.contentPersonView.setVisibility(View.VISIBLE);
            } else
            {
                holder.contentPersonView.setText("");
                holder.contentPersonView.setVisibility(View.GONE);
            }
        } else
        {
            // Stay Outbound 의 경우 PlaceType 이 없음
            holder.contentGradeView.setText("");
            holder.contentDotImageView.setVisibility(View.GONE);

            holder.contentPersonView.setText("");
            holder.contentPersonView.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByStay(PlaceViewHolder holder, CarouselListItem item)
    {
        final Stay stay = item.getItem();

        holder.contentImageView.setTag(holder.contentImageView.getId(), item);
        Util.requestImageResize(mContext, holder.contentImageView, stay.imageUrl);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (stay.isSoldOut == true)
        //        {
        //            holder.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.soldoutView.setVisibility(View.GONE);
        //        }

        holder.contentTextView.setText(stay.name);

        int originPrice = stay.price;
        int discountPrice = stay.discountPrice;

        if (originPrice == 0 || discountPrice == 0 || mIsUsePriceLayout == false)
        {
            holder.priceLayout.setVisibility(mIsUsePriceLayout == false ? View.GONE : View.INVISIBLE);
            holder.contentOriginPriceView.setText("");
            holder.contentDiscountPriceView.setText("");
            holder.contentPersonView.setText("");
        } else
        {
            holder.priceLayout.setVisibility(View.VISIBLE);

            String strPrice = DailyTextUtils.getPriceFormat(mContext, originPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, discountPrice, false);

            holder.contentDiscountPriceView.setText(strDiscount);

            if (originPrice <= 0 || originPrice <= discountPrice)
            {
                holder.contentOriginPriceView.setText("");
            } else
            {
                holder.contentOriginPriceView.setText(strPrice);
                holder.contentOriginPriceView.setPaintFlags(holder.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.contentProvinceView.setText(stay.regionName);

        holder.contentGradeView.setText(stay.getGrade().getName(mContext));
        holder.contentDotImageView.setVisibility(View.VISIBLE);

        holder.contentPersonView.setText("");
        holder.contentPersonView.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByStayOutbound(PlaceViewHolder holder, CarouselListItem item)
    {
        final StayOutbound stayOutbound = item.getItem();

        holder.contentImageView.setTag(holder.contentImageView.getId(), item);
        ImageMap imageMap = stayOutbound.getImageMap();
        String url;

        if (ScreenUtils.getScreenWidth(mContext) >= ScreenUtils.DEFAULT_STAYOUTBOUND_XXHDPI_WIDTH)
        {
            if (DailyTextUtils.isTextEmpty(imageMap.bigUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.bigUrl;
            }
        } else
        {
            if (DailyTextUtils.isTextEmpty(imageMap.mediumUrl) == true)
            {
                url = imageMap.smallUrl;
            } else
            {
                url = imageMap.mediumUrl;
            }
        }

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>()
        {
            @Override
            public void onFailure(String id, Throwable throwable)
            {
                if (throwable instanceof IOException == true)
                {
                    if (url.equalsIgnoreCase(imageMap.bigUrl) == true)
                    {
                        imageMap.bigUrl = null;
                    } else if (url.equalsIgnoreCase(imageMap.mediumUrl) == true)
                    {
                        imageMap.mediumUrl = null;
                    } else
                    {
                        // 작은 이미지를 로딩했지만 실패하는 경우.
                        return;
                    }

                    holder.contentImageView.setImageURI(imageMap.smallUrl);
                }
            }
        };

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()//
            .setControllerListener(controllerListener).setUri(url).build();

        holder.contentImageView.setController(draweeController);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.soldoutView.setVisibility(View.GONE);
        //        }

        holder.priceLayout.setVisibility(mIsUsePriceLayout == false ? View.GONE : View.INVISIBLE);
        holder.contentOriginPriceView.setText("");
        holder.contentDiscountPriceView.setText("");
        holder.contentPersonView.setText("");

        holder.contentTextView.setText(stayOutbound.name);
        //        holder.nameEngTextView.setText("(" + stayOutbound.nameEng + ")");

        holder.contentProvinceView.setText(stayOutbound.city);

        // Stay Outbound 의 경우 PlaceType 이 없음
        holder.contentGradeView.setText("");
        holder.contentDotImageView.setVisibility(View.GONE);

        holder.contentPersonView.setText("");
        holder.contentPersonView.setVisibility(View.GONE);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void onBindViewHolderByGourmet(PlaceViewHolder holder, CarouselListItem item)
    {
        final Gourmet gourmet = item.getItem();

        holder.contentImageView.setTag(holder.contentImageView.getId(), item);
        Util.requestImageResize(mContext, holder.contentImageView, gourmet.imageUrl);

        if (VersionUtils.isOverAPI16() == true)
        {
            holder.gradientBottomView.setBackground(mPaintDrawable);
        } else
        {
            holder.gradientBottomView.setBackgroundDrawable(mPaintDrawable);
        }

        //        // SOLD OUT 표시
        //        if (place.isSoldOut == true)
        //        {
        //            holder.soldoutView.setVisibility(View.VISIBLE);
        //        } else
        //        {
        //            holder.soldoutView.setVisibility(View.GONE);
        //        }

        holder.contentTextView.setText(gourmet.name);

        int originPrice = gourmet.price;
        int discountPrice = gourmet.discountPrice;

        if (originPrice == 0 || discountPrice == 0 || mIsUsePriceLayout == false)
        {
            holder.priceLayout.setVisibility(mIsUsePriceLayout == false ? View.GONE : View.INVISIBLE);
            holder.contentOriginPriceView.setText("");
            holder.contentDiscountPriceView.setText("");
            holder.contentPersonView.setText("");
        } else
        {
            holder.priceLayout.setVisibility(View.VISIBLE);

            String strPrice = DailyTextUtils.getPriceFormat(mContext, originPrice, false);
            String strDiscount = DailyTextUtils.getPriceFormat(mContext, discountPrice, false);

            holder.contentDiscountPriceView.setText(strDiscount);

            if (originPrice <= 0 || originPrice <= discountPrice)
            {
                holder.contentOriginPriceView.setText("");
            } else
            {
                holder.contentOriginPriceView.setText(strPrice);
                holder.contentOriginPriceView.setPaintFlags(holder.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        holder.contentProvinceView.setText(gourmet.regionName);

        // grade
        if (DailyTextUtils.isTextEmpty(gourmet.category) == true)
        {
            holder.contentGradeView.setVisibility(View.GONE);
            holder.contentDotImageView.setVisibility(View.GONE);
            holder.contentGradeView.setText("");
        } else
        {
            holder.contentGradeView.setVisibility(View.VISIBLE);
            holder.contentDotImageView.setVisibility(View.VISIBLE);
            holder.contentGradeView.setText(gourmet.category);
        }

        if (gourmet.persons > 1)
        {
            holder.contentPersonView.setText(//
                mContext.getString(R.string.label_home_person_format, gourmet.persons));
            holder.contentPersonView.setVisibility(View.VISIBLE);
        } else
        {
            holder.contentPersonView.setText("");
            holder.contentPersonView.setVisibility(View.GONE);
        }
    }

//    public void onBindViewHolder(PlaceViewHolder holder, CarouselListItem place, final int position)
//    {
//        // left view 생성
//        holder.leftLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
//
//        // right view 생성
//        int size = getItemCount();
//        boolean isLast = size <= 0 || (position == size - 1);
//
//        ViewGroup.LayoutParams rightViewParam = holder.rightLayout.getLayoutParams();
//        int rightViewWidth = ScreenUtils.dpToPx(mContext, isLast == true ? 15 : 12);
//
//        if (rightViewParam == null)
//        {
//            rightViewParam = new ViewGroup.LayoutParams(rightViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//        } else
//        {
//            rightViewParam.width = rightViewWidth;
//        }
//
//        holder.rightLayout.setLayoutParams(rightViewParam);
//        // left, right view end
//
//        holder.contentImageView.setTag(holder.contentImageView.getId(), position);
//
//        Util.requestImageResize(mContext, holder.contentImageView, place.imageUrl);
//
//        //        // SOLD OUT 표시
//        //        if (place.isSoldOut == true)
//        //        {
//        //            holder.soldoutView.setVisibility(View.VISIBLE);
//        //        } else
//        //        {
//        //            holder.soldoutView.setVisibility(View.GONE);
//        //        }
//
//        holder.contentTextView.setText(place.title);
//
//        Prices prices = place.prices;
//
//        if (prices == null)
//        {
//            holder.contentOriginPriceView.setText("");
//            holder.contentDiscountPriceView.setText("");
//            holder.contentPersonView.setText("");
//        } else
//        {
//            String strPrice = DailyTextUtils.getPriceFormat(mContext, prices.normalPrice, false);
//            String strDiscount = DailyTextUtils.getPriceFormat(mContext, prices.discountPrice, false);
//
//            holder.contentDiscountPriceView.setText(strDiscount);
//
//            if (prices.normalPrice <= 0 || prices.normalPrice <= prices.discountPrice)
//            {
//                holder.contentOriginPriceView.setText("");
//            } else
//            {
//                holder.contentOriginPriceView.setText(strPrice);
//                holder.contentOriginPriceView.setPaintFlags(holder.contentOriginPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            }
//        }
//
//        holder.contentProvinceView.setText(place.regionName);
//
//        if (place.placeType == Constants.PlaceType.HOTEL)
//        {
//            holder.contentGradeView.setText(place.details.stayGrade.getName(mContext));
//            holder.contentDotImageView.setVisibility(View.VISIBLE);
//
//            holder.contentPersonView.setText("");
//            holder.contentPersonView.setVisibility(View.GONE);
//        } else if (place.placeType == Constants.PlaceType.FNB)
//        {
//            // grade
//            if (DailyTextUtils.isTextEmpty(place.details.category) == true)
//            {
//                holder.contentGradeView.setVisibility(View.GONE);
//                holder.contentDotImageView.setVisibility(View.GONE);
//                holder.contentGradeView.setText("");
//            } else
//            {
//                holder.contentGradeView.setVisibility(View.VISIBLE);
//                holder.contentDotImageView.setVisibility(View.VISIBLE);
//                holder.contentGradeView.setText(place.details.category);
//            }
//
//            if (prices != null && place.details.persons > 1)
//            {
//                holder.contentPersonView.setText(//
//                    mContext.getString(R.string.label_home_person_format, place.details.persons));
//                holder.contentPersonView.setVisibility(View.VISIBLE);
//            } else
//            {
//                holder.contentPersonView.setText("");
//                holder.contentPersonView.setVisibility(View.GONE);
//            }
//        } else
//        {
//            // Stay Outbound 의 경우 PlaceType 이 없음
//            holder.contentGradeView.setText("");
//            holder.contentDotImageView.setVisibility(View.GONE);
//
//            holder.contentPersonView.setText("");
//            holder.contentPersonView.setVisibility(View.GONE);
//        }
//
//        holder.itemView.setTag(place);
//        holder.itemView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (mItemClickListener != null)
//                {
//                    mItemClickListener.onItemClick(v, position);
//                }
//            }
//        });
//
//        if (Util.supportPreview(mContext) == true)
//        {
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
//            {
//                @Override
//                public boolean onLongClick(View v)
//                {
//                    if (mItemClickListener == null)
//                    {
//                        return false;
//                    } else
//                    {
//                        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
//                        vibrator.vibrate(70);
//
//                        mItemClickListener.onItemLongClick(v, position);
//
//                        return true;
//                    }
//                }
//            });
//        }
//    }

    public CarouselListItem getItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.get(position);
    }

    public int getItemCount()
    {
        return mList != null && mList.size() > 0 ? mList.size() : 0;
    }

    public ArrayList<CarouselListItem> getData()
    {
        return mList;
    }

    public void setData(ArrayList<CarouselListItem> list)
    {
        mList = list;
    }

    public void setUsePriceLayout(boolean isUse)
    {
        mIsUsePriceLayout = isUse;
    }

    private void setLayoutMargin(PlaceViewHolder holder, int position)
    {
        if (holder == null)
        {
            return;
        }

        int outSide = ScreenUtils.dpToPx(mContext, 15d);
        int inSide = ScreenUtils.dpToPx(mContext, 12d) / 2;

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        params.leftMargin = position == 0 ? outSide : inSide;
        params.rightMargin = position == getItemCount() - 1 ? outSide : inSide;
        holder.itemView.setLayoutParams(params);
    }

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.38f};

        mPaintDrawable = new PaintDrawable();
        mPaintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        mPaintDrawable.setShaderFactory(sf);
    }

    private class PlaceViewHolder extends RecyclerView.ViewHolder
    {
        SimpleDraweeView contentImageView;
        ImageView soldoutView;
        DailyTextView contentTextView;
        DailyTextView contentDiscountPriceView;
        DailyTextView contentOriginPriceView;
        DailyTextView contentPersonView;
        DailyTextView contentProvinceView;
        DailyTextView contentGradeView;
        View contentDotImageView;
        View gradientBottomView;
        View priceLayout;
        View leftLayout;
        View rightLayout;

        public PlaceViewHolder(View view)
        {
            super(view);

            contentImageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
            soldoutView = (ImageView) view.findViewById(R.id.soldoutView);
            contentTextView = (DailyTextView) view.findViewById(R.id.contentTextView);
            contentDiscountPriceView = (DailyTextView) view.findViewById(R.id.contentDiscountPriceView);
            contentOriginPriceView = (DailyTextView) view.findViewById(R.id.contentOriginPriceView);
            contentPersonView = (DailyTextView) view.findViewById(R.id.contentPersonView);
            contentProvinceView = (DailyTextView) view.findViewById(R.id.contentProvinceView);
            contentGradeView = (DailyTextView) view.findViewById(R.id.contentGradeView);
            contentDotImageView = view.findViewById(R.id.contentDotImageView);
            gradientBottomView = view.findViewById(R.id.gradientBottomView);
            priceLayout = view.findViewById(R.id.priceLayout);
            leftLayout = view.findViewById(R.id.leftLayout);
            rightLayout = view.findViewById(R.id.rightLayout);

            int width = contentImageView.getWidth() == 0 ? ScreenUtils.dpToPx(mContext, 239) : contentImageView.getWidth();
            int height = ScreenUtils.getRatioHeightType16x9(width);

            contentImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            contentImageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            contentImageView.setLayoutParams(layoutParams);
            soldoutView.setLayoutParams(layoutParams);
        }
    }

//    private class PlaceViewHolder extends RecyclerView.ViewHolder
//    {
//        SimpleDraweeView contentImageView;
//        ImageView soldoutView;
//        DailyTextView contentTextView;
//        DailyTextView contentDiscountPriceView;
//        DailyTextView contentOriginPriceView;
//        DailyTextView contentPersonView;
//        DailyTextView contentProvinceView;
//        DailyTextView contentGradeView;
//        View contentDotImageView;
//        View leftLayout;
//        View rightLayout;
//
//        public PlaceViewHolder(View view)
//        {
//            super(view);
//
//            contentImageView = (SimpleDraweeView) view.findViewById(R.id.contentImageView);
//            soldoutView = (ImageView) view.findViewById(R.id.soldoutView);
//            contentTextView = (DailyTextView) view.findViewById(R.id.contentTextView);
//            contentDiscountPriceView = (DailyTextView) view.findViewById(R.id.contentDiscountPriceView);
//            contentOriginPriceView = (DailyTextView) view.findViewById(R.id.contentOriginPriceView);
//            contentPersonView = (DailyTextView) view.findViewById(R.id.contentPersonView);
//            contentProvinceView = (DailyTextView) view.findViewById(R.id.contentProvinceView);
//            contentGradeView = (DailyTextView) view.findViewById(R.id.contentGradeView);
//            contentDotImageView = view.findViewById(R.id.contentDotImageView);
//            leftLayout = view.findViewById(R.id.leftLayout);
//            rightLayout = view.findViewById(R.id.rightLayout);
//
//            int width = contentImageView.getWidth() == 0 ? ScreenUtils.dpToPx(mContext, 239) : contentImageView.getWidth();
//            int height = ScreenUtils.getRatioHeightType16x9(width);
//
//            contentImageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
//            contentImageView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder);
//
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
//            contentImageView.setLayoutParams(layoutParams);
//            soldoutView.setLayoutParams(layoutParams);
//        }
//    }
}
