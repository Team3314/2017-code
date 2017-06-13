package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

//This class sets up the cam when initializing the robot, setting up the encoder and it's PID constants

public class CamStateMachine {
	Robot robot;
	double desiredPosition;
	
	public CamStateMachine(Robot myRobot) {
		robot = myRobot;
		robot.hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute); // Sets can to absolute mode to start
		robot.hal.camTalon.setAllowableClosedLoopErr(0); //Sets the cam's error tolerance to zero, so the motor will keep trying to turn until it reaches the exact target position assinged
		robot.hal.camTalon.setInverted(true); //Reverses output of encoder 
		robot.hal.camTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.camTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
	}
	
	public void reset() {
		//Rotates cam to zero postion (plastic vertical)
		desiredPosition = 0;
	}
	
	
	public void update() {
		robot.hal.camTalon.set(desiredPosition / 4096);
	}

}
