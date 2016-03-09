package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;

public class PaymentResultActivity extends BaseActivity implements OnClickListener
{
    public static Intent newInstance(Context context, SaleRoomInformation saleRoomInformation, SaleTime saleTime, String imageUrl)
    {
        Intent intent = new Intent(context, PaymentResultActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION, saleRoomInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, imageUrl);

        return intent;
    }

    public static Intent newInstance(Context context, TicketInformation ticketInformation, SaleTime saleTime, String imageUrl)
    {
        Intent intent = new Intent(context, PaymentResultActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION, ticketInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL, imageUrl);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_result);
    }

    private void initToolbar(String title)
    {
    }

    private void initLayout()
    {
    }

    @Override
    public void onClick(View v)
    {

    }
}
