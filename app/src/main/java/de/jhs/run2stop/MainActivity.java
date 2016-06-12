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
import android.text.InputFilter;
import android.text.Spanned;
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
import java.util.logging.Handler;

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

import de.jhs.run2stop.model.BusData;
import de.jhs.run2stop.model.Calculator;
import de.jhs.run2stop.model.Departure;
import de.jhs.run2stop.model.Element;
import de.jhs.run2stop.model.RootElement;

public class MainActivity extends AppCompatActivity implements LocationListener  {

    //region VarDef
    private MapView         mMapView;
    private MapController   mMapController;
    RoadManager roadManager;
    GeoPoint currenLocation;
    GeoPoint destinationLocation;
    LocationManager locationManager;
    Departure busDepart;
    double lat;
    double lng;
    LocationManager lm;
    LocationListener ll;
    double latitude, longitude;
    EditText eT_radian;
    double in_radian;
    boolean gotStation = false;
    Marker lastMarker = null;
    // Activitate Demo Mode
    static boolean demomode = true;
   //endregion

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

        //region  Radian Setter
        eT_radian = (EditText)findViewById(R.id.radian);
        String st_radian = eT_radian.getText().toString();
        if(!st_radian.equals("")) {
            in_radian = (double)Integer.parseInt(st_radian) / 1000;
            // Weitergabe an Bus getStation
        }
        else
        {
            in_radian = 0.750d;
        }
        //endregion

