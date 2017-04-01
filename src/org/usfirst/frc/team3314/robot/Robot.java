package org.usfirst.frc.team3314.robot;

import java.io.File;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
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
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {
	//some classes
	HardwareAbstractionLayer hal;
	HumanInput hi;
	TankDriveTrain tdt;
	ShooterStateMachine shooter;
	//MotionProfile profile;
	
	//File myFile = new File("myfile.csv");
	
	//auto classes
	AutoNothing auto0;
	AutoCrossBaseline auto1;
	AutoGearCenter auto2;
	AutoGearLeft auto3;
	AutoGearRight auto4;
	AutoShootTenGearDrive auto5;
	AutoShootTen auto6;
	AutoShootTenGear auto7;
	AutoDriveToHopperShoot auto8;
	//AutoGearDriveToHopperShoot auto9;
	//MotionProfile auto9;
	
	
	//misc
	CamStateMachine cam;
	Turret turret;
	TrackingStateMachine tracking;
	AHRS navx = new AHRS(SPI.Port.kMXP);
	UsbCamera drivingCam;
	CustomCamera turretCam;
	
	//button input
	boolean raiseGearIntakeRequest;
	boolean dropGearIntakeRequest;
	boolean fuelIntakeRequest;
	boolean gyroLockRequest;
	boolean speedControlRequest;
	boolean highGearRequest;
	boolean lowGearRequest;
	boolean spinShooterRequest;
	boolean shootRequest = false;
	boolean flashlightRequest;
	
	boolean turretTrackRequest = false;
	boolean lastGyroLock = false;
	boolean lastSpeedControl = false;
	
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
	
	boolean binaryOne = false;
	boolean binaryTwo = false;
	boolean binaryFour = false;
	boolean binaryEight = false;
	
	int autoSelect = 0;
	
	boolean openGearIntakeRequest = false;
	boolean closeGearIntakeRequest = false;
	
	boolean auto0Request;
	boolean auto1Request;
	boolean auto2Request;
	boolean auto3Request;
	boolean auto4Request;
	boolean auto5Request;
	boolean auto6Request;
	boolean auto7Request;
	boolean auto8Request;
	boolean auto9Request;
	
	boolean zeroTurretRequest = false;
	
	boolean redRequest = false;
	boolean blueRequest = false;
	
	boolean turnCamNearZeroRequest = false;
	double absolutePosition;
	double last_world_linear_accel_y;
	double time = 0;	
	
	Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 3.0 , 2.0, 60.0);
	Trajectory trajectory;
	
	boolean enableDistanceCheckingRequest = false;
	
	Waypoint[] points = new Waypoint[] {
		    new Waypoint(-3, 0 , 0),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees                       // Waypoint @ x=-2, y=0, exit angle=0 radians
		    new Waypoint(0, 0, 0)                           // Waypoint @ x=0, y=0,   exit angle=0 radians
		};
	 boolean reverseIndexRequest;
	 boolean reverseAgitatorRequest;
	 boolean resetGyroRequest;
	
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//some classes
		hal = new HardwareAbstractionLayer(this);
		hi = new HumanInput();
		tdt = new TankDriveTrain(this);
		shooter = new ShooterStateMachine(this);
		cam = new CamStateMachine(this);
		turret = new Turret(this);
		turretCam = new CustomCamera(this);
		//profile = new MotionProfile(this);
		tracking = new TrackingStateMachine(this);
		
		//auto classes
		auto0 = new AutoNothing(this);
		auto1 = new AutoCrossBaseline(this);
		auto2 = new AutoGearCenter(this);
		auto3 = new AutoGearLeft(this);
		auto4 = new AutoGearRight(this);
		auto5 = new AutoShootTenGearDrive(this); 
		auto6 = new AutoShootTen(this);
		auto7 = new AutoShootTenGear(this); 
		auto8 = new AutoDriveToHopperShoot(this);
		//auto9 = new MotionProfile(this);
		
		tdt.resetDriveEncoders();
		//misc
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	public void disabledInit() {
		//resets navx
		//navx.reset();
		//hal.camTalon.setPosition(0); //Turns cam after disable -- Unnecessary/ Detrimental?
		hal.camTalon.setPosition(0);
		hal.camTalon.setPulseWidthPosition(0);
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		tdt.resetDriveEncoders();
	}
	
	public void disabledPeriodic() {
		updateButtonStatus();
		if (zeroTurretRequest) {
			hal.turretTalon.setPosition(0);
			turret.desiredTarget = 0;
		}
		if (resetGyroRequest) {
			navx.reset();
		}
		SmartDashboard.putNumber("Auto Selection", autoSelect);
		//SmartDashboard.putBoolean("Red", redRequest);
		//SmartDashboard.putBoolean("Blue", blueRequest);
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
		cam.desiredPosition = (hal.camTalon.getPulseWidthPosition() - 944);
		hal.camTalon.setPosition(cam.desiredPosition);
		//lets auto chooser work with human input by setting varsxs
		
		
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
	}
	
	@Override
	public void autonomousInit() {
		//sets to gyrolock + low gear
		tdt.setDriveMode(driveMode.TANK);
		SmartDashboard.putBoolean("Red", redRequest);
		SmartDashboard.putBoolean("Blue", blueRequest);
		tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , Constants.kGyroLock_kD);
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		hal.camTalon.setEncPosition((int) cam.desiredPosition);
		//cam.calibrated = false; Not used
		//cam.reset(); Not Used
		turret.reset();
		//resets drive + navx vals
		navx.reset();
		tdt.resetDriveEncoders();
		tdt.setDriveTrainSpeed(0);
		//auto chooser
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
		//auto chooser
		tdt.avgEncPos = (tdt.lDriveTalon1.getPosition() + tdt.rDriveTalon1.getPosition()) / 2;
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
			SmartDashboard.putString("Auto Mode", "Drive to Hopper Shoot");
			SmartDashboard.putString("Auto State", auto7.currentState.toString());
			auto7.update();
		}
		
		if (autoSelect == 8) {
			SmartDashboard.putString("Auto Mode", "Gear Drive to Hopper Shoot");
			SmartDashboard.putString("Auto State", auto8.currentState.toString());
			auto8.update();
		}
		
		tdt.update();
		shooter.update();
		turret.update();
		cam.update();
		turretCam.update();
		tracking.update();
		
		if(Math.abs(tdt.desiredAngle - navx.getYaw()) > 20 ) {
			tdt.gyroControl.setPID(Constants.kGyroLock_kP, 0, Constants.kGyroLock_kD);
		}
		else if (Math.abs(tdt.desiredAngle - navx.getYaw()) <= 20 ) {
			tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , Constants.kGyroLock_kD);
		}
		
		//auto1.update();
		
		//SmartDashboard.putBoolean("Red", redRequest);
		//SmartDashboard.putBoolean("Blue", blueRequest);
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
		SmartDashboard.putBoolean("Calibrated", cam.calibrated);
		SmartDashboard.putNumber("PDP Temperature", hal.pdp.getTemperature());
		SmartDashboard.putNumber("D", tdt.gyroControl.getD());
		
		}
		
	public void teleopInit() {
		//sets to tank, resets navx, ensures robot is in low gear
		hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		hal.camTalon.setEncPosition((int) cam.desiredPosition);
		shootRequest = false;
		//cam.reset(); not used
		turret.update();
		tdt.lDriveTalon1.setPosition(0);
		tdt.rDriveTalon1.setPosition(0);
		tdt.setDriveMode(driveMode.TANK);
		turret.desiredTarget = turret.turretPosition;
		navx.reset();
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		/*
    	for (int i = 0; i < profile.trajectory.length(); i++) {
    	    Trajectory.Segment seg = profile.trajectory.get(i);
    	    
    	    SmartDashboard.putNumber("Delta Time",  seg.dt);
    	    SmartDashboard.putNumber("X", seg.x);
    	    SmartDashboard.putNumber("Y", seg.y);
    	    SmartDashboard.putNumber("Position", seg.position);
    	    SmartDashboard.putNumber("Velocity", seg.velocity);
    	    SmartDashboard.putNumber("Acceleration", seg.acceleration);
    	    SmartDashboard.putNumber("Jerk", seg.jerk);
    	    SmartDashboard.putNumber("Heading", seg.heading);
    	    
    	    
    	    System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
    	        seg.dt, seg.x, seg.y, seg.position, seg.velocity, 
    	            seg.acceleration, seg.jerk, seg.heading);
    	}*/
		tdt.gyroControl.setPID(Constants.kGyroLock_kP, Constants.kGyroLock_kI , 0);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		//joystick input
		
		//SmartDashboard.putNumber("Length", trajectory.length());
		
		//tdt.avgEncPos = (tdt.lDriveTalon1.getPosition() + tdt.rDriveTalon1.getPosition()) / 2;
		tdt.setStickInputs(hi.leftStick.getY()  * tdt.multiplyer, hi.rightStick.getY() *tdt.multiplyer); 
		
		 if (hi.turnTen()) {
			 tdt.desiredAngle = navx.getYaw() + 10;
		 }
		 if (hi.turnNegativeTen()) {
			 tdt.desiredAngle = navx.getYaw() - 10;
		 }
		 
		 SmartDashboard.putNumber("D", tdt.gyroControl.getD());
		 if (hi.turnNinety()) {
			 tdt.desiredAngle = navx.getYaw() + 90;
		 }
		 if (hi.turnNegativeNinety()) {
			 tdt.desiredAngle = navx.getYaw() - 90;
		 }

		tdt.update();
		cam.update();
		shooter.update();
		turret.update();
		turretCam.update();
		tracking.update();
		
		/* if (turretTrackRequest) {
				turret.getEncError(turretCam.calcTurretYaw());
			}*/
		 
		 if (enableDistanceCheckingRequest) {
			 turretCam.calcDistance();
			 turretCam.distanceCheck();
		 }
    	
		//what each button does
    	updateButtonStatus();
    	if (resetGyroRequest) {
    		navx.reset();
    	}
    	if(fuelIntakeRequest) {
    		hal.upperIntakeSpark.set(1);
    	} else if (!fuelIntakeRequest) {
    		hal.upperIntakeSpark.set(0); 
    	}
    	
    	if (gyroLockRequest) {
    		if (!lastGyroLock) {
    			tdt.setDriveMode(driveMode.GYROLOCK);
    			tdt.setDriveAngle(navx.getYaw()); //makes sure robot will move straight
    		}
    		tdt.setDriveTrainSpeed(tdt.rightStickInput); //moving speed dependent on right stick
    	}
    	else if (speedControlRequest && !gyroLockRequest) {
    		if (!lastSpeedControl) {
    			tdt.setDriveMode(driveMode.SPEEDCONTROL);
    		}	
    	}
    	else {
    		tdt.setDriveMode(driveMode.TANK);
    		tdt.gyroControl.disable();
    	}
    	
    	if (highGearRequest) {
    		hal.driveShifter.set(Value.valueOf(Constants.kShiftHighGear));
    	}
    	
    	 if (lowGearRequest && hal.driveShifter.get().toString() == Constants.kShiftHighGear) {
    		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
    		
    		if (Math.abs(tdt.avgSpeed) < 20) {
    			tdt.multiplyer = -.2;
    			tdt.shiftIteration = -2;
    		}
    		
    		else if (Math.abs(tdt.avgSpeed ) < 500) {
    			tdt.multiplyer = 0;
    			tdt.shiftIteration = 0;
    		}
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
    		//filip isnt cool 
    	}
    	/*
    	else if (hi.runClimberReverse()) {
    		hal.climberSpark.set(-1);
    	}*/
    	else {
    		hal.climberSpark.set(0);
    	}
    	if (setShooterCloseRequest) {
    		cam.desiredPosition = Constants.kCamClosePosition;
    		shooter.desiredSpeed = Constants.kShooterCloseSpeed;
    		//hal.turretTalon.set(Constants.kTurretClosePosition);
    	}
    	
    	else if (setShooterFarRequest) {
    		cam.desiredPosition = Constants.kCamFarPosition;
    		shooter.desiredSpeed = Constants.kShooterFarSpeed;
    		//hal.turretTalon.set(Constants.kTurretFarPosition);
    	}
    	else if (hi.setShooterManual()) {
    		cam.desiredPosition = ((hi.rightStick.getZ() + 1) / 2)*4096;
   		 	shooter.desiredSpeed = (((hi.leftStick.getZ() + 1) /2)*6500);
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
    	/*
    	else if (turnCamNearZeroRequest) {
    		cam.desiredPosition = .078125;
    	}*/
    	
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
    	
    	
    		
    	lastGyroLock = gyroLockRequest;
    	lastSpeedControl = speedControlRequest;
    	
   		SmartDashboard.putNumber("Gyro Angle", navx.getYaw());
   		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
   		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
       	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
   		SmartDashboard.putNumber("PID setpoint", tdt.gyroControl.getSetpoint());
   	 
   		SmartDashboard.putNumber("Left RPM", tdt.leftDriveRPM);
    	SmartDashboard.putNumber("Right RPM", tdt.rightDriveRPM); 
    	
   		SmartDashboard.putString("LeftDriveMode", tdt.lDriveTalon1.getControlMode().toString());
    	SmartDashboard.putString("RightDriveMode", tdt.rDriveTalon1.getControlMode().toString());
  
       	
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
        	SmartDashboard.putNumber("Jerk", tdt.calcJerk());
        	
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
        	SmartDashboard.putNumber("Desired Speed", shooter.desiredSpeed);
        	
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
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	
	public void testInit() {
		/*
		hal.turretTalon.changeControlMode(TalonControlMode.PercentVbus);
	
		 trajectory = Pathfinder.generate(points, config);

		Pathfinder.writeToCSV(myFile, trajectory);
		
	*/
	}
	
	public void testPeriodic() {
		updateButtonStatus();
		SmartDashboard.putNumber("Turret Position", hal.turretTalon.getEncPosition());
		SmartDashboard.putNumber("left stick input", hi.leftStick.getY());
		SmartDashboard.putNumber("right stick input", hi.rightStick.getY());
		
		
	}
	
	public void updateButtonStatus() {
		//checks if button is pressed
		fuelIntakeRequest = hi.getFuelIntake();
		gyroLockRequest = hi.getGyroLock();
		//speedControlRequest = hi.getSpeedControl();
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
	}
	
	
	
}