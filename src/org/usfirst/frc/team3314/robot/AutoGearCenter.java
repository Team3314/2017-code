package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoGearCenterStates {
	START,
	DRIVE,
	STOP1,
	DROPGEAR,
	WAIT,
	DRIVEBACK,
	DONE
}

public class AutoGearCenter {
	autoGearCenterStates currentState;
	autoGearCenterStates nextState;
	Robot robot;
	double time = 0;
	double desiredDistance = 80;

	public AutoGearCenter(Robot myRobot) {
		robot = myRobot;
		currentState = autoGearCenterStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGearCenterStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			robot.navx.reset();
			nextState = autoGearCenterStates.DRIVE;
			break;
		case DRIVE:
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearCenterStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0){
				nextState = autoGearCenterStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearCenterStates.WAIT;
			}
			break;
		case WAIT:
			if (time <= 0) {
				nextState = autoGearCenterStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK: 
			if (robot.tdt.avgEncPos <= (desiredDistance * Constants.kInToRevConvFactor)) {
				nextState = autoGearCenterStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGearCenterStates.START && nextState == autoGearCenterStates.DRIVE) {
			//robot drives straight forward at max speed, 4 sec
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(0.5);
		}
		
		if (currentState == autoGearCenterStates.DRIVE && nextState == autoGearCenterStates.STOP1) {
			//stops robot, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 13;
		}
		if (currentState == autoGearCenterStates.STOP1 && nextState == autoGearCenterStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearCenterStates.DROPGEAR && nextState == autoGearCenterStates.WAIT) {
			time = 25;
		}
		if (currentState == autoGearCenterStates.WAIT && nextState == autoGearCenterStates.DRIVEBACK) {
			robot.tdt.resetDriveEncoders();
			desiredDistance = -25;
			robot.tdt.setDriveTrainSpeed(-.5);
		}
		if (currentState == autoGearCenterStates.DRIVEBACK && nextState == autoGearCenterStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
	}
}