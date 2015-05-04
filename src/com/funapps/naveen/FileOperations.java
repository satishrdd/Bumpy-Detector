package com.funapps.naveen;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import android.content.Context;
import android.util.Log;

public class FileOperations {
	Context c;

	public FileOperations(Context c) {
		// TODO Auto-generated constructor stub
		this.c = c;
	}

	public boolean write(String filename, String filecontent) {
		try {
			String filepath = filename;
			FileOutputStream fos = new FileOutputStream(filepath, true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			osw.write(filecontent + "\n");
			osw.close();
			Log.d("Success", "Success");

		} catch (Exception e) {
			Log.d("Error", "Can't write to file");
		}

		return false;

	}

	public String read(String filename) {

		String Response = null;
		try {
			String filepath = filename;
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			Response = sb.toString();

			br.close();
		} catch (Exception e) {
			Log.d("Exception", "Can't read file");
		}

		return Response;
	}
}
