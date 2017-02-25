package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

public class AngleAdjust {
	Robot robot;
	double desiredEncTick;
	boolean calibrated = false;
	
	public AngleAdjust(Robot myRobot) {
		robot = myRobot;
		
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.adjustTalon.configEncoderCodesPerRev(2048);
		robot.hal.adjustTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
		robot.hal.adjustTalon.setAllowableClosedLoopErr(100);
	}
	
	public void update() {
		//talon turns motor to target
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.set(desiredEncTick);
		robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(false, false);
		robot.hal.adjustTalon.getPinStateQuadIdx(); //encoder status
	}

	public double setCamPosition(double position) {
		//set using desired encoder tick value
		desiredEncTick = position;
		return desiredEncTick;
	}
	public void calibrate() {
		if (robot.hal.adjustTalon.getPinStateQuadIdx() == 0); {
			robot.hal.adjustTalon.changeControlMode(TalonControlMode.PercentVbus);
			robot.hal.adjustTalon.set(.1);
		}
		if (robot.hal.adjustTalon.getPinStateQuadIdx() == 1 ) {
			robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
			robot.hal.adjustTalon.setPosition(0);
			robot.hal.adjustTalon.set(0);
			calibrated = true;
		}
	}
}