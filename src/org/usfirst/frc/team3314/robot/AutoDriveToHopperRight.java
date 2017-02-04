package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoDTHRightStates {
	START,
	DRIVE1,
	STOP1,
	DRIVE2,
	STOP2,
	SHOOT,
	DONE
}

public class AutoDriveToHopperRight {
	autoDTHRightStates currentState;
	autoDTHRightStates nextState;
	Robot robot;
	double time = 0; //times are placeholder for now
	
	public AutoDriveToHopperRight(Robot myRobot) {
		robot = myRobot;
		currentState = autoDTHRightStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoDTHRightStates.START;
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
			nextState = autoDTHRightStates.DRIVE1;
			break;
		case DRIVE1:
			if (Constants.kAvgEncPos > (108*Constants.kEncConvFactor)){
				nextState = autoDTHRightStates.STOP1;
			}
			break;
		case STOP1:
			if (time <= 0 ){
				nextState = autoDTHRightStates.DRIVE2;
			}
			break;
		case DRIVE2:
			if (robot.tdt.detectCollision() > Constants.kCollisionThreshold_DeltaG){
				nextState = autoDTHRightStates.STOP2;
			}
			break;
		case STOP2:
			if (time <=0){
				nextState = autoDTHRightStates.SHOOT;
			}
			break;
		case SHOOT:
			if (time <=0){
				nextState = autoDTHRightStates.DONE;
			}
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == autoDTHRightStates.START && nextState == autoDTHRightStates.DRIVE1) {
			//robot drives straight forward at max speed, 3 sec
			robot.tdt.setDriveAngle(robot.ahrs.getYaw());
			robot.tdt.setDriveTrainSpeed(1);
			time = 150;
		}
		
		if (currentState == autoDTHRightStates.DRIVE1 && nextState == autoDTHRightStates.STOP1) {
			//stops robot, 1/2 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 25;
		}
		
		if (currentState == autoDTHRightStates.STOP1 && nextState == autoDTHRightStates.DRIVE2) {
			//robot drives forward again at max speed but at angle of hopper, 1.5 sec
			robot.tdt.setDriveAngle(90);
			robot.tdt.setDriveTrainSpeed(1);
			time = 75;
		}
		
		if (currentState == autoDTHRightStates.DRIVE2 && nextState == autoDTHRightStates.STOP2) {
			//stops robot again, 1 sec
			robot.tdt.setDriveTrainSpeed(0);
			time = 50;
		}
		
		if (currentState == autoDTHRightStates.STOP2 && nextState == autoDTHRightStates.SHOOT){
			//placeholder, will shoot balls into the hopper
			time = 50;
		}
	}
}