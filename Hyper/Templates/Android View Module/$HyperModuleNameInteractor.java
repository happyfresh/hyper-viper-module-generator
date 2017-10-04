package com.happyfresh.modules.$HyperModuleName;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.$HyperModuleName.CameraUpdate;
import com.google.android.gms.$HyperModuleName.CameraUpdateFactory;
import com.google.android.gms.$HyperModuleName.model.CameraPosition;
import com.google.android.gms.$HyperModuleName.model.LatLng;
import com.google.android.gms.$HyperModuleName.model.LatLngBounds;
import com.happyfresh.abstracts.BaseInteractor;
import com.happyfresh.callbacks.ICartCallback;
import com.happyfresh.common.ICartConstant;
import com.happyfresh.fragments.AddressManagementFragment;
import com.happyfresh.managers.tracking.TrackingManager;
import com.happyfresh.managers.tracking.TrackingUtils;
import com.happyfresh.managers.tracking.Value;
import com.happyfresh.models.CoverageResponse;
import com.happyfresh.models.SupplierResponse;
import com.happyfresh.models.TextSearchResult;
import com.happyfresh.snowflakes.hoverfly.Sprinkles;
import com.happyfresh.snowflakes.hoverfly.models.Address;
import com.happyfresh.snowflakes.hoverfly.models.Coordinate;
import com.happyfresh.snowflakes.hoverfly.models.Order;
import com.happyfresh.snowflakes.hoverfly.models.StockLocation;
import com.happyfresh.snowflakes.hoverfly.models.SubDistrict;
import com.happyfresh.snowflakes.hoverfly.models.Supplier;
import com.happyfresh.snowflakes.hoverfly.services.User.UserService;
import com.happyfresh.snowflakes.hoverfly.shared.subscribers.ServiceSubscriber;
import com.happyfresh.snowflakes.hoverfly.utils.IANATimeZoneUtils;
import com.happyfresh.utils.LogUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kharda on 7/6/17.
 */

public class $HyperModuleNameInteractor extends BaseInteractor<$HyperModuleNameInteractorOutput> {

    // region Attributes

    private static final String TAG = "$HyperModuleNameInteractor";

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final long UPDATE_INTERVAL_IN_MILLIS = 10000;

    private UserService userService;

    private GoogleApiClient googleApiClient;

    private Location lastLocation;

    private String typeInteraction = Value.Interaction.None;

    private String regionMethod;

    private boolean fromMore;

    private double currentLongitude;

    private double currentLatitude;

    private long currentAddressRemoteId;

    private String pinAddress;

    private boolean requestMovingCameraToCurrentLocation = true;

    private long productId;

    private long promotionId;

    private String orderNumber;

    private long stockLocationId;

    private String callingActivity = ICartConstant.SCREEN_NAME.CHOOSE_STORE;

    private List<Address> recentAddress;

