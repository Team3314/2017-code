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
		double upperTargetTop = upperTargetX + upperTargetWidth;
		double theta = (Math.toDegrees(Math.atan((upperTargetTop - ((Constants.kXRes/2)-.5))/Constants.kFocalLength))) + 33;
		double distance = 66.5/(Math.tan(Math.toRadians(theta))); //66.5 in = height from camera to reflective tape
		return distance;	
	}
	
	public double calcTurretYaw() { //goes with turret tracking
		double centerX = upperTargetY + (upperTargetHeight/2);
		turnError = centerX - Constants.kCenterOfView;
		double yaw = Math.toDegrees(Math.atan(turnError/Constants.kFocalLength));
		return yaw;
	}
	
	public void distanceCheck() {
		if (distance >= 42 && distance < 48) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 48 && distance < 54) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 54 && distance < 60) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 60 && distance < 66) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 66 && distance < 72) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 72 && distance < 78) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 78 && distance < 84) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 84 && distance < 90) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 90 && distance < 96) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 96 && distance < 102) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 102 && distance < 108) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 108 && distance < 114) {
			robot.cam.desiredPosition = 0;
			robot.shooter.desiredSpeed = 0;
		}
		else if (distance >= 114 && distance < 120) {
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