package org.usfirst.frc.team3314.robot;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class DataLogger {
	File file;
	StringBuilder sb;
	PrintWriter writer;
	boolean firstRun = true;
	String print = "";
	
	public DataLogger() {
		firstRun = true;
	}
	
	
	public void logData (String[] fields, double[] data) {
		if (firstRun) {
			try {
				Calendar date = Calendar.getInstance();
				file  = new File("log.csv" + date.getTimeInMillis());
				if(!file.exists()) {
					file.createNewFile();
				}
				sb = new StringBuilder(print);
				writer = new PrintWriter(file);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			sb.append("Time,");
			for (int i = 0; i < fields.length; i ++) {
				sb.append(fields[i] + ",");
			}
			sb.append("\n");
			firstRun = false;
		}
		Calendar date = Calendar.getInstance();
		sb.append(date.getTimeInMillis()+ ",");
		for (int i = 0; i < data.length; i++) {
			sb.append(data[i] + ",");
		}
		sb.append("\n");
		writer.write(sb.toString());
		writer.flush();
		sb.setLength(0);
	}
	
}
