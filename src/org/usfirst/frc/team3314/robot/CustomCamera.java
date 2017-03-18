package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class CustomCamera {
	Robot robot;
	NetworkTable pi = NetworkTable.getTable("CameraPublisher");
	double upperTargetX;
	double upperTargetY;
	double upperTargetHeight;
	double upperTargetWidth;
	double lowerTargetX;
	double lowerTargetY;
	double lowerTargetHeight;
	double lowerTargetWidth;
	double upperCenterX;
	double lowerCenterX;
	double distance;
	
	public CustomCamera(Robot r) {
		robot = r;
	}
	
	public void update() {
		upperTargetX = pi.getNumber("Upper Target X", 160);
		upperTargetY = pi.getNumber("Upper Target Y", 0);
		upperTargetWidth = pi.getNumber("Upper Target Width", 0);
		upperTargetHeight = pi.getNumber("Upper Target Height", 0);
		lowerTargetX = pi.getNumber("Lower Target Width", 0);
		lowerTargetY = pi.getNumber("Lower Target Width", 0);
		lowerTargetWidth = pi.getNumber("Lower Target Width", 0);
		lowerTargetHeight = pi.getNumber("Lower Target Width", 0);
		
		
	}
	
	public double getTurnErrorPixels() {
		double error = 0;
		double centerX = upperTargetY + .5*upperTargetHeight;
		 error = Constants.kCenterOfView - centerX;
		return error;
	}
	
	public double calcDistance() {
		double upperTargetTop = upperTargetX + upperTargetWidth;
		double angle = ((upperTargetTop - .5*(Constants.kYRes)) / 226.7) + 33;
		double distance = 66.5/Math.tan(angle);
		return distance;	
	}
}