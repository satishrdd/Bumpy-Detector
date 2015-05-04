package com.funapps.naveen;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {
	EditText username, password;
	Button login;
	TextView alerttext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		initialize();
		alerttext.setVisibility(View.INVISIBLE);
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (username.getText().toString().length() > 0
						&& password.getText().toString().length() > 0)
					new Checklogin().execute();
				else {
					alerttext.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initialize() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login);
		alerttext = (TextView) findViewById(R.id.alerttext);
	}

	class Checklogin extends AsyncTask<Void, Void, Integer> {
		final String url = "http://192.168.3.196/Bumpy/login.php";
		List<NameValuePair> list;

		Checklogin() {
			list = new ArrayList<NameValuePair>();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			list.add(new BasicNameValuePair("username", username.getText()
					.toString()));
			list.add(new BasicNameValuePair("password", password.getText()
					.toString()));
			JSONParse jparse = new JSONParse();
			JSONObject object = jparse.makeHttpRequest(url, "POST", list);
			try {
				if (object.getInt("result") == 1) {
					return 1;
				} else {
					return 0;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == 1) {
				Intent i = new Intent("com.funapps.naveen.MainActivity");
				i.putExtra("username", username.getText().toString());
				i.putExtra("password", password.getText().toString());
				startActivity(i);
			} else {
				username.setText("");
				password.setText("");
				alerttext.setVisibility(View.VISIBLE);
			}
		}

	}

}
