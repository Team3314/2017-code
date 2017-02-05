package org.usfirst.frc.team3314.robot;

public class Constants {
	Robot robot;
	
	//tankdrivetrain
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
	
	//turret pid
	public static double kTurret_kP;
	public static double kTurret_kI;
	public static double kTurret_kD;
	public static double kTurret_kF;
	public static int kTurret_IZone;
	public static double kTurret_RampRate;
	public static int kTurret_Profile;
	
	//adjust pid
	public static double kAdjust_kP;
	public static double kAdjust_kI;
	public static double kAdjust_kD;
	public static double kAdjust_kF;
	public static int kAdjust_IZone;
	public static double kAdjust_RampRate;
	public static int kAdjust_Profile;
	public static double kAdjust_EncConvFactor; //conversion from degrees to enc ticks
	
	//shooter (pid + other values)
	public static double kShooter_kP;
	public static double kShooter_kI;
	public static double kShooter_kD;
	public static double kShooter_kF;
	public static int kShooter_IZone;
	public static double kShooter_RampRate;
	public static int kShooter_Profile;
	public static double kShooter_TargetRPM;
	public static double kShooter_IndexSensorThreshold;
	
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
		kEncConvFactor = 81.92; //enc ticks in 1 inch
		kAvgEncPos = (robot.tdt.lDriveTalon1.getEncPosition() + robot.tdt.rDriveTalon1.getEncPosition())/2;
	
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
		kTurret_IZone = 0;
		kTurret_RampRate = 0;
		kTurret_Profile = 0;
		
		//adjust pidcontroller, placeholders
		kAdjust_kP = 0;
		kAdjust_kI = 0;
		kAdjust_kD = 0;
		kAdjust_kF = 0;
		kAdjust_IZone = 0;
		kAdjust_RampRate = 0;
		kAdjust_Profile = 0;
		
		kAdjust_EncConvFactor = 0;
		
		//shooter, placeholders
		kShooter_kP = 1;
		kShooter_kI = 0.01;
		kShooter_kD = 0;
		kShooter_kF = 3;
		kShooter_IZone = 0;
		kShooter_RampRate = 0;
		kShooter_Profile = 0;
		
		kShooter_TargetRPM = 5000;
		kShooter_IndexSensorThreshold = 2;
		
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