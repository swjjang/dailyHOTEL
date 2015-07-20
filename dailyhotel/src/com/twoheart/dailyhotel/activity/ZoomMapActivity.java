package com.twoheart.dailyhotel.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.HotelNameInfoWindowAdapter;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.LocationFactory;
import com.twoheart.dailyhotel.util.ui.MyLocationMarker;

public class ZoomMapActivity extends BaseActivity
{
	private GoogleMap mGoogleMap;

	private View mMyLocationView;
	private MarkerOptions mMyLocationMarkerOptions;
	private Marker mMyLocationMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_zoom_map);

		Intent intent = getIntent();

		String hotelName = null;
		double latitude = 0;
		double longitude = 0;

		if (intent != null)
		{
			hotelName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME);
			latitude = intent.getDoubleExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, 0);
			longitude = intent.getDoubleExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, 0);
		}

		if (hotelName == null || latitude == 0 || longitude == 0)
		{
			finish();
			return;
		}

		setActionBar(hotelName);

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag_full_map)).getMap();

		if (mGoogleMap != null)
		{
			mGoogleMap.getUiSettings().setCompassEnabled(false);
			mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
			mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
			mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
			mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
			mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

			mGoogleMap.setMyLocationEnabled(false);

			relocationMyLocation();
			relocationZoomControl();
			addMarker(mGoogleMap, latitude, longitude, hotelName);
		}
	}

	private void relocationMyLocation()
	{
		mMyLocationView = findViewById(0x2);

		if (mMyLocationView != null)
		{
			mMyLocationView.setVisibility(View.VISIBLE);
			mMyLocationView.setOnClickListener(mOnMyLocationClickListener);
		}
	}

	private void relocationZoomControl()
	{
		View zoomControl = findViewById(0x1);

		if (zoomControl != null && zoomControl.getLayoutParams() instanceof RelativeLayout.LayoutParams)
		{
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) zoomControl.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

			zoomControl.setPadding(zoomControl.getPaddingLeft(), Util.dpToPx(this, 50), zoomControl.getPaddingRight(), zoomControl.getPaddingBottom());
			zoomControl.setLayoutParams(params);
		}
	}

	private void addMarker(GoogleMap googleMap, Double lat, Double lng, String hotel_name)
	{
		if (googleMap != null)
		{
			Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(hotel_name));
			marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.info_ic_map_large));
			marker.showInfoWindow();

			LatLng address = new LatLng(lat, lng);
			CameraPosition cp = new CameraPosition.Builder().target((address)).zoom(15).build();

			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
			googleMap.setInfoWindowAdapter(new HotelNameInfoWindowAdapter(this));
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
			{
				@Override
				public boolean onMarkerClick(Marker marker)
				{
					marker.showInfoWindow();
					return true;
				}
			});
		}
	}

	private View.OnClickListener mOnMyLocationClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			LocationFactory.getInstance(ZoomMapActivity.this).startLocationMeasure(ZoomMapActivity.this, mMyLocationView, new LocationListener()
			{
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderEnabled(String provider)
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onProviderDisabled(String provider)
				{
					if (isFinishing() == true)
					{
						return;
					}

					// 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
					LocationFactory.getInstance(ZoomMapActivity.this).stopLocationMeasure();

					AlertDialog.Builder builder = new AlertDialog.Builder(ZoomMapActivity.this).setTitle(R.string.dialog_title_used_gps).setMessage(getString(R.string.dialog_msg_used_gps)).setNegativeButton(getString(R.string.dialog_btn_text_cancel), null).setPositiveButton(getString(R.string.dialog_btn_text_dosetting), new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION);
						}
					});

					builder.show();
				}

				@Override
				public void onLocationChanged(Location location)
				{
					if (isFinishing() == true || mGoogleMap == null)
					{
						return;
					}

					LocationFactory.getInstance(ZoomMapActivity.this).stopLocationMeasure();

					if (mMyLocationMarkerOptions == null)
					{
						mMyLocationMarkerOptions = new MarkerOptions();
						mMyLocationMarkerOptions.icon(new MyLocationMarker(ZoomMapActivity.this).makeIcon());
						mMyLocationMarkerOptions.anchor(0.5f, 0.5f);
					}

					if (mMyLocationMarker != null)
					{
						mMyLocationMarker.remove();
					}

					mMyLocationMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
					mMyLocationMarker = mGoogleMap.addMarker(mMyLocationMarkerOptions);

					CameraPosition cameraPosition = new CameraPosition.Builder().target(mMyLocationMarkerOptions.getPosition()).zoom(13f).build();
					mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}
			});
		}
	};
}
