package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class CustomCamera {
	Robot robot;
	NetworkTable pi = NetworkTable.getTable("CameraPublisher");
	double centerX;
	double pixelHeight;
	double distance;
	
	public CustomCamera(Robot r) {
		robot = r;
	}
	
	public void update() {
		centerX = pi.getNumber("CenterX", 160);
		pixelHeight = pi.getNumber("pixelHeight", 0);
	}
	
	public double getXError() {
		double error = 0;
		 error = Constants.kCenterOfView - centerX;
		return error;
	}
	
	public double calcDistance() {
		distance = Constants.kTargetHeight * Constants.kYRes / (2 * pixelHeight * Math.tan(Constants.kHorizontalViewAngle));
		return distance;	
	}
}