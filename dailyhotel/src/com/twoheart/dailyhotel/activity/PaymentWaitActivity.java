package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class PaymentWaitActivity extends BaseActivity implements DailyHotelJsonResponseListener {

	private final static String TAG = "PaymentWaitActivity";
	Booking booking;

	TextView tvHotelName;
	TextView tvAccount;
	TextView tvName;
	TextView tvPrice;
	TextView tvDeadline;
	TextView tvGuide1;
	TextView tvGuide2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		booking = new Booking();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);

		setActionBar(getString(R.string.actionbar_title_payment_wait_activity));
		setContentView(R.layout.activity_payment_wait);

		tvHotelName = (TextView) findViewById(R.id.tv_payment_wait_hotel_name);
		tvAccount = (TextView) findViewById(R.id.tv_payment_wait_account);
		tvName = (TextView) findViewById(R.id.tv_payment_wait_name);
		tvPrice = (TextView) findViewById(R.id.tv_payment_wait_price);
		tvDeadline = (TextView) findViewById(R.id.tv_payment_wait_deadline);
		tvGuide1 = (TextView) findViewById(R.id.tv_payment_wait_guide1);
		tvGuide2 = (TextView) findViewById(R.id.tv_payment_wait_guide2);

		tvHotelName.setText(booking.getHotel_name());

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
		.append(URL_WEBAPI_RESERVE_MINE_DETAIL)
		.append("/").append(booking.getPayType()+"")
		.append("/").append(booking.getTid()+"").toString();
		
		lockUI();
		
		android.util.Log.e(TAG + " / URL",url);
		mQueue.add(new DailyHotelJsonRequest(Method.GET, url, null, this, this));
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		android.util.Log.e(TAG + " / RESPONSE", response.toString());
		if (url.contains(URL_WEBAPI_RESERVE_MINE_DETAIL)) {
			try {
				if (!response.getBoolean("result")) {
					Intent intent = new Intent();
					intent.putExtra("msg", response.getString("msg"));
					setResult(CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT, intent);
					finish();
				} else {
					tvAccount.setText(response.getString("bank_name") +", "+ response.getString("account_num"));
					tvName.setText(response.getString("name"));

					DecimalFormat comma = new DecimalFormat("###,##0");
					tvPrice.setText(comma.format(response.getInt("amt"))+getString(R.string.currency));
					
					String[] dateSlice = response.getString("date").split("/");
					String[] timeSlice = response.getString("time").split(":");
					
					tvDeadline.setText(Integer.parseInt(dateSlice[1])+"월 "+Integer.parseInt(dateSlice[2])+"일 "+timeSlice[0]+":"+timeSlice[1]+"까지");

					tvGuide1.setText(response.getString("msg1"));
					tvGuide2.setText(response.getString("msg2"));
					unLockUI();
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.payment_wait_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_call:
			Intent i = new Intent(
					Intent.ACTION_DIAL,
					Uri.parse(new StringBuilder("tel:")
					.append(PHONE_NUMBER_DAILYHOTEL)
					.toString()));
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
