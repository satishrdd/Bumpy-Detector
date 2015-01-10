package com.funapps.naveen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	LocationService locationService;
	Button gps, nwt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gps = (Button) findViewById(R.id.btgps);
		nwt = (Button) findViewById(R.id.btnwt);
		locationService = new LocationService(MainActivity.this);

		gps.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Location gpsLocation = locationService
						.getLocation(LocationManager.GPS_PROVIDER);
				if(gpsLocation!=null){
					double latitude = gpsLocation.getLatitude();
					double longitude = gpsLocation.getLongitude();
					Toast.makeText(
							getApplicationContext(),
							"Mobile Location (GPS): \nLatitude: " + latitude
									+ "\nLongitude: " + longitude,
							Toast.LENGTH_LONG).show();
				}
				else{
					showSettingsAlert("GPS");
				}
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
