package org.usfirst.frc.team3314.robot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import edu.wpi.first.wpilibj.Timer;

public class DataLogger {
	
	Robot robot;
	File data;
	PrintWriter pw;
	StringBuilder sb;
    boolean run = false;
    
	
	public DataLogger(Robot r) {
		robot = r;		
	}
	
	public void createNewFile() {
		run = false;
		data = new File("test"+ System.currentTimeMillis() + ".csv");
		try {
			if(!data.exists()) {
				data.createNewFile();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logData(String[] names, String[] values) {
	     if (!run) {
	    	 try {
	 			pw = new PrintWriter(data);
	 			sb = new StringBuilder();
	 		}
	 		catch (IOException e) {
	 			e.printStackTrace();
	 		}
	    	 sb.append("Time");
		     sb.append(',');
		     for(int i = 0; i < names.length; i++) {
			     sb.append(names[i].toString());
			     sb.append(',');
		     }
		     sb.append('\n');
		     run = true;
	     }
	     sb.append(System.currentTimeMillis());
	     sb.append(',');
	     for(int i = 0; i < values.length; i++) {
	    	 sb.append(values[i].toString());
	    	 sb.append(',');
	     }
	     sb.append('\n');
	     pw.print(sb.toString());
	     pw.flush(); //Forces Printwriter to write to file every loop instead of waiting for buffer to fill up
	     sb.setLength(0);
	     
	}

}
