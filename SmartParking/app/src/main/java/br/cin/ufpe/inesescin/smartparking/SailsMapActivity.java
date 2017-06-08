package br.cin.ufpe.inesescin.smartparking;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sails.engine.Beacon;
import com.sails.engine.LocationRegion;
import com.sails.engine.MarkerManager;
import com.sails.engine.PathRoutingManager;
import com.sails.engine.PinMarkerManager;
import com.sails.engine.SAILS;
import com.sails.engine.SAILSMapView;
import com.sails.engine.core.model.GeoPoint;
import com.sails.engine.overlay.Marker;

import java.util.List;

import br.cin.ufpe.inesescin.smartparking.asyncTasks.BlockNameByStoreNameAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.CheckExistenceAndCreateOrUpdateAsync;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.OnBlockNameReceivedListener;
import br.cin.ufpe.inesescin.smartparking.asyncTasks.BlockNameByUserPreferencesAsync;
import br.cin.ufpe.inesescin.smartparking.util.Constants;
import br.cin.ufpe.inesescin.smartparking.util.PermissionRequest;

public class SailsMapActivity extends AppCompatActivity implements OnBlockNameReceivedListener {

    static SAILS mSails;
    static SAILSMapView mSailsMapView;
    ImageView zoomin;
    ImageView zoomout;
    ImageView lockcenter;
    Button endRouteButton;
    Spinner floorList;
    ArrayAdapter<String> adapter;
    byte zoomSav = 0;
    private String searchString;
    Boolean finished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sails_map);
        setActivityEnvironment();
        if(!PermissionRequest.checkLocationPermission(this)){
            PermissionRequest.requestLocationPermission(this);
        }
        getSearchQuery();
        //floorList = (Spinner) findViewById(R.id.spinner);
        endRouteButton = (Button) findViewById(R.id.stopRoute);
        endRouteButton.setVisibility(View.INVISIBLE);
        lockcenter = (ImageView) findViewById(R.id.lockcenter);
        zoomin = (ImageView) findViewById(R.id.zoomin);
        zoomout = (ImageView) findViewById(R.id.zoomout);
        zoomin.setOnClickListener(controlListener);
        zoomout.setOnClickListener(controlListener);
        lockcenter.setOnClickListener(controlListener);
        //new a SAILS engine.
        mSails = new SAILS(this);
        //set location mode.
        mSails.setMode(SAILS.WIFI_GFP_IMU);
        //set floor number sort rule from descending to ascending.
        mSails.setReverseFloorList(true);
        //create location change call back.
        mSails.setOnLocationChangeEventListener(new SAILS.OnLocationChangeEventListener() {
            @Override
            public void OnLocationChange() {

                if (mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor() && !mSails.getFloor().equals("") && mSails.isLocationFix()) {
                    //set the map that currently location engine recognize.
                    mSailsMapView.getMapViewPosition().setZoomLevel((byte) 20);
                    mSailsMapView.loadCurrentLocationFloorMap();
                    Toast t = Toast.makeText(getBaseContext(), mSails.getFloorDescription(mSails.getFloor()), Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        mSails.setOnBLEPositionInitialzeCallback(5000,new SAILS.OnBLEPositionInitializeCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFixed() {

            }

            @Override
            public void onTimeOut() {
                if(!mSails.checkMode(SAILS.BLE_ADVERTISING))
                    mSails.stopLocatingEngine();
                new AlertDialog.Builder(SailsMapActivity.this)
                        .setTitle("Positioning Timeout")
                        .setMessage("Put some time out message!")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialoginterface, int i) {
                                mSailsMapView.setMode(SAILSMapView.GENERAL);
                            }
                        }).setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mSails.startLocatingEngine();
                    }
                }).show();
            }
        });

        mSails.setNoWalkAwayPushRepeatDuration(6000);
        mSails.setOnBTLEPushEventListener(new SAILS.OnBTLEPushEventListener() {
            @Override
            public void OnPush(final Beacon mB) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(),mB.push_name,Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void OnNothingPush() {
                Log.e("Nothing Push","true");
            }
        });

        //new and insert a SAILS MapView from layout resource.
        mSailsMapView = new SAILSMapView(this);
        ((FrameLayout) findViewById(R.id.SAILSMap)).addView(mSailsMapView);
        //configure SAILS map after map preparation finish.
        mSailsMapView.post(new Runnable() {
            @Override
            public void run() {
                //please change token and building id to your own building project in cloud.
                   // mSails.loadCloudBuilding("f920fef19da544d493c7ee2b02202c02", "57d8265a08920f6b4b0003fc", new SAILS.OnFinishCallback() {
                mSails.loadCloudBuilding("f920fef19da544d493c7ee2b02202c02", "5873e7edcc8415f521000137", new SAILS.OnFinishCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapViewInitial();
                                routingInitial();
                            }
                        });
                    }

                    @Override
                    public void onFailed(String response) {
                        Toast t = Toast.makeText(getBaseContext(), "Load cloud project fail, please check network connection.", Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
            }
        });
        finished = true;
    }

    public void setActivityEnvironment(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            this.searchString = query;
            BlockNameByStoreNameAsync asyncBlsn = new BlockNameByStoreNameAsync(query, this);
            asyncBlsn.execute();
            CheckExistenceAndCreateOrUpdateAsync checkEcua = new CheckExistenceAndCreateOrUpdateAsync(Constants.USERNAME,query);
            checkEcua.execute();
        } else if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            String data = intent.getDataString();
            if(data.equals("O de sempre")){
                BlockNameByUserPreferencesAsync asyncBupa = new BlockNameByUserPreferencesAsync(Constants.USERNAME, this);
                asyncBupa.execute();
            }else{
                this.searchString = data;
                CheckExistenceAndCreateOrUpdateAsync checkEcua = new CheckExistenceAndCreateOrUpdateAsync(Constants.USERNAME,data);
                checkEcua.execute();
                BlockNameByStoreNameAsync asyncBlsn = new BlockNameByStoreNameAsync(data, this);
                asyncBlsn.execute();
            }
        }
    }

    void mapViewInitial() {
        //establish a connection of SAILS engine into SAILS MapView.
        mSailsMapView.setSAILSEngine(mSails);

        //set location pointer icon.
        mSailsMapView.setLocationMarker(R.drawable.circle, R.drawable.arrow, null, 35);

        //set location marker visible.
        mSailsMapView.setLocatorMarkerVisible(true);

        //load first floor map in package.
        mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(0));

        //Auto Adjust suitable map zoom level and position to best view position.
        mSailsMapView.autoSetMapZoomAndView();

        //set location region click call back.
        mSailsMapView.setOnRegionClickListener(new SAILSMapView.OnRegionClickListener() {
            @Override
            public void onClick(List<LocationRegion> locationRegions) {
                LocationRegion lr = locationRegions.get(0);
                //begin to routing

                if (mSails.isLocationEngineStarted()) {
                    //set routing start point to current user location.
                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);

                    //set routing end point marker icon.
                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

                    //set routing path's color.
                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);
                    endRouteButton.setVisibility(View.VISIBLE);

                } else {
                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF85b038);
                    if (mSailsMapView.getRoutingManager().getStartRegion() != null)
                        endRouteButton.setVisibility(View.VISIBLE);
                }

                //set routing end point location.
                mSailsMapView.getRoutingManager().setTargetRegion(lr);

                //begin to route.
                mSailsMapView.getRoutingManager().enableHandler();
            }
        });

        //design some action in floor change call back.
        mSailsMapView.setOnFloorChangedListener(new SAILSMapView.OnFloorChangedListener() {
            @Override
            public void onFloorChangedBefore(String floorName) {
                //get current map view zoom level.
                zoomSav = mSailsMapView.getMapViewPosition().getZoomLevel();
            }

            @Override
            public void onFloorChangedAfter(final String floorName) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        //check is locating engine is start and current brows map is in the locating floor or not.
                        if (mSails.isLocationEngineStarted() && mSailsMapView.isInLocationFloor()) {
                            //change map view zoom level with animation.
                            mSailsMapView.setAnimationToZoom(zoomSav);
                        }
                    }
                };
                new Handler().postDelayed(r, 1000);

                int position = 0;
                for (String mS : mSails.getFloorNameList()) {
                    if (mS.equals(floorName))
                        break;
                    position++;
                }
                floorList.setSelection(position);
            }
        });

        //design some action in mode change call back.
        mSailsMapView.setOnModeChangedListener(new SAILSMapView.OnModeChangedListener() {
            @Override
            public void onModeChanged(int mode) {
                if (((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) && ((mode & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)) {
                    lockcenter.setImageDrawable(ContextCompat.getDrawable(SailsMapActivity.this, R.drawable.center3));
                } else if ((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) {
                    lockcenter.setImageDrawable(ContextCompat.getDrawable(SailsMapActivity.this, R.drawable.center2));
                } else {
                    lockcenter.setImageDrawable(ContextCompat.getDrawable(SailsMapActivity.this, R.drawable.center1));
                }
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSails.getFloorDescList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
/*        floorList.setAdapter(adapter); //TODO Code to handle floor changing
        floorList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mSailsMapView.getCurrentBrowseFloorName().equals(mSails.getFloorNameList().get(position)))
                    mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    void routingInitial() {
        mSailsMapView.getRoutingManager().setStartMakerDrawable(Marker.boundCenter(getResources().getDrawable(R.drawable.start_point)));
        mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.map_destination)));
        mSailsMapView.getRoutingManager().setOnRoutingUpdateListener(new PathRoutingManager.OnRoutingUpdateListener() {
            @Override
            public void onArrived(LocationRegion targetRegion) {
                Toast.makeText(getApplication(), "Chegou ao destino", Toast.LENGTH_SHORT).show();
                endRouteButton.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onRouteSuccess() {
                List<GeoPoint> gplist = mSailsMapView.getRoutingManager().getCurrentFloorRoutingPathNodes();
                mSailsMapView.autoSetMapZoomAndView(gplist);
            }

            @Override
            public void onRouteFail() {
                Toast.makeText(getApplication(), "Route Fail.", Toast.LENGTH_SHORT).show();
                mSailsMapView.getRoutingManager().disableHandler();
            }

            @Override
            public void onPathDrawFinish() {
            }

            @Override
            public void onTotalDistanceRefresh(int distance) {
            }

            @Override
            public void onReachNearestTransferDistanceRefresh(int distance, int nodeType) {
                switch (nodeType) {
                    case PathRoutingManager.SwitchFloorInfo.ELEVATOR:
                       // currentFloorDistanceView.setText("To Nearest Elevator Distance: " + Integer.toString(distance) + " (m)");
                        break;
                    case PathRoutingManager.SwitchFloorInfo.ESCALATOR:
                      //  currentFloorDistanceView.setText("To Nearest Escalator Distance: " + Integer.toString(distance) + " (m)");
                        break;
                    case PathRoutingManager.SwitchFloorInfo.STAIR:
                      //  currentFloorDistanceView.setText("To Nearest Stair Distance: " + Integer.toString(distance) + " (m)");
                        break;
                    case PathRoutingManager.SwitchFloorInfo.DESTINATION:
                      //  currentFloorDistanceView.setText("To Destination Distance: " + Integer.toString(distance) + " (m)");
                        break;
                }
            }

            @Override
            public void onSwitchFloorInfoRefresh(List<PathRoutingManager.SwitchFloorInfo> infoList, int nearestIndex) {
                }
            });
    }

    View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == zoomin) {
                //set map zoomin function.
                mSailsMapView.zoomIn();
            } else if (v == zoomout) {
                //set map zoomout function.
                mSailsMapView.zoomOut();
            } else if (v == lockcenter) {
                Boolean x = mSails.isInThisBuilding();
                if (!mSails.isLocationFix() || !mSails.isLocationEngineStarted()) {
                    Toast t = Toast.makeText(getBaseContext(), "Localização nao encontrada!.", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                if (!mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor()) {
                    //set the map that currently location engine recognize.
                    mSailsMapView.loadCurrentLocationFloorMap();

                    Toast t = Toast.makeText(getBaseContext(), "Vá para o mesmo andar que o mapa.", Toast.LENGTH_SHORT);
                    t.show();
                    return;
                }
                //set map mode.
                //FOLLOW_PHONE_HEADING: the map follows the phone's heading.
                //LOCATION_CENTER_LOCK: the map locks the current location in the center of map.
                //ALWAYS_LOCK_MAP: the map will keep the mode even user moves the map.
                if (mSailsMapView.isCenterLock()) {
                    if ((mSailsMapView.getMode() & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)
                        //if map control mode is follow phone heading, then set mode to location center lock when button click.
                        mSailsMapView.setMode(mSailsMapView.getMode() & ~SAILSMapView.FOLLOW_PHONE_HEADING);
                    else
                        //if map control mode is location center lock, then set mode to follow phone heading when button click.
                        mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.FOLLOW_PHONE_HEADING);
                } else {
                    //if map control mode is none, then set mode to loction center lock when button click.
                    mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.LOCATION_CENTER_LOCK);
                }
            }
        }
    };

    @Override
    public void onBlockNameReceivedListener(String result) {
        LocationRegion target = new LocationRegion();
        mSails.startLocatingEngine();
        Boolean foundRoute = false;
        if (mSails.isLocationEngineStarted()) {
            if(result.equalsIgnoreCase("nulo")){
                Toast.makeText(SailsMapActivity.this,"Essa loja nao existe!",Toast.LENGTH_LONG).show();
            }else{
                String floor = mSails.getFloor();
                if("".equals(floor)){
                    floor = "1";
                }
                List<LocationRegion> all_lr = mSails.getLocationRegionList(floor);
                if(all_lr != null && all_lr.size() > 0){
                    for(LocationRegion lr : all_lr){
                        if (lr.label.equalsIgnoreCase(result)){
                            target = lr;
                            foundRoute = true;
                        }
                    }
                }else{
                    Toast t = Toast.makeText(getBaseContext(), "Falha ao carregar caminho, por favor tente novamente", Toast.LENGTH_LONG);
                    t.show();
                }
                if (foundRoute){
                    //set routing start point to current user location.
                    mSailsMapView.getRoutingManager().setStartRegion(PathRoutingManager.MY_LOCATION);
                    mSailsMapView.getRoutingManager().setTargetRegion(target);
                    //set routing end point marker icon.
                    mSailsMapView.getRoutingManager().setTargetMakerDrawable(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.destination)));

                    //set routing path's color.
                    mSailsMapView.getRoutingManager().getPathPaint().setColor(0xFF35b3e5);
                    endRouteButton.setVisibility(View.VISIBLE);
                    mSailsMapView.getRoutingManager().enableHandler();
                    endRouteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            endRouteButton.setVisibility(View.INVISIBLE);
                            //distanceView.setVisibility(View.INVISIBLE);
                            //currentFloorDistanceView.setVisibility(View.INVISIBLE);
                            //msgView.setVisibility(View.INVISIBLE);
                            //end route.
                            mSailsMapView.getRoutingManager().disableHandler();
                            finish();
                        }
                    });
                }else{
                    Toast t = Toast.makeText(getBaseContext(), "Falha ao carregar caminho, por favor tente novamente", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
            if (!mSails.isLocationEngineStarted() && finished) {
                mSails.startLocatingEngine();
                Double x = mSails.getLatitude();
                Double y = mSails.getLongitude();
                String j = mSails.getBuildingName();
                String k = mSails.getFloor();
                endRouteButton.setVisibility(View.INVISIBLE);
            }
        finished = false;
        }
    }