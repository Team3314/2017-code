package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon;
import com.ctre.CANTalon.*;
import edu.wpi.first.wpilibj.PIDController;

enum driveMode {
	IDLE,
	TANK,
	GYROLOCK,
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
		//Sets up gyro PID controller
		gyroPIDOutput = new GyroPIDOutput();
	    	gyroControl = new PIDController(Constants.kGyroLock_kP, Constants.kGyroLock_kI, Constants.kGyroLock_kD,
	    		Constants.kGyroLock_kF, robot.navx, gyroPIDOutput);
		//Sets the PID controller to treat 180 and -180 to be the same point, 
		//so that when turning the robot takes the shortest path instead of going the long way around
		//Effectively changes PID input from a line to a circle
	    	gyroControl.setContinuous(); 
		gyroControl.setInputRange(-180, 180);
		gyroControl.setOutputRange(-.7, .7);		// Limits speed of turn to prevent overshoot
		gyroControl.setAbsoluteTolerance(1); //Stop robot from oscilating by target by cutting output once within 1 degree
		
		rDriveTalon1 = new CANTalon(1);
		rDriveTalon2 = new CANTalon(3);
		lDriveTalon1 = new CANTalon(0);
		lDriveTalon2 = new CANTalon(2);
		
		//Makes sure the motors on each side of the drive train are set to the same speed by setting one talon to follow the other
		lDriveTalon2.changeControlMode(TalonControlMode.Follower);
		lDriveTalon2.set(lDriveTalon1.getDeviceID());
		rDriveTalon2.changeControlMode(TalonControlMode.Follower);
		rDriveTalon2.set(rDriveTalon1.getDeviceID());
		
		lDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		rDriveTalon1.changeControlMode(TalonControlMode.PercentVbus);
		//Sets up drive encoders
		lDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		lDriveTalon1.configEncoderCodesPerRev(2048);
		rDriveTalon1.configEncoderCodesPerRev(2048);
		rDriveTalon1.setInverted(true);
		lDriveTalon1.reverseSensor(true);
		rDriveTalon1.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 20);
		lDriveTalon1.setStatusFrameRateMs(CANTalon.StatusFrameRate.QuadEncoder, 20);
		//Prevents breaker trips by restricting the talons to drawing 50 amps
		lDriveTalon1.EnableCurrentLimit(true);
		lDriveTalon1.setCurrentLimit(50);
		lDriveTalon2.EnableCurrentLimit(true);
		lDriveTalon2.setCurrentLimit(50);
		rDriveTalon1.EnableCurrentLimit(true);
		rDriveTalon1.setCurrentLimit(50);
		rDriveTalon2.EnableCurrentLimit(true);
		rDriveTalon2.setCurrentLimit(50);
	}
	
	/*Runs every 20ms while robot is enabled, updating the mode of and input to the drivetrain, as well as 
	updating variables providing information about the drivetrain*/
	public void update() {
		lDriveTalon1.set(rawLeftSpeed);
		rDriveTalon1.set(rawRightSpeed);
		//Drive train position in rotiations
		rightDrivePosition = rDriveTalon1.getPosition();
		leftDrivePosition = lDriveTalon1.getPosition();
		//Drive train position in encoder ticks
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
		
		
		switch(currentMode){
		case IDLE:
			//Drivetrain stopped, does not accept joystick input
			rawLeftSpeed = 0;
			rawRightSpeed = 0;
			break;
		case TANK:
			//Left and right drivetrain speed set by left and right joysticks
			rawLeftSpeed = leftStickInput;
			rawRightSpeed = rightStickInput;
			break;
		case GYROLOCK:
 			//Left and right speed set by right joystick, with gyro keeping the robot pointed straight
			if (!gyroControl.isEnabled()){
				gyroControl.enable();
			}
			
			rawLeftSpeed = desiredSpeed + gyroPIDOutput.turnSpeed;
			rawRightSpeed = desiredSpeed - gyroPIDOutput.turnSpeed;
			gyroControl.setSetpoint(desiredAngle);	
			break;
		}
	}
	
	public void setStickInputs(double leftInput, double rightInput) {
		//Method that maps joystick inputs to variables to be used in setting drivetrain speed
		/*Inputs are made negative because forward on the joysticks used was negative and backwards was negative
		 forward positive and backward negative makes more sense*/
		leftStickInput = -leftInput;
		rightStickInput = -rightInput;
	}
	//Method used to switch between drive modes (Tank, gyrolock, etc.)
	public void setDriveMode(driveMode mode) {
		currentMode = mode;
	}
	//Used in gyrolock to give both sides the same input
	public void setDriveTrainSpeed(double speed) {	
		desiredSpeed = speed;
	}
	
	public void setDriveAngle(double angle) {
		desiredAngle = angle;
	}
	//Sets position of drive encoders to zero, usually used in auto between drive states so the robot always starts at zero
	public void resetDriveEncoders() {
		lDriveTalon1.setPosition(0);
		rDriveTalon1.setPosition(0);
		lDriveTalon1.setEncPosition(0);
		rDriveTalon1.setEncPosition(0);
	}
	/* Runs every 20ms in update, and when multiplyer (used to slow robot when shifting) is set to a number below 1
	via a button press, it is incremented back up to 1, gradually accelerating the robot back up to speed*/
	public void calcShiftMultiplyer() {
		if (multiplyer < 1) {
			shiftIteration++;
			multiplyer = shiftIteration / 10;
		}
	}
	
}
