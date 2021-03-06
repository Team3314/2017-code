package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/* 
* acceptable camera settings:
* min brightness, max contrast, default saturation, max gain,
* max white bal, min exposure, defocused at about 80, no auto settings 
*/

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
	
	double theta;
	double distance;
	double turnError = 0;
	double yaw;
	
	public CustomCamera(Robot r) {
		robot = r;
	}
	
	public void update() {
		//Gets every 20 ms numbers about target from raspberry pi and creates variables for them
		//Makes sure that "upperTarget" variables always refer to the upper target by making the upper target the one 
		//higher in the image (Lower x value) and hte lower target the one lower in the image (higher x value)
		if (pi.getNumber("UpperTarget X", 160) > pi.getNumber("Lower Target X", 160)) {
			lowerTargetX = pi.getNumber("Upper Target X", 160);
			lowerTargetY = pi.getNumber("Upper Target Y", 0);
			lowerTargetWidth = pi.getNumber("Upper Target Width", 0);
			lowerTargetHeight = pi.getNumber("Upper Target Height", 0);
			
			upperTargetX = pi.getNumber("Lower Target X", 0);
			upperTargetY = pi.getNumber("Lower Target Y", 0);
			upperTargetWidth = pi.getNumber("Lower Target Width", 0);
			upperTargetHeight = pi.getNumber("Lower Target Height", 0);
		}
		else {
			upperTargetX = pi.getNumber("Upper Target X", 160);
			upperTargetY = pi.getNumber("Upper Target Y", 0);
			upperTargetWidth = pi.getNumber("Upper Target Width", 0);
			upperTargetHeight = pi.getNumber("Upper Target Height", 0);
			
			lowerTargetX = pi.getNumber("Lower Target X", 0);
			lowerTargetY = pi.getNumber("Lower Target Y", 0);
			lowerTargetWidth = pi.getNumber("Lower Target Width", 0);
			lowerTargetHeight = pi.getNumber("Lower Target Height", 0);
		}
		
	}
	
	public double calcDistance() { //goes with shooter throttle
		double upperTargetTop = upperTargetX - upperTargetWidth ;
		//Calculates the angle between a horizontal line out from the camera and the line from the camera to the upper target
		double theta = -(Math.toDegrees(Math.atan((upperTargetTop - ((Constants.kXRes/2)-.5))/Constants.kFocalLength))) + 46;
		//Divides known height by the tangent of the angle to the target to the the distance
		distance = 66.5/(Math.tan(Math.toRadians(theta))); //66.5 in = height from camera to reflective tape
		return distance;	
	}
	
	
	//Calculates the angle the turret has to turn to make balls into the boiler
	public double calcTurretYaw() { //goes with turret tracking
		double centerX = upperTargetY + (upperTargetHeight/2); // Calculates the center pixel of the target
		turnError = centerX - Constants.kCenterOfView; // Calculates the distance in pixels between the center of the target and the center of the camera
		double yaw = -Math.toDegrees(Math.atan(turnError/Constants.kFocalLength)) + 4; //Calculates distance in degrees between center of view and center of target 
		return yaw;
	}
	//Sets cam and shooter speed to emperically tested values depending on calculated distance
	public void distanceCheck() {
		if (distance >= 36 && distance < 43) {
			robot.cam.desiredPosition = 976;
			robot.shooter.desiredSpeed =  3400;
		}
		else if (distance >= 43 && distance < 49) {
			robot.cam.desiredPosition = 896;
			robot.shooter.desiredSpeed = 3600;
		}
		else if (distance >= 49 && distance < 56) {
			robot.cam.desiredPosition = 1120;
			robot.shooter.desiredSpeed =  3600;
		}
		else if (distance >= 56 && distance < 62) {
			robot.cam.desiredPosition = 1152;
			robot.shooter.desiredSpeed =3630;
		}
		else if (distance >= 62 && distance < 68) {
			robot.cam.desiredPosition = 1200;
			robot.shooter.desiredSpeed =  3630;
		}
		else if (distance >= 68 && distance < 75) {
			robot.cam.desiredPosition = 1376;
			robot.shooter.desiredSpeed = 3630;
		}
		else if (distance >= 75 && distance < 81) {
			robot.cam.desiredPosition = 1392;
			robot.shooter.desiredSpeed = 3760;
		}
		else if (distance >= 81 && distance < 87) {
			robot.cam.desiredPosition = 1400;
			robot.shooter.desiredSpeed = 3860;
		}
		else if (distance >= 87 && distance < 92) {
			robot.cam.desiredPosition = 1264;
			robot.shooter.desiredSpeed = 3940;
		}
		else if (distance >= 92 && distance < 98) {
			robot.cam.desiredPosition = 1264;
			robot.shooter.desiredSpeed =4070;
		}
		else if (distance >= 98 && distance < 102) {
			robot.cam.desiredPosition = 1376;
			robot.shooter.desiredSpeed = 4145;
		}
		else if (distance >= 102 && distance < 106) {
			robot.cam.desiredPosition = 1392;
			robot.shooter.desiredSpeed = 4300;
		}
		else if (distance >= 106 && distance < 112) {
			robot.cam.desiredPosition = 1456;
			robot.shooter.desiredSpeed = 5400;
		}
		else if (distance >= 112) {
			robot.cam.desiredPosition = 1456;
			robot.shooter.desiredSpeed = 5400;
		}
	
	}
}
