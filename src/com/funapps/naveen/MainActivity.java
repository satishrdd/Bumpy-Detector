package com.funapps.naveen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
	FusedLocationService fusedLocationService;
	Button gps,nwt;
	TextView tvLocation;
	LocationService locationService;
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isGooglePlayServicesAvailable()){
			finish();
		}
		fusedLocationService = new FusedLocationService(MainActivity.this);
		locationService = new LocationService(MainActivity.this);
		setContentView(R.layout.activity_main);
		gps = (Button) findViewById(R.id.btgps);
		nwt = (Button) findViewById(R.id.btnwt);
		tvLocation =(TextView) findViewById(R.id.tvLocation);
		gps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Location location = fusedLocationService
						.getLocation();
				String locationResult = "";
                if (null != location) {
                    Log.i("TAG", location.toString());
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    float accuracy = location.getAccuracy();
                    double elapsedTimeSecs = (double) location.getElapsedRealtimeNanos()
                            / 1000000000.0;
                    String provider = location.getProvider();
                    double altitude = location.getAltitude();
                    locationResult = "Latitude: " + latitude + "\n" +
                            "Longitude: " + longitude + "\n" +
                            "Altitude: " + altitude + "\n" +
                            "Accuracy: " + accuracy + "\n" +
                            "Elapsed Time: " + elapsedTimeSecs + " secs" + "\n" +
                            "Provider: " + provider + "\n";
                } else {
                    locationResult = "Location Not Available!";
                }
                tvLocation.setText(locationResult);
			}
		});
		
		nwt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Location nwtLocation = locationService.getLocation(LocationManager.NETWORK_PROVIDER);
				if(nwtLocation!=null){
					double latitude = nwtLocation.getLatitude();
					double longitude = nwtLocation.getLongitude();
					Toast.makeText(
							getApplicationContext(),
							"Mobile Location (NW): \nLatitude: " + latitude
									+ "\nLongitude: " + longitude,
							Toast.LENGTH_LONG).show();
				}
				else{
					showSettingsAlert("NETWORK");
				}
			}
		});
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

	public void showSettingsAlert(String provider) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);

		alertDialog.setTitle(provider + " SETTINGS");

		alertDialog.setMessage(provider
				+ " is not enabled! Want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						MainActivity.this.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
	}

}
