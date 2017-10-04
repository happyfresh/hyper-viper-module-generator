package com.happyfresh.modules.$HyperModuleName;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.TaskStackBuilder;

import com.happyfresh.abstracts.BaseActivity;
import com.happyfresh.abstracts.BaseRouter;
import com.happyfresh.activities.CreateAddressFormActivity;
import com.happyfresh.activities.ProductCampaignActivity;
import com.happyfresh.common.ICartConstant;
import com.happyfresh.modules.Store.Picker.StorePickerRouter;
import com.happyfresh.snowflakes.hoverfly.models.Address;
import com.happyfresh.snowflakes.hoverfly.models.StockLocation;
import com.happyfresh.snowflakes.hoverfly.models.SubDistrict;

import java.util.ArrayList;

/**
 * Created by $HyperAuthorName on 6/12/17.
 */

public class $HyperModuleNameRouter extends BaseRouter {

    public $HyperModuleNameRouter(Context context) {
        super(context);
    }

    @Override
    public void detach() {

    }

    @Override
    public Class<? extends BaseActivity> viewClass() {
        return $HyperModuleNameActivity.class;
    }

    public void openAddressFormScreen(Address address) {
        Intent intent = new Intent(context, CreateAddressFormActivity.class);
        intent.putExtra(ICartConstant.EXTRAS.ADDRESS_NAME, address.address1);
        intent.putExtra(ICartConstant.EXTRAS.ADDRESS_LAT, address.lat);
        intent.putExtra(ICartConstant.EXTRAS.ADDRESS_LON, address.lon);
        ((Activity) context).startActivityForResult(intent, ICartConstant.REQUEST_CODES.$HyperModuleName_ACTIVITY);
    }

    public void openCheckoutScreen() {
        Activity activity = (Activity) context;
        activity.setResult(Activity.RESULT_OK, activity.getIntent());
        activity.finish();
    }

    public void backToCampaignDetailScreen(double latitude, double longitude, String addressName) {
        Intent intent = new Intent();
        intent.putExtra(ICartConstant.EXTRAS.LATITUDE, latitude);
        intent.putExtra(ICartConstant.EXTRAS.LONGITUDE, longitude);
        intent.putExtra(ICartConstant.EXTRAS.ADDRESS_NAME, addressName);

        Activity activity = (Activity) context;
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    public void openCampaignDetailScreen(long productId, long promotionid, double latitude,
            double longitude, String zipcode, String name, String cityName, ArrayList<StockLocation> stockLocations,
            SubDistrict subDistrict, String pinAddress, long remoteId) {
        Activity activity = (Activity) context;

        // TODO: Check StorePickerRouter

        StorePickerRouter storePickerRouter = new StorePickerRouter(context);

        Bundle bundle = chooseStoreBundle(subDistrict, latitude, longitude, zipcode, name,
                cityName, stockLocations, pinAddress, remoteId);
        Intent chooseStoreIntent = storePickerRouter.createIntent(bundle);

        bundle.putLong(ICartConstant.EXTRAS.DEEP_LINKS.PRODUCT_ID, productId);
        bundle.putBoolean(ICartConstant.EXTRAS.IS_NEW_USER, true);

        if (promotionid > 0) {
            bundle.putLong(ICartConstant.EXTRAS.DEEP_LINKS.PROMOTION_ID, promotionid);
        }

        Intent campaignDetailIntent = storePickerRouter.createIntent(ProductCampaignActivity.class, bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addNextIntent(chooseStoreIntent);
        stackBuilder.addNextIntent(campaignDetailIntent);
        stackBuilder.startActivities();
    }

    public void openChooseStoreScreen(SubDistrict subDistrict, double latitude, double longitude,
            String zipCode, String areaName, String cityName, ArrayList<StockLocation> stockLocations,
            String pinAddress, long remoteId) {
        // TODO: Check StorePickerRouter

        Bundle bundle = chooseStoreBundle(subDistrict, latitude, longitude, zipCode, areaName,
                cityName, stockLocations, pinAddress, remoteId);

        StorePickerRouter storePickerRouter = new StorePickerRouter(context);
        storePickerRouter.start(bundle);
    }

    private Bundle chooseStoreBundle(SubDistrict subDistrict, double latitude, double longitude, String zipCode,
            String areaName, String cityName, ArrayList<StockLocation> stockLocations, String pinAddress,
            long remoteId) {
        Bundle bundle = new Bundle();

        if (subDistrict != null) {
            bundle.putParcelable(ICartConstant.EXTRAS.SUB_DISTRICT, subDistrict.toParcel());
        }

        bundle.putDouble(ICartConstant.EXTRAS.LATITUDE, latitude);
        bundle.putDouble(ICartConstant.EXTRAS.LONGITUDE, longitude);
        bundle.putString(ICartConstant.EXTRAS.ZIP_CODE, zipCode);
        bundle.putString(ICartConstant.EXTRAS.SUB_LOCALITY, areaName);
        bundle.putString(ICartConstant.EXTRAS.CITY_NAME, cityName);
        bundle.putString(ICartConstant.EXTRAS.PIN_ADDRESS, pinAddress);
        bundle.putLong(ICartConstant.EXTRAS.ADDRESS_ID, remoteId);

        ArrayList<Parcelable> parcelableList = (ArrayList<Parcelable>) StockLocation.toListParcel(stockLocations);
        bundle.putParcelableArrayList(ICartConstant.EXTRAS.STOCK_LOCATIONS, parcelableList);

        return bundle;
    }
}
