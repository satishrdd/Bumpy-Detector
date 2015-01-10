package com.funapps.naveen;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener {

	protected LocationManager locationManager;
	Location location;
	private static final long MIN_DISTANCE_FOR_UPDATE = 10;
	private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

	public LocationService(Context c) {
		// TODO Auto-generated constructor stub
		locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
	}

	public Location getLocation(String provider) {
		if (locationManager.isProviderEnabled(provider)) {
			locationManager.requestLocationUpdates(provider,
					MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
			if(locationManager!=null){
				location = locationManager.getLastKnownLocation(provider);
				return location;
			}
		}

		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
