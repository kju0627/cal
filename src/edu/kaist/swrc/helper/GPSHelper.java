/**
 * 
 */
package edu.kaist.swrc.helper;

import android.location.Location;

import com.google.android.maps.GeoPoint;

/**
 * <p>Title: GPSHelper.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: KAIST., Ltd</p>
 * 
 * @author <a href="mailto:his.barnabas@kaist.ac.kr">Kim, Kyoungryol</a>
 * @version v 1.0 2011. 4. 29.
 */

public class GPSHelper {
	/**
	 * http://snippets.dzone.com/posts/show/10687
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @param unit	"M":miles, "K":kilometers, "N":Nautical Miles
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) 
	{
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts decimal degrees to radians             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::  This function converts radians to decimal degrees             :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
	public static GeoPoint getGeoPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6)));
	}
	public static GeoPoint getGeoPoint(Location location){
		if(location == null)
			return null;
		Double lat = location.getLatitude() * 1E6;
		Double lng = location.getLongitude() * 1E6;
		return new GeoPoint(lat.intValue(), lng.intValue());
	}
	

}
