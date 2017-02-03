package org.usfirst.frc.team3314.robot;

public class Constants {
	Robot robot;
	
	//misc tank drive train
	public static double kAvgEncPos; //average of left + right drive talon encoder positions
	public static double kEncConvFactor; //conversion from inches to encoder ticks
	public static double kHighGear; //rpm for high gear
	public static double kLowGear; //rpm for low gear
	
	//Solenoid states
	public static String kShiftHighGear;
	public static String kShiftLowGear;
	
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
		kHighGear = 200;
		kLowGear = 75;
		
		//Solenoid States
		kShiftHighGear = "kForward";
		kShiftLowGear = "kReverse";
		
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
