package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDOutput;

enum driveMode {
	IDLE,
	TANK,
	GYROLOCK,
	SPEEDCONTROL
}

public class TankDriveTrain {
	Robot robot;
	
	CANTalon rDriveTalon1;
	CANTalon rDriveTalon2;
	CANTalon lDriveTalon1;
	CANTalon lDriveTalon2;
	
	double count;
	double leftStickInput;
	double rightStickInput;
	double rawLeftSpeed;
	double rawRightSpeed;
	double desiredSpeed;
	double desiredAngle;
	
	//for gyrolock
	PIDController PID;
	PIDSource PIDSource;
	PIDOutput PIDOutput;
	GyroPIDSource gyroPIDSource;
    GyroPIDOutput gyroPIDOutput;
    PIDController gyroControl;
	
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
		lDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		
		/*gyroPIDSource = new GyroPIDSource(robot, robot.hi.rightStick.getY(), 0);
		gyroPIDOutput = new GyroPIDOutput();
		gyroControl = new PIDController(0.5, 0.000025, 0, 0, gyroPIDSource, gyroPIDOutput);*/
		
		//to make speedcontrol work goodly
		lDriveTalon1.configEncoderCodesPerRev(497);
		rDriveTalon1.configEncoderCodesPerRev(497);
	}
	
	public void update() {
		lDriveTalon1.set(rawLeftSpeed);
		rDriveTalon1.set(rawRightSpeed);
		
		if (currentMode == driveMode.SPEEDCONTROL){
			lDriveTalon1.changeControlMode(TalonControlMode.Speed);
			rDriveTalon1.changeControlMode(TalonControlMode.Speed);
		} else {
			lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
			rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		}
		
		switch(currentMode){
		case IDLE:
			//motor stopped
			rawLeftSpeed = 0;
			rawRightSpeed = 0;
			break;
		case TANK:
			//motor speed determined by joystick input
			rawLeftSpeed = leftStickInput;
			rawRightSpeed = rightStickInput;
			break;
		case GYROLOCK:
 			//motor speed determined by angle of robot relative to desired angle, pid broken atm
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
			
			correction = errorAngle * 0.05; //0.05 is old gyroPconstant
			
			rawLeftSpeed = desiredSpeed - (correction);
			rawRightSpeed = desiredSpeed + (correction);
			
			//SmartDashboard.putNumber("Error angle", errorAngle);
			//SmartDashboard.putNumber("Correction", correction);
			//SmartDashboard.putNumber("Desired angle", desiredAngle);
			//SmartDashboard.putNumber("Desired speed", desiredSpeed);
			
			
			
			//robot.gyroControl.setSetpoint(robot.hi.rightStick.getY());	
			break;
		case SPEEDCONTROL:
			//motor speed is equivalent to desired rpm
			lDriveTalon1.setPID(Constants.kSpeedControl_kP,Constants.kSpeedControl_kI,Constants.kSpeedControl_kD,
			Constants.kSpeedControl_kF,Constants.kSpeedControl_IZone,Constants.kSpeedControl_RampRate,
			Constants.kSpeedControl_Profile);
			
			rDriveTalon1.setPID(Constants.kSpeedControl_kP,Constants.kSpeedControl_kI,Constants.kSpeedControl_kD,
			Constants.kSpeedControl_kF,Constants.kSpeedControl_IZone,Constants.kSpeedControl_RampRate,
			Constants.kSpeedControl_Profile);
			
			rawLeftSpeed = leftStickInput;
			rawRightSpeed = rightStickInput;
			
			if (robot.hal.driveShifter.get().toString() == "kForward"){
				rawLeftSpeed *= Constants.kHighGear; //high gear
				rawRightSpeed *= Constants.kHighGear;
				}
				
			if (robot.hal.driveShifter.get().toString() == "kReverse"){
				rawLeftSpeed *= Constants.kLowGear; //low gear
				rawRightSpeed *= Constants.kLowGear;
			}
			break;
		}
	}
	
	public void setStickInputs(double leftInput, double rightInput) {
		//sets what joystick input to get
		leftStickInput = leftInput;
		rightStickInput = rightInput;
	}
		
	public void setDriveMode(driveMode mode) {
		currentMode = mode;
	}
	
	public void setDriveTrainSpeed(double speed) {	
		desiredSpeed = speed;
	}
	
	public void setDriveAngle(double angle) {
		desiredAngle = angle;
	}	
}