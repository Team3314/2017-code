package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class AngleAdjust {
	Robot robot;
	double desiredEncTick;
	
	public AngleAdjust(Robot myRobot) {
		robot = myRobot;
		
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.adjustTalon.setPID(Constants.kAdjust_kP, Constants.kAdjust_kI, Constants.kAdjust_kD,
		Constants.kAdjust_kF, Constants.kAdjust_IZone, Constants.kAdjust_RampRate, Constants.kAdjust_Profile);
	}
	
	public void update() {
		//talon turns motor to target
		robot.hal.adjustTalon.set(desiredEncTick);
	}

	public double setHoodAngle(double angle) {
		desiredEncTick = angle * Constants.kAdjust_EncConvFactor;
		return desiredEncTick;
	}
}