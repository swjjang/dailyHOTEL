package com.twoheart.dailyhotel.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.R;

public class SelectDetailRegionDialog extends Dialog implements com.twoheart.dailyhotel.util.Constants, View.OnClickListener
{
    private OnSelectedDetailRegionListener mOnSelectedDetailRegionListener;

    public SelectDetailRegionDialog(Context context)
    {
        super(context);

        initLayout(context);
    }

    protected SelectDetailRegionDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
    {
        super(context, cancelable, cancelListener);

        initLayout(context);
    }

    public SelectDetailRegionDialog(Context context, int theme)
    {
        super(context, theme);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_select_region, null, true);

        setContentView(view);

        View view01 = view.findViewById(R.id.view01);
        View view02 = view.findViewById(R.id.view02);
        View view03 = view.findViewById(R.id.view03);
        View view04 = view.findViewById(R.id.view04);
        View view05 = view.findViewById(R.id.view05);

        view01.setOnClickListener(this);
        view02.setOnClickListener(this);
        view03.setOnClickListener(this);
        view04.setOnClickListener(this);
        view05.setOnClickListener(this);

        setOnCancelListener(new OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                if (mOnSelectedDetailRegionListener != null)
                {
                    mOnSelectedDetailRegionListener.onCancel();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        String detailRegion = null;

        if (v.getId() == R.id.view01)
        {
            detailRegion = "강남구|서초구|동작구|송파구|강동구";
        } else if (v.getId() == R.id.view02)
        {
            detailRegion = "동대문구|성동구|광진구|성북구|강북구|중랑구|노원구|도봉구";
        } else if (v.getId() == R.id.view03)
        {
            detailRegion = "중구|종로구|용산구";
        } else if (v.getId() == R.id.view04)
        {
            detailRegion = "마포구|서대문구|은평구";
        } else if (v.getId() == R.id.view05)
        {
            detailRegion = "영등포구|양천구|강서구|금천구|구로구";
        }

        if (mOnSelectedDetailRegionListener != null)
        {
            mOnSelectedDetailRegionListener.onClick(detailRegion);
        }

        dismiss();
    }

    public void setOnSelectedRegionListener(OnSelectedDetailRegionListener listener)
    {
        mOnSelectedDetailRegionListener = listener;
    }

    public interface OnSelectedDetailRegionListener
    {
        public void onClick(String detailRegion);

        public void onCancel();
    }
}
