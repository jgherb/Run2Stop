package de.jhs.run2stop.model;

import org.apache.commons.lang3.ObjectUtils;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

/**
 * Created by Pascal on 11.06.2016.
 */
public class Calculator {


    // Semi-axes of WGS-84 geoidal reference
    private final static  double WGS84_a = 6378137.0; // Major semiaxis [m]
    private final static double WGS84_b = 6356752.3; // Minor semiaxis [m]

    // 'halfSideInKm' is the half length of the bounding box you want in kilometers.
    public static ArrayList<GeoPoint> GetBoundingBox(GeoPoint point, double halfSideInKm)
    {
        ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
        // Bounding box surrounding the point at given coordinates,
        // assuming local approximation of Earth surface as a sphere
        // of radius given by WGS84
        double lat = Deg2rad(point.getLatitude());
        double lon = Deg2rad(point.getLongitude());
        double halfSide = 1000 * halfSideInKm;

        // Radius of Earth at given latitude
        double radius = WGS84EarthRadius(lat);
        // Radius of the parallel at given latitude
        double pradius = radius * Math.cos(lat);

        double latMin = lat - halfSide / radius;
        double latMax = lat + halfSide / radius;
        double lonMin = lon - halfSide / pradius;
        double lonMax = lon + halfSide / pradius;
            GeoPoint first = new GeoPoint(Rad2deg(latMin),Rad2deg(lonMin));
            GeoPoint second = new GeoPoint(Rad2deg(latMax),Rad2deg(lonMax));
        list.add(first);
        list.add(second);
        return  list;
    }

    // degrees to radians
    private static double Deg2rad(double degrees)
    {
        return Math.PI * degrees / 180.0;
    }

    // radians to degrees
    private static double Rad2deg(double radians)
    {
        return 180.0 * radians / Math.PI;
    }

    // Earth radius at a given latitude, according to the WGS-84 ellipsoid [m]
    private static double WGS84EarthRadius(double lat)
    {
        // http://en.wikipedia.org/wiki/Earth_radius
        double An = WGS84_a * WGS84_a * Math.cos(lat);
        double Bn = WGS84_b * WGS84_b * Math.sin(lat);
        double Ad = WGS84_a * Math.cos(lat);
        double Bd = WGS84_b * Math.sin(lat);
        return Math.sqrt((An * An + Bn * Bn) / (Ad * Ad + Bd * Bd));
    }

    public static  String getBoundingBoxString(GeoPoint mapPoint,double distance)
    {
       ArrayList<GeoPoint>  x= GetBoundingBox(mapPoint,distance);
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<x.size();i++)
        {
            GeoPoint point = x.get(i);
            String separator =",";
            if(i==0) separator="";
            builder.append(separator + String.format("%s,%s",String.valueOf(point.getLatitude()),String.valueOf(point.getLongitude())));
        }
        return builder.toString();
    }

    public static void test()
    {
       GeoPoint mapPoint = new GeoPoint(48.41985948302625,9.942197799682617);
      String s = getBoundingBoxString(mapPoint,1.99);
        String string = s.toUpperCase();
    }
}
