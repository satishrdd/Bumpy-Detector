package com.funapps.naveen;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewFiles extends Activity {

	ListView list;
	ArrayList<String> array;
	FileOperations fileoperations;
	String[] filenames;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewfiles);
		list = (ListView) findViewById(R.id.viewfilelist);
		array = new ArrayList<String>();
		fileoperations = new FileOperations(this);
		File filename = new File("/sdcard/files.txt");
		if (filename.exists() & filename.length() != 0) {
			String file = fileoperations.read("files");
			filenames = file.split("\n");
			for (int i = 0; i < filenames.length; i++) {
				array.add(filenames[i] + ".txt");
			}
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_multiple_choice, array);
			list.setAdapter(adapter);
		}
	}
}
