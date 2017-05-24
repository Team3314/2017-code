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
	double avgEncError = 0;
	double rightDrivePosition = 0;
	double leftDrivePosition = 0;
	int leftDrivePositionTicks = 0;
	int rightDrivePositionTicks = 0;
	double leftDriveError = 0;
	double rightDriveError = 0;
	double rightDriveRPM = 0;
	double leftDriveRPM = 0;
	double absLeftSpeed = 0;
	double absRightSpeed = 0;
	double avgAbsSpeed = 0;
	
	double avgSpeed;
	
	PIDController gyroControl;
	GyroPIDOutput gyroPIDOutput;
	double multiplyer = 1;
	double shiftIteration = 0;
	
	boolean turn = false;
	
	driveMode currentMode = driveMode.IDLE;

	public TankDriveTrain(Robot myRobot) {
		robot = myRobot;
		
		gyroPIDOutput = new GyroPIDOutput();
	    gyroControl = new PIDController(Constants.kGyroLock_kP, Constants.kGyroLock_kI, Constants.kGyroLock_kD,
	    		Constants.kGyroLock_kF, robot.navx, gyroPIDOutput);
	    gyroControl.setContinuous(); //makes angle correct itself in the shortest distance
		gyroControl.setInputRange(-180, 180);
		gyroControl.setOutputRange(-.7, .7);
		gyroControl.setAbsoluteTolerance(1);
		
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
		rDriveTalon1.setInverted(true);
		lDriveTalon1.reverseSensor(true);
		rDriveTalon1.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 20);
		lDriveTalon1.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 20);
		lDriveTalon1.EnableCurrentLimit(true);
		lDriveTalon1.setCurrentLimit(50);
		lDriveTalon2.EnableCurrentLimit(true);
		lDriveTalon2.setCurrentLimit(50);
		rDriveTalon1.EnableCurrentLimit(true);
		rDriveTalon1.setCurrentLimit(50);
		rDriveTalon2.EnableCurrentLimit(true);
		rDriveTalon2.setCurrentLimit(50);
	}
	
	public void update() {
		lDriveTalon1.set(rawLeftSpeed);
		rDriveTalon1.set(rawRightSpeed);
		rightDrivePosition = rDriveTalon1.getPosition();
		leftDrivePosition = lDriveTalon1.getPosition();
		rightDrivePositionTicks = (int)(leftDrivePosition * 8192);
		leftDrivePositionTicks = (int)(rightDrivePosition * 8192);
		rightDriveError = rDriveTalon1.getClosedLoopError();
		leftDriveError = lDriveTalon1.getClosedLoopError();
		rightDriveRPM = rDriveTalon1.getSpeed();
		leftDriveRPM = lDriveTalon1.getSpeed();
		avgSpeed = (leftDriveRPM + rightDriveRPM) / 2;
		absLeftSpeed = Math.abs(lDriveTalon1.getSpeed());
		absRightSpeed = Math.abs(rDriveTalon1.getSpeed());
		avgAbsSpeed = (absLeftSpeed + absRightSpeed) /2;
		
		calcShiftMultiplyer();
		
		//talon changes mode based on tank drive state
		/*
		if (currentMode == driveMode.SPEEDCONTROL){
			lDriveTalon1.changeControlMode(TalonControlMode.Speed);
			rDriveTalon1.changeControlMode(TalonControlMode.Speed);
		} else {
			lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
			rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		}*/
		
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
			if (!gyroControl.isEnabled()){
				gyroControl.enable();
			}
		//	if (turn) {
			/*
			if (Math.abs(gyroPIDOutput.turnSpeed) < Constants.kMinTurnInput && gyroPIDOutput.turnSpeed > 0) {
				gyroPIDOutput.turnSpeed = -Constants.kMinTurnInput;
			}
			if (Math.abs(gyroPIDOutput.turnSpeed) < Constants.kMinTurnInput && gyroPIDOutput.turnSpeed < 0) {
				gyroPIDOutput.turnSpeed = Constants.kMinTurnInput;
			}*/
			
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
		leftStickInput = -leftInput;
		rightStickInput = -rightInput;
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
	
	public double calcJerk() {
		double curr_world_linear_accel_y = robot.navx.getWorldLinearAccelY();
		double currentJerkY = curr_world_linear_accel_y - last_world_linear_accel_y;
		last_world_linear_accel_y = curr_world_linear_accel_y;
		return currentJerkY;
	}

	public void resetDriveEncoders() {
		lDriveTalon1.setPosition(0);
		rDriveTalon1.setPosition(0);
		lDriveTalon1.setEncPosition(0);
		rDriveTalon1.setEncPosition(0);
	}
	public void calcShiftMultiplyer() {
		if (multiplyer < 1) {
			shiftIteration++;
			multiplyer = shiftIteration / 10;
		}
	}
	
}