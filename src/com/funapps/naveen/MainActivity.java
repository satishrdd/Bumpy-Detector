package com.funapps.naveen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements OnClickListener,
		SensorEventListener, LocationListener {

	Spinner vehicletype;
	JSONArray myarray;
	JSONObject myobject;
	Button stop, start, upload, btview;
	GoogleMap googlemap;
	TextView tvstatus;
	FileOperations fileoperations;
	boolean mInitialized;
	float mLastX, mLastY, mLastZ;
	Location location;
	LocationManager locationManager;
	String bestProvider = "LocationManager.GPS_PROVIDER", filename,
			defaultfile = "files";
	Criteria criteria;
	Time time;
	LatLng latlng;
	SensorManager mSensorManager;
	Sensor mAccelerometer;
	ConnectivityManager connectivityManager;
	NetworkInfo info;
	boolean isConnected = false;
	private final float NOISE = (float) 2.0;
	Handler customHandler;
	long timeinSeconds = 0L, updatedTime = 0L, starttime = 0L;
	public ArrayAdapter<String> adapter;
	private String[] type = { "Auto", "Bike", "BiCycle", "Bus", "Car", "Lorry" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (!isGooglePlayServicesAvailable()) {
			finish();
		}
		setContentView(R.layout.menu);

		init();
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		fileoperations = new FileOperations(this);
		SupportMapFragment supportmapfragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.googleMap);
		googlemap = supportmapfragment.getMap();
		googlemap.setMyLocationEnabled(true);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		criteria = new Criteria();
		customHandler = new Handler();
		location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		btview.setOnClickListener(this);
		upload.setOnClickListener(this);
		adapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_spinner_item, type);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		vehicletype.setAdapter(adapter);
		vehicletype.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				vehicletype.setSelection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
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
						finish();
					}
				});

		alertDialog.show();
	}

	public void showNetworks() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				MainActivity.this);
		alertDialog.setTitle("No Internet");

		alertDialog.setMessage("No Internet Connection Available.");

		alertDialog.setPositiveButton("Try Later",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
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

	void init() {

		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		upload = (Button) findViewById(R.id.upload);
		btview = (Button) findViewById(R.id.view);
		tvstatus = (TextView) findViewById(R.id.tvstatus);
		vehicletype = (Spinner) findViewById(R.id.vehicletype);
		stop.setEnabled(false);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.start:
			start.setEnabled(false);
			stop.setEnabled(true);
			mInitialized = false;
			time = new Time(Time.getCurrentTimezone());
			time.setToNow();

			filename = time.monthDay + "" + (time.month + 1) + "" + time.year
					+ "" + time.format("%k" + "%M" + "%S");
			myobject = new JSONObject();
			myarray = new JSONArray();

			mSensorManager.registerListener(MainActivity.this, mAccelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
			starttime = SystemClock.uptimeMillis();
			customHandler.postDelayed(updateTimerThread, 0);
			break;
		case R.id.stop:
			stop.setEnabled(false);
			start.setEnabled(true);
			customHandler.removeCallbacks(updateTimerThread);
			mSensorManager
					.unregisterListener(MainActivity.this, mAccelerometer);
			try {
				myobject.put("data", myarray);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fileoperations.write(defaultfile, filename);
			fileoperations.write(filename, myobject.toString());
			break;
		case R.id.upload:
			upload.setEnabled(false);
			mSensorManager
					.unregisterListener(MainActivity.this, mAccelerometer);
			start.setEnabled(false);
			customHandler.removeCallbacks(updateTimerThread);
			new Senddatafromfile(defaultfile,
					"http://192.168.3.196/Bumpy/uploadfile.php");

			break;
		case R.id.view:
			Intent i = new Intent("com.funapps.naveen.ViewFILES");
			startActivity(i);
			break;
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
			try {
				myobject.put("filename", filename);
				myobject.put("vehicletype", vehicletype.getSelectedItem());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
			if (deltaZ > 1) {

				time.setToNow();
				if (location == null)
					location = locationManager
							.getLastKnownLocation(bestProvider);
				if (location != null) {
					Log.d("provider", bestProvider);
					int tempmonth = time.month + 1;
					int tempday = time.monthDay;
					int year = time.year;
					String month = tempmonth + "";
					String day = tempday + "";
					if (tempmonth < 10)
						month = "0" + tempmonth;
					if (tempday < 10)
						day = "0" + tempday;
					String timer = time.format("%k:%M:%S");
					JSONObject tempobj = new JSONObject();
					try {
						tempobj.put("time", timer);
						tempobj.put("date", day+"-"+month+"-"+year);
						tempobj.put("latitude", location.getLatitude());
						tempobj.put("longitude", location.getLongitude());
						tempobj.put("xacc", deltaX);
						tempobj.put("yacc", deltaY);
						tempobj.put("zacc", deltaZ);
						tempobj.put("speed", location.getSpeed());
						myarray.put(tempobj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//					String write = time.monthDay + "/" + (time.month + 1) + "/"
//							+ time.year + " " + time.format("%k:%M:%S") + " "
//							+ location.getLatitude() + " "
//							+ location.getLongitude() + " " + deltaX + " "
//							+ deltaY + " " + deltaZ + " " + location.getSpeed();
//					fileoperations.write(filename, write);

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
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		latlng = new LatLng(latitude, longitude);
		googlemap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
		googlemap.animateCamera(CameraUpdateFactory.zoomTo(15));

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		bestProvider = "LocationManager.GPS_PROVIDER";
		location = locationManager.getLastKnownLocation(bestProvider);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		showSettingsAlert("GPS");
		bestProvider = locationManager.getBestProvider(criteria, true);
	}

	public Runnable updateTimerThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			timeinSeconds = SystemClock.uptimeMillis() - starttime;
			updatedTime = timeinSeconds;
			long sec = (updatedTime / 1000);
			long min = (sec / 60);
			long hrs = (min / 60);
			sec = sec % 60;
			tvstatus.setText(String.format("%02d", hrs) + ":"
					+ String.format("%02d", min) + ":"
					+ String.format("%02d", sec));
			customHandler.postDelayed(this, 0);
		}

	};

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
			fileOperations = new FileOperations(MainActivity.this);
			new Upload().execute();
		}

		class Upload extends AsyncTask<Void, Void, Void> {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();

				String file = fileOperations.read(defaultname);

				try {
					filenames = file.split("\n");

				} catch (Exception e) {
					Log.d("Can't open default file", "");
					filenames = null;
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				i = 0;

				while (filenames != null && i < filenames.length) {
					try {
						// Set your file path here
						connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						info = connectivityManager.getActiveNetworkInfo();
						if (info == null) {

							return null;
						} else {
							FileInputStream fstrm = new FileInputStream(
									"/sdcard/" + filenames[i] + ".txt");

							// Set your server page url (and the file
							// title/description)
							FileUpload hfu = new FileUpload(url, filenames[i],
									"my file description");

							hfu.Send_Now(fstrm);
							i++;
						}

					} catch (FileNotFoundException e) {
						// Error: File not found

						Log.d("File Upload", "failed");

					}

				}

				return null;

			}

			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (filenames != null && i == filenames.length) {
					File file = new File("/sdcard/files.txt");
					file.delete();

					Toast.makeText(getApplicationContext(), "Upload Success",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "Upload failed",
							Toast.LENGTH_SHORT).show();
				}
				upload.setEnabled(true);
				start.setEnabled(true);
			}

		}

	}

}
