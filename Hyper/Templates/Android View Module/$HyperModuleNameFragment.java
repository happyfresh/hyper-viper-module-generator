package com.happyfresh.modules.$HyperModuleName;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.$HyperModuleName.CameraUpdate;
import com.google.android.gms.$HyperModuleName.CameraUpdateFactory;
import com.google.android.gms.$HyperModuleName.GoogleMap;
import com.google.android.gms.$HyperModuleName.OnMapReadyCallback;
import com.google.android.gms.$HyperModuleName.model.CameraPosition;
import com.google.android.gms.$HyperModuleName.model.LatLng;
import com.happyfresh.R;
import com.happyfresh.abstracts.BaseFragment;
import com.happyfresh.common.ICartConstant;
import com.happyfresh.customs.CircularProgressBar;
import com.happyfresh.customs.CustomEditText;
import com.happyfresh.customs.Custom$HyperModuleNameFragment;
import com.happyfresh.customs.MapWrapperFrameLayout;
import com.happyfresh.fragments.AddressManagementFragment;
import com.happyfresh.fragments.OutOfRangeDialog;
import com.happyfresh.fragments.UnsupportedDialogFragment;
import com.happyfresh.managers.tracking.TrackingUtils;
import com.happyfresh.managers.tracking.Value;
import com.happyfresh.snowflakes.hoverfly.utils.IANATimeZoneUtils;
import com.happyfresh.utils.permissions.LocationPermissionUtil;

import org.solovyev.android.views.llm.LinearLayoutManager;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by $HyperAuthorName on $HyperCreatedDate.
 */

