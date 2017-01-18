package org.usfirst.frc.team3314.robot; //from old code

import java.io.IOException;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CustomCamera {


	Image frame;
	double count;
	Joystick stick;
	double horizontalAngle = 0;
	double distance = 0;
	double centerX;
	double centerY;
	
	Robot robot;
	
	private final static String[] GRIP_ARGS = new String [] {
    	"/usr/local/frc/JRE/bin/java", "-jar",
    	"/home/lvuser/grip.jar", "/home/lvuser/project.grip"
    };
    
    private final NetworkTable grip = NetworkTable.getTable("GRIP");
	
	public CustomCamera(Robot r){
		robot = r;
		count = 0;
		
		
	}
	

	public void CreateImage(){
		
		 frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
	}
	
	public void CreateServer(){
		  try {
	        	Runtime.getRuntime().exec(GRIP_ARGS);
	        	
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	}
	
	public void DisplayData(){
		//count++;
    	//if (count == 13) {
    	for(double centerx : grip.getNumberArray("targets/centerX",  new double[0]) ) {
    		//System.out.println(centerx);
    		centerX = centerx;
    	}
    	
    	/*double lowestError = 640;		//max resolution
    	for(double centerx1 : grip.getNumberArray("targets/centerX",  new double[0]) ) {
    		//System.out.println(cente+---------------------------------------------------
    		---------------------------------------------------*rx);
    		if (Math.abs(centerx1 - 320)< lowestError)
    		{
    			lowestError = Math.abs(centerx1 - 320);
    			centerX = centerx1;
    		}
    	}
    	/*for(double centerx1 : grip.getNumberArray("targets/centerX",  new double[0]) ) {
    		double centerx[] = grip.getNumberArray("targets/centerX",  new double[0]);
    		//System.out.println(centerx);
    		if (centerx.length > 1) {
        		if (Math.abs(centerx[0] - 320) < Math.abs(centerx[1] - 320) ) {
            		centerX = centerx[0];
        		}
        		else {
        			centerX = centerx[1];
        		}
        		
        	} else {
        		centerX = centerx1;        	}
        		
    	}*/
    	
    	for (double centery : grip.getNumberArray("targets/centerY", new double[0])) {
			centerY = centery;
		}
    	
    	SmartDashboard.putNumber("center y", centerY);
    	//count = 0;
    	}
	//}

	
	
	public double getDistance(){
		double distance = 0;
		//double aspectRatio;
		double fov;
		//double correction;
		//double height[] = grip.getNumberArray("targets/height", new double [0]);
		
		
			for (double width : grip.getNumberArray("targets/width" , new double[0]) ) {
				fov =  44.977;
				
				//aspectRatio = width/height[0];
				//correction = .459/aspectRatio;
				
				
				
				
				
				distance = 20*300/(width*0.54298947895);
				
			}
			
		    
			return distance;
		
	}
	public double getFOV() {
		double FOV = Math.atan(20*600/10455);
		
		return FOV;
	}
	
	public double getHorizontalAngle() {
		double center = 0;
		double horizontalAngle = 0;
		for (double centerX : grip.getNumberArray("targets/centerX", new double[0])) {
			center = centerX;
			horizontalAngle = center/(640/30);
		}
		return horizontalAngle - 30;
		
	}
	/*
	public double getVerticalAngle() {
		double center = 0;
		double VerticalAngle = 0;
		for (double centerY : grip.getNumberArray("targets/centerY", new double[0])) {
		center = centerY;
		VerticalAngle = center/(400/24.5);
		}
		return -VerticalAngle + 24.5;
		
	}
	*/
	public void centerCamera() {
		SmartDashboard.putNumber("center x", centerX);
		
		if (centerX > 363  && centerX < 383) {
			robot.tdt.setStickInputs(0, 0);
		}
		else if (centerX > 383) {
			robot.tdt.setStickInputs(-.2, .1875);
		}
		else if (centerX < 363) {
			robot.tdt.setStickInputs(.1875, -.2);
		}
	}
	
	public void centerCameraWithP() {
		SmartDashboard.putNumber("center x", centerX);
		double cameraError = centerX - 373;
		double cameraPconstant = .003;
		double correction = cameraError * cameraPconstant;
		
		
		if (centerX > 368  && centerX < 378) {
			robot.tdt.setStickInputs(0, 0);
		}
		else if (centerX > 378) {
			robot.tdt.setStickInputs((-.2 - correction), (.1875 + correction));
		}
		else if (centerX < 368) {
			robot.tdt.setStickInputs((.1875 - correction), (-.2 + correction));
		}
	}
	
	public void centerCameraAngle() {
		SmartDashboard.putNumber("center x", centerX);
		double cameraError = centerX - 340;
		double xAngleError = cameraError * .09375;
		
		
		robot.tdt.setDriveMode(driveMode.GYROLOCK);
		
		if (Math.abs(xAngleError) <= .75){
			robot.tdt.setDriveTrainSpeed(0);
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
		}
		else {
			robot.tdt.setDriveAngle(robot.hal.gyro.angle() + xAngleError);		}
		}
	
	public void adjustDistance() {
		robot.tdt.setDriveMode(driveMode.GYROLOCK);
		
		
		
		if (centerY > 175 && centerY < 205) {
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(0);
		}
		else if (centerY > 205) {
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(-.85);
		}
		else if (centerY < 175) {
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(.85);
		}
		}
	}
	/*
	public void setTiltAngle(int degrees) {
		robot.hal.cameraTilt.setAngle(degrees);
		
	}
	
	public void setPanAngle(int degrees) {
		robot.hal.cameraPan.setAngle(degrees);
	}*/
	

