package org.usfirst.frc.team3314.robot;

public class Constants {
	Robot robot;
	
	//misc tank drive train
	public static double kAvgEncPos; //average of left + right drive talon encoder positions
	public static double kEncConvFactor; //conversion from inches to encoder ticks
	public static double kHighGearRPM; //rpm for high gear
	public static double kLowGearRPM; //rpm for low gear
	public static double kCollisionThreshold_DeltaG; //acceleration limit for collision detection
	
	//Solenoid States
	public static String kExtendGearIntake;
	public static String kRetractGearIntake;
	public static String kShiftHighGear;
	public static String kShiftLowGear;
	public static boolean kFlashlightOn;
	public static boolean kFlashlightOff;
	
	//gyrolock pidcontroller
	public static double kGyroLock_kP;
	public static double kGyroLock_kI;
	public static double kGyroLock_kD;
	public static double kGyroLock_kF;
	
	//turret pidcontroller
	public static double kTurret_kP;
	public static double kTurret_kI;
	public static double kTurret_kD;
	public static double kTurret_kF;
	
	//speedcontrol pid
	public static double kSpeedControl_kP;
	public static double kSpeedControl_kI;
	public static double kSpeedControl_kD;
	public static double kSpeedControl_kF;
	public static int kSpeedControl_IZone;
	public static double kSpeedControl_RampRate;
	public static int kSpeedControl_Profile;
	
	public Constants(Robot r) {
		robot = r;
		
		//misc tank drive train
		kAvgEncPos = (robot.tdt.lDriveTalon1.getEncPosition() + robot.tdt.rDriveTalon1.getEncPosition())/2;
		kEncConvFactor = 81.92;
		kHighGearRPM = 200;
		kLowGearRPM = 75;
		kCollisionThreshold_DeltaG = 0.5; //half of earths gravity
		
		//Solenoid States
		kExtendGearIntake = "kForward";
		kRetractGearIntake = "kReverse";
		kShiftHighGear = "kForward";
		kShiftLowGear = "kReverse";
		kFlashlightOn = true;
		kFlashlightOff = false;
		
		//gyrolock pidcontroller, placeholders
		kGyroLock_kP = 0;
		kGyroLock_kI = 0;
		kGyroLock_kD = 0;
		kGyroLock_kF = 0;
		
		//turret pidcontroller, placeholders
		kTurret_kP = 0;
		kTurret_kI = 0;
		kTurret_kD = 0;
		kTurret_kF = 0;
		
		//speedcontrol pid
		kSpeedControl_kP = 1;
		kSpeedControl_kI = 0.01;
		kSpeedControl_kD = 0;
		kSpeedControl_kF = 3;
		kSpeedControl_IZone = 0;
		kSpeedControl_RampRate = 0;
		kSpeedControl_Profile = 0;
	}
}