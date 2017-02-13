package org.usfirst.frc.team3314.robot;

//import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.DoubleSolenoid.*;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.PIDController;
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
	AHRS ahrs = new AHRS(SPI.Port.kMXP);
	TankDriveTrain tdt;
	ShooterStateMachine shooter;
	
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
	Turret turret;
	UsbCamera drivingCam;
	CustomCamera turretCam;
	
	
	
	
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
	
	boolean turretTrackRequest = false;
	
	boolean lastGyroLock = false;
	boolean lastSpeedControl = false;
	
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
		turret = new Turret(this);
		
		//auto classes
		auto0 = new AutoNothing(this);
		auto1 = new AutoCrossBaseline(this);
		auto2 = new AutoGearToPeg(this);
		auto3 = new AutoGearToPegLeft(this);
		auto4 = new AutoGearToPegRight(this);
		auto5 = new AutoDriveToHopperLeft(this);
		auto6 = new AutoDriveToHopperRight(this);
		auto7 = new AutoGearHopperLeft(this);
		auto8 = new AutoGearHopperRight(this);
		
		turretCam = new CustomCamera(this);
		
		
		
		//misc
		//some placeholder pid values = 0.5, 0.000025, 0, 0

		hal.gearIntake.set(Value.valueOf(Constants.kExtendGearIntake));
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
		ahrs.reset();
	}
	
	@Override
	public void autonomousInit() {
		//sets to gyrolock + low gear
		tdt.setDriveMode(driveMode.GYROLOCK);
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
		
		//resets all values then auto
		ahrs.reset();
		tdt.lDriveTalon1.setPosition(0);
		tdt.rDriveTalon1.setPosition(0);
		tdt.setDriveTrainSpeed(0);
		auto1.reset();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		//goes through auto states
		auto1.update();
		if (turretTrackRequest) {
			turret.getEncError(turretCam.GetXError());
			turret.update();
		}
		tdt.update();
		
		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    
    	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
		SmartDashboard.putNumber("Ticks", tdt.rDriveTalon1.getPosition());
		}
		
	public void teleopInit() {
		//sets to tank, resets gyro, ensures robot is in low gear
		tdt.setDriveMode(driveMode.TANK);
		ahrs.reset();
		hal.driveShifter.set(Value.valueOf(Constants.kShiftLowGear));
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override		
	public void teleopPeriodic() {
		//joystick input
		tdt.setStickInputs(hi.leftStick.getY(), hi.rightStick.getY()); 
		tdt.update();
    	
		//what each button does
    	updateButtonStatus();
    	if(extendGearIntakeRequest) {
    		hal.gearIntake.set(Value.valueOf(Constants.kExtendGearIntake));
    	}
    	
    	if(retractGearIntakeRequest) {
     		hal.gearIntake.set(Value.valueOf(Constants.kRetractGearIntake));
    	}
    	
    	if(fuelIntakeRequest) {
    		hal.intakeSpark.set(1);
    	} else if (!fuelIntakeRequest) {
    		hal.intakeSpark.set(0);
    	}
    	
    	
    	if (gyroLockRequest) {
    		if (!lastGyroLock) {
    			tdt.gyroControl.enable();
    			tdt.setDriveMode(driveMode.GYROLOCK);
    			tdt.setDriveAngle(ahrs.getYaw()); //makes sure robot will move straight
    		}
    		tdt.setDriveTrainSpeed(hi.rightStick.getY()); //moving speed dependent on right stick
    	}
    	else if (speedControlRequest && !gyroLockRequest) {
    		if (!lastSpeedControl) {
    			tdt.setDriveMode(driveMode.SPEEDCONTROL);
    			//tdt.lDriveTalon1.changeControlMode(CANTalon.TalonControlMode.Speed);
    			//tdt.rDriveTalon1.changeControlMode(CANTalon.TalonControlMode.Speed);
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
    	
    	if (shootRequest) {
    		shooter.reset();
    		shooter.update();
    	}
    	else if (!shootRequest) {
    		shooter.stopShoot();
    		shooter.reset();
    		}
    	
    	if(flashlightRequest) {
    		hal.flashlight.set(Constants.kFlashlightOn);
    	} else if(!flashlightRequest) {
    		hal.flashlight.set(Constants.kFlashlightOff);
    	}
    		
    	lastGyroLock = gyroLockRequest;
    	lastSpeedControl = speedControlRequest;
    	
    	
    		SmartDashboard.putNumber("Gyro Angle", ahrs.getYaw());
    		SmartDashboard.putNumber("Left stick speed", tdt.rawLeftSpeed);
    		SmartDashboard.putNumber("Right stick speed", tdt.rawRightSpeed);    	
        	SmartDashboard.putString("Drive state", tdt.currentMode.toString());
    		SmartDashboard.putNumber("PID setpoint", tdt.gyroControl.getSetpoint());
    		SmartDashboard.putNumber("RPM", tdt.lDriveTalon1.getSpeed());
    		
    		SmartDashboard.putString("LeftDriveMode", tdt.lDriveTalon1.getControlMode().toString());
    		SmartDashboard.putString("RightDriveMode", tdt.rDriveTalon1.getControlMode().toString());
    		
        	
        	SmartDashboard.putNumber("Left 1 current", tdt.lDriveTalon1.getOutputCurrent());
        	SmartDashboard.putNumber("Left 2 current", tdt.lDriveTalon2.getOutputCurrent());
        	SmartDashboard.putNumber("Right 1 current", tdt.rDriveTalon1.getOutputCurrent());
        	SmartDashboard.putNumber("Right 2 current", tdt.rDriveTalon2.getOutputCurrent());
        	
        	SmartDashboard.putNumber("Desired Speed", tdt.desiredSpeed);
        	SmartDashboard.putBoolean("lastGyroLock", lastGyroLock);
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