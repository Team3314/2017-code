package org.usfirst.frc.team3314.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

enum shooterStates {
	START,
	AGITATE,
	INDEX,
	SHOOT,
	STOP,
	DONE
}

public class ShooterStateMachine {
	shooterStates currentState;
	shooterStates nextState;
	Robot robot;
	double shooterSpeed = 0;
	double desiredSpeed = 3500;
	double time = 0;
	double sensorTime = 50;
	double rpm;
	
	public ShooterStateMachine(Robot myRobot) {
		robot = myRobot;
		currentState = shooterStates.START;
	}
	
	public void reset() {
		//Puts state machine back to beginning
		currentState = shooterStates.START;
	}
	
	public void update() {
		//Checks whether requirements to go to next state are fulfilled and switches states if so,
		//executes code assigned to each state every 20ms
		shooterSpeed = robot.hal.shooterTalon.getSpeed();
		calcNext();
		doTransition();
		currentState = nextState; //Moves state machine to next state
		time --;
		SmartDashboard.putString("Shooter state", currentState.toString());
		SmartDashboard.putNumber("Time", time);
	}
	
	public void calcNext() {
		nextState = currentState;
		
		switch (currentState) {
		case START: // Waits for button press 
			if (robot.shootRequest) {
				nextState = shooterStates.AGITATE;
			}
			
			break;
			
		case AGITATE:
			//Stops executing state machine if button is released
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			//Waits for shooter to get up to speed before feeding balls in
			if (Math.abs(desiredSpeed + robot.hal.shooterTalon.getSpeed()) <= 250){
				nextState = shooterStates.INDEX;
			}
			break;
			
		case INDEX:
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			//Makes sure indexing motors are on
			if (robot.hal.lowerIndexSpark.get() == 1 && robot.hal.upperIndexSpark.get() == 1) {
				nextState = shooterStates.SHOOT;
			}
			break;
			
		case SHOOT:
			//Stops shooting when shoot button is released
			if (!robot.shootRequest) {
				nextState = shooterStates.STOP;
			}
			break;
			
		case STOP:
			//Checks if motors for shooting are stopped before going back to start and accepting a button press
			if (robot.hal.agitatorSpark.get() == 0 && robot.hal.lowerIndexSpark.get() == 0 && 
			    robot.hal.shooterTalon.get() == 0) {
				nextState = shooterStates.DONE;
			}
			break;
			
		case DONE:
			nextState = shooterStates.START;
			break;
		}
	}
	
	public void doTransition() {
		if (currentState == shooterStates.START && nextState == shooterStates.AGITATE) {
			//Sets shooter to desired speed
			robot.hal.shooterTalon.set(desiredSpeed);
		}
		
		if (currentState == shooterStates.AGITATE && nextState == shooterStates.INDEX) {
			//Starts feeding balls into shooter 
			robot.hal.agitatorSpark.set(.9);
			robot.hal.lowerIndexSpark.set(1);
			robot.hal.upperIndexSpark.set(1);
		}
		
		if (currentState == shooterStates.INDEX && nextState == shooterStates.SHOOT) {
			//prior code allows robot to shoot w/o code needed here
		}
		
		if (currentState == shooterStates.AGITATE && nextState == shooterStates.STOP) {
			//Turns all motors off
			robot.hal.agitatorSpark.set(0);
			robot.hal.shooterTalon.set(0);
			robot.hal.lowerIndexSpark.set(0);
			robot.hal.upperIndexSpark.set(0);
		}
		if (currentState == shooterStates.INDEX && nextState == shooterStates.STOP) {
				//all motors off
				robot.hal.agitatorSpark.set(0);
				robot.hal.shooterTalon.set(0);
				robot.hal.lowerIndexSpark.set(0);
				robot.hal.upperIndexSpark.set(0);
			}
		if (currentState == shooterStates.SHOOT && nextState == shooterStates.STOP) {
				//all motors off
				robot.hal.agitatorSpark.set(0);
				robot.hal.shooterTalon.set(0);
				robot.hal.lowerIndexSpark.set(0);
				robot.hal.upperIndexSpark.set(0);
			}
	}
}
