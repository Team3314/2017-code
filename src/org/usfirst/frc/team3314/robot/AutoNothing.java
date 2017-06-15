package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum autoNothingStates {
	START,
	DONE
}
//This auto does nothing - it goes from start to done
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
		currentState = autoNothingStates.START;
	}
	
	public void update() {
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
