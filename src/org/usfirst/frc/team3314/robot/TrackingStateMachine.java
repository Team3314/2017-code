package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum trackingStates {
	START,
	CALCULATE,
	WAIT,
	TURN,
	STOP,
	RESET,
	DONE
}

public class TrackingStateMachine {
	trackingStates currentState;
	trackingStates nextState;
	Robot robot;
	double targetYaw = 0;
	double time = 0;
	
	public TrackingStateMachine(Robot myRobot) {
		robot = myRobot;
		currentState = trackingStates.START;
	}
	
	public void reset() {
		//sets horizontal angle tracking back to beginning
		currentState = trackingStates.START;
	}
	
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Turret tracking state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			if (robot.turretTrackRequest) {
				nextState = trackingStates.CALCULATE;
			}
			break;
			
		case CALCULATE:

			if (!robot.turretTrackRequest) {
				nextState = trackingStates.STOP;
			}
			else {
				nextState = trackingStates.WAIT;
			}
			break;
			
		case WAIT:
			if (!robot.turretTrackRequest) {
				nextState = trackingStates.STOP;
			}
			if ( targetYaw >= -.25 && targetYaw <= .25) {
				nextState = trackingStates.START;
			}
			if (time <= 0) {
				nextState = trackingStates.TURN;
			}
			break;
			
		case TURN:
			if (!robot.turretTrackRequest) {
				nextState = trackingStates.STOP;
			}
			if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) < 125) {
				nextState = trackingStates.STOP;
			}
			break;
			
		case STOP:
			//if (targetYaw == 0) {
				nextState = trackingStates.RESET;
			//}
			break;
			
		case RESET:
			if (time <= 0) {
				nextState = trackingStates.DONE;
			}
			break;
			
		case DONE:
			nextState = trackingStates.START;
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == trackingStates.START && nextState == trackingStates.CALCULATE) {
			targetYaw = robot.turretCam.calcTurretYaw();
		}

		if (currentState == trackingStates.CALCULATE && nextState == trackingStates.WAIT) {
			time = 1;
		}
		
		if (currentState == trackingStates.WAIT && nextState == trackingStates.TURN) {
			robot.turret.getEncError(targetYaw);
			//robot.turret.update();
			robot.turretCam.calcTurretYaw();
		}
		
		if (currentState == trackingStates.WAIT && nextState == trackingStates.CALCULATE) {
			targetYaw = robot.turretCam.calcTurretYaw();
		}
		
		if (currentState == trackingStates.CALCULATE && nextState == trackingStates.STOP) {
			//targetYaw = 0;
		}
		
		if (currentState == trackingStates.WAIT && nextState == trackingStates.STOP) {
			//targetYaw = 0;
		}
		
		if (currentState == trackingStates.TURN && nextState == trackingStates.STOP) {
			//targetYaw = 0;
		}
		
		if (currentState == trackingStates.STOP && nextState == trackingStates.RESET) {
			robot.turretTrackRequest = false;
			//robot.turret.desiredTarget = robot.turret.turretPosition;
			time = 5;
		}
	}
}
