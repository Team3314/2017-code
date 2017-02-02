package org.usfirst.frc.team3314.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {
	//1 inch = 81.92 enc ticks
	//some classes
	HardwareAbstractionLayer hal;
	HumanInput hi;
	TankDriveTrain tdt;
	
	//auto classes
	AutoNothing auto0;
	AutoCrossBaseline auto1;
	AutoGearToPeg auto2;
	AutoGearToPegLeft auto3;
	AutoGearToPegRight auto4;
	AutoDriveToHopperLeft auto5;
	AutoDriveToHopperRight auto6;
	AutoGearHopperLeft auto7;
	AutoGearHopperRight auto8;
	
	//misc
	//PIDController control;
	GyroPIDSource gyroPIDSource;
    GyroPIDOutput gyroPIDOutput;
    PIDController gyroControl;
	UsbCamera camera;
	double encoderConversionFactor = 81.92;
	
	//button input
	boolean extendGearIntakeRequest;
	boolean retractGearIntakeRequest;
	boolean fuelIntakeRequest;
	boolean gyroLockRequest;
	boolean speedControlRequest;
	boolean highGearRequest;
	boolean lowGearRequest;
	boolean shootRequest;
	boolean flashlightRequest;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//all classes
		hal = new HardwareAbstractionLayer(this);
		hi = new HumanInput();
		tdt = new TankDriveTrain(this);
		auto0 = new AutoNothing(this);
		auto1 = new AutoCrossBaseline(this);
		auto2 = new AutoGearToPeg(this);
		auto3 = new AutoGearToPegLeft(this);
		auto4 = new AutoGearToPegRight(this);
		auto5 = new AutoDriveToHopperLeft(this);
		auto6 = new AutoDriveToHopperRight(this);
		auto7 = new AutoGearHopperLeft(this);
		auto8 = new AutoGearHopperRight(this);
		
		//misc
		//control = new PIDController(0.5, 0.000025, 0, 0, tdt.rDriveTalon1, tdt.rDriveTalon1);
		gyroPIDSource = new GyroPIDSource(this,0 ,hi.rightStick.getY());
		gyroPIDOutput = new GyroPIDOutput();
		gyroControl = new PIDController(1, 0.5, 0, .1, gyroPIDSource, gyroPIDOutput);
		
		//camera = new UsbCamera("Logitech", 0);
		camera = CameraServer.getInstance().startAutomaticCapture(); //remember to add cameraserver stream viewer widget
		camera.setResolution(640, 480);
		
		hal.gyro.calibrate();
		hal.gearIntake.set(Value.kForward);
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
	@Override
	public void autonomousInit() {
		/*autonomous reset*/
		tdt.setDriveMode(driveMode.GYROLOCK);
		hal.gyro.reset();
		
		tdt.lDriveTalon1.setPosition(0);
		tdt.rDriveTalon1.setPosition(0);
		tdt.setDriveTrainSpeed(0);
		
		auto1.reset();
		
		/*pid code*/
		//control.enable();
		//tdt.rDriveTalon1.changeControlMode(TalonControlMode.Position);
		//tdt.rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		//tdt.rDriveTalon1.setPID(0.5, 0.000025, 0, 0, 0, 0, 0);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
		SmartDashboard.putNumber("Ticks", tdt.rDriveTalon1.getPosition());
    	
    	hal.gyro.update();
    	
		/* goes thru auto states */
		//auto1.update();
		tdt.update();
		
		/* pid code*/
		//tdt.rDriveTalon1.set(2048);
	
		//motor set to encoder tick pos
		//2048 encoder ticks = 1 revolution
	
		/*else {
		if (talon.getEncPosition() < 2000) {
			talon.set(0.3);
		} else {
			talon.set(0);}*/
		
		//timer
		//50 loops = 1 second
		/*if(time < 250){
			talon.set(1);
			time++;
		}
			talon.set(0);
	}*/
		}
		
	public void teleopInit() {
		tdt.setDriveMode(driveMode.TANK);
		hal.gyro.reset();
		gyroControl.enable();
		hal.driveShifter.set(Value.kReverse);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
		SmartDashboard.putNumber("PID setpoint", gyroControl.getSetpoint());
		SmartDashboard.putNumber("PID output", gyroPIDSource.pidGet());
		SmartDashboard.putNumber("RPM", tdt.lDriveTalon1.getSpeed());
    	
    	SmartDashboard.putNumber("Left 1 current", tdt.lDriveTalon1.getOutputCurrent());
    	SmartDashboard.putNumber("Left 2 current", tdt.lDriveTalon2.getOutputCurrent());
    	SmartDashboard.putNumber("Right 1 current", tdt.rDriveTalon1.getOutputCurrent());
    	//SmartDashboard.putNumber("Right 2 current", tdt.rDriveTalon2.getOutputCurrent());
    	
    	
    	hal.gyro.update();
    	gyroPIDSource.setPIDInput(0, hi.rightStick.getY());
    	//gyroControl.setSetpoint(hi.rightStick.getY());
    	
		//joystick input
		tdt.setStickInputs(hi.leftStick.getY(), hi.rightStick.getY()); 
		tdt.update();
    	
		//what each button does
    	updateButtonStatus();
    	if(extendGearIntakeRequest) {
    		hal.gearIntake.set(Value.kForward);
    	}
    	
    	if(retractGearIntakeRequest) {
     		hal.gearIntake.set(Value.kReverse);
    	}
    	
    	if(fuelIntakeRequest) {
    		hal.intakeSpark.set(1);
    	} else {
    		if (!fuelIntakeRequest) {
    		hal.intakeSpark.set(0);
    		}
    	}
    	
    	if (gyroLockRequest) {
    		tdt.setDriveMode(driveMode.GYROLOCK);
    		tdt.setDriveAngle(hal.gyro.angle()); //makes sure robot will move straight
    		tdt.setDriveTrainSpeed(hi.rightStick.getY()); //moving speed dependent on right stick
    	} else {
    		if (!gyroLockRequest) {
    			tdt.setDriveMode(driveMode.TANK);
    		}
    	}
    	
    	if (speedControlRequest) {
    		tdt.setDriveMode(driveMode.SPEEDCONTROL);
    	} else {
    		if (!speedControlRequest) {
    			tdt.setDriveMode(driveMode.TANK);
    		}
    	}
    	
    	if (highGearRequest) {
    		hal.driveShifter.set(Value.kForward);
    	}
    	
    	if (lowGearRequest) {
    		hal.driveShifter.set(Value.kReverse);
    	}
    	
    	if (shootRequest) {
    	}
    	
    	if(flashlightRequest) {
    		hal.flashlight.set(true);
    	} else {
    		if(!flashlightRequest) {
    		hal.flashlight.set(false);
    		}
    	}
    }
		
	/**
	 * This function is called periodically during test mode
	 */
	@Override
	
	public void testPeriodic() {
	}
	
	public void updateButtonStatus() {
		//checks if button is pressed
		extendGearIntakeRequest = hi.getExtendGearIntake();
		retractGearIntakeRequest = hi.getRetractGearIntake();
		fuelIntakeRequest = hi.getFuelIntake();
		gyroLockRequest = hi.getGyroLock();
		speedControlRequest = hi.getSpeedControl();
		highGearRequest = hi.getHighGear();
		lowGearRequest = hi.getLowGear();
		shootRequest = hi.getShoot();
		flashlightRequest = hi.getFlashlight();
	}
}