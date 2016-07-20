package br.cin.ufpe.inesescin.smartparking;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import br.cin.ufpe.inesescin.smartparking.service.DirectionListener;
import br.cin.ufpe.inesescin.smartparking.service.DirectionServiceImp;
import br.cin.ufpe.inesescin.smartparking.service.IDirectionService;
import br.cin.ufpe.inesescin.smartparking.service.LocationListener;
import br.cin.ufpe.inesescin.smartparking.service.LocationService;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.BlockLatLngByStoreNameAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.OnBlockLatLngReceivedListener;
import br.cin.ufpe.inesescin.smartparking.util.PermissionRequest;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionListener, LocationListener, OnBlockLatLngReceivedListener {

    private GoogleMap mMap;
    private LocationService locationService;
    private IDirectionService directionService;
    private LatLng origin;
    private LatLng destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setActivityEnvironment();
        setUpGoogleMap();
        setUpDirectionsService();
    }

    public void setActivityEnvironment(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    private void setUpGoogleMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setUpDirectionsService(){
        directionService = new DirectionServiceImp();
        directionService.setDirectionListener(this);
    }

    public void callDirections(LatLng from, LatLng to) {
        directionService.getDirections(from, to);
    }

    private void callCurrentLocation(){
        locationService = new LocationService(this);
        locationService.execute();
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
        this.mMap = googleMap;
        if(PermissionRequest.checkLocationPermission(this)){
            this.mMap.setMyLocationEnabled(true);
        }else{
            PermissionRequest.requestLocationPermission(this);
        }
        this.mMap.setBuildingsEnabled(true);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng latLng = new LatLng(-8.086155, -34.894311);
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.busca);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                //Do something on submit
                BlockLatLngByStoreNameAsync blockLatLngByStoreNameAsync = new BlockLatLngByStoreNameAsync(s, MapsActivity.this);
                blockLatLngByStoreNameAsync.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBlockLatLngReceived(LatLng latLng) {
        this.destination = latLng;
        callCurrentLocation();
    }

    @Override
    public void onLocationReceived(Location location) {
        this.origin = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(origin));
        callDirections(destination, origin);
    }

    @Override
    public void onDirectionReceived(List<LatLng> latLngs) {
        PolylineOptions polylineOptions = createPolyline(latLngs, 3, Color.BLACK);
        mMap.addPolyline(polylineOptions);
    }

    public static PolylineOptions createPolyline(List<LatLng> locationList, int width, int color) {
        PolylineOptions rectLine = new PolylineOptions().width(width).color(color).geodesic(true);
        for (LatLng location : locationList) {
            rectLine.add(location);
        }
        return rectLine;
    }


}
