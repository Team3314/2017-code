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
		robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(true, false);
		robot.hal.adjustTalon.getPinStateQuadIdx();
	}

	public double setCamPosition(double position) {
		//converts desired angle to encoder ticks
		desiredEncTick = position;
		return desiredEncTick;
	}
}