package org.usfirst.frc.team3314.robot;

public class Constants {
	Robot robot = new Robot();
	
	//tankdrivetrain
	public static double kInToRevConvFactor = 0.1197; //conversion from inches to drivetrain revolutions --- ** 17.15 in per rev **
	public static double kRevToInConvFactor = 8.351288;
	public static double kHighGearRPM = 500; //rpm for high gear
	public static double kLowGearRPM = 200;//rpm for low gear
	public static double kCollisionThreshold_DeltaG = .5; //acceleration limit for collision detection
	
	//Solenoid States
	public static String kCloseGearIntake = "kReverse";
	public static String kOpenGearIntake = "kForward";
	
	public static String kShiftHighGear = "kReverse";
	public static String kShiftLowGear = "kForward";
	
	public static boolean kFlashlightOn = true;
	public static boolean kFlashlightOff = false;
	
	public static boolean kRingLightOn = true;
	public static boolean kRingLightOff = false;
	
	//gyrolock pidcontroller
	public static double kGyroLock_kP = .04;
	public static double kGyroLock_kI = .002;
	public static double kGyroLock_kD = .1;
	public static double kGyroLock_kF = 0;
	
	//turret pid
	public static double kTurret_kP = .8;
	public static double kTurret_kI = .000;
	public static double kTurret_kD = 80;
	public static double kTurret_kF = 0;
	public static int kTurret_IZone = 0;
	public static double kTurret_RampRate = 0;
	public static int kTurret_Profile = 0;
	
	//adjust pid
	public static double kAngleAdjust_kP = 3.2; //.8;
	public static double kAngleAdjust_kI = .00; //.002;
	public static double kAngleAdjust_kD = 0;
	public static double kAngleAdjust_kF = .0;
	public static int kAngleAdjust_IZone = 0;
	public static double kAngleAdjust_RampRate = 0;
	public static int kAdjust_Profile = 0;
	public static double kCamOffset = -.86999; // PRACTICE BOT ###   ####  No Longer Used
	
	
	
	
	public static double kCamInitPosition = 2;
	
	
	
	
	public static double kAdjust_EncConvFactor = 0; //conversion from degrees to enc ticks
	
	//shooter (pid + other values)
	public static double kShooter_kP = .04;
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
	public static double kCenterOfView = 89.5;
	//public static double kTargetHeight = (10/12);
	public static double kXRes = 320;
	public static double kYRes = 180;
	public static double kVerticalViewAngle = 70.42;
	public static double kHorizontalViewAngle = 43.3;
	public static double kFocalLength = 226.7;
	//public static double kPxlToDegConvFactor = 0.271875;
	public static double kDegToEncTicksConvFactor = 159.2888888888;
	
	public static double kDefaultCamPosition = .3555;
	
	public static double kCamClosePosition = .2892;
	public static double kShooterCloseSpeed = 3500;
	public static double kTurretClosePosition = 0;
	
	public static double kCamFarPosition = .3555;
	public static double kShooterFarSpeed = 4500;
	public static double kTurretFarPosition = 0;
	
	public static double kPulleyDiameter = 0.06985;
	public static double kMaxVelocity = 0;
}