package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;

enum driveMode {
	IDLE,
	TANK,
	GYROLOCK
}

public class TankDriveTrain {

	CANTalon rDriveTalon1;
	CANTalon rDriveTalon2;
	CANTalon lDriveTalon1;
	CANTalon lDriveTalon2;
	Robot robot;
	
	double leftStickInput;
	double rightStickInput;
	double rawLeftSpeed;
	double rawRightSpeed;
	double desiredSpeed;
	double desiredAngle;
	double gyroPconstant = 0.05;
	
	driveMode currentMode = driveMode.IDLE;

	public TankDriveTrain(Robot myRobot) {
		robot = myRobot;
		rDriveTalon1 = new CANTalon(0);
		rDriveTalon2 = new CANTalon(2);
		lDriveTalon1 = new CANTalon(1);
		lDriveTalon2 = new CANTalon(3);
		
		//sets forward talons to default mode and rear talons as following them
		lDriveTalon2.changeControlMode(TalonControlMode.Follower);
		lDriveTalon2.set(lDriveTalon1.getDeviceID());
		rDriveTalon2.changeControlMode(TalonControlMode.Follower);
		rDriveTalon2.set(rDriveTalon1.getDeviceID());
		lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
	}
	
	public void update() {
		lDriveTalon1.set(rawLeftSpeed);
		rDriveTalon1.set(rawRightSpeed);
		
		switch(currentMode){
		case IDLE:
			//motor stopped
			rawLeftSpeed = 0;
			rawRightSpeed = 0;
			break;
		case TANK:
			//motor speed determined by joystick input (set to negative to have motor turn correct direction)
			rawLeftSpeed = -(leftStickInput);
			rawRightSpeed = -(rightStickInput);
			break;
		case GYROLOCK:
 			//motor speed determined by angle of robot relative to desired angle
			double currentAngle = robot.hal.gyro.angle();
			double errorAngle = desiredAngle - currentAngle;
			double correction;
			
			//keeps error between -180 and 180
			errorAngle = errorAngle % 360;
			while (errorAngle > 180){
				errorAngle -= 360;
			}
			while (errorAngle < -180){
				errorAngle += 360;
			}
			
			correction = errorAngle * gyroPconstant;
			
			/*SmartDashboard.putNumber("Error angle", errorAngle);
			SmartDashboard.putNumber("correction", correction);
			SmartDashboard.putNumber("desired angle", desiredAngle);
			SmartDashboard.putNumber("desired speed", desiredSpeed);*/
			
			rawLeftSpeed = desiredSpeed - (correction);
			rawRightSpeed = desiredSpeed + (correction);
			break;
		}
	}
	
	public void setStickInputs(double leftInput, double rightInput) {
		//sets what joystick input to get
		leftStickInput = leftInput;
		rightStickInput = rightInput;
	}
	
	public void setDriveTrainSpeed(double speed) {	
		//mainly used for autonomous
		desiredSpeed = speed;
	}
		
	public void setDriveMode(driveMode mode) {
		currentMode = mode;
	}
	
	public void setDriveAngle(double angle) {
		desiredAngle = angle;
	}	
}