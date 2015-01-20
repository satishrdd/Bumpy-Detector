package com.funapps.naveen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

public class BumpyLocation extends FragmentActivity implements
		LocationListener, SensorEventListener {
	TextView x_acc, y_acc, z_acc;
	LocationService locationService;
	Time time;
	// GoogleMap googleMap;
	LatLng latlong;
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	FileOperations fileOperations;
	final String filename = "location";
	private final float NOISE = (float) 2.0;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isGooglePlayServicesAvailable()) {
			finish();
		}
		setContentView(R.layout.bumpylocation);
		fileOperations = new FileOperations();
		// tvLocation = (TextView) findViewById(R.id.tvLocation);
		x_acc = (TextView) findViewById(R.id.x_acc);
		y_acc = (TextView) findViewById(R.id.y_acc);
		z_acc = (TextView) findViewById(R.id.z_acc);
		x_acc.setText("");
		y_acc.setText("");
		z_acc.setText("");
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);

		// SupportMapFragment supportMapFragment = (SupportMapFragment)
		// getSupportFragmentManager()
		// .findFragmentById(R.id.googleMap);
		// googleMap = supportMapFragment.getMap();
		// googleMap.setMyLocationEnabled(true);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null) {
			onLocationChanged(location);
		}

		locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);
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
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		latlong = new LatLng(latitude, longitude);

		// googleMap.addMarker(new MarkerOptions().position(latlong));
		// googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlong));
		// googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		// tvLocation.setText("Latitude: " + latitude + "\n" + "Longitude: "
		// + longitude);
		//

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
				// CircleOptions circleOptions = new CircleOptions();
				// circleOptions.center(latlong);
				// circleOptions.radius(5);
				// circleOptions.strokeColor(Color.YELLOW);
				// circleOptions.strokeWidth(5);
				// circleOptions.fillColor(Color.GREEN);
				// googleMap.addCircle(circleOptions);
				// if (prevlatlong != null && latlong != prevlatlong)
				// googleMap.addPolyline(new PolylineOptions().add(latlong,
				// prevlatlong));
				Time time = new Time(Time.getCurrentTimezone());
				time.setToNow();
				String write = latlong.latitude + " " + latlong.longitude+" "+time.monthDay+"/"+time.month+1+"/"+time.year+" "+time.format("%k:%M:%S");
				if(deltaZ>1&deltaZ<5){
					write = "1 "+write;
				}
				else if(deltaZ>=5&deltaZ<10){
					write = "2 "+write;
				}
				else if(deltaZ>=10&deltaZ<20){
					write = "3 "+write;
				}
				else if(deltaZ>=20&deltaZ<30){
					write = "4 "+write;
				}
				else if(deltaZ>=30&deltaZ<40){
					write = "5 "+write;
				}
				else if(deltaZ>=40&deltaZ<50){
					write = "6 "+write;
				}
				else{
					write = "7 "+write;
				}
				
				fileOperations.write(filename, write);
				x_acc.setText(x_acc.getText().toString() + "\n"
						+ Float.toString(deltaX));
				y_acc.setText(y_acc.getText().toString() + "\n"
						+ Float.toString(deltaY));
				z_acc.setText(z_acc.getText().toString() + "\n"
						+ Float.toString(deltaZ));
			}

			// iv.setVisibility(View.VISIBLE);
			if (deltaX > deltaY) {
				// iv.setImageResource(R.drawable.horizontal);
			} else if (deltaY > deltaX) {
				// iv.setImageResource(R.drawable.vertical);
			} else {
				// iv.setVisibility(View.INVISIBLE);
			}
		}
	}

	protected void onResume() {
		super.onResume();
		// mSensorManager.registerListener(this, mAccelerometer,
		// SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onPause() {
		super.onPause();
		Log.d("Paused", "event paused");
		// mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
