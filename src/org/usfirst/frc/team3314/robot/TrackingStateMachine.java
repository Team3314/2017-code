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
		
		currentState = trackingStates.START;
	}
	// Calculates whether to transition to the next state
	public void update() {
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Turret tracking state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	/*This method performs different checks in each state to determine when to transition between states*/
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		// Waits for button press
		case START:
			if (robot.turretTrackRequest) {
				nextState = trackingStates.CALCULATE;
			}
			break;
			
		case CALCULATE:
			//Stops state machine if button is released
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
			//Does not turn if turret is within a quarter of a degree to target
			if ( targetYaw >= -.25 && targetYaw <= .25) {
				nextState = trackingStates.START;
			}
			//Waits slightly before turning to make sure turret is stopped
			if (time <= 0) {
				nextState = trackingStates.TURN;
			}
			break;
			
		case TURN:
			if (!robot.turretTrackRequest) {
				nextState = trackingStates.STOP;
			}
			//Stops turret once it is within an acceptable range of the target, preventing oscillation
			if (Math.abs(robot.hal.turretTalon.getClosedLoopError()) < 125) {
				nextState = trackingStates.STOP;
			}
			break;
			
		case STOP:
				nextState = trackingStates.RESET;
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
	/*Executes commands when going from one state to another */
	public void doTransition() {
		//Calculates degrees to turn from camera image
		if (currentState == trackingStates.START && nextState == trackingStates.CALCULATE) {
			targetYaw = robot.turretCam.calcTurretYaw();
		}
		//Puts a timer of 20ms in the wait state
		if (currentState == trackingStates.CALCULATE && nextState == trackingStates.WAIT) {
			time = 1;
		}
		//Tells the turret to turn the calculated degree error 
		if (currentState == trackingStates.WAIT && nextState == trackingStates.TURN) {
			robot.turret.getEncError(targetYaw);
		}
		//Puts a 100 ms timer before resetting the state machine and allowing it to recalculate and turn again
		if (currentState == trackingStates.STOP && nextState == trackingStates.RESET) {
			robot.turretTrackRequest = false;
			time = 5;
		}
	}
}
