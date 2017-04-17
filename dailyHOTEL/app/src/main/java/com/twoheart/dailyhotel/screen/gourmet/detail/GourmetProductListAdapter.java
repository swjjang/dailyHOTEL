package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.network.model.ProductImageInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class GourmetProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<PlaceViewItem> mGourmetProductList;
    OnProductClickListener mOnProductClickListener;

    private int mDpi;

    // ABTest
    private boolean mIsLeft;

    public interface OnProductClickListener
    {
        void onProductDetailClick(int position, int where);

        void onReservationClick(int position);
    }

    public GourmetProductListAdapter(Context context, List<PlaceViewItem> arrayList, OnProductClickListener listener)
    {
        mContext = context;
        mOnProductClickListener = listener;

        mGourmetProductList = new ArrayList<>();
        mGourmetProductList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(Collection<PlaceViewItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mGourmetProductList.clear();
        mGourmetProductList.addAll(collection);
    }

    public PlaceViewItem getItem(int position)
    {
        if (position < 0 || mGourmetProductList.size() <= position)
        {
            return null;
        }

        return mGourmetProductList.get(position);
    }

    public void setDpi(int dpi)
    {
        mDpi = dpi;
    }

    public void setThumbnailLocation(boolean isLeft)
    {
        mIsLeft = isLeft;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mGourmetProductList.get(position).mType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case PlaceViewItem.TYPE_ENTRY:
            {
                View view;

                if (mIsLeft == true)
                {
                    view = mInflater.inflate(R.layout.list_row_detail_product_left, parent, false);
                } else
                {
                    view = mInflater.inflate(R.layout.list_row_detail_product, parent, false);
                }

                return new ProductInformationViewHolder(view);
            }

            case PlaceViewItem.TYPE_FOOTER_VIEW:
            {
                View view = mInflater.inflate(R.layout.list_row_product_footer, parent, false);

                return new FooterViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PlaceViewItem item = getItem(position);

        if (item == null)
        {
            return;
        }

        switch (item.mType)
        {
            case PlaceViewItem.TYPE_ENTRY:
                onBindViewHolder((ProductInformationViewHolder) holder, position, item);
                break;

            case PlaceViewItem.TYPE_FOOTER_VIEW:
                break;
        }
    }

    private void onBindViewHolder(ProductInformationViewHolder productInformationViewHolder, int position, PlaceViewItem placeViewItem)
    {
        GourmetProduct gourmetProduct = placeViewItem.getItem();

        if (gourmetProduct == null)
        {
            return;
        }

        productInformationViewHolder.contentsLayout.setTag(position);
        productInformationViewHolder.contentsLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnProductClickListener.onProductDetailClick((int) v.getTag(), 2);
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

            productInformationViewHolder.simpleDraweeView.setImageURI(Uri.parse(productImageInformation.imageUrl + "?impolicy=" + url));
            productInformationViewHolder.simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.layerlist_placeholder_s);
        }

        productInformationViewHolder.productNameTextView.setText(gourmetProduct.ticketName);

        if (hasThumbnail == false)
        {
            productInformationViewHolder.simpleDraweeView.setVisibility(View.GONE);
        } else
        {
            productInformationViewHolder.simpleDraweeView.setVisibility(View.VISIBLE);
        }

        productInformationViewHolder.contentsList.removeAllViews();

        if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.menuBenefit) == true && com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.needToKnow) == true//
            && com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == true)
        {
            productInformationViewHolder.contentsList.setVisibility(View.GONE);
        } else
        {
            productInformationViewHolder.contentsList.setVisibility(View.VISIBLE);

            // 베네핏
            if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.menuBenefit) == false)
            {
                addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, false);
            }

            // 이용 시간
            if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == false)
            {
                String timeFormat = mContext.getString(R.string.label_office_hours) + " " + String.format(Locale.KOREA, "%s ~ %s", gourmetProduct.openTime, gourmetProduct.closeTime);

                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.lastOrderTime) == false)
                {
                    timeFormat += " " + mContext.getString(R.string.label_gourmet_product_lastorder, gourmetProduct.lastOrderTime);
                }

                addProductSubInformation(mInflater, productInformationViewHolder.contentsList, timeFormat, R.drawable.ic_detail_item_03_time, true);
            }

            // 확인 사항
            if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.needToKnow) == false)
            {
                addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, true);
            }
        }

        //        } else
        //        {
        //
        //
        //            if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.menuBenefit) == true && com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.needToKnow) == true//
        //                && com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == true)
        //            {
        //                productInformationViewHolder.contentsList.setVisibility(View.GONE);
        //            } else
        //            {
        //                productInformationViewHolder.contentsList.setVisibility(View.VISIBLE);
        //
        //                // 베네핏
        //                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.menuBenefit) == false)
        //                {
        //                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, false);
        //                }
        //
        //                // 이용 시간
        //                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.openTime, gourmetProduct.closeTime) == false)
        //                {
        //                    String timeFormat = mContext.getString(R.string.label_office_hours) + " " + String.format(Locale.KOREA, "%s ~ %s", gourmetProduct.openTime, gourmetProduct.closeTime);
        //
        //                    if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.lastOrderTime) == false)
        //                    {
        //                        timeFormat += " " + mContext.getString(R.string.label_gourmet_product_lastorder, gourmetProduct.lastOrderTime);
        //                    }
        //
        //                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, timeFormat, R.drawable.ic_detail_item_03_time, true);
        //                }
        //
        //                // 확인 사항
        //                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.needToKnow) == false)
        //                {
        //                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, true);
        //                }

        //                int titleTextViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 30) - ScreenUtils.dpToPx(mContext, 98);
        //                float titleTextViewHeight = Util.getTextViewHeight(productInformationViewHolder.productNameTextView, titleTextViewWidth);
        //
        //                float startY = titleTextViewHeight + ScreenUtils.dpToPx(mContext, 8);
        //                Rect rect = new Rect(0, 0, ScreenUtils.dpToPx(mContext, 98), ScreenUtils.dpToPx(mContext, 98));
        //                int textViewWidth = ScreenUtils.getScreenWidth(mContext) - ScreenUtils.dpToPx(mContext, 39) - ScreenUtils.dpToPx(mContext, 15);
        //
        //                // 베네핏
        //                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.menuBenefit) == false)
        //                {
        //                    startY = addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.menuBenefit, R.drawable.ic_detail_item_02_benefit, startY, textViewWidth, rect, false);
        //                }
        //
        //                // 확인 사항
        //                if (com.daily.base.util.TextUtils.isTextEmpty(gourmetProduct.needToKnow) == false)
        //                {
        //                    addProductSubInformation(mInflater, productInformationViewHolder.contentsList, gourmetProduct.needToKnow, R.drawable.ic_detail_item_01_info, startY, textViewWidth, rect, true);
        //                }
        //            }
        //        }

        String price = com.daily.base.util.TextUtils.getPriceFormat(mContext, gourmetProduct.price, false);
        String discountPrice = com.daily.base.util.TextUtils.getPriceFormat(mContext, gourmetProduct.discountPrice, false);

        if (gourmetProduct.price <= 0 || gourmetProduct.price <= gourmetProduct.discountPrice)
        {
            productInformationViewHolder.priceTextView.setVisibility(View.GONE);
            productInformationViewHolder.priceTextView.setText(null);
        } else
        {
            productInformationViewHolder.priceTextView.setVisibility(View.VISIBLE);
            productInformationViewHolder.priceTextView.setText(price);
        }

        productInformationViewHolder.discountPriceTextView.setText(discountPrice);

        productInformationViewHolder.detailView.setTag(position);
        productInformationViewHolder.detailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnProductClickListener != null)
                {
                    mOnProductClickListener.onProductDetailClick((int) v.getTag(), 1);
                }
            }
        });

        productInformationViewHolder.reservationView.setTag(position);
        productInformationViewHolder.reservationView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnProductClickListener != null)
                {
                    mOnProductClickListener.onReservationClick((int) v.getTag());
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (mGourmetProductList == null)
        {
            return 0;
        }

        return mGourmetProductList.size();
    }

    private void addProductSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, int iconResId, boolean hasTopMargin)
    {
        if (layoutInflater == null || viewGroup == null || com.daily.base.util.TextUtils.isTextEmpty(contentText) == true)
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

    private float addProductSubInformation(LayoutInflater layoutInflater, ViewGroup viewGroup, String contentText, int iconResId, float startY, int textViewWidth, Rect rect, boolean hasTopMargin)
    {
        if (layoutInflater == null || viewGroup == null || com.daily.base.util.TextUtils.isTextEmpty(contentText) == true)
        {
            return startY;
        }

        View textLayout = layoutInflater.inflate(R.layout.list_row_detail_product_text, viewGroup, false);
        viewGroup.addView(textLayout);

        if (hasTopMargin == true)
        {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = ScreenUtils.dpToPx(mContext, 6);
            textLayout.setLayoutParams(layoutParams);

            startY += layoutParams.topMargin;
        } else
        {
            startY += ScreenUtils.dpToPx(mContext, 2);
        }

        DailyImageView iconImageView = (DailyImageView) textLayout.findViewById(R.id.iconImageView);
        iconImageView.setVectorImageResource(iconResId);

        TextView textView = (TextView) textLayout.findViewById(R.id.textView);

        if (textViewWidth <= 0)
        {
            textView.setText(contentText);
            return 0;
        }

        return measureText(textView, contentText, startY, textViewWidth, rect);
    }

    protected float measureText(TextView textView, String text, float viewY, int viewWidth, Rect rect)
    {
        if (textView == null || viewWidth <= 0 || com.daily.base.util.TextUtils.isTextEmpty(text) == true)
        {
            return 0;
        }

        final int length = text.length();
        StringBuilder stringBuilder = new StringBuilder();
        Paint paint = textView.getPaint();

        float textViewHeight = 0.0f;
        int startIndex = 0;
        int textCount = 0;
        float textWidth, realWidth;
        boolean isSettingRange = false;

        char[] texts = text.toCharArray();

        while (startIndex < length)
        {
            if (viewY + textViewHeight < rect.bottom)
            {
                realWidth = viewWidth - rect.width();
            } else
            {
                realWidth = viewWidth;
            }

            textCount = paint.breakText(texts, startIndex, texts.length - startIndex, realWidth, null);

            if (stringBuilder.length() > 0)
            {
                stringBuilder.append('\n');
            }

            stringBuilder.append(texts, startIndex, textCount);

            startIndex += textCount;

            textView.setText(stringBuilder.toString());
            textViewHeight = com.daily.base.util.TextUtils.getTextViewHeight(textView, viewWidth);
        }

        textView.setText(stringBuilder.toString());

        return viewY + com.daily.base.util.TextUtils.getTextViewHeight(textView, viewWidth);
    }

    private class ProductInformationViewHolder extends RecyclerView.ViewHolder
    {
        View contentsLayout;
        SimpleDraweeView simpleDraweeView;
        TextView productNameTextView;
        TextView discountPriceTextView;
        TextView priceTextView;
        LinearLayout contentsList;
        View detailView;
        View reservationView;

        public ProductInformationViewHolder(View itemView)
        {
            super(itemView);

            contentsLayout = itemView.findViewById(R.id.contentsLayout);
            simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.simpleDraweeView);
            productNameTextView = (TextView) itemView.findViewById(R.id.productNameTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            priceTextView.setPaintFlags(priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discountPriceTextView = (TextView) itemView.findViewById(R.id.discountPriceTextView);
            contentsList = (LinearLayout) itemView.findViewById(R.id.contentsList);
            detailView = itemView.findViewById(R.id.detailView);
            reservationView = itemView.findViewById(R.id.reservationView);
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
