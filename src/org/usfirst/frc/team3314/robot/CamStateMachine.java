package org.usfirst.frc.team3314.robot;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class CamStateMachine {
	
	enum camStates {
		INIT,
		CALIBRATE,
		CALIBRATED,
	}
	
	camStates currentState;
	camStates nextState;
	Robot robot;
	boolean calibrated;
	double desiredPosition = .3555;
	
	public CamStateMachine(Robot myRobot) {
		robot = myRobot;
		currentState = camStates.INIT;	
		robot.hal.adjustTalon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		robot.hal.adjustTalon.configEncoderCodesPerRev(2048);
		robot.hal.adjustTalon.setPID(Constants.kAngleAdjust_kP, Constants.kAngleAdjust_kI, Constants.kAngleAdjust_kD,
		Constants.kAngleAdjust_kF, Constants.kAngleAdjust_IZone, Constants.kAngleAdjust_RampRate, Constants.kAdjust_Profile);
	}
	
	public void reset() {
		//sets shooter back to beginning
		nextState = camStates.INIT;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch(currentState) {
			case INIT:
				robot.hal.adjustTalon.setPosition(Constants.kCamInitPosition);
				robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(true, false);
				robot.hal.adjustTalon.changeControlMode(TalonControlMode.PercentVbus);
				nextState = camStates.CALIBRATE;
			break;
			
			case CALIBRATE:
				if (calibrated) {
					nextState = camStates.CALIBRATED;
				}
				calibrate();
			break;
			case CALIBRATED:
				robot.hal.adjustTalon.set(desiredPosition);
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
		if (robot.hal.adjustTalon.getPosition() >= 1) {
			robot.hal.adjustTalon.set(.2);
		}
		else if (robot.hal.adjustTalon.getPosition() <1) {
			robot.hal.adjustTalon.enableZeroSensorPositionOnIndex(false, false);
			robot.hal.adjustTalon.changeControlMode(TalonControlMode.Position);
			robot.hal.adjustTalon.setPosition(0 + Constants.kCamOffset);
			robot.hal.adjustTalon.set(0);
			robot.hal.adjustTalon.setAllowableClosedLoopErr(20);
			calibrated = true;
		}
	}

}
