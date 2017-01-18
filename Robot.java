package org.usfirst.frc.team3314.robot;

//import edu.wpi.first.wpilibj.CANSpeedController.ControlMode;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import com.ctre.CANTalon;
//import com.ctre.CANTalon.*;
//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {
	
	//CANTalon talon;
	HardwareAbstractionLayer hal;
	HumanInput hi;
	TankDriveTrain tdt;

	AutoTest auto0;
	
	int time;
	
	boolean lightOffRequest;
	boolean light1Request;
	boolean light2Request;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		//talon = new CANTalon(0);
		hal = new HardwareAbstractionLayer(this);
		hi = new HumanInput();
		tdt = new TankDriveTrain(this);

		auto0 = new AutoTest(this);
		
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
		
		auto0.reset();
		
		//time = 0;
		
		//talon.changeControlMode(TalonControlMode.Position); //default is PercentVbus
		//talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		//talon.configEncoderCodesPerRev(2048);
		//talon.setPID(0.5, 0.001, 0, 0, 0, 0, 0);
		//talon.setPosition(0);
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		
		auto0.update();
		
		//SmartDashboard.putNumber("ticks", talon.getPosition());
		//talon.enable();
		//talon.set(8192);
		
		//2048 encoder ticks = 1 revolution
		/*if (talon.getEncPosition() < 2000) {
			talon.set(0.3);
		} else {
			talon.set(0);}*/
		
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
		
		//time = 0;
		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		
		SmartDashboard.putNumber("left stick speed", -(hi.leftStick.getY()));
		SmartDashboard.putNumber("right stick speed", -(hi.rightStick.getY()));
    	tdt.setStickInputs(-(hi.leftStick.getY()), -(hi.rightStick.getY())); 
    	
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
		
		lightOffRequest = hi.getLightsOff();
		light1Request = hi.getFirstLight();
		light2Request = hi.getSecondLight();
		
	}
}