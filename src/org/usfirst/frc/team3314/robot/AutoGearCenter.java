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
		//Checks whether requirements to go to next state are fulfilled and switches states if so,
		//executes code assigned to each state every 20ms
		calcNext();
		doTransition();
		currentState = nextState;//Moves state machine to next state
		time --;
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			//Resets gyro before driving
			robot.navx.reset();
			nextState = autoGearCenterStates.DRIVE;
			break;
		case DRIVE:
			//Stops robot when it has driven the desired distance
			if (robot.tdt.avgEncPos >= (desiredDistance*Constants.kInToRevConvFactor)){
				nextState = autoGearCenterStates.STOP1;
			}
			break;
		case STOP1:
			//Robot waits to make sure it has stopped completely before dropping gear
			if (time <= 0){
				nextState = autoGearCenterStates.DROPGEAR;
			}
			break;
		case DROPGEAR:
			//Makes sure gear intake is open beore moving back
			if (robot.hal.gearIntake.get().toString() == Constants.kOpenGearIntake) {
				nextState = autoGearCenterStates.WAIT;
			}
			break;
		case WAIT:
			//Makes sure geat has finished falling from robot beofre moving backards.
			if (time <= 0) {
				nextState = autoGearCenterStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK: 
			//Drives backwards until robot drives desired distance
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
			//robot drives straight forward at half speed 
			robot.hal.gearIntake.set(Value.valueOf(Constants.kCloseGearIntake));
			robot.tdt.resetDriveEncoders();
			robot.tdt.setDriveAngle(0);
			robot.tdt.setDriveMode(driveMode.GYROLOCK);
			robot.tdt.setDriveTrainSpeed(0.5);
		}
		
		if (currentState == autoGearCenterStates.DRIVE && nextState == autoGearCenterStates.STOP1) {
			//Stops robot for a 1/4 second before dropping gear to make sure robot has sett;ed
			robot.tdt.setDriveTrainSpeed(0);
			time = 13;
		}
		if (currentState == autoGearCenterStates.STOP1 && nextState == autoGearCenterStates.DROPGEAR) {
			robot.hal.gearIntake.set(Value.valueOf(Constants.kOpenGearIntake));
		}
		if (currentState == autoGearCenterStates.DROPGEAR && nextState == autoGearCenterStates.WAIT) {
			//Waits 1/2 second before driving back after dropping gear to make sure it is completely out of robot
			time = 25;
		}
		if (currentState == autoGearCenterStates.WAIT && nextState == autoGearCenterStates.DRIVEBACK) {
			//Resets encoders and then drives back at half speed for 25 inches
			robot.tdt.resetDriveEncoders();
			desiredDistance = -25;
			robot.tdt.setDriveTrainSpeed(-.5);
		}
		if (currentState == autoGearCenterStates.DRIVEBACK && nextState == autoGearCenterStates.DONE) {
			robot.tdt.setDriveTrainSpeed(0);
		}
	}
}
