package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;

public class GourmetProductDetailActivity extends BaseActivity
{
    private GourmetDetail mGourmetDetail;
    private TicketInformation mTicketInformation;

    private GourmetProductDetailLayout mGourmetProductDetailLayout;

    public static Intent newInstance(Context context, SaleTime saleTime, GourmetDetail gourmetDetail, TicketInformation ticketInformation)
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
