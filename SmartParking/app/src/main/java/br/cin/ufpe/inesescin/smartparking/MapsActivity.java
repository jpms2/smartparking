package br.cin.ufpe.inesescin.smartparking;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import br.cin.ufpe.inesescin.smartparking.asyncTasks.CheckExistenceAndCreateOrUpdateAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.LatLngByUserPreferencesAsync;
import br.cin.ufpe.inesescin.smartparking.service.DirectionListener;
import br.cin.ufpe.inesescin.smartparking.service.DirectionServiceImp;
import br.cin.ufpe.inesescin.smartparking.service.IDirectionService;
import br.cin.ufpe.inesescin.smartparking.service.LocationListener;
import br.cin.ufpe.inesescin.smartparking.service.LocationService;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.BlockLatLngByStoreNameAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.OnBlockLatLngReceivedListener;
import br.cin.ufpe.inesescin.smartparking.util.Constants;
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
        getSearchQuery();
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

        LatLng latLng = new LatLng(-8.084905, -34.894845);
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.2f));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.busca).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }

    public void getSearchQuery(){
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            BlockLatLngByStoreNameAsync asyncBlsn = new BlockLatLngByStoreNameAsync(query, MapsActivity.this);
            asyncBlsn.execute();
            CheckExistenceAndCreateOrUpdateAsync checkEcua = new CheckExistenceAndCreateOrUpdateAsync(Constants.USERNAME,query);
            checkEcua.execute();
        } else if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            String data = intent.getDataString();
            if(data.equals("O de sempre")){
                LatLngByUserPreferencesAsync asyncBupa = new LatLngByUserPreferencesAsync(Constants.USERNAME,MapsActivity.this);
                asyncBupa.execute();
            }else{
                BlockLatLngByStoreNameAsync asyncBlsn = new BlockLatLngByStoreNameAsync(data, MapsActivity.this);
                asyncBlsn.execute();
                CheckExistenceAndCreateOrUpdateAsync checkEcua = new CheckExistenceAndCreateOrUpdateAsync(Constants.USERNAME,data);
                checkEcua.execute();
            }
        }

    }

    public String getStoreFromData(String data){

        return "";
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
        LatLng problematicLatLng = new LatLng(0,0);
        if(latLng!=null){
            if(latLng == problematicLatLng){
                Toast.makeText(this, "Essa loja não está cadastrada em nosso sistema, tente novamente", Toast.LENGTH_SHORT).show();
            }else{
                this.destination = latLng;
                callCurrentLocation();
            }
        }else{
            Toast.makeText(this, "Erro ao se conectar com o servidor. Tente novamente mais tarde", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        this.origin = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(origin).title("Usuario"));
//        mMap.addMarker(new MarkerOptions().position(destination).title("Destino"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-8.084884, -34.894539)).title("Destino")); //TODO apenas para teste
        callDirections(origin, destination);
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
