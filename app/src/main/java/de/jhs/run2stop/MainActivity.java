package de.jhs.run2stop;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import de.jhs.run2stop.model.Calculator;
import de.jhs.run2stop.model.Element;
import de.jhs.run2stop.model.RootElement;

public class MainActivity extends AppCompatActivity implements LocationListener  {

    private MapView         mMapView;
    private MapController   mMapController;

    LocationManager locationManager;

    double lat;
    double lng;
    LocationManager lm;
    LocationListener ll;

    double latitude, longitude;

    ArrayList<OverlayItem> anotherOverlayItemArray;
    String[] sources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DateFormat df = new SimpleDateFormat("HH:mm");
        String date = "Aktuelle Uhrzeit: "+ df.format(Calendar.getInstance().getTime());
        TextView currentTime = (TextView) findViewById(R.id.tV_current_time);
        currentTime.setText(date);



        // get GPS manager -> GPS longitude, latitude in onLocationChanged
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;


        try {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, MainActivity.this);
        }
        catch(final SecurityException ex) {
            Toast.makeText(this, "Leider können wir ohne GPS keine Route berechnen", Toast.LENGTH_SHORT).show();
        }




        // MapView integration
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(13);

        // more at onLocationChanged




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent toCalulator = new Intent(MainActivity.this, Activity2.class);
                startActivity(toCalulator);
            }
        });


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
boolean gotStation = false;
    @Override
    public void onLocationChanged(Location location) {


        double latitude = (double) (location.getLatitude());
        double longitude = (double) (location.getLongitude());

        // fuer Verbindungen zwischen zwei Verbindungen
        // ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        //waypoints.add(startPoint);
        //GeoPoint endPoint = new GeoPoint(48.4, -1.9);
        //waypoints.add(endPoint);
        if(!gotStation)
        {
            getStation(startPoint);
            gotStation=true;
            mMapView.getOverlays().clear();
        }


        mMapController.setCenter(startPoint);

        // Marker integration
        Marker startMarker = new Marker(mMapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mMapView.getOverlays().add(startMarker);

        //Drawable d = getResources().getDrawable(R.drawable.ic_current_location);

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

        TextView currentLoc = (TextView) findViewById(R.id.gps_current_loc);
        String st_currentLoc = String.valueOf(latitude)+ " , " + String.valueOf(longitude);
        currentLoc.setText(st_currentLoc);



        mMapController.setCenter(startPoint);
    }

    private  void getStation(final  GeoPoint endPoint)
    {

        Ion.getDefault(this).configure().setLogging("ION", Log.DEBUG);
        Ion.with(this).load("https://overpass-api.de/api/interpreter").setBodyParameter("data","\n" +
                "[out:json][timeout:25];\n" +
                "(\n" +
                "  // query part for: “highway=bus_stop”\n" +
                "  node[\"highway\"=\"bus_stop\"]("+ Calculator.getBoundingBoxString(endPoint,0.857)+");\n" +
                "  \n" +
                ");\n" +
                "// print results\n" +
                "out body;\n" +
                ">;\n" +
                "out skel qt;").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if(e!= null)
                {
                    e.printStackTrace();
                    return;
                }
                Type collectionType = new TypeToken<RootElement>() {
                }.getType();

                RootElement userApiFrame = (RootElement)new Gson().fromJson(result,collectionType);
                List<Element> elements = userApiFrame.getElements();

                List<Element> filteredElements = new ArrayList<Element>();

                for(Element el: elements)
                {
                    if(!containsName(el.getTags().getName(),filteredElements))
                    {
                        filteredElements.add(el);
                    }
                }

                TreeMap<Double,Element> elemDistList = new TreeMap<Double, Element>();
                for(Element element : filteredElements)
                {
                    double dist = Calculator.distFromCoords(endPoint,element.getGeoPoint());
                    elemDistList.put(dist,element);
                }

              Element[] elementss = elemDistList.values().toArray(new Element[elemDistList.size()]);

                Element element = elementss[0];
             String name =    element.getTags().getName();

                EditText editText = (EditText)findViewById(R.id.fromStation);
                editText.setText(name);

                RoadManager roadManager = new MapQuestRoadManager("DIGIF2BiDrtD1Xbi28SXrkd9Ap2h73Vn");
                roadManager.addRequestOption("routeType=pedestrian");
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(endPoint);
                GeoPoint endPoint = new GeoPoint(element.getGeoPoint());
                waypoints.add(endPoint);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Road road = roadManager.getRoad(waypoints);
                double d = road.mDuration;
                double x = road.mLength;
                Toast.makeText(MainActivity.this,String.format("%s min  %s km", String.valueOf(d/60),String.valueOf(x)), Toast.LENGTH_SHORT).show();
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road, MainActivity.this);
                mMapView.getOverlays().add(roadOverlay);
                mMapView.invalidate();




            }
        });
    }

    private  boolean containsName(String name,List<Element> list)
    {
        for(Element element : list)
        {
            if(element.getTags().getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return  false;
    }

    @Override
    public void onProviderDisabled(String provider) {


    }

    @Override
    public void onProviderEnabled(String provider) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }




}
