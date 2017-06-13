package org.usfirst.frc.team3314.robot;


/* 
This class creates variables with constant values, like PID constants. This allows these numbers to be referenced by name from this 
central file, making it easier to understand what the values actually mean
*/
public class Constants {
	Robot robot = new Robot();
	
	//tankdrivetrain
	public static double kInToRevConvFactor = 0.1197; //Converts inches to revolutions of drive train --- ** 17.15 in per rev **
	public static double kRevToInConvFactor = 8.351288; //Converts revolutions of drive train to inches
	
	//Solenoid States - Used to extend and retracts pistons
	public static String kCloseGearIntake = "kReverse";
	public static String kOpenGearIntake = "kForward";
	
	public static String kShiftHighGear = "kReverse";
	public static String kShiftLowGear = "kForward";
	
	public static boolean kFlashlightOn = true;
	public static boolean kFlashlightOff = false;
	
	public static boolean kRingLightOn = true;
	public static boolean kRingLightOff = false;
	
	//PID values for gyro
	public static double kGyroLock_kP = .04;
	public static double kGyroLock_kI = .002;
	public static double kGyroLock_kD = .1;
	public static double kGyroLock_kF = 0;
	
	//PID values for turret
	public static double kTurret_kP = 1;
	public static double kTurret_kI = .000;
	public static double kTurret_kD = 80;
	public static double kTurret_kF = 0;
	public static int kTurret_IZone = 0;
	public static double kTurret_RampRate = 0; 
	public static int kTurret_Profile = 0;
	
	//adjust pid
	public static double kAngleAdjust_kP = 3.2;
	public static double kAngleAdjust_kI = 0;
	public static double kAngleAdjust_kD = 0;
	public static double kAngleAdjust_kF = 0;
	public static int kAngleAdjust_IZone = 0;
	public static double kAngleAdjust_RampRate = 0;
	public static int kAdjust_Profile = 0;
	public static double kCamOffset = -.86999; // PRACTICE BOT ###   ####  No Longer Used
	
	//PID values for shooter
	public static double kShooter_kP = .04;
	public static double kShooter_kI = 0;
	public static double kShooter_kD = 0;
	public static double kShooter_kF = .013;
	public static int kShooter_IZone = 0;
	public static double kShooter_RampRate = 0;
	public static int kShooter_Profile = 0;
	
	
	public static double kCenterOfView = 89.5; //Center point of the camera's view (in pixels)
	public static double kXRes = 320; // Vertical resolution of camera
	public static double kYRes = 180; // Horizontal resolution of camera
	public static double kVerticalViewAngle = 70.42; 
	public static double kHorizontalViewAngle = 43.3;
	public static double kFocalLength = 226.7;
	public static double kDegToEncTicksConvFactor = .038888888888888; // Converts degrees of camera vision to turret encoder ticks to transate the camera image to turret rotation
	
	//Emperically testerd values for close and far shot
	public static double kCamClosePosition = 1184;
	public static double kShooterCloseSpeed = 3500;
	
	public static double kCamFarPosition = 1456;
	public static double kShooterFarSpeed = 4500;

}
