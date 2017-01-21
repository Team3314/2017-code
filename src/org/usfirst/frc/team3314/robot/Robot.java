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
	HardwareAbstractionLayer hal;
	HumanInput hi;
	TankDriveTrain tdt;
	AutoTest auto0;
	
	PIDController control;
	UsbCamera camera;	
	
	//button input
	boolean lightOffRequest;
	boolean light1Request;
	boolean light2Request;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		hal = new HardwareAbstractionLayer(this);
		hi = new HumanInput();
		tdt = new TankDriveTrain(this);
		auto0 = new AutoTest(this);
		
		control = new PIDController(0.1, 0.001, 0, 0, tdt.rDriveTalon1, tdt.rDriveTalon1);
		//camera = new UsbCamera("Logitech", 0);
		
		camera = CameraServer.getInstance().startAutomaticCapture(); //remember to add cameraserver stream viewer widget
		camera.setResolution(640, 480);
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
		/* autonomous reset */
		auto0.reset();
		
		/* how to setup pid
		talon.changeControlMode(TalonControlMode.Position); //default is PercentVbus
		talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		talon.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
		talon.setPosition(0); */
		
		/* pidcontroller code
		control.enable();
		tdt.rDriveTalon1.changeControlMode(TalonControlMode.Position);
		tdt.rDriveTalon1.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		tdt.rDriveTalon1.setPosition(0); */
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Ticks", tdt.rDriveTalon1.getPosition());
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
    	
		/* goes thru auto states */
		auto0.update();
		tdt.update();
		
		//how to do pid
		//talon.enable();
		//talon.set(8192);
		
		/* pidcontroller code
		tdt.rDriveTalon1.set(2048); */
	
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
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
    	
		//joystick input
		tdt.setStickInputs(hi.leftStick.getY(), hi.rightStick.getY()); 
		tdt.update();
    	
		//what each button does
    	updateButtonStatus();
    	
    	if (lightOffRequest) {
    		hal.solenoid.set(Value.kOff);}
    	
    	if (light1Request) {
    		hal.solenoid.set(Value.kReverse);}
    	
    	if (light2Request) {
    		hal.solenoid.set(Value.kForward);}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public void updateButtonStatus() {
		//checks if button is pressed
		lightOffRequest = hi.getLightsOff();
		light1Request = hi.getFirstLight();
		light2Request = hi.getSecondLight();
	}
}