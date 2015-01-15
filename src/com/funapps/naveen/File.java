package com.funapps.naveen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class File extends Activity{
	
	Button fwrite,fread;
	TextView tvout;
	EditText etwrite;
	FileOperations foperations;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file);
		fwrite = (Button) findViewById(R.id.btwrite);
		fread = (Button) findViewById(R.id.btfout);
		tvout = (TextView) findViewById(R.id.fileoutput);
		etwrite = (EditText) findViewById(R.id.write);
		final String filename = "file";
		foperations = new FileOperations();
		fwrite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String write = etwrite.getText().toString();
				if(write.length()>0){
					foperations.write(filename, write);
				}
			}
		});
		fread.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String response = foperations.read(filename);
				tvout.setText(response);
			}
		});
	}
}
