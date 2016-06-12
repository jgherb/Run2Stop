package de.jhs.run2stop;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.Type;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import de.jhs.run2stop.model.Calculator;
import de.jhs.run2stop.model.Departure;
import de.jhs.run2stop.model.Element;
import de.jhs.run2stop.model.RootElement;

public class Activity2 extends AppCompatActivity implements LocationListener {

    //region varDef
    MapView mMapView;
    MapController mMapController;
    RoadManager roadManager;
    GeoPoint currenLocation;
    GeoPoint destinationLocation;
    LocationManager locationManager;
    Departure busDeparture;
    boolean gotStation = false;
    Marker lastMarker = null;
    TextView distGoal, timeLeft,timeToBus;
    //endregion

    /**
     * Init method for instanciate all variables
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //region initVars
        double dLat = getIntent().getDoubleExtra("xtra_destination_lat", 0);
        double dLong = getIntent().getDoubleExtra("xtra_destination_long",0);
        busDeparture = (Departure)getIntent().getSerializableExtra("xtra_bus_depart") ;
        distGoal = (TextView) findViewById(R.id.tV_distance_goal);
        timeLeft = (TextView) findViewById(R.id.tV_time_left);
        timeToBus = (TextView) findViewById(R.id.tV_time_left_to_bus);
        destinationLocation = new GeoPoint(dLat, dLong);
        //endregion

        //region GPS Location init
        if (!MainActivity.demomode) {
            // get GPS manager -> GPS longitude, latitude in onLocationChanged
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;


            try {
                locationManager.requestLocationUpdates(locationProvider, 0, 0, Activity2.this);
            }
            catch(final SecurityException ex) {
                Toast.makeText(this, "Leider können wir ohne GPS keine Route berechnen", Toast.LENGTH_SHORT).show();
            }
        }

        else {
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Location location  = new Location("");
                    location.setLatitude(48.4224);//your coords of course
                    location.setLongitude(9.9579);

                    onLocationChanged(location);
                }
            },1000);
        }
        //endregion

        //Map view init
        // MapView integration
        mMapView = (MapView) findViewById(R.id.mapview_calculated);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(17);
        //endregion

        //road manager init
        roadManager = new MapQuestRoadManager("DIGIF2BiDrtD1Xbi28SXrkd9Ap2h73Vn");
        roadManager.addRequestOption("routeType=pedestrian");



    }


    @Override
    public void onLocationChanged(Location location) {

        //get location
        double latitude = (double) (location.getLatitude());
        double longitude = (double) (location.getLongitude());

        // fuer Verbindungen zwischen zwei Verbindungen

        final GeoPoint startPoint = new GeoPoint(latitude, longitude);

        currenLocation = startPoint;

        //region TimeDifference calculator
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        java.util.Date date;
        String timeStr = "NULL";
        try {
             date = (java.util.Date)formatter.parse(busDeparture.getTimetable());
            Calendar cal = Calendar.getInstance();
            long diffhours = Math.abs(date.getHours() - cal.getTime().getHours());
           // int diffhours = (int) (diff / (60 * 60 * 1000));
            int min =0;
            if(diffhours>=1)
            {
                min=60;
            }
           int diffmin = date.getMinutes()+min - cal.getTime().getMinutes();//(int) (diff / (60 * 1000));

            timeStr = String.valueOf(diffmin) + " min";

        }
        catch(Exception exeption)
        {
            exeption.printStackTrace();
        }

        timeToBus.setText(timeStr);
        //endregion



           mMapController.setCenter(startPoint);
        mMapView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMapController.setCenter(startPoint);
                return true;
            }
        });


        // Marker integration
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if(lastMarker!=null)
        {
            mMapView.getOverlays().remove(lastMarker);
        }
        lastMarker = startMarker;
        mMapView.getOverlays().add(startMarker);


        if(destinationLocation!=null) calcRoute();
        // aktualisiert Map
        mMapView.invalidate();

        // set marker and color
        startMarker.setTitle("Aktuelle Position");

        // aktualisiert Map
        mMapView.invalidate();
    }

    //region notUsedMethods
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
  //endregion

    /**
     * Calculates a route from your start location to the next bus stop
     */
    private  void calcRoute()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();//Strict mode for ignore NetworkOnMainExceptions (!for anytime, have to put in threading)
        StrictMode.setThreadPolicy(policy);

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>(); //Build up list of geopoints
        waypoints.add(currenLocation);
        GeoPoint endPoint = new GeoPoint(destinationLocation);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        double d = road.mDuration; // get duration
        double x = road.mLength; // get length
        String st_timeLeft = String.format("%s min", String.valueOf(myRound(d / 60,3)));//format string
        String st_dist = String.valueOf(myRound(x * 1000,2)) + " m";

        timeLeft.setText(st_timeLeft);
        distGoal.setText(st_dist);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Activity2.this);// draw route
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();


    }

    /**
     * Round Method for rounding doubles by number of digit
     * @param wert Value to round
     * @param stellen Numer of digit
     * @return rounded value
     */
    static double myRound(double wert, int stellen) {
        return  Math.round(wert * Math.pow(10, stellen)) / Math.pow(10, stellen);
    }

}
