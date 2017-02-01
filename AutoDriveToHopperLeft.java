package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoDTHLeftStates {
	START,
	DRIVE1,
	STOP1,
	DRIVE2,
	STOP2,
	SHOOT,
	DONE
}

public class AutoDriveToHopperLeft {
	autoDTHLeftStates currentState;
	autoDTHLeftStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
	public AutoDriveToHopperLeft(Robot myRobot) {
		robot = myRobot;
		currentState = autoDTHLeftStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoDTHLeftStates.START;
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
			nextState = autoDTHLeftStates.DRIVE1;
			break;
		case DRIVE1:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (108*robot.encoderConversionFactor)){
				nextState = autoDTHLeftStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoDTHLeftStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (/*robot.tdt.lDriveTalon1.getEncPosition() > &&*/ robot.tdt.rDriveTalon1.getEncPosition() > (36*robot.encoderConversionFactor)){
				nextState = autoDTHLeftStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoDTHLeftStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <=0){
				nextState = autoDTHLeftStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoDTHLeftStates.START && nextState == autoDTHLeftStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.hal.gyro.angle());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoDTHLeftStates.DRIVE1 && nextState == autoDTHLeftStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoDTHLeftStates.STOP1 && nextState == autoDTHLeftStates.DRIVE2) {
			//robot drives forward again at max speed but at angle of hopper, 1.5 sec
			robot.tdt.setDriveAngle(-90);
			robot.tdt.setDriveTrainSpeed(1);
			time = 75;
		}
		
		if (currentState == autoDTHLeftStates.DRIVE2 && nextState == autoDTHLeftStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoDTHLeftStates.STOP2 && nextState == autoDTHLeftStates.SHOOT){
			//placeholder, will shoot balls into the hopper
			time = 50;
		}
	}
}