package com.dylanmohsoftware.publictrans;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dylanmohsoftware.publictrans.Modules.DirectionFinder;
import com.dylanmohsoftware.publictrans.Modules.DirectionFinderListener;
import com.dylanmohsoftware.publictrans.Modules.Route;
import com.dylanmohsoftware.publictrans.Modules.RouteStep;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener, MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    StepsRecyclerViewAdapter stepsAdapter;

    private Context mContext;
    private Activity mActivity;
    private LinearLayout mLinearLayout;
    Route theSelectedRoute;

    private GoogleMap mMap;
    private EditText editTextOrigin;
    private EditText editTextDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarker = new ArrayList<>();
    private List<Polyline> polyLinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private String state = "search";
    private RadioGroup filters;
    private RadioButton filterSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mContext = getApplicationContext();
        mActivity = MapsActivity.this;
        mLinearLayout = (LinearLayout) findViewById(R.id.main_layout);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        state = "search";
        editTextOrigin = (EditText)findViewById(R.id.editTextOrigin);
        editTextDestination = (EditText)findViewById(R.id.editTextDestination);
        editTextOrigin.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String destination = editTextDestination.getText().toString();
                if (!destination.isEmpty()) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        sendRequest();
                        return true;
                    }
                    return false;
                }
                return false;
            }

        });
        editTextDestination.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String origin = editTextOrigin.getText().toString();
                if (!origin.isEmpty()) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        sendRequest();
                        return true;
                    }
                    return false;
                }
                return false;
            }

        });
    }

    public void filtersButton(View view) {
        state = "filters";
        findViewById(R.id.search_layout).setVisibility(View.GONE);
        findViewById(R.id.map_layout).setVisibility(View.GONE);
        findViewById(R.id.filters_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (state != "search") {
            if (state == "filters") {
                state = "search";
                findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.map_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.filters_layout).setVisibility(View.GONE);
            }
            else if (state == "results") {
                state = "search";
                findViewById(R.id.map_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.results_layout).setVisibility(View.GONE);
            }
            else if (state == "steps") {
                state = "results";
                findViewById(R.id.results_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.steps_layout_wrap).setVisibility(View.GONE);
            }
            else if (state == "success") {
                state = "steps";
                findViewById(R.id.steps_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.map_layout).setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        List<RouteStep> the_steps = adapter.getItem(position).steps;
        state = "steps";
        findViewById(R.id.results_layout).setVisibility(View.GONE);
        findViewById(R.id.steps_layout_wrap).setVisibility(View.VISIBLE);

        RecyclerView stepsRecyclerView = findViewById(R.id.steps_recyler_view);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stepsAdapter = new StepsRecyclerViewAdapter(this, the_steps);
        stepsRecyclerView.setAdapter(stepsAdapter);
        theSelectedRoute = adapter.getItem(position);
    }

    private void sendRequest() {
        editTextDestination.clearFocus();
        InputMethodManager in = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editTextDestination.getWindowToken(), 0);
        String origin = editTextOrigin.getText().toString();
        String destination = editTextDestination.getText().toString();
        filters = findViewById(R.id.priority_filter);
        filterSelected = findViewById(filters.getCheckedRadioButtonId());
        String the_Filter = "";
        if (filterSelected.getText() == "Less Walking") {
            the_Filter = "less_walking";
        }
        else if (filterSelected.getText() == "Fewer Transfers") {
            the_Filter = "fewer_transfers";
        }
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()){
            Toast.makeText(this, "Please enter destination!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding direction", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarker != null) {
            for (Marker marker : destinationMarker) {
                marker.remove();
            }
        }
        if (polyLinePaths != null) {
            for (Polyline polylinePath : polyLinePaths) {
                polylinePath.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderList(List<Route> routes) {
        progressDialog.dismiss();
        state = "results";
        findViewById(R.id.map_layout).setVisibility(View.GONE);
        findViewById(R.id.results_layout).setVisibility(View.VISIBLE);

        RecyclerView recyclerView = findViewById(R.id.results_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, routes);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void onDirectionFinderSuccess(View view) {
        state = "success";
        findViewById(R.id.steps_layout).setVisibility(View.GONE);
        findViewById(R.id.map_layout).setVisibility(View.VISIBLE);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarker != null) {
            for (Marker marker : destinationMarker) {
                marker.remove();
            }
        }
        if (polyLinePaths != null) {
            for (Polyline polylinePath : polyLinePaths) {
                polylinePath.remove();
            }
        }
        polyLinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarker = new ArrayList<>();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(theSelectedRoute.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(theSelectedRoute.startAddress)
                    .position(theSelectedRoute.startLocation)));

            destinationMarker.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(theSelectedRoute.endAddress)
                    .position(theSelectedRoute.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10);

            for (int i = 0; i < theSelectedRoute.points.size(); i++) {
                polylineOptions.add(theSelectedRoute.points.get(i));
            }

            polyLinePaths.add(mMap.addPolyline(polylineOptions));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int reqCode = 1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
            return;
        }
        mMap.setMyLocationEnabled(true);

    }
}
