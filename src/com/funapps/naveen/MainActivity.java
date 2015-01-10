package com.funapps.naveen;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener {
	TextView tvLocation;
	LocationService locationService;
	GoogleMap googleMap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isGooglePlayServicesAvailable()) {
			finish();
		}
		setContentView(R.layout.activity_main);
		tvLocation = (TextView) findViewById(R.id.tvLocation);
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap);
		googleMap = supportMapFragment.getMap();
		googleMap.setMyLocationEnabled(true);
		
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);
		
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if(location!=null){
			onLocationChanged(location);
		}
		
		locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
		
		
//		gps = (Button) findViewById(R.id.btgps);
//		nwt = (Button) findViewById(R.id.btnwt);
		
//		gps.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Location location = fusedLocationService.getLocation();
//				String locationResult = "";
//				if (null != location) {
//					Log.i("TAG", location.toString());
//					double latitude = location.getLatitude();
//					double longitude = location.getLongitude();
//					float accuracy = location.getAccuracy();
//					double elapsedTimeSecs = (double) location
//							.getElapsedRealtimeNanos() / 1000000000.0;
//					String provider = location.getProvider();
//					double altitude = location.getAltitude();
//					locationResult = "Latitude: " + latitude + "\n"
//							+ "Longitude: " + longitude + "\n" + "Altitude: "
//							+ altitude + "\n" + "Accuracy: " + accuracy + "\n"
//							+ "Elapsed Time: " + elapsedTimeSecs + " secs"
//							+ "\n" + "Provider: " + provider + "\n";
//				} else {
//					locationResult = "Location Not Available!";
//				}
//				tvLocation.setText(locationResult);
//			}
//		});

//		nwt.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Location nwtLocation = locationService
//						.getLocation(LocationManager.NETWORK_PROVIDER);
//				if (nwtLocation != null) {
//					double latitude = nwtLocation.getLatitude();
//					double longitude = nwtLocation.getLongitude();
//					Toast.makeText(
//							getApplicationContext(),
//							"Mobile Location (NW): \nLatitude: " + latitude
//									+ "\nLongitude: " + longitude,
//							Toast.LENGTH_LONG).show();
//				} else {
//					showSettingsAlert("NETWORK");
//				}
//			}
//		});
	}

	private boolean isGooglePlayServicesAvailable() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (ConnectionResult.SUCCESS == status) {
			return true;
		} else {
			GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	public void showSettingsAlert(String provider) {
//		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//				MainActivity.this);
//
//		alertDialog.setTitle(provider + " SETTINGS");
//
//		alertDialog.setMessage(provider
//				+ " is not enabled! Want to go to settings menu?");
//
//		alertDialog.setPositiveButton("Settings",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						Intent intent = new Intent(
//								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//						MainActivity.this.startActivity(intent);
//					}
//				});
//
//		alertDialog.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//					}
//				});
//
//		alertDialog.show();
//	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		
		LatLng latlong = new LatLng(latitude, longitude);
		
		googleMap.addMarker(new MarkerOptions().position(latlong));
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlong));
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		tvLocation.setText("Latitude: "+latitude+"\n"+"Longitude: "+longitude);
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

}
