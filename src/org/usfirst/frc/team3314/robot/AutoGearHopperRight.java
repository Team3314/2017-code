package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoGHRightStates {
	START,
	DRIVE1,
	STOP1,
	TURN1,
	DRIVE2,
	STOP2,
	DRIVEBACK,
	STOP3,
	TURN2,
	DRIVE4,
	STOP4,
	DONE
}

public class AutoGearHopperRight {
	autoGHRightStates currentState;
	autoGHRightStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoGearHopperRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoGHRightStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoGHRightStates.START;
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
			robot.ahrs.reset();
			nextState = autoGHRightStates.DRIVE1;
			break;
		case DRIVE1:
			if (robot.tdt.avgEncPos > (75.25*Constants.kEncConvFactor)){
				nextState = autoGHRightStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoGHRightStates.TURN1;
			}
			break;
		case TURN1:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 60) <= .25) {
				nextState = autoGHRightStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.avgEncPos > (20*Constants.kEncConvFactor)){
				nextState = autoGHRightStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoGHRightStates.DRIVEBACK;
			}
			break;
		case DRIVEBACK:
			if (robot.tdt.avgEncPos > (-20*Constants.kEncConvFactor)){
				//placeholders
				nextState = autoGHRightStates.STOP3;
			}
			break;
		case STOP3:
			if (time <=0){
				nextState = autoGHRightStates.TURN2;
			}
			break;
		case TURN2:
			if (Math.abs(Math.abs(robot.ahrs.getYaw()) - 30) <= .25) {
				nextState = autoGHRightStates.DRIVE4;
			}
			break;
		case DRIVE4:
			if ((robot.tdt.detectCollision() > Constants.kCollisionThreshold_DeltaG)){
				//placeholders
				nextState = autoGHRightStates.STOP4;
			}
			break;
		case STOP4:
			if (time <=0){
				nextState = autoGHRightStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoGHRightStates.START && nextState == autoGHRightStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoGHRightStates.DRIVE1 && nextState == autoGHRightStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoGHRightStates.STOP1 && nextState == autoGHRightStates.TURN1) {
			//robot turns left to angle of hopper, 1.5 sec
			robot.ahrs.reset();
			robot.tdt.setDriveAngle(-60);
			time = 75;
		}
		
		if(currentState == autoGHRightStates.TURN1 && nextState == autoGHRightStates.DRIVE2) {
			//robot drives at max speed
			robot.tdt.setDriveTrainSpeed(1);
		}
		
		if (currentState == autoGHRightStates.DRIVE2 && nextState == autoGHRightStates.STOP2) {
			//stops robot again + gear intake retracts , 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			robot.hal.gearIntake.set(Value.valueOf(Constants.kDropGearIntake));
			time = 50;
		}
		
		if (currentState == autoGHRightStates.STOP2 && nextState == autoGHRightStates.DRIVEBACK){
			//robot drives straight back at max speed, 1 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(-1);
			time = 50;
		}
		
		if (currentState == autoGHRightStates.DRIVEBACK && nextState == autoGHRightStates.STOP3) {
			//stops robot again, 1 sec
			robot.ahrs.reset();
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoGHRightStates.STOP3 && nextState == autoGHRightStates.TURN2){
			//robot turns left at angle, 1 sec
			robot.tdt.setDriveAngle(-30);
			time = 50;
		}
		
		if (currentState == autoGHRightStates.TURN2 && nextState == autoGHRightStates.DRIVE4) {
			//robot drives at max speed
			robot.tdt.setDriveTrainSpeed(1);
		}
		
		if (currentState == autoGHRightStates.DRIVE4 && nextState == autoGHRightStates.STOP4) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
	}
}