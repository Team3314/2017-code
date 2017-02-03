package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoNothingStates {
	START,
	DONE
}

public class AutoNothing {
	autoNothingStates currentState;
	autoNothingStates nextState;
	Robot robot;
	double time = 0;
	
	public AutoNothing(Robot myRobot) {
		robot = myRobot;
		currentState = autoNothingStates.START;
	}
	
	public void reset() {
		//sets auto back to beginning
		currentState = autoNothingStates.START;
	}
	
	public void update() {
		//sees whether requirements to go to next state are fulfilled and switches states if necessary,
		//executes code assigned to each state, counts down time every 20ms
		calcNext();
		doTransition();
		currentState = nextState;
		time --;
		SmartDashboard.putString("Auto state", currentState.toString());
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START:
			nextState = autoNothingStates.DONE;
			break;
		case DONE:
			break;
		}
	}
	
	public void doTransition() {
		//filler
		}
	}