        //region GPS Location
        if (!demomode) {
            // get GPS manager -> GPS longitude, latitude in onLocationChanged
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;


            try {
                locationManager.requestLocationUpdates(locationProvider, 0, 0, MainActivity.this);
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
            },4500);
        }
        //endregion

        //region MapInit
        // MapView integration
        mMapView = (MapView) findViewById(R.id.mapview);
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapController = (MapController) mMapView.getController();
        mMapController.setZoom(17);

        roadManager = new MapQuestRoadManager("DIGIF2BiDrtD1Xbi28SXrkd9Ap2h73Vn");
        roadManager.addRequestOption("routeType=pedestrian");
       //endregion

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent toCalulator = new Intent(MainActivity.this, Activity2.class);
                toCalulator.putExtra("xtra_destination_lat", destinationLocation.getLatitude());
                toCalulator.putExtra("xtra_destination_long", destinationLocation.getLongitude());
                toCalulator.putExtra("xtra_bus_depart",busDepart);
                startActivity(toCalulator);
            }
        });


    }



  //region obsolete Methods
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



    @Override
    public void onProviderDisabled(String provider) {


    }

    @Override
    public void onProviderEnabled(String provider) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }



    //endregion


    @Override
    public void onLocationChanged(Location location) {


        double latitude = (double) (location.getLatitude());
        double longitude = (double) (location.getLongitude());

        // fuer Verbindungen zwischen zwei Verbindungen
        final GeoPoint startPoint = new GeoPoint(latitude, longitude);
        currenLocation = startPoint;


        if(!gotStation)
        {
            getStation(startPoint);
            gotStation=true;
            mMapView.getOverlays().clear();
        }

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


        StrictMode.ThreadPolicy pol = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(pol);

        // aktualisiert Map
        mMapView.invalidate();

        TextView currentLoc = (TextView) findViewById(R.id.gps_current_loc);
        String st_currentLoc = String.valueOf(latitude)+ " , " + String.valueOf(longitude);
        currentLoc.setText(st_currentLoc);


        mMapController.setCenter(startPoint);//Center map
    }

    /**
     * Calulates a route from current destination to the next bus stop
     */
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
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, MainActivity.this);
        mMapView.getOverlays().add(roadOverlay);
        mMapView.invalidate();


    }

    /**
     * Get the next station to the geoLocation
     * @param endPoint endPoint to search for busStations
     */
    private  void getStation(final  GeoPoint endPoint)
    {

        Ion.getDefault(this).configure().setLogging("ION", Log.DEBUG);
        Ion.with(this).load("https://overpass-api.de/api/interpreter").setBodyParameter("data","\n" +
                "[out:json][timeout:25];\n" +
                "(\n" +
                "  // query part for: “highway=bus_stop”\n" +
                "  node[\"highway\"=\"bus_stop\"]("+ Calculator.getBoundingBoxString(endPoint,in_radian)+");\n" +
                "  \n" +
                ");\n" +
                "// print results\n" +
                "out body;\n" +
                ">;\n" +
                "out skel qt;").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (e != null) {
                    e.printStackTrace();
                    return;
                }
                Type collectionType = new TypeToken<RootElement>() {
                }.getType();

                RootElement userApiFrame = (RootElement) new Gson().fromJson(result, collectionType);
                List<Element> elements = userApiFrame.getElements();

                List<Element> filteredElements = new ArrayList<Element>();

                for (Element el : elements) {
                    if (!containsName(el.getTags().getName(), filteredElements)) {
                        filteredElements.add(el);
                    }
                }

                TreeMap<Double, Element> elemDistList = new TreeMap<Double, Element>();
                for (Element element : filteredElements) {
                    double dist = Calculator.distFromCoords(endPoint, element.getGeoPoint());
                    elemDistList.put(dist, element);
                }

                Element[] elementss = elemDistList.values().toArray(new Element[elemDistList.size()]);

                Element element = elementss[0];
                String name = element.getTags().getName();

                EditText editText = (EditText) findViewById(R.id.fromStation);
                editText.setText(name);

                getBusData(getBusId(name));

                destinationLocation = element.getGeoPoint();
               calcRoute();




            }
        });
    }

    /**
     * Checks if the parameter list contanis name
     * @param name name to check
     * @param list list to search
     * @return list contains name?
     */
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

    /**
     * Returns hardcoded hash map for all Bus stations in Ulm
     * @return
     */
    public HashMap<String,Integer> getBusList()
    {
        HashMap<String,Integer> hashMap = new HashMap<>();

        hashMap.put("Ulm / Hauptbahnhof",1008);
        hashMap.put("Ulm / Theater",1010);
        hashMap.put("Ulm / Universität Süd",1240);
        hashMap.put("Ulm / Uni West",1246);
        hashMap.put("Ulm / Ehinger Tor",1350);
        hashMap.put("Ulm / ZOB",1000);
        hashMap.put("ZUP",1700);
        hashMap.put("Adenauerbrücke",1701);
        hashMap.put("Albecker Steige",1061);
        hashMap.put("Albstraße",1162);
        hashMap.put("Alfred-Delp-Weg",1083);
        hashMap.put("Allgäuer Straße",1767);
        hashMap.put("Alte Siedlung",1602);
        hashMap.put("Am Hochsträß",1392);
        hashMap.put("Am Roten Berg",1339);
        hashMap.put("Am Sandhaken",1591);
        hashMap.put("Amtsgericht",1705);
        hashMap.put("Arena",1745);
        hashMap.put("Arnegg, Bildstöckle",2763);
        hashMap.put("Arnegg, Gasthof Blautal",2761);
        hashMap.put("Auf dem Hart",1173);
        hashMap.put("Auf der Gölde",1367);
        hashMap.put("Auf der Laue",1331);
        hashMap.put("Augsburger Tor",1713);
        hashMap.put("Bahnhof Söflingen",1220);
        hashMap.put("Barbaralinde",1395);
        hashMap.put("Beim Alten Fritz",1038);
        hashMap.put("Beim B'scheid",1366);
        hashMap.put("Beim Türmle",1280);
        hashMap.put("Benzstraße",1506);
        hashMap.put("Beringerbrücke",1222);
        hashMap.put("Blaustein, Am Blaugarten",2708);
        hashMap.put("Blaustein, Bahnhof/Rathaus",2701);
        hashMap.put("Blaustein, Galgenbergstraße",2702);
        hashMap.put("Blaustein, Hofstraße",2710);
        hashMap.put("Blaustein, Kalte Herberge",2704);
        hashMap.put("Blaustein, Lindenstraße",2720);
        hashMap.put("Blautal-Center",1354);
        hashMap.put("Bleicher Hag",1225);
        hashMap.put("Blücherstraße",1351);
        hashMap.put("Borsigstraße",1864);
        hashMap.put("Boschstraße",1507);
        hashMap.put("Botanischer Garten",1241);
        hashMap.put("Bradleystraße",1734);
        hashMap.put("Burgunderweg",1210);
        hashMap.put("Burlafingen, Bahnhof",1961);
        hashMap.put("Burlafingen, Dorfplatz",1962);
        hashMap.put("Burlafingen, Glöcklerstraße",1964);
        hashMap.put("Burlafingen, Schule",1968);
        hashMap.put("Clarissenstraße",1342);
        hashMap.put("Congress Centrum",1049);
        hashMap.put("Daimlerstraße",1502);
        hashMap.put("Danziger Straße",1772);
        hashMap.put("DEUTZ AG",1586);
        hashMap.put("Donau-Iller-Werkstätten",1167);
        hashMap.put("Donaubad",1702);
        hashMap.put("Donaucenter",1707);
        hashMap.put("Donauhalle",1054);
        hashMap.put("Donauklinik",1739);
        hashMap.put("Donaustadion",1052);
        hashMap.put("Donaustetten, Illerkirchberger Str.",1672);
        hashMap.put("Donaustetten, Wasserturm",1671);
        hashMap.put("Edisoncenter",1725);
        hashMap.put("Edith-Stein-Ring",1170);
        hashMap.put("Egertweg",1087);
        hashMap.put("Egginer Weg",1382);
        hashMap.put("Eggingen, Rathaus",1422);
        hashMap.put("Eggingen, Ringinger Straße",1421);
        hashMap.put("Ehinger Tor",1350);
        hashMap.put("Ehrensteiner Feld",1266);
        hashMap.put("Eichberg",1081);
        hashMap.put("Eichberg Nord",1082);
        hashMap.put("Eichenplatz",1079);
        hashMap.put("Einsingen, Ensostraße",1412);
        hashMap.put("Einsingen, Hirsch",1413);
        hashMap.put("Einsingen, Kirche",1414);
        hashMap.put("Eisenbahnstraße",1403);
        hashMap.put("Erbacher Straße",1404);
        hashMap.put("Erenlauh",1620);
        hashMap.put("Ermingen, Allewind",1431);
        hashMap.put("Ermingen, Panoramastraße",1432);
        hashMap.put("Ermingen, Waldstraße",1433);
        hashMap.put("Ernst-Abbe-Straße",1539);
        hashMap.put("Escheugraben",1732);
        hashMap.put("Eselsberg Hasenkopf",1200);
        hashMap.put("Eugen-Bolz-Straße",1080);
        hashMap.put("Fachoberschule",1741);
        hashMap.put("Fichtenstraße",1165);
        hashMap.put("Firma Meiller",1548);
        hashMap.put("Firma Nanz",1550);
        hashMap.put("Firma Rheinzink",1552);
        hashMap.put("Firma Seeberger",1594);
        hashMap.put("Firma UPS",1554);
        hashMap.put("Firma Wieland",1555);
        hashMap.put("Fischerhauser Weg",1621);
        hashMap.put("Fort Unterer Eselsberg",1201);
        hashMap.put("Franz-Wiedemeier-Straße",1346);
        hashMap.put("Franzenhauserweg",1161);
        hashMap.put("Frauensteige",1031);
        hashMap.put("Frauenstraße",1025);
        hashMap.put("Fünf-Bäume-Weg",1345);
        hashMap.put("Fünf-Bäume-Weg Mitte",1324);
        hashMap.put("Gänsturm",1006);
        hashMap.put("Gartenstraße",1723);
        hashMap.put("Gasthof Donautal",1528);
        hashMap.put("Gehrnstraße",1166);
        hashMap.put("Gewerbeschulen Königstraße",1383);
        hashMap.put("Gleißelstetten",1323);
        hashMap.put("Gögglingen, Hoher Berg",1652);
        hashMap.put("Gögglingen, Riedlenstraße",1653);
        hashMap.put("Gögglingen, Zollbrücke",1651);
        hashMap.put("Graf-Arco-Straße",1561);
        hashMap.put("Häberlinweg",1401);
        hashMap.put("Hafengasse",1009);
        hashMap.put("Hafnerweg",1768);
        hashMap.put("Hans-Lorenser-Straße",1540);
        hashMap.put("Harthausen, Kirche",1451);
        hashMap.put("Hasenweg",1765);
        hashMap.put("Haslacher Weg",1074);
        hashMap.put("Haßlerstraße",1359);
        hashMap.put("Hauptbahnhof",1008);
        hashMap.put("Haus der Begegnung",1005);
        hashMap.put("Heilmeyersteige",1255);
        hashMap.put("Herdbrücke",1015);
        hashMap.put("Herrlingen, Bahnhof",2728);
        hashMap.put("Heuweg",1503);
        hashMap.put("Hochschule Eselsberg",1248);
        hashMap.put("Hörverlsinger Weg",1160);
        hashMap.put("Illerbrücke",1704);
        hashMap.put("IVECO",1574);
        hashMap.put("Jägerstraße",1349);
        hashMap.put("Jakobsruhe",1703);
        hashMap.put("Junginger Straße",1103);
        hashMap.put("Justizgebäude",1011);
        hashMap.put("Kapelle",1605);
        hashMap.put("Karlstraße",1020);
        hashMap.put("Kasernstraße",1714);
        hashMap.put("Kastbrücke",1590);
        hashMap.put("Katholische kirche",1766);
        hashMap.put("Kelternweg",1251);
        hashMap.put("Kemptener Straße",1632);
        hashMap.put("Keplerstraße",1012);
        hashMap.put("Kienlesberg",1039);
        hashMap.put("Kliniken Michelsberg",1030);
        hashMap.put("Kliniken Wissenschaftsstadt",1245);
        hashMap.put("Königstraße",1311);
        hashMap.put("Kuhberg Schulzentrum",1390);
        hashMap.put("Lehrer Tal",1232);
        hashMap.put("Leonberger Weg",1322);
        hashMap.put("Lessingstraße",1871);
        hashMap.put("Lise-Meitner-Straße",1254);
        hashMap.put("Liststraße",1501);
        hashMap.put("Loherstraße",1102);
        hashMap.put("Ludwig-Beck-Straße",1078);
        hashMap.put("Lupferbrücke",1218);
        hashMap.put("Magirusstraße",1353);
        hashMap.put("Mähringen, Ulmer Steige",1131);
        hashMap.put("Mähringer Straße",1101);
        hashMap.put("Maienweg",1332);
        hashMap.put("Maienweg Nord",1325);
        hashMap.put("Manfred-Börner-Straße",1247);
        hashMap.put("Margarete-Steiff-Weg",1171);
        hashMap.put("Marlene-Dietrich-Straße",1735);
        hashMap.put("Maybachstraße",1504);
        hashMap.put("Mecklenburgweg",1072);
        hashMap.put("Meininger Allee",1729);
        hashMap.put("Memminger Straße",1724);
        hashMap.put("Multscherschule",1202);
        hashMap.put("Neue Hochschule",1743);
        hashMap.put("Neuer Friedhof",1044);
        hashMap.put("Neunkirchenweg",1296);
        hashMap.put("Neutorstraße",1097);
        hashMap.put("Oberer Kuhberg",1391);
        hashMap.put("Oberer Roter Berg",1329);
        hashMap.put("Oberer Wirt",1601);
        hashMap.put("Oberfeld",1771);
        hashMap.put("Offenhausen, Grundweg",1904);
        hashMap.put("Offenhausen, Ortsstraße",1903);
        hashMap.put("Offenhausen, Roseggerstraße",1906);
        hashMap.put("Örlinger Straße",1048);
        hashMap.put("Ostermahdweg",1608);
        hashMap.put("Ostplatz",1058);
        hashMap.put("Ostpreußenweg",1070);
        hashMap.put("Ottiliengasse",1301);
        hashMap.put("Pauluskirche",1004);
        hashMap.put("Petrusplatz",1708);
        hashMap.put("Pfuhl, Altes Rathaus",1936);
        hashMap.put("Pfuhl, Kirchstraße",1934);
        hashMap.put("Pfuhl, Leipheimer Straße",1933);
        hashMap.put("Pfuhl, Platzgasse",1937);
        hashMap.put("Pfuhl, Schulzentrum",1942);
        hashMap.put("Pfuhl, Stauffenbergstraße",1938);
        hashMap.put("Pfuhl, Trissionplatz",1939);
        hashMap.put("Pfuhl, Winterstraße",1940);
        hashMap.put("Pranger",1600);
        hashMap.put("Rathaus Jungingen",1163);
        hashMap.put("Rathaus Neu-Ulm",1710);
        hashMap.put("Rathaus Ulm",1002);
        hashMap.put("Rathausstraße",1400);
        hashMap.put("Ratiopharm",1549);
        hashMap.put("Reichenberger Straße",1769);
        hashMap.put("Reutlinger Straße",1635);
        hashMap.put("Riedstraße",1733);
        hashMap.put("Riedwiesenweg",1340);
        hashMap.put("Robert-Dick-Weg",1381);
        hashMap.put("Römerplatz",1360);
        hashMap.put("Roncallihaus",1358);
        hashMap.put("Rosengasse",1003);
        hashMap.put("Ruländerweg",1260);
        hashMap.put("Saarlandstraße",1380);
        hashMap.put("Safranberg",1060);
        hashMap.put("Saulgauer Straße",1633);
        hashMap.put("Schillerhöhe",1032);
        hashMap.put("Schongauer Weg",1321);
        hashMap.put("Schützenstraße",1706);
        hashMap.put("Schwarzenbergstraße",1164);
        hashMap.put("Science Park II",1249);
        hashMap.put("Sedanstraße",1299);
        hashMap.put("Siemensstraße",1532);
        hashMap.put("Söflingen",1300);
        hashMap.put("Söflinger Weinberge",1257);
        hashMap.put("Sonnenfeld",1258);
        hashMap.put("Sonnenstraße",1310);
        hashMap.put("Sportplatz TSG",1343);
        hashMap.put("St.-Gallener-Straße",1631);
        hashMap.put("St.-Jakob-Straße",1341);
        hashMap.put("Staudingerstraße",1244);
        hashMap.put("Staufenring",1051);
        hashMap.put("Steinbeisstraße",1500);
        hashMap.put("Steinerne Brücke",1001);
        hashMap.put("Steinhövelstraße",1059);
        hashMap.put("Steubenstraße",1731);
        hashMap.put("Sulzbachweg",1297);
        hashMap.put("Tannenplatz Zentrum",1634);
        hashMap.put("Theater",1010);
        hashMap.put("Theodor-Heuss-Platz",1352);
        hashMap.put("Thüringenweg",1073);
        hashMap.put("Torstraße",1333);
        hashMap.put("Traminerweg",1261);
        hashMap.put("Universität Süd",1240);
        hashMap.put("Universität West",1246);
        hashMap.put("Veltlinerweg",1265);
        hashMap.put("Virchowstraße",1256);
        hashMap.put("Voithstraße",1534);
        hashMap.put("Waldeck",1719);
        hashMap.put("Washingtonallee",1744);
        hashMap.put("Wiley Club",1736);
        hashMap.put("Wiley Süd",1727);
        hashMap.put("Wilhelmsburgkaserne",1046);
        hashMap.put("Willy-Brandt-Platz",1050);
        hashMap.put("Wohnpark Friedrichsau",1053);
        return  hashMap;
    }

    /**
     * Gets the bus id from the station name
     * @param name Station name
     * @return bus id of the station
     */
    public  int getBusId(String name)
    {

        return getBusList().get(name);
    }

    /**
     * Get the current bus data by the bus station id
     * @param busId bus (station) id
     */
    public  void getBusData(int busId)
    {
        Ion.with(this).load(String.format("http://h.fs-et.de/api.php?id=%s&limit=5",String.valueOf(busId))).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {

                if(e!= null)
                {
                    e.printStackTrace();
                    return;
                }
                Type collectionType = new TypeToken<BusData>() {
                }.getType();

                BusData userApiFrame = (BusData)new Gson().fromJson(result,collectionType);
                TextView textView = (TextView)findViewById(R.id.tV_bus_comes_at);
                Departure departure = userApiFrame.getDepartures().get(0);
                busDepart = departure;
                textView.setText(String.format("Bus (%s - %s) kommt um: %s",departure.getLine(),departure.getDirection(),departure.getTimetable()));

            }
        });
    }





}
