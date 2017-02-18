package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class AngleAdjust {
	Robot robot;
	double desiredEncTick;
	
	public AngleAdjust(Robot myRobot) {
		robot = myRobot;
		
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.adjustTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
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