    private boolean fromCheckout;

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            output().moveCameraToCurrentLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            output().showGooglePlacesApiError();
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            $HyperModuleNameInteractor.this.lastLocation = location;
        }
    };

    // endregion

    // region Constructor

    public $HyperModuleNameInteractor() {
        this.userService = Sprinkles.service(UserService.class);
    }

    // endregion

    // region Functionality

    public void initGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(app().getApplicationContext()).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API).addApi(LocationServices.API)
                .addConnectionCallbacks(this.connectionCallbacks)
                .addOnConnectionFailedListener(this.connectionFailedListener).build();
    }

    // TODO: check permission location granted before call this method
    @SuppressWarnings("MissingPermission")
    public void initMyLocation() {
        initMyLocation(this.requestMovingCameraToCurrentLocation);
        this.requestMovingCameraToCurrentLocation = false;
    }

    // TODO: check permission location granted before call this method
    @SuppressWarnings("MissingPermission")
    public void initMyLocation(boolean moveCamera) {
        if (this.googleApiClient.isConnected()) {

            LocationAvailability locationAvailability = LocationServices.FusedLocationApi
                    .getLocationAvailability(this.googleApiClient);
            if (locationAvailability != null && !locationAvailability.isLocationAvailable()) {
                output().showGPSDisabled();
            }

            this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);

            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLIS);

            LocationServices.FusedLocationApi
                    .requestLocationUpdates(this.googleApiClient, locationRequest, this.locationListener);

            // only once to move camera into current location
            if (moveCamera) {
                output().moveCameraToLocation(this.lastLocation);
            }
        }
    }

    public void getAutoComplete(final String query) {
        if (this.googleApiClient.isConnected()) {
            LatLngBounds bounds;

            Coordinate currentLocation = app().getSharedPreferencesManager().getCoordinate();

            if (currentLocation != null && currentLocation.latitude != 0 && currentLocation.longitude != 0) {
                bounds = new LatLngBounds(new LatLng(currentLocation.latitude - 0.05, currentLocation.longitude - 0.05),
                        new LatLng(currentLocation.latitude + 0.05, currentLocation.longitude + 0.05));
            }
            else {
                bounds = IANATimeZoneUtils.getCountrySearchBounds(app());
            }

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi
                    .getAutocompletePredictions(this.googleApiClient, query, bounds, null);
            results.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    // Confirm that the query completed successfully, otherwise return null
                    Status status = autocompletePredictions.getStatus();
                    if (!status.isSuccess()) {
                        autocompletePredictions.release();
                        output().showErrorAutoComplete();
                        return;
                    }

                    // Freeze the results immutable representation that can be stored safely.
                    ArrayList<AutocompletePrediction> predictions = DataBufferUtils
                            .freezeAndClose(autocompletePredictions);
                    autocompletePredictions.release();

                    $HyperModuleNameInteractor.this.fromMore = false;

                    output().showPredictions(query, predictions);
                }
            });
        }
    }

    public void track$HyperModuleNameearchAddress(String query, int size) {
        TrackingUtils.track$HyperModuleNameearchAddress(query, size);
    }

    public void trackView$HyperModuleNamecreen() {
        TrackingUtils.view$HyperModuleNamecreen();
        TrackingManager.with(app()).setCurrentScreen(Value.Screen.Map);
    }

    public void prepareGetAddressOnCameraChange(CameraPosition cameraPosition) {
        setCurrentLatLong(cameraPosition.target.latitude, cameraPosition.target.longitude);

        getCurrentAddress(this.currentLatitude, this.currentLongitude, new ICartCallback<String>(null) {
            @Override
            public void onSuccess(String object) {
                $HyperModuleNameInteractor.this.pinAddress = object;
                output().prepareSetTextForEtFindAddress($HyperModuleNameInteractor.this.pinAddress);
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);
                output().prepareSetTextForEtFindAddress(null);
            }
        });
    }

    public void track$HyperModuleNameearchMoreAddress(String searchTerm, int size) {
        TrackingUtils.track$HyperModuleNameearchMoreAddress(searchTerm, size);
        this.fromMore = true;
        this.typeInteraction = Value.Interaction.EnterMoreAddress;
        refreshRegionMethod();
    }

    public void prepareOnSearchForListItemClick(String searchTerm) {
        app().getGoogle$HyperModuleNameManager().searchPlaceByText(searchTerm, new ICartCallback<List<TextSearchResult>>(TAG) {
            @Override
            public void onSuccess(List<TextSearchResult> results) {
                output().showTextSearchResults(results, searchTerm);
            }

            @Override
            public void onFailure(Throwable throwable) {
                super.onFailure(throwable);

                output().showTextSearchFailed();
            }
        });
    }

    public boolean prepareGetPlaceById(String placeId) {
        if (this.googleApiClient.isConnected()) {
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(this.googleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    prepareShowPlaceResult(places);
                    places.release();
                }
            });

            LogUtils.LOG("Called getPlaceById to get Place details for " + placeId);

            return true;
        }

        LogUtils.LOG("Google API client is not connected for place details query.");
        return false;
    }

    public void prepareShowPlaceResult(PlaceBuffer places) {
        if (places.getStatus().isSuccess() && places.getCount() > 0) {
            Place place = places.get(0);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16);

            // prepare long lat
            setCurrentLatLong(place.getLatLng().latitude, place.getLatLng().longitude);

            // prepare pin address
            if (place.getAddress() == null) {
                this.pinAddress = place.getName().toString();
            }
            else {
                this.pinAddress = place.getAddress().toString();
            }

            output().prepareShowPlaceResult(this.pinAddress, cameraUpdate);
        }
        else {
            output().prepareShowPlaceResultError();
        }
    }

    public void getCurrentAddress(final double latitude, final double longitude, final ICartCallback callback) {
        new AsyncTask<Void, Void, String>() {

            public Geocoder mGeoCoder;

            @Override
            protected void onPreExecute() {
                mGeoCoder = new Geocoder(app().getApplicationContext(), Locale.getDefault());
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    android.location.Address address = mGeoCoder.getFromLocation(latitude, longitude, 1).get(0);
                    String currentAddress = address.getAddressLine(0);
                    for (int i = 1; i < address.getMaxAddressLineIndex(); i++) {
                        currentAddress = currentAddress.concat(", ");
                        currentAddress = currentAddress.concat(address.getAddressLine(i));
                    }

                    return currentAddress;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        return getAddressManually(latitude, longitude);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                return null;
            }

            private String getAddressManually(double latitude, double longitude) throws JSONException, IOException {
                String geocodingUrl = String
                        .format("https://$HyperModuleName.googleapis.com/$HyperModuleName/api/geocode/json?language=%s&address=%s,%s",
                                Locale.getDefault(), latitude, longitude);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(geocodingUrl).build();

                Response response = client.newCall(request).execute();
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray results = jsonObject.getJSONArray("results");
                return results.getJSONObject(0).getString("formatted_address");
            }

            @Override
            protected void onPostExecute(String address) {
                if (address != null) {
                    callback.onSuccess(address);
                }
                else {
                    callback.onFailure(null);
                }
            }
        }.execute();
    }

    public void preparePinLocation() {
        trackSelectDeliveryLocation();

        if (this.callingActivity.equals(ICartConstant.SCREEN_NAME.ADDRESS_MANAGEMENT)) {
            checkCoverageAndCreateAddress();
        }
        else if (this.callingActivity.equals(ICartConstant.SCREEN_NAME.MY_ACCOUNT)) {
            Address address = new Address();
            address.address1 = this.pinAddress;
            address.lat = this.currentLatitude;
            address.lon = this.currentLongitude;

            output().openAddressFormScreen(address);
        }
        else {
            getNearbySuppliers(this.currentLatitude, this.currentLongitude);
        }
    }

    public void prepareIntentExtras(Intent intent) {
        this.productId = intent.getLongExtra(ICartConstant.EXTRAS.DEEP_LINKS.PRODUCT_ID, 0);
        this.promotionId = intent.getLongExtra(ICartConstant.EXTRAS.DEEP_LINKS.PROMOTION_ID, 0);
        this.orderNumber = intent.getStringExtra(AddressManagementFragment.ORDER_NUMBER);
        this.stockLocationId = intent.getLongExtra(AddressManagementFragment.STOCK_LOCATION_KEY, 0);
        this.fromCheckout = intent.getBooleanExtra(AddressManagementFragment.ADDRESS_CHECKOUT_KEY, false);

        if (intent.getStringExtra(ICartConstant.EXTRAS.$HyperModuleName_PARENT_EXTRAS) != null) {
            this.callingActivity = intent.getStringExtra(ICartConstant.EXTRAS.$HyperModuleName_PARENT_EXTRAS);
        }

        if (this.callingActivity.contentEquals(ICartConstant.SCREEN_NAME.CHOOSE_STORE)) {
            output().setMarkerNextChooseSupermarket();
        }
    }

    public void trackSelectDeliveryLocation() {
        TrackingUtils.selectDeliveryLocation(this.typeInteraction, this.regionMethod);
    }

    public void checkCoverageAndCreateAddress() {
        app().getLocationManager()
                .getCoverageByPosition(this.stockLocationId, this.currentLatitude, this.currentLongitude,
                        new ICartCallback<CoverageResponse>(TAG) {
                            @Override
                            public void onSuccess(CoverageResponse response) {
                                if (!response.coverage) {
                                    output().showOutOfRangeDialog($HyperModuleNameInteractor.this.currentLatitude,
                                            $HyperModuleNameInteractor.this.currentLongitude,
                                            $HyperModuleNameInteractor.this.pinAddress);
                                    return;
                                }

                                if (StringUtils.isEmpty($HyperModuleNameInteractor.this.pinAddress)) {
                                    output().showNoAddressError();
                                    return;
                                }

                                prepareAddressForCheckout();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                output().showConnectionError();
                            }

                        });
    }

    public void getNearbySuppliers(final double latitude, final double longitude) {
        app().getLocationManager().getNearbySuppliers(latitude, longitude, app().getStockLocationId(),
                new ICartCallback<SupplierResponse>(TAG) {
                    @Override
                    public void onSuccess(SupplierResponse supplierResponse) {
                        if (!supplierResponse.suppliers.isEmpty() && supplierResponse.subDistrict != null) {
                            SubDistrict currentSubDistrict = supplierResponse.subDistrict;
                            String cityName = "";

                            if (currentSubDistrict.district != null && currentSubDistrict.district.state != null) {
                                cityName = currentSubDistrict.district.state.name;
                            }

                            ArrayList<StockLocation> stockLocations = new ArrayList<>();
                            for (Supplier supplier : supplierResponse.suppliers) {
                                stockLocations.add(supplier.convert());
                            }

                            if ($HyperModuleNameInteractor.this.callingActivity
                                    .equals(ICartConstant.SCREEN_NAME.PRODUCT_CAMPAIGN)) {
                                output().backToCampaignDetailScreen($HyperModuleNameInteractor.this.currentLatitude,
                                        $HyperModuleNameInteractor.this.currentLongitude,
                                        $HyperModuleNameInteractor.this.pinAddress);
                                return;
                            }

                            if ($HyperModuleNameInteractor.this.productId > 0) {
                                output().openCampaignDetailScreen($HyperModuleNameInteractor.this.productId,
                                        $HyperModuleNameInteractor.this.promotionId,
                                        $HyperModuleNameInteractor.this.currentLatitude,
                                        $HyperModuleNameInteractor.this.currentLongitude, currentSubDistrict.zipcode,
                                        currentSubDistrict.name,
                                        cityName, stockLocations, currentSubDistrict, $HyperModuleNameInteractor.this.pinAddress,
                                        $HyperModuleNameInteractor.this.currentAddressRemoteId);
                            }
                            else {
                                output().openChooseStoreScreen(currentSubDistrict, $HyperModuleNameInteractor.this.currentLatitude,
                                        $HyperModuleNameInteractor.this.currentLongitude, currentSubDistrict.zipcode,
                                        currentSubDistrict.name,
                                        cityName, stockLocations, $HyperModuleNameInteractor.this.pinAddress,
                                        $HyperModuleNameInteractor.this.currentAddressRemoteId);
                            }

                        }
                        else {
                            output().showOutsideAreaDialog($HyperModuleNameInteractor.this.pinAddress, latitude, longitude);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        super.onFailure(throwable);

                        output().showNoAddressError();
                    }
                });
    }

    public void prepareAddressForCheckout() {
        try {
            app().getOrderManager()
                    .createAddressOnCheckout(this.orderNumber, this.pinAddress, true, 0L, this.currentLatitude,
                            this.currentLongitude, new ICartCallback<Order>(TAG) {
                                @Override
                                public void onSuccess(Order response) {
                                    app().getSharedPreferencesManager().setMapAddress(response.shipAddress);
                                    app().getSharedPreferencesManager()
                                            .setLocalOriginalAddress(response.shipAddress.original_id);

                                    output().openCheckoutScreen();
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    super.onFailure(throwable);

                                    output().showConnectionError();
                                }
                            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void prepareRecentAddress() {
        if (this.recentAddress != null) {
            output().showRecentAddressResult(this.recentAddress);
        }
        else {
            this.userService.fetchRecentAddress().subscribe(
                    new ServiceSubscriber<List<Address>>() {
                        @Override
                        public void onFailure(Throwable e) {
                            $HyperModuleNameInteractor.this.recentAddress = null;
                        }

                        @Override
                        public void onSuccess(List<Address> data) {
                            if (data.size() > 3) {
                                $HyperModuleNameInteractor.this.recentAddress = data.subList(0, 3);
                            }
                            else {
                                $HyperModuleNameInteractor.this.recentAddress = data;
                            }

                            output().showRecentAddressResult($HyperModuleNameInteractor.this.recentAddress);
                        }
                    });
        }
    }

    public void prepareOnRecentAddressItemClick(Address address) {
        setCurrentLatLongWithId(address.lat, address.lon, address.remoteId);
        this.pinAddress = address.toString();

        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(new LatLng(this.currentLatitude, this.currentLongitude), 16);

        output().prepareShowPlaceResult(this.pinAddress, cameraUpdate);
    }

    // endregion

    // region Methods

    public void connectGoogleApiClient() {
        this.googleApiClient.connect();
    }

    public void resetGoogleApiClient() {
        if (this.lastLocation == null && !this.googleApiClient.isConnecting()) {
            this.googleApiClient.reconnect();
        }
    }

    public void removeLocationUpdates() {
        if (this.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this.locationListener);
            disconnectGoogleApiClient();
        }
    }

    public void disconnectGoogleApiClient() {
        this.googleApiClient.disconnect();
    }

    public void refreshRegionMethod() {
        this.regionMethod = this.typeInteraction;
    }

    public void setTypeInteraction(String typeInteraction) {
        this.typeInteraction = typeInteraction;
    }

    public void setCurrentLatLong(double latitude, double longitude) {
        setCurrentLatLongWithId(latitude, longitude, 0);
    }

    public void setCurrentLatLongWithId(double latitude, double longitude, long addressRemoteId) {
        this.currentLatitude = latitude;
        this.currentLongitude = longitude;
        this.currentAddressRemoteId = addressRemoteId;
    }

    public boolean isFromCheckout() {
        return fromCheckout;
    }

    // endregion
}
