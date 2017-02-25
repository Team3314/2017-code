package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

enum driveMode {
	IDLE,
	TANK,
	GYROLOCK,
	SPEEDCONTROL,
	GYROLOCK_SPEEDCONTROL
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
	double avgEncPos = 0;
	PIDController gyroControl;
	GyroPIDOutput gyroPIDOutput;
	
	driveMode currentMode = driveMode.IDLE;

	public TankDriveTrain(Robot myRobot) {
		robot = myRobot;
		
		gyroPIDOutput = new GyroPIDOutput();
	    gyroControl = new PIDController(Constants.kGyroLock_kP, Constants.kGyroLock_kI, Constants.kGyroLock_kD,
	    		Constants.kGyroLock_kF, robot.ahrs, gyroPIDOutput);
	    gyroControl.setContinuous(); //makes angle correct itself in the shortest distance
		gyroControl.setInputRange(-180, 180);
		
		rDriveTalon1 = new CANTalon(1);
		rDriveTalon2 = new CANTalon(3);
		lDriveTalon1 = new CANTalon(0);
		lDriveTalon2 = new CANTalon(2);
		
		//sets forward talons to default mode and rear talons as following them
		lDriveTalon2.changeControlMode(TalonControlMode.Follower);
		lDriveTalon2.set(lDriveTalon1.getDeviceID());
		rDriveTalon2.changeControlMode(TalonControlMode.Follower);
		rDriveTalon2.set(rDriveTalon1.getDeviceID());
		
		lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		lDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		lDriveTalon1.configEncoderCodesPerRev(2048);
		rDriveTalon1.configEncoderCodesPerRev(2048);
		lDriveTalon1.setInverted(true);
	}
	
	public void update() {
		lDriveTalon1.set(rawLeftSpeed);
		rDriveTalon1.set(rawRightSpeed);
		
		avgEncPos = (lDriveTalon1.getPosition() + rDriveTalon1.getPosition()) / 2;
		
		//talon changes mode based on tank drive state
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
 			//motor speed determined by angle of robot relative to desired angle
			rawLeftSpeed = desiredSpeed - gyroPIDOutput.turnSpeed;
			rawRightSpeed = desiredSpeed + gyroPIDOutput.turnSpeed;
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
				rawLeftSpeed *= Constants.kHighGearRPM;
				rawRightSpeed *= Constants.kLowGearRPM;
			}
				
			if (robot.hal.driveShifter.get().toString() == Constants.kShiftLowGear){
				rawLeftSpeed *= Constants.kLowGearRPM;
				rawRightSpeed *= Constants.kLowGearRPM;
			}
			break;
		case GYROLOCK_SPEEDCONTROL:
			lDriveTalon1.setPID(Constants.kSpeedControl_kP,Constants.kSpeedControl_kI,Constants.kSpeedControl_kD,
			Constants.kSpeedControl_kF,Constants.kSpeedControl_IZone,Constants.kSpeedControl_RampRate,
			Constants.kSpeedControl_Profile);
					
			rDriveTalon1.setPID(Constants.kSpeedControl_kP,Constants.kSpeedControl_kI,Constants.kSpeedControl_kD,
			Constants.kSpeedControl_kF,Constants.kSpeedControl_IZone,Constants.kSpeedControl_RampRate,
			Constants.kSpeedControl_Profile);
			
			rawLeftSpeed = leftStickInput;
			rawRightSpeed = rightStickInput;
			rawLeftSpeed += gyroPIDOutput.turnSpeed;
			rawRightSpeed += gyroPIDOutput.turnSpeed;
			
			if (robot.hal.driveShifter.get().toString() == Constants.kShiftHighGear){
				rawLeftSpeed *= Constants.kHighGearRPM;
				rawRightSpeed *= Constants.kLowGearRPM;
			}
				
			if (robot.hal.driveShifter.get().toString() == Constants.kShiftLowGear){
				rawLeftSpeed *= Constants.kLowGearRPM;
				rawRightSpeed *= Constants.kLowGearRPM;
			}
			
			gyroControl.setSetpoint(desiredAngle);
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
		//set negative so robot movement isnt inverted
		speed = -desiredSpeed;
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