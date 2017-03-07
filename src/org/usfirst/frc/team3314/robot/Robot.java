package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.TalonControlMode;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	
	//auto classes
	AutoNothing auto0;
	AutoCrossBaseline auto1;
	AutoShootTen auto2;
	AutoGearToPegLeft auto3;
	AutoGearToPegRight auto4;
	AutoDriveToHopperLeft auto5;
	AutoDriveToHopperRight auto6;
	AutoGearHopperLeft auto7;
	AutoGearHopperRight auto8;
	
	//misc
	CamStateMachine cam;
	Turret turret;
	AHRS ahrs = new AHRS(SPI.Port.kMXP);
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
	
	boolean turnShooterLeftRequest = false;
	boolean turnShooterForwardRequest = false;
	boolean turnShooterRightRequest = false;
	boolean enableTurretTrackingRequest = false;
	boolean calibrateCamRequest = false;
	boolean setShooterCloseRequest = false;
	boolean setShooterFarRequest = false;
	boolean ringLightRequest = false;
	
	boolean binaryOne = false;
	boolean binaryTwo = false;
	boolean binaryFour = false;
	boolean binaryEight = false;
	
	int autoSelect = 0;
	
	boolean compressorOverrideRequest = false;
	
	boolean auto0Request;
	boolean auto1Request;
	boolean auto2Request;
	boolean auto3Request;
	boolean auto4Request;
	boolean auto5Request;
	boolean auto6Request;
	boolean auto7Request;
	
	boolean redRequest = false;
	boolean blueRequest = false;
	
	double last_world_linear_accel_y;
	double time = 0;
	
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
		
		//auto classes
		auto0 = new AutoNothing(this);
		auto1 = new AutoCrossBaseline(this);
		auto2 = new AutoShootTen(this);
		auto3 = new AutoGearToPegLeft(this);
		auto4 = new AutoGearToPegRight(this);
		auto5 = new AutoDriveToHopperLeft(this);
		auto6 = new AutoDriveToHopperRight(this);
		auto7 = new AutoGearHopperLeft(this);
		auto8 = new AutoGearHopperRight(this);
		
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
		//ahrs.reset();
		hal.adjustTalon.setPosition(0);
		turret.desiredTarget = 0;
		hal.turretTalon.setPosition(0);
	}
	
	public void disabledPeriodic() {
		updateButtonStatus();
		SmartDashboard.putNumber("Auto Selection", autoSelect);
		
		SmartDashboard.putNumber("Gyro Angle", ahrs.getYaw());
		SmartDashboard.putNumber("DPad", hi.operator.getPOV());

		//lets auto chooser work with human input by setting vars
		
		
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
		tdt.setDriveMode(driveMode.GYROLOCK);
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		cam.calibrated = false;
		cam.reset();

		//resets drive + gyro vals
		ahrs.reset();
		tdt.lDriveTalon1.setPosition(0);
		tdt.rDriveTalon1.setPosition(0);
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
		if (autoSelect == 0) {
			auto0.update();
		}
			
		if (autoSelect == 1) {
			auto1.update();
		}
		
		if (autoSelect == 2) {
			auto2.update();
		}
		
		if (autoSelect == 3) {
			auto3.update();
		}
		
		if (autoSelect == 4) {
			auto4.update();
		}
		
		if (autoSelect == 5) {
			auto5.update();
		}
		
		if (autoSelect == 6) {
			auto6.update();
		}
		
		if (autoSelect == 7) {
			auto7.update();
		}
		
		if (autoSelect == 8) {
			auto8.update();
		}

		if (turretTrackRequest) {
			turret.getEncError(turretCam.getXError());
			turret.update();
		}
		
		tdt.update();
		shooter.update();
		cam.update();
		//auto1.update();
		SmartDashboard.putNumber("Gyro Angle", ahrs.getYaw());
		SmartDashboard.putNumber("Desired Angle", tdt.desiredAngle);
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
		SmartDashboard.putNumber("Average Encoder Position", tdt.avgEncPos);
		}
		
	public void teleopInit() {
		//sets to tank, resets gyro, ensures robot is in low gear
		shootRequest = false;
		cam.reset();
		tdt.lDriveTalon1.setPosition(0);
		tdt.rDriveTalon1.setPosition(0);
		tdt.setDriveMode(driveMode.TANK);
		cam.calibrated = false;
		ahrs.reset();
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
    	turret.desiredTarget = 0;
		hal.turretTalon.setPosition(0);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		//joystick input
		tdt.setStickInputs(hi.leftStick.getY(), hi.rightStick.getY()); 
		
		// TEST CODE TEST CODE TEST CODE TEST CODE TEST CODE
		// turret.desiredTarget = ((hi.leftStick.getZ() + 1)/2)*7;
		
		 if (hi.turnTen()) {
			 tdt.desiredAngle = ahrs.getYaw() + 10;
		 }
		 if (hi.turnNegativeTen()) {
			 tdt.desiredAngle = ahrs.getYaw() - 10;
		 }
		 
		 
		 if (hi.turnNinety()) {
			 tdt.desiredAngle = ahrs.getYaw() + 90;
		 }
		 if (hi.turnNegativeNinety()) {
			 tdt.desiredAngle = ahrs.getYaw() - 90;
		 }
		 
	
		 //hal.shooterTalon.set(0);
		// hal.shooterTalon.set(((hi.leftStick.getZ() + 1) /2)*5900);
		 //TEST CODE TEST CODE TEST CODE TEST CODE TEST CODE

		tdt.update();
		cam.update();
		turret.update();
		shooter.update();
		
		 if (turretTrackRequest) {
				turret.getEncError(turretCam.getXError());
		 }
    	
		//what each button does
    	updateButtonStatus();
    	if(raiseGearIntakeRequest) {
    		hal.gearIntake.set(Value.valueOf(Constants.kRaiseGearIntake));
    	}
    	
    	if(dropGearIntakeRequest) {
     		hal.gearIntake.set(Value.valueOf(Constants.kDropGearIntake));
    	}
    	
    	if(fuelIntakeRequest) {
    		hal.gearIntake.set(Value.valueOf(Constants.kRaiseGearIntake));
    		hal.intakeSpark.set(1);
    		hal.upperIntakeSpark.set(1);
    	} else if (!fuelIntakeRequest) {
    		hal.upperIntakeSpark.set(0); 
    		hal.intakeSpark.set(0);
    	}
    	
    	if (gyroLockRequest) {
    		if (!lastGyroLock) {
    			tdt.setDriveMode(driveMode.GYROLOCK);
    			tdt.setDriveAngle(ahrs.getYaw()); //makes sure robot will move straight
    		}
    		tdt.setDriveTrainSpeed(hi.rightStick.getY()); //moving speed dependent on right stick
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
    	} else if(!flashlightRequest) {
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
    		turret.desiredTarget = 3.5;
    		turretTrackRequest = false;
    	}
    	else if (turnShooterRightRequest) {
    		turret.desiredTarget = 7;
    		turretTrackRequest = false;
    	}
    	else if (enableTurretTrackingRequest) {
    		turretTrackRequest = true;
    	}
    	if (calibrateCamRequest) {
    		cam.calibrated = false;
    	}
    	if (hi.runClimber()) {
    		hal.climberSpark.set(1);
    	}
    	else if (hi.runClimberReverse()) {
    		hal.climberSpark.set(-1);
    	}
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
    		cam.desiredPosition = (hi.rightStick.getZ() + 1) / 2;
   		 	shooter.desiredSpeed = (((hi.leftStick.getZ() + 1) /2)*5900);
    	}
    	if (compressorOverrideRequest) { 
    		hal.pcm1.stop();
    	}
    	else {
    		hal.pcm1.start();
    	}
    	if (ringLightRequest) {
    		hal.ringLight.set(true);
    	}
    	else {
    		
    		hal.ringLight.set(false);
    	}
    	if (hi.buttonBox.getRawButton(1)) {
    		hal.agitatorSpark.set(-1);   
    	}
    	else if(!hi.buttonBox.getRawButton(1) && !feedShooterRequest && !shootRequest) {
    		hal.agitatorSpark.set(0);
    	}
    	
    	if (hi.buttonBox.getRawButton(2)) {
    		hal.lowerIndexSpark.set(-1);
    	}
    	else if(!hi.buttonBox.getRawButton(1) && !feedShooterRequest && !shootRequest) {
    		hal.lowerIndexSpark.set(0);
    	}
    		
    	lastGyroLock = gyroLockRequest;
    	lastSpeedControl = speedControlRequest;
    	
   		SmartDashboard.putNumber("Gyro Angle", ahrs.getYaw());
   		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
   		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
       	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
   		SmartDashboard.putNumber("PID setpoint", tdt.gyroControl.getSetpoint());
    	SmartDashboard.putNumber("Left RPM", tdt.leftDriveRPM);
    	SmartDashboard.putNumber("Right RPM", tdt.rightDriveRPM);
    		
    	SmartDashboard.putString("LeftDriveMode", tdt.lDriveTalon1.getControlMode().toString());
    	SmartDashboard.putString("RightDriveMode", tdt.rDriveTalon1.getControlMode().toString());
        	
       	SmartDashboard.putNumber("Left 1 current", tdt.lDriveTalon1.getOutputCurrent());
       	SmartDashboard.putNumber("Left 2 current", tdt.lDriveTalon2.getOutputCurrent());
       	SmartDashboard.putNumber("Right 1 current", tdt.rDriveTalon1.getOutputCurrent());
       	SmartDashboard.putNumber("Right 2 current", tdt.rDriveTalon2.getOutputCurrent());
       	
        	SmartDashboard.putNumber("Left 1", tdt.lDriveTalon1.get());
        	SmartDashboard.putNumber("Left 2 ", tdt.lDriveTalon2.get());
        	SmartDashboard.putNumber("Right 1 ", tdt.rDriveTalon1.get());
        	SmartDashboard.putNumber("Right 2 ", tdt.rDriveTalon2.get());
        	
        	SmartDashboard.putNumber("Average Encoder Position", tdt.avgEncPos);
        	
        	SmartDashboard.putNumber("Left Encoder Position", tdt.leftDrivePosition);
        	SmartDashboard.putNumber("Right Encoder Position", tdt.rightDrivePosition);
        	
        	SmartDashboard.putNumber("Desired Speed", tdt.desiredSpeed);
        	SmartDashboard.putNumber("Desired Angle", tdt.desiredAngle);
        	SmartDashboard.putBoolean("lastGyroLock", lastGyroLock);
        	
        	SmartDashboard.putBoolean("Pressure Switch", hal.pcm1.getPressureSwitchValue());
        	SmartDashboard.putString("Shooter State", shooter.currentState.toString());
        	SmartDashboard.putNumber("Cam position", hal.adjustTalon.getPosition()*8192);
        	SmartDashboard.putNumber ("Cam Input", cam.desiredPosition);
        	SmartDashboard.putNumber("Target Cam Position", hal.adjustTalon.getSetpoint() * 8192);
        	SmartDashboard.putNumber("Cam Error", hal.adjustTalon.getError());
        	
        	SmartDashboard.putNumber("Cam Voltage", hal.adjustTalon.getOutputVoltage());
        	SmartDashboard.putNumber("Cam Curret", hal.adjustTalon.getOutputCurrent());
        	SmartDashboard.putString("Cam State", cam.currentState.toString());
        	SmartDashboard.putString("Cam control mode", hal.adjustTalon.getControlMode().toString());
        	
        	SmartDashboard.putNumber("Turret Position", hal.turretTalon.getPosition());
        	SmartDashboard.putNumber("Target Turret Position", hal.turretTalon.getSetpoint());
        	SmartDashboard.putNumber("Lower Roller Input", hal.lowerIndexSpark.get());

        	SmartDashboard.putBoolean("Intake Spark", hal.intakeSpark.isAlive());        	
        	SmartDashboard.putString("Gear Intake state", hal.gearIntake.get().toString());
        	SmartDashboard.putNumber("Shooter RPM", shooter.shooterSpeed);
        	SmartDashboard.putNumber("Shooter Voltage", hal.shooterTalon.getOutputVoltage());
        	SmartDashboard.putNumber("Shooter Current", hal.shooterTalon.getOutputCurrent());
        	
        	
        	SmartDashboard.putNumber("Target ShooterRPM" ,(((hi.leftStick.getZ() + 1) /2)*5900));
        	
        	SmartDashboard.putNumber("Shooter Error", hal.shooterTalon.getSetpoint());
        	
        	SmartDashboard.putBoolean("Cam Calibrated", cam.calibrated);
      
        	
        	SmartDashboard.putNumber("Index Pulse Value", hal.adjustTalon.getPinStateQuadIdx());
        	
        	SmartDashboard.putNumber("Get Agitator", hal.agitatorSpark.get());
        	SmartDashboard.putNumber("Lower Index Get", hal.lowerIndexSpark.get());
        	SmartDashboard.putNumber("Upper Index get", hal.upperIndexSpark.get());
        	SmartDashboard.putBoolean("Shoot Request", shootRequest);
        	SmartDashboard.putNumber("Desired Speed", shooter.desiredSpeed);
        	
        	SmartDashboard.putNumber("Acceleration", ahrs.getWorldLinearAccelX());
    }
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	
	public void testInit() {
		hal.turretTalon.changeControlMode(TalonControlMode.PercentVbus);
	}
	
	public void testPeriodic() {
		updateButtonStatus();
		if (compressorOverrideRequest) {
    		hal.pcm1.stop();
    	}
    	else {
    		hal.pcm1.start();
    	}
		SmartDashboard.putNumber("Turret Position", hal.turretTalon.getEncPosition());
		
	}
	
	public void updateButtonStatus() {
		//checks if button is pressed
		raiseGearIntakeRequest = hi.getRaiseGearIntake();
		dropGearIntakeRequest = hi.getDropGearIntake();
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
		calibrateCamRequest = hi.calibrateCam();
		setShooterCloseRequest = hi.setShooterClose();
		setShooterFarRequest = hi.setShooterFar();
		binaryOne = hi.getBinaryOne();
		binaryTwo = hi.getBinaryTwo();
		binaryFour = hi.getBinaryFour();
		binaryEight = hi.getBinaryEight();	
		compressorOverrideRequest = hi.compressorOverride();
		ringLightRequest = hi.getRingLight();
		blueRequest = hi.getBlue();
		redRequest = hi.getRed();
		
		
	}
}