public class $HyperModuleNameFragment extends BaseFragment<$HyperModuleNamePresenter> implements $HyperModuleNameViewBehavior, OnMapReadyCallback,
        MapWrapperFrameLayout.OnMapTouchEventListener, GoogleMap.OnCameraChangeListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 100;

    private final String ADDRESS_BAR_ON_FOCUS = "Address Bar On Focus";

    // region Butterknife Bind

    @Bind(R.id.rv_suggestions)
    RecyclerView suggestionsRecyclerView;

    @Bind(R.id.et_find_address)
    CustomEditText findAddressEditText;

    @Bind(R.id.iv_clear)
    ImageView clearImageView;

    @Bind(R.id.iv_icon)
    ImageView leftIconImageView;

    @Bind(R.id.pb_location_pin)
    CircularProgressBar locationPinProgressBar;

    @Bind(R.id.cv_search_bar)
    CardView searchBarCardView;

    @Bind(R.id.pb_search_loading)
    CircularProgressBar searchLoadingCircularProgressBar;

    @Bind(R.id.btn_next)
    Button nextButton;

    @Bind(R.id.rl_button_container)
    RelativeLayout buttonContainerLayout;

    @Bind(R.id.fab_my_location)
    FloatingActionButton myLocationFloatingButton;

    @Bind(R.id.v_overlay)
    View overlayView;

    // endregion

    // region Attributes

    private GoogleMap map;

    private Toolbar toolbar;

    private boolean addressBarOnFocus = false;

    KeyListener findAddressKeyListener;

    // endregion

    // region static

    public static $HyperModuleNameFragment newInstance(Bundle bundle) {
        $HyperModuleNameFragment fragment = new $HyperModuleNameFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    // endregion

    // region Life Cycle

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = getToolbar();

        Custom$HyperModuleNameFragment custom$HyperModuleNameFragment = (Custom$HyperModuleNameFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        custom$HyperModuleNameFragment.getMapAsync(this);
        custom$HyperModuleNameFragment.setOnTouchListener(this);

        findAddressKeyListener = findAddressEditText.getKeyListener();
        setFindAddressUneditable();

        leftIconImageView.setClickable(false);

        findAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start != before) {
                    presenter().onSearchTextChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findAddressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                presenter().onSearchFocusChanged(hasFocus);
            }
        });

        findAddressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int action, KeyEvent event) {
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    presenter().prepareOnSearchForListItemClick(textView.getText().toString());
                }
                return false;
            }
        });

        hideLoadingInSearchBar();

        initRvSuggestions();

        hideLoadingInLocationPin();

        checkGooglePlayServices();

        ViewCompat.setElevation(buttonContainerLayout, 30);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            addressBarOnFocus = savedInstanceState.getBoolean(ADDRESS_BAR_ON_FOCUS, false);
        }

        presenter().prepareInitGoogleApiClient();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter().prepareIntentExtras(getActivity().getIntent());

        presenter().prepareConnectGoogleApiClient();
        presenter().trackView$HyperModuleNamecreen();
    }

    @Override
    public void onDestroy() {
        presenter().prepareRemoveLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (findAddressEditText.hasFocus()) {
            outState.putBoolean(ADDRESS_BAR_ON_FOCUS, true);
        }
    }

    // endregion

    // region BaseFragment

    @Override
    public int layoutResID() {
        return R.layout.fragment_$HyperModuleName;
    }

    @Override
    protected $HyperModuleNamePresenter providePresenter() {
        return new $HyperModuleNamePresenter(getContext());
    }

    // endregion

    // region $HyperModuleNameViewBehavior

    @Override
    public void hideClearButtonInSearchBar() {
        clearImageView.setVisibility(View.GONE);
    }

    @Override
    public void showClearButtonInSearchBar() {
        if (!TextUtils.isEmpty(findAddressEditText.getEditableText().toString())) {
            clearImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void suggestionsListSetVisibility(int visibility) {
        suggestionsRecyclerView.setVisibility(visibility);
    }

    @Override
    public void showLoadingInLocationPin() {
        nextButton.setEnabled(false);
        locationPinProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingInLocationPin() {
        nextButton.setEnabled(true);
        locationPinProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showOutsideAreaDialog(String title, final Double lat, final Double lon, String zipcode) {
        final UnsupportedDialogFragment mUnsupportedDialog = UnsupportedDialogFragment
                .newInstance(title, null, lat, lon, zipcode);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null || getFragmentManager() == null) {
                    return;
                }

                Fragment fragment = getFragmentManager().findFragmentByTag("unsupportedDialog");
                if (fragment == null) {
                    mUnsupportedDialog.show(getFragmentManager(), "unsupportedDialog");
                    TrackingUtils.viewOutOfCoverageMapDelivery();
                }
            }
        }, 100);
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(getActivity(), getString(R.string.default_error_message_new), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isInProgress() {
        return locationPinProgressBar.getVisibility() == View.VISIBLE;
    }

    @Override
    public void setTextForFindAddressEditText(String addressLine) {
        findAddressEditText.setText(addressLine);
    }

    @Override
    public void setFindAddressEditable() {
        findAddressEditText.setKeyListener(findAddressKeyListener);
    }

    @Override
    public void setFindAddressUneditable() {
        findAddressEditText.setKeyListener(null);
    }

    @Override
    public void showOutOfRangeDialog(double latitude, double longitude, String zipCode, String areaName,
            String cityName, String pinAddress) {
        final OutOfRangeDialog dialog = OutOfRangeDialog.newInstance(
                getActivity().getIntent().getStringExtra(AddressManagementFragment.STOCK_LOCATION_NAME_KEY), latitude,
                longitude, zipCode, areaName, cityName, pinAddress);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(OutOfRangeDialog.TAG);
                if (fragment == null) {
                    dialog.show(getActivity().getSupportFragmentManager(), OutOfRangeDialog.TAG);

                    TrackingUtils.viewOutOfCoverageAlert(Value.Screen.Map);
                }
            }
        }, 100);
    }

    @Override
    public void showViewsOnMapAfterDrag() {
        toolbarAnimateShow();
        buttonAnimateShow();
        hideOverlay();
    }

    @Override
    public void hideViewsOnMapDrag() {
        toolbarAnimateHide();
        buttonAnimateHide();
        clearSearchFocus();
        hideOverlay();

        suggestionsRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findAddressEditText.getWindowToken(), 0);
    }

    @Override
    public void clearSearchFocus() {
        findAddressEditText.clearFocus();
    }

    @Override
    public void animateMapCamera(CameraUpdate cameraUpdate) {
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void showLoadingInSearchBar() {
        searchLoadingCircularProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingInSearchBar() {
        searchLoadingCircularProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showNoAddressError() {
        Toast.makeText(getActivity(), R.string.$HyperModuleName_no_subdistrict_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showPermissionRejected() {
        Toast.makeText(getActivity(), R.string.$HyperModuleName_permission_rejected, Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void showPermissionAccepted() {
        presenter().prepareMoveCameraToCurrentLocation();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void toolbarAnimateShow() {
        toolbar.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(180);

        searchBarCardView.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(180);
    }

    @Override
    public void toolbarAnimateHide() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new LinearInterpolator())
                .setDuration(180);

        searchBarCardView.animate().translationY(-toolbar.getHeight() / 1.5f).setInterpolator(new LinearInterpolator())
                .setDuration(180);
    }

    @Override
    public void showOverlay() {
        myLocationFloatingButton.setVisibility(View.GONE);
        overlayView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideOverlay() {
        myLocationFloatingButton.setVisibility(View.VISIBLE);
        overlayView.setVisibility(View.GONE);
    }

    @Override
    public void buttonAnimateShow() {
        int height = buttonContainerLayout.getHeight();
        if (map != null) {
            map.setPadding(0, 0, 0, height);
        }
        buttonContainerLayout.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(180);
        myLocationFloatingButton.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(180);
    }

    @Override
    public void buttonAnimateHide() {
        int height = buttonContainerLayout.getHeight();
        if (map != null) {
            map.setPadding(0, 0, 0, 0);
        }
        buttonContainerLayout.animate().translationY(height).setInterpolator(new LinearInterpolator()).setDuration(180);
        myLocationFloatingButton.animate().translationY(height).setInterpolator(new LinearInterpolator()).setDuration(180);
    }

    @Override
    public void showGooglePlacesApiError() {
        if (isAdded()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.default_error_message_google_places_api),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showGPSDisabled() {
        Toast.makeText(getActivity(), R.string.$HyperModuleName_gps_disabled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void moveCameraToCurrentLocation() {
        if (getActivity() == null || !LocationPermissionUtil.with(getActivity()).isGranted()) {
            return;
        }

        presenter().prepareMoveCameraToCurrentLocation();
    }

    @Override
    public void moveCameraToLocation(Location lastLocation) {
        if (lastLocation != null && map != null) {
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 16));
        }
    }

    @Override
    public void showErrorAutoComplete() {
        Toast.makeText(getActivity(), getResources().getString(R.string.default_error_message_google_places_api),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clearSearch() {
        findAddressEditText.setText("");
    }

    @Override
    public void setMarkerNextChooseSupermarket() {
        nextButton.setText(getString(R.string.next_choose_store));
    }

    @Override
    public void changeLeftIconToBack() {
        leftIconImageView.setImageResource(R.drawable.hf_icon_back_dark);
        leftIconImageView.setClickable(true);
    }

    @Override
    public void changeLeftIconToPin() {
        leftIconImageView.setImageResource(R.drawable.hf_icon_location_dark);
        leftIconImageView.setClickable(false);
    }

    // endregion

    // region $HyperModuleName Listener

    @SuppressWarnings("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraChangeListener(this);
        map.moveCamera(CameraUpdateFactory
                .newLatLngZoom(IANATimeZoneUtils.getCountrySearchBounds(getActivity()).getCenter(), 4f));
        map.getUiSettings().setMyLocationButtonEnabled(false);

        if (LocationPermissionUtil.with(getActivity())
                .isGranted(true, ICartConstant.REQUEST_CODES.PERMISSION_REQUEST_LOCATION)) {
            map.setMyLocationEnabled(true);
        }

        map.setPadding(0, 0, 0, buttonContainerLayout.getHeight());

        if (addressBarOnFocus) {
            buttonAnimateHide();
            addressBarOnFocus = false;
        }
    }

    @Override
    public void onMapTouchEvent(MotionEvent ev) {
        presenter().onMapTouchEvent(ev);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        presenter().prepareMoveCamera(cameraPosition);
    }

    // endregion

    // region Events

    @OnClick(R.id.fab_my_location)
    void onMyLocationButtonClick() {
        presenter().onMyLocationButtonClick();
    }

    @OnClick(R.id.btn_next)
    void onLocationPinClick() {
        presenter().preparePinLocation();
    }

    @OnClick(R.id.iv_clear)
    void onIvClearClick() {
        findAddressEditText.getText().clear();
    }

    @OnClick(R.id.iv_icon)
    void onIvIconClick() {
        presenter().onSearchBackPressed();
    }

    @OnClick(R.id.et_find_address)
    void onFindAddressBarClick() {
        presenter().setFindAddressOnFocus(true);
    }


    // endregion

    //region Methods

    private void initRvSuggestions() {
        LinearLayoutManager suggestedLayoutManager = new org.solovyev.android.views.llm.LinearLayoutManager(
                getActivity());

        suggestionsRecyclerView.setLayoutManager(suggestedLayoutManager);
        suggestionsRecyclerView.setAdapter(presenter().getSuggestedAreaAdapter());
        suggestionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                presenter().onScrollStateChanged(newState);
            }
        });
    }

    private void checkGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }
    }

    public boolean onBackPressedHandled() {
        return presenter().prepareOnBackPressedHandled();
    }

    // endregion
}
