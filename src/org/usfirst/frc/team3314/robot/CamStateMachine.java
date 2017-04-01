package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class CamStateMachine {
	/*s
	enum camStates {
		INIT,
		CALIBRATE,
		CALIBRATED,
	}
	
	camStates currentState;
	camStates nextState;
	*/
	Robot robot;
	boolean calibrated;
	double desiredPosition;
	
	public CamStateMachine(Robot myRobot) {
		robot = myRobot;
		//currentState = camStates.INIT;	
		robot.hal.camTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		robot.hal.camTalon.enableZeroSensorPositionOnIndex(false, false);
		robot.hal.camTalon.setAllowableClosedLoopErr(0);
		robot.hal.camTalon.setInverted(true);
		robot.hal.camTalon.changeControlMode(TalonControlMode.Position);
		robot.hal.camTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
	}
	
	public void reset() {
		//sets shooter back to bginning
		desiredPosition = 0;
	}
	
	
	public void update() {
		robot.hal.camTalon.set(desiredPosition / 4096);
	}
	/*
	public void calcNext() {
		nextState = currentState;
		
		switch(currentState) {
			case INIT:
				robot.hal.camTalon.setPosition(Constants.kCamInitPosition);
				robot.hal.camTalon.enableZeroSensorPositionOnIndex(true, false);
				robot.hal.camTalon.changeControlMode(TalonControlMode.PercentVbus);
				nextState = camStates.CALIBRATE;
			break;
			
			case CALIBRATE:
				if (calibrated) {
					nextState = camStates.CALIBRATED;
				}
				calibrate();
			break;
			case CALIBRATED:
				robot.hal.camTalon.set(desiredPosition);
				if (!calibrated) {
					nextState = camStates.INIT;
				}
			break;
		}
	}
	
	public void doTransition() {
		if(currentState == camStates.INIT && nextState == camStates.CALIBRATE) {
			
		}
		if(currentState == camStates.CALIBRATE && nextState == camStates.CALIBRATED) {
		
		}
		
	}
	
	public void calibrate() {
		if (robot.hal.camTalon.getPosition() >= 1) {
			robot.hal.camTalon.set(.2);
		}
		else if (robot.hal.camTalon.getPosition() <1) {
			robot.hal.camTalon.enableZeroSensorPositionOnIndex(false, false);
			robot.hal.camTalon.changeControlMode(TalonControlMode.Position);
			robot.hal.camTalon.setPosition(0 + Constants.kCamOffset);
			robot.hal.camTalon.set(0 + Constants.kCamOffset);
			robot.hal.camTalon.setAllowableClosedLoopErr(10);
			calibrated = true;
		}
	}*/

}
