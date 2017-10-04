package com.happyfresh.modules.$HyperModuleName;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.$HyperModuleName.CameraUpdate;
import com.google.android.gms.$HyperModuleName.model.CameraPosition;
import com.happyfresh.abstracts.BasePresenter;
import com.happyfresh.managers.tracking.TrackingUtils;
import com.happyfresh.managers.tracking.Value;
import com.happyfresh.models.TextSearchResult;
import com.happyfresh.snowflakes.hoverfly.models.Address;
import com.happyfresh.snowflakes.hoverfly.models.StockLocation;
import com.happyfresh.snowflakes.hoverfly.models.SubDistrict;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNamePresenter extends BasePresenter<$HyperModuleNameInteractor, $HyperModuleNameRouter, $HyperModuleNameViewBehavior>
        implements $HyperModuleNameInteractorOutput {

    // region Attributes

    private AutoSubmitTimer mTimerToGetSearchResult;

    private SuggestedAreaAdapter mSuggestedAreaAdapter;

    private ShowViewsAfterMapDragTimer mShowViewsAfterMapDragTimer;

    private boolean mDisableGetAddressOnCameraChange;

    private boolean mShouldShowAutoSuggestion = true;

    private boolean mInProgress;

    private boolean mFromMore;

    private boolean findAddressOnFocus = false;

    // endregion

    // region Constructor

    public $HyperModuleNamePresenter(Context context) {
        super(context);
        mTimerToGetSearchResult = new AutoSubmitTimer();
        mShowViewsAfterMapDragTimer = new ShowViewsAfterMapDragTimer();
        initSuggestedAreaAdapter(context);
    }

    // endregion

    // region BasePresenter

    @Override
    protected $HyperModuleNameInteractor provideInteractor() {
        return new $HyperModuleNameInteractor();
    }

    @Override
    protected $HyperModuleNameRouter provideRouter(Context context) {
        return new $HyperModuleNameRouter(context);
    }

    // endregion

    //region $HyperModuleNameInteractorOutput

    @Override
    public void showGooglePlacesApiError() {
        view.showGooglePlacesApiError();
    }

    @Override
    public void showGPSDisabled() {
        if (!isViewAttached()) {
            return;
        }

        view.showGPSDisabled();
    }

    @Override
    public void moveCameraToCurrentLocation() {
        if (!isViewAttached()) {
            return;
        }

        view.moveCameraToCurrentLocation();
    }

    @Override
    public void moveCameraToLocation(Location location) {
        if (!isViewAttached()) {
            return;
        }

        view.moveCameraToLocation(location);
    }

    @Override
    public void showErrorAutoComplete() {
        if (!isViewAttached()) {
            return;
        }

        view.suggestionsListSetVisibility(View.GONE);
        view.showErrorAutoComplete();
    }

    @Override
    public void showPredictions(String query, ArrayList<AutocompletePrediction> predictions) {
        mSuggestedAreaAdapter.setSearchForText(query);
        int size = mSuggestedAreaAdapter.setData(query, predictions);
        mSuggestedAreaAdapter.notifyDataSetChanged();
        interactor().track$HyperModuleNameearchAddress(query, size);
        mFromMore = false;

        if (isViewAttached()) {
            view.suggestionsListSetVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showTextSearchResults(List<TextSearchResult> results, String searchTerm) {
        if (!isViewAttached()) {
            return;
        }

        int size = setDataTextSearch(results, searchTerm);
        view.suggestionsListSetVisibility(View.VISIBLE);

        interactor().track$HyperModuleNameearchMoreAddress(searchTerm, size);
        mFromMore = true;
    }

    @Override
    public void showTextSearchFailed() {
        if (!isViewAttached()) {
            return;
        }

        view.suggestionsListSetVisibility(View.GONE);
    }

    @Override
    public void prepareShowPlaceResult(String placeName, CameraUpdate cameraUpdate) {
        if (!isViewAttached()) {
            return;
        }

        mShouldShowAutoSuggestion = false;
        mDisableGetAddressOnCameraChange = true;

        view.suggestionsListSetVisibility(View.GONE);
        view.hideLoadingInSearchBar();
        view.setTextForFindAddressEditText(placeName);
        view.animateMapCamera(cameraUpdate);

    }

    @Override
    public void showConnectionError() {
        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        view.showConnectionError();
    }

    @Override
    public void prepareSetTextForEtFindAddress(String pinAddress) {
        if (!isViewAttached()) {
            return;
        }

        if (StringUtils.isNotEmpty(pinAddress)) {
            // auto suggest false first before set text for find address to avoid auto suggest
            mShouldShowAutoSuggestion = false;

            view.setTextForFindAddressEditText(pinAddress);
        }

        view.hideLoadingInLocationPin();
    }

    @Override
    public void openAddressFormScreen(Address address) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        router().openAddressFormScreen(address);
    }

    @Override
    public void showOutOfRangeDialog(double latitude, double longitude, String pinAddress) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        view.showOutOfRangeDialog(latitude, longitude, "", "", "", pinAddress);
    }

    @Override
    public void showNoAddressError() {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        view.showNoAddressError();
    }

    @Override
    public void openCheckoutScreen() {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        router().openCheckoutScreen();
    }

    @Override
    public void backToCampaignDetailScreen(double latitude, double longitude, String pinAddress) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        router().backToCampaignDetailScreen(latitude, longitude, pinAddress);
    }

    @Override
    public void openCampaignDetailScreen(long productId, long promotionId, double latitude, double longitude,
            String zipcode, String areaName, String cityName, ArrayList<StockLocation> stockLocations,
            SubDistrict subDistrict, String pinAddress, long addressId) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        router().openCampaignDetailScreen(productId, promotionId, latitude, longitude, zipcode, areaName, cityName,
                stockLocations, subDistrict, pinAddress, addressId);
    }

    @Override
    public void openChooseStoreScreen(SubDistrict subDistrict, double latitude, double longitude, String zipcode,
            String areaName, String cityName, ArrayList<StockLocation> stockLocations, String pinAddress,
            long addressId) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        router().openChooseStoreScreen(subDistrict, latitude, longitude, zipcode, areaName, cityName, stockLocations,
                pinAddress, addressId);
    }

    @Override
    public void showOutsideAreaDialog(String pinAddress, double latitude, double longitude) {
        mInProgress = false;

        if (!isViewAttached()) {
            return;
        }

        view.hideLoadingInLocationPin();
        view.showOutsideAreaDialog(pinAddress, latitude, longitude, "");
    }

    @Override
    public void prepareShowPlaceResultError() {
        if (!isViewAttached()) {
            return;
        }

        view.suggestionsListSetVisibility(View.VISIBLE);
        view.showConnectionError();
    }

    @Override
    public void setMarkerNextChooseSupermarket() {
        view.setMarkerNextChooseSupermarket();
    }

    @Override
    public void showRecentAddressResult(List<Address> results) {
        if (!isViewAttached() || results.size() == 0) {
            return;
        }

        mShouldShowAutoSuggestion = false;
        mSuggestedAreaAdapter.setDataRecentAddress(results);
        mSuggestedAreaAdapter.notifyDataSetChanged();
        view.suggestionsListSetVisibility(View.VISIBLE);
    }

    // endregion

    // region Functionality

    public void prepareConnectGoogleApiClient() {
        interactor().connectGoogleApiClient();
    }

    public void prepareRemoveLocationUpdates() {
        interactor().removeLocationUpdates();
    }

    public void trackView$HyperModuleNamecreen() {
        interactor().trackView$HyperModuleNamecreen();
    }

    public void prepareInitGoogleApiClient() {
        interactor().initGoogleApiClient();
    }

    public void prepareMoveCameraToCurrentLocation() {
        interactor().initMyLocation();
    }

    public void prepareMoveCamera(CameraPosition cameraPosition) {
        if (!mDisableGetAddressOnCameraChange && !mInProgress) {
            view.showLoadingInLocationPin();
            view.clearSearch();
            interactor().prepareGetAddressOnCameraChange(cameraPosition);
        }
        else {
            mDisableGetAddressOnCameraChange = false;
        }
    }

    public void onSearchTextChanged(String query) {
        if (!isViewAttached()) {
            return;
        }

        if (mShouldShowAutoSuggestion) {
            mTimerToGetSearchResult.cancel();

            if (StringUtils.isEmpty(query)) {
                view.suggestionsListSetVisibility(View.GONE);
            }
            else {
                mTimerToGetSearchResult.setQuery(query);
                mTimerToGetSearchResult.start();
            }
        }

        // force into true
        mShouldShowAutoSuggestion = true;

        if (StringUtils.isEmpty(query)) {
            view.hideClearButtonInSearchBar();
        }
    }

    public void onSearchFocusChanged(boolean hasFocus) {
        interactor().setTypeInteraction(Value.Interaction.EnterAddress);
        interactor().refreshRegionMethod();

        if (hasFocus) {
            view.toolbarAnimateHide();
            view.buttonAnimateHide();
            view.showOverlay();
            view.showClearButtonInSearchBar();
            view.setFindAddressEditable();
            view.changeLeftIconToBack();
            setFindAddressOnFocus(true);
            interactor().prepareRecentAddress();
        }
        else {
            view.hideClearButtonInSearchBar();
            view.setFindAddressUneditable();
            view.changeLeftIconToPin();
            setFindAddressOnFocus(false);
        }
    }

    public void onSearchBackPressed() {
        view.hideSoftKeyboard();
        view.toolbarAnimateShow();
        view.buttonAnimateShow();
        view.hideOverlay();
        view.clearSearchFocus();
        view.suggestionsListSetVisibility(View.GONE);
        setFindAddressOnFocus(false);
    }

    public void onMapTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                mShowViewsAfterMapDragTimer.start();
                break;

            case MotionEvent.ACTION_DOWN:
                if (mShowViewsAfterMapDragTimer.isInProgress()) {
                    mShowViewsAfterMapDragTimer.cancel();
                }
                else {
                    view.hideViewsOnMapDrag();
                }

                view.hideSoftKeyboard();

                interactor().setTypeInteraction(Value.Interaction.DragMap);
                interactor().refreshRegionMethod();
                break;

        }
    }

    public void onScrollStateChanged(int newState) {
        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
            view.hideSoftKeyboard();
        }
    }

    public void prepareOnSearchForListItemClick(String searchTerm) {
        interactor().prepareOnSearchForListItemClick(searchTerm);
    }

    public void onMyLocationButtonClick() {
        interactor().resetGoogleApiClient();

        interactor().initMyLocation(true);

        interactor().setTypeInteraction(Value.Interaction.AutoLocate);
        interactor().refreshRegionMethod();
    }

    public void prepareOnAddressNotFoundItemClick() {
        view.hideOverlay();
        view.hideSoftKeyboard();
        view.setTextForFindAddressEditText("");
        view.clearSearchFocus();
        view.suggestionsListSetVisibility(View.GONE);
    }

    public void prepareOnRecentAddressItemClick(Address address) {
        onAddressItemClickTransition();
        interactor().setTypeInteraction(Value.Interaction.RecentAddress);
        interactor().refreshRegionMethod();
        interactor().prepareOnRecentAddressItemClick(address);
    }

    public void prepareOnSuggestedListItemClick(String searchTerm, final String placeId) {
        onAddressItemClickTransition();
        interactor().prepareGetPlaceById(placeId);
    }

    private void onAddressItemClickTransition() {
        view.toolbarAnimateShow();
        view.buttonAnimateShow();
        view.hideOverlay();
        view.clearSearchFocus();
        view.hideSoftKeyboard();
        view.suggestionsListSetVisibility(View.INVISIBLE);
        view.showLoadingInSearchBar();
    }

    public void preparePinLocation() {
        if (mInProgress) {
            return;
        }

        mInProgress = true;

        view.showLoadingInLocationPin();

        interactor().preparePinLocation();
    }

    public void prepareIntentExtras(Intent intent) {
        interactor().prepareIntentExtras(intent);
    }

    public boolean isFindAddressOnFocus() {
        return findAddressOnFocus;
    }

    public void setFindAddressOnFocus(boolean onFocus) {
        findAddressOnFocus = onFocus;
    }

    public boolean prepareOnBackPressedHandled() {
        if (isFindAddressOnFocus()) {
            onSearchBackPressed();
            return true;
        }

        if (interactor().isFromCheckout()) {
            router().openCheckoutScreen();
            return true;
        }

        return false;
    }

    // endregion

    // region Methods

    public void initSuggestedAreaAdapter(Context context) {
        mSuggestedAreaAdapter = new SuggestedAreaAdapter(context);

        mSuggestedAreaAdapter.setOnSuggestedItemClickListener(
                new SuggestedAreaAdapter.SuggestedAreaListItemClickListener() {
                    @Override
                    public void onSearchForListItemClick(String searchTerm) {
                        prepareOnSearchForListItemClick(searchTerm);
                    }

                    @Override
                    public void onAddressNotFoundItemClick() {
                        prepareOnAddressNotFoundItemClick();
                    }

                    @Override
                    public void onRecentAddressItemClick(Address address) {
                        prepareOnRecentAddressItemClick(address);
                    }

                    @Override
                    public void onSuggestedListItemClick(String searchTerm, @Nullable String placeId,
                            @Nullable long taxonId,
                            int position) {
                        prepareOnSuggestedListItemClick(searchTerm, placeId);
                        TrackingUtils.trackMapAddressTapped(searchTerm, mFromMore);
                    }
                });
    }

    public SuggestedAreaAdapter getSuggestedAreaAdapter() {
        return mSuggestedAreaAdapter;
    }

    public int setDataTextSearch(List<TextSearchResult> results, String searchTerm) {
        int size = mSuggestedAreaAdapter.setDataTextSearch(searchTerm, results);
        mSuggestedAreaAdapter.notifyDataSetChanged();

        return size;
    }

    // endregion

    // region Sub Class

    private class AutoSubmitTimer extends CountDownTimer {

        private String mQuery;

        public AutoSubmitTimer() {
            super(500, 500);
        }

        @Override
        public void onTick(long millis) {
        }

        @Override
        public void onFinish() {
            interactor().getAutoComplete(mQuery);
            view.showClearButtonInSearchBar();
        }

        public void setQuery(String query) {
            mQuery = query;
        }
    }

    private class ShowViewsAfterMapDragTimer extends CountDownTimer {

        private boolean mInProgress = false;

        public ShowViewsAfterMapDragTimer() {
            super(700, 100);
        }

        public boolean isInProgress() {
            return mInProgress;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (!mInProgress) {
                mInProgress = true;
            }
        }

        @Override
        public void onFinish() {
            mInProgress = false;

            if (isViewAttached()) {
                view.showViewsOnMapAfterDrag();
            }
        }
    }

    // endregion
}
