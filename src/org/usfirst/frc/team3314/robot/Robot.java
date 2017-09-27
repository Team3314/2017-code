package org.usfirst.frc.team3314.robot;

import java.io.File;
import com.ctre.CANTalon.FeedbackDevice;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
RIP Again

 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {
	//Creating objects
	HardwareAbstractionLayer hal;
	HumanInput hi;
	TankDriveTrain tdt;
	ShooterStateMachine shooter;
	Cam cam;
	Turret turret;
	TrackingStateMachine tracking;
	AHRS navx = new AHRS(SPI.Port.kMXP);
	UsbCamera drivingCam;
	CustomCamera turretCam;
	
	//Auto classes - used for auto selection
	AutoNothing auto0;
	AutoShootCenterGearDriveFeeder auto1;
	AutoGearCenter auto2;
	AutoGearLeft auto3;
	AutoGearRight auto4;
	AutoShootTenGearDrive auto5;
	AutoShootTen auto6;
	AutoDriveToHopperShoot auto7;
	AutoShootCenterGear auto8;
	boolean auto0Request;
	boolean auto1Request;
	boolean auto2Request;
	boolean auto3Request;
	boolean auto4Request;
	boolean auto5Request;
	boolean auto6Request;
	boolean auto7Request;
	boolean auto9Request;
	
	//Creating button variables
	boolean fuelIntakeRequest;
	boolean gyroLockRequest;
	boolean highGearRequest;
	boolean lowGearRequest;
	boolean spinShooterRequest;
	boolean shootRequest = false;
	boolean flashlightRequest;
	boolean turretTrackRequest = false;
	boolean lastGyroLock = false;
	boolean feedShooterRequest = false; 
	boolean climberRequest = false;
	boolean turnShooterLeftRequest = false;
	boolean turnShooterForwardRequest = false;
	boolean turnShooterRightRequest = false;
	boolean enableTurretTrackingRequest = false;
	boolean zeroCamRequest = false;
	boolean setShooterCloseRequest = false;
	boolean setShooterFarRequest = false;
	boolean ringLightRequest = false;
	boolean incrementTurretLeftRequest = false;
	boolean incrementTurretRightRequest = false;
	boolean incrementSpeedUpRequest = false;
	boolean incrementSpeedDownRequest = false;
	boolean incrementCamPositionDownRequest = false;
	boolean incrementCamPositionUpRequest = false;
	public boolean lastShooterIncrement;
	public boolean lastCamIncrement;
	public boolean lastTurretLeftIncrement;
	public boolean lastTurretRightIncrement;
	public boolean lastCamDownIncrement;
	public boolean lastCamUpIncrement;
	public boolean lastShooterUpIncrement;
	public boolean lastShooterDownIncrement;
	boolean openGearIntakeRequest = false;
	boolean closeGearIntakeRequest = false;
	boolean zeroTurretRequest = false;
	boolean turnCamNearZeroRequest = false;
	boolean redRequest = false;
	boolean blueRequest = false;
	boolean enableDistanceCheckingRequest = false;
	boolean reverseIndexRequest;
	boolean reverseAgitatorRequest;
	boolean resetGyroRequest;
	boolean lastTurretIncrement;
	
	//Auto switch inputs
	boolean binaryOne = false;
	boolean binaryTwo = false;
	boolean binaryFour = false;
	boolean binaryEight = false;
	int autoSelect = 0;
	
	double absolutePosition;
	double last_world_linear_accel_y;
	double time = 0;	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Instantiating objects
		hal = new HardwareAbstractionLayer(this);
		hi = new HumanInput();
		tdt = new TankDriveTrain(this);
		shooter = new ShooterStateMachine(this);
		cam = new Cam(this);
		turret = new Turret(this);
		turretCam = new CustomCamera(this);
		tracking = new TrackingStateMachine(this);
		
		//Instantiating auto objects
		auto0 = new AutoNothing(this);
		auto1 = new AutoShootCenterGearDriveFeeder(this);
		auto2 = new AutoGearCenter(this);
		auto3 = new AutoGearLeft(this);
		auto4 = new AutoGearRight(this);
		auto5 = new AutoShootTenGearDrive(this); 
		auto6 = new AutoShootTen(this);
		auto7 = new AutoDriveToHopperShoot(this);
		auto8 = new AutoShootCenterGear(this);
		
		//Resets drive encoders to zero
		tdt.resetDriveEncoders();
		
	}
	
	public void disabledInit() {
		//Resets cam and drive encoders to zero, sets gyro tolerance 
		hal.camTalon.setPosition(0);
		hal.camTalon.setPulseWidthPosition(0);
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		tdt.resetDriveEncoders();
		tdt.gyroControl.setAbsoluteTolerance(1);
	}
	
	//Runs every 20 ms while the robot is disabled
	public void disabledPeriodic() {
		//Allows for button inputs
		updateButtonStatus();
		//Resets turret positon to zero, allows for change of turret postion while disabled
		if (zeroTurretRequest) {
			hal.turretTalon.setPosition(0);
			turret.desiredTarget = 0;
		}
		//Resets gyro on request
		if (resetGyroRequest) {
			navx.reset();
		}
		//Prints variables to smart dashboard for diagnosing issues 
		SmartDashboard.putNumber("Auto Selection", autoSelect);
		SmartDashboard.putNumber("Gyro Angle", navx.getYaw());
		SmartDashboard.putNumber("DPad", hi.operator.getPOV());
		SmartDashboard.putNumber("Turret Error", hal.turretTalon.getClosedLoopError());
		SmartDashboard.putNumber("Turret Position", hal.turretTalon.getPosition());
		SmartDashboard.putNumber("Relative Cam Positon",hal.camTalon.getPosition());
		SmartDashboard.putNumber("Absolute Cam Positon", hal.camTalon.getPulseWidthPosition());
		SmartDashboard.putNumber("Desired Cam Position", cam.desiredPosition);
		SmartDashboard.putNumber("Average Encoder Position", tdt.avgEncPos);
		SmartDashboard.putNumber("Left Encoder Position", tdt.lDriveTalon1.getPosition());
		SmartDashboard.putNumber("Right Encoder Position", tdt.rDriveTalon1.getPosition());
		SmartDashboard.putNumber("PDP Temperature", hal.pdp.getTemperature());
		
		//Takes absolute positon of the encoder and subtracts 944 so that zero is when the shot is vertical
		//Sets the target positon to the current positon so that the desired positon cannot change while the robot is disabled
		cam.desiredPosition = (hal.camTalon.getPulseWidthPosition() - 944);
		hal.camTalon.setPosition(cam.desiredPosition);
		
		//Looks at four binary outputs of auto selector and converts them to a base ten number, which is used to select auto
		if (!binaryEight && !binaryFour && !binaryTwo && !binaryOne) {
			autoSelect = 0;
		}
			
		if (!binaryEight && !binaryFour && !binaryTwo && binaryOne) {
			autoSelect = 1;
		}
		
		if (!binaryEight && !binaryFour && binaryTwo && !binaryOne) {
			autoSelect = 2;
		}
		
		if (!binaryEight && !binaryFour && binaryTwo && binaryOne) {
			autoSelect = 3;
		}
		
		if (!binaryEight && binaryFour && !binaryTwo && !binaryOne) {
			autoSelect = 4;
		}
		
		if (!binaryEight && binaryFour && !binaryTwo && binaryOne) {
			autoSelect = 5;
		}
		
		if (!binaryEight && binaryFour && binaryTwo && !binaryOne) {
			autoSelect = 6;
		}
		
		if (!binaryEight && binaryFour && binaryTwo && binaryOne) {
			autoSelect = 7;
		}
		
		if (binaryEight && !binaryFour && !binaryTwo && !binaryOne) {
			autoSelect = 8;
		}
		if (binaryEight && !binaryFour && !binaryTwo && binaryOne) {
			autoSelect = 9;
		}
	}
	
	@Override
	public void autonomousInit() {
		//Sets drivetrain mode to tank and low gear, starts PID loop for gyro lock
		tdt.setDriveMode(driveMode.TANK);
		tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , Constants.kGyroLock_kD);
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear)); //Allows for greater control and better stopping distance
		//Makes sure encoder position of cam is the same as the desired position so cam does not move when enabled, resets turret
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		hal.camTalon.setEncPosition((int) cam.desiredPosition);
		turret.reset();
		//Resets drive encoders and gyro to zero, sets drive motor speeds to zero
		navx.reset();
		tdt.resetDriveEncoders();
		tdt.setDriveTrainSpeed(0);
		//Resets the currently selected auto to the starting state
		if (autoSelect == 0) {
			auto0.reset();
		}
			
		if (autoSelect == 1) {
			auto1.reset();
		}
		
		if (autoSelect == 2) {
			auto2.reset();
		}
		
		if (autoSelect == 3) {
			auto3.reset();
		}
		
		if (autoSelect == 4) {
			auto4.reset();
		}
		
		if (autoSelect == 5) {
			auto5.reset();
		}
		
		if (autoSelect == 6) {
			auto6.reset();
		}
		
		if (autoSelect == 7) {
			auto7.reset();
		}
		
		if (autoSelect == 8) {
			auto8.reset();
		}
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		tdt.avgEncPos = (tdt.lDriveTalon1.getPosition() + tdt.rDriveTalon1.getPosition()) / 2;
		//Displays selected auto, current auto state, and runs auto
		if (autoSelect == 0) {
			SmartDashboard.putString("Auto Mode", "Nothing");
			SmartDashboard.putString("Auto State", auto0.currentState.toString());
			auto0.update();
		}
			
		if (autoSelect == 1) {
			SmartDashboard.putString("Auto Mode", "Cross Baseline");
			SmartDashboard.putString("Auto State", auto1.currentState.toString());
			auto1.update();
		}
		
		if (autoSelect == 2) {
			SmartDashboard.putString("Auto Mode", "Side Gear");
			SmartDashboard.putString("Auto State", auto2.currentState.toString());
			auto2.update();
		}
		
		if (autoSelect == 3) {
			SmartDashboard.putString("Auto Mode", "Shoot Side Drive");
			SmartDashboard.putString("Auto State", auto3.currentState.toString());
			auto3.update();
		}
		
		if (autoSelect == 4) {
			SmartDashboard.putString("Auto Mode", "Shoot Ten");
			SmartDashboard.putString("Auto State", auto4.currentState.toString());
			auto4.update();
		}
		
		if (autoSelect == 5) {
			SmartDashboard.putString("Auto Mode", "Shoot Ten Gear");
			SmartDashboard.putString("Auto State", auto5.currentState.toString());
			auto5.update();
		}
		
		if (autoSelect == 6) {
			SmartDashboard.putString("Auto Mode", "Shoot Ten Gear Drive");
			SmartDashboard.putString("Auto State", auto6.currentState.toString());
			auto6.update();
		}
		
		if (autoSelect == 7) {
			SmartDashboard.putString("Auto Mode", "Gear Drive to Hopper Shoot");
			SmartDashboard.putString("Auto State", auto7.currentState.toString());
			auto7.update();
		}
		
		if (autoSelect == 8) {
			SmartDashboard.putString("Auto Mode", "Shoot Center Gear");
			SmartDashboard.putString("Auto State", auto8.currentState.toString());
			auto8.update();
		}
		//Updates status of various objects
		tdt.update();
		shooter.update();
		turret.update();
		cam.update();
		turretCam.update();
		tracking.update();
		
		//Removes I constant of gyro PID while difference between current angle and target angle is greater than 20 degrees.
		//Prevents overaccumulation of I in PID output, which results in overshoot
		if(Math.abs(tdt.desiredAngle - navx.getYaw()) > 20 ) {
			tdt.gyroControl.setPID(Constants.kGyroLock_kP, 0, Constants.kGyroLock_kD);
		}
		else if (Math.abs(tdt.desiredAngle - navx.getYaw()) <= 20 ) {
			tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , Constants.kGyroLock_kD);
		}
	
		//Prints variables to smartdashboard, helps in diagnosis of issues
		SmartDashboard.putNumber("Distance", tdt.avgEncPos / Constants.kInToRevConvFactor);
		SmartDashboard.putNumber("Left 1 Voltage", tdt.lDriveTalon1.getOutputVoltage());
		SmartDashboard.putNumber("Left 2 Voltage", tdt.lDriveTalon2.getOutputVoltage());
		SmartDashboard.putNumber("Right 1 Voltage", tdt.rDriveTalon1.getOutputVoltage());
		SmartDashboard.putNumber("Right 2 Voltage", tdt.rDriveTalon2.getOutputVoltage());
		SmartDashboard.putNumber("Left 1 current", tdt.lDriveTalon1.getOutputCurrent());
		SmartDashboard.putNumber("Left 2 current", tdt.lDriveTalon2.getOutputCurrent());
		SmartDashboard.putNumber("Right 1 current", tdt.rDriveTalon1.getOutputCurrent());
		SmartDashboard.putNumber("Right 2 current", tdt.rDriveTalon2.getOutputCurrent());
		SmartDashboard.putString("Shooter state", shooter.currentState.toString());
		SmartDashboard.putNumber("Gyro Angle", navx.getYaw());
		SmartDashboard.putNumber("Desired Angle", tdt.desiredAngle);
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    
		SmartDashboard.putString("Drive state", tdt.currentMode.toString());
		SmartDashboard.putNumber("Average Encoder Position", tdt.avgEncPos);
		SmartDashboard.putNumber("Turret Error", hal.turretTalon.getClosedLoopError());
		SmartDashboard.putNumber("Target Turret Position", hal.turretTalon.getSetpoint());
		SmartDashboard.putNumber("Turret Position", hal.turretTalon.getEncPosition());
		SmartDashboard.putNumber("Cam Error",hal.camTalon.getClosedLoopError());
		SmartDashboard.putNumber("Cam Position",hal.camTalon.getEncPosition());
		SmartDashboard.putNumber("Cam Setpoint", hal.camTalon.getSetpoint());
		SmartDashboard.putNumber("Cam Target Position" , cam.desiredPosition);
		SmartDashboard.putNumber("PDP Temperature", hal.pdp.getTemperature());
		SmartDashboard.putNumber("D", tdt.gyroControl.getD());
		
		}
	
	//Runs when robot is enabled into teleop
	public void teleopInit() {
		//Sets cam to relative mode and sets current position to desired position to prevent movement of cam upon enable
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		hal.camTalon.setEncPosition((int) cam.desiredPosition);
		//Prevents robot from shooting when entering teleop
		shootRequest = false;
		turret.update();
		tdt.resetDriveEncoders();
		tdt.setDriveMode(driveMode.TANK);
		//Prevents turret from moving when enabled
		turret.desiredTarget = turret.turretPosition;
		navx.reset();
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		//Removes D term in teleop gyro PID to prevent oscillations
		tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , 0);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		//Joystick input to drivetrain (Multiplyer slows robot down when shifting to help robot shift)
		tdt.setStickInputs(hi.leftStick.getY()  * tdt.multiplyer, hi.rightStick.getY() *tdt.multiplyer); 

		tdt.update();
		cam.update();
		shooter.update();
		turret.update();
		turretCam.update();
		tracking.update();
		updateButtonStatus();
		
		//Executes commands upon button presses
		//Runs vision program and turns turret
		if (enableDistanceCheckingRequest) {
			 turretCam.calcDistance();
			 turretCam.distanceCheck();
		}
		if (resetGyroRequest) {
			navx.reset();
		}
		//Runs ball intake
		if(fuelIntakeRequest) {
			hal.upperIntakeSpark.set(1);
		} else if (!fuelIntakeRequest) {
			hal.upperIntakeSpark.set(0); 
		}

		if (gyroLockRequest) {
			if (!lastGyroLock) {
				tdt.setDriveMode(driveMode.GYROLOCK);
				tdt.setDriveAngle(navx.getYaw()); //makes sure robot will move straight - Sets desired angle to current angle
			}
			tdt.setDriveTrainSpeed(tdt.rightStickInput); //Sets both sides of drive train to output of right stick - Helps robot drive straight
			}
		else {
			tdt.setDriveMode(driveMode.TANK);
			tdt.gyroControl.disable(); // Disables gyro PID when gyrolock is disabled
		}

		if (highGearRequest) {
			hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
		}
		//Slows robot down when shifting into low gear
		 if (lowGearRequest && hal.driveShifter.get().toString() == Constants.kShiftHighGear) {
			hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
			//Reverses robot slightly when travelling at very low speed to take pressure off gearbox in pushing match
			if (Math.abs(tdt.avgSpeed) < 20) {
				tdt.multiplyer = -.2;
				tdt.shiftIteration = -2;
			}
			//Stops robot if travelling at moderate speed
			else if (Math.abs(tdt.avgSpeed ) < 500) {
				tdt.multiplyer = 0;
				tdt.shiftIteration = 0;
			}
			 //Only slows robot to half speeed if travelling at speed to prevent harsh stops
			else {
				tdt.multiplyer = .5;
				tdt.shiftIteration = 5;
			}
		}

		if (lowGearRequest) {
			hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		}

		if (spinShooterRequest) {
			hal.shooterTalon.set(shooter.desiredSpeed);
		}
		else if (!spinShooterRequest && !shootRequest) {
			hal.shooterTalon.set(0);
		}

		if(flashlightRequest) {
			hal.flashlight.set(Constants.kFlashlightOn);
		} else  {
			hal.flashlight.set(Constants.kFlashlightOff);
		}
		if (feedShooterRequest) {
			hal.upperIndexSpark.set(1);
			hal.lowerIndexSpark.set(1);
			hal.agitatorSpark.set(1);
		}
		else if(!feedShooterRequest && !shootRequest && !hi.buttonBox.getRawButton(1) && !hi.buttonBox.getRawButton(2)){
			hal.upperIndexSpark.set(0);
			hal.lowerIndexSpark.set(0);
			hal.agitatorSpark.set(0);
		}
		if (turnShooterLeftRequest) {
			turret.desiredTarget = 0;
			turretTrackRequest = false;
		}
		else if (turnShooterForwardRequest) {
			turret.desiredTarget = 3.6;
			turretTrackRequest = false;
		}
		else if (turnShooterRightRequest) {
			turret.desiredTarget = 7;
			turretTrackRequest = false;
		}
		else if (enableTurretTrackingRequest) {
			turretTrackRequest = true;
		}
		else {
			turretTrackRequest = false;
		}

		if (zeroCamRequest) {
			cam.desiredPosition = 0;
		}
		if (climberRequest) {
			hal.climberSpark.set(-1);
		}

		else if (hi.runClimberReverse()) {
			hal.climberSpark.set(1);
		}
		else if (!hi.runClimber() && !hi.runClimberReverse()) {
			hal.climberSpark.set(0);
		}
		//Set different values for shooter speed and cam position
		if (setShooterCloseRequest) {
			cam.desiredPosition = Constants.kCamClosePosition;
			shooter.desiredSpeed = Constants.kShooterCloseSpeed;
		}

		else if (setShooterFarRequest) {
			cam.desiredPosition = Constants.kCamFarPosition;
			shooter.desiredSpeed = Constants.kShooterFarSpeed;
		}
		else if (hi.setShooterManual()) {
			cam.desiredPosition = ((hi.rightStick.getZ() + 1) / 2)*4096;
			shooter.desiredSpeed = (((hi.leftStick.getZ() + 1) /2)*6150);
		}
		else if (hi.getHopperShot()) {
			if (redRequest) {
				turret.desiredTarget = .1;
				cam.desiredPosition = 1376;
				shooter.desiredSpeed = 5150;
			}
			else if (blueRequest) {
				turret.desiredTarget = 7.2;
				cam.desiredPosition = 1376;
				shooter.desiredSpeed = 5450;
			}
		}
		if (ringLightRequest || turretTrackRequest) {
			hal.ringLight.set(true);
		}
		else {
			hal.ringLight.set(false);
		}
		if (reverseAgitatorRequest) {
			hal.agitatorSpark.set(-1);   
		}
		else if(!reverseAgitatorRequest && !feedShooterRequest && !shootRequest) {
			hal.agitatorSpark.set(0);
		}
		//
		if (reverseIndexRequest) {
			hal.shooterTalon.set(-shooter.desiredSpeed);
			hal.upperIndexSpark.set(-1);
			hal.lowerIndexSpark.set(-1);
		}
		else if(!reverseIndexRequest && !feedShooterRequest && !shootRequest) {
			hal.lowerIndexSpark.set(0);
		}
		if (openGearIntakeRequest) {
		hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
		}
		else if (closeGearIntakeRequest) {
			hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		//Allows manual fine adjustment of turret position, shooter speed and cam position
		if (incrementCamPositionUpRequest && !lastCamUpIncrement) {
			if (cam.desiredPosition <4096) {
				cam.desiredPosition = cam.desiredPosition + 32;
			}
		}
		if (incrementCamPositionDownRequest && !lastCamDownIncrement) {
			if (cam.desiredPosition > 0) {
				cam.desiredPosition = cam.desiredPosition - 32;
			}
		}
		if (incrementSpeedUpRequest && !lastShooterUpIncrement) {
			if (shooter.desiredSpeed < 6150) {
				shooter.desiredSpeed = shooter.desiredSpeed + 100;
			}
		}
		if (incrementSpeedDownRequest && !lastShooterDownIncrement) {
			if (shooter.desiredSpeed > 0) {
				shooter.desiredSpeed = shooter.desiredSpeed - 100;
			}
		}
		if (incrementTurretLeftRequest && !lastTurretLeftIncrement) {
			if (turret.desiredTarget > 0) {
				turret.desiredTarget = turret.desiredTarget - .1;
			}
		}
		if (incrementTurretRightRequest && !lastTurretRightIncrement) {
			if (turret.desiredTarget <= 7.5) {
				turret.desiredTarget = turret.desiredTarget + .1;
			}
		}
		//Updates variables that store previous button state
		lastCamUpIncrement = incrementCamPositionUpRequest;
		lastCamDownIncrement = incrementCamPositionDownRequest;
		lastShooterUpIncrement = incrementSpeedUpRequest;
		lastShooterDownIncrement = incrementSpeedDownRequest;
		lastTurretLeftIncrement = incrementTurretLeftRequest;
		lastTurretRightIncrement = incrementTurretRightRequest;
		lastGyroLock = gyroLockRequest;
    	
   		SmartDashboard.putNumber("Gyro Angle", navx.getYaw());
   		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
   		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
       		SmartDashboard.putString("Drive state", tdt.currentMode.toString());
   		SmartDashboard.putNumber("PID setpoint", tdt.gyroControl.getSetpoint());
   	 
   		SmartDashboard.putNumber("Left RPM", tdt.leftDriveRPM);
    		SmartDashboard.putNumber("Right RPM", tdt.rightDriveRPM); 
    	
   		SmartDashboard.putString("LeftDriveMode", tdt.lDriveTalon1.getControlMode().toString());
    		SmartDashboard.putString("RightDriveMode", tdt.rDriveTalon1.getControlMode().toString());
  
    	
    		SmartDashboard.putBoolean("Cam Increment Down", incrementCamPositionDownRequest);
       		SmartDashboard.putBoolean("Cam Increment Up", incrementCamPositionUpRequest);
       		SmartDashboard.putBoolean("Speed Increment Down", incrementSpeedDownRequest);
       		SmartDashboard.putBoolean("Speed Increment Up", incrementSpeedUpRequest);
       		SmartDashboard.putNumber("Left 1", tdt.lDriveTalon1.get());
        	SmartDashboard.putNumber("Left 2 ", tdt.lDriveTalon2.get());
        	SmartDashboard.putNumber("Right 1 ", tdt.rDriveTalon1.get());
        	SmartDashboard.putNumber("Right 2 ", tdt.rDriveTalon2.get());
       	
        	SmartDashboard.putNumber("Average Encoder Position", tdt.avgEncPos);
        	SmartDashboard.putNumber("Left Encoder Position", tdt.lDriveTalon1.getPosition());
        	SmartDashboard.putNumber("Right Encoder Position", tdt.rDriveTalon1.getPosition());
        	
        	SmartDashboard.putNumber("Desired Speed", tdt.desiredSpeed);
        	SmartDashboard.putNumber("Desired Angle", tdt.desiredAngle);
        	SmartDashboard.putBoolean("lastGyroLock", lastGyroLock);
        	
        	SmartDashboard.putBoolean("Pressure Switch", hal.pcm1.getPressureSwitchValue());
        	SmartDashboard.putString("Shooter State", shooter.currentState.toString());
        	
        	SmartDashboard.putNumber("Cam position", hal.camTalon.getPosition());
        	SmartDashboard.putNumber ("Desired Cam Position", cam.desiredPosition);
        	SmartDashboard.putNumber("Target Cam Position", hal.camTalon.getSetpoint() * 8192);
        	SmartDashboard.putNumber("Cam Error", hal.camTalon.getClosedLoopError());
        	
        	SmartDashboard.putNumber("Cam Voltage", hal.camTalon.getOutputVoltage());
        	SmartDashboard.putNumber("Cam Current", hal.camTalon.getOutputCurrent());
        	
        	SmartDashboard.putString("Cam control mode", hal.camTalon.getControlMode().toString());
        	
        	SmartDashboard.putNumber("Turret Position", hal.turretTalon.getPosition());
        	SmartDashboard.putNumber("Target Turret Position", hal.turretTalon.getSetpoint());
        	SmartDashboard.putNumber("Lower Roller Input", hal.lowerIndexSpark.get());

        	SmartDashboard.putBoolean("Intake Spark", hal.intakeSpark.isAlive());        	
        	SmartDashboard.putString("Gear Intake state", hal.gearIntake.get().toString());
        	SmartDashboard.putNumber("Shooter RPM", shooter.shooterSpeed);
        	SmartDashboard.putNumber("Shooter Voltage", hal.shooterTalon.getOutputVoltage());
        	SmartDashboard.putNumber("Shooter Current", hal.shooterTalon.getOutputCurrent());
        	SmartDashboard.putNumber("PDP Temperature", hal.pdp.getTemperature());
        	SmartDashboard.putNumber("Target ShooterRPM" ,(((hi.leftStick.getZ() + 1) /2)*6500));
        	
        	SmartDashboard.putNumber("Shooter Error", hal.shooterTalon.getSetpoint());
        	
        	SmartDashboard.putNumber("Get Agitator", hal.agitatorSpark.get());
        	SmartDashboard.putNumber("Lower Index Get", hal.lowerIndexSpark.get());
        	SmartDashboard.putNumber("Upper Index get", hal.upperIndexSpark.get());
        	SmartDashboard.putBoolean("Shoot Request", shootRequest);
        	SmartDashboard.putNumber("Desired Shooter Speed", shooter.desiredSpeed);
        	
        	SmartDashboard.putNumber("Acceleration X", navx.getWorldLinearAccelX());
        	SmartDashboard.putNumber("Acceleration Y", navx.getWorldLinearAccelY());
        	SmartDashboard.putNumber("left stick input", hi.leftStick.getY());
    		SmartDashboard.putNumber("right stick input", hi.rightStick.getY());
    		SmartDashboard.putBoolean("Red", redRequest);
    		SmartDashboard.putBoolean("Blue", blueRequest);
    		
    		SmartDashboard.putNumber("Relative Cam Positon", hal.camTalon.getPosition());
    		SmartDashboard.putNumber("Absolute Cam Positon", hal.camTalon.getPulseWidthPosition());
    		SmartDashboard.putNumber("Desired Cam Position", cam.desiredPosition);
    		
    		SmartDashboard.putNumber("Distance", turretCam.calcDistance());
    		SmartDashboard.putNumber("Angle of Theta", turretCam.theta);
    		SmartDashboard.putNumber("Turn Error", turretCam.turnError);
    		SmartDashboard.putNumber("Yaw", turretCam.calcTurretYaw());
    		SmartDashboard.putNumber("Target yaw", tracking.targetYaw);
    		SmartDashboard.putNumber("Target Turret Position", turret.desiredTarget);
    		
    		SmartDashboard.putNumber("Left 1 Input Voltage", tdt.lDriveTalon1.getBusVoltage());
        	SmartDashboard.putNumber("Left 2 Input Voltage", tdt.lDriveTalon2.getBusVoltage());
        	SmartDashboard.putNumber("Right 1 Input Voltage", tdt.rDriveTalon1.getBusVoltage());
        	SmartDashboard.putNumber("Right 2 Input Voltage", tdt.rDriveTalon2.getBusVoltage());
        	
        	SmartDashboard.putNumber("Left 1 Output Voltage", tdt.lDriveTalon1.getOutputVoltage());
        	SmartDashboard.putNumber("Left 2 Output Voltage", tdt.lDriveTalon2.getOutputVoltage());
        	SmartDashboard.putNumber("Right 1 Output Voltage", tdt.rDriveTalon1.getOutputVoltage());
        	SmartDashboard.putNumber("Right 2 Output Voltage", tdt.rDriveTalon2.getOutputVoltage());
        	
           	SmartDashboard.putNumber("Left 1 Output Current", tdt.lDriveTalon1.getOutputCurrent());
           	SmartDashboard.putNumber("Left 2 Output Current", tdt.lDriveTalon2.getOutputCurrent());
           	SmartDashboard.putNumber("Right 1 Output Current", tdt.rDriveTalon1.getOutputCurrent());
           	SmartDashboard.putNumber("Right 2 Output Current", tdt.rDriveTalon2.getOutputCurrent());
           	
           	SmartDashboard.putNumber("PDP Left 1 Output Current" , hal.pdp.getCurrent(15));
           	SmartDashboard.putNumber("PDP Left 2 Output Current", hal.pdp.getCurrent(13));
           	SmartDashboard.putNumber("PDP Right 1 Output Current", hal.pdp.getCurrent(14));
           	SmartDashboard.putNumber("PDP Right 2 Output Current", hal.pdp.getCurrent(12));
           	
           	SmartDashboard.putNumber("Multiplyer", tdt.multiplyer);
           	
    }
		
	
	@Override
	
	public void testInit() {
		
	}
	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		//Allows for button input when the robot is in test mode
		updateButtonStatus();
		SmartDashboard.putNumber("Turret Position", hal.turretTalon.getEncPosition());
		SmartDashboard.putNumber("left stick input", hi.leftStick.getY());
		SmartDashboard.putNumber("right stick input", hi.rightStick.getY());
		
		
	}
	
	public void updateButtonStatus() {
		//Checks outputs of buttons and checks if they have been pressed
		fuelIntakeRequest = hi.getFuelIntake();
		gyroLockRequest = hi.getGyroLock();
		feedShooterRequest = hi.feedShooter();
		highGearRequest = hi.getHighGear();
		lowGearRequest = hi.getLowGear();
		spinShooterRequest = hi.getSpinShooter();
		shootRequest = hi.getShoot();
		flashlightRequest = hi.getFlashlight();
		turnShooterLeftRequest = hi.turnShooterLeft();
		turnShooterForwardRequest = hi.turnShooterForward();
		turnShooterRightRequest = hi.turnShooterRight();
		enableTurretTrackingRequest = hi.enableTurretTracking();
		enableDistanceCheckingRequest = hi.enableDistanceChecking();
		zeroCamRequest = hi.zeroCam();
		setShooterCloseRequest = hi.setShooterClose();
		setShooterFarRequest = hi.setShooterFar();
		binaryOne = hi.getBinaryOne();
		binaryTwo = hi.getBinaryTwo();
		binaryFour = hi.getBinaryFour();
		binaryEight = hi.getBinaryEight();	
		ringLightRequest = hi.getRingLight();
		blueRequest = hi.getBlue();
		redRequest = hi.getRed();
		openGearIntakeRequest = hi.getOpenGearIntake();
		closeGearIntakeRequest = hi.getCloseGearIntake();
		climberRequest = hi.runClimber();
		resetGyroRequest = hi.gyroReset();
		zeroTurretRequest = hi.zeroTurret();
		reverseIndexRequest = hi.getReverseIndex();
		reverseAgitatorRequest = hi.getReverseAgitator();
		incrementTurretLeftRequest = hi.incrementTurretLeft();
		incrementTurretRightRequest = hi.incrementTurretRight();
		incrementSpeedUpRequest = hi.incrementSpeedUp();
		incrementSpeedDownRequest = hi.incrementSpeedDown();
		incrementCamPositionUpRequest = hi.incrementCamPositionUp();
		incrementCamPositionDownRequest = hi.incrementCamPositonDown();
				
	}
	
	
	
}
