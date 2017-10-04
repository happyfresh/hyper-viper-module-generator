package com.happyfresh.modules.$HyperModuleName;

import android.location.Location;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.$HyperModuleName.CameraUpdate;
import com.happyfresh.abstracts.InteractorProtocolOutput;
import com.happyfresh.models.TextSearchResult;
import com.happyfresh.snowflakes.hoverfly.models.Address;
import com.happyfresh.snowflakes.hoverfly.models.StockLocation;
import com.happyfresh.snowflakes.hoverfly.models.SubDistrict;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kharda on 7/6/17.
 */

public interface $HyperModuleNameInteractorOutput extends InteractorProtocolOutput {

    void showGooglePlacesApiError();

    void showGPSDisabled();

    void moveCameraToCurrentLocation();

    void moveCameraToLocation(Location location);

    void showErrorAutoComplete();

    void showPredictions(String query, ArrayList<AutocompletePrediction> predictions);

    void showTextSearchResults(List<TextSearchResult> results, String searchTerm);

    void showTextSearchFailed();

    void prepareShowPlaceResult(String placeName, CameraUpdate cameraUpdate);

    void showConnectionError();

    void prepareSetTextForEtFindAddress(String pinAddress);

    void openAddressFormScreen(Address address);

    void showOutOfRangeDialog(double latitude, double longitude, String pinAddress);

    void showNoAddressError();

    void openCheckoutScreen();

    void backToCampaignDetailScreen(double latitude, double longitude, String pinAddress);

    void openCampaignDetailScreen(long productId, long promotionId, double latitude, double longitude, String zipcode,
            String areaName, String cityName, ArrayList<StockLocation> stockLocations, SubDistrict subDistrict,
            String pinAddress, long addressId);

    void openChooseStoreScreen(SubDistrict subDistrict, double latitude, double longitude, String zipcode,
            String areaName, String cityName, ArrayList<StockLocation> stockLocations, String pinAddress,
            long addressId);

    void showOutsideAreaDialog(String pinAddress, double latitude, double longitude);

    void prepareShowPlaceResultError();

    void setMarkerNextChooseSupermarket();

    void showRecentAddressResult(List<Address> results);
}
