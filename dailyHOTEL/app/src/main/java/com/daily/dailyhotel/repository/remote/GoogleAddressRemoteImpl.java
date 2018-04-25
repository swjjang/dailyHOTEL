package com.daily.dailyhotel.repository.remote;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.domain.GoogleAddressInterface;
import com.daily.dailyhotel.entity.GoogleAddress;
import com.daily.dailyhotel.repository.remote.model.GoogleAddressComponentsData;
import com.daily.dailyhotel.repository.remote.model.GoogleAddressData;
import com.twoheart.dailyhotel.network.dto.GoogleMapListDto;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2018. 1. 31..
 */

public class GoogleAddressRemoteImpl extends BaseRemoteImpl implements GoogleAddressInterface
{
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_SUBLOCALITY_LEVEL_2 = "sublocality_level_2";
    private static final String KEY_ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";

    @Override
    public Observable<GoogleAddress> getLocationAddress(double latitude, double longitude)
    {
        final String url = String.format(Locale.KOREA, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&location_type=APPROXIMATE&result_type=sublocality_level_2|administrative_area_level_1&key=%s&language=ko"//
            , Double.toString(latitude)//
            , Double.toString(longitude)//
            , Crypto.getUrlDecoderEx(Constants.GOOGLE_MAP_KEY));

        return mDailyMobileService.getSearchAddress(url).subscribeOn(Schedulers.io()).map(new Function<GoogleMapListDto<GoogleAddressData>, GoogleAddress>()
        {
            @Override
            public GoogleAddress apply(GoogleMapListDto<GoogleAddressData> googleAddressDataGoogleMapListDto) throws Exception
            {
                GoogleAddress googleAddress;

                if (googleAddressDataGoogleMapListDto != null)
                {
                    try
                    {
                        List<GoogleAddressData> list = googleAddressDataGoogleMapListDto.results;

                        if ("OK".equalsIgnoreCase(googleAddressDataGoogleMapListDto.status) == true && list != null)
                        {
                            googleAddress = getGoogleAddress(list);
                        } else
                        {
                            throw new BaseException(-1, null);
                        }
                    } catch (Exception e)
                    {
                        throw new BaseException(-1, null);
                    }
                } else
                {
                    throw new BaseException(-1, null);
                }

                return googleAddress;
            }
        });
    }

    GoogleAddress getGoogleAddress(List<GoogleAddressData> googleAddressDataList)
    {
        if (googleAddressDataList == null || googleAddressDataList.size() == 0)
        {
            return null;
        }

        GoogleAddress googleAddress = new GoogleAddress();

        GoogleAddressComponentsData countryData = getComponentsData(KEY_COUNTRY, googleAddressDataList);
        GoogleAddressComponentsData subLocalityData = getComponentsData(KEY_SUBLOCALITY_LEVEL_2, googleAddressDataList);
        GoogleAddressComponentsData administrativeData = getComponentsData(KEY_ADMINISTRATIVE_AREA_LEVEL_1, googleAddressDataList);

        googleAddress.country = countryData == null ? null : countryData.longName;
        googleAddress.shortCountry = countryData == null ? null : countryData.shortName;

        if (countryData == null)
        {
            googleAddress.address = googleAddressDataList.get(0).formattedAddress;
            return googleAddress;
        }

        boolean isKr = "KR".equalsIgnoreCase(countryData.shortName);

        if (isKr == true)
        {
            if (subLocalityData != null)
            {
                googleAddress.shortAddress = subLocalityData.longName;
            } else if (administrativeData != null)
            {
                googleAddress.shortAddress = administrativeData.longName;
            }
        } else
        {
            if (administrativeData != null)
            {
                googleAddress.shortAddress = administrativeData.longName;
            } else if (subLocalityData != null)
            {
                googleAddress.shortAddress = subLocalityData.longName;
            }
        }

        String address = null;
        String searchType = isKr ? KEY_SUBLOCALITY_LEVEL_2 : KEY_ADMINISTRATIVE_AREA_LEVEL_1;

        for (GoogleAddressData googleAddressData : googleAddressDataList)
        {
            if (googleAddressData.types.contains(searchType) == true)
            {
                address = googleAddressData.formattedAddress;
                break;
            }
        }

        if (DailyTextUtils.isTextEmpty(address) == false)
        {
            ExLog.d("sam - origin : " + address + " , country : " + countryData.longName + " , trim Address : " + address.replace(countryData.longName, "").trim());

            if (isKr)
            {
                address = address.replace(countryData.longName, "").trim();
            }
        }

        googleAddress.address = address;

        return googleAddress;
    }

    private GoogleAddressComponentsData getComponentsData(String key, List<GoogleAddressData> googleAddressDataList)
    {
        if (googleAddressDataList == null || googleAddressDataList.size() == 0 || DailyTextUtils.isTextEmpty(key))
        {
            return null;
        }

        List<GoogleAddressComponentsData> list = googleAddressDataList.get(0).addressComponents;
        if (list == null || list.size() == 0)
        {
            return null;
        }

        for (GoogleAddressComponentsData data : list)
        {
            if (data.types.contains(key) == true)
            {
                return data;
            }
        }

        return null;
    }

    @Override
    public Observable<String> getLocationRegionName(Context context, String regionName //
        , double latitude, double longitude, boolean isCountryName)
    {
        if (!DailyTextUtils.isTextEmpty(regionName)) {
            return Observable.just(regionName);
        }

        return Observable.defer(new Callable<ObservableSource<String>>()
        {
            @Override
            public ObservableSource<String> call() throws Exception
            {
                Geocoder geocoder = new Geocoder(context, Locale.KOREA);

                String result = "";

                try
                {
                    List<Address> list = geocoder.getFromLocation(latitude, longitude, 10);
                    if (list != null && list.size() > 0)
                    {
                        for (Address address : list)
                        {
                            String checkString = isCountryName ? address.getCountryName() : address.getLocality();

                            if (DailyTextUtils.isTextEmpty(checkString) == false)
                            {
                                result = checkString;
                                break;
                            }
                        }
                    }
                } catch (IOException e)
                {
                    ExLog.d(e.toString());
                }

                return Observable.just(result);
            }
        }).subscribeOn(Schedulers.io());
    }
}
