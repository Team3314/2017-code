package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoGHLeftStates {
	START,
	DRIVE1,
	STOP1,
	DRIVE2,
	STOP2,
	DRIVE3,
	STOP3,
	DRIVE4,
	STOP4,
	DONE
}

public class AutoGearHopperLeft {
	autoGHLeftStates currentState;
	autoGHLeftStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
	public AutoGearHopperLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoGHLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGHLeftStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			robot.hal.gyro.reset();
			nextState = autoGHLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (75.25*robot.encoderConversionFactor)){
				nextState = autoGHLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGHLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (20*robot.encoderConversionFactor)){
				nextState = autoGHLeftStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGHLeftStates.DRIVE3;
			}
			break;
		case DRIVE3:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (20*robot.encoderConversionFactor)){
				//enc pos is placeholder, placeholder distance
				nextState = autoGHLeftStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGHLeftStates.DRIVE4;
			}
			break;
		case DRIVE4:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (20*robot.encoderConversionFactor)){
				//enc pos is placeholder, placeholer distance
				nextState = autoGHLeftStates.STOP4;
			}
			break;
		case STOP4:
			if (time <=0){
				nextState = autoGHLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGHLeftStates.START && nextState == autoGHLeftStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoGHLeftStates.DRIVE1 && nextState == autoGHLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGHLeftStates.STOP1 && nextState == autoGHLeftStates.DRIVE2) {
			//robot drives forward again at max speed but at angle of hopper, 1.5 sec
			robot.tdt.setDriveAngle(60);
			robot.tdt.setDriveTrainSpeed(1);
			time = 75;
		}
		
		if (currentState == autoGHLeftStates.DRIVE2 && nextState == autoGHLeftStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.STOP2 && nextState == autoGHLeftStates.DRIVE3){
			//placeholder angle, robot drives forward at fullspeed, 1 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.DRIVE3 && nextState == autoGHLeftStates.STOP3) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.STOP3 && nextState == autoGHLeftStates.DRIVE4){
			//place holder angle, robot drives forward at fullspeed, 1 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 50;
		}
		
		if (currentState == autoGHLeftStates.DRIVE4 && nextState == autoGHLeftStates.STOP4) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
	}
}