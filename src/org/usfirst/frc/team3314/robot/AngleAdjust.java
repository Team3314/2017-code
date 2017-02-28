package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.*;

enum adjustStates {
	INIT,
	CALIBRATE,
	CALIBRATED
}

public class AngleAdjust {
	Robot robot;
	adjustStates currentState;
	double desiredEncTick;
	boolean calibrated = false;
	
	public AngleAdjust(Robot myRobot) {
		robot = myRobot;
		
		robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.adjustTalon.configEncoderCodesPerRev(2048);
		robot.hal.adjustTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
		robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(true, false);
		robot.hal.adjustTalon.setPosition(1);
		//robot.hal.adjustTalon.setAllowableClosedLoopErr(20);
	}
	
	
	
	public void update() {
		//talon turns motor to target
		
		switch(currentState) {
		case INIT:
			robot.hal.adjustTalon.setPosition(1);
			robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(true, false);
			if (calibrated) {
				currentState = adjustStates.CALIBRATED;
			}
			else if (!calibrated) {
				currentState = adjustStates.CALIBRATE;
			}
			break;
		case CALIBRATE:
			calibrate();
			break;
		case CALIBRATED:
			
			robot.hal.adjustTalon.set(desiredEncTick);
			break;
		}
	}

	public double setCamPosition(double position) {
		//converts desired angle to encoder ticks
		desiredEncTick = position;
		return desiredEncTick;
	}
	public void calibrate() {
		
		if (robot.hal.adjustTalon.getPosition() > .5); {
			robot.hal.adjustTalon.changeControlMode(TalonControlMode.PercentVbus);
			robot.hal.adjustTalon.set(.1);
		}
		if (robot.hal.adjustTalon.getPosition() <= .5 ) {
			robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
			robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(false, false);
			robot.hal.adjustTalon.setPosition(0);
			robot.hal.adjustTalon.set(0);
			calibrated = true;
		}
		
		
	}
	
}