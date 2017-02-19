package org.usfirst.frc.team3314.robot;

public class Constants {
	Robot robot = new Robot();
	
	//tankdrivetrain
	public static double kEncConvFactor = .04; //conversion from inches to encoder ticks
	public static double kHighGearRPM = 500; //rpm for high gear
	public static double kLowGearRPM = 200;//rpm for low gear
	public static double kCollisionThreshold_DeltaG = .5; //acceleration limit for collision detection
	
	//Solenoid States
	public static String kExtendGearIntake = "kForward";
	public static String kRetractGearIntake = "kReverse";
	
	public static String kShiftHighGear = "kReverse";
	public static String kShiftLowGear = "kForward";
	
	public static boolean kFlashlightOn = false;
	public static boolean kFlashlightOff = true;
	
	//gyrolock pidcontroller
	public static double kGyroLock_kP = .0175;
	public static double kGyroLock_kI = 0;
	public static double kGyroLock_kD = 0;
	public static double kGyroLock_kF = 0;
	
	//turret pid
	public static double kTurret_kP = 1;
	public static double kTurret_kI = .000;
	public static double kTurret_kD = 80;
	public static double kTurret_kF = 0;
	public static int kTurret_IZone = 0;
	public static double kTurret_RampRate = 0;
	public static int kTurret_Profile = 0;
	
	//adjust pid
	public static double kAngleAdjust_kP = .8;
	public static double kAngleAdjust_kI = .008;
	public static double kAngleAdjust_kD = 0;
	public static double kAngleAdjust_kF = .0;
	public static int kAngleAdjust_IZone = 0;
	public static double kAngleAdjust_RampRate = 0;
	public static int kAdjust_Profile = 0;
	
	public static double kAdjust_EncConvFactor = 0; //conversion from degrees to enc ticks
	
	//shooter (pid + other values)
	public static double kShooter_kP = .08;
	public static double kShooter_kI = 0;
	public static double kShooter_kD = 0;
	public static double kShooter_kF = .013;
	public static int kShooter_IZone = 0;
	public static double kShooter_RampRate = 0;
	public static int kShooter_Profile = 0;
	
	public static double kShooter_TargetRPM = 5000;
	public static double kShooter_TargetHopperRPM = 5000;
	public static double kShooter_IndexSensorThreshold = 2;
	
	//speedcontrol pid
	public static double kSpeedControl_kP = .01;
	public static double kSpeedControl_kI = 0.0;
	public static double kSpeedControl_kD = 0;
	public static double kSpeedControl_kF = 0;
	public static int kSpeedControl_IZone = 0;
	public static double kSpeedControl_RampRate = 0;
	public static int kSpeedControl_Profile = 0;
	
	//camera
	public static double kCenterOfView = 320;
	public static double kTargetHeight = (10/12);
	public static double kYRes = 640;
	public static double kViewAngle = 43.5;
	public static double kPxlToEncTicksConvFactor = 1;
	public static double kPxlToDegConvFactor = 1;
}