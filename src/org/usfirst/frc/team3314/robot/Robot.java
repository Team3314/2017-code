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
	AutoGearToPegLeft auto2;
	
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
	boolean gyroLockRequest;
	boolean highGearRequest;
	boolean lowGearRequest;
	boolean shootRequest;
	
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
		auto2 = new AutoGearToPegLeft(this);
		
		//misc
		//control = new PIDController(0.5, 0.000025, 0, 0, tdt.rDriveTalon1, tdt.rDriveTalon1);
		//camera = new UsbCamera("Logitech", 0);
		//gyroPIDSource = new GyroPIDSource(this, hi.rightStick.getY(), 0);
		//gyroPIDOutput = new GyroPIDOutput();
		//gyroControl = new PIDController(0.5, 0.000025, 0, 0, gyroPIDSource, gyroPIDOutput);
		
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
		//gyroControl.enable();
		
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
		SmartDashboard.putNumber("Ticks", tdt.rDriveTalon1.getPosition());
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
    	SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    
		//SmartDashboard.putNumber("PID setpoint", gyroControl.getSetpoint());
		//SmartDashboard.putNumber("PID output", gyroPIDSource.pidGet());
    	
    	hal.gyro.update();
    	
		/* goes thru auto states */
		//auto1.update();
		tdt.update();
		//gyroControl.setSetpoint(hi.rightStick.getY());
		
		/* pid code*/
		//tdt.rDriveTalon1.set(2048);
	
		//motor set to encoder tick pos
		//2048 encoder ticks = 1 revolution
		/*if (talon.getEncPosition() < 2000) {
			talon.set(0.3);
		} else {
			talon.set(0);}*/
		
		//timer
		//50 loops = 1 second
		/*if(time < 250){
			talon.set(1);
			time++;
		}
		else {
			talon.set(0);
		}*/
	}
	
	public void teleopInit() {
		tdt.setDriveMode(driveMode.TANK);
		hal.gyro.reset();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
    	
    	hal.gyro.update();
    	
		//joystick input
		tdt.setStickInputs(hi.operator.getRawAxis(1), hi.operator.getRawAxis(5)); 
		tdt.update();
    	
		//what each button does
    	updateButtonStatus();
    	if(extendGearIntakeRequest) {
    		hal.gearIntake.set(Value.kForward);
    	}
    	
    	if(retractGearIntakeRequest) {
     		hal.gearIntake.set(Value.kReverse);
    	}
    	
    	if (gyroLockRequest) {
    		tdt.setDriveAngle(hal.gyro.angle()); //makes sure robot will move straight
    		tdt.setDriveMode(driveMode.GYROLOCK);
    		tdt.setDriveTrainSpeed(hi.rightStick.getY()); //moving speed dependent on right stick
    	}
    	
    	if (highGearRequest) {
    		hal.driveShifter.set(Value.kReverse);
    	}
    	
    	if (lowGearRequest) {
    		hal.driveShifter.set(Value.kForward);
    	}
    	
    	if (shootRequest) {
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
		gyroLockRequest = hi.getGyroLock();
		highGearRequest = hi.getHighGear();
		lowGearRequest = hi.getLowGear();
		shootRequest = hi.getShoot();
	}
}