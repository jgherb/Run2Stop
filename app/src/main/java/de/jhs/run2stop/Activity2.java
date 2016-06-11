package de.jhs.run2stop;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.jhs.run2stop.model.Calculator;
import de.jhs.run2stop.model.Element;
import de.jhs.run2stop.model.RootElement;

public class Activity2 extends AppCompatActivity implements LocationListener {

    MapView mMapView;
    MapController mMapController;

    RoadManager roadManager;
    GeoPoint currenLocation;
    GeoPoint destinationLocation;

    LocationManager locationManager;

    TextView distGoal, timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        double dLat = getIntent().getDoubleExtra("xtra_destination_lat", 0);
        double dLong = getIntent().getDoubleExtra("xtra_destination_long",0);

        distGoal = (TextView) findViewById(R.id.tV_distance_goal);
        timeLeft = (TextView) findViewById(R.id.tV_time_left);


        destinationLocation = new GeoPoint(dLat, dLong);

        // get GPS manager -> GPS longitude, latitude in onLocationChanged
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;


        try {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, Activity2.this);
        }
        catch(final SecurityException ex) {
            Toast.makeText(this, "Leider können wir ohne GPS keine Route berechnen", Toast.LENGTH_SHORT).show();
        }


        // MapView integration
        mMapView = (MapView) findViewById(R.id.mapview_calculated);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(13);

        roadManager = new MapQuestRoadManager("DIGIF2BiDrtD1Xbi28SXrkd9Ap2h73Vn");
        roadManager.addRequestOption("routeType=pedestrian");



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

    boolean gotStation = false;
    Marker lastMarker = null;

    @Override
    public void onLocationChanged(Location location) {


        double latitude = (double) (location.getLatitude());
        double longitude = (double) (location.getLongitude());

        // fuer Verbindungen zwischen zwei Verbindungen
        // ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        final GeoPoint startPoint = new GeoPoint(latitude, longitude);
        currenLocation = startPoint;
        //waypoints.add(startPoint);
        //GeoPoint endPoint = new GeoPoint(48.4, -1.9);
        //waypoints.add(endPoint);


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

        //Drawable d = getResources().getDrawable(R.drawable.ic_current_location);
        if(destinationLocation!=null) calcRoute();
        // aktualisiert Map
        mMapView.invalidate();

        // set marker and color
        //d.setColorFilter(getResources().getColor(R.color.currentLoc), PorterDuff.Mode.MULTIPLY);
        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_current_location));
        startMarker.setTitle("Aktuelle Position");


        StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(pol);

        //RoadManager roadManager = new MapQuestRoadManager("DIGIF2BiDrtD1Xbi28SXrkd9Ap2h73Vn");
        //roadManager.addRequestOption("routeType=pedestrian");
        //Road road = roadManager.getRoad(waypoints);
        //Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);
        //mMapView.getOverlays().add(roadOverlay);

        // aktualisiert Map
        mMapView.invalidate();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private  void calcRoute()
    {


        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(currenLocation);
        GeoPoint endPoint = new GeoPoint(destinationLocation);
        waypoints.add(endPoint);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Road road = roadManager.getRoad(waypoints);
        double d = road.mDuration;
        double x = road.mLength;
        String st_timeLeft = String.format("%s min  %s m", String.valueOf(d / 60));
        String st_dist = String.valueOf(x * 1000);

        timeLeft.setText(st_timeLeft);
        distGoal.setText(st_dist);
                //Toast.makeText(Activity2.this, String.format("%s min  %s m", String.valueOf(d / 60), String.valueOf(x * 1000)), Toast.LENGTH_SHORT).show();
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Activity2.this);
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();


    }

}
