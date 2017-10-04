package com.happyfresh.modules.$HyperModuleName;

import android.location.Location;

import com.google.android.gms.$HyperModuleName.CameraUpdate;
import com.happyfresh.R;
import com.happyfresh.abstracts.ViewBehavior;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public interface $HyperModuleNameViewBehavior extends ViewBehavior {

    void hideClearButtonInSearchBar();

    void showClearButtonInSearchBar();

    void suggestionsListSetVisibility(int visibility);

    void showLoadingInLocationPin();

    void hideLoadingInLocationPin();

    void showOutsideAreaDialog(String title, final Double lat, final Double lon, String zipcode);

    void showConnectionError();

    boolean isInProgress();

    void setTextForFindAddressEditText(String addressLine);

    void showOutOfRangeDialog(double latitude, double longitude, String zipCode, String areaName, String cityName,
            String pinAddress);

    void showViewsOnMapAfterDrag();

    void hideViewsOnMapDrag();

    void hideSoftKeyboard();

    void clearSearchFocus();

    void animateMapCamera(CameraUpdate cameraUpdate);

    void showLoadingInSearchBar();

    void hideLoadingInSearchBar();

    void showNoAddressError();

    void showPermissionRejected();

    void showPermissionAccepted();

    void toolbarAnimateShow();

    void toolbarAnimateHide();

    void showOverlay();

    void hideOverlay();

    void buttonAnimateShow();

    void buttonAnimateHide();

    void showGooglePlacesApiError();

    void showGPSDisabled();

    void moveCameraToCurrentLocation();

    void moveCameraToLocation(Location location);

    void showErrorAutoComplete();

    void clearSearch();

    void setMarkerNextChooseSupermarket();

    void setFindAddressEditable();

    void setFindAddressUneditable();

    void changeLeftIconToBack();

    void changeLeftIconToPin();
}
