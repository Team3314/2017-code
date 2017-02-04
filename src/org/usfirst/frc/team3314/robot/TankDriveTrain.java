package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

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
	double last_world_linear_accel_y = 0;
	
	//for gyrolock
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
		
		gyroPIDOutput = new GyroPIDOutput();
		gyroControl = new PIDController(Constants.kGyroLock_kP, Constants.kGyroLock_kI, Constants.kGyroLock_kD, 
		Constants.kGyroLock_kF, robot.ahrs, gyroPIDOutput);
		
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
			rawLeftSpeed = desiredSpeed + gyroPIDOutput.turnSpeed;
			rawRightSpeed = desiredSpeed - gyroPIDOutput.turnSpeed;
			gyroControl.setSetpoint(desiredAngle);	
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
			
			if (robot.hal.driveShifter.get().toString() == Constants.kShiftHighGear){
				rawLeftSpeed *= Constants.kHighGearRPM; //high gear
				rawRightSpeed *= Constants.kHighGearRPM;
				}
				
			if (robot.hal.driveShifter.get().toString() == Constants.kShiftLowGear){
				rawLeftSpeed *= Constants.kLowGearRPM; //low gear
				rawRightSpeed *= Constants.kLowGearRPM;
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
	
	public double detectCollision() {
		double curr_world_linear_accel_y = robot.ahrs.getWorldLinearAccelY();
		double currentJerkY = curr_world_linear_accel_y - last_world_linear_accel_y;
		last_world_linear_accel_y = curr_world_linear_accel_y;
		return currentJerkY;
	}
}