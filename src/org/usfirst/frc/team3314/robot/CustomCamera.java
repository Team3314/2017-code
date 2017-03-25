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
		upperTargetX = pi.getNumber("Upper Target X", 160);
		upperTargetY = pi.getNumber("Upper Target Y", 0);
		upperTargetWidth = pi.getNumber("Upper Target Width", 0);
		upperTargetHeight = pi.getNumber("Upper Target Height", 0);
		
		lowerTargetX = pi.getNumber("Lower Target X", 0);
		lowerTargetY = pi.getNumber("Lower Target Y", 0);
		lowerTargetWidth = pi.getNumber("Lower Target Width", 0);
		lowerTargetHeight = pi.getNumber("Lower Target Height", 0);
		
		theta = (Math.toDegrees(Math.atan(((upperTargetX+upperTargetWidth) - ((Constants.kXRes/2)-.5)) / Constants.kFocalLength))) + 33;
	}
	
	public double calcDistance() { //goes with shooter throttle
		double upperTargetTop = upperTargetX - upperTargetWidth ;
		double theta = -(Math.toDegrees(Math.atan((upperTargetTop - ((Constants.kXRes/2)-.5))/Constants.kFocalLength))) + 46;
		double distance = 66.5/(Math.tan(Math.toRadians(theta))); //66.5 in = height from camera to reflective tape
		return distance;	
	}
	
	public double calcTurretYaw() { //goes with turret tracking
		double centerX = upperTargetY + (upperTargetHeight/2);
		turnError = centerX - Constants.kCenterOfView;
		double yaw = -Math.toDegrees(Math.atan(turnError/Constants.kFocalLength));
		return yaw;
	}
	
	public void distanceCheck() {
		if (distance >= 36 && distance < 43) {
			robot.cam.desiredPosition = 2128;
			robot.shooter.desiredSpeed =  3500;
		}
		else if (distance >= 43 && distance < 49) {
			robot.cam.desiredPosition = 2128;
			robot.shooter.desiredSpeed =  3500;;
		}
		else if (distance >= 49 && distance < 56) {
			robot.cam.desiredPosition = 2400;
			robot.shooter.desiredSpeed =  3500;
		}
		else if (distance >= 56 && distance < 62) {
			robot.cam.desiredPosition = 2400;
			robot.shooter.desiredSpeed =3500;
		}
		else if (distance >= 62 && distance < 68) {
			robot.cam.desiredPosition = 2400;
			robot.shooter.desiredSpeed =  3500;
		}
		else if (distance >= 68 && distance < 75) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 75 && distance < 81) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 81 && distance < 87) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 87 && distance < 92) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 92 && distance < 98) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 98 && distance < 102) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 102 && distance < 106) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 106 && distance < 112) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 120 && distance <= 126) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		/*else if (distance < 42 || distance > 126) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}*/
	}
}