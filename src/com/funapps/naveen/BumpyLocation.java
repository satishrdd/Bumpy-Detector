package com.funapps.naveen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class BumpyLocation extends FragmentActivity implements
		LocationListener, SensorEventListener {
	TextView x_acc, y_acc, z_acc;
	Button upload;
	LocationManager locationManager;
	Location location;
	Time time;
	LatLng latlong;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	FileOperations fileOperations;
	String filename, bestProvider = "LocationManager.GPS_PROVIDER",
			defaultfile = "files";
	Criteria criteria;
	ConnectivityManager connectivityManager;
	NetworkInfo info;
	int result = 1;
	boolean isConnected = false;
	private final float NOISE = (float) 2.0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isGooglePlayServicesAvailable()) {
			finish();
		}
		setContentView(R.layout.bumpylocation);
		fileOperations = new FileOperations(this);
		// tvLocation = (TextView) findViewById(R.id.tvLocation);
		x_acc = (TextView) findViewById(R.id.x_acc);
		ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
		y_acc = (TextView) findViewById(R.id.y_acc);
		z_acc = (TextView) findViewById(R.id.z_acc);
		upload = (Button) findViewById(R.id.upload);
		x_acc.setText("");
		y_acc.setText("");
		z_acc.setText("");
		mInitialized = false;
		criteria = new Criteria();
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connectivityManager.getActiveNetworkInfo();
		isConnected = info.isConnected();
		// SupportMapFragment supportMapFragment = (SupportMapFragment)
		// getSupportFragmentManager()
		// .findFragmentById(R.id.googleMap);
		// googleMap = supportMapFragment.getMap();
		// googleMap.setMyLocationEnabled(true);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(bestProvider)) {
			showSettingsAlert("GPS");
			Log.d("cameback", "working");
		}
		time = new Time(Time.getCurrentTimezone());
		time.setToNow();

		filename = time.format("%k" + "%M" + "%S") + time.monthDay
				+ (time.month + 1) + time.year;

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 0, this);

		upload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Senddatafromfile file = new Senddatafromfile(defaultfile,
						"http://192.168.3.196/location.php");
			}
		});
	}

	// public Location getLocation(String provider) {
	// if (locationManager.isProviderEnabled(provider)) {
	// locationManager.requestLocationUpdates(provider, 2000, 0, this);
	// if (locationManager != null) {
	// location = locationManager.getLastKnownLocation(provider);
	// return location;
	// }
	// }
	//
	// return null;
	// }

	public void showSettingsAlert(String provider) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				BumpyLocation.this);
		alertDialog.setTitle(provider + " SETTINGS");

		alertDialog.setMessage(provider
				+ " is not enabled! Want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						
						BumpyLocation.this.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						finish();
					}
				});

		alertDialog.show();
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			// tvX.setText("0.0");
			// tvY.setText("0.0");
			// tvZ.setText("0.0");
			mInitialized = true;
			fileOperations.write(defaultfile, filename);
		} else {
			float deltaX = Math.abs(mLastX - x);
			float deltaY = Math.abs(mLastY - y);
			float deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE)
				deltaX = (float) 0.0;
			if (deltaY < NOISE)
				deltaY = (float) 0.0;
			if (deltaZ < NOISE)
				deltaZ = (float) 0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			// edited here
			if (deltaZ > 1) {

				time.setToNow();
				if (location == null)
					location = locationManager
							.getLastKnownLocation(bestProvider);
				if (location != null) {
					Log.d("provider", bestProvider);
					latlong = new LatLng(location.getLatitude(),
							location.getLongitude());
					String write = time.monthDay + "/" + (time.month + 1) + "/"
							+ time.year + " " + time.format("%k:%M:%S") + " "
							+ latlong.latitude + " " + latlong.longitude + " "
							+ deltaX + " " + deltaY + " " + deltaZ;
					// if(!isConnected){
					fileOperations.write(filename, write);
					// }
					// else{
					// List<NameValuePair> list= new ArrayList<NameValuePair>();
					// String b[]=write.split(" ");
					// list.add(new BasicNameValuePair("date",b[0]));
					// list.add(new BasicNameValuePair("time", b[1]));
					// list.add(new BasicNameValuePair("latitude", b[2]));
					// list.add(new BasicNameValuePair("longitude", b[3]));
					// list.add(new BasicNameValuePair("x_acc", b[4]));
					// list.add(new BasicNameValuePair("y_acc", b[5]));
					// list.add(new BasicNameValuePair("z_acc",b[6]));
					// SendData senddata = new
					// SendData("http://192.168.3.196/location.php",list );
					// senddata.execute();
					// }
					x_acc.setText(x_acc.getText().toString() + "\n"
							+ Float.toString(deltaX));
					y_acc.setText(y_acc.getText().toString() + "\n"
							+ Float.toString(deltaY));
					z_acc.setText(z_acc.getText().toString() + "\n"
							+ Float.toString(deltaZ));
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		this.location = location;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		// Log.d("Provider", "enabled");
		bestProvider = "LocationManager.GPS_PROVIDER";
		location = locationManager.getLastKnownLocation(bestProvider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		// showSettingsAlert("GPS");
		// Log.d("Provider", "disabled");
		bestProvider = locationManager.getBestProvider(criteria, true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("Resume", "resemed");

	}

	public class Senddatafromfile {
		FileOperations fileOperations;
		String defaultname, url;
		String filenames[];
		String[] content;
		int i;
		boolean result = true;

		public Senddatafromfile(String name, String url) {
			// TODO Auto-generated constructor stub
			this.defaultname = name;
			this.url = url;
			fileOperations = new FileOperations(BumpyLocation.this);
			new Upload().execute();
		}

		class Upload extends AsyncTask<Void, Void, Void> {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				mSensorManager.unregisterListener(BumpyLocation.this,
						mAccelerometer);
				String file = fileOperations.read(defaultname);

				try {
					filenames = file.split("\n");
					
				} catch (Exception e) {
					Log.d("Can't open default file", "");
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub

				JSONParse jparse = new JSONParse();
				for (i = 0; i < filenames.length; i++) {
					String file = fileOperations.read(filenames[i]);
					Log.d("filenames",filenames[i]);
					try {
						content = file.split("\n");
						for (int j = 0; j < content.length; j++) {
							String b[] = content[j].split(" ");
							List<NameValuePair> list = new ArrayList<NameValuePair>();
							list.add(new BasicNameValuePair("date", b[0]));
							list.add(new BasicNameValuePair("time", b[1]));
							list.add(new BasicNameValuePair("latitude", b[2]));
							list.add(new BasicNameValuePair("longitude", b[3]));
							list.add(new BasicNameValuePair("x_acc", b[4]));
							list.add(new BasicNameValuePair("y_acc", b[5]));
							list.add(new BasicNameValuePair("z_acc", b[6]));
							jparse.makeHttpRequest(url, "POST", list);
							
						}
						result = true;

					} catch (Exception e) {
						
						result = false;
						
					}
					
				}
				
				return null;

			}

			@Override
			protected void onPostExecute(Void reesult) {
				// TODO Auto-generated method stub
				if (result) {
					File file = new File("/sdcard/" + defaultfile + ".txt");
					file.delete();
					try {
						fileOperations.write(defaultfile,
								filenames[filenames.length - 1]);
					} catch (Exception e) {
						Log.d("defaultfile","error");
					}
					Toast.makeText(BumpyLocation.this, "Upload Success",
							Toast.LENGTH_LONG).show();
					x_acc.setText("");
					y_acc.setText("");
					z_acc.setText("");
				} else {
					Toast.makeText(BumpyLocation.this, "Upload Failed",
							Toast.LENGTH_LONG).show();
				}
				mSensorManager.registerListener(BumpyLocation.this,
						mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
				Log.d("Success", "uploaded files");
			}

		}

	}

}
