package org.usfirst.frc.team3314.robot; //from old code

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogGyro;

public class CustomGyro {
	AnalogGyro gyro;
	AnalogInput gyroTemp;
	
	double drift = 0.0002;
	double coeffcient = 0.00000217;
	double accumulatedDrift;
	double baseTemp = 679;
	double degrees = 0;
	double temp = 0;
	
	public CustomGyro(int gyroPort, int tempPort, double sensitivity){
		gyro = new AnalogGyro(gyroPort);
		gyroTemp = new AnalogInput(tempPort);
		gyro.setSensitivity(sensitivity);
	}
	
	public void reset(){
		gyro.reset();
		accumulatedDrift = 0;
	}
	
	public void update(){
		degrees = gyro.getAngle();
		temp = gyroTemp.getValue();
		double deltaTemp = temp - baseTemp;
		
		accumulatedDrift += drift + deltaTemp * coeffcient;
		//degrees -= accumulatedDrift;
	}
	
	public double angle(){
		return degrees;
	}
	
	public double temp(){
		return temp;
	}
	
	public void calibrate(){
		gyro.initGyro();
	